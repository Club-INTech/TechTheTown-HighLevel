package pathfinder;

import container.Service;
import enums.ConfigInfoRobot;
import exceptions.Locomotion.PointInObstacleException;
import exceptions.NoPathFound;
import pfg.config.Config;
import robot.Locomotion;
import smartMath.Vec2;
import table.Table;
import threads.ThreadInterface;
import threads.ThreadPathFollower;
import threads.dataHandlers.ThreadLidar;
import utils.Log;

import java.util.ArrayList;

/**
 * Pathfinding du robot ! Contient l'algorithme
 *
 * @author Yousra, Sam, rem
 */

public class Pathfinding implements Service {

    /** Log & config */
    private Log log;
    private Config config;

    /** Graphe & Table */
    private ThreadLidar grapheHandler;
    private Table table;

    /** Le pathfinding gère aussi la trajectoire via un autre Thread */
    private Locomotion locomotion;

    /** Vitesses du robot */
    private int robot_linear_speed;
    private double robot_angular_speed;

    /** Coût fixe ajouté à chaque noeud */
    private int coutFixe;

    /** Constructeur */
    public Pathfinding(Log log, Config config, Locomotion locomotion, ThreadLidar graphHandler) {
        this.log = log;
        this.config = config;
        this.locomotion = locomotion;
        this.grapheHandler = graphHandler;
        updateConfig();
    }

    /**
     * Methode surcouche du pathfinding permettant de gérer les obstacles mouvants (LPA*)
     *
     * @param aim le point à atteindre
     */
    public void moveTo(Vec2 aim) {
        /* Algo :
        initialisation du Graphe, c-à-d : ajout des noeuds de départ et d'arrivé
        Tant qu'on est pas arrivé :
            Calcul du plus court chemin
            Lancement du thread de suivit de trajectoire
            Attente de changements du graphe ou d'un evenement du gestionnaire de trajectoire, c-a-d obstacle sur la ligne droite suivit par le robot
            Mise à jour du graphe en cas d'evenement du gestionnaire de trajectoire, c-a-d retrait du noeud de départ initial et ajout d'un nouveau noeud de départ
         */
    }

    /**
     * Methode basée sur l'algorithme A* renvoyant une liste de vecteurs qui contient le chemin le plus rapide
     * entre les deux positions d'entrée.
     *
     * @param positionDepart
     * @param positionArrive
     * @return
     */
    public ArrayList<Vec2> findmyway(Vec2 positionDepart, Vec2 positionArrive) throws PointInObstacleException, NoPathFound {
        return null;
    }

    /** Permet de calculer le temps pour se rendre à une position. */
    public double howManyTime(Vec2 positionDepart, Vec2 positionArrive) throws PointInObstacleException, NoPathFound {
        ArrayList<Vec2> path = findmyway(positionDepart,positionArrive);
        double time = 0;
        for(int i = 0; i < path.size() - 1; i++){
            time += robot_angular_speed*path.get(i).minusNewVector(path.get(i+1)).angle();
            time += robot_linear_speed*path.get(i).minusNewVector(path.get(i+1)).length();
        }
        return time;
    }

    /** Getters & Setters */
    public Graphe getGraphe() {
        return grapheHandler.getGraph();
    }

    @Override
    public void updateConfig() {
        this.coutFixe = config.getInt(ConfigInfoRobot.COUT_FIXE);
        this.robot_linear_speed = config.getInt(ConfigInfoRobot.ROBOT_LINEAR_SPEED);
        this.robot_angular_speed = config.getDouble(ConfigInfoRobot.ROBOT_ANGULAR_SPEED);
    }
}
