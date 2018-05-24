package threads;

import exceptions.Locomotion.ImmobileEnnemyForOneSecondAtLeast;
import exceptions.Locomotion.UnableToMoveException;
import pfg.config.Config;
import robot.Locomotion;
import smartMath.Vec2;
import utils.Log;

import java.util.ArrayList;

/**
 * Thread créer à chaque appel au Pathfinding : il gère le calcul de trajectoire
 *
 * @author rem
 */
public class ThreadPathFollower extends AbstractThread {

    /** Locomotion */
    private Locomotion locomotion;

    /** Le chemin à suivre */
    private ArrayList<Vec2> path;

    /** Constructeur : on construit un ThreadPathFollower à chaque requête du robot */
    public ThreadPathFollower(ArrayList<Vec2> path, Locomotion locomotion) {
        this.locomotion = locomotion;
        this.path = path;
    }

    @Override
    public void interrupt() {

    }

    @Override
    public void run() {
        try {
            locomotion.followPath(path);
            // TODO : ajouter UnexpectedObstacleOnPathException, ainsi qu'un moyen de communiquer les exceptions au mainThread
        } catch (UnableToMoveException e) {

        } catch (ImmobileEnnemyForOneSecondAtLeast e){

        }
    }
}
