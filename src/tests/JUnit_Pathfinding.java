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

import exceptions.ContainerException;
import exceptions.Locomotion.PointInObstacleException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.NoPathFound;
import graphics.Window;
import hook.HookFactory;
import org.junit.Test;
import pathfinder.Pathfinding;
import pathfinder.Graphe;
import robot.Robot;
import scripts.ScriptManager;
import smartMath.Vec2;
import strategie.GameState;
import table.Table;
import table.obstacles.ObstacleManager;

import java.util.ArrayList;

/**
 * Tests Unitaires pour le pathfinding : utiliser ce JUnit pour faire des petits tests de java
 * si vous voulez !
 */
public class JUnit_Pathfinding extends JUnit_Test {

    /**
     * Spécification des variables
     */
    private Table table;
    private ObstacleManager obstacleManager;
    private Robot robotReal;
    private ScriptManager scriptManager;
    private GameState state;
    private HookFactory hookFactory;

    /**
     * Méthode pour un test : ce sont les instruction executées par IntelliJ lorque vous lancer le test (clique
     * droit sur le test, 'Run')
     */
    @Test
    public void testUnit() throws ContainerException, InterruptedException {

        /** Instanciation des variables */
        table = container.getService(Table.class);
        //Pathfinding pathfinding = container.getService(Pathfinding.class);
        obstacleManager = table.getObstacleManager(); // Grâce au container, le champ ObstacleManager de votre table est déjà instancié !
        // Mais... pour commencer instancier vos variables à la main :
        Vec2 example = new Vec2(50, 40);
        Vec2 example2 = new Vec2(600, 1600);


        // Whatever you want... Le debug pour le moment c'est mettre des 'System.out.println()' qui affiche ce que
        // vous voulez dans la console lors de l'execution; par exemple :
//        System.out.println("\nAh ! : " + example);
        // ou (petite subtilité : entryposition est un champ static, lié à la classe Table; c'est pour cela que l'on y accède via Table.entryposition)
//        System.out.println("Position de départ du robot : " + Table.entryPosition);
        // Mais la facon "normale" fonctionne aussi :
        //      System.out.println("Position de départ : " + table.entryPosition);


        Window window = new Window(table);
        Graphe graphe = new Graphe(table, config, log);
        window.setArete(graphe.getBoneslist());

        robotReal = container.getService(Robot.class);
        state = container.getService(GameState.class);
        container.startInstanciedThreads();

        // Thread.sleep(20000);

        Pathfinding pathfinding = new Pathfinding(log,config,table);
        ArrayList<Vec2> path = new ArrayList<>();
        window.setPath(path);
        Vec2 clic = new Vec2();

        pathfinding.initGraphe();
        robotReal.setPosition(new Vec2(0, 500));
        robotReal.setOrientation(Math.PI / 2);

        while (true) {

            try {
                clic = window.waitLClic();

                path = pathfinding.findmyway(robotReal.getPosition(), clic);

                robotReal.followPath(path);

                window.setPath(path);
            } catch (PointInObstacleException e) {

                System.out.println("Obstacle!!");
                e.printStackTrace();
            } catch (UnableToMoveException e) {

                System.out.println("No way found !!");

                e.printStackTrace();
            }
        }

    }
}

