package pathfinder;

import smartMath.Vec2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

public class Astar {

    private Graphe graphe;


    // methode pour trouver le chemain le plus rapide entre deux points
    public ArrayList<Vec2> Astar (Vec2 positiondepart, Vec2 positionarrive){
        //ArrayList<Noeud> openList;
     /  PriorityQueue<Noeud> openList = new PriorityQueue<Noeud>(BetterNode());
        ArrayList<Noeud> closeList;
        ArrayList<Vec2> finalPath = new ArrayList<Vec2>();
        ArrayList<Noeud> nodes = graphe.createNodes();
        HashMap<Noeud,ArrayList<Arete>> nodesbones = graphe.nodesbones;
        Noeud noeuddepart = new Noeud(positiondepart, 0);
        Noeud noeudarrive = new Noeud(positionarrive, 0);
        Noeud noeudcourant = noeuddepart;
        finalPath.add(noeudcourant.position);
        nodes.add(noeuddepart);
        nodes.add(noeudarrive);
        graphe=new Graphe();
        graphe.createAretes();
        ArrayList<Arete> areteliste = graphe.nodesbones.get(noeuddepart);
        nodesbones.get(noeudarrive);
        for(int i = 0; i < nodesbones.get(noeudcourant).size(); i++){

        }

        return finalPath;
    }
}
