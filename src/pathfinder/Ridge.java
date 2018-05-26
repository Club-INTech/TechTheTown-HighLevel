package pathfinder;

/**
 * Classe implémentant une arrête du graphe : c'est une simple structure de données qui stocke coût
 * et disponibilité de l'arrête
 *
 * @author rem
 */
public class Ridge {

    /** Coût de l'arrête */
    private final int cost;

    /** Disponibilité : true si franchissable */
    private boolean reachable;

    /** Constructeur */
    public Ridge(int cost) {
        this.cost = cost;
        this.reachable = true;
    }

    /** Getters & Setters */
    public int getCost() {
        return cost;
    }
    public boolean isReachable() {
        return reachable;
    }
    public void setReachable(boolean reachable) {
        this.reachable = reachable;
    }
}
