package strategie.IA;

import container.Service;
import exceptions.BadVersionException;
import exceptions.BlockedActuatorException;
import exceptions.ExecuteException;
import exceptions.Locomotion.PointInObstacleException;
import exceptions.Locomotion.UnableToMoveException;
import scripts.*;
import strategie.GameState;

import java.util.ArrayList;

public class IA implements Service {

    private GameState gameState;
    private ScriptManager scriptManager;
    private Graph graph;
    private int nb_tas_pris;
    private ArrayList<Node> nodesToExecute;

    /** Permet de s'adapter au déroulement d'un match grace à un graphe de décision. */

    public IA(GameState gameState, ScriptManager scriptManager) throws BadVersionException {
        this.gameState = gameState;
        this.scriptManager = scriptManager;
        this.graph = new Graph(createNodes());
        this.nb_tas_pris = 0;
        this.nodesToExecute = kruskal();
    }

    /** Créer les noeuds du graphe de décision. */

    public ArrayList<Node> createNodes() throws BadVersionException {
        Node pattern = new Pattern(0, null, scriptManager, gameState);
        Node abeille = new Abeille(0, null, scriptManager, gameState);
        Node panneau = new Panneau(0, null, scriptManager, gameState);
        Node takeCubes = new TakeCubes(0, null, scriptManager, gameState);
        Node takeCubes2 = new TakeCubes(0, null, scriptManager, gameState);
        Node deposeCubes = new DeposeCubes(0, null, scriptManager, gameState);

        ArrayList<Node> nodes = new ArrayList<>();
        nodes.add(pattern);
        nodes.add(abeille);
        nodes.add(panneau);
        nodes.add(takeCubes);
        nodes.add(deposeCubes);
        nodes.add(takeCubes2);

        return nodes;
    }

    /** Trouve un parcourt optimal dans le graphe de décision. */

    public ArrayList<Node> kruskal() {
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
            if(!nodes.contains(edge.getNode1())){
                nodes.add(edge.getNode1());
            }
            if(!nodes.contains(edge.getNode2())){
                nodes.add(edge.getNode2());
            }
        }
         return nodes;
    }

    public void execute(Exception e) throws BlockedActuatorException, UnableToMoveException, PointInObstacleException, ExecuteException, BadVersionException {
        for(Node node : nodesToExecute){
            node.execute(e);
        }
    }

    public Graph getGraph() {return graph;}

    public GameState getGameState() {return gameState;}

    @Override
    public void updateConfig() {    }

}
