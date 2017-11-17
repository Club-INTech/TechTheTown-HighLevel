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
import smartMath.Vec2;
import sun.font.TrueTypeFont;
import sun.security.util.Length;
import sun.nio.cs.ArrayEncoder;
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



    private Pathfinding(Log logn, Config config){

    }

    @Override
    public void updateConfig() {
    }
    /*Cette méthode retourne le noeud le plus proche à un noeud en prenant l'arete avec
    le moindre cout
     */
    public Noeud closestNode(Noeud node) {
        ArrayList<Arete> aretelist = graphe.nodesbones.get(node);
        int n = aretelist.size();
        double min=aretelist.get(0).cout;
        int indicemin=0;
        for (int i = 0; i < n; i++) {
            if(aretelist.get(i).cout<min){
                min=aretelist.get(i).cout;
                indicemin=i;
            }
        }
        return aretelist.get(indicemin).noeud2;
    }



    /*la méthode findmeaway appelle la méthode précédente pour trouver les noeuds de proche
    en proche puis améliore le chemin à l'aide de nodesbones
    */

    public ArrayList<Vec2> findmeaway(Vec2 positiondepart, Vec2 positionarrivee){
        HashMap<Noeud,ArrayList<Arete>> nodesbones =graphe.nodesbones;
        ArrayList<Noeud> nodes=graphe.createNodes();
        Noeud noeudepart=new Noeud(positiondepart,0);
        nodes.add(noeudepart);
        Noeud noeudarrivee=new Noeud(positionarrivee,0);
        nodes.add(noeudarrivee);
        ArrayList<Noeud> nodestofollow=new ArrayList<>();
        ArrayList<Vec2> pathtofollow=new ArrayList<>();
        nodestofollow.add(noeudepart);
        graphe=new Graphe();
        int n=graphe.nodes.size();
        graphe.createAretes();
        //Trouver un chemin initial en utilisant la méthode précedente
        for(int i=0;i<n;i++) {
            nodestofollow.add(closestNode(nodestofollow.get(i)));
            pathtofollow.add(nodestofollow.get(i).position);
        }
        /*Améliorer le chemin trouvé en utilisant nodesbones
          Le chemin trouvé contient forcément le chemin optimal, à l'aide du dictionnaire
          nodesbones, pour chaque noeud on a la liste de toutes les aretes qui lui sont
          reliées, donc si on trouve un noeud dans le chemin trouvé mais qui est déjà relié à
          noeud avant on supprime tous les noeuds between ces deux
         */
        int m=nodestofollow.size();
        ArrayList<Arete> aretelist=new ArrayList<>();
        for(int i=0; i<m;i++) {
            aretelist = nodesbones.get(nodestofollow.get(i));
            for(int j=0;j<m;j++){
                if(contain(aretelist,nodestofollow.get(j))){
                    int ind=j;
                }
            for(int k=j;j>=i;j--){
                    nodestofollow.remove(k);
                    pathtofollow.remove(nodestofollow.get(k).position);
            }

            }
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

    public ArrayList<Vec2> findmyway (Vec2 positiondepart, Vec2 positionarrive){
        ArrayList<Noeud> openList;
        ArrayList<Noeud> closeList;
       // ArrayList<Arete> aretes = graphe.createAretes();
        ArrayList<Noeud> nodes = graphe.createNodes();
        HashMap<Noeud,ArrayList<Arete>> nodesbones = graphe.nodesbones;
        Noeud noeuddepart = new Noeud(positiondepart, 0);
        Noeud noeudarrive = new Noeud(positionarrive, 1);



    }


    public boolean NodeInList(ArrayList<Noeud> lst, Noeud node){
        for(int i = 0; i < lst.size(); i++ ){
            if (lst.get(i)==node){
                return true;
            }
        }
        return false;
    }


}



