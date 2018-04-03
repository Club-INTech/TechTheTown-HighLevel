package strategie.IA;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

public class Graph {

    private ArrayList<Node> nodes;
    private PriorityQueue<Edge> edges;

    /** Graphe de décision qui gère les actions à effectuer durant un match. */

    public Graph(ArrayList<Node> nodes) {
        this.edges = new PriorityQueue<>(new Comparator<Edge>() {
            @Override
            public int compare(Edge edge, Edge t1) {
//                if (edge.getCost()>t1.getCost()){
//                    return 1;
//                }
//                else if (edge.getCost()<t1.getCost()){
//                    return -1;
                if (edge.getCost()<t1.getCost()){
                    return 1;
                }
                else if (edge.getCost()>t1.getCost()){
                    return -1;
                } else {
                    return 0;
                }
            }
        });
        this.nodes = nodes;
        createEdge(nodes);
    }

    /** Génère les arretes du graphe, est appelé par le constructeur de Graph. */

    public void createEdge(ArrayList<Node> nodes){
        int n = nodes.size();
        for (int i = 0; i < n ; i++){
            for(int j = i+1; j < n ; j++){
                edges.add(new Edge(nodes.get(i),nodes.get(j)));
            }
            nodes.get(i).setId(i);
        }
    }

    /** Met à jour les couts des arretes. */

    public void updateEdgesCost(){
        for(Edge edge: edges){
            edge.updateCost();
        }
    }

    /** Affichre le contenu du graphe. */

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
}
