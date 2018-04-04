package pathfinder;

import java.util.Comparator;

public class BetterNode implements Comparator<Noeud> {

    /**
     *     Methode comparant la qualité de deux noeuds.
     *     Peut être appelée pour trier une PriorityQueue.
     */

    @Override
    public int compare(Noeud noeud1, Noeud noeud2){

        if(noeud1.getCout()+noeud1.getHeuristique()>noeud2.getCout()+noeud2.getHeuristique()){
            return 1;
        }
        else if(noeud1.getCout()+noeud1.getHeuristique()<noeud2.getCout()+noeud2.getHeuristique()){
            return -1;
        }
        else{
            return 0;
        }
    }

}
