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
	
	/** Vitesse ultra lente de translation, ultra lente en rotation */
	ULTRA_SLOW_ALL(85,Math.PI/8),

	/** vitesse lente en translation, lente en rotation */
	SLOW_ALL(300, Math.PI/3),
	
	/** vitesse lente en translation, normale en rotation */
	SLOW_T_MEDIUM_R(300,2*Math.PI/3),

	/** Just for fishing */
	SLOW_MEDIUM(300, 2*Math.PI/3),

	/** Vitesse standard de déplacement et rotation */
	MEDIUM_ALL(500,1.6D),
	
	/** vitesse standard de déplacement, lente en rotation */
	MEDIUM_T_SLOW_R(480,Math.PI/3),
	
	/** vitesse rapide en translation et lente en rotation */
	FAST_T_SLOW_R(520, 1.75D),

	/** vitesse rapide en translation et moyen en rotation */
	FAST_T_MEDIUM_R(520, 1.8D),

	/** vitesse rapide en translation et rotation */
	FAST_ALL(600,(Math.PI));
    
    /** vitesse des moteurs lors d'une translation, ce sont ces valeurs qui seront envoyées à la STM */
    public int translationSpeed;

    /** vitesse des moteurs lors d'une rotation, ce sont ces valeurs qui seront envoyées à la STM */
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
}
