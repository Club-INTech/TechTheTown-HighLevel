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

package hook;

import enums.ActuatorOrder;
import enums.MotionOrder;
import enums.Speed;
import org.opencv.core.Mat;
import smartMath.Vec2;

/**
 * Contient le nom des hooks et leurs paramètres associés
 * ATTENTION à ne pas mettre deux hooks avec le meme id !!
 */
public enum HookNames {

    // Example :
    SPEED_DOWN(1, new Vec2(50, 50), 5 ,0,Math.PI,Speed.SLOW_ALL),
    ACTIVE_BRAS_AVANT_ABEILLE(2, new Vec2(1400,1800), 500, 0, 2*Math.PI, ActuatorOrder.ACTIVE_BRAS_AVANT_POUR_ABEILLE),
    ACTIVE_BRAS_ARRIERE_ABEILLE(3, new Vec2(1400,1800), 500, 0, 2*Math.PI, ActuatorOrder.ACTIVE_BRAS_ARRIERE_POUR_ABEILLE),

    ;

    /** Ordre du hook */
    private MotionOrder order;

    /** Position de trigger du hook */
    private Vec2 position;

    /** Tolérence sur la position */
    private int tolerency; //en mm

    /** Id du hook, utile pour pouvoir l'activer/désactivé manuellement*/
    private int id;

    private double orientation;
    private double tolerencyAngle; //en radians

    /** Constructeur */
    HookNames(int id, Vec2 position, int tolerency, double orientation, double tolerencyAngle, MotionOrder order){
        this.id = id;
        this.position = position;
        this.tolerency = tolerency;
        this.order = order;
        this.orientation=orientation;
        this.tolerencyAngle=tolerencyAngle;
    }

    /** Getters & Setters */
    public MotionOrder getOrder() {
        return order;
    }
    public Vec2 getPosition() {
        return position;
    }
    public int getTolerency(){
        return tolerency;
    }
    public int getId() {
        return id;
    }

    public double getOrientation() {
        return orientation;
    }

    public double getTolerencyAngle() {
        return tolerencyAngle;
    }

    public void setPosition(Vec2 position) {
        this.position = position;
    }
}
