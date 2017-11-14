package pathfinder;

import smartMath.Vec2;

import java.util.ArrayList;

public class Noeud {
    public Vec2 position;
    int heuristique;

    public Noeud(Vec2 position, int heuristique) {
        this.position = position;
        this.heuristique = heuristique;
    }

}
