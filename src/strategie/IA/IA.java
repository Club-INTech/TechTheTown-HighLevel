package strategie.IA;

import container.Service;
import enums.ScriptNames;
import exceptions.BadVersionException;
import exceptions.BlockedActuatorException;
import exceptions.ExecuteException;
import exceptions.Locomotion.ImmobileEnnemyForOneSecondAtLeast;
import exceptions.Locomotion.PointInObstacleException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.NoPathFound;
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
    private NodeArray nodes;
    private ArrayList<Node> exploredNodes;
    private ArrayList<Node> availableNodes;
    private HookFactory hookFactory;

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
        boolean reussite = goToAnotherNode(null);
        resumeMatch();
    }


    private void resumeMatch(){
        while (findBestNode()!=null) {
            goToAnotherNode(null);
        }
        log.debug("On a fini toutes les nodes disponibles");
    }


    /**
     *  Renvoie le prochain noeud à executer. Si les tours sont remplies, on exception un
     *  dépose cube et sinon on va faire le script le plus proche.
     */
    private Node findBestNode() {
        Vec2 robotPosition = gameState.robot.getPosition();
        updateAvailableNodes(); //On récupère les nodes des actions qui n'ont pas encore été faites
        double dmin = 1000000000;
        int j = 0;
        //Si toutes les nodes sont faites, on renvoie null;
        if (availableNodes.isEmpty()){
            return null;
        }

        //La dernière action de la partie quand on a plus beaucoup de temps
        else if (gameState.getTimeEllapsed()>85000){
            //Si on a une tour dans le robot, on va la déposer à DeposeCubes1
            if ((gameState.isTourAvantRemplie() || gameState.isTourArriereRemplie())){
                Node deposeCubes0 = nodes.getNodeByNameAndVersion(ScriptNames.DEPOSE_CUBES,0);
                if (!availableNodes.contains(deposeCubes0)) {
                    return nodes.getNodeByNameAndVersion(ScriptNames.DEPOSE_CUBES, 1);
                }
                else{
                    return nodes.getNodeByNameAndVersion(ScriptNames.DEPOSE_CUBES, 1);
                }
            }
            //Si le panneau n'a pas encore été fait, et qu'on a pas de tours, on fait le panneau
            else if (!availableNodes.contains(nodes.getNodeByNameAndVersion(ScriptNames.ACTIVATION_PANNEAU_DOMOTIQUE,0))){
                return nodes.getNodeByNameAndVersion(ScriptNames.ACTIVATION_PANNEAU_DOMOTIQUE,0);
            }
            //Si on n'a plus de tours, et que le panneau a déjà été activé, on va faire l'abeille, quitte à ce qu'il y ait un robot ennemi là bas
            else if (!availableNodes.contains(nodes.getNodeByNameAndVersion(ScriptNames.ACTIVE_ABEILLE,0))){
                return nodes.getNodeByNameAndVersion(ScriptNames.ACTIVE_ABEILLE,0);
            }
        }

        //Si on a 2 tours dans le robot, on va les déposer
        else if (gameState.isTourAvantRemplie() && gameState.isTourArriereRemplie()){
            Node deposeCubes0 = nodes.getNodeByNameAndVersion(ScriptNames.DEPOSE_CUBES,0);
            if (!availableNodes.contains(deposeCubes0)){
                return deposeCubes0;
            }
            else{
                return nodes.getNodeByNameAndVersion(ScriptNames.DEPOSE_CUBES,1);
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
                if (!availableNodes.contains(deposeCubes0)){
                    return deposeCubes0;
                }
                else{
                    return nodes.getNodeByNameAndVersion(ScriptNames.DEPOSE_CUBES,1);
                }
            }
        }


        //Si aucun des cas spécifiques plus haut ne s'est présenté, on cherche la node la plus proche
        for (int i = 0; i<availableNodes.size();i++){
            Node currentNode = availableNodes.get(i);
            if (!(currentNode instanceof DeposeCubes)){
                double d = 0;
                try {
                    d = pathfinding.howManyTime(robotPosition, currentNode.getPosition());
                } catch (UnableToMoveException e) {
                    e.printStackTrace();
                } catch (PointInObstacleException e) {
                    e.printStackTrace();
                } catch (NoPathFound noPathFound) {
                    noPathFound.printStackTrace();
                    //si il n'y a pas de chemin, on n'y vas pas.
                    d = 1000000000;
                }
                if (d < dmin) {
                    j = i;
                    dmin = d;
                }
            }
        }
        return availableNodes.get(j);
    }

    /**
     * Méthode essayant d'aller à une autre node, en les essayant toutes
     * @return si on a réussi à aller à une node et à l'exécuter
     */
    public boolean goToAnotherNode(Node previousFailedNode) {
        Node nextNode = findBestNode();
        if (previousFailedNode!=null) {
            exploredNodes.add(previousFailedNode);
        }
        boolean success=false;
        while ((nextNode != null) || (!success)){
            exploredNodes.add(nextNode);
            try {
                log.debug("////////// IA ////////// SELECTED NODE : "+nextNode.getName()+" "+nextNode.getVersionToExecute());
                log.debug("////////// IA ////////// EXECUTE : "+nextNode.getName()+" "+nextNode.getVersionToExecute());
                nextNode.execute(gameState);
                nextNode.setDone(true);
                success=true;
            } catch (ImmobileEnnemyForOneSecondAtLeast immobileEnnemyForOneSecondAtLeast) {
                log.debug("ImmobileEnnemyForOneSecondAtLeast");
                immobileEnnemyForOneSecondAtLeast.printStackTrace();
                nextNode = findBestNode();
            } catch (PointInObstacleException e) {
                log.debug("PointInObstacleException");
                e.printStackTrace();
                nextNode = findBestNode();
            } catch (BlockedActuatorException e) {
                log.debug("BlockedActuatorException");
                e.printStackTrace();
                nextNode = findBestNode();
            } catch (ExecuteException e) {
                log.debug("ExecuteException");
                e.printStackTrace();
                nextNode = findBestNode();
            } catch (BadVersionException e) {
                log.debug("BadVersionException");
                e.printStackTrace();
                nextNode = findBestNode();
            } catch (UnableToMoveException e) {
                log.debug("UnableToMoveException");
                e.printStackTrace();
                nextNode = findBestNode();
            }
        }
        exploredNodes.clear();
        return success;
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
                log.debug("L'ennemi est toujours là");
                Sleep.sleep(250);
            } catch (PointInObstacleException e1) {
                log.debug("PointInObstacleException, on part au noeud le plus proche");
                Sleep.sleep(250);
            } catch (UnableToMoveException e1) {
                log.debug("UnableToMoveException");
                Sleep.sleep(250);
            } catch (NoPathFound noPathFound) {
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
        Node abeille = new Abeille(ScriptNames.ACTIVE_ABEILLE,0,  scriptManager, gameState,pathfinding,hookFactory,config, log);
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
