package pathfinder;

public class BetterNode {

    //methode comparant la qualité de deux noeuds

    public int BetterNode(Noeud noeud1, Noeud noeud2, Noeud noeudarrive){
        noeud1.setHeuristique( (int) noeud1.getPosition().distance(noeudarrive.getPosition()));
        noeud2.setHeuristique( (int) noeud2.getPosition().distance(noeudarrive.getPosition()));
        if(noeud1.getHeuristique()>noeud2.getHeuristique()){
            return 1;
        }
        else if(noeud1.getHeuristique()<noeud2.getHeuristique()){
            return -1;
        }
        else{
            return 0;
        }
    }

}
