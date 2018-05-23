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
import enums.Speed;
import exceptions.ContainerException;
import exceptions.Locomotion.ImmobileEnnemyForOneSecondAtLeast;
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
import simulator.ThreadSimulator;
import simulator.ThreadSimulatorMotion;
import smartMath.Circle;
import smartMath.Vec2;
import strategie.GameState;
import table.Table;
import table.obstacles.ObstacleManager;
import threads.ThreadInterface;

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
    private Pathfinding pathfinding;

    /**
     * Threads
     */
    private ThreadSimulator simulator;
    private ThreadSimulatorMotion simulatorMotion;
    private ThreadInterface anInterface;

    /*
    @Before
    public void setUp(){
        try {
            super.setUp();
            robotReal = container.getService(Robot.class);
            state=container.getService(GameState.class);
            scriptManager=container.getService(ScriptManager.class);
            table=container.getService(Table.class);
            container.startInstanciedThreads();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
*/

    /**
     * Méthode pour un test : ce sont les instruction executées par IntelliJ lorque vous lancer le test (clique
     * droit sur le test, 'Run')
     */
    @Test
    public void testUnit() throws ContainerException, InterruptedException, ImmobileEnnemyForOneSecondAtLeast {

        /** Instanciation des variables */

        //Pathfinding pathfinding = container.getService(Pathfinding.class);
        ; // Grâce au container, le champ ObstacleManager de votre table est déjà instancié !
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
        Graphe graphe = new Graphe(log, config, table);
        // Thread.sleep(20000);

//        Pathfinding pathfinding = new Pathfinding(log, config, table);
        //Pathfinding pathfinding = container.getService(Pathfinding.class);
        ArrayList<Vec2> path = new ArrayList<>();
        window.setPath(path);
        Vec2 clic = new Vec2();

//        pathfinding.initGraphe();
        Vec2 positionDepart=new Vec2(1252, 455);
        robotReal.setPosition(positionDepart);
        robotReal.setOrientation(Math.PI / 2);
        //   robotReal.setLocomotionSpeed(Speed.ULTRA_SLOW_ALL);

        while (true) {

            try {
                //clic = window.waitLClic();
                Vec2 positionentreeDeposeCubes = new Vec2(650, 175 + config.getInt(ConfigInfoRobot.ROBOT_RADIUS));

                path = pathfinding.findmyway(robotReal.getPosition(), clic);
                robotReal.followPath(path);

                //clic = window.waitLClic();
                //position d'entree de ActivationPanneauDomotique
                Vec2 arrivee = new Vec2(370, 350);
                Circle aim = new Circle(arrivee, 0);
                robotReal.moveToCircle(aim);
                window.setPath(path);
            } catch (PointInObstacleException e) {

                System.out.println("Obstacle!!");
                e.printStackTrace();
            } catch (UnableToMoveException e) {

                System.out.println("No way found !!");

                e.printStackTrace();
            } catch (NoPathFound e) {
                log.debug("pas de chemin trouvé");
                e.printStackTrace();
            }
        }
    }

    @Test
    public void pathTest() throws InterruptedException, ContainerException, ImmobileEnnemyForOneSecondAtLeast {
        pathfinding = container.getService(Pathfinding.class);
        table = container.getService(Table.class);
        obstacleManager = container.getService(ObstacleManager.class);
        robotReal = container.getService(Robot.class);
        state = container.getService(GameState.class);
        container.startInstanciedThreads();

        robotReal.setPosition(new Vec2(890, 835));
        robotReal.setOrientation(Math.PI);


        try {
            ArrayList<Vec2> pathToFollow = new ArrayList<>();
            //activation panneau domotique
            //pathToFollow = pathfinding.findmyway(robotReal.getPosition(), new Vec2(350, 370));
            //tas de cube
            pathToFollow = pathfinding.findmyway(robotReal.getPosition(), new Vec2(750,175+212));
            robotReal.followPath(pathToFollow);




        } catch (PointInObstacleException e) {

            System.out.println("Obstacle!!");
            e.printStackTrace();
        } catch (UnableToMoveException e) {

            System.out.println("No way found !!");

            e.printStackTrace();
        } catch (NoPathFound noPathFound) {
            noPathFound.printStackTrace();
        }
    }

    // promenade du robot
    @Test
    public void randomPathTest() throws InterruptedException, ContainerException, ImmobileEnnemyForOneSecondAtLeast {
        pathfinding = container.getService(Pathfinding.class);
        table = container.getService(Table.class);
        obstacleManager = container.getService(ObstacleManager.class);
        robotReal = container.getService(Robot.class);
        state = container.getService(GameState.class);
        anInterface = container.getService(ThreadInterface.class);

        simulator = container.getService(ThreadSimulator.class);
        simulatorMotion = container.getService(ThreadSimulatorMotion.class);


        container.startInstanciedThreads();
//        log.debug("begin script");
//        pathfinding.initGraphe();
        robotReal.setPosition(new Vec2(1252, 455));
        robotReal.setOrientation(Math.PI);
        robotReal.setLocomotionSpeed(Speed.SLOW_ALL);

        ArrayList<Vec2> pathToFollow = new ArrayList<>();
        Vec2 position = new Vec2();

        for (int i = 0; i <= 42; i++) {
            position.setX(Math.max((((int) (Math.random() * 3000 - 1500))), -1300));
            position.setY(((int) (Math.random() * 2000)));
            log.debug("Position : " + position);
            try {
                pathToFollow = pathfinding.findmyway(robotReal.getPosition(), position);
                robotReal.followPath(pathToFollow);
                log.debug("Arrived at " + position + i);
                if (i == 42) {
                    robotReal.followPath(pathfinding.findmyway(robotReal.getPosition(), new Vec2(1252, 455)));
                }
            } catch (PointInObstacleException e) {

                System.out.println("Obstacle!!" + i);
                e.printStackTrace();
            } catch (UnableToMoveException e) {

                System.out.println("No way found !!");

                e.printStackTrace();
            } catch (NoPathFound noPathFound) {
                noPathFound.printStackTrace();
            }
        }
    }

    @Test
    public void testSimulation() throws InterruptedException, ContainerException {

        pathfinding = container.getService(Pathfinding.class);
        table = container.getService(Table.class);
        obstacleManager = container.getService(ObstacleManager.class);
        robotReal = container.getService(Robot.class);
        state = container.getService(GameState.class);
//        simulator = container.getService(ThreadSimulator.class);
//        simulatorMotion = container.getService(ThreadSimulatorMotion.class);
        //anInterface = container.getService(ThreadInterface.class);
        container.startInstanciedThreads();


        Window window = new Window(table, state,scriptManager,false);
        /*window.setArete(pathfinding.getGraphe().getBoneslist());
        ArrayList<Vec2> path = new ArrayList<>();
        window.setPath(path);
        ArrayList<Vec2> clics = new ArrayList<>();*/
        Graphe graphe=container.getService(Graphe.class);

//        while (true) {
//              Vec2 position=new Vec2(650,540);
//              Circle circle=new Circle(position,87+212);
//              ArrayList<Vec2> path=circle.pointsaroundcircle(12);
//              window.setNode(graphe.getNodes());
//              window.setArete(graphe.getBoneslist());
//
//        }
        ArrayList<Vec2> clics = new ArrayList<>();
        ArrayList<Vec2> path = new ArrayList<>();
        window.repaint();
        while (true) {

            try {
                //clics = window.waitLRClic();
                path = pathfinding.findmyway(Table.entryPosition, new Vec2(370,390));
                window.setPath(path);
                window.repaint();
            } catch (PointInObstacleException e) {
                System.out.println("Obstacle!!");
                e.printStackTrace();
            } catch (NoPathFound e) {
                System.out.println("No way found");
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testGraphe() throws InterruptedException, ContainerException {
        pathfinding = container.getService(Pathfinding.class);
        table = container.getService(Table.class);
        state = container.getService(GameState.class);
        anInterface = container.getService(ThreadInterface.class);
        container.startInstanciedThreads();

        while (true);
    }
}