package threads;

import exceptions.Locomotion.UnableToMoveException;
import pathfinder.Path;
import robot.Locomotion;
import smartMath.Vec2;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Thread créer à chaque appel au Pathfinding : il gère le calcul de trajectoire
 *
 * @author rem
 */
public class ThreadPathFollower extends AbstractThread {

    /** Locomotion */
    private Locomotion locomotion;

    /** Le chemin à suivre */
    private Path path;

    /** La File d'evement */
    private ConcurrentLinkedQueue<UnableToMoveException> eventQueue;

    /** Constructeur : on construit un ThreadPathFollower à chaque requête du robot */
    public ThreadPathFollower(Path path, ConcurrentLinkedQueue<UnableToMoveException> eventQueue, Locomotion locomotion) {
        this.locomotion = locomotion;
        this.path = path;
        this.eventQueue = eventQueue;
    }

    @Override
    public void run() {
        try {
            Vec2 aim;
            boolean hasNext;
            do {
                synchronized (path.lock) {
                    aim = path.getPath().poll();
                }
                locomotion.moveToPoint(aim, false, true);
                hasNext = !path.getPath().isEmpty();
            } while (hasNext);
        } catch (UnableToMoveException e) {
            eventQueue.add(e);
        }
    }
}
