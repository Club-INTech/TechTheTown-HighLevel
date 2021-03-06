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
    TAS_BASE_PRIS(false),
    TAS_CHATEAU_PRIS(false),
    TAS_STATION_EPURATION_PRIS(false),
    TAS_BASE_ENNEMI_PRIS(false),
    TAS_CHATEAU_ENNEMI_PRIS(false),
    TAS_STATION_EPURATION_ENNEMI_PRIS(false),
    INDICE_PATTERN_SIMULATION(0),

    /** Dimensions du robot */
    ROBOT_LENGTH(300),
    ROBOT_WIDTH(300),
    ROBOT_RADIUS(212),
    ENNEMY_RADIUS(400),

    /** Vitesses du robot */
    ROBOT_LINEAR_SPEED(840),        // mm/s
    ROBOT_ANGULAR_SPEED(Math.PI),   // rad/s

    /**Les cubes*/
    LONGUEUR_CUBE(58),

    /**Longueur bras*/
    LONGUEUR_BRAS_AVANT(317),
    LONGUEUR_BRAS_ARRIERE(333),

    /**Dimension portes*/
    DIMENSION_PORTES(87),

    /** Paramètres obstacles */
    PEREMP_OBST(2000),

    /** Paramètres capteurs */

    ROBOT_EN_RADIUS(220),           //en mm
    MAX_SENSOR_RANGE(600),          //en mm
    MIN_SENSOR_RANGEAV(30),         //en mm
    MIN_SENSOR_RANGEAR(30),         //en mm
    MIN_SENSOR_RANGE(30),           //en mm
    SENSOR_ORIENTATION_FRONT(0),    //en radians
    SENSOR_ORIENTATION_BACK(0),     //en radians
    SENSOR_ANGLE_WIDENESS(1.04),    //en radians, CONE TOTAL (PAS DEMI CONE)
    UNCERTAINTY(1),
    // TODO à compléter

    /** Paramètres Locomotion */
    BASIC_DETECTION(true),
    ADVANCED_DETECTION(false),
    BASIC_DETECTION_LOOP_DELAY(500),
    BASIC_DETECTION_DISTANCE(300),
    DETECTION_DISTANCE(300),
    DETECTION_RAY(300),
    FEEDBACK_LOOPDELAY(10),
    ENNEMY_TIMEOUT(10000),

    DISTANCE_TO_DISENGAGE(50),
    MAX_RETRIES_IF_BLOCKED(1),

    /** Paramètres Pathfinding */
    COUT_FIXE(10),

    /** Paramètres des scripts */
    VECTEUR_EXAMPLE(new Vec2(50, 60)),

    /** Paramètre simulation */
    SIMULATION(false),

    /** Paramètre d'attente du jumper */
    ATTENTE_JUMPER(true),

    /** Paramètre permettant de savoir quel matchscript on exception */
    MATCHSCRIPT_TO_EXECUTE(0),

    /** Paramètres reconnaissance de couleurs */
    LOCALIZATION_AUTOMATED(true),
    FIRST_COLOR("null"),
    SECOND_COLOR("null"),
    THIRD_COLOR("null"),
    IMAGE_WIDTH(1280),
    IMAGE_HEIGHT(720),

    /**Paramètres couleurs*/
    rorange(183),
    gorange(107),
    borange(71),
    rjaune(184),
    gjaune(177),
    bjaune(37),
    rbleu(50),
    gbleu(84),
    bbleu(112),
    rnoir(13),
    gnoir(24),
    bnoir(20),
    rvert(43),
    gvert(120),
    bvert(68),
    saturationPreModifier(1.8),
    brightnessPreModifier(1.3),
    saturationModifierLightingUp(1.3),
    brightnessModifierLightingUp(1.1),

    /**Paramètres scripts*/
    DISTANCE_INTERRUPTEUR(50),
    DISTANCE_PENETRATION_ZONE_DEPOSE_CUBES(70),

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

