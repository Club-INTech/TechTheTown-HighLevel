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

    public Arete(Noeud noeud1, Noeud noeud2, double cout){
        this.noeud1 = noeud1;
        this.noeud2 = noeud2;
        this.cout = cout;
    }


}
