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

package table.obstacles;
import smartMath.Segment;
import smartMath.Vect;

/**
 * classe abstraite pour les obstacles sur la table.
 * Les obstacles peuvent avoir différentes formes, et être soit fixes d'un match a l'autre, soit mobiles (un robot adverse est par exemple un obstacle mobile)
 * @author pf,
 *
 */
public abstract class Obstacle
{
	/** Position de l'obstacle sur la table. En fonction de la forme de l'obstacle, il peut s'étendre plus ou moins loin de cette position dans diverses directions */
	protected Vect position;

	/**
	 * construit un nouvel obstacle à une position donnée
	 * @param position position de l'obstacle à construire
	 */
	public Obstacle (Vect position)
	{
		this.position = position.clone();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public abstract Obstacle clone();

	/**
	 * Renvoie la position de cet obstacle.
	 * @return the position
	 */
	public Vect getPosition()
	{
		return this.position;
	}
	
	/** Change la position de l'obstacle
	 * @param position la nouvelle position de l'obstacle
	 */
	public void setPosition(Vect position)
	{
		this.position = position.clone();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return "Obstacle en "+position;
	}

	/** Renvoie vrai si le vecteur est dans l'obstacle
	 * @param vec le vecteur à tester
	 */
	public abstract boolean isInObstacle(Vect vec);

	/**
	 * Cette méthode teste si un segment est en intersection avec les obstacles
	 * @param segment
	 * @return
	 */
	public abstract boolean intersects(Segment segment);
	
}
