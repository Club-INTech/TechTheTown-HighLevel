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
        this.position = new Vec2(Integer.parseInt(infos[0]), Integer.parseInt(infos[1]));
        this.orientation = Double.parseDouble(infos[2]);
    }

    /** Getters & Setters */
    public Vec2 getPosition() {
        return position;
    }
    public void setPosition(Vec2 position) {
        this.position = position;
    }
    public double getOrientation() {
        return orientation;
    }
    public void setOrientation(double orientation) {
        this.orientation = orientation;
    }
}
