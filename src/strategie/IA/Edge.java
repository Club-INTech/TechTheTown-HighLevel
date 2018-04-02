package strategie.IA;

import java.util.ArrayList;

public class Edge {

    private Node node1;
    private Node node2;
    private double cost;

    /** Arrete reliant deux noeuds d'action, constituant du graphe de décision.
     *  Possède un coup à exprimer en points/s
     * */

    public Edge(Node node1, Node node2) {
        this.node1 = node1;
        this.node2 = node2;
//        this.cost = node1.getScore()+node2.getScore();
        this.cost = node1.getPosition().distance(node2.getPosition());
    }

    @Override
    public String toString() {
        return node1.toString()+"-"+node2.toString()+" cost :"+cost;
    }

    public Node getNode1() {return node1;}

    public Node getNode2() {return node2;}

    public double getCost() {return cost;}
}
