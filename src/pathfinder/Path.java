package pathfinder;

import smartMath.Vec2;
import sun.misc.Queue;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Petite structure de données parce que j'ai rien trouvé de mieux... (mais c'est propre)
 *
 * @author rem
 */
public class Path {

    /** Le chemin en soit */
    private ConcurrentLinkedQueue<Vec2> path;

    /** Lock */
    public final String lock = "PathLock";

    /** Constructeur */
    public Path(ConcurrentLinkedQueue path) {
        this.path = path;
    }

    /** Getter & Setter*/
    public ConcurrentLinkedQueue<Vec2> getPath() {
        return path;
    }
    public void setPath(ConcurrentLinkedQueue<Vec2> path) {
        this.path = path;
    }
}
