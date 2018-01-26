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

//import scripts.TakeCubes;

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


/*			 _________________
 * 		   *|                 |*
 *		   *|  DEPLACEMENTS   |*
 *		   *|_________________|*
 */

	MOVE_LENTGHWISE("d"),
	TURN("t"),
	TURN_RIGHT_ONLY("tor"),
	TURN_LEFT_ONLY("tol"),
	STOP("stop"),
	IS_ROBOT_MOVING("f"),
	SEND_POSITION("?xyo"),

/*			 ____________________
 * 		   *|                    |*
 *		   *|   INITIALISATION   |*
 *		   *|____________________|*
 */

	ENABLE_FORCE_MOVEMENT("efm"),
	DISABLE_FORCE_MOVEMENT("dfm"),
	SET_TRANSLATION_SPEED("ctv"),
	SET_ROTATIONNAL_SPEED("crv"),
	SET_SPEED("ctrv"),

	SET_X("cx"),
	SET_Y("cy"),
	SET_ORIENTATION("co"),
	SET_POSITION("cxyo"),

	INITIALISE_HOOK("nh"),
	ENABLE_HOOK("eh"),
	DISABLE_HOOK("dh"),

	ACTIVE_LA_POMPE("alp",100),
	DESACTIVE_LA_POMPE("dlp",100),

	BAISSE_LE_BRAS_AVANT("blbAv",1500),
	RELEVE_LE_BRAS_AVANT("rlbAv",1500),
	OUVRE_LA_PORTE_AVANT("olpAv"),
	FERME_LA_PORTE_AVANT("flpAv"),
	TILT_LA_PORTE_AVANT("tlpAv"),
	ACTIVE_LE_BRAS_LATERAL("albl"),
	ACTIVE_ELECTROVANNE_AVANT("aeAv"),
	DESACTIVE_ELECTROVANNE_AVANT("deAv"),

	BAISSE_LE_BRAS_ARRIERE("blbAr",1500),
	ACTIVE_LA_POMPE_ARRIERE("alpAr",100),
	RELEVE_LE_BRAS_ARRIERE("rlbAr",1500),
	FERME_LA_PORTE_ARRIERE("flpAr",1000),
	OUVRE_LA_PORTE_ARRIERE("olpAr"),
	TILT_LA_PORTE_ARRIERE("tlpAr"),
	ACTIVE_ELECTROVANNE_ARRIERE("aeAr"),
	DESACTIVE_ELECTROVANNE_ARRIERE("deAr"),

/*			 _____________________
 * 		   *|                     |*
 *		   *|   ASSERVISSEMENTS   |*
 *		   *|_____________________|*
 */

	NO_ASSERV_TRANSLATION("ct0"),
	ASSERV_TRANSLATION("ct1"),
	NO_ASSERV_ROTATION("cr0"),
	ASSERV_ROTATION("cr1"),
	NO_ASSERV_SPEED("cv0"),
	ASSERV_SPEED("cv1"),
	DEBUG("pfdebug"),

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
	DIST_US_BACK_RIGHT("usard"),
	DIST_US_BACK_LEFT("usarg"),
	DIST_US_FRONT_RIGHT("usavd"),
	DIST_US_FRONT_LEFT("usavg"),


/*			 _____________________
 * 		   *|                     |*
 *		   *|CONTACTEURS ET JUMPER|*
 *		   *|_____________________|*
 */

    //état jumper (0='en place', 1='retiré')
    JUMPER_STATE("j"),

    //états contacteurs (0='non appuyé', 1='appuyé')
    ETAT_CONTACTEUR1("c1"),				//vaut 1 si l'ascenseur est en position haute
    ETAT_CONTACTEUR2("c2"),				//vaut 1 si l'ascenseur est en position basse
    ETAT_CONTACTEUR3("c3");

/*			____________________
 *		  *|					|*
 * 		  *|	 ACTIONNEURS    |*
 * 		  *|____________________|*
 *
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
