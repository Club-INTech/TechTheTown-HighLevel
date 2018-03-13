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

import java.awt.*;

/**
 * classe de calculs de géométrie
 * @author Etienne, Rem
 */

//TODO simplifier les methodes dégeulasses à l'aide des nouveaux outils
public class Geometry
{
	/**
	 * Calcul le moduloSpec entre -modul et +module
	 * @param number le nombre dont on veut calculer le moduloSpec
	 * @param module le module pour le moduloSpec
	 * @return number [module]
	 */
	public static double moduloSpec(double number, double module)
	{
		number = number%(2*module);
		if (number > module){
			number -= 2*module;
		}else if(number < -module){
			number += 2*module;
		}
		return number;
	}
	
	/**
	 * Détermine si deux segments s'intersectent ou non
	 * @param segment1
	 * @param segment2
	 * @return vrai si il y a intersection entre les deux segments, faux sinon (les extremités ne sont pas comptées comme intersection)
	 */
	public static boolean intersects(Segment segment1, Segment segment2)
	{
		// les points formant les segments 1 et 2 sont A1, B1, A2, B2
		// pour qu'il y ait intersection, il faut :
		// - les segments ne soient pas parallèles : (A1B1)^(A2B2) != 0
		// - le point d'intersection est entre A2 et B2 : (A1B1)^(A1B2) * (A1B1)^(A1A2) < 0
		// - le point d'intersection est entre A1 et B1 : (A2B2)^(A2B1) * (A2B2)^(A2A1) < 0
		// ^ = produit vectoriel

		return ((double)segment1.getB().getX() - (double)segment1.getA().getX()) * ((double)segment2.getB().getY() - (double)segment2.getA().getY()) - ((double)segment1.getB().getY() - (double)segment1.getA().getY()) * ((double)segment2.getB().getX() - (double)segment2.getA().getX()) != 0
				&& (((double)segment1.getB().getX() - (double)segment1.getA().getX()) * ((double)segment2.getB().getY() - (double)segment1.getA().getY()) - ((double)segment1.getB().getY() - (double)segment1.getA().getY()) * ((double)segment2.getB().getX() - (double)segment1.getA().getX())) * (((double)segment1.getB().getX() - (double)segment1.getA().getX()) * ((double)segment2.getA().getY() - (double)segment1.getA().getY()) - ((double)segment1.getB().getY() - (double)segment1.getA().getY()) * ((double)segment2.getA().getX() - (double)segment1.getA().getX())) < 0
				&& (((double)segment2.getB().getX() - (double)segment2.getA().getX()) * ((double)segment1.getB().getY() - (double)segment2.getA().getY()) - ((double)segment2.getB().getY() - (double)segment2.getA().getY()) * ((double)segment1.getB().getX() - (double)segment2.getA().getX())) * (((double)segment2.getB().getX() - (double)segment2.getA().getX()) * ((double)segment1.getA().getY() - (double)segment2.getA().getY()) - ((double)segment2.getB().getY() - (double)segment2.getA().getY()) * ((double)segment1.getA().getX() - (double)segment2.getA().getX())) < 0
				;
		/*return  ((segment1.getVector().crossProduct(segment2.getVector()) != 0)
				&& ((segment1.getVector().crossProduct(segment2.getB().minusNewVector(segment1.getA()))  *  segment1.getVector().crossProduct(segment2.getA().minusNewVector(segment1.getA()))) < 0)
				&& ((segment2.getVector().crossProduct(segment1.getB().minusNewVector(segment2.getA()))  *  segment2.getVector().crossProduct(segment1.getA().minusNewVector(segment2.getA()))) < 0));
				*/
	}
	
