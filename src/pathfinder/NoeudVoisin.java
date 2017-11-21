package pathfinder;

import java.util.ArrayList;

public class NoeudVoisin {

    public ArrayList<Noeud> NoeudVoisin(Noeud noeud, ArrayList<Arete> lst){
        ArrayList<Noeud> nodes = new ArrayList<Noeud>();
        for(int i = 0; i<lst.size();i++){
            nodes.add(lst.get(i).noeud2);
        }
        return nodes;
    }
}
