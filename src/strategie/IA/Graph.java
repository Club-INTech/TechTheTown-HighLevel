package strategie.IA;

import java.util.ArrayList;

public class Graph {
    private ArrayList<Node> nodes;
    private ArrayList<Edge> edges;

    public Graph(ArrayList<Node> nodes) {
        this.edges = new ArrayList<>();
        this.nodes = nodes;
        createEdge(nodes);
    }

    public void createEdge(ArrayList<Node> nodes){
        int n = nodes.size();
        for (int i = 0; i < n ; i++){
            for(int j = i+1; j < n ; j++){
                edges.add(new Edge(nodes.get(i),nodes.get(j)));
            }nodes.get(i).setId(i);

        }
    }

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
}
