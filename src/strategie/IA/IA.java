package strategie.IA;

import container.Service;
import enums.ScriptNames;
import enums.Speed;
import exceptions.*;
import exceptions.Locomotion.ImmobileEnnemyForOneSecondAtLeast;
import exceptions.Locomotion.PointInObstacleException;
import exceptions.Locomotion.UnableToMoveException;
import hook.HookFactory;
import pathfinder.Pathfinding;
import pfg.config.Config;
import scripts.*;
import smartMath.Vec2;
import strategie.GameState;
import utils.Log;
import utils.Sleep;

import java.util.ArrayList;

public class IA implements Service {

    private Log log;
    private Config config;
    private GameState gameState;
    private ScriptManager scriptManager;
    private Graph graph;
    private Pathfinding pathfinding;
    private HookFactory hookFactory;
    private NodeArray nodes;
    private ArrayList<Node> exploredNodes;
    private ArrayList<Node> availableNodes;
    private Node lastNodeTried;

    /**
     * Permet de s'adapter au déroulement d'un match grace à un graphe de décision.
     */
    public IA(Log log, Config config, GameState gameState, ScriptManager scriptManager, Pathfinding pathfinding, HookFactory hookFactory) throws BadVersionException, UnableToMoveException, PointInObstacleException, NoPathFound {
        this.log = log;
        this.config = config;
        this.gameState = gameState;
        this.scriptManager = scriptManager;
        this.pathfinding = pathfinding;
        this.hookFactory = hookFactory;
        this.graph = new Graph(createNodes(),log);
        this.nodes = createNodes();
        this.availableNodes = new ArrayList<>();
        this.exploredNodes = new ArrayList<>();
        this.lastNodeTried=null;
    }

    public void start(ScriptNames scriptNames, int versionToExecute)  {
        try {
            scriptManager.getScript(scriptNames).goToThenExec(versionToExecute,gameState);
        } catch (Exception e) {
            e.printStackTrace();
            log.debug("////////// IA ////////// An exception happened");
            log.debug("////////////// LANCEMENT IA ///////////////");
            handleException(e);
        }
    }

