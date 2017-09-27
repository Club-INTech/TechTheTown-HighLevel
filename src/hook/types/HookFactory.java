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

package hook.types;

import container.Service;
import enums.ConfigInfoRobot;
import exceptions.ConfigPropertyNotFoundException;
import hook.Hook;
import pfg.config.Config;
import smartMath.Vec2;
import strategie.GameState;
import utils.Log;

/**
 * Service fabriquant des hooks à la demande.
 * Chaque script appelle la hookfactory pour créer ses hooks.
 * @author pf , Pingu
 */
public class HookFactory implements Service
{
	
	/**  endroit ou lire la configuration du robot. */
	private Config config;

	/**  système de log a utiliser. */
	private Log log;
	
	/**  robot a surveiller pour le déclenchement des hooks. */
	private GameState realState;
	
	/** spécifie de quelle couleur est le robot. Uniquement donné par le fichier de config. */ // TODO: en faire une enum
	String color;
	
	/**
	 *  appellé uniquement par Container.
	 *  Initialise la factory de hooks.
	 * @param config fichier de config du match
	 * @param log système de log
	 * @param realState état du jeu
	 */
	private HookFactory(Config config, Log log, GameState realState)
	{
		this.config = config;
		this.log = log;
		this.realState = realState;
		updateConfig();
	}

	/*
	 * (non-Javadoc)
	 * @see container.Service#updateConfig()
	 */
	public void updateConfig()
	{
		color = config.getString(ConfigInfoRobot.COULEUR);
	}
	
	//TODO Ajouter les nouveaux hooks
	
	/* ======================================================================
	 * 							Hooks de position
	 * ======================================================================
	 */
	
	/* ======================================================================
	 * 							Hooks d'abscisse (sur X)
	 * ======================================================================
	 */

	/* ======================================================================
	 * 							Hook d'ordonnée (sur Y)
	 * ======================================================================
	 */
    
    /* ======================================================================
   	 * 							Hooks de position et orientation
   	 * ======================================================================
   	 */
    
	/**
	 * Hook déclenché pour une position et orientation données
	 * @param position : la position
	 * @param orientation : l'orientation
	 * @param tolerancyPos : la tolérance sur la position
	 * @param tolerancyOr : la tolérance sur l'orientation
	 * @return le hook souhaité
	 */
    public Hook newPositionHook(Vec2 position, float orientation, float tolerancyPos, float tolerancyOr)
	{
		return new HookIsPositionAndOrientationCorrect(config, log, realState, position, orientation, tolerancyPos, tolerancyOr);
	}
}
