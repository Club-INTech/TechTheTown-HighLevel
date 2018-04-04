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

    /**
     * Constructeur
     *
     */
    public Arete(Noeud noeud1, Noeud noeud2) {
        this.noeud1 = noeud1;
        this.noeud2 = noeud2;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Arete) {
            if (this.noeud1.equals(((Arete) object).noeud1)) {
                if (this.noeud2.equals(((Arete) object).noeud2)) {
                    return true;
                } else {
                    return false;
                }
            }

        }
        return false;


    }


}
