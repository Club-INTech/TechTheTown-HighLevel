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
 * Protocole des actionneurs.
 * contient pour chaque actionneur le nom des consignes java, la chaine à transmetttre à la carte actionneurs et la durée que cette action prend
 * @author pf
 *
 */

public enum ActuatorOrder implements MotionOrder
{
	// Syntaxe: NOM_METHODE("protocole_serie") Cette syntaxe suppose que l'action prends une seconde pour s'exécuter
	// Syntaxe alternative: NOM_METHODE("protocole_serie", durée actions)
	// exemple : MOVE_FORWARD("av")


/*			 __________________
 * 		   *|                  |*
 *		   *|  TESTS DE BASE   |*
 *		   *|__________________|*
 */

	DEPLACAMENT("d"),     //déplacement avant ou arrière sans asserv
	ROT_ABS("t"),         //rotation (angle absolu) sans asserv
	ROT_REL_RAD("t3"),    //rotation (angle relatif en radian) sans asserv
	ROT_REL_DEG("r"),     //rotation (angle relatif en degré) sans asserv
	//TRAJ_COURBE("dc"),  //trajectoire courbe : déplacement + rotation
	STOP("stop"),         //arrêt


/*			 __________________
 * 		   *|                  |*
 *		   *|   AUTRES TESTS   |*
 *		   *|__________________|*
 */

	//ACTIVER_MOUV_FORCE("efm"),
	//DESACTIVER_MOUV_FORCE("dfm"),


/*			 _____________________
 * 		   *|                     |*
 *		   *|   ASSERVISSEMENTS   |*
 *		   *|_____________________|*
 */

	NO_ASSERV_TRANS("ct0"),
	ASSERV_TRANS("ct1"),
	NO_ASSERV_ROT("cr0"),
	ASSERV_ROT("cr1"),
	NO_ASSERV_V("cv0"),
	ASSERV_V("cv1"),


/*			 _______________________
 * 		   *|                       |*
 *		   *|  MOUVEMENTS DU ROBOT  |*
 *		   *|    AVEC MONTLHERY     |*
 *		   *|_______________________|*
 */

	MONTLHERY("montlhery"),
	MOVE_FORWARD("av"),
	MOVE_BACKWARD("rc"),
	TURN_RIGHT("td"),
	TURN_LEFT("tg"),
	SSTOP("sstop"),


/*			 __________________
 * 		   *|                  |*
 *		   *|     CAPTEURS     |*
 *		   *|__________________|*
 */

	//lire distance donnée par les capteurs US ,  en millimètres
	DIST_US_ARD("usard"), //Distance capteur Arrière droit
	DIST_US_ARG("usarg"), //Distance capteur Arrière gauche
	DIST_US_AVD("usavd"), //Distance capteur Avant droit
	DIST_US_AVG("usavg"), //Distance capteur Avant gauche


/*			 _____________________
 * 		   *|                     |*
 *		   *|CONTACTEURS ET JUMPER|*
 *		   *|_____________________|*
 */

    //état jumper (0='en place', 1='retiré')
    ETAT_JUMPER("j"),

    //états contacteurs (0='non appuyé', 1='appuyé')
    ETAT_CONTACTEUR1("c1"),				//vaut 1 si l'ascenseur est en position haute
    ETAT_CONTACTEUR2("c2"),				//vaut 1 si l'ascenseur est en position basse
    ETAT_CONTACTEUR3("c3");

/*			____________________
 *		  *|					|*
 * 		  *|	 ACTIONNEURS    |*
 * 		  *|____________________|*
 */
	// TODO : Ajouter les ordres & ajuster les WaitForCompletion (durée des ordres)

	/**
	 *  chaine de caractère envoyée au travers de la liaison série
	 */
	private String serialOrder;

	/** duurée de l'action en millisecondes */
	private int duration;

	/**
	 * Construit un ordre pour un actionneur
	 * on suppose que son temps d'exécution est d'une seconde
	 * @param serialString la chaine de caractère à envoyer à la carte actionnneurs
	 */
    ActuatorOrder(String serialString)
	{
		this.serialOrder = serialString;
		this.duration = 700;	// valeur par défaut de la durée de mouvement d'un actionneur
	}

	/**
	 * Construit un ordre pour un actionneur avec le temps d'exécution spécifié 
	 * @param serialString la chaine de caractère à envoyer à la carte actionnneurs
	 */
    ActuatorOrder(String serialString, int duration)
	{
		this.serialOrder = serialString;
		this.duration = duration;
	}
	
	/**
	 * Retrouve la chaine de caractère a envoyer par la série a la carte actionneur pour qu'elle effectue cet ordre
	 * @return la chaine de caractère à envoyer par la série à la carte actionneur
	 */
	public String getSerialOrder()
	{
		return serialOrder;
	}

	/**
	 * Renvoie la durée de l'action
	 * @return durée d'exécution de l'action
	 */
	public int getDuration()
	{
		return duration;
	}
}
