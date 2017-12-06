package pathfinder;

import smartMath.Geometry;
import smartMath.Segment;
import smartMath.Vec2;
import tests.container.A;

import java.util.ArrayList;
import java.util.HashMap;

public class Arete {
    public Noeud noeud1;
    public Noeud noeud2;
    public double cout;

    public Arete(Noeud noeud1, Noeud noeud2, double cout) {
        this.noeud1 = noeud1;
        this.noeud2 = noeud2;
        this.cout = cout;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Arete) {
            if (this.noeud1.equals(((Arete) object).noeud1)) {
                if (this.noeud2.equals(((Arete) object).noeud2)) {
                    if (this.cout == ((Arete) object).cout) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            }

        }
        return false;


    }
    public boolean traceArete(Noeud noeud1, Noeud noeud2,ArrayList<Arete> listAretes ){
        int n=listAretes.size();
        for(int i=0;i<n;i++){
            if(listAretes.get(i).noeud1.equals(noeud1) &&listAretes.get(i).noeud2.equals(noeud2) ){
                return true;
            }
        }
        return false;
    }

}
