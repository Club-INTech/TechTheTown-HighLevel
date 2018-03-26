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

import pathfinder.Noeud;
import smartMath.Circle;
import smartMath.Geometry;
import smartMath.Segment;
import smartMath.Vec2;

/**
 * Obstacle de forme circulaire.
 *
 * @author pf,
 */
public class ObstacleCircular extends Obstacle
{
	/** rayon en mm de cet obstacle */
	protected Circle circle;
	
	/**
	 * crée un nouvel obstacle de forme circulaire a la position et a la taille spécifiée.
	 * @param circle le cercle représentant l'obstacle
	 */
	public ObstacleCircular(Circle circle)
	{
		super(circle.getCenter());
		this.circle=circle;
	}

	/* (non-Javadoc)
	 * @see table.obstacles.Obstacle#clone()
	 */
	public ObstacleCircular clone()
	{
		return new ObstacleCircular(circle);
	}
	
	/**
	 * Verifie si a == b pour des obstacles circulaires
	 * @param otherObstacle b
	 * @return true si a == b
	 */
	public boolean equals(ObstacleCircular otherObstacle) 
	{
		return (
				this.circle == otherObstacle.circle
			&&  this.position.equals(otherObstacle.position)	
				);
	}

	/**
	 * Copie this dans other, sans modifier this
	 *
	 * @param other l'obstacle circulaire a modifier
	 */
	public void clone(ObstacleCircular other)
	{
		other.position = position;
		other.circle = circle;
	}

	/**
	 * Vérifie si le point donné est dans l'obstacle
	 * @param point le point à tester
	 */
	@Override
	public boolean isInObstacle(Vec2 point)
	{
		return ((Segment.squaredLength(point, position) < circle.getRadius()*circle.getRadius()));
	}
	@Override
	public boolean intersects(Segment segment){
		return Geometry.intersects(segment, this.getCircle());
	}

	/**
	 * Donne le rayon de cet obstacle circulaire.
	 *
	 * @return le rayon de cet obstacle circulaire.
	 */
	public int getRadius()
	{
		return (int)circle.getRadius();
	}

	public void setRadius(int radius)
	{
		this.circle.setRadius((double)radius);
	}
	
	public Circle getCircle(){
		return this.circle;
	}

	public String toString()
	{
		return "Obstacle circulaire de centre " + position + " et de rayon: " + circle.getRadius();
	}
	
	public void printObstacleMemory()
	{
		System.out.println("Obstacle en memoire");
	}


}
