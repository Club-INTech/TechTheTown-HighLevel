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
import graphics.Window;
import patternRecognition.PatternRecognition;
import patternRecognition.UseWebcam;
import pfg.config.Config;
import robot.EthWrapper;
import robot.Locomotion;
import scripts.ScriptManager;
import smartMath.Circle;
import smartMath.Geometry;
import smartMath.Vec2;
import strategie.GameState;
import table.Table;
import threads.ThreadInterface;
import threads.dataHandlers.ThreadSensor;
import threads.threadScore.ThreadScore;
import threads.ThreadTimer;
import threads.dataHandlers.ThreadEth;

import javax.print.Doc;

/**
 * Code qui démarre le robot en début de match
 *
 * @author 4223, gaelle, rem
 */
public class MainMontlhery {
    static Container container;
    static Config config;
    static GameState realState;
    static ScriptManager scriptmanager;
    static EthWrapper mEthWrapper;
    static Locomotion mLocomotion;
    static PatternRecognition patternRecognition;
    static Window win;

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
            realState = container.getService(GameState.class);
            mEthWrapper = container.getService(EthWrapper.class);
            mLocomotion = container.getService(Locomotion.class);
            scriptmanager = container.getService(ScriptManager.class);
            container.getService(ThreadEth.class);
            container.startInstanciedThreads();
            realState.robot.setPosition(Table.entryPosition);
            realState.robot.setOrientation(Table.entryOrientation);
            realState.robot.setLocomotionSpeed(Speed.DEFAULT_SPEED);
        } catch (ContainerException p) {
            System.out.println("bug container");
            p.printStackTrace();
        }
        try {

            // TODO : initialisation du robot avant retrait du jumper (actionneurs)
            System.out.println("MatchScript to execute: "+matchScriptVersionToExecute);
            System.out.println("Le robot commence le match");

            win = new Window(container.getService(Table.class), realState, scriptmanager,true);

            realState.robot.setPosition(Table.entryPosition);
            realState.robot.setOrientation(Math.PI);
            realState.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);

            realState.robot.updateConfig();
            realState.robot.useActuator(ActuatorOrder.MONTLHERY, false);

            win.getKeyboard().run();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
