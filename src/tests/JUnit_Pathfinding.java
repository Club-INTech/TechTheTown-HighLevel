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

package tests;

import container.Container;
import exceptions.ContainerException;
import exceptions.Locomotion.PointInObstacleException;
import exceptions.NoPathFound;
import graphics.Window;
import org.junit.Before;
import org.junit.Test;
import robot.Robot;
import scripts.ScriptManager;
import smartMath.Circle;
import smartMath.Vec2;
import strategie.GameState;
import table.Table;

import java.util.ConcurrentModificationException;

/**
 * Tests Unitaires pour le pathfinding : utiliser ce JUnit pour faire des petits tests de java
 * si vous voulez !
 */
public class JUnit_Pathfinding extends JUnit_Test {

    /** Table */
    private Table table;
    private ScriptManager scriptManager;

    @Before
    public void setUp() throws ContainerException, InterruptedException {
        container = new Container();
        gameState = container.getService(GameState.class);
        robot = container.getService(Robot.class);
        table = container.getService(Table.class);
        scriptManager = container.getService(ScriptManager.class);
    }

    @Test
    public void testMoveToCircle() {
        try {
            container.startInstanciedThreads();
            robot.setPosition(Table.entryPosition);
            robot.setOrientation(Table.entryOrientation);

            (new Thread(() -> (new Window(table, gameState, scriptManager, false)).showHandled())).start();

            try {
                robot.moveToCircle(new Circle(new Vec2(0, 500)));
            } catch (ConcurrentModificationException ce) {
                ce.printStackTrace();
            }
        } catch (InterruptedException e ) {
            e.printStackTrace();
        } catch (PointInObstacleException e) {
            e.printStackTrace();
        } catch (NoPathFound e) {
            e.printStackTrace();
        }
    }
}