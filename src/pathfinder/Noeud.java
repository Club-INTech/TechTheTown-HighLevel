package pathfinder;

import smartMath.Vec2;

public class Noeud {
    public Vec2 position;
    int heuristique;

    public Noeud(Vec2 position, int heuristique) {
        this.position = position;
        this.heuristique = heuristique;
    }
    /*public boolean isMobile(){}*/
}
