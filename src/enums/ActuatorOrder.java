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

public enum ActuatorOrder
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
    ETAT_CONTACTEUR3("c3"),


/*			 ____________________
 * 		   *|                    |*
 *		   *|    Pelle T-3000    |*
 *		   *|____________________|*
 */

	//********** 3 POSITIONS DES BRAS DE LA PELLETEUSE ***********//
	// Rangement de la pelleteuse
	REPLIER_PELLETEUSE("bpr",700),

	// Baisse un peu plus la pelleteuse
	DEPLOYER_PELLETEUSE_BAS("bpra",700),

	// Déploiement de la pelleteuse
	DEPLOYER_PELLETEUSE("bpd",600),

	// Position intermédiaire de la pelleteuse, pour rotations de la pelle
	MED_PELLETEUSE("bpm",300),

	LIVRAISON_PELLETEUSE("bpdm", 1100),


	//********** 3 POSITIONS DE LA PELLE ***********//
	// Position avant prise de boules
	PRET_PELLE("pd",700),

	// Position 1 pour éviter le blocage des boules dans la pelle
	P1("p1", 600),

	// Position 2, à faire juste après la position 1 et avant de finir le mouvement habituel de la pelle
	P2("p2", 600),

	//Position initiale, de rangement de la pelle
	RANGE_PELLE("pb", 700),

	// Position intermédiaire de la pelle, maintient les boules
	PREND_PELLE("pm",700),//avant:2000

	//Pos de déplacement avec les boules
	TIENT_BOULES("pt",800),

	//Position de livraison de boules de la pelle
	LIVRE_PELLE("pf",800),

    //Reasservir pelle, a ne pas utiliser lors d'une prise de boules... Donner juste après un ordre de position(pret pelle, prend pelle ou tientboules)
    PELLE_REASSERV("pelreasserv"),


/*			 _____________________
 * 		   *|                     |*
 *		   *|Attrappes Module SSV2|*
 *		   *|_____________________|*
 */

//		6 ORDRES DES ATTRAPE-MODULES (3 droites, 3 gauches (et vive le MODEM))	//

	// Côté Droit
	//Position avant prise
	REPOS_ATTRAPE_D("amdd",500),

	//Position d'évitement de la cale
	MID_ATTRAPE_D("ammd",700),

	// Position de livraison
	PREND_MODULE_D("amfd",1500),

	//Côté gauche
	REPOS_ATTRAPE_G("amdg",400),

	MID_ATTRAPE_G("ammg",700),

	PREND_MODULE_G("amfg",1500),


/*			 ___________________
 * 		   *|                   |*
 *		   *|   Cales Module    |*
 *		   *|___________________|*
 */

//		2 POSITIONS DES CALLAGES-MODULES(les marteaux)	//

	// Position haut
	REPLI_CALLE_D("cmdd",300),

	//Position repos
	REPOS_CALLE_D("cmmd",350),

	// Position basse
	LIVRE_CALLE_D("cmfd",600),

	// Position haut
	REPLI_CALLE_G("cmdg",300),

	//Poition de repos
	REPOS_CALLE_G("cmmg",350),

	// Position basse
	LIVRE_CALLE_G("cmfg",550),


/*			 ___________________
 * 		   *|                   |*
 *		   *|   Largue Module   |*
 *		   *|___________________|*
 */

	//		2 POSITIONS DU LARGUE MODULE	//
	// Position de repos
	REPOS_LARGUEUR("lmd",200),

	// Position de poussée
	POUSSE_LARGUEUR("lmf", 700),
	// Position de poussée en mode lent
	POUSSE_LARGUEUR_LENT("lmfl", 1800),

        //Réasservissement largue module
        REASSERV_LARGEUR("lmreasserv"),


/*		     ___________________
* 		   *|                   |*
*		   *|     Ascenseur     |*
*		   *|___________________|*
*/

	//		2 POSITIONS DE L'ASCENCEUR  //
	// Position basse
	BAISSE_ASC("ascdown",1000),

	// Position haute
	LEVE_ASC("ascup",1200),

	//Stoppe l'ascenseur
	STOP_ASC("ascstop");



	// TODO : Ajuster des durées pour les actions qui prennent du temps(selon les tests qu'on fera) (et les WaitForCompletion)



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
