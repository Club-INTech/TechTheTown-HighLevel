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
import enums.DirectionStrategy;
import enums.ScriptNames;
import enums.Speed;
import exceptions.ContainerException;
import exceptions.Locomotion.UnableToMoveException;
import hook.Hook;
import robot.Locomotion;
import robot.SerialWrapper;
import scripts.ScriptManager;
import strategie.GameState;
import table.Table;
import threads.ThreadInterface;
import threads.ThreadTimer;
import threads.dataHandlers.ThreadSensor;
import utils.Config;

import java.util.ArrayList;

/**
 * Code qui démarre le robot en début de match
 * @author 4223, gaelle, rem
 *
 */
public class Main {
	static Container container;
	static Config config;
	static GameState realState;
	static ArrayList<Hook> emptyHook = new ArrayList<>();
	static ScriptManager scriptmanager;
	static SerialWrapper mSerialWrapper;
	static Locomotion mLocomotion;


	// dans la config de debut de match, toujours demander une entrée clavier assez longue (ex "oui" au lieu de "o", pour éviter les fautes de frappes. Une erreur a ce stade coûte cher.
// ---> En même temps si tu tapes n à la place de o, c'est que tu es vraiment con.  -Discord
// PS : Les vérifications et validations c'est pas pour les chiens.
	public static void main(String[] args) throws InterruptedException {
		try {

			Config.configFileName = "config.txt";
			container = new Container();
			config = container.getService(Config.class);
			//AffichageDebug aff = container.getService(AffichageDebug.class);
			realState = container.getService(GameState.class);
			scriptmanager = container.getService(ScriptManager.class);
			mSerialWrapper = container.getService(SerialWrapper.class);
			mLocomotion = container.getService(Locomotion.class);
			config.updateConfig();

			Thread.currentThread().setPriority(6);

			// TODO : initialisation des variables globales du robot & objets...
			realState.robot.setPosition(Table.entryPosition);
			realState.robot.setOrientation(-Math.PI/2);
			realState.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);

			container.getService(ThreadSensor.class);
			//container.getService(ThreadInterface.class);
			container.getService(ThreadTimer.class);
			container.startInstanciedThreads();

		} catch (ContainerException p) {
			System.out.println("bug container");
		}
		// container.startAllThreads();
		try {
			// TODO : initialisation du robot avant retrait du jumper (actionneurs)
			System.out.println("Le robot commence le match");
			waitMatchBegin();
			// TODO : lancer l'IA

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * Attend la mise en place puis le retrait du jumper pour lancer le robot dans son match
	 * Méthode à appeler dans le main juste avant de lancer l'IA ou le match scripté
	 */
	static void waitMatchBegin()
	{

		System.out.println("Robot pret pour le match, attente du retrait du jumper");

		// attend l'insertion du jumper
		while(mSerialWrapper.isJumperAbsent())
		{
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// puis attend son retrait
		while(!mSerialWrapper.isJumperAbsent())
		{
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// maintenant que le jumper est retiré, le match a commencé
		ThreadTimer.matchStarted = true;
	}
}
