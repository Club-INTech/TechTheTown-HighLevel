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

package exceptions.Locomotion;

import smartMath.Vec2;

/**
 * Peut etre utile pour le Pathfinding...
 */
public class PointInObstacleException extends Exception {
    private Vec2 point;
    public PointInObstacleException(Vec2 point)
    {
        super();
        this.point=point;

    }

    public Vec2 getPoint() {
        return point;
    }

    public void setPoint(Vec2 point) {
        this.point = point;
    }
}