    private void handleException(Exception e){
        ScriptNames lastScriptExecuted = gameState.getLastScript();
        int lastVersionExecuted = gameState.getLastScriptVersion();
        //On récupère la dernier node qu'on a essayé de réaliser
        this.lastNodeTried=nodes.getNodeByNameAndVersion(lastScriptExecuted,lastVersionExecuted);
        if (this.lastNodeTried==nodes.getNodeByNameAndVersion(ScriptNames.ACTIVATION_PANNEAU_DOMOTIQUE,0)){
            gameState.robot.setLocomotionSpeed(Speed.DEFAULT_SPEED);
        }

        if (e instanceof ImmobileEnnemyForOneSecondAtLeast){
            log.warning("IA HANDLED EXCEPTION : ImmobileEnnemyForOneSecondAtLeast");
            Vec2 aim = ((ImmobileEnnemyForOneSecondAtLeast) e).getAim();
            tryToDoAnotherNode(this.lastNodeTried);
        }
        else if (e instanceof NoPathFound){
            log.warning("IA HANDLED EXCEPTION : NoPathFound");
            Vec2 aim = ((NoPathFound) e).getAim();
            tryToDoAnotherNode(this.lastNodeTried);
        }
        else if (e instanceof NoNodesAvailableException){
            log.warning("IA HANDLED EXCEPTION : NoNodesAvailableException");
            log.warning("On se tourne pour essayer de changer notre perspective, et on attend 2s le temps que la situation se stabilise");
            turnRelativelyHandleException(Math.PI/2);
            exploredNodes.clear();
            updateAvailableNodes();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
        else if (e instanceof UnableToMoveException){
            Vec2 aim = ((UnableToMoveException) e).getAim();
            log.warning("IA HANDLED EXCEPTION : UnableToMoveException");
            tryToDoAnotherNode(this.lastNodeTried);
        }
        else if (e instanceof PointInObstacleException){
            //Bien géré normalement
            Vec2 problemPoint = ((PointInObstacleException) e).getPoint();
            log.warning("IA HANDLED EXCEPTION : PointInObstacleException");
            boolean isDepartInObstacle = ((PointInObstacleException) e).isDepartInOsbtacle();
            if (isDepartInObstacle){
                Vec2 aimToExitObstacle = pathfinding.getGraphe().closestNodeToPosition(gameState.robot.getPosition()).getPosition();
                goToHandleException(aimToExitObstacle);
            }
            else{
                tryToDoAnotherNode(this.lastNodeTried);
            }
        }
        else if (e instanceof ExecuteException){
            //Normalement ne doit pas arriver
            log.warning("IA HANDLED EXCEPTION : ExecuteException");
            log.critical("//////////////////// ExecuteException ! Ne devrait pas arriver ! ///////////////////////////");
            tryToDoAnotherNode(this.lastNodeTried);
        }
        else{
            log.critical("IA EXCEPTION : "+e.getClass().getName() +" NOT CURRENTLY HANDLED EXCEPTION");
            for (StackTraceElement elem : e.getStackTrace()) {
                log.critical(elem);
            }
            tryToDoAnotherNode(this.lastNodeTried);
        }
        log.warning("///// IA ///// On a résolu l'exception, on continue le match");
        resumeMatch();
    }



    private void resumeMatch(){
        while (!availableNodes.isEmpty()){
            updateAvailableNodes();
            tryToDoAnotherNode(this.lastNodeTried);
        }
        exploredNodes.clear();
        try {
            //Si cette condition est fausse, cela veut dire qu'il n'y a plus aucune node qui n'est pas faite, car on a reset toutes les nodes explorées
            if (findBestNode()!=null){
                resumeMatch();
            }
            else{
                log.debug("On a fini toutes les nodes disponibles");
            }
        } catch (NoNodesAvailableException e) {
            handleException(e);
        }
        log.debug("Plus aucune node n'est à faire, le match est terminé");
    }


    /**
     * Méthode permettant de faire un goto avec le robot à partir de handleException, tout en gérant les exceptions récursivement
     */
    private void goToHandleException(Vec2 aim){
        goToHandleException(aim,false,true);
    }

    private void goToHandleException(Vec2 aim, boolean expectsWallImpact, boolean mustDetect){
        try {
            gameState.robot.goTo(aim,expectsWallImpact,mustDetect);
            //On ne rassemble pas les exceptions en un seul catch pour pouvoir voir quelles exceptions sont lancées
        } catch (UnableToMoveException e) {
            log.debug("UnableToMoveEvent from goTo");
            gameState.robot.immobilise();
            handleException(e);
        } catch (ImmobileEnnemyForOneSecondAtLeast e) {
            log.debug("ImmobileEnnemyForOneSecondAtLeast from goTo");
            gameState.robot.immobilise();
            handleException(e);
        }
    }

    /**
     * Méthode permettant de tourner le robot à partir de handleException, tout en gérant les exceptions récursivement
     */
    private void turnRelativelyHandleException(double angle){
        try {
            gameState.robot.turnRelatively(angle);
            //On ne rassemble pas les exceptions en un seul catch pour pouvoir voir quelles exceptions sont lancées
        } catch (UnableToMoveException e) {
            log.debug("UnableToMoveEvent from turn");
            gameState.robot.immobilise();
            handleException(e);
        } catch (ImmobileEnnemyForOneSecondAtLeast e) {
            log.debug("ImmobileEnnemyForOneSecondAtLeast from turn");
            gameState.robot.immobilise();
            handleException(e);
        }
    }


    /**
     *  Renvoie le prochain noeud à executer. Si les tours sont remplies, on exception un
     *  dépose cube et sinon on va faire le script le plus proche.
     */
    private Node findBestNode() throws NoNodesAvailableException {
        Vec2 robotPosition = gameState.robot.getPosition();
        updateAvailableNodes(); //On récupère les nodes des actions qui n'ont pas encore été faites
        //Si toutes les nodes sont faites, on renvoie null;
        if (availableNodes.isEmpty()){
            return null;
        }

        //La dernière action de la partie quand on a plus beaucoup de temps
        else if (gameState.getTimeEllapsed()>85000){
            exploredNodes.clear();
            //Si on a une tour dans le robot, on va la déposer à DeposeCubes1
            if ((gameState.isTourAvantRemplie() || gameState.isTourArriereRemplie())){
                Node deposeCubes0 = nodes.getNodeByNameAndVersion(ScriptNames.DEPOSE_CUBES,0);
                Node deposeCubes1 = nodes.getNodeByNameAndVersion(ScriptNames.DEPOSE_CUBES,1);
                if (availableNodes.contains(deposeCubes0)) {
                    return deposeCubes0;
                }
                else if (availableNodes.contains(deposeCubes1)){
                    return deposeCubes1;
                }
            }
            //Si le panneau n'a pas encore été fait, et qu'on a pas de tours, on fait le panneau
            else if (availableNodes.contains(nodes.getNodeByNameAndVersion(ScriptNames.ACTIVATION_PANNEAU_DOMOTIQUE,0))){
                return nodes.getNodeByNameAndVersion(ScriptNames.ACTIVATION_PANNEAU_DOMOTIQUE,0);
            }
            //Si on n'a plus de tours, et que le panneau a déjà été activé, on va faire l'abeille, quitte à ce qu'il y ait un robot ennemi là bas
            else if (availableNodes.contains(nodes.getNodeByNameAndVersion(ScriptNames.ACTIVE_ABEILLE,0))){
                return nodes.getNodeByNameAndVersion(ScriptNames.ACTIVE_ABEILLE,0);
            }
        }

        //Si on a 2 tours dans le robot, on va les déposer
        else if (gameState.isTourAvantRemplie() && gameState.isTourArriereRemplie()){
            Node deposeCubes0 = nodes.getNodeByNameAndVersion(ScriptNames.DEPOSE_CUBES,0);
            Node deposeCubes1 = nodes.getNodeByNameAndVersion(ScriptNames.DEPOSE_CUBES,1);
            if (availableNodes.contains(deposeCubes0)){
                return deposeCubes0;
            }
            else if (availableNodes.contains(deposeCubes1)){
                return deposeCubes1;
            }
        }

        //Si on a une tour dans le robot, et qu'on a pris les trois tas, on va la déposer
        else if (gameState.isTourAvantRemplie() || gameState.isTourArriereRemplie()){
            int nbTasPris = 0;
            if (gameState.isTas_base_pris()){ nbTasPris++; }
            if (gameState.isTas_chateau_eau_pris()){ nbTasPris++; }
            if (gameState.isTas_station_epuration_pris()){ nbTasPris++; }
            if (nbTasPris==3){
                Node deposeCubes0 = nodes.getNodeByNameAndVersion(ScriptNames.DEPOSE_CUBES,0);
                Node deposeCubes1 = nodes.getNodeByNameAndVersion(ScriptNames.DEPOSE_CUBES,1);
                if (availableNodes.contains(deposeCubes0)){
                    return deposeCubes0;
                }
                else if (availableNodes.contains(deposeCubes1)){
                    return deposeCubes1;
                }
            }
        }


        double minCost = 1000000000;
        int j = -1;
        //Si aucun des cas spécifiques plus haut ne s'est présenté, on cherche la node la plus proche
        for (int i = 0; i<availableNodes.size();i++) {
            Node currentNode = availableNodes.get(i);
            if (!(currentNode instanceof DeposeCubes)) {
                double cost = calculateNodeCost(currentNode, robotPosition);
                if (cost < minCost) {
                    j = i;
                    minCost = cost;
                }
            }
        }
        if (j == -1) {
            throw new NoNodesAvailableException();
        }
        else{
            return availableNodes.get(j);
        }
    }

    /**
     * Calcul le coût d'une node
     */
    private double calculateNodeCost(Node node, Vec2 robotPosition){
        double cost=0;
        try {
            cost = pathfinding.howManyTime(robotPosition, node.getPosition());
        } catch (PointInObstacleException e){
            if (e.isDepartInOsbtacle()){
                handleException(e);
            } else{
                cost = 1000000000;
            }
        } catch (NoPathFound noPathFound){
            log.debug("NoPathFound from calculateNodeCost");
            //si il n'y a pas de chemin, on met un cout énorme pour ne pas y aller
            cost = 1000000000;
        }
        return cost;
    }


    /**
     * Méthode essayant d'aller à une autre node, en les essayant toutes
     * @return si on a réussi à aller à une node et à l'exécuter
     */
    public void tryToDoAnotherNode(Node previousFailedNode) {
        if (previousFailedNode != null) {
            if (!exploredNodes.contains(previousFailedNode)) {
                exploredNodes.add(previousFailedNode);
            }
        }
        Node nextNode;
        try {
            nextNode = findBestNode();
        } catch (NoNodesAvailableException e) {
            handleException(e);
            return;
        }
        if (nextNode!=null) {
            exploredNodes.add(nextNode);
            try {
                this.lastNodeTried = nextNode;
                this.gameState.setLastScript(nextNode.getScriptName());
                this.gameState.setLastScriptVersion(nextNode.getVersionToExecute());
                log.debug("////////// IA ////////// SELECTED NODE : " + nextNode.getName() + " " + nextNode.getVersionToExecute());
                log.debug("////////// IA ////////// EXECUTE : " + nextNode.getName() + " " + nextNode.getVersionToExecute());
                nextNode.execute(gameState);
                nextNode.setDone(true);
                log.debug("Node (" + nextNode.getName() + ", " + nextNode.getVersionToExecute() + ") is done");
            } catch (Exception e) {
                log.debug("Exception : "+e.getClass());
                gameState.robot.immobilise();
                handleException(e);
            }
        }
        else{
            handleException(new NoNodesAvailableException());
        }
    }


    /**
     * Méthode permettant d'esquiver un ennemi
     * @param aim le point visé
     */
    public boolean dodgeEnnemy(Vec2 aim){
        log.debug("On tente d'esquiver");
        boolean ennemyDodged = false;
        int attemps=0;
        while (!ennemyDodged && attemps<3){
            attemps++;
            try {
                log.debug("Début esquive (tentative "+attemps+")");
                //On s'éloigne de l'aim
                Vec2 directionToGo = (aim.minusNewVector(gameState.robot.getPosition()));
                double prodScal = directionToGo.dot(new Vec2(100.0, gameState.robot.getOrientation()));


                //On regarde si le point où l'on veut reculer est dans un obstacle, si c'est le cas, on throw PointInObstacleException
                if (prodScal>0) {
                    Vec2 wantToBackUpTo=gameState.robot.getPosition().plusNewVector(new Vec2(-50.0,gameState.robot.getOrientation()));
                    if (!gameState.table.getObstacleManager().isPositionInObstacle(wantToBackUpTo)) {
                        gameState.robot.moveLengthwise(-50);
                    }
                    else{
                        log.debug("Point in obstacle");
                        //on renvoie une exception avec notre objectif initial en paramètre
                        throw new PointInObstacleException(aim, false);
                    }
                }
                else{
                    Vec2 wantToBackUpTo=gameState.robot.getPosition().plusNewVector(new Vec2(50.0,gameState.robot.getOrientation()));
                    if (!gameState.table.getObstacleManager().isPositionInObstacle(wantToBackUpTo)) {
                        gameState.robot.moveLengthwise(50);
                    }
                    else{
                        log.debug("Point in obstacle");
                        //on renvoie une exception avec notre objectif initial en paramètre
                        throw new PointInObstacleException(aim, false);
                    }
                }


                //On cherche un nouveau chemin pour y aller
                ArrayList<Vec2> pathToFollow = gameState.robot.getPathfinding().findmyway(gameState.robot.getPosition(), aim);
                gameState.robot.followPath(pathToFollow);
                ennemyDodged = true;

            } catch (ImmobileEnnemyForOneSecondAtLeast immobileEnnemyForOneSecondAtLeast) {
                gameState.robot.immobilise();
                log.debug("L'ennemi est toujours là");
                Sleep.sleep(250);
            } catch (PointInObstacleException e1) {
                gameState.robot.immobilise();
                log.debug("PointInObstacleException, on part au noeud le plus proche");
                Sleep.sleep(250);
            } catch (UnableToMoveException e1) {
                gameState.robot.immobilise();
                log.debug("UnableToMoveException");
                Sleep.sleep(250);
            } catch (NoPathFound noPathFound) {
                gameState.robot.immobilise();
                log.debug("NoPathFound");
                Sleep.sleep(250);
            }
        }
        return ennemyDodged;
    }


    /**
     * On update la liste des nodes qui n'ont pas encore été faites et qu'on a pas déjà essayé d'explorer
     */
    private void updateAvailableNodes() {
        availableNodes.clear();
        for (Node node : nodes.getArrayList()) {
            if (!node.isDone()) {
                if (!exploredNodes.contains(node)) {
                    availableNodes.add(node);
                }
            }
        }
    }


    /**
     * Créer les noeuds du graphe de décision
     */
    private NodeArray createNodes() throws BadVersionException {
        Node abeille = new Abeille(ScriptNames.ACTIVE_ABEILLE,1,  scriptManager, gameState,pathfinding,hookFactory,config, log);
        Node panneau = new Panneau(ScriptNames.ACTIVATION_PANNEAU_DOMOTIQUE,0,  scriptManager, gameState,pathfinding,hookFactory,config, log);
        Node takeCubes = new TakeCubes(ScriptNames.TAKE_CUBES,0, scriptManager, gameState,pathfinding,hookFactory,config, log);
        Node takeCubes2 = new TakeCubes(ScriptNames.TAKE_CUBES,1, scriptManager, gameState,pathfinding,hookFactory,config, log);
        Node takeCubes3 = new TakeCubes(ScriptNames.TAKE_CUBES,2, scriptManager, gameState,pathfinding,hookFactory,config, log);
        Node deposeCubes = new DeposeCubes(ScriptNames.DEPOSE_CUBES,0, scriptManager, gameState,pathfinding,hookFactory,config, log);
        Node deposeCubes2 = new DeposeCubes(ScriptNames.DEPOSE_CUBES,1, scriptManager, gameState,pathfinding,hookFactory,config, log);
        NodeArray nodes = new NodeArray();
        nodes.add(panneau);
        nodes.add(abeille);
        nodes.add(takeCubes);
        nodes.add(takeCubes2);
        nodes.add(deposeCubes);
        nodes.add(takeCubes3);
        nodes.add(deposeCubes2);
        return nodes;
    }

    public Graph getGraph() {return graph;}

    @Override
    public void updateConfig() {

    }

}
