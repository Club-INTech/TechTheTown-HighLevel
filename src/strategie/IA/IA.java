package strategie.IA;

import container.Service;
import enums.ConfigInfoRobot;
import enums.ScriptNames;
import exceptions.BadVersionException;
import exceptions.BlockedActuatorException;
import exceptions.ExecuteException;
import exceptions.Locomotion.ImmobileEnnemyForOneSecondAtLeast;
import exceptions.Locomotion.PointInObstacleException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.Locomotion.UnexpectedObstacleOnPathException;
import exceptions.NoPathFound;
import hook.HookFactory;
import hook.HookNames;
import pathfinder.Pathfinding;
import pfg.config.Config;
import scripts.*;
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
    private ArrayList<Node> nodesToExecute;
    private HookFactory hookFactory;

    /** Permet de s'adapter au déroulement d'un match grace à un graphe de décision. */

    public IA(Log log, Config config, GameState gameState, ScriptManager scriptManager, Pathfinding pathfinding, HookFactory hookFactory) throws BadVersionException, UnableToMoveException, PointInObstacleException, NoPathFound {
        this.log = log;
        this.config = config;
        this.gameState = gameState;
        this.scriptManager = scriptManager;
        this.pathfinding = pathfinding;
        this.hookFactory = hookFactory;
        this.graph = new Graph(createNodes());
        this.nb_tas_pris = 0;
        this.nodesToExecute = kruskal();
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
        nodes.add(pattern);
        nodes.add(abeille);
        nodes.add(panneau);
        nodes.add(takeCubes);
        nodes.add(deposeCubes);
        nodes.add(deposeCubes2);
        nodes.add(takeCubes2);
        nodes.add(takeCubes3);

        return nodes;
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
            execute(e);
        }
    }

    public void execute(Exception e) {
        graph.clean();
        kruskal();
        for(Node node : nodesToExecute){
            try {
                node.execute(e,gameState);
            } catch (Exception e1){
                e1.printStackTrace();
                node.exception(e1);
            }
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

    @Override
    public void updateConfig() {    }

}
