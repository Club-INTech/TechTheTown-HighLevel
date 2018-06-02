package pathfinder;

import smartMath.Vec2;
import java.util.Comparator;

public class BetterNode implements Comparator<Node> {

    /** Le position de visée servant à évaluer les noeuds */
    private static Vec2 aim;

    /**
     * Methode comparant la qualité de deux noeuds.
     * Appelé automatiquement par une PriorityQueue.
     */
    @Override
    public int compare(Node node1, Node node2){
        if(computeHeuristic(node1) > computeHeuristic(node2)){
            return 1;
        }
        else if(computeHeuristic(node1) < computeHeuristic(node2)){
            return -1;
        }
        else{
            return 0;
        }
    }

    /** Calcul l'heuristique si elle n'a pas déjà été calculée avant */
    private int computeHeuristic(Node node) {
        if (node.getHeuristique() == Node.getDefaultHeuristic() && !node.getPosition().equals(aim)) {
            node.setHeuristique(node.getPosition().intDistance(aim));
        }
        return node.getHeuristique();
    }

    /** Setter */
    public static void setAim(Vec2 aim) {
        BetterNode.aim = aim;
    }
}
