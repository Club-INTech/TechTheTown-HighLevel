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

package threads;

import enums.ConfigInfoRobot;
import pfg.config.Config;
import robot.EthWrapper;
import robot.Robot;
import table.Table;
import threads.dataHandlers.ThreadEth;
import utils.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Thread qui s'occupe de la gestion du temps: début du match et immobilisation du robot en fin de match
 * demande aussi périodiquement a la table qu'on lui fournit de retirer les obstacles périmés
 * C'est lui qui active les capteurs en début de match.
 * @author pf
 *
 */

public class ThreadTimer extends AbstractThread
{
	/** La robot */
	private Robot robot;

	/** La carte avec laquelle on doit communiquer */
	private EthWrapper ethWrapper;

	/** vrai si le match a effectivment démarré, faux sinon */
	public static boolean matchStarted = false;

	/** vrai si le match a effectivment pris fin, faux sinon */
	public static boolean matchEnded = false;

	/** Date de début du match. */
	private static long matchStartTimestamp = 0;

	/** Durée en millisecondes d'un match recupéré de la config */
	private static long matchDuration = 100000;

	/** Indique si on attend le jumper ou non */
	private boolean usingJumper;
	/**
	 * Crée le thread timer.-
	 *
	 * @param ethWrapper La carte d'asservissement avec laquelle on doit communiquer
	 */

	public ThreadTimer(Config config, Log log, Robot robot, EthWrapper ethWrapper)
	{
		super(config, log);
		this.ethWrapper = ethWrapper;
		this.robot=robot;

		updateConfig();
		Thread.currentThread().setPriority(8);
	}

	@Override
	public void run() {
		log.debug("Lancement du thread timer");

		ethWrapper.updateConfig();

		// Attente du démarrage du match : on attend que le jumper soit retiré

		if (this.usingJumper) {
			while (!robot.getmLocomotion().getThEvent().wasJumperRemoved()) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		// maintenant que le jumper est retiré, le match a commencé
		matchStarted = true;

		// Le match démarre ! On change l'état du thread pour refléter ce changement
		matchStartTimestamp = System.currentTimeMillis();
		log.critical("Jumper Enlevé");

		ethWrapper.updateConfig();

		log.debug("LE MATCH COMMENCE !");

		// boucle principale, celle qui dure tout le match
		while (System.currentTimeMillis() - matchStartTimestamp < matchDuration) {
			if (stopThreads) {
				// on s'arrète si le ThreadManager le demande
				log.debug("Arrêt du thread timer demandé durant le match");
				return;
			}
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				log.critical("InterruptedException : le sleep du ThreadTimer a été interrompu");
			}
		}

		log.debug("Fin des "+matchDuration+" ms de match, temps : "+(System.currentTimeMillis() - matchStartTimestamp));

		// Actions de fin de match
		onMatchEnded();
	}

	/**
	 * Actions à faire lorsque le match est terminé
	 */
	private void onMatchEnded()
	{
		// Le match est fini, immobilisation du robot
		log.debug("Lancement des actions de fin de match");
		matchEnded = true;
		ethWrapper.immobilise();

		// fin du match : on eteint la STM (RIP STM ?-2017 /// HAIL teensy 2018-?)
		ethWrapper.disableRotationnalFeedbackLoop();
		ethWrapper.disableTranslationnalFeedbackLoop();
		ethWrapper.disableSpeedFeedbackLoop();

		//On ferme la liaison HL/LL
		ethWrapper.close();
	}

	/**
	 * Temps restant avant la fin du match.
	 * @return le temps restant du match en milisecondes
	 */
	public static long remainingTime()
	{
		return matchStartTimestamp + matchDuration - System.currentTimeMillis();
	}

	/**
	 * Renvoie le temps actuellement écoulé depuis le début du match
	 * @return le temps (en ms)
	 */
	public static long getMatchCurrentTime() {
		if (matchStartTimestamp!=0) {
			return System.currentTimeMillis() - matchStartTimestamp;
		}
		else{
			return 0;
		}
	}

	public void updateConfig()
	{
		try
		{
			// Facteur x1000 car TEMPS_MATCH est en secondes et matchDuration en ms
			matchDuration = config.getInt(ConfigInfoRobot.TEMPS_MATCH)*1000;
			this.usingJumper=config.getBoolean(ConfigInfoRobot.ATTENTE_JUMPER);
		}
		catch(Exception e)
		{
			log.warning(e);
		}
	}
}
