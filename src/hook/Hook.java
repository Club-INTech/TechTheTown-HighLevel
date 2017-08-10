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

package hook;

import strategie.GameState;
import utils.Config;
import utils.Log;

import java.util.ArrayList;

/**
 * Classe-mère abstraite des hooks, utilisés pour la programmation évènementielle.
 * Les hooks permettent via leur callback de déclencher une action lors d'un mouvement du robot sous certaines conditions de position ou d'état de jeu.
 * @author pf
 *
 * TODO A déplacer en LL
 */

abstract public class Hook
{
	/** Liste de callback du hook*/
	protected ArrayList <Callback> callbacks = new ArrayList<Callback>();

	/** Système de log sur lequel écrire */
	protected Log log;
	
	/** endroit ou lire la configuration du robot */
	@SuppressWarnings("unused")
	private Config config;
	
	/** Etat du jeu sur lequel on vérifie si le hook se déclenche ou non */
	protected GameState mState;

	/**
	 *  ce constructeur ne sera appellé que par les constructeurs des classes filles (des hooks bien précis dans le package hook.types)  
	 * @param config endroit où lire la configuration du robot 
	 * @param log Système de log sur lequel écrire
	 * @param gameState Etat du jeu sur lequel on vérifie si le hook se déclenche ou non
	 */
	public Hook(Config config, Log log, GameState gameState)
	{
		this.config = config;
		this.log = log;
		this.mState = gameState;
	}
	
	/**
	 * On peut ajouter un callback à un hook.
	 * Il n'y a pas de méthode pour en retirer, car il n'y en a a priori pas besoin
	 * @param callback le callback que l'on souhaite ajouter au hook 
	 */
	public void addCallback(Callback callback)
	{
		callbacks.add(callback);
	}
	
	/**
	 * Déclenche le hook en exécutant tous ses callbacks.
	 * @return true si ce hook modifie les déplacements du robot
	 */
	protected boolean trigger()
	{
		boolean retour = false;
		
		for(Callback callback : callbacks)
			retour |= callback.call();
		return retour;
	}

	/**
	 * Méthode qui sera surchargée par les classes filles.
	 * Elle contient la condition d'appel du hook et de ses callbacks.
	 * @return true si ce hook modifie les déplacements du robot, false sinon
	 */
	public abstract boolean evaluate();
	
	/**
	 * On peut supprimer le hook s'il n'y a plus aucun callback déclenchable.
	 * @return vrai si le hook est supprimable
	 */
	public boolean canBeDeleted()
	{
	    for(Callback c: callbacks)
	        if(!c.shouldBeDeleted())
	            return false;
	    return true;
	}

}