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
import enums.ActuatorOrder;
import enums.ConfigInfoRobot;
import enums.ScriptNames;
import enums.Speed;
import exceptions.ContainerException;
import pfg.config.Config;
import robot.EthWrapper;
import robot.Locomotion;
import scripts.ScriptManager;
import strategie.GameState;
import table.Table;
import threads.ThreadTimer;
import threads.dataHandlers.ThreadEth;
import threads.dataHandlers.ThreadSensor;
import utils.Log;

/**
 * Main Homologation
 */
public class MainHomologation {
    static Container container;
    static Config config;
    static GameState realState;
    static ScriptManager scriptmanager;
    static EthWrapper mEthWrapper;
    static Log log;
    static Locomotion mLocomotion;

    // dans la config de debut de match, toujours demander une entrée clavier assez longue (ex "oui" au lieu de "o", pour éviter les fautes de frappes. Une erreur a ce stade coûte cher.
// ---> En même temps si tu tapes n à la place de o, c'est que tu es vraiment con.  -Discord
// PS : Les vérifications et validations c'est pas pour les chiens.

    public static void main(String[] args) throws InterruptedException {
        try {
            container = new Container();
            config = container.getConfig();
            config.override(ConfigInfoRobot.BASIC_DETECTION,true);
            config.override(ConfigInfoRobot.ADVANCED_DETECTION,false);
            log = container.getService(Log.class);
            realState = container.getService(GameState.class);
            scriptmanager = container.getService(ScriptManager.class);
            mEthWrapper = container.getService(EthWrapper.class);
            mLocomotion = container.getService(Locomotion.class);
            Thread.currentThread().setPriority(6);
            container.getService(ThreadSensor.class);
            container.getService(ThreadEth.class);
            container.getService(ThreadTimer.class);
            container.startInstanciedThreads();
            realState.robot.setPosition(Table.entryPosition);
            realState.robot.setOrientation(Table.entryOrientation);
            realState.robot.setLocomotionSpeed(Speed.SLOW_ALL);


        } catch (ContainerException p) {
            p.printStackTrace();
        }
        try {
            realState.robot.useActuator(ActuatorOrder.BASIC_DETECTION_ENABLE,true);
            waitMatchBegin();
            scriptmanager.getScript(ScriptNames.SCRIPT_HOMOLOGATION).goToThenExec(0, realState);


        } catch (Exception e) {
            log.logException(e);
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
}