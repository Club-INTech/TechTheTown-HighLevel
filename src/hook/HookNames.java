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

import enums.MotionOrder;
import enums.Speed;
import smartMath.Vec2;

/**
 * Contient le nom des hooks et leurs paramètres associés
 * ATTENTION à ne pas mettre deux hooks avec le meme id !!
 */
public enum HookNames {

    // Example :
    SPEED_DOWN(1, new Vec2(50, 50), 5, Speed.SLOW_ALL),
    ;

    /** Ordre du hook */
    private MotionOrder order;

    /** Position de trigger du hook */
    private Vec2 position;

    /** Tolérence sur la position */
    private int tolerency;

    /** Id du hook, utile pour pouvoir l'activer/désactivé manuellement*/
    private int id;

    /** Constructeur */
    HookNames(int id, Vec2 position, int tolerency, MotionOrder order){
        this.id = id;
        this.position = position;
        this.tolerency = tolerency;
        this.order = order;
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
}
