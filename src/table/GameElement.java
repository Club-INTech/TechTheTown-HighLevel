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

import smartMath.Vec2;

/**
 * Element de jeu
 * @author Discord
 */
abstract class GameElement
{
	
	/** La position de l'élément sur la table */
	protected Vec2 position;
	
	/** Booléen qui précise si l'élement est dans le robot ou non */
	public boolean inRobot;
	
	/**
	 * Crée un nouvel élément à l'endroit de la table spécifié
	 *
	 * @param position position à laquelle instancier l'élément de jeu
	 */
	public GameElement(Vec2 position)
	{
		this.position = position;
	}
	
	/**
	 * Renvoie la position courante de l'élément de jeu
	 *
	 * @return la position courante de l'élément de jeu
	 */
	public Vec2 getPosition()
	{
		return position;
	}
}
