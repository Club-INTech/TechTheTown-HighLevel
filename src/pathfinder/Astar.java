package pathfinder;

import container.Service;
import pfg.config.Config;
import smartMath.Vec2;
import table.Table;
import utils.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

public class Astar implements Service {

    @Override
    public void updateConfig() {

    }

    Log log;
    Config config;
    Graphe graphe;

    public Astar(Log log, Config config, Graphe graphe) {
        this.log = log;
        this.config = config;
        Table table = new Table(log, config);
        graphe = new Graphe(table);
        this.graphe = graphe;
    }


    public ArrayList<Vec2> findmyway (Vec2 positiondepart, Vec2 positionarrive){
        PriorityQueue<Noeud> openList = new PriorityQueue<Noeud>(new BetterNode());
        Noeud noeuddepart = new Noeud(positiondepart, 0);
        Noeud noeudarrive = new Noeud(positionarrive, 0);
        Noeud noeudcourant;
        ArrayList<Noeud> nodes = graphe.createNodes();
        ArrayList<Noeud> closeList = new ArrayList<Noeud>();
        ArrayList<Noeud> noeudvoisin = new ArrayList<Noeud>();
        ArrayList<Vec2> finalPath = new ArrayList<Vec2>();
        nodes.add(noeuddepart);
        nodes.add(noeudarrive);
        graphe.createAretes();
        HashMap<Noeud,ArrayList<Arete>> nodesbones = graphe.getNodesbones();
        int p = 0;  //dernier élément ajouté à closelist
        int betternode = 0;
        boolean better = false;


        //FinalPath.add(noeudcourant.getPosition());
        //noeud1.setHeuristique( (int) noeud1.getPosition().distance(noeudarrive.getPosition()));
        //ArrayList<Arete> areteliste = graphe.getNodesbones().get(noeuddepart);
        //nodesbones.get(noeudarrive);

        closeList.add(noeuddepart);

        while(!NodeInList(closeList,noeudarrive)){
            noeudcourant = closeList.get(p);
           //noeudvoisin = NoeudVoisin(noeudcourant, nodesbones.get(noeudcourant));
            for (int i = 0; i < noeudvoisin.size(); i++) {

                if(NodeInList(closeList, noeudvoisin.get(i))){

                }

                else if(!NodeInQueue(openList, noeudvoisin.get(i))){
                    noeudvoisin.get(i).setHeuristique( (int) noeudvoisin.get(i).getPosition().distance(noeudarrive.getPosition()));
                    openList.add(noeudvoisin.get(i));
                }

                else{
                    if (distance(noeudcourant, closeList) > distance(noeudvoisin.get(i), closeList));
                        better = true;
                        betternode=i;
                }
            }
            if(better){
                closeList.set(betternode, noeudvoisin.get(betternode));
            }
            else {
                closeList.add(openList.remove());

            }
            better = false;
            p++;

        }

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

    public boolean NodeInList(ArrayList<Noeud> lst, Noeud node){
        for(int i = 0; i < lst.size(); i++ ){
            if (lst.get(i)==node){
                return true;
            }
        }
        return false;
    }

    public boolean NodeInQueue(PriorityQueue<Noeud> lst, Noeud node){
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
        for(int i=0; i<lst.size();i++){
            distance += lst.get(i).getPosition().distance(lst.get(i+1).getPosition());
        }

        return distance;
    }


}
