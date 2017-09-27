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

import container.Service;
import pfg.config.Config;
import utils.Log;

/**
 * Classe abstraite des threads.
 * Elle héreite des threads java
 *
 * @author pf
 */

public abstract class AbstractThread extends Thread implements Service
{

	/** Fichier ou lire la configuration du match. Ce sera le même pour tout les threads, et est directement rempli par le ThreadManager */
	protected static Config config;
	
	/** Le système de log a utiliser pour écrire. Ce sera le même pour tout les threads, et est directement rempli par le ThreadManager */
	protected static Log log;

	/** Commande d'arrêt des Threads: si ce boolée passe a true, les threads autres que main vont terminer leur exécution */
	protected static boolean stopThreads = false;
	
	/**
	 * Crée un nouveau Thread abstrait.
	 *
	 * @param config Fichier ou lire la configuration du match.
	 * @param log Le système de log a utiliser pour écrire.
	 */
	protected AbstractThread(Config config, Log log)
	{
		AbstractThread.config = config;
		AbstractThread.log = log;
	}

	/**
	 * Crée un nouveau Thread abstrait.
	 * C'est ce constructeur qui sera appellé par les classes héritant de AbstractThread 
	 */
	protected AbstractThread()
	{		
	}

	/* (non-Javadoc)
	 * @see container.Service#updateConfig()
	 */
	public void updateConfig()
	{
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public abstract void run();

}

