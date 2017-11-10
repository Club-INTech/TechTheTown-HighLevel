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
import robot.Robot;
import smartMath.Vec2;
import table.obstacles.Obstacle;
import table.obstacles.ObstacleCircular;
import table.obstacles.ObstacleManager;
import table.obstacles.ObstacleRectangular;


import java.util.ArrayList;


/**
 * Pathfinding du robot ! Contient l'algorithme
 */
public class Pathfinding implements Service {
    private ObstacleManager obstacleManager;
    private ArrayList<ObstacleCircular> listCircu;
    private Vec2 tabposition[]=new Vec2[10];
    private ArrayList<Noeud> noeud;
    private int distanceobsnodeX;
    private int distanceobsnodeY;



    private Pathfinding() {}
    @Override
    public void updateConfig() {


    }

    public void createnoeudTable{
        listCircu=obstacleManager.getmCircularObstacle();
        for(int i = 1; i <= 11; i++){
            tabposition[i].setX(listCircu.get(0).getPosition().getX() + distanceobsnodeX);
            tabposition[i].setY(listCircu.get(0).getPosition().getY() + distanceobsnodeY);
            noeud.get(i).position = tabposition[i];

        }
    public void createArrete(Noeud node1, Noeud node2 ) {
            /*equation de la droite entre node1 et node2 :
            y=(node1.position.getY()-node2.position.getY())*(x-node1.position.getX())+node1.position.getY*(node1.position.getX()-node2.position.getX())/(node1.position.getX()-node2.position.getX())
            y=((y1-y2)(x-x1)+y1(x1-x2))/(x1-x2)
             */

    }
    public void findmeaway(Noeud nodeArrivee) {
        Robot robot=new Robot();
        noeud.add(robot.getPosition());
        noeud.add(nodeArrivee);
        if robot.getposition.getX()=nodeArrivee.position.getX(){
            if obstacleManager.isEnnemyInCone()=false{


            }

        }

    }







    }

    /*Noeud noeud1= new Noeud( new Vec2( 1350, 246),0);
    Noeud noeud2= new Noeud( new Vec2( 1178, 772),0);
    Noeud noeud3= new Noeud( new Vec2( 943, 1404),0);*/





    

}