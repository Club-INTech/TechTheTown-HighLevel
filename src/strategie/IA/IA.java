package strategie.IA;

import container.Service;
import exceptions.BadVersionException;
import exceptions.BlockedActuatorException;
import exceptions.ExecuteException;
import exceptions.Locomotion.ImmobileEnnemyForOneSecondAtLeast;
import exceptions.Locomotion.PointInObstacleException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.NoPathFound;
import hook.HookFactory;
import hook.HookNames;
import pathfinder.Pathfinding;
import scripts.*;
import strategie.GameState;

import java.util.ArrayList;

public class IA implements Service {

    private GameState gameState;
    private ScriptManager scriptManager;
    private Graph graph;
    private Pathfinding pathfinding;
    private int nb_tas_pris;
    private ArrayList<Node> nodesToExecute;
    private HookFactory hookFactory;

    /** Permet de s'adapter au déroulement d'un match grace à un graphe de décision. */

    public IA(GameState gameState, ScriptManager scriptManager, Pathfinding pathfinding, HookFactory hookFactory) throws BadVersionException, UnableToMoveException, PointInObstacleException, NoPathFound {
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
        Node pattern = new Pattern(0, null, scriptManager, gameState);
        Node abeille = new Abeille(1, null, scriptManager, gameState);
        Node panneau = new Panneau(0, null, scriptManager, gameState);
        Node takeCubes = new TakeCubes(0, null, scriptManager, gameState);
        Node takeCubes2 = new TakeCubes(1, null, scriptManager, gameState);
        Node takeCubes3 = new TakeCubes(2, null, scriptManager, gameState);
        Node deposeCubes = new DeposeCubes(0, null, scriptManager, gameState);
        Node deposeCubes2 = new DeposeCubes(0, null, scriptManager, gameState);

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

    public ArrayList<Node> kruskal() throws UnableToMoveException, PointInObstacleException, NoPathFound {
        graph.setEdgesCost(pathfinding);
        ArrayList<Edge> bestEdges = new ArrayList<>();
        UnionFind u1 = new UnionFind(graph.getNodes().size());
        Edge curentEdge;
        while (graph.getEdges().peek() != null){
//            graph.updateEdgesCost();
            curentEdge = graph.getEdges().poll();
            if(u1.find(curentEdge.getNode1().getId()) != u1.find(curentEdge.getNode2().getId())){
                bestEdges.add(curentEdge);
                u1.union(curentEdge.getNode1().getId(), curentEdge.getNode2().getId());
                if(curentEdge.getNode1().toString().equals("TakeCubes")||curentEdge.getNode2().toString().equals("TakeCubes")){
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

    public void execute(Exception e) throws BlockedActuatorException, UnableToMoveException, PointInObstacleException, ExecuteException, BadVersionException, ImmobileEnnemyForOneSecondAtLeast {
        hookFactory.configureHook(HookNames.ACTIVE_BRAS_AVANT_ABEILLE, HookNames.ACTIVE_BRAS_ARRIERE_ABEILLE, HookNames.ACTIVE_BRAS_AVANT_ABEILLE_SYMETRIQUE, HookNames.ACTIVE_BRAS_ARRIERE_ABEILLE_SYMETRIQUE);
        for(Node node : nodesToExecute){
            node.execute(e);
        }
    }

    public Graph getGraph() {return graph;}

    public GameState getGameState() {return gameState;}

    @Override
    public void updateConfig() {    }

}
