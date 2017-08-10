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

import pathfinder.Graphe;
import pathfinder.Noeud;
import smartMath.Circle;
import smartMath.Vec2;

import java.util.ArrayList;

/**
 * Obstacles détectés par capteurs de proximité (ultrasons et infrarouges).
 * Ces obstacles sont supposés circulaires
 *
 * @author pf,
 */
public class ObstacleProximity extends ObstacleCircular
{
	/** temps ou l'obstacle sera perime en ms */
	private long mOutDatedTime;
	
	private int lifetime;

	/**
	 * Nombre de fois détécté :
	 * Positif : on l'a vu plusieurs fois
	 * Negatif : si on ne l'a pas detecté alors qu'on aurais dû
	 */
	public int numberOfTimeDetected;

	/*
	 * limite entre obstacle confirmé ou infirmé; x
	 * 
	 * 	    unconf   conf
	 * 	 0 |------|x|-----|y
	 * 
	 */
	private int thresholdConfirmedOrUnconfirmed;
	
	/*
	 *  Maximum d'incrementation de numberOfTimeDetected; y
	 * 
	 * 	    unconf   conf
	 * 	 0 |------|x|-----|y
	 * 
	 */
	private int maxNumberOfTimeDetected;

	
	/**
	 * Crée un nouvel obstacle détecté a proximité du robot.
	 * Ces obstacles sont supposés circulaires: on les définit par leur centre et leur rayon
	 * "a proximité du robot" signifie qu'il a été détecté par les capteurs de proximité, mais
	 * dans l'absolu, il n'y a pas de contrainte géométrique de proximité
	 *
	 * @param circle le cercle représentant l'obstacle circulaire
	 * @param lifetime la durée de vie (en ms) de l'objet a créer
	 */
	public ObstacleProximity (Circle circle, int lifetime)
	{
		super(circle);
		
		this.lifetime = lifetime;
		mOutDatedTime = System.currentTimeMillis() + lifetime;// la date de peremption = temps actuel + temps de peremption de l'obstacle
		//TODO mettre dans le fichier de config le "temps de peremption" de chaque obstacle 
		numberOfTimeDetected=1;

		thresholdConfirmedOrUnconfirmed=3;
		maxNumberOfTimeDetected=3;
	}
	
	/* (non-Javadoc)
	 * @see table.obstacles.ObstacleCircular#clone()
	 */
	public ObstacleProximity clone()
	{
		return new ObstacleProximity(circle, lifetime);
	}

	public ArrayList<Noeud> fabriqueNoeud(Graphe graphe, int n, int ecart)
	{
		ArrayList<Noeud> myList = new ArrayList<Noeud>();
		double h=(this.getRadius()+ecart)/Math.cos(Math.PI/n);
		for (int i=0;i<n;i++)
		{

			Vec2 spin=new Vec2((int)(h*Math.cos(2*Math.PI*i/n)), (int) (h*Math.sin(Math.PI*2*i/n)));
			Vec2 po=this.getPosition().plusNewVector(spin);
			if(Math.abs(po.getX())<=1500 && po.getY()<=2000 && po.getY()>=0) {
				Noeud noeudact = new Noeud(graphe, po);
				myList.add(noeudact);
				graphe.getlNoeuds().add(noeudact);
			}
		}
		this.lNoeud=myList;
		//
		return myList;
	}
	
	public long getOutDatedTime()
	{
		return mOutDatedTime;
	}
	
	public int getThresholdConfirmedOrUnconfirmed()
	{
		return thresholdConfirmedOrUnconfirmed;
	}
	
	public int getMaxNumberOfTimeDetected()
	{
		return maxNumberOfTimeDetected;
	}
	
	/**
	 * nouveau du temps de vie pour l'obstacle
	 * @param time le nouveau temps de vie
	 */
	public void setLifeTime(int time) 
	{
		lifetime = time;
		mOutDatedTime = System.currentTimeMillis() + lifetime;
	}
}
