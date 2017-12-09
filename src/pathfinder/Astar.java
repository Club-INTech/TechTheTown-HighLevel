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

    public Astar(Log log, Config config, Graphe graphe) {
        this.log = log;
        this.config = config;
        Table table = new Table(log, config);
        graphe = new Graphe(table);
        this.graphe = graphe;
    }

    /**
     *
     * Methode basée sur l'algorithme A* renvoyant une liste de vecteurs qui contient le chemain le plus rapide
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
        ArrayList<Noeud> nodes = graphe.createNodes();
        ArrayList<Noeud> closeList = new ArrayList<Noeud>();
        ArrayList<Noeud> noeudvoisin = new ArrayList<Noeud>();
        ArrayList<Vec2> finalPath = new ArrayList<Vec2>();
        ArrayList<Noeud> finalList = new ArrayList<>();
        nodes.add(0, noeuddepart);
        nodes.add(noeudarrive);
        ObstacleManager obstacleManager =  new ObstacleManager(log,config);


        if(        obstacleManager.isObstructed(noeuddepart.getPosition())
                || ! obstacleManager.isRobotInTable(noeuddepart.getPosition())
                || obstacleManager.isObstructed(noeudarrive.getPosition())
                || ! obstacleManager.isRobotInTable(noeudarrive.getPosition())){

            throw new NoPathFound(true,false);
        }
        else {
            ArrayList aretes = graphe.createAretes(nodes);
            openList.add(noeuddepart);

            while (!nodeInList(closeList, noeudarrive) && openList.size() != 0) {

                noeudcourant = openList.poll();
                closeList.add(noeudcourant);
                noeudvoisin = noeudcourant.getVoisins();

                int i = 0;


                while (i < noeudvoisin.size()) {

                    if (nodeInList(closeList, noeudvoisin.get(i))) {

                    } else if (nodeInQueue(openList, noeudvoisin.get(i))) {
                        if (noeudvoisin.get(i).getCout() < noeudcourant.getCout() + (noeudvoisin.get(i).getPosition().distance(noeudcourant.getPosition()))) {
                            noeudvoisin.get(i).setPred(noeudvoisin.get(i).getPred());
                        }
                        else {
                            noeudvoisin.get(i).setCout(noeudcourant.getCout() + (noeudvoisin.get(i).getPosition().distance(noeudcourant.getPosition())));
                        }
                    } else {
                        noeudvoisin.get(i).setHeuristique(noeudvoisin.get(i).getPosition().distance(noeudarrive.getPosition()));
                        noeudvoisin.get(i).setCout(noeudcourant.getCout() + (noeudvoisin.get(i).getPosition().distance(noeudcourant.getPosition())));
                        openList.add(noeudvoisin.get(i));
                        noeudvoisin.get(i).setPred(noeudcourant);
                    }
                    i++;
                }
            }
            // pas de chemain trouvé.
            if(!nodeInList(closeList, noeudarrive) && openList.size() == 0){
                System.out.println("No way found");
                throw new NoPathFound(false,true);
            }

            // fabrique le chemain à partir de la closeList
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
    }

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


    /**
     *
     * Méthode qui teste la présence d'un noeud dans une liste
     *
     * @param lst
     * @param node
     * @return
     */

    public boolean nodeInList( ArrayList<Noeud> lst, Noeud node){
        for(int i = 0; i < lst.size(); i++ ){
            if (lst.get(i).equals(node)){
                return true;
            }
        }
        return false;
    }

    /**
     *
     * Méthode qui teste la présence d'un noeud dans une PriorityQueue
     *
     * @param lst
     * @param node
     * @return
     */

    public boolean nodeInQueue(PriorityQueue<Noeud> lst, Noeud node){
        for(int i = 0; i < lst.size(); i++ ){
            if (lst.element().equals(node)){
                return true;
            }
        }
        return false;
    }


}
