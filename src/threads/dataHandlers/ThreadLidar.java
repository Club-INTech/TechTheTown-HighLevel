package threads.dataHandlers;

import pathfinder.Graphe;
import threads.AbstractThread;

/**
 * Thread qui récupère les informations du Lidar et les traite en mettant à jour le graphe
 *
 * @author rem
 */
public class ThreadLidar extends AbstractThread {

    /** Graphe à modifier en fonction des données recues par le Lidar */
    private Graphe graph;

    /** Constructeur */
    private ThreadLidar(Graphe graph) {
        this.graph = graph;
    }

    @Override
    public void run() {}

    /** Getters & Setters */
    public Graphe getGraph() {
        return graph;
    }
}
