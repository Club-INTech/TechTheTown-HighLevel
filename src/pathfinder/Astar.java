package pathfinder;

import container.Service;
import pfg.config.Config;
import smartMath.Vec2;
import table.Table;
import utils.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

public class Astar implements Service{

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
        ArrayList<Noeud> closeList;
        ArrayList<Vec2> finalPath = new ArrayList<Vec2>();
        ArrayList<Noeud> nodes = graphe.createNodes();

        HashMap<Noeud,ArrayList<Arete>> nodesbones = graphe.getNodesbones();

        Noeud noeuddepart = new Noeud(positiondepart, 0);
        Noeud noeudarrive = new Noeud(positionarrive, 0);
        Noeud noeudcourant = noeuddepart;

        finalPath.add(noeudcourant.getPosition());

        nodes.add(noeuddepart);
        nodes.add(noeudarrive);
        graphe.createAretes();

        ArrayList<Arete> areteliste = graphe.getNodesbones().get(noeuddepart);
        nodesbones.get(noeudarrive);
        for(int i = 0; i < nodesbones.get(noeudcourant).size(); i++){

    //        noeud1.setHeuristique( (int) noeud1.getPosition().distance(noeudarrive.getPosition()));
        }

        return finalPath;
    }


}
