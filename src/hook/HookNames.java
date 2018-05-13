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
import smartMath.Vect;
import smartMath.VectCart;

/**
 * Contient le nom des hooks et leurs paramètres associés
 * ATTENTION à ne pas mettre deux hooks avec le meme id !!
 */
public enum HookNames {

    // Example :
    SPEED_DOWN(1, new VectCart(50, 50), 5 ,0,Math.PI,Speed.SLOW_ALL),
    BASIC_DETECTION_DISABLE(2,new VectCart(1500,2000),450,0,4*Math.PI,ActuatorOrder.BASIC_DETECTION_DISABLE),
    ACTIVE_BRAS_AVANT_ABEILLE(3, new VectCart(1500,2000), 450, 0, 4*Math.PI, ActuatorOrder.ACTIVE_BRAS_AVANT_POUR_ABEILLE),
    ACTIVE_BRAS_ARRIERE_ABEILLE(4, new VectCart(1500,2000), 450, 0, 4*Math.PI, ActuatorOrder.ACTIVE_BRAS_ARRIERE_POUR_ABEILLE),
    FERMER_PORTE_AVANT(5,new VectCart(1200,1700), 666, 0, 4*Math.PI, ActuatorOrder.FERME_LA_PORTE_AVANT),
    FERMER_PORTE_ARRIERE(6,new VectCart(1200,1700), 666, 0, 4*Math.PI, ActuatorOrder.FERME_LA_PORTE_ARRIERE),
    ;

    /** Ordre du hook */
    private MotionOrder order;

    /** Position de trigger du hook */
    private Vect position;

    /** Tolérence sur la position */
    private int tolerency; //en mm

    /** Id du hook, utile pour pouvoir l'activer/désactivé manuellement*/
    private int id;

    private double orientation;
    private double tolerencyAngle; //en radians

    /** Constructeur */
    HookNames(int id, Vect position, int tolerency, double orientation, double tolerencyAngle, MotionOrder order){
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
    public Vect getPosition() {
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

    public void setPosition(Vect position) {
        this.position = position;
    }

}
