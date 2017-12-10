package pathfinder;

import container.Service;
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

public class Astar implements Service {

    @Override
    public void updateConfig() {

    }

    private Log log;
    private Config config;
    private Graphe graphe;
    private Table table;
    private ObstacleManager obstacleManager;

    public Astar(Log log, Config config, Table table) {
        this.log = log;
        this.config = config;
        this.table = table;
        graphe = new Graphe(table);
        obstacleManager=table.getObstacleManager();
    }

    /** Méthode initialisant le graghe, à appeler au début du match.        */

    public void initGraphe(){
        graphe.createNodes();
        graphe.createAretes(graphe.getNodes());
    }


    /** Methode basée sur l'algorithme A* renvoyant une liste de vecteurs qui contient le chemain le plus rapide
     * entre les deux positions entrées.
     *
     * @param positiondepart
     * @param positionarrive
     * @return
     */

    public ArrayList<Vec2> findmyway(Vec2 positiondepart, Vec2 positionarrive) throws NoPathFound{
        long time1=System.currentTimeMillis();
        PriorityQueue<Noeud> openList = new PriorityQueue<Noeud>(new BetterNode());
        Noeud noeuddepart = new Noeud(positiondepart, 0, 0, new ArrayList<Noeud>());
        Noeud noeudarrive = new Noeud(positionarrive, 0, 0, new ArrayList<Noeud>());
        Noeud noeudcourant;
        ArrayList<Noeud> nodes = graphe.getNodes();
        ArrayList<Noeud> closeList = new ArrayList<Noeud>();
        ArrayList<Vec2> finalPath = new ArrayList<Vec2>();
        ArrayList<Noeud> finalList = new ArrayList<>();
        ObstacleManager obstacleManager =  table.getObstacleManager();


        if(        obstacleManager.isObstructed(noeuddepart.getPosition())
                || ! obstacleManager.isRobotInTable(noeuddepart.getPosition())
                || obstacleManager.isObstructed(noeudarrive.getPosition())
                || ! obstacleManager.isRobotInTable(noeudarrive.getPosition())){

            throw new NoPathFound(true,false);
        }
        else {
            graphe.addNodeInGraphe(noeudarrive);
            graphe.addNodeInGraphe(noeuddepart);
            openList.add(noeuddepart);

            while (! closeList.contains(noeudarrive) && !openList.isEmpty()) {

                noeudcourant = openList.poll();
                closeList.add(noeudcourant);

                for (Noeud voisin: noeudcourant.getVoisins()) {

                    if (closeList.contains(voisin)) {
                        if (voisin.getCout() > noeudcourant.getCout() + (voisin.getPosition().distance(noeudcourant.getPosition()))) {
                            closeList.remove(voisin);
                            openList.add(voisin);
                            voisin.setPred(noeudcourant);
                            voisin.setCout(noeudcourant.getCout() + (voisin.getPosition().distance(noeudcourant.getPosition())));
                        }
                      }

                    else if(openList.contains(voisin)) {
                        if (voisin.getCout() > noeudcourant.getCout() + (voisin.getPosition().distance(noeudcourant.getPosition()))) {
                            voisin.setPred(voisin.getPred());
                            voisin.setCout(noeudcourant.getCout() + (voisin.getPosition().distance(noeudcourant.getPosition())));
                        }
                    }
                    else {
                        voisin.setHeuristique(voisin.getPosition().distance(noeudarrive.getPosition()));
                        voisin.setCout(noeudcourant.getCout() + (voisin.getPosition().distance(noeudcourant.getPosition())));
                        openList.add(voisin);
                        voisin.setPred(noeudcourant);
                    }
                }

            }
        }
        // pas de chemain trouvé.
        if(! closeList.contains(noeudarrive) && openList.isEmpty()){
            System.out.println("No way found");
            throw new NoPathFound(false,true);
        }

        // fabrique le chemain en partant du noeud d'arrivé
        finalList.add(noeudarrive);
        while (noeuddepart != finalList.get(finalList.size() - 1) ) {
            finalList.add(finalList.get(finalList.size() - 1).getPred());
        }
        for (int i = 0; i < finalList.size(); i++) {
            finalPath.add(finalList.get(i).getPosition());
        }
        long time2=System.currentTimeMillis()-time1;
        System.out.println("Time to execute (ms): "+time2);
        return finalPath;
    }



    /** Methode basée sur l'algorithme Dijkstra renvoyant une liste de vecteurs qui contient le chemain le plus rapide
     * entre les deux positions entrées.
     *
     * @param positiondepart
     * @param positionarrive
     * @return
     */
    public ArrayList<Vec2> findmywayD(Vec2 positiondepart, Vec2 positionarrive) throws NoPathFound  {
        long time1=System.currentTimeMillis();
        Noeud noeuddepart = new Noeud(positiondepart, 0, 0, new ArrayList<Noeud>());
        Noeud noeudarrive = new Noeud(positionarrive, 999999999, -1, new ArrayList<Noeud>());
        ArrayList<Noeud> nodes = graphe.createNodes();
        ArrayList<Noeud> noeudvoisin = new ArrayList<Noeud>();
        ArrayList<Vec2> finalPath = new ArrayList<Vec2>();
        ArrayList<Noeud> finalList = new ArrayList<>();
        nodes.add(0, noeuddepart);
        nodes.add(noeudarrive);
        ObstacleManager obstacleManager =  new ObstacleManager(log,config);
        ArrayList aretes = graphe.createAretes(nodes);

        Noeud noeudcourant;
        PriorityQueue<Noeud> openList = new PriorityQueue<Noeud>(new BetterNode());
        openList.addAll(nodes);

        while(! openList.isEmpty()){
            noeudcourant=openList.poll();
            for (Noeud voisin: noeudcourant.getVoisins()
                    ) {
                if(voisin.getCout()>noeudcourant.getCout()+voisin.getPosition().distance(noeudcourant.getPosition())) {
                    voisin.setHeuristique(0);
                    voisin.setCout(noeudcourant.getCout() + voisin.getPosition().distance(noeudcourant.getPosition()));
                    voisin.setPred(noeudcourant);
                }
            }
        }

        noeudcourant=noeudarrive;
        while (!noeudcourant.equals(noeuddepart)){
            finalList.add(noeudcourant);
            noeudcourant.getPred();
        }

        for (int i = 0; i < finalList.size(); i++) {
            finalPath.add(finalList.get(i).getPosition());
        }

        long time2=System.currentTimeMillis()-time1;
        System.out.println("Time to execute (ms): "+time2);
        return finalPath;
    }

}
