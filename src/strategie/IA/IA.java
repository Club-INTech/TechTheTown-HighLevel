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
    private ArrayList<Node> nodesToExecute;

    /** Permet de s'adapter au déroulement d'un match grace à un graphe de décision. */

    public IA(GameState gameState, ScriptManager scriptManager) throws BadVersionException {
        this.gameState = gameState;
        this.scriptManager = scriptManager;
        this.graph = new Graph(createNodes());
        this.nodesToExecute = kruskal();
    }

    /** Créer les noeuds du graphe de décision. */

    public ArrayList<Node> createNodes() throws BadVersionException {
        Node pattern = new Pattern(0, null, scriptManager, gameState);
        Node abeille = new Abeille(0, null, scriptManager, gameState);
        Node panneau = new Panneau(0, null, scriptManager, gameState);
        Node takeCubes = new TakeCubes(0, null, scriptManager, gameState);
        Node deposeCubes = new DeposeCubes(0, null, scriptManager, gameState);

        ArrayList<Node> nodes = new ArrayList<>();
        nodes.add(pattern);
        nodes.add(abeille);
        nodes.add(panneau);
        nodes.add(takeCubes);
        nodes.add(deposeCubes);

        return nodes;
    }

    /** Trouve un parcourt optimal dans l'arbre de décision. */

    public ArrayList<Node> kruskal() {
        ArrayList<Edge> bestEdges = new ArrayList<>();
        UnionFind u1 = new UnionFind(graph.getNodes().size());
        Edge curentEdge;
        while (graph.getEdges().peek() != null){
            curentEdge = graph.getEdges().poll();
            if(u1.find(curentEdge.getNode1().getId()) != u1.find(curentEdge.getNode2().getId())){
                bestEdges.add(curentEdge);
                u1.union(curentEdge.getNode1().getId(), curentEdge.getNode2().getId());
            }
        }
        return bestEdges;
    }

    public void execute(Exception e) throws BlockedActuatorException, UnableToMoveException, PointInObstacleException, ExecuteException, BadVersionException {
//        root.selectNode().execute(e);
    }

    public Graph getGraph() {
        return graph;
    }

    @Override
    public void updateConfig() {

    }

}
