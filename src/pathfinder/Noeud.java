package pathfinder;

import smartMath.Vec2;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Classe représentant un noeud du graphe
 *
 * @author rem
 */
public class Noeud {

    /** Variables static pour l'initialistation des noeuds */
    public static final int DEFAULT_HEURISTIC      = 1000000;
    public static final int DEFAULT_COST           = -1;

    /** Position du noeud */
    private Vec2 position;

    /** Variables Pathfinding */
    private int heuristique;
    private int cout;

    /** Noeuds voisins & predecesseur */
    private HashMap<Noeud, Integer> voisins;
    private Noeud pred;

    /** Constructeur */
    public Noeud(Vec2 position) {
        this.position = position;
        this.heuristique = DEFAULT_HEURISTIC;
        this.cout = DEFAULT_COST;
        this.voisins = new HashMap<Noeud, Integer>();
    }

    /**
     * Ajoute un voisin au noeud, le cout de l'arete est calculée
     *
     * @param voisin le noeud voisin à ajouter
     */
    public void addVoisin(Noeud voisin){
        voisins.put(voisin, this.position.intDistance(voisin.position));
    }

    /**
     * Test utiliser pour trouver si un noeud est plus interessant qu'un autre
     */
    @Override
    public boolean equals(Object object){
        if(object instanceof Noeud){
            if(this.position.equals(((Noeud) object).position) ){
                return true;
            }
        }
        return false;
    }

    /**
     * On a choisit de stocker les voisins et leur distances sous forme de Hashmap
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + position.hashCode();
        return result;
    }

    @Override
    public String toString(){
        String toReturn = "Node ("+this.position.getX()+","+this.position.getY()+")";
        return toReturn;
    }

    /** Getters & Setters */
    public int getHeuristique() {
        return heuristique;
    }

    public void setHeuristique(int heuristique) {
        this.heuristique = heuristique;
    }

    public int getCout() {
        return cout;
    }

    public void setCout(int cout) {
        this.cout = cout;
    }

    public Noeud getPred() {
        return pred;
    }

    public void setPred(Noeud pred) {
        this.pred = pred;
    }

    public Vec2 getPosition() {
        return position;
    }

    public HashMap<Noeud, Integer> getVoisins() {
        return voisins;
    }
}
