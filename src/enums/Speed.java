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

/**
 * Définition des vitesses possibles de déplacement du robot.
 * Les deux arguments passés dans les vitesses correspondent à des valeurs en mm/s pour la translation puis en rad/s pour la rotation
 * @author pf, CF, Discord
 */

public enum Speed implements MotionOrder
{
	//TODO régler les valeurs de vitesse en translations et rotations avec des phases de test, décider des combinaisons de vitesses inutiles
	
	/** Vitesse ultra lente en translation, ultra lente en rotation */
	ULTRA_SLOW_ALL(100,0.5D),

    /** Vitesse très lente en translation, très lenten en rotation */
    VERY_SLOW_ALL(150,0.75D),

	/** Vitesse lente en translation, lente en rotation */
	SLOW_ALL(300, 1D),

	/** Vitesse standard de déplacement et rotation */

	MEDIUM_ALL(400,2D),

	/** Vitesse rapide en translation et rotation */
	FAST_ALL(700,2.5D),

	/** Vitesse ultra rapide en translation et rotation */
	ULTRA_FAST_ALL(1000,4D),

    /** Vitesse par défaut (utliser une des vitesses au dessus dans les parenthèses)
     *  Fait pour ne pas avoir à changer la vitesse dans tout le code
     */
    DEFAULT_SPEED(FAST_ALL);


    /** Vitesse des moteurs lors d'une translation, ce sont ces valeurs qui seront envoyées à la STM */

    public int translationSpeed;

    /** Vitesse des moteurs lors d'une rotation, ce sont ces valeurs qui seront envoyées à la STM */
    public double rotationSpeed;


    /**
     * Constructeur d'une vitesse.
     * @param translationSpeed la vitesse de translation ( en mm/s)
     * @param rotationSpeed la vitesse de rotation (en rad/s)
     */
    Speed(int translationSpeed, double rotationSpeed)
    {
        this.translationSpeed = translationSpeed;
        this.rotationSpeed = rotationSpeed;
    }

    Speed(Speed speed){
        this.translationSpeed=speed.getTranslationSpeed();
        this.rotationSpeed=speed.getRotationSpeed();
    }

    public int getTranslationSpeed() {
        return translationSpeed;
    }

    public double getRotationSpeed() {
        return rotationSpeed;
    }
}
