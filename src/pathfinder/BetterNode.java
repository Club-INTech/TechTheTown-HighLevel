package pathfinder;

public class BetterNode {

    //methode comparant la qualité de deux noeuds

    public int BetterNode(Noeud noeud1, Noeud noeud2, Noeud noeudarrive){
        noeud1.heuristique = (int) noeud1.position.distance(noeudarrive.position);
        noeud2.heuristique = (int) noeud2.position.distance(noeudarrive.position);
        if(noeud1.heuristique>noeud2.heuristique){
            return 1;
        }
        else if(noeud1.heuristique<noeud2.heuristique){
            return -1;
        }
        else{
            return 0;
        }
    }

}
