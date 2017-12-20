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
 * Classe de calcul de vecteurs de dimension 2.
 *
 * @author pf, rem
 */

public class Vec2 {

	/** Abscisse x */
	private int x;

	/** Ordonnée y */
	private int y;

	/** Rayon r */
	private double r;

	/** Angle a, entre -pi et pi */
	private double a;

	/** Constructeur d'un vecteur nul */
	public Vec2() {
		x = 0;
		y = 0;
		r = 0;
		a = 0;
	}

	/**
	 * Construit un vecteur à partir de ses coordonnées cartésiennes
	 *
	 * @param requestedX abscisse
	 * @param requestedY ordonnée
	 */
	public Vec2(int requestedX, int requestedY) {
		x = requestedX;
		y = requestedY;
		r = Math.sqrt(x * x + y * y);
		a = this.angle();
	}

	/**
	 * Construit un vecteur à partir de ses coordonnées polaires
	 *
	 * @param requestedR rayon
	 * @param requestedA angle
	 */
	public Vec2(double requestedR, double requestedA) {
		if (requestedR < 0){
			r = Math.abs(requestedR);
			a = Geometry.moduloSpec(requestedA + Math.PI, Math.PI);
		}
		else{
			r = requestedR;
			a = requestedA;
		}
		x = (int) (r * Math.cos(a));
		y = (int) (r * Math.sin(a));
	}

	// Il est plus performant de trouver la longueur au carré et de la comparer à des distances au carré que d'en extraire la racine

	/**
	 * @return la longueur au carré du vecteur
	 */
	public int squaredLength() {
		return (int) (r * r);
	}

	/**
	 * @return la longueur du vecteur
	 */
	public float length() {
		return (float) r;
	}

	/**
	 * Effectue le produit scalaire avec un second vecteur
	 * @param other le second vecteur du produit scalaire
	 * @return résultat du produit
	 */
	public int dot(Vec2 other) {
		return x * other.x + y * other.y;
	}

	/**
	 * Effectue le produit vectoriel avec un second vecteur
	 * ATTENTION : le produit vectoriel est anti-symétrique, le paramètre est donc le 2e vecteur
	 * De plus, comme on est en 2D, on renvoie évidemment le scalaire associé au vecteur
	 * @param other le second vecteur du produit vectoriel
	 * @return scalaire associé au produit vectoriel
	 */
	public int crossProduct (Vec2 other){
		return (x * other.y - y * other.x);
	}

	/**
	 * Construit un nouveau vecteur avec une somme
	 *
	 * @param other le vecteur à sommer au premier
	 * @return le nouveau vecteur
	 */
	public Vec2 plusNewVector(Vec2 other) {
		return new Vec2(x + other.x, y + other.y);
	}

	/**
	 * Construit un nouveau vecteur avec une différence
	 *
	 * @param other le vecteur à soustraire au premier
	 * @return le nouveau vecteur
	 */
	public Vec2 minusNewVector(Vec2 other) {
		return new Vec2(x - other.x, y - other.y);
	}

	/**
	 * Accroissement du vecteur actuel par un second
	 *
	 * @param other le second vecteur
	 */
	public void plus(Vec2 other) {
		x += other.x;
		y += other.y;
		r = Math.sqrt(x * x + y * y);
		a = this.angle();
	}

