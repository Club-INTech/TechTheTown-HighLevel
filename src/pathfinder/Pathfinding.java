package pathfinder;

import container.Service;
import enums.ConfigInfoRobot;
import exceptions.Locomotion.PointInObstacleException;
import exceptions.NoPathFound;
import pfg.config.Config;
import smartMath.Vec2;
import table.Table;
import table.obstacles.ObstacleCircular;
import table.obstacles.ObstacleManager;
import utils.Log;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import static enums.TasCubes.*;

/**
 * Pathfinding du robot ! Contient l'algorithme
 *
 * @author Yousra, Sam
 */

public class Pathfinding implements Service {

    private Log log;
    private Config config;
    private Graphe graphe;
    private Table table;
    private ObstacleManager obstacleManager;
    private CopyOnWriteArrayList<ObstacleCircular> circularObstacles;
    private boolean tasBaseRemoved;
    private boolean tasChateauRemoved;
    private boolean tasStationEpurationRemoved;
    private boolean tasBaseEnnemiRemoved;
    private boolean tasChateauEnnemiRemoved;
    private boolean tasStationEpurationEnnemiRemoved;




    private int robot_linear_speed;
    private double robot_angular_speed;

    /** Coût fixe ajouté à chaque noeud*/
    private int coutFixe;

    public Pathfinding(Log log, Config config, Table table, Graphe graphe) {
        this.log = log;
        this.config = config;
        updateConfig();
        this.table = table;
        obstacleManager = table.getObstacleManager();
        circularObstacles = (CopyOnWriteArrayList<ObstacleCircular>) obstacleManager.getmCircularObstacle().clone();
        this.graphe = graphe;
        this.tasBaseRemoved =false;
        this.tasChateauRemoved =false;
        this.tasStationEpurationRemoved =false;
        this.tasBaseEnnemiRemoved =false;
        this.tasChateauEnnemiRemoved =false;
        this.tasStationEpurationEnnemiRemoved =false;
//        initGraphe();
        log.debug("init PATHFINDING");
    }


    /**
     * Méthode initialisant le graghe, à appeler au début du match.
     */
    public void initGraphe() {
        graphe = new Graphe(log, config, table);
        graphe.updateConfig();
    }


    /**
     * Retire un tas de cubes du graphes, si le robot les a récupérés.
     */
    public void removeObstacle() {
        boolean grapheHasToBeRecreated=false;
        if (!this.tasBaseRemoved) {
            if (config.getBoolean(ConfigInfoRobot.TAS_BASE_PRIS)) {
                this.graphe.getListCircu().remove(circularObstacles.get(TAS_BASE.getID()));
                grapheHasToBeRecreated=true;
                this.tasBaseRemoved =true;
            }
        }
        if (!this.tasChateauRemoved) {
            if (config.getBoolean(ConfigInfoRobot.TAS_CHATEAU_PRIS)) {
                this.graphe.getListCircu().remove(circularObstacles.get(TAS_CHATEAU_EAU.getID()));
                grapheHasToBeRecreated=true;
                this.tasChateauRemoved =true;
            }
        }
        if (!this.tasStationEpurationRemoved) {
            if (config.getBoolean(ConfigInfoRobot.TAS_STATION_EPURATION_PRIS)) {
                this.graphe.getListCircu().remove(circularObstacles.get(TAS_STATION_EPURATION.getID()));
                grapheHasToBeRecreated=true;
                this.tasStationEpurationRemoved =true;
            }
        }
        if (!this.tasBaseEnnemiRemoved) {
            if (config.getBoolean(ConfigInfoRobot.TAS_BASE_ENNEMI_PRIS)) {
                this.graphe.getListCircu().remove(circularObstacles.get(TAS_BASE_ENNEMI.getID()));
                grapheHasToBeRecreated=true;
                this.tasBaseEnnemiRemoved =true;
            }
        }
        if (!this.tasChateauEnnemiRemoved) {
            if (config.getBoolean(ConfigInfoRobot.TAS_CHATEAU_ENNEMI_PRIS)) {
                this.graphe.getListCircu().remove(circularObstacles.get(TAS_CHATEAU_EAU_ENNEMI.getID()));
                grapheHasToBeRecreated=true;
                this.tasChateauEnnemiRemoved =true;
            }
        }
        if (!this.tasStationEpurationEnnemiRemoved) {
            if (config.getBoolean(ConfigInfoRobot.TAS_STATION_EPURATION_ENNEMI_PRIS)) {
                this.graphe.getListCircu().remove(circularObstacles.get(TAS_STATION_EPURATION_ENNEMI.getID()));
                grapheHasToBeRecreated=true;
                this.tasStationEpurationEnnemiRemoved =true;
            }
        }
    }

    /**
     * Methode basée sur l'algorithme A* renvoyant une liste de vecteurs qui contient le chemin le plus rapide
     * entre les deux positions d'entrée.
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

    public Graphe getGraphe() {
        return graphe;
    }

    @Override
    public void updateConfig() {
        this.coutFixe = config.getInt(ConfigInfoRobot.COUT_FIXE);
        this.robot_linear_speed = config.getInt(ConfigInfoRobot.ROBOT_LINEAR_SPEED);
        this.robot_angular_speed = config.getDouble(ConfigInfoRobot.ROBOT_ANGULAR_SPEED);

    }
}
