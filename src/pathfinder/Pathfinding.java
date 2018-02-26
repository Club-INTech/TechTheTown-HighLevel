package pathfinder;

import container.Container;
import container.Service;
import enums.ConfigInfoRobot;
import enums.UnableToMoveReason;
import exceptions.Locomotion.PointInObstacleException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.NoPathFound;
import pfg.config.Config;
import smartMath.Vec2;
import table.Table;
import table.obstacles.Obstacle;
import table.obstacles.ObstacleCircular;
import table.obstacles.ObstacleManager;
import table.obstacles.ObstacleRectangular;
import utils.Log;

import java.util.ArrayList;
import java.util.PriorityQueue;

/**
 * Pathfinding du robot ! Contient l'algorithme
 *
 * @author Yousra, Sam
 */

public class Pathfinding implements Service {

    @Override
    public void updateConfig() {

    }

    private Log log;
    private Config config;
    private Graphe graphe;
    private Table table;
    private ObstacleManager obstacleManager;

    public Pathfinding(Log log, Config config, Table table) {
        this.log = log;
        this.config = config;
        this.table = table;
        obstacleManager = table.getObstacleManager();
        initGraphe();
        log.debug("init PATHFINDING");
    }

    /**
     * Méthode initialisant le graghe, à appeler au début du match.
     */

    public void initGraphe() {
        graphe = new Graphe(log,config,table);
        graphe.updateConfig();
    }

    /**
     * Méthode réinitialisant le graphe, à appeler après chaque utilisation de findmyway
     */

    public void reInitGraphe(Noeud noeudDepart, Noeud noeudArrive) {
        for (Noeud node : graphe.getNodes()) {
            node.setPred(null);
            node.setCout(-1);
            node.setHeuristique(999999999);
            node.removeNeighbour(noeudDepart);
            node.removeNeighbour(noeudArrive);
        }
        if(config.getBoolean(ConfigInfoRobot.TOURAVANTREMPLIE)){
            obstacleManager.removeObstacle(obstacleManager.getmCircularObstacle().get(0));
        }
        if(config.getBoolean(ConfigInfoRobot.TOURARRIEREMPLIE)){
            obstacleManager.removeObstacle(obstacleManager.getmCircularObstacle().get(1));
        }
        graphe.removeNode(noeudDepart);
        graphe.removeNode(noeudArrive);
    }

    /**
     * Methode basée sur l'algorithme A* renvoyant une liste de vecteurs qui contient le chemin le plus rapide
     * entre les deux positions d'entrée.
     *
     * @param positiondepart
     * @param positionarrive
     * @return
     */

    public ArrayList<Vec2> findmyway(Vec2 positiondepart, Vec2 positionarrive) throws PointInObstacleException, UnableToMoveException, NoPathFound {
        long time1 = System.currentTimeMillis();

        /** Dévclaration des variables */
        PriorityQueue<Noeud> openList = new PriorityQueue<Noeud>(new BetterNode());
        Noeud noeuddepart = new Noeud(positiondepart, 0, 0, new ArrayList<Noeud>());
        Noeud noeudarrive = new Noeud(positionarrive, 0, 0, new ArrayList<Noeud>());
        Noeud noeudcourant;
        ArrayList<Noeud> nodes = graphe.getNodes();
        ArrayList<Noeud> closeList = new ArrayList<Noeud>();
        ArrayList<Vec2> finalPath = new ArrayList<Vec2>();
        ArrayList<Noeud> finalList = new ArrayList<>();
        int heuristique = 100;
        int k = 0;
        /** exception départ ou arrivée dans un obstacle */
        if (obstacleManager.isObstructed(noeuddepart.getPosition()) || !obstacleManager.isRobotInTable(noeuddepart.getPosition())) {
            throw new PointInObstacleException(noeuddepart.getPosition());
        } else if (obstacleManager.isObstructed(noeudarrive.getPosition())
                || !obstacleManager.isRobotInTable(noeudarrive.getPosition())) {
            throw new PointInObstacleException(noeudarrive.getPosition());
        }
        //début de l'algorithme
        else {
            graphe.addNodeInGraphe(noeudarrive);
            graphe.addNodeInGraphe(noeuddepart);

            if (noeuddepart.getVoisins().contains(noeudarrive)) {
                finalPath.add(positiondepart);
                finalPath.add(positionarrive);
                long time2 = System.currentTimeMillis() - time1;
                log.debug("Time to execute (ms): " + time2);
                reInitGraphe(noeuddepart, noeudarrive);
                return finalPath;
            }

            openList.add(noeuddepart);

            while (!closeList.contains(noeudarrive) && !openList.isEmpty()) {

                noeudcourant = openList.poll();
                closeList.add(noeudcourant);

                for (Noeud voisin : noeudcourant.getVoisins()) {

                    if (closeList.contains(voisin)) {
                        if (voisin.getCout() > noeudcourant.getCout() + (voisin.getPosition().distance(noeudcourant.getPosition()))) {
                            closeList.remove(voisin);
                            openList.add(voisin);
                            voisin.setPred(noeudcourant);
                            voisin.setCout(noeudcourant.getCout() + (voisin.getPosition().distance(noeudcourant.getPosition())));
                        }
                    } else if (openList.contains(voisin)) {
                        if (voisin.getCout() > noeudcourant.getCout() + (voisin.getPosition().distance(noeudcourant.getPosition()))) {
                            voisin.setPred(voisin.getPred());
                            voisin.setCout(noeudcourant.getCout() + (voisin.getPosition().distance(noeudcourant.getPosition())));
                        }
                    } else {
//                        voisin.setHeuristique(voisin.getPosition().distance(noeudarrive.getPosition()));
                        voisin.setHeuristique(k*heuristique);
                        voisin.setCout(noeudcourant.getCout() + (voisin.getPosition().distance(noeudcourant.getPosition())));
                        openList.add(voisin);
                        voisin.setPred(noeudcourant);
                    }
                }
                k++;
                log.debug(k);
            }
        }
        // pas de chemin trouvé.
        if (!closeList.contains(noeudarrive) && openList.isEmpty()) {
            log.debug("No way found");
            throw new NoPathFound(false, true);
        }
        // fabrique le chemain en partant du noeud d'arrivé
        finalList.add(noeudarrive);
        if (noeudarrive.getPred() == null) {
            log.debug("prednull");
        }
        while (noeuddepart != finalList.get(finalList.size() - 1)) {
            finalList.add(finalList.get(finalList.size() - 1).getPred());
        }
        for (int i = 1; i <= finalList.size(); i++) {
            finalPath.add(finalList.get(finalList.size() - i).getPosition());
        }

        long time2 = System.currentTimeMillis() - time1;
        log.debug("Time to execute (ms): " + time2);

        reInitGraphe(noeuddepart, noeudarrive);

        return finalPath;
    }

    public Graphe getGraphe() {
        return graphe;
    }
}
