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

package smartMath;

/**
 * segment, coordonnées double
 * @author Etienne
 *
 */
public class Segment
{
	/** Premier pont du segment*/
	private Vec2 mPointA;
	
	/** Second point du segment*/
	private Vec2 mPointB;
	
	/** Construit le segment à partir de ses points extrêmes
	 * @param pointA premier point
	 * @param pointB second point
	 */
	public Segment(Vec2 pointA, Vec2 pointB)
	{
		mPointA = pointA;
		mPointB = pointB;
	}

	/**
	 * Donne le segment sous forme d'un vecteur
	 * @return le vecteur !
	 */
	public Vec2 getVector(){
		return mPointB.minusNewVector(mPointA);
	}

	/**
	 * @return le premier point
	 */
	public Vec2 getA()
	{
		return mPointA;
	}
	
	/**
	 * @return le second point
	 */
	public Vec2 getB()
	{
		return mPointB;
	}
	
	/**
	 * @param pointA le nouveau point initial
	 */
	public void setA(Vec2 pointA)
	{
		mPointA = pointA;
	}
	
	/**
	 * @param pointB le nouveau point final
	 */
	public void setB(Vec2 pointB)
	{
		mPointB = pointB;
	}

	/**
	 * Renvoie la distance au carré entre deux points Vec2
	 * @param pointA point 1
	 * @param pointB point 2
     */
	public static double squaredLength(Vec2 pointA, Vec2 pointB)
	{
		return (pointB.getX() - pointA.getX())*(pointB.getX() - pointA.getX()) + (pointB.getY() - pointA.getY())*(pointB.getY() - pointA.getY());

	}

	/**
	 * Cette méthode retourne le vecteur directeur du segment
	 */
	public Vec2 vecdirecteur(){
		int a;
		int b;
		int xA=this.getA().getX();
		int xB=this.getB().getX();
		int yA=this.getA().getY();
		int yB=this.getB().getY();
		//Il s'agit d'une droite parallèle à l'axe des ordonnées, la valeur de a peut être qcq
		if(xA==xB){
			b=xA;
			a=1;
			return new Vec2(b,a);
		}
		else{
			//Il s'agit d'une droite parallèle à l'axe des abcisses
			if(yA==yB){
				a=0;
				b=yA;
				return new Vec2(a,b);
			}
			//Cas général
			else{
				a=(yB-yA)/(xB-xA);
				b=(yA*xB-xA*yB)/(xB-xA);
				return new Vec2(-b,a);
			}
		}
	}

}
