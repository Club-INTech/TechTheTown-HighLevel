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

package utils;

import container.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintStream;
import java.util.Calendar;
import java.util.GregorianCalendar;

import enums.ConfigInfoRobot;
import pfg.config.Config;

/**
 * Service de log, affiche à l'écran et enregistre dans des fichiers de logs des informations avec différents niveaux de couleurs.
 *
 * @author pf
 */

public class Log implements Service
{
	/** fichier de configuration pour le match. */
	private Config config;

	/** Redirecteur de chaine de caractères vers le fichier de log. */
	private BufferedWriter writer = null;

	/** Préfixe donnant la couleur en console des messages de debug */
	private String debugPrefix 	= "Dbg - \u001B[32m";

	/** Préfixe donnant la couleur en console des messages de warning */
	private String warningPrefix = "Warn - \u001B[30m";

	/** Préfixe donnant la couleur en console des messages critiques */
	private String criticalPrefix = "Critical - \u001B[31m";

	/** Affixe resettant la couleur actuelle */
	// Actuellement, on ne met rien, mais on est censé reset la couleur avec \u001B[0m a la fin du messsage.
	// voir http://stackoverflow.com/questions/5762491/how-to-print-color-in-console-using-system-out-println
	private String resetColor = "\u001B[0m";

	/** Vrai s'il faut afficher les messages sur la sortie standard (prend du temps CPU), faux sinon. */
	private boolean printLogs = true;

	/** Vrai s'il faut sauvegarder les logs dans un fichier. */
	private boolean saveLogs = true;

	/** Nom du fichier dans lequel on sauvegarde les logs */
	private String saveFile;

	/** Nom du fichier dans lequel on copie les logs sauvegardés à la fin du script */
	private String finalSaveFile;

	private static boolean stop = false;


	/**
	 * Instancie un nouvveau service de log
	 *
	 * @param config fichier de configuration pour le match.
	 */
	private Log(Config config)
	{
		this.config = config;
		updateConfig();

		// crée le fichier de log si on spécifie d'écrire dans un fichcier les logs du robot
		if(saveLogs)
			try
			{
				java.util.GregorianCalendar calendar = new GregorianCalendar();
				String heure = calendar.get(Calendar.HOUR)+":"+calendar.get(Calendar.MINUTE)+":"+calendar.get(Calendar.SECOND);
				File testRepertoire = new File("/tmp/logs");
				File testFinalRepertoire = new File("./logs");
				//La auvegarde dans le dossier logs du projet est faite dans le ThreadEth
				this.saveFile="/tmp/logs/LOG-"+heure+".txt";
				this.finalSaveFile="./logs/LOG-"+heure+".txt";
				if(!testRepertoire.exists())
					testRepertoire.mkdir();
				if(!testFinalRepertoire.exists())
					testFinalRepertoire.mkdir();
				writer = new BufferedWriter(new FileWriter(saveFile, true));
			}
			catch(Exception e)
			{
				e.printStackTrace();
				critical(e);
			}
		debug("Service de log démarré");

	}

	/**
	 * Méthode à appeler uniquement depuis une méthode statique.
	 *
	 * @param message the message
	 */
	public void appel_static(String message)
	{
		writeToLog("AppelStatic: "+message, debugPrefix, System.out);
	}


	/**
	 * Affichage de debug, en vert. User-friendly
	 *
	 * @param message message a logguer
	 */
	public void debug(Object message)
	{
		writeToLog(message.toString(), debugPrefix, System.out);
	}

	/**
	 * Affichage de debug, en vert.
	 *
	 * @param message message a logguer
	 */
	public void debug(String message)
	{
		writeToLog(message, debugPrefix, System.out);
	}

	/**
	 * Affiche une exception et sa stack trace
	 *
	 * @param exception exception à logger
	 */
	public void logException(Exception exception){
		String className = exception.getClass().toString();
		StackTraceElement[] stackTraceElements = exception.getStackTrace();
		StringBuilder toLog = new StringBuilder();
		toLog.append(className);
		for (StackTraceElement stackTraceElement : stackTraceElements){
			toLog.append(stackTraceElement.toString());
			toLog.append("\r\n");
		}
		writeToLog(toLog.toString(), criticalPrefix, System.out);
	}

