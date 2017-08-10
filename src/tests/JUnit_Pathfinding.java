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

import enums.DirectionStrategy;
import enums.ScriptNames;
import enums.Speed;
import graphics.Window;
import hook.Hook;
import org.junit.Before;
import org.junit.Test;
import pathfinder.Graphe;
import pathfinder.Noeud;
import pathfinder.Pathfinding;
import scripts.ScriptManager;
import smartMath.Geometry;
import smartMath.Segment;
import smartMath.Vec2;
import strategie.GameState;
import table.Table;
import utils.Log;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Deux tests pour le Pathfinding : Une sans le robot, permettant de voir la trajectoire du robot à partir deux deux
 * clicks sur la table, et une avec le robot, version qui envoie le robot à des positions random
 * @author alban
 *
 */
public class JUnit_Pathfinding extends JUnit_Test {

    private GameState mRobot;
    private ScriptManager scriptManager;
    private Table table;
    private Pathfinding pf;
    private Window win;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        log = container.getService(Log.class);
        table = container.getService(Table.class);
        win = new Window(table);
        pf = container.getService(Pathfinding.class);
    }

    @Test
    public void testClickedPF() throws Exception {
        Graphe graphe = pf.getGraphe();
        ArrayList<Vec2> graph = new ArrayList<>();

        for (Noeud n : graphe.getlNoeuds()) {
            graph.add(n.position);
        }

        win.getPanel().drawGraphe(graph);
        win.getPanel().drawLinesGraph(graphe.getlNoeuds()); // Commenter cette ligne pour ne plus afficher les liens du graphe

        Vec2 dep = new Vec2();
        Vec2 arr = new Vec2();
        while (true) {
            if (win.getMouse().hasClicked()) {
                dep = win.getMouse().getLeftClickPosition();
                arr = win.getMouse().getRightClickPosition();
                log.debug(dep + " bkd " + arr);

                if (!dep.isNull() && !arr.isNull()) {
                    log.debug(win.getMouse().getLeftClickPosition() + " " + win.getMouse().getRightClickPosition());
                    long start = System.currentTimeMillis();

                    ArrayList<Vec2> p = pf.Astarfoulah(dep, arr, Math.PI, 460, 1.6D);

                    long end = System.currentTimeMillis();
                    System.out.println("time elapsed : " + (end - start));
                    win.getPanel().drawArrayList(p);
                    win.getPanel().repaint();
                } else {
                    Thread.sleep(200);

                }
            } else
                Thread.sleep(200);
        }
    }

    @Test
    public void testRandom() throws Exception {
        mRobot = container.getService(GameState.class);
        mRobot.updateConfig();
        mRobot.robot.setPosition(Table.entryPosition);
        mRobot.robot.setOrientation(Math.PI);
        mRobot.robot.setLocomotionSpeed(Speed.SLOW_ALL);
        scriptManager = container.getService(ScriptManager.class);
        mRobot.robot.setDirectionStrategy(DirectionStrategy.FASTEST);

        scriptManager.getScript(ScriptNames.INITIALISE_ROBOT).goToThenExec(0, mRobot, new ArrayList<Hook>());

        Graphe graphe = pf.getGraphe();
        ArrayList<Vec2> graph = new ArrayList<>();

        for (Noeud n : graphe.getlNoeuds()) {
            graph.add(n.position);
        }

        int randomYarr = ThreadLocalRandom.current().nextInt(0, 2000);
        int randomXarr = ThreadLocalRandom.current().nextInt(-1500, 1500);
        ArrayList<Vec2> path = pf.Astarfoulah(mRobot.robot.getPosition(), new Vec2(randomXarr, randomYarr), mRobot.robot.getOrientation(), mRobot.robot.getLocomotionSpeed().translationSpeed, mRobot.robot.getLocomotionSpeed().rotationSpeed);

        mRobot.robot.followPath(path, new ArrayList<Hook>());

        for (int i = 0; i < 100; i++) {

            win.getPanel().drawArrayList(path);
            win.getPanel().repaint();
            win.getPanel().drawGraphe(graph);
            win.getPanel().drawLinesGraph(graphe.getlNoeuds());

            randomYarr = ThreadLocalRandom.current().nextInt(0, 2000);
            randomXarr = ThreadLocalRandom.current().nextInt(-1500, 1500);
            path = pf.Astarfoulah(mRobot.robot.getPosition(), new Vec2(randomXarr, randomYarr), mRobot.robot.getOrientation(), mRobot.robot.getLocomotionSpeed().translationSpeed, mRobot.robot.getLocomotionSpeed().rotationSpeed);
            mRobot.robot.followPath(path, new ArrayList<Hook>());
        }
        returnToEntryPosition(mRobot);
    }
}
