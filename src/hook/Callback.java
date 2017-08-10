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

/**
 * Classe de callback qui possède la fonction que le robot doit effectuer pendant son déplacement.
 * @author pf
 */
public class Callback
{

	/** L'évènement a-t-il été réalisé ? */
	private boolean isDone = false;
	
	/** L'évènement ne doit-t-il survenir qu'une unique fois ? */
	private boolean isUnique;
	
	/** le code à éxecuter lors de l'évènement */
	public Executable mExecutable;
	
	private GameState stateToConsider = null;

	/**
	 * Constructeur d'un callback avec 2 paramètres
	 * @param methode la méthode à exécuter
	 * @param unique si l'exécution est unique
	 * @param stateToConsider 
	 */
	public Callback(Executable methode, boolean unique, GameState stateToConsider)
	{
		this.mExecutable = methode;
		this.isUnique = unique;
		this.stateToConsider = stateToConsider;
	}
	
	/**
	 * Le callback appelle la méthode, si elle n'est pas unique ou si elle n'est pas déjà faite
	 * @return vrai si le robot a été déplacé/ tourné, faux sinon
	 */
	public boolean call()
	{
		if(!(shouldBeDeleted()))
		{
            isDone = true;
			return mExecutable.execute(stateToConsider);
		}
		return false;
	}
	
	/**
	 * Explique si le Callback devrait être détruit
	 * @return true si le Callback devrait être détruit
	 */
	public boolean shouldBeDeleted()
	{
	    return isUnique && isDone;
	}
}
