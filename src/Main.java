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

import container.Container;
import enums.*;
import exceptions.ContainerException;
import exceptions.Locomotion.PointInObstacleException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.NoPathFound;
import patternRecognition.PatternRecognition;
import pfg.config.Config;
import robot.EthWrapper;
import robot.Locomotion;
import scripts.AbstractScript;
import scripts.ScriptManager;
import scripts.TakeCubes;
import strategie.GameState;
import strategie.Pair;
import table.Table;
import threads.ThreadInterface;
import threads.dataHandlers.ThreadLidar;
import threads.threadScore.ThreadScore;
import threads.ThreadTimer;
import threads.dataHandlers.ThreadEth;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Code qui démarre le robot en début de match
 *
 * @author 4223, gaelle, rem
 */
public class Main {
    static Container container;
    static Config config;
    static GameState realState;
    static ScriptManager scriptmanager;
    static EthWrapper mEthWrapper;
    static Locomotion mLocomotion;
    static PatternRecognition patternRecognition;
    static Process process;

    // dans la config de debut de match, toujours demander une entrée clavier assez longue (ex "oui" au lieu de "o", pour éviter les fautes de frappes. Une erreur a ce stade coûte cher.
// ---> En même temps si tu tapes n à la place de o, c'est que tu es vraiment con.  -Discord
// PS : Les vérifications et validations c'est pas pour les chiens.

    public static void main(String[] args) throws InterruptedException {
        int matchScriptVersionToExecute=2;
        boolean symetry;
        try {
            // TODO : initialisation des variables globales du robot & objets...
            container = new Container();
            config = container.getConfig();
            matchScriptVersionToExecute=config.getInt(ConfigInfoRobot.MATCHSCRIPT_TO_EXECUTE);
            symetry=config.getString(ConfigInfoRobot.COULEUR).equals("orange");
            TasCubes.setSymetry(symetry);
            TasCubes.setMatchScriptVersion(matchScriptVersionToExecute);

            ProcessBuilder pBuilder = new ProcessBuilder("python3", "main.py");
            pBuilder.directory(new File("../lidar"));

            Runtime.getRuntime().addShutdownHook(new Thread(() -> shutdown()));

            realState = container.getService(GameState.class);
            scriptmanager = container.getService(ScriptManager.class);
            mEthWrapper = container.getService(EthWrapper.class);
            mLocomotion = container.getService(Locomotion.class);
            patternRecognition = container.getService(PatternRecognition.class);
            if (config.getBoolean(ConfigInfoRobot.SIMULATION)){
                ThreadInterface anInterface = container.getService(ThreadInterface.class);
            }
            Thread.currentThread().setPriority(6);
            container.getService(ThreadEth.class);
            container.getService(ThreadTimer.class);
            container.getService(ThreadScore.class);
            container.getService(ThreadLidar.class);
            container.startInstanciedThreads();
            realState.robot.setPosition(Table.entryPosition.clone());
            realState.robot.setOrientation(Table.entryOrientation);
            realState.robot.setLocomotionSpeed(Speed.DEFAULT_SPEED);
            Thread.sleep(500);
            process = pBuilder.start();
        } catch (ContainerException p) {
            System.out.println("bug container");
            p.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            int index = 0;
            ArrayList<Pair> scriptsToExecute = new ArrayList<>();
            scriptsToExecute.add(new Pair(scriptmanager.getScript(ScriptNames.TAKE_CUBES), 2));
            scriptsToExecute.add(new Pair(scriptmanager.getScript(ScriptNames.TAKE_CUBES), 1));
            scriptsToExecute.add(new Pair(scriptmanager.getScript(ScriptNames.DEPOSE_CUBES), 0));
            scriptsToExecute.add(new Pair(scriptmanager.getScript(ScriptNames.ACTIVATION_PANNEAU_DOMOTIQUE), 0));
            scriptsToExecute.add(new Pair(scriptmanager.getScript(ScriptNames.TAKE_CUBES), 0));
            scriptsToExecute.add(new Pair(scriptmanager.getScript(ScriptNames.DEPOSE_CUBES), 2));

            // TODO : initialisation du robot avant retrait du jumper (actionneurs)
            System.out.println("MatchScript to execute: "+matchScriptVersionToExecute);
            System.out.println("Le robot commence le match");

            waitMatchBegin();
            while(patternRecognition.isMovementLocked()) {
                Thread.sleep(10);
            }

            //TODO : lancer l'IA
            Pair currentPair;
            while (!scriptsToExecute.isEmpty()) {
                currentPair = scriptsToExecute.get(index);
                try {
                    currentPair.getScript().goToThenExec(currentPair.getVersion(), realState);
                    index +=1;
                } catch (Exception e) {
                    e.printStackTrace();
                    currentPair.getScript().finalize(realState, e);
                    if (realState.table.getObstacleManager().isPositionInObstacle(realState.robot.getPosition())) {
                        realState.robot.goTo(realState.table.getGraph().closestNodeToPosition(realState.robot.getPosition()).getPosition());
                    }
                    if (currentPair.getScript() instanceof TakeCubes && currentPair.getVersion() == 2) {
                        scriptsToExecute.add(2, new Pair(scriptmanager.getScript(ScriptNames.TAKE_CUBES), 0));
                        scriptsToExecute.remove(4);
                        index+=1;
                    }
                }
                if (index >= scriptsToExecute.size()) {
                    index = 0;
                }
            }

            Runtime.getRuntime().exec("killall -SIGINT python3");

        } catch (Exception e) {
            e.printStackTrace();
            try {
                Runtime.getRuntime().exec("killall -SIGINT python3");
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            container.getLog().critical(e.getStackTrace());
        }
    }





    /**
     * Attend la mise en place puis le retrait du jumper pour lancer le robot dans son match
     * Méthode à appeler dans le main juste avant de lancer l'IA ou le match scripté
     */
    static void waitMatchBegin() {

        boolean useJumper=config.getBoolean(ConfigInfoRobot.ATTENTE_JUMPER);
        if (useJumper) {
            mEthWrapper.waitForJumperRemoval();
            System.out.println("Robot pret pour le match, attente du retrait du jumper");
            while (!mLocomotion.getThEvent().wasJumperRemoved()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            realState.setJumperRemoved(true);
            // maintenant que le jumper est retiré, le match a commencé
            ThreadTimer.matchStarted = true;

            //On attend encore 50ms pour que le jumper soit bien retiré
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        else{
            mLocomotion.getThEvent().setJumperRemoved(true);
            realState.setJumperRemoved(true);
            // maintenant que le jumper est retiré, le match a commencé
            ThreadTimer.matchStarted = true;
            System.out.println("Robot pret pour le match, pas d'attente du retrait de jumper");
        }
    }

    /** Shutdown ! */
    static void shutdown() {
        try {
            Runtime.getRuntime().exec("killall -SIGINT python3");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
