package pathfinder;

import java.util.Comparator;

public class BetterNode implements Comparator<Noeud> {

    //methode comparant la qualité de deux noeuds

    @Override
    public int compare(Noeud noeud1, Noeud noeud2){

        if(noeud1.getHeuristique()+noeud1.getCout()<noeud2.getHeuristique()+noeud2.getCout()){
            return 1;
        }
        else if(noeud1.getHeuristique()+noeud1.getCout()>noeud2.getHeuristique()+noeud2.getCout()){
            return -1;
        }
        else{
            return 0;
        }
    }

}
