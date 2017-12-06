/*
 * Copyright (c) 2016, INTech.
 *
 * This file is part of INTech's HighLevel.
 *
 *  INTech's HighLevel is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  INTech's HighLevel is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with it.  If not, see <http://www.gnu.org/licenses/>.
 */

package pathfinder;

import container.Service;
import pfg.config.Config;
import smartMath.Geometry;
import smartMath.Segment;
import smartMath.Vec2;
import sun.font.TrueTypeFont;
import sun.security.util.Length;
import sun.nio.cs.ArrayEncoder;
import table.Table;
import table.obstacles.ObstacleCircular;
import table.obstacles.ObstacleManager;
import utils.Log;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Pathfinding du robot ! Contient l'algorithme
 * @author Yousra, Sam
 */
public class Pathfinding implements Service {
    Log logn;
    Config config;
    private Graphe graphe;
    Table table=new Table(logn,config);

    private Pathfinding(Log logn, Config config) {
        this.logn = logn;
        this.config = config;

    }

    @Override
    public void updateConfig() {
    }

    /**
     * Cette méthode retourne le noeud le plus proche à un noeud en prenant l'arete avec
     * le moindre cout
     */
    public Noeud closestNode(Noeud node) {
        ArrayList<Noeud> voisins=node.getVoisins();
        int n = voisins.size();
        double cost = Segment.squaredLength(node.getPosition(), voisins.get(0).getPosition());
        cost=Math.sqrt(cost);
        Arete arete=new Arete(node,voisins.get(0),cost);
        double min = arete.cout;
        int indicemin = 0;
        ArrayList<Arete> areteListVoi
        for (int i = 0; i < n; i++) {
            if(voisins.get(i).)
        }
        return areteslist.get(indicemin).noeud2;
    }
    public ArrayList<Vec2> findmeaway(Vec2 positionDepart, Vec2 positionArrivee){
        graphe = new Graphe(table);
        Noeud noeudepart = new Noeud(positionDepart, 0,0,new ArrayList<>());
        Noeud noeudarrivee = new Noeud(positionArrivee, 0,0,new ArrayList<>());
        ArrayList<Noeud> nodes = graphe.getNodes();
        nodes.add(0,noeudepart);
        nodes.add(noeudarrivee);
        graphe.createAretes(nodes);
        ArrayList<Noeud> openlist=new ArrayList<>();
        ArrayList<Noeud> nodesTofollow=new ArrayList<>();
        nodesTofollow.add(noeudepart);
        ArrayList<Vec2> pathTofollow=new ArrayList<>();
        int n=pathTofollow.size();
        for(int i=0;i<n;i++){
          nodesTofollow.add(closestNode(nodesTofollow.get(i)));
          n=nodesTofollow.size();
        }
        n=nodesTofollow.size();
        for(int i=0;i<n;i++){
            pathTofollow.add(nodesTofollow.get(i).getPosition());
        }
        return pathTofollow;
    }
}



    /**la méthode findmeaway appelle la méthode précédente pour trouver les noeuds de proche
    en proche puis améliore le chemin à l'aide du dictionnaire nodesbones
    */

        /*public ArrayList<Vec2> findmeaway(Vec2 positiondepart, Vec2 positionarrivee) {
        Table table=new Table(logn,config);

        graphe = new Graphe(table);
        Noeud noeudepart = new Noeud(positiondepart, 0);
        Noeud noeudarrivee = new Noeud(positionarrivee, 0);
        ArrayList<Noeud> nodes = graphe.getNodes();
        nodes.add(0,noeudepart);
        nodes.add(noeudarrivee);
        graphe.createAretes(nodes);
        Noeud noeudepart = new Noeud(positiondepart, 0, 0,new ArrayList<Noeud>());
        Noeud noeudarrivee = new Noeud(positionarrivee, 0, 0, new ArrayList<Noeud>());
        graphe = new Graphe(table);
        ArrayList<Noeud> nodes = graphe.getNodes();
        HashMap<Noeud, ArrayList<Arete>> nodesbones = graphe.getNodesbones();
      //  Noeud noeudepart = new Noeud(positiondepart, 0, new ArrayList<Noeud>());
        nodes.add(noeudepart);
        //Noeud noeudarrivee = new Noeud(positionarrivee, 0, new ArrayList<Noeud>());
        nodes.add(noeudarrivee);
        graphe.createAretes(nodes);
        //HashMap<Noeud, ArrayList<Arete>> nodesbones = graphe.getNodesbones();
        ArrayList<Noeud> nodestofollow = new ArrayList<>();
        ArrayList<Vec2> pathtofollow = new ArrayList<>();
        ArrayList<Noeud> nodesTokeep=new ArrayList<>();
        // la liste pathtofollow contient les vecteurs associés aux noeuds dans nodestofollow
        nodestofollow.add(0,noeudepart);
        int n = graphe.getNodes().size();
        ArrayList<Arete> aretelist;
        /*Trouver un chemin initial en utilisant la méthode précedente:trouver les noeuds
        les plus proches de proche en proche
         */
        /*for (int i = 0; i < n; i++) {
            nodestofollow.add(closestNode(nodestofollow.get(i)));
            if(nodestofollow.get(i)==noeudarrivee){
                break;
            }
        }
        /*Améliorer le chemin trouvé en utilisant nodesbones
          Le chemin trouvé contient forcément le chemin optimal, à l'aide du dictionnaire
          nodesbones, pour chaque noeud on a la liste de toutes les aretes qui lui sont
          reliées, donc si on trouve un noeud dans le chemin trouvé mais qui est déjà relié à
          noeud avant on supprime tous les noeuds between ces deux
         */

        /*int m = nodestofollow.size();

        boolean toadd;
        for (int i = 0; i < m; i++) {
            //aretelist = graphe.getBoneslist().get(nodestofollow.get(i));
            toadd=false;
            for(int j=i;j<m;j++){
                //if(contain(aretelist,nodestofollow.get(j))){
                nodesTokeep.add(nodestofollow.get(i));
                nodesTokeep.add(nodestofollow.get(j));
                toadd=true;
                }

            }
            //if(!toadd){
                //nodesTokeep.add(nodestofollow.get(i));
            //}
        //}
        //int l=nodesTokeep.size();
        /*for(int i=0;i<l;i++){
            pathtofollow.get(i).setX(nodesTokeep.get(i).getPosition().getX());
            pathtofollow.get(i).setY(nodesTokeep.get(i).getPosition().getY());
        }
        return pathtofollow;
    }
    //test pour savoir si un noeud est relié à un autre
    private boolean contain(ArrayList<Arete> aretelist,Noeud node){
        int n=aretelist.size();
        for(int i=0;i<n;i++){
            if(aretelist.get(i).noeud2==node){
                return true;
            }
        }
        return false;
    }





/*


    //rajoute dans une liste les voisins d'un noeud
    public void OpenList(Noeud node, ArrayList<Noeud> list, ArrayList areteliste){
        for(int i; i<areteliste.size();i++){
            if(node == areteliste.get(i).noeud1){

            }
        }
    }
*/
//}



