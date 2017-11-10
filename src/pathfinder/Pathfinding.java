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
import robot.EthWrapper;
import robot.Locomotion;
import robot.Robot;
import smartMath.Vec2;
import table.Table;
import table.obstacles.Obstacle;
import table.obstacles.ObstacleCircular;
import table.obstacles.ObstacleManager;
import table.obstacles.ObstacleRectangular;
import utils.Log;


import java.util.ArrayList;


/**
 * Pathfinding du robot ! Contient l'algorithme
 */
public class Pathfinding implements Service {
    private ObstacleManager obstacleManager;
    private ArrayList<ObstacleCircular> listCircu;
    private Vec2 tabposition[] = new Vec2[10];
    private Noeud nodeRobot;
    private ArrayList<Noeud> noeud;
    private int distanceobsnodeX;
    private int distanceobsnodeY;
    private Table table;


    private Pathfinding() {
    }

    @Override
    public void updateConfig() {


    }

    public void createnoeudTable(){
        listCircu = obstacleManager.getmCircularObstacle();
        for (int i = 1; i <= 11; i++) {
            tabposition[i].setX(listCircu.get(i).getPosition().getX() + distanceobsnodeX);
            tabposition[i].setY(listCircu.get(i).getPosition().getY() + distanceobsnodeY);
            noeud.get(i).position = tabposition[i];


        }
    }
}
