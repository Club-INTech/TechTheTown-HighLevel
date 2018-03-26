package strategie.IA;

import java.util.ArrayList;

public class Graph {
    private ArrayList<Node> nodes;
    private ArrayList<Edge> edges;

    public Graph(ArrayList<Node> nodes) {
        this.nodes = nodes;
        this.edges = new ArrayList<>();
    }

    public void createEdge(ArrayList<Node> nodes){
        int n = nodes.size();
        for (int i = 0; 0 < n ; i++){
            for(int j = i+1; j < n; j++){
                edges.add(new Edge(nodes.get(i),nodes.get(j)));
            }
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