	/**
	 * Affichage de warnings, en orange. User-friendly
	 *
	 * @param message message a logguer
	 */
	public void warning(Object message)
	{
		writeToLog(message.toString(), warningPrefix, System.out);
	}

	/**
	 * Affichage de warnings, en orange.
	 *
	 * @param message message a logguer
	 */
	public void warning(String message)
	{
		writeToLog(message, warningPrefix, System.out);
	}

	/**
	 * Affichage d'erreurs critiques, en rouge. User-friendly
	 *
	 * @param message message a logguer
	 */
	public void critical(Object message)
	{
		writeToLog(message.toString(), criticalPrefix, System.out);
	}

	/**
	 * Affichage d'erreurs critiques, en rouge.
	 *
	 * @param message message a logguer
	 */
	public void critical(String message)
	{
		writeToLog(message, criticalPrefix, System.err);
	}

	/**
	 * loggue pour de vrai le massage.
	 * Après appele de cette méthode, le message été loggué en fonction de la configuration.
	 *
	 * @param message message a logguer
	 * @param prefix le préfixe a rajouter, avant que l'heure ne soit mise
	 * @param logPrinter ou afficher sur l'écran le log
	 */
	private void writeToLog(String message, String prefix, PrintStream logPrinter)
	{
		// trouve l'heure pour la rajouter dans le message de log
		java.util.GregorianCalendar calendar = new GregorianCalendar();
		String heure = calendar.get(Calendar.HOUR_OF_DAY)+"h"+calendar.get(Calendar.MINUTE)+":"+calendar.get(Calendar.SECOND)+","+calendar.get(Calendar.MILLISECOND);

		if((prefix.equals(debugPrefix) || printLogs) && !Log.stop)
		{
			// Le log doit toujours afficher la methode qu'il l'a appelé ; ici on s'arrange pour qu'il y ait toujours 3 methodes
			// entre log.debug et getStackTrace (qui stocke les méthodes appelées sous forme de pile)
			StackTraceElement elem = Thread.currentThread().getStackTrace()[3];
			logPrinter.println(heure+" "+elem.getClassName()+"."+elem.getMethodName()+":"+elem.getLineNumber()+" > "+message+resetColor);
		}
		if(saveLogs && !Log.stop)
			writeToFile(prefix+heure+" "+message+resetColor); // suffixe en \u001B[0m pour que la prochiane ligne soit blanche si on ne spécifie rien
	}

	/**
	 * Ecrit le message spécifié dans le fichier de log
	 *
	 * @param message le message a logguer
	 */
	private void writeToFile(String message)
	{
		// chaque message sur sa propre ligne
		message += "\n";
		try
		{
			writer.write(message);
			writer.flush();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Sorte de destructeur, dans lequel le fichier est sauvegardé.
	 * // TODO: refuser les demande d'écriture sur le log si l'initialisation n'a pas été faite, ou si le destructeur a été appellé 
	 */
	public void close()
	{
		warning("Fin du log");

		if(saveLogs)
			try {
				debug("Sauvegarde du fichier de logs");
				if(writer != null)
					writer.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
	}

	/* (non-Javadoc)
	 * @see container.Service#updateConfig()
	 */
	@Override
	public void updateConfig()
	{
		try {
			// vérifie s'il faut afficher les logs a l'écran
			printLogs = config.getBoolean(ConfigInfoRobot.PRINT_LOG);
		}
		catch(Exception e) {
			e.printStackTrace();
			critical(e);
		}
		try {
			// vérifie s'il faut écrire les logs dans un fichier
			saveLogs = config.getBoolean(ConfigInfoRobot.SAVE_LOG);
			// TODO: mettre ici ouverture/fermeture de fichier si la valeur de sauvegarde_fichier change
		}
		catch(Exception e) {
			e.printStackTrace();
			critical(e);
		}
	}

	/**
	 * Arrête les logs
	 */
	public static void stop()
	{
		stop = true;
	}

	public String getSavePath(){
		return this.saveFile;
	}

	public String getFinalSavePath(){
		return this.finalSaveFile;
	}

}
