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

import java.util.ArrayList;

public class IA implements Service {

    private Log log;
    private Config config;
    private GameState gameState;
    private ScriptManager scriptManager;
    private Graph graph;
    private Pathfinding pathfinding;
    private int nb_tas_pris;
    private NodeArray nodes;
    private ArrayList<Node> availableNodes;
    private ArrayList<Node> nodesToExecute;
    private Node nextNode;
    private HookFactory hookFactory;

    /** Permet de s'adapter au déroulement d'un match grace à un graphe de décision. */

    public IA(Log log, Config config, GameState gameState, ScriptManager scriptManager, Pathfinding pathfinding, HookFactory hookFactory) throws BadVersionException, UnableToMoveException, PointInObstacleException, NoPathFound {
        this.log = log;
        this.config = config;
        this.gameState = gameState;
        this.scriptManager = scriptManager;
        this.pathfinding = pathfinding;
        this.hookFactory = hookFactory;
        this.graph = new Graph(createNodes(),log);
        this.nb_tas_pris = 0;
        this.nodes = createNodes();
        this.availableNodes = new ArrayList<>();
//        this.nodesToExecute = kruskal();
//        this.nextNode = theAnswer();
    }

    /** Créer les noeuds du graphe de décision. */

    private NodeArray createNodes() throws BadVersionException {
        Node abeille = new Abeille("Abeille",0,  scriptManager, gameState,pathfinding,hookFactory,config, log);
        Node panneau = new Panneau("Panneau",0,  scriptManager, gameState,pathfinding,hookFactory,config, log);
        Node takeCubes = new TakeCubes("TakeCubes",0, scriptManager, gameState,pathfinding,hookFactory,config, log);
        Node takeCubes2 = new TakeCubes("TakeCubes",1, scriptManager, gameState,pathfinding,hookFactory,config, log);
        Node takeCubes3 = new TakeCubes("TakeCubes",2, scriptManager, gameState,pathfinding,hookFactory,config, log);
        Node deposeCubes = new DeposeCubes("DeposeCubes",0, scriptManager, gameState,pathfinding,hookFactory,config, log);
        Node deposeCubes2 = new DeposeCubes("DeposeCubes",1, scriptManager, gameState,pathfinding,hookFactory,config, log);

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

    /** Renvoie le prochain noeud à executer. Si les tours sont remplies, on execute un
     *  dépose cube et sinon on va faire le script le plus proche.
     */

    private Node theAnswer() {
        Vec2 robotPosition = gameState.robot.getPosition();
        updateAvailableNodes(); //On récupère les nodes dont on a besoin
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
                Node deposeCubes0 = nodes.getNodeByNameAndVersion("DeposeCubes",0);
                if (!availableNodes.contains(deposeCubes0)) {
                    return nodes.getNodeByNameAndVersion("DeposeCubes", 1);
                }
                else{
                    return nodes.getNodeByNameAndVersion("DeposeCubes", 1);
                }
            }
            //Si le panneau n'a pas encore été fait, et qu'on a pas de tours, on fait le panneau
            else if (!availableNodes.contains(nodes.getNodeByNameAndVersion("Panneau",0))){
                return nodes.getNodeByNameAndVersion("Panneau",0);
            }
            //Si on n'a plus de tours, et que le panneau a déjà été activé, on va faire l'abeille, quitte à ce qu'il y ait un robot ennemi là bas
            else if (!availableNodes.contains(nodes.getNodeByNameAndVersion("Abeille",0))){
                return nodes.getNodeByNameAndVersion("Abeille",0);
            }
        }

        //Si on a 2 tours dans le robot, on va les déposer
        else if (gameState.isTourAvantRemplie() && gameState.isTourArriereRemplie()){
            Node deposeCubes0 = nodes.getNodeByNameAndVersion("DeposeCubes",0);
            if (!availableNodes.contains(deposeCubes0)){
                return deposeCubes0;
            }
            else{
                return nodes.getNodeByNameAndVersion("DeposeCubes",1);
            }
        }

