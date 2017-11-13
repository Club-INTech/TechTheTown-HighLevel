package pathfinder;

import smartMath.Vec2;

public class Arete {
    public Noeud noeud1;
    public Noeud noeud2;
    public float cout;

    public Arete(Noeud noeud1, Noeud noeud2, float cout){
        this.noeud1 = noeud1;
        this.noeud2 = noeud2;
        this.cout = cout;
    }
}
