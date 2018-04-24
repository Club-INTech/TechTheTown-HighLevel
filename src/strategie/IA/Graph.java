package strategie.IA;

import exceptions.Locomotion.PointInObstacleException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.NoPathFound;
import pathfinder.Pathfinding;
import strategie.GameState;
import utils.Log;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

public class Graph {

    private ArrayList<Node> nodes;
    private PriorityQueue<Edge> edges;
    private Log log;

    /** Graphe de décision qui gère les actions à effectuer durant un match. */

    public Graph(ArrayList<Node> nodes, Log log) {
        this.nodes = nodes;
        this.edges = createEdge();
        this.log = log;
    }

    /** Génère les arretes du graphe, est appelé par le constructeur de Graph. */

    public PriorityQueue<Edge> createEdge(){
        PriorityQueue<Edge> edges = new PriorityQueue<>(new Comparator<Edge>() {
            @Override
            public int compare(Edge edge, Edge t1) {
                if (edge.getCost()>t1.getCost()){
                    return 1;
                }
                else if (edge.getCost()<t1.getCost()){
                    return -1;
                } else {
                    return 0;
                }
            }
        });
        int n = nodes.size();
        for (int i = 0; i < n ; i++){
            for(int j = i+1; j < n ; j++){
                edges.add(new Edge(nodes.get(i),nodes.get(j)));
            }
            nodes.get(i).setId(i);
        }
        return edges;
    }

    /** Set le couts des arretes. */

    public void setEdgesCost(Pathfinding pathfinding) throws UnableToMoveException, PointInObstacleException, NoPathFound {
        for(Edge edge: edges){
//            edge.setCost(edge.getNode1().getPosition().distance(edge.getNode2().getPosition()));
            edge.setCost(pathfinding.howManyTime(edge.getNode1().getPosition(),edge.getNode2().getPosition()));
        }
    }

    /** Met à jour les couts des arretes. */

    public void updateEdgesCost(int nb_tas_pris){
        for(Edge edge: edges){
            edge.updateCost(nb_tas_pris);
        }
    }

    /** Retire du graphe les noeuds déjà exécuté */

    public void clean(){
        ArrayList<Node> lst = new ArrayList<>();
        for(Node node: nodes){
            log.debug(node.isDone());
            if(!node.isDone()){
                lst.add(node);
            }
        }
        setNodes(lst);
        setEdges(createEdge());
    }

    /** Affiche le contenu du graphe. */

    public void display(){
        System.out.println("Liste des noeuds");
        for(Node node : nodes){
            System.out.println(node);
        }
        System.out.println("Liste des arretes");
        for(Edge edge : edges){
            System.out.println(edge);
        }
    }

    public ArrayList<Node> getNodes() {
        return nodes;
    }

    public PriorityQueue<Edge> getEdges() {
        return edges;
    }

    public void setNodes(ArrayList<Node> nodes){ this.nodes=nodes;}

    public void setEdges(PriorityQueue<Edge> edges){ this.edges=edges;}
}
