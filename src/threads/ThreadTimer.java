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

import exceptions.serial.SerialConnexionException;
import robot.Robot;
import robot.SerialWrapper;
import table.Table;
import threads.dataHandlers.ThreadSerial;
import utils.Config;
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
	/** La table sur laquelle le thread doit croire évoluer */
	private Table table;
	
	private Robot robot;

	/** La carte avec laquelle on doit communiquer */
	private SerialWrapper serialWrapper;
	
	/** vrai si le match a effectivment démarré, faux sinon */
	public static boolean matchStarted = false;

	/** vrai si le match a effectivment pris fin, faux sinon */
	public static boolean matchEnded = false;
	
	/** Date de début du match. */
	public static long matchStartTimestamp;
	
	/** Durée en miliseconde d'un match recupéré de la config */
	public static long matchDuration = 90000;
	
	/** Temps en ms qui s'écoule entre deux mise a jour de la liste des obstacle périmables. Lors de chaque mise a jour, les obstacles périmés sont détruits. */
	public static int obstacleRefreshInterval = 100;
	
	/** interface graphique d'affichage de la table, pour le debug */
	//public Window window;
	
	/**
	 * indique si l'interface graphique est activée ou non 
	 */
	private boolean isGraphicalInterfaceEnabled = false;

    private BufferedWriter out;
	
	
	/**
	 * Crée le thread timer.-
	 *
	 * @param table La table sur laquelle le thread doit croire évoluer
	 * @param locomotionCardWrapper La carte d'asservissement avec laquelle on doit communiquer
	 */

	
	ThreadTimer(Config config, Log log, Table table, Robot robot, SerialWrapper locomotionCardWrapper)
	{
		super(config, log);

		this.table = table;
		this.serialWrapper = locomotionCardWrapper;
		this.robot=robot;
		
		updateConfig();
		Thread.currentThread().setPriority(10);

		// DEBUG: interface graphique
		/*try
		{
			window = new Window(table, robot);
		}
		catch (Exception e)
		{
			isGraphicalInterfaceEnabled = false;
			log.debug("Affichage graphique non disponible");
		}*/
	}

	/* (non-Javadoc)
	 * @see threads.AbstractThread#run()
	 */
	@Override
	public void run()
	{
		log.debug("Lancement du thread timer");

		// on eteind les capteursgetObstacleManager
		//config.set("capteurs_on", "true");
		serialWrapper.updateConfig();

        try
        {
            File file = new File("pos.txt");
            if (!file.exists()) {
                //file.delete();
                file.createNewFile();
            }
            out = new BufferedWriter(new FileWriter(file));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Attente du démarrage du match
		
		// attends que le jumper soit retiré du robot

		while(serialWrapper.isJumperAbsent())
		{
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		while(!serialWrapper.isJumperAbsent())
		{
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// maintenant que le jumper est retiré, le match a commencé
		matchStarted = true;
		
		//log.debug(!serialWrapper.isJumperAbsent() +" / "+ !matchStarted);

		// Le match démarre ! On chage l'état du thread pour refléter ce changement
		matchStartTimestamp = System.currentTimeMillis();
		log.critical("Jumper Enlevé");

		matchStarted = true;

		// config.set("capteurs_on", "true");
		serialWrapper.updateConfig();

		log.debug("LE MATCH COMMENCE !");
        long ddm = System.currentTimeMillis();

        // boucle principale, celle qui dure tout le match
		while(System.currentTimeMillis() - matchStartTimestamp < matchDuration)
		{

			if(stopThreads)
			{
				// ons 'arrète si le ThreadManager le demande
				log.debug("Arrêt du thread timer demandé durant le match");
				return;
			}

            if((System.currentTimeMillis()-ddm) >=12)
            {
               // log.debug("lol, i'm noob");
                try {
                    out.write(Integer.toString(robot.getPositionFast().getX()));
                    out.write("\t");
                    out.write(Integer.toString(robot.getPositionFast().getY()));
                    out.newLine();
                    out.flush();
                    ddm = System.currentTimeMillis();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
			
			//on rafraichit l'interface graphique de la table
			/*if(isGraphicalInterfaceEnabled/* && window != null)
			{
				window.getPanel().repaint();
				
				window.getPanel().drawArrayList(robot.cheminSuivi);
			}*/
			//else
			//	System.out.println("damn");
				
			
			try
			{
				Thread.sleep(obstacleRefreshInterval);
			}
			catch(Exception e)
			{
				log.warning(e.toString());
			}
		}
		log.debug("Fin des "+matchDuration+" ms de match, temps : "+(System.currentTimeMillis() - matchStartTimestamp));


		// actions de fin de match
		onMatchEnded();
		
		log.debug("Fin du thread timer");
		
	}
	
	/**
	 * On match ended.
	 */
	private void onMatchEnded()
	{
 
		log.debug("Fin du Match car fin des 90s !");

		// Le match est fini, immobilisation du robot
		matchEnded = true;

		try
		{
            serialWrapper.immobilise();
		} catch (SerialConnexionException e) {
			log.debug( e.logStack());
		}

		// fin du match : on eteint la STM
		try 
		{
            serialWrapper.disableRotationnalFeedbackLoop();
            serialWrapper.disableTranslationnalFeedbackLoop();
            serialWrapper.disableSpeedFeedbackLoop();

			//mLocomotionCardWrapper.shutdownSTM();
			Log.stop();
			ThreadSerial.shutdown = true;
		}
		catch (SerialConnexionException e)
		{
			log.critical( e.logStack());
		}
		
		// et on coupe la connexion avec la carte d'asser comme ca on est sur qu'aucune partie du code ne peut faire quoi que ce soit pour faire bouger le robot
        serialWrapper.closeLocomotion();
	}
	
	
	/**
	 * Temps restant avant la fin du match.
	 *
	 * @return le temps restant du match en milisecondes
	 */
	public static long remainingTime()
	{
		return matchStartTimestamp + matchDuration - System.currentTimeMillis();
	}
	

	/**
	 * Temps écoulé depuis le début du match
	 *
	 * @return le temps écoulé du match en milisecondes
	 */
	public static long ellapsedTimeSinceMatchStarted()
	{
		return  System.currentTimeMillis() - matchStartTimestamp;
	}
	
	/* (non-Javadoc)
	 * @see threads.AbstractThread#updateConfig()
	 */
	public void updateConfig()
	{
		// facteur 1000 car temps_match est en secondes et duree_match en ms
		try
		{
			matchDuration =  Integer.parseInt(config.getProperty("temps_match").replaceAll(" ","") ) *1000;
		}
		catch(Exception e)
		{
			log.warning(e);
		}
	}
	
}
