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
import enums.ColorModule;
import exceptions.ConfigPropertyNotFoundException;
import smartMath.Vec2;
import table.obstacles.ObstacleManager;
import utils.Config;
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
 * (0,2000) = Base Lunaire
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
	public Balls ballsCratereDepart;
	public Balls ballsCratereBaseLunaire;
	public Fusee fuseeDepart;
	public Fusee fuseeBase;
	public Cylindre cylindreDevantDepart;
	public Cylindre cylindreCratereDepart;
	public Cylindre cylindreCratereBase;
	public Cylindre cylindreDevantBase;
	public Cylindre cylindrePleinMilieu;

	// Au besoin, créer les classes nécessaires dans le package table

	/** point de départ du match à modifier a chaque base roulante */
	public static Vec2 entryPosition = new Vec2(590, 176);

	/**
	 * Instancie une nouvelle table
	 *
	 * @param log le système de log sur lequel écrire.
	 * @param config l'endroit ou lire la configuration du robot
	 */
	private Table(Log log, Config config)
	{
		this.log = log;
		this.config = config;
		this.mObstacleManager = new ObstacleManager(log, config);
		initialise();
	}

	public void initialise() // initialise la table du debut du jeu
	{
		ballsCratereDepart =new Balls(new Vec2(850, 540));
		ballsCratereBaseLunaire =new Balls(new Vec2(500,1850 ));
		fuseeDepart=new Fusee(new Vec2(350, 40), ColorModule.BLUE);
		fuseeBase=new Fusee(new Vec2(1460, 1350),ColorModule.MULTI);
		cylindreDevantDepart =new Cylindre(new Vec2(500,600),ColorModule.MULTI);
		cylindreCratereDepart =new Cylindre(new Vec2(1300,600),ColorModule.BLUE);
		cylindreCratereBase =new Cylindre(new Vec2(700,1850),ColorModule.BLUE);
		cylindreDevantBase =new Cylindre(new Vec2(600,1400),ColorModule.MULTI);
		cylindrePleinMilieu =new Cylindre(new Vec2(1000,1100),ColorModule.MULTI);

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
		try {
			symetry = config.getProperty("couleur").replaceAll(" ", "").equals("jaune");
		}catch (ConfigPropertyNotFoundException e){
			log.debug("Revoir le code : impossible de trouver la propriété "+e.getPropertyNotFound());
			log.critical( e.logStack());
		}
		// TODO update config
	}
}

