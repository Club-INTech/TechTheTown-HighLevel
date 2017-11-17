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
import table.obstacles.ObstacleCircular;
import table.obstacles.ObstacleManager;
import utils.Log;


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
    //Cette méthode retourne le noeud le plus proche à une position
    public Noeud closestNode(Noeud node) {
        int r = 1;
        int x0 = node.position.getX();
        int y0 = node.position.getY();
        int n = graphe.nodes.size();
        Vec2 position0=new Vec2();
        position0.setX(0);
        position0.setY(0);
        Noeud nodetoreturn=new Noeud(position0,0);
        for (int i = 0; i < n; i++) {
            int x = graphe.nodes.get(i).position.getX();
            int y = graphe.nodes.get(i).position.getY();
            if (Math.pow(x - x0, 2) + Math.pow(y - y0, 2) < Math.pow(r, 2)) {
                nodetoreturn=graphe.nodes.get(i);
            }
        }
        return nodetoreturn;

    }





    public ArrayList<Vec2> findmeaway(Vec2 positiondepart, Vec2 positionarrivee){
        graphe=new Graphe();
        int n=graphe.nodes.size();
        graphe.createAretes();
        HashMap<Noeud,ArrayList<Arete>> nodesbones =graphe.nodesbones;
        ArrayList<Noeud> nodes=graphe.createNodes();
        Noeud noeudepart=new Noeud(positiondepart,0);
        nodes.add(noeudepart);
        ArrayList<Noeud> nodestofollow=new ArrayList<>();
        ArrayList<Vec2> pathtofollow=new ArrayList<>();
        nodestofollow.add(noeudepart);
        for(int i=0;i<n;i++){
            nodestofollow.add(closestNode(nodestofollow.get(i)));
            if(!graphe.aretebetweentwonodes(nodestofollow.get(i-1),nodestofollow.get(i))){
                nodestofollow.remove(i);
            }
            pathtofollow.add(nodestofollow.get(i).position);
        }
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
    private boolean contain(ArrayList<Arete> aretelist,Noeud node){
        int n=aretelist.size();
        for(int i=0;i<n;i++){
            if(aretelist.get(i).noeud2==node){
                return true;
            }
        }
        return false;
    }



}



