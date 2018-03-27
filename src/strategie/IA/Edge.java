package strategie.IA;

public class Edge {

    private Node node1;
    private Node node2;
    private int cost;

    public Edge(Node node1, Node node2) {
        this.node1 = node1;
        this.node2 = node2;
        this.cost = node1.getScore()+node2.getScore();
    }

    @Override
    public String toString() {
        return node1.toString()+"-"+node2.toString()+" cost :"+cost;
    }

    public Node getNode1() {        return node1;    }

    public Node getNode2() {        return node2;    }

    public int getCost() {        return cost;       }
}
