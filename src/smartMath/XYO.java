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
 * Classe regroupant la position et l'orientation (pratique pour récupérer les infos du LL)
 * LA POSITION ET L'ORIENTATION SONT CELLE DU LL
 * @author rem
 */
public class XYO {

    /** Vecteur position */
    private Vec2 position;

    /** Orientation */
    private double orientation;

    /**
     * Constructeur normal
     * @param position
     * @param orientation
     */
    public XYO(Vec2 position, double orientation){
        this.position = position;
        this.orientation = orientation;
    }

    /**
     * Constructeur destinant à être appelé par le Wrapper : la chaine de carac doit être sous cette forme:
     * "X splitString Y splitString O"
     * @param buffer
     */
    public XYO(String buffer, String splitString){
        String[] infos = buffer.split(splitString);
        this.position = new Vec2((int)Math.round(Double.parseDouble(infos[0])), (int)Math.round(Double.parseDouble(infos[1])));
        this.orientation = Double.parseDouble(infos[2]);
    }

    /**
     * Methode mettant à jour la position du robot
     * @param buffer
     */
    public synchronized void update(String buffer, String splitString){
        String[] infos = buffer.split(splitString);
        this.position = new Vec2((int) Math.round(Double.parseDouble(infos[0])), (int) Math.round(Double.parseDouble(infos[1])));
        this.orientation = Double.parseDouble(infos[2]);
    }

    /** Méthode de symetrisation de l'objet par rapport à la table */
    public synchronized void symetrize() {
        this.position.symetrize();
        this.orientation = Geometry.moduloSpec(Math.PI - orientation, Math.PI);
    }

    @Override
    public synchronized XYO clone() {
        return new XYO(this.position.clone(), this.orientation);
    }

    @Override
    public String toString() {
        return "XY : " + this.position.toStringEth() + ", O : " + this.orientation;
    }

    /** Getters & Setters */
    public synchronized Vec2 getPosition() {
        return position;
    }
    public synchronized void setPosition(Vec2 position) {
        synchronized (this) {
            this.position = position;
        }
    }
    public synchronized double getOrientation() {
        return orientation;
    }
    public synchronized void setOrientation(double orientation) {
        synchronized (this) {
            this.orientation = orientation;
        }
    }


}
