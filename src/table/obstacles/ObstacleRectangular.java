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
import smartMath.Vec2;

import java.awt.*;
import java.util.ArrayList;

/**
 * Obstacle rectangulaire sont les bords sont alignés avec les axes X et Y (pas de possibilité de faire un rectangle en biais).
 * 
 * @author pf,
 */
public class ObstacleRectangular extends Obstacle
{
	/** taille du rectangle en mm selon l'axe X */
	protected int sizeX;

	/** taille du rectangle en mm selon l'axe Y */
	protected int sizeY;

	/**
	 *	crée un nouvel obstacle rectangulaire sur la table a la position désirée.
	 *
	 * @param position Positon désirée du centre du rectangle représentant l'obstacle (intersection des 2 diagonales)
	 * @param sizeX taille voulue du rectangle représentant l'obstacle en mm selon l'axe X
	 * @param sizeY taille voulue du rectangle représentant l'obstacle en mm selon l'axe Y
	 */
	public ObstacleRectangular(Vec2 position, int sizeX, int sizeY)
	{
		super(position);
		this.sizeY = sizeY;
		this.sizeX = sizeX;
	}

	/* (non-Javadoc)
	 * @see table.obstacles.Obstacle#clone()
	 */
	public ObstacleRectangular clone()
	{
		return new ObstacleRectangular(position.clone(), sizeX, sizeY);
	}

	/* (non-Javadoc)
	 * @see table.obstacles.Obstacle#toString()
	 */
	public String toString()
	{
		return "ObstacleRectangulaire";
	}

	/**
	 * Renvoit la taille du rectangle en mm selon l'axe Y
	 *
	 * @return the size y
	 */
	public int getSizeY()
	{
		return this.sizeY;
	}

	/**
	 *  Renvoit la taille du rectangle en mm selon l'axe X
	 *
	 * @return the size x
	 */
	public int getSizeX()
	{
		return this.sizeX;
	}

	/**
	 * Renvoie les Segments des diagonales du rectangle
	 */
	public ArrayList<Segment> getDiagos()
	{
		ArrayList<Segment> segments = new ArrayList<Segment>();
		segments.add(new Segment(new Vec2(position.getX() + sizeX/2 , position.getY() + sizeY/2), new Vec2(position.getX() - sizeX/2 , position.getY() - sizeY/2)));
		segments.add(new Segment(new Vec2(position.getX() + sizeX/2 , position.getY() - sizeY/2), new Vec2(position.getX() - sizeX/2 , position.getY() + sizeY/2)));

		return segments;
	}

	/**
	 * Vérifie si le point donné est dans l'obstacle
	 * @param point le point à tester
	 */
	@Override
	public boolean isInObstacle(Vec2 point)
	{
		return point.getX() <= position.getX() + (sizeX / 2)
				&& point.getX() >= position.getX() - (sizeX / 2)
				&& point.getY() <= position.getY() + (sizeY / 2)
				&& point.getY() >= position.getY() - (sizeY / 2);
	}

	/**
	 * Fourni la plus petite distance entre le point fourni et l'obstacle.
	 *
	 * @param point point a considérer
	 * @return la plus petite distance entre le point fourni et l'obstacle.
	 */
	public float distance(Vec2 point)
	{
		return (float) Math.sqrt(SquaredDistance(point));
	}

