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
import enums.ConfigInfoRobot;
import enums.ScriptNames;
import enums.Speed;
import exceptions.ContainerException;
//import org.omg.Messaging.SYNC_WITH_TRANSPORT;
import patternRecognition.PatternRecognition;
import pfg.config.Config;
import robot.EthWrapper;
import robot.Locomotion;
import scripts.ScriptManager;
import simulator.ThreadSimulator;
import strategie.GameState;
import table.Table;
import tests.JUnit_PatternRecognition;
import threads.ThreadInterface;
import threads.ThreadTimer;
import threads.dataHandlers.ThreadEth;

import java.nio.file.FileSystemLoopException;
import java.time.LocalDateTime;

/**
 * Code qui démarre le robot en début de match
 *
 * @author 4223, gaelle, rem
 */
public class MainPattern {
    static Container container;
    static Config config;
    static EthWrapper mEthWrapper;
    static PatternRecognition patternRecognition;
    static GameState gameState;
    static Locomotion mLocomotion;

    // dans la config de debut de match, toujours demander une entrée clavier assez longue (ex "oui" au lieu de "o", pour éviter les fautes de frappes. Une erreur a ce stade coûte cher.
// ---> En même temps si tu tapes n à la place de o, c'est que tu es vraiment con.  -Discord
// PS : Les vérifications et validations c'est pas pour les chiens.

    public static void main(String[] args) throws InterruptedException {
        try {
            container = new Container();
            Thread.currentThread().setPriority(6);
            config = container.getConfig();
            mEthWrapper = container.getService(EthWrapper.class);
            gameState=container.getService(GameState.class);
            mLocomotion=container.getService(Locomotion.class);
            container.getService(ThreadEth.class);
            patternRecognition = container.getService(PatternRecognition.class);
            patternRecognition.setDebugPatternRecognition(true);
            container.startInstanciedThreads();
        } catch (ContainerException p) {
            p.printStackTrace();
            System.out.println("bug container");
        }

        while (patternRecognition.isMovementLocked()) {
            Thread.sleep(10);
        }
        while (!patternRecognition.isRecognitionDone()) {
            Thread.sleep(10);
        }

        System.out.println("Reconnaissance de pattern terminée");

        try {
            container.destructor();
        } catch (ContainerException e) {
            e.printStackTrace();
            System.out.println("bug container");
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
            gameState.setJumperRemoved(true);
            //Maintenant que le jumper est retiré, le match a commencé
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
            gameState.setJumperRemoved(true);
            // maintenant que le jumper est retiré, le match a commencé
            ThreadTimer.matchStarted = true;
            System.out.println("Robot pret pour le match, pas d'attente du retrait de jumper");
        }
    }
}
