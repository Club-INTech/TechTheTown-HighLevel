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

import enums.ConfigInfoRobot;
import enums.ScriptNames;
import enums.Speed;
import exceptions.*;
import exceptions.Locomotion.ImmobileEnnemyForOneSecondAtLeast;
import exceptions.Locomotion.PointInObstacleException;
import exceptions.Locomotion.UnableToMoveException;
import graphics.Window;
import hook.HookFactory;
import org.junit.Before;
import org.junit.Test;
import pathfinder.Graphe;
import pathfinder.Pathfinding;
import robot.Robot;
import scripts.ScriptManager;
import simulator.ThreadSimulator;
import simulator.ThreadSimulatorMotion;
import smartMath.Circle;
import smartMath.Vec2;
import strategie.GameState;
import strategie.IA.IA;
import table.Table;
import table.obstacles.ObstacleManager;
import threads.ThreadInterface;
import threads.dataHandlers.ThreadSensor;

import java.util.ArrayList;

/**
 * Tests Unitaires pour le pathfinding : utiliser ce JUnit pour faire des petits tests de java
 * si vous voulez !
 */
public class JUnit_Recalage extends JUnit_Test {

    private Robot robotReal;
    private ScriptManager scriptManager;
    private GameState state;
    private ThreadSimulator simulator;
    private ThreadSimulatorMotion simulatorMotion;
    private ThreadSensor threadSensor;
    private Table table;

    private IA ia;


    @Before
    public void setUp() {
        try {
            super.setUp();
            robotReal = container.getService(Robot.class);
            scriptManager = container.getService(ScriptManager.class);
            state = container.getService(GameState.class);
            table = container.getService(Table.class);
            threadSensor=container.getService(ThreadSensor.class);
            container.getService(ThreadInterface.class);
            ia=container.getService(IA.class);

            container.startInstanciedThreads();

        } catch (Exception e) {
            e.printStackTrace();
            ia.tryToDoAnotherNode(null);
        }
    }


    @Test
    public void test() {

        gameState.robot.setPosition(Table.entryPosition);
        gameState.robot.setOrientation(Table.entryOrientation);

        log.debug("Avant recalage : " +"Position : " + robotReal.getPosition()+"Orientation : "+robotReal.getOrientation());
        try {
            scriptManager.getScript(ScriptNames.RECALAGE).execute(0, state);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UnableToMoveException e) {
            e.printStackTrace();
        } catch (ExecuteException e) {
            e.printStackTrace();
        } catch (BlockedActuatorException e) {
            e.printStackTrace();
        } catch (BadVersionException e) {
            e.printStackTrace();
        } catch (PointInObstacleException e) {
            e.printStackTrace();
        } catch (ImmobileEnnemyForOneSecondAtLeast immobileEnnemyForOneSecondAtLeast) {
            immobileEnnemyForOneSecondAtLeast.printStackTrace();
        } catch (NoPathFound noPathFound) {
            noPathFound.printStackTrace();
        }
        log.debug("Après recalage : " +"Position : " + robotReal.getPosition()+"Orientation : "+robotReal.getOrientation());
    }

}