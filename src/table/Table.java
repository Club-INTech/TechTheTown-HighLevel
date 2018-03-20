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

package table;


import container.Service;
import enums.ConfigInfoRobot;
import pfg.config.Config;
import smartMath.Vec2;
import table.obstacles.ObstacleManager;
import utils.Log;

/* Positions :
 * 			_______________________________________________________
 * 			|-1500,2000         	0,2000		         1500,2000|
 * 			|           		      							  |
 * 			|           		     							  |
 * 			|           	 		  							  |
 * 			|           	 		  							  |
 * 			|           	 		  							  |
 * 			|-1500,0           		 0,0       				1500,0|
 *          -------------------------------------------------------
 *          
 * (0,0) = entre les deux zones de départ
 * (0,2000) = Stations de récupération
 * (0,0) = Pattern de couleurs
 */

/**
 * Stocke toutes les informations liées à la table (muables et immuables) au cours d'un match.
 * @author Discord
 */
public class Table implements Service
{
	/** Le gestionnaire d'obstacle. */
	private ObstacleManager mObstacleManager;

	/** système de log sur lequel écrire. */
	private Log log;

	/** endroit ou lire la configuration du robot */
	private Config config;

	/** coté du robot */
	private boolean symetry = false;

	//TODO : définir les éléments de jeu de la table

	// Au besoin, créer les classes nécessaires dans le package table

	/** point de départ du match à modifier a chaque base roulante */
	public static Vec2 entryPosition = new Vec2(1269, 455);
	public static double entryOrientation = Math.PI;

	/**
	 * Instancie une nouvelle table
	 *
	 * @param log le système de log sur lequel écrire.
	 * @param config l'endroit ou lire la configuration du robot
	 */
	public Table(Log log, Config config)
	{
		this.log = log;
		this.config = config;
		this.mObstacleManager = new ObstacleManager(log, config);
		initialise();
	}

	public void initialise() // initialise la table du debut du jeu
	{
		if(symetry)
		{
			entryPosition = new Vec2(560, 176);
		}
		// TODO : initialiser les éléments de jeu définis plus haut
	}

	public ObstacleManager getObstacleManager()
	{
		return mObstacleManager;
	}

	/* (non-Javadoc)
	 * @see container.Service#updateConfig()
	 */
	@Override
	public void updateConfig()
	{
		symetry = (config.getString(ConfigInfoRobot.COULEUR).equals("orange"));
		// TODO update config
	}

	public Config getConfig() {
		return config;
	}
}

