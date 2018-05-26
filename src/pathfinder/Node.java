package pathfinder;

import smartMath.Vec2;

import java.util.HashMap;
import java.util.Hashtable;

/**
 * Classe représentant un noeud du graphe
 *
 * @author rem
 */
public class Node {

    /** Variables static pour l'initialistation des noeuds */
    public static final int DEFAULT_HEURISTIC      = 1000000;
    public static final int DEFAULT_COST           = -1;

    /** Position du noeud */
    private Vec2 position;

    /** Variables Pathfinding */
    private int heuristique;
    private int cout;

    /** Noeuds voisins & predecesseur */
    private Hashtable<Node, Ridge> neighbours;
    private Node pred;

    /** Constructeur */
    public Node(Vec2 position) {
        this.position = position;
        this.heuristique = DEFAULT_HEURISTIC;
        this.cout = DEFAULT_COST;
        this.neighbours = new Hashtable<Node, Ridge>();
    }

    /**
     * Ajoute un voisin au noeud, le cout de l'arete est calculée
     *
     * @param neighbour le noeud voisin à ajouter
     * @param ridge l'arrête qui les relien
     */
    public void addNeighbour(Node neighbour, Ridge ridge){
        neighbours.put(neighbour, ridge);
    }

    /**
     * Test utiliser pour trouver si un noeud est plus interessant qu'un autre
     */
    @Override
    public boolean equals(Object object){
        if(object instanceof Node){
            if(this.position.equals(((Node) object).position) ){
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
    public Node getPred() {
        return pred;
    }
    public void setPred(Node pred) {
        this.pred = pred;
    }
    public Vec2 getPosition() {
        return position;
    }
    public Hashtable<Node, Ridge> getNeighbours() {
        return neighbours;
    }
}
