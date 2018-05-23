package pathfinder;

import java.util.Comparator;

public class BetterNode implements Comparator<Node> {

    /**
     *     Methode comparant la qualité de deux noeuds.
     *     Peut être appelée pour trier une PriorityQueue.
     */

    @Override
    public int compare(Node node1, Node node2){

        if(node1.getCout()+ node1.getHeuristique()> node2.getCout()+ node2.getHeuristique()){
            return 1;
        }
        else if(node1.getCout()+ node1.getHeuristique()< node2.getCout()+ node2.getHeuristique()){
            return -1;
        }
        else{
            return 0;
        }
    }

}
