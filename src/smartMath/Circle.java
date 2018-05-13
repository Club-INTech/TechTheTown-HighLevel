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

import java.util.ArrayList;

/**
 * Classe des cercles, utile pour les points d'entrée des scripts et le Pathfinding
 * @author paul, rem
 */

public class Circle {

	/** Position du centre du cercle */
	private Vect center;
	
	/** Rayon du cercle */
	private double radius;

	/** Etendu de l'arc de cercle (en absolue et dans le sens trigo) */
	private double angleStart;
	private double angleEnd;
	
	/**
	 * construit un cercle à partir de son centre et rayon
	 * @param center le centre
	 * @param radius le rayon en mm
	 */
	public Circle(Vect center, double radius)
	{
		this.center=center;
		this.radius=radius;
		this.angleStart = -Math.PI;
		this.angleEnd = Math.PI;
	}

	/**
	 * construit un cercle de rayon nul, soit un point
	 * @param center le centre en mm, pas de virgule
	 */
	public Circle(Vect center)
	{
		this.center=center;
		this.radius=0;
		this.angleStart=0;
		this.angleEnd=0;
	}

	/**
	 * construit un arc de cercle à partir de son rayon, son centre et son étendue
	 * @param center le centre
	 * @param radius le rayon en mm
	 * @param angleStart l'angle du début de l'arc ou étendue
	 * @param angleEnd l'angle de fin de l'arc ou angle du centre
	 * @param convention comment on représente l'arc de cercle: true veut dire que l'on met un angle start et end, false
	 *                   veut dire avec un angle milieu de l'arc et une étendue
	 */
	public Circle(Vect center, double radius, double angleStart, double angleEnd, boolean convention)
	{
		this.center=center;
		this.radius=radius;
		if (convention) {
			this.angleStart = angleStart;
			this.angleEnd = angleEnd;
		}
		else{
			this.angleStart = angleEnd - angleStart/2;
			this.angleEnd = angleEnd + angleStart/2;
		}
	}

	/**
	 * Getter de center
	 * @return le centre du cercle (position en mm)
	 */
	public Vect getCenter()
	{
		return this.center;
	}

	public double getRadius() {
		return radius;
	}

	public double getAngleStart() {
		return angleStart;
	}

	public double getAngleEnd() {
		return angleEnd;
	}

	public void setRadius(double radius){
		this.radius=radius;
	}

	/**
	 * test si le Vect est dans le disque
	 * @param point un vec2 a tester
	 * @return vrai si le point est a l'interieur du cercle ou dessus
	 */
	public boolean containDisk(Vect point)
	{
		return (point.distance(this.center)<=this.radius);
	}
	
	/**
	 * test si le Vect appartient au cercle
	 * @param point un Vect a tester
	 * @return vrai si le point est sur le cercle
	 */
	public boolean containCircle(Vect point)
	{
		double dx=point.getX()-this.center.getX();
		double dy=point.getY()-this.center.getY();
		return (dx*dx+dy*dy)<=(radius*radius);
	}

	/**
	 * Cette méthode retourne des n vecteurs autour d'un cercle
	 * @return
	 */
	public ArrayList<Vect> pointsaroundcircle(int n){
		ArrayList<Vect> l=new ArrayList<>();
		for(int i=0;i<n;i++){
			int x=new Integer((int)(this.getRadius()*Math.cos(2*i*Math.PI/n))+this.getCenter().getX());
			int y=new Integer((int)(this.getRadius()*Math.sin(2*i*Math.PI/n))+this.getCenter().getY());
			Vect vectoadd=new VectCart(x,y);
			l.add(vectoadd);
		}
		return l;
	}

	public boolean isInsideEnough(Circle circle){
		double maxRadius=Math.max(this.radius,circle.getRadius());
		if(this.getCenter().distance(circle.getCenter())<0.75*maxRadius){
			return true;
		}
		else{
			return false;
		}
	}
}