        //Si on a une tour dans le robot, et qu'on a pris les trois tas, on va la déposer
        else if (gameState.isTourAvantRemplie() || gameState.isTourArriereRemplie()){
            int nbTasPris = 0;
            if (gameState.isTas_base_pris()){ nbTasPris++; }
            if (gameState.isTas_chateau_eau_pris()){ nbTasPris++; }
            if (gameState.isTas_station_epuration_pris()){ nbTasPris++; }
            if (nbTasPris==3){
                Node deposeCubes0 = nodes.getNodeByNameAndVersion("DeposeCubes",0);
                if (!availableNodes.contains(deposeCubes0)){
                    return deposeCubes0;
                }
                else{
                    return nodes.getNodeByNameAndVersion("DeposeCubes",1);
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
                    if (e.isDepartInOsbtacle()) {
                        currentNode.exception(e);
                    }
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

    //On récupère les nodes qui n'ont pas encore été faites
    private void updateAvailableNodes(){
        availableNodes.clear();
        for (Node node: nodes.getArrayList()) {
            if (!node.isDone()) {
                availableNodes.add(node);
            }
        }
    }

    /** Trouve un parcourt optimal dans le graphe de décision. */

    public ArrayList<Node> kruskal()  {
        try {
            graph.setEdgesCost(pathfinding);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ArrayList<Edge> bestEdges = new ArrayList<>();
        UnionFind u1 = new UnionFind(graph.getNodes().size());
        Edge curentEdge;
        while (graph.getEdges().peek() != null){
            graph.updateEdgesCost(nb_tas_pris);
            curentEdge = graph.getEdges().poll();
            if(u1.find(curentEdge.getNode1().getId()) != u1.find(curentEdge.getNode2().getId())){
                bestEdges.add(curentEdge);
                u1.union(curentEdge.getNode1().getId(), curentEdge.getNode2().getId());
                if(curentEdge.getNode1().getName().equals("TakeCubes")||curentEdge.getNode2().getName().equals("TakeCubes")){
                    nb_tas_pris++;
                    graph.updateEdgesCost(nb_tas_pris);
                }
            }
        }
        return edgeToNode(bestEdges);
    }

    /** Transforme le parcours optimal composé d'arrete en une liste de noeud à exécuter */

    private ArrayList<Node> edgeToNode(ArrayList<Edge> edges) {
        ArrayList<Node> nodes = new ArrayList<>();
        for (Edge edge : edges){
            if(edge.getNode1().getScore()>edge.getNode2().getScore()){
                if(!nodes.contains(edge.getNode1())){
                    nodes.add(edge.getNode1());
                }
                if(!nodes.contains(edge.getNode2())){
                    nodes.add(edge.getNode2());
                }
            }
            else{
                if(!nodes.contains(edge.getNode2())){
                    nodes.add(edge.getNode2());
                }
                if(!nodes.contains(edge.getNode1())){
                    nodes.add(edge.getNode1());
                }
            }
        }
        return nodes;
    }

    public void start(ScriptNames scriptNames, int versionToExecute)  {
        try {
            scriptManager.getScript(scriptNames).goToThenExec(versionToExecute,gameState);
        } catch (Exception e) {
            e.printStackTrace();
            log.debug("Exception");
            execute(e);
        }
    }

    public void execute(Exception e) {
//        graph.clean();
//        log.debug("Clean du graphe");
//        setNodesToExecute(kruskal());
//        display();
//        for(Node node : nodesToExecute){
//            try {
//                node.execute(e,gameState);
//            } catch (Exception e1){
//                e1.printStackTrace();
//                execute(e1);
//            }
//        }
        nextNode = theAnswer();
        int n=availableNodes.size();
        int attempts=0;
        while (nextNode != null && attempts<n){
            try {
                log.debug("//////IA////// SELECTED NODE : "+nextNode.name +" "+nextNode.getVersionToExecute());
                nextNode.execute(e, gameState);
                attempts++;
                log.debug("//////IA////// EXECUTE : "+nextNode.name+ " "+nextNode.getVersionToExecute());
            } catch (PointInObstacleException e1) {
                e1.printStackTrace();
            } catch (BadVersionException e1) {
                e1.printStackTrace();
            } catch (ExecuteException e1) {
                e1.printStackTrace();
            } catch (BlockedActuatorException e1) {
                e1.printStackTrace();
            } catch (UnableToMoveException e1) {
                e1.printStackTrace();
            } catch (ImmobileEnnemyForOneSecondAtLeast immobileEnnemyForOneSecondAtLeast) {
                immobileEnnemyForOneSecondAtLeast.printStackTrace();
            }
            nextNode = theAnswer();
        }
        //On a vu un ennemi, et on a testé toutes les possibilités, mais on le voit toujours : on esquive
        if(attempts>n){
            //On esquive
            log.debug("On tente une esquive.");
            if(e instanceof ImmobileEnnemyForOneSecondAtLeast){
                nextNode.exception(e);
            }
        }
    }

    public void display(){
        log.debug("NodeArray to execute :");
        for(Node node: nodesToExecute){
            log.debug(node.toString());
        }
    }

    public Graph getGraph() {return graph;}

    public GameState getGameState() {return gameState;}

    public void setNodesToExecute(ArrayList<Node> nodesToExecute) {this.nodesToExecute = nodesToExecute;}

    @Override
    public void updateConfig() {    }

}