	/**
	 * Fourni la plus petite distance au carré entre le point fourni et l'obstacle.
	 *
	 * @param in  point a considérer
	 * @return la plus petite distance au carré entre le point fourni et l'obstacle
	 */
	public float SquaredDistance(Vec2 in)
	{
		
		/*		
		 *  Schéma de la situation :
		 *
		 * 		 												  y
		 * 			4	|		3		|		2					    ^
		 * 				|				|								|
		 * 		____________________________________				    |
		 * 				|				|								-----> x
		 * 				|				|
		 * 			5	|	obstacle	|		1
		 * 				|				|
		 * 		____________________________________
		 * 		
		 * 			6	|		7		|		8
		 * 				|				|
		 */

		// calcul des positions des coins
		Vec2 coinBasGauche = position.plusNewVector((new Vec2(0,-sizeY)));
		Vec2 coinHautGauche = position.plusNewVector((new Vec2(0,0)));
		Vec2 coinBasDroite = position.plusNewVector((new Vec2(sizeX,-sizeY)));
		Vec2 coinHautDroite = position.plusNewVector((new Vec2(sizeX,0)));

		// si le point fourni est dans les quarts-de-plans n°2,4,6 ou 8
		if(in.getX() < coinBasGauche.getX() && in.getY() < coinBasGauche.getY())
			return in.squaredDistance(coinBasGauche);

		else if(in.getX() < coinHautGauche.getX() && in.getY() > coinHautGauche.getY())
			return in.squaredDistance(coinHautGauche);

		else if(in.getX() > coinBasDroite.getX() && in.getY() < coinBasDroite.getY())
			return in.squaredDistance(coinBasDroite);

		else if(in.getX() > coinHautDroite.getX() && in.getY() > coinHautDroite.getY())
			return in.squaredDistance(coinHautDroite);

		// Si le point fourni est dans les demi-bandes n°1,3,5,ou 7
		if(in.getX() > coinHautDroite.getX())
			return (in.getX() - coinHautDroite.getX())*(in.getX() - coinHautDroite.getX());

		else if(in.getX() < coinBasGauche.getX())
			return (in.getX() - coinBasGauche.getX())*(in.getX() - coinBasGauche.getX());

		else if(in.getY() > coinHautDroite.getY())
			return (in.getY() - coinHautDroite.getY())*(in.getY() - coinHautDroite.getY());

		else if(in.getY() < coinBasGauche.getY())
			return (in.getY() - coinBasGauche.getY())*(in.getY() - coinBasGauche.getY());

		// Sinon, on est dans l'obstacle
		return 0f;
	}

	public void changeDim(int sizeX, int sizeY)
	{
		this.sizeX=sizeX;
		this.sizeY=sizeY;
	}

	/**
	 * Renvoie le point du rectangle le plus proche d'un point dans le rectangle
	 * @param inObstacle
	 * @return le point du rectangle le plus proche de inObstacle
	 */
	public Vec2 pointProche (Vec2 inObstacle){
		Vec2 ref = inObstacle.minusNewVector(position);
		int min = Math.min(sizeX-Math.abs(ref.getX()), sizeY-Math.abs(ref.getY()));
		if (ref.getX()>0 && ref.getY()>0){
			if (min == sizeX-Math.abs((ref.getX()))){
				ref.setX(sizeX/2+1);
				return ref.plusNewVector(position);
			}
			else{
				ref.setY(sizeY/2+1);
				return ref.plusNewVector(position);
			}
		}
		else if(ref.getX()>0 && ref.getY()<0){
			if (min == sizeX-Math.abs((ref.getX()))){
				ref.setX(sizeX/2+1);
				return ref.plusNewVector(position);
			}
			else{
				ref.setY(-sizeY/2-1);
				return ref.plusNewVector(position);
			}
		}
		else if(ref.getX()<0 && ref.getY()<0){
			if (min == sizeX-Math.abs((ref.getX()))){
				ref.setX(-sizeX/2-1);
				return ref.plusNewVector(position);
			}
			else{
				ref.setY(-sizeY/2-1);
				return ref.plusNewVector(position);
			}
		}
		else if(ref.getX()<0 && ref.getY()>0){
			if (min == sizeX-Math.abs((ref.getX()))){
				ref.setX(-sizeX/2-1);
				return ref.plusNewVector(position);
			}
			else{
				ref.setY(sizeY/2+1);
				return ref.plusNewVector(position);
			}
		}
		return new Vec2();
	}

}