	/**
	 * Détermine si un segment et un cercle s'intersectent ou non
	 * @param segment
	 * @param circle
	 * @return vrai si il y a intersection entre le segment et le cercle, faux sinon
	 */
	public static boolean intersects(Segment segment, Circle circle)
	{
		// TODO : expliquer l'algo
		double area = ((double)circle.getCenter().getX() - (double)segment.getA().getX())*((double)segment.getB().getY() - (double)segment.getA().getY()) - ((double)circle.getCenter().getY() - (double)segment.getA().getY())*((double)segment.getB().getX() - (double)segment.getA().getX());
		double distA = ((double)segment.getA().getX() - (double)circle.getCenter().getX())*((double)segment.getA().getX() - (double)circle.getCenter().getX()) + ((double)segment.getA().getY() - (double)circle.getCenter().getY())*((double)segment.getA().getY() - (double)circle.getCenter().getY());
		double distB = ((double)segment.getB().getX() - (double)circle.getCenter().getX())*((double)segment.getB().getX() - (double)circle.getCenter().getX()) + ((double)segment.getB().getY() - (double)circle.getCenter().getY())*((double)segment.getB().getY() - (double)circle.getCenter().getY());
		if(distA >= circle.getRadius() * circle.getRadius() && distB < circle.getRadius() * circle.getRadius() || distA < circle.getRadius() * circle.getRadius() && distB >= circle.getRadius() * circle.getRadius())
			return true;
		return distA >= circle.getRadius() * circle.getRadius()
				&& distB >= circle.getRadius() * circle.getRadius()
				&& area * area / (((double)segment.getB().getX() - (double)segment.getA().getX())*((double)segment.getB().getX() - (double)segment.getA().getX())+((double)segment.getB().getY() - (double)segment.getA().getY())*((double)segment.getB().getY() - (double)segment.getA().getY())) <= circle.getRadius() * circle.getRadius()
				&& ((double)segment.getB().getX() - (double)segment.getA().getX())*((double)circle.getCenter().getX() - (double)segment.getA().getX()) + ((double)segment.getB().getY() - (double)segment.getA().getY())*((double)circle.getCenter().getY() - (double)segment.getA().getY()) >= 0
				&& ((double)segment.getA().getX() - (double)segment.getB().getX())*((double)circle.getCenter().getX() - (double)segment.getB().getX()) + ((double)segment.getA().getY() - (double)segment.getB().getY())*((double)circle.getCenter().getY() - (double)segment.getB().getY()) >= 0;

		/*double area = (circle.getCenter().getX() - segment.getA().getX()) * (segment.getB().getY() - segment.getA().getY()) - (circle.getCenter().getY() - segment.getA().getY()) * (segment.getB().getX() - segment.getA().getX());
		double distA = circle.getCenter().minusNewVector(segment.getA()).squaredLength();
		double distB = circle.getCenter().minusNewVector(segment.getB()).squaredLength();

		if(distB < circle.getRadius()*circle.getRadius() || distA < circle.getRadius()*circle.getRadius())
			return true;
		return (distA >= circle.getRadius() * circle.getRadius()
			&& distB >= circle.getRadius() * circle.getRadius()
			&& area * area / (double)(segment.getVector().squaredLength()) < circle.getRadius()*circle.getRadius()
			&& (segment.getB().getX() - segment.getA().getX())*(circle.getCenter().getX() - segment.getA().getX()) + (segment.getB().getY() - segment.getA().getY())*(circle.getCenter().getY() - segment.getA().getY()) >= 0
			&& (segment.getA().getX() - segment.getB().getX())*(circle.getCenter().getX() - segment.getB().getX()) + (segment.getA().getY() - segment.getB().getY())*(circle.getCenter().getY() - segment.getB().getY()) >= 0);
			*/
	}

	/**
	 * Retourne le point de l'arc de cerlce le plus proche d'un point hors du cercle
	 * @param pointHorsCercle
	 * @param circle
	 * @return le point de l'arc de cercle le plus proche de pointHorsCercle
	 */
	public static Vec2 closestPointOnCircle(Vec2 pointHorsCercle, Circle circle) {
		if (circle.containCircle(pointHorsCercle)) {
			return (pointHorsCercle);
		}
		Vec2 vec = pointHorsCercle.minusNewVector(circle.getCenter());

		// Si la direction donnée par le vecteur pointHorsCercle intersecte l'arc de cercle, on a le point avec les coordonnées polaires ;)
		if (vec.getA() >= circle.getAngleStart() && vec.getA() <= circle.getAngleEnd())
		{
			vec.setR(circle.getRadius());
			return circle.getCenter().plusNewVector(vec);
		}

		// Sinon, on doit choisir entre le point du début de l'arc de cercle et celui de fin
		else {
			Vec2 circleCenterStart = new Vec2(circle.getRadius(), circle.getAngleStart());
			Vec2 circleCenterEnd = new Vec2(circle.getRadius(), circle.getAngleEnd());

			if (circle.getCenter().plusNewVector(circleCenterStart).distance(pointHorsCercle) >= circle.getCenter().plusNewVector(circleCenterEnd).distance(pointHorsCercle)){
				return circleCenterEnd.plusNewVector(circle.getCenter());
			}
			else{
				return circleCenterStart.plusNewVector(circle.getCenter());
			}
		}
	}

