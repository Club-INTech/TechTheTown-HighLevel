package threads;

import exceptions.Locomotion.UnableToMoveException;
import pathfinder.Path;
import pfg.config.Config;
import robot.Locomotion;
import smartMath.Vec2;
import utils.Log;

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
    private ConcurrentLinkedQueue<Object> eventQueue;

    /** Constructeur : on construit un ThreadPathFollower à chaque requête du robot */
    public ThreadPathFollower(Log log, Config config, Path path, ConcurrentLinkedQueue<Object> eventQueue, Locomotion locomotion) {
        super(config, log);
        this.locomotion = locomotion;
        this.path = path;
        this.eventQueue = eventQueue;
    }

    @Override
    public void run() {
        try {
            Vec2 aim;
            boolean hasNext;
            log.debug("Thread Pathfollower lancé, début de suivit de chemin : " + path.getPath());
            do {
                synchronized (path.lock) {
                    aim = path.getPath().peek();
                }
                locomotion.moveToPoint(aim, false, true);
                synchronized (path.lock) {
                    path.getPath().poll();
                }
                hasNext = !path.getPath().isEmpty();
            } while (hasNext);
            log.debug("Thread Pathfollower terminé");
            eventQueue.add(new Boolean(true));
        } catch (UnableToMoveException e) {
            eventQueue.add(e);
        }
    }
}
