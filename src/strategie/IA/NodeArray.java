package strategie.IA;

import enums.ScriptNames;

import java.util.ArrayList;

public class NodeArray {

    private ArrayList<Node> nodes;

    NodeArray(){
        this.nodes=new ArrayList<>();
    }



    public void add(Node node){
        this.nodes.add(node);
    }

    public void remove(Node node){
        this.nodes.remove(node);
    }

    public Node get(int index){
        return this.nodes.get(index);
    }

    public int size(){
        return this.nodes.size();
    }

    public ArrayList<Node> getArrayList(){
        return this.nodes;
    }

    public void setNodes(ArrayList<Node> array){
        this.nodes=array;
    }

    public Node getNodeByNameAndVersion(ScriptNames name, int version){
        for (Node node : nodes){
            if (node.getVersionToExecute()==version) {
                if (node.getName()==name) {
                    return node;
                }
            }
        }
        return null;
    }
}