	/**
	 * Retourne le point a l'extérieur du cercle le plus proche du point donné
	 * @param pointDansCercle le point dans le cercle
	 * @param circle le cercle
	 * @return la nouvelle position du point
	 */

	public static Vec2 closestPointOutCircle(Vec2 pointDansCercle, Circle circle) {
		if (!circle.containCircle(pointDansCercle))
		{
			return(pointDansCercle);
		}
		Vec2 toReturn = pointDansCercle.minusNewVector(circle.getCenter());

		if (isBetween(toReturn.getA(), circle.getAngleStart(), circle.getAngleEnd())) {
			toReturn.setR(circle.getRadius()+10);
			return circle.getCenter().plusNewVector(toReturn);
		}
		else{
			Vec2 vecAngleStart = new Vec2(circle.getRadius() + 10, circle.getAngleStart());
			Vec2 vecAngleEnd = new Vec2(circle.getRadius() + 10, circle.getAngleEnd());

			if (vecAngleStart.plusNewVector(circle.getCenter()).distance(pointDansCercle)<=vecAngleEnd.plusNewVector(circle.getCenter()).distance(pointDansCercle)){
				return circle.getCenter().plusNewVector(vecAngleStart);
			}
			else{
				return circle.getCenter().plusNewVector(vecAngleEnd);
			}
		}
	}

	/**
	 * Détermine le point d'intersection entre deux segments
	 * @param segment1
	 * @param segment2
	 * @return le point d'intersection des droites portées par les segments.
	 */
	public static Vec2 intersection(Segment segment1, Segment segment2)
	{
		// resolution du systeme associe aux deux segments
		double inter, k;
		
		if((segment2.getB().getY() - segment2.getA().getY()) != 0)
		{
			inter = (double)(segment2.getB().getX() - segment2.getA().getX()) / (double)(segment2.getB().getY() - segment2.getA().getY());
			k = (segment1.getA().getX() - segment2.getA().getX() + inter * (double)(segment2.getA().getY() - segment1.getA().getY())) / (segment1.getB().getX() - segment1.getA().getX() - inter * (segment1.getB().getY() - segment1.getA().getY()));
		}
		else
			k = -(double)(segment2.getA().getY() - segment1.getA().getY()) / (double)(segment1.getB().getY() - segment1.getA().getY());
		
		return new Vec2((int)(segment1.getA().getX() - k * (segment1.getB().getX() - segment1.getA().getX())), (int)(segment1.getA().getY() - k * (segment1.getB().getY() - segment1.getA().getY())));
	}

	/**
	 * Vérifie si la valeur donnée est entre les bornes données (limites incluses), utilisé pour simplifier les if
	 * @param val la valeur à tester
	 * @param a borne inf
	 * @param b borne sup
     */
	public static boolean isBetween(double val, double a, double b)
	{
		if(a>b) //Si le singe a mie de pain inf à la place de sup (2013-2014)
		{
			double temp=b;
			b=a;
			a=temp;
		}

		return val >= a && val <= b;
	}

	/**
	 * Cette méthode retourne true s'il y'a une intersection entre un segment et un rectangle
	 * @param segment
	 * @param rectangle
	 * @return
	 */
	public boolean intersects(Segment segment, Rectangle rectangle){
		rectangle.getHeight();
	}

	/**
	 * Retourne la valeur au carré (c'est chiant de le faire à la main)
	 * @param val
	 * @return le carré !
	 */
	public static int square(int val){
		return val*val;
	}
}
