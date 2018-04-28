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
    private ArrayList<Node> nodes;
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
        this.nodesToExecute = kruskal();
        this.nextNode = theAnswer();
    }

    /** Créer les noeuds du graphe de décision. */

    public ArrayList<Node> createNodes() throws BadVersionException {
        Node pattern = new Pattern("Pattern",0, null, scriptManager, gameState,pathfinding,hookFactory,config, log);
        Node abeille = new Abeille("Abeille",1, null, scriptManager, gameState,pathfinding,hookFactory,config, log);
        Node panneau = new Panneau("Panneau",0, null, scriptManager, gameState,pathfinding,hookFactory,config, log);
        Node takeCubes = new TakeCubes("TakeCubes",0, null, scriptManager, gameState,pathfinding,hookFactory,config, log);
        Node takeCubes2 = new TakeCubes("TakeCubes",1, null, scriptManager, gameState,pathfinding,hookFactory,config, log);
        Node takeCubes3 = new TakeCubes("TakeCubes",2, null, scriptManager, gameState,pathfinding,hookFactory,config, log);
        Node deposeCubes = new DeposeCubes("DeposeCube",0, null, scriptManager, gameState,pathfinding,hookFactory,config, log);
        Node deposeCubes2 = new DeposeCubes("DeposeCube",1, null, scriptManager, gameState,pathfinding,hookFactory,config, log);

        ArrayList<Node> nodes = new ArrayList<>();
//        nodes.add(pattern);
        nodes.add(abeille);
        nodes.add(panneau);
        nodes.add(takeCubes);
        nodes.add(takeCubes2);
        nodes.add(takeCubes3);
        nodes.add(deposeCubes);
        nodes.add(deposeCubes2);

        return nodes;
    }

    /** Renvoie le prochain noeud à executer. Si les tours sont remplies, on execute un
     *  dépose cube et sinon on va faire le script le plus proche.
     *
     */

    public Node theAnswer() {

        Vec2 robotPosition = gameState.robot.getPosition();
        updateAvailableNodes();
        double dmin = 66666666666666.;
        int j = 0;
        if (availableNodes.isEmpty())
            return null;
        if (gameState.isTourAvantRemplie() && gameState.isTourArriereRemplie()){
            if (availableNodes.contains(nodes.get(nodes.size()-2))){
                return nodes.get(nodes.size()-2);
            } else{
                return nodes.get(nodes.size()-1);
            }
        }
        if ( (gameState.isTourAvantRemplie() || gameState.isTourArriereRemplie()) && gameState.isTas_base_pris() && gameState.isTas_chateau_eau_pris() &&
        gameState.isTas_station_epuration_pris()){
            if (availableNodes.contains(nodes.get(nodes.size()-2))){
                return nodes.get(nodes.size()-2);
            } else{
                return nodes.get(nodes.size()-1);
            }
        }
        for (int i = 0; i<availableNodes.size();i++){
            try {
                double d = pathfinding.howManyTime(robotPosition, availableNodes.get(i).position);
                if (d<dmin){
                    j=i;
                    dmin=d;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return availableNodes.get(j);
    }
    public void updateAvailableNodes(){
        availableNodes.clear();
        for (Node node: nodes) {
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

    public ArrayList<Node> edgeToNode(ArrayList<Edge> edges) {
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
        while (nextNode != null){
            try {
                log.debug("//////IA////// SELECTED NODE : "+nextNode.name);
                nextNode.execute(e, gameState);
                log.debug("//////IA////// EXECUTE : "+nextNode.name);
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
    }

    public void display(){
        log.debug("Nodes to execute :");
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
