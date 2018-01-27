package strategie.IA;

import java.util.ArrayList;

public class Node {

    private Node previous;
    private ArrayList<Node> nextNodes;
    private Boolean condition;

    public Node(Node previous, ArrayList<Node> nextNodes, Boolean condition) {
        this.previous = previous;
        this.nextNodes = nextNodes;
        this.condition = condition;
    }


}
