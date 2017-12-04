package pathfinder;

import container.Service;
import pfg.config.Config;
import smartMath.Vec2;
import table.Table;
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
     * Methode renvoyant une liste de vecteurs qui contient le chemain le plus rapide
     * entre les deux positions entrées.
     *
     * @param positiondepart
     * @param positionarrive
     * @return
     */

    public ArrayList<Vec2> findmyway(Vec2 positiondepart, Vec2 positionarrive){
        PriorityQueue<Noeud> openList = new PriorityQueue<Noeud>(new BetterNode());
        Noeud noeuddepart = new Noeud(positiondepart, 0, 0, new ArrayList<Noeud>());
        Noeud noeudarrive = new Noeud(positionarrive, 0, 0, new ArrayList<Noeud>());
        Noeud noeudcourant;
        ArrayList<Noeud> nodes = graphe.createNodes();
        ArrayList<Noeud> closeList = new ArrayList<Noeud>();
        ArrayList<Noeud> noeudvoisin = new ArrayList<Noeud>();
        ArrayList<Vec2> finalPath = new ArrayList<Vec2>();
        nodes.add(0, noeuddepart);
        nodes.add(noeudarrive);
        int betternode = 0;
        boolean better = false;
        graphe.createAretes(nodes);
        openList.add(noeuddepart);

        while(!nodeInList(closeList,noeudarrive) && openList.size() != 0){


            if(better){
                closeList.set(closeList.size()-1, noeudvoisin.get(betternode));
                better=false;
                noeudcourant=closeList.get(closeList.size()-1);
            }
            else{
                noeudcourant = openList.poll();
                closeList.add(noeudcourant);
            }
            noeudvoisin =noeudcourant.getVoisins();

            int i = 0;

            while ( i < noeudvoisin.size() && better==false) {

                if (nodeInList(closeList, noeudvoisin.get(i))) {

                } else if (noeudvoisin.get(i).getCout() > noeudcourant.getCout() + (noeudvoisin.get(i).getPosition().distance(noeudcourant.getPosition()))) {
                    if(better && noeudvoisin.get(i).getHeuristique() + noeudvoisin.get(i).getCout() < noeudvoisin.get(i).getHeuristique() + noeudvoisin.get(betternode).getCout()){
                        betternode=i;
                    }
                    else{
                        better = true;
                        betternode=i;
                    }
                }
                else {
                    noeudvoisin.get(i).setHeuristique(noeudvoisin.get(i).getPosition().distance(noeudarrive.getPosition()));
                    noeudvoisin.get(i).setCout(noeudcourant.getCout() + (noeudvoisin.get(i).getPosition().distance(noeudcourant.getPosition())));
                    openList.add(noeudvoisin.get(i));
                }


                i++;
            }
        }
        // fabrique le chemain à partir de la closeList
        for(int i=0; i<closeList.size();i++) {
            finalPath.add(closeList.get(i).getPosition());
        }
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
            if (lst.get(i)==node){
                return true;
            }
        }
        return false;
    }

    public boolean nodeInQueue(PriorityQueue<Noeud> lst, Noeud node){
        for(int i = 0; i < lst.size(); i++ ){
            if (lst.element()==node){
                return true;
            }
        }
        return false;
    }

    //

    /**
     * Methode qui renvoit la distance parcourue pour arriver à un noeud
     *
     * @param noeud
     * @param lst
     * @return
     */

    public double distance(Noeud noeud, ArrayList<Noeud> lst){
        double distance = 0;
        lst.add(noeud);
        for(int i=0; i<lst.size() - 1;i++){
            distance += lst.get(i).getPosition().distance(lst.get(i+1).getPosition());
        }

        return distance;
    }

    /**
     * Méthode fournissant la liste des voisins d'un noeud
     * @param noeud
     * @param lst
     * @return
     */
    public ArrayList<Noeud> noeudVoisin(Noeud noeud, ArrayList<Arete> lst){
        ArrayList<Noeud> nodes = new ArrayList<Noeud>();
        for(int i = 0; i<lst.size() - 1; i++){
            nodes.add(lst.get(i).noeud2);
        }
        return nodes;
    }

}
