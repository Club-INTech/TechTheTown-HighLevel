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



