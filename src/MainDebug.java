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
import enums.Speed;
import exceptions.ContainerException;
import graphics.AffichageDebug;
import pfg.config.Config;
import robot.EthWrapper;
import robot.Locomotion;
import scripts.ScriptManager;
import strategie.GameState;
import table.Table;
import threads.ThreadInterface;
import threads.ThreadTimer;
import utils.Log;
import utils.Sleep;

/**
 * Code de tracé de graphe pour l'asser'
 * @author PF
 *
 */
public class MainDebug
{

    static Container container;
    static Config config;
    static GameState realState;
    static ScriptManager scriptmanager;
    static EthWrapper mEthWrapper;
    static Locomotion mLocomotion;


    // dans la config de debut de match, toujours demander une entrée clavier assez longue (ex "oui" au lieu de "o", pour éviter les fautes de frappes. Une erreur a ce stade coûte cher.
// ---> En même temps si tu tapes n à la place de o, c'est que tu es vraiment con.  -Discord
// PS : Les vérifications et validations c'est pas pour les chiens.
    public static void main(String[] args) throws Exception
    {
        try
        {
            container = new Container();
            config = container.getConfig();
            realState = container.getService(GameState.class);
            scriptmanager = container.getService(ScriptManager.class);
            mEthWrapper = container.getService(EthWrapper.class);
            mLocomotion= container.getService(Locomotion.class);

            Thread.currentThread().setPriority(6);

            // TODO : faire une initialisation du robot et de ses actionneurs
            realState.robot.setPosition(Table.entryPosition);
            realState.robot.setOrientation(Math.PI);
            realState.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
            //  container.startAllThreads();
            container.getService(ThreadInterface.class);


            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        AffichageDebug aff = container.getService(AffichageDebug.class);
                        Sleep.sleep(100);
                        String[] noms = {"tick g", "tick d", "angle", "vg", "vd", "consigne transl", "consigne g", "consigne d"};
                        for(int i = 0; i < 3000; i++) {
                            double[] data;
                                data = mEthWrapper.pfdebug();
                                aff.addData(data, noms);
                        }
                    } catch (ContainerException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });


            t.start(); // On lance le thread
            realState.robot.turn(-0.01);
            Thread.sleep(500);
            t.join(); //On attend la fin du thread

            Log.stop();

        } catch (ContainerException e) {
            e.printStackTrace();
        }
    }



    /**
     * Attend la mise en place puis le retrait du jumper pour lancer le robot dans son match
     * Méthode à appeler dans le main juste avant de lancer l'IA ou le match scripté
     */
    static void waitMatchBegin()
    {

        boolean useJumper=config.getBoolean(ConfigInfoRobot.ATTENTE_JUMPER);

        // attend l'insertion du jumper
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