	/**
	 * Réduction du vecteur actuel par un second
	 *
	 * @param other le second vecteur
	 */
	public void minus(Vec2 other) {
		x -= other.x;
		y -= other.y;
		r = Math.sqrt(x * x + y * y);
		a = this.angle();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Vec2 clone() {
		return new Vec2(this.x, this.y);
	}

	/**
	 * Calcul la distance au carré séparant le vecteur actuel d'un second
	 *
	 * @param other le second vecteur
	 * @return distance au carré entre les deux vecteurs
	 */
	public float squaredDistance(Vec2 other) {
		return (x - other.x) * (x - other.x) + (y - other.y) * (y - other.y);
	}

	/**
	 * Distance entre le vecteur actuel et un second
	 *
	 * @param other le second vecteur
	 * @return la distance entre les deux vecteurs
	 */
	public float distance(Vec2 other) {
		return (float) Math.sqrt(squaredDistance(other));
	}

	/**
	 * Compare deux vecteurs
	 *
	 * @param other le second vecteur
	 * @return vrai si les coordonnées sont égales
	 */
	public boolean equals(Vec2 other) {
		return x == other.x && y == other.y;
	}

	/**
	 * Multiplication par un scalaire
	 *
	 * @param a le scalaire
	 * @return ancien vecteur dilaté
	 */
	public Vec2 dotFloat(double a) {
		return new Vec2((int) (x * a), (int) (y * a));
	}

	/**
	 * Angle du vecteur par rapport à l'abscisse
	 *
	 * @return l'angle en radians
	 */
	public double angle() {
		if (this.squaredLength() == 0)
			return 0;

		double a = Math.min((double) Math.abs(x), Math.abs(y)) / Math.max(Math.abs(x), Math.abs(y));
		double s = a * a;
		double r = ((-0.0464964749 * s + 0.15931422) * s - 0.327622764) * s * a + a;

		if (Math.abs(y) > Math.abs(x))
			r = 1.57079637 - r;
		if (x < 0)
			r = 3.14159274 - r;
		if (y < 0)
			r = -r;
		return r;
	}

	public boolean isNull() {
		return (this.x == 0 && this.y == 0);
	}

	/**
	 * Tous les setters et getters parce que private :p
	 */

	public void set(Vec2 other) {
		x = other.x;
		y = other.y;
		r = other.r;
		a = other.a;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
		r = this.length();
		a = this.angle();
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
		r = this.length();
		a = this.angle();
	}

	public double getR() {
		return r;
	}

	public void setR(double r) {
		this.r = r;
		x = (int) (r * Math.cos(a));
		y = (int) (r * Math.sin(a));
	}

	public double getA() {
		return a;
	}

	public void setA(double a) {
		this.a = a % Math.PI;
		x = (int) (r * Math.cos(a));
		y = (int) (r * Math.sin(a));
	}

	/** Le hashCode permet d'identifier un objet par un id géneré en fonction des paramètres (ici x et y);
	 * ca sert pour la fonction equals notamment
	 */
	/* (non-Javadoc)
 * @see java.lang.Object#hashCode()
 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		else if (obj == null)
			return false;
		else if (!(obj instanceof Vec2))
			return false;
		Vec2 other = (Vec2) obj;
		if (x != other.x)
			return false;
		else if (y != other.y)
			return false;
		return true;
	}

	/* (non-Javadoc)
 * @see java.lang.Object#toString()
 */
	@Override
	public String toString() {
		String rs = String.format("%s", Math.round(r*10000)/10000).substring(0,6);
		String os = String.format("%s", Math.round(a*10000)/10000).substring(0,6);
		return String.format("(%s , %s) (%s , %s)", x,y,rs,os);
	}

	/**
	 * Lorsque l'on discute avec le LL, on signifie que c'est un vecteur comme ceci
	 */
	public String toStringEth(){
		return x + " " + y;
	}

	/**
	 * Pour l'interface, on n'affiche que les coordonnées cartésiennes
	 */
	public String toStringInterface(){
		return "(" + x + "," + y + ")";
	}

	/** Commentaire ? */
	public String[] toStringTableau() {
		String TableauVec[] = {"(" + x + ")", "(" + y + ")", "(" + r + ")", "(" + a + ")"};
		return TableauVec;
	}

	/**
	 * transforme un point en un cercle de rayon nul
	 * @return un cercle de centre ce Vec2 et de rayon nul.
	 */
	public Circle toCircle() {
		return new Circle(this, 0);
	}
}

