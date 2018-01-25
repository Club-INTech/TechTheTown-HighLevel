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

package enums;

/*
 * Copyright (C) 2013-2017 Pierre-François Gimenez
 * Distributed under the MIT License.
 */

import pfg.config.ConfigInfo;
import smartMath.Vec2;

/**
 * Configuration du robot : la valeur est celle par défaut; si la variable figure dans le .ini,
 * elle est override.
 * @author Pierre-François Gimenez
 */

public enum ConfigInfoRobot implements ConfigInfo
{
    /** Constantes (rarement modifiées) */
    TABLE_X(3000),
    TABLE_Y(2000),
    TEMPS_MATCH(100),
    ETH_DELAY(50),

    /** Paramètres log */
    PRINT_LOG(true),
    SAVE_LOG(true),

    /** Paramètres match !*/
    COULEUR("vert"),
    C_DES_FOUS_EN_FACE(false),

    /** Dimensions du robot */
    ROBOT_LENGTH(300),
    ROBOT_WIDTH(300),
    ROBOT_RADIUS(212),
    ENNEMY_RADIUS(400),


    /**Les cubes*/
    LONGUEUR_CUBE (55),

    /** Paramètres obstacles */
    PEREMP_OBST(5000),

    /** Paramètres capteurs */
    ROBOT_EN_RADIUS(220),
    MAX_SENSOR_RANGE(800),
    MIN_SENSOR_RANGEAV(30),
    MIN_SENSOR_RANGEAR(30),
    MIN_SENSOR_RANGE(30),
    SENSOR_POSITION_ANGLE_FRONT(1),
    SENSOR_POSITION_ANGLE_BACK(1),
    SENSOR_ANGLE_WIDENESS(30),
    UNCERTAINTY(1),
    // TODO à compléter

    /** Paramètres Locomotion */
    BASIC_DETECTION_DISTANCE(100),
    DETECTION_DISTANCE(200),
    DETECTION_RAY(250),
    FEEDBACK_LOOPDELAY(50),
    ENNEMY_LOOPDELAY(500),
    ENNEMY_TIMEOUT(10000),

    DISTANCE_TO_DISENGAGE(50),
    MAX_RETRIES_IF_BLOCKED(1),

    /** Paramètres des scripts */
    VECTEUR_EXAMPLE(new Vec2(50, 60)),

    /** Paramètre simulation */
    SIMULATION(false),
    ;

    private Object defaultValue;

    /**
     * Constructor with some default value
     * @param defaultValue
     */
    private ConfigInfoRobot(Object defaultValue)
    {
        this.defaultValue = defaultValue;
    }

    /**
     * Just a getter
     */
    @Override
    public Object getDefaultValue()
    {
        return defaultValue;
    }

    /**
     * The toString() method is already adapted
     */
}

