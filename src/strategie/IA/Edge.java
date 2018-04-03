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
        updateCost(0);
    }

    /** Met à jour le cout d'une arrete. On peut ainsi modifier le cout pendant le calcule du parcours optimal
     * du graphe de décision. Peut être utile pour choisir de déposer les cubes lorsque qu'on a ramassé deux tas.
     */

    public void updateCost(int nb_tas_pris){
        //        this.cost = node1.getScore()+node2.getScore();
        if(node1.toString().equals("DeposeCube") || node2.toString().equals("DeposeCube")){
            if(nb_tas_pris == 2){
                setCost(666);
            }
            else {
                setCost(0);
            }
        }
        else{
            setCost(node1.getPosition().distance(node2.getPosition()));
        }
    }

    @Override
    public String toString() {
        return node1.toString()+"-"+node2.toString()+" cost :"+cost;
    }

    public Node getNode1() {return node1;}

    public Node getNode2() {return node2;}

    public double getCost() {return cost;}

    public void setCost(double cost) {this.cost = cost;}
}
