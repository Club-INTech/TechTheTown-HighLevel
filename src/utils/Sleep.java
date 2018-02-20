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

/**
 * Classe qui fournit juste un sleep sans try/catch.
 *
 * @author pf,
 */

public class Sleep
{

	// Constructeur privé car cette classe n'a qu'une méthode statique
	/**
	 * Instantiates a new sleep.
	 */
	private Sleep()
	{
	}
	
	
	/**
	 * Fait attendre le programme.
	 * Attention: Le programme va vraiment attendre ! 
	 * Si on veut juste une estimation du temps que cela va prendre d'attendre ce temps là (sic), utiliser Robot.sleep, qui en fonction de son appel
	 * sur Robot ou Robothrono, déclenchera vraiment une attente ou une simple incrémentation du chronomètre
	 * @param delay durée en ms d'attente
	 */
	public static void sleep(long delay)
	{
		try
		{
			// fait attendre le thread appelant cette méthode
			Thread.sleep(delay);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
	
}
