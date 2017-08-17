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

package robot;

import container.Service;
import enums.*;
import exceptions.ConfigPropertyNotFoundException;
import exceptions.Locomotion.PointInObstacleException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;
import hook.Hook;
import pathfinder.Pathfinding;
import smartMath.Circle;
import smartMath.Geometry;
import smartMath.Vec2;
import table.Table;
import utils.Config;
import utils.Log;
import utils.Sleep;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Effectue le lien entre le code et la réalité (permet de parler aux actionneurs, d'interroger les capteurs, etc.)
 * @author pf
 */
public class Robot implements Service {

	/**
	 * système de log sur lequel écrire.
	 */
	protected Log log;

	/**
	 * endroit ou lire la configuration du robot.
	 */
	protected Config config;

	/**
	 * la table est symétrisée si le robot démarre du côté x<0
	 */
	protected boolean symmetry;

	/**
	 * vitesse du robot sur la table.
	 */
	protected Speed speed;

	/**
	 * la position du robot
	 */
	protected Vec2 position;

	protected Pathfinding pathfinding;

	/**
	 * l'orientation du robot
	 */
	protected double orientation;

	/**
	 * Rayon du robot provenant du fichier de config, modélise le robot comme un cercle.
	 * Le rayon est la distance entre le centre des roues et le point le plus éloigné du centre
	 */
	private int robotRay;

	/**
	 * Largeur du robot
	 */
	public int robotWidth;

	/**
	 * Longueur du robot
	 */
	public int robotLength;

	/**
	 * chemin en court par le robot, utilise par l'interface graphique
	 */
	public ArrayList<Vec2> cheminSuivi = new ArrayList<Vec2>();

	/**
	 * Si le robot force dans ses mouvements
	 */
	protected boolean isForcing = false;

	/**
	 * Protocole de communication série
	 */
	private SerialWrapper serialWrapper;

	/**
	 * Map pour la symétrie des actionneurs
	 */
	private SymmetrizedActuatorOrderMap mActuatorCorrespondenceMap = new SymmetrizedActuatorOrderMap();

	/**
	 * Map pour la symétrie de la stratégie en rotation
	 */
	private SymmetrizedTurningStrategy mTurningStrategyCorrespondenceMap = new SymmetrizedTurningStrategy();

	/**
	 * Map pour la symétrie des capteurs
	 */
	private SymmetrizedSensorNamesMap mSensorNamesMap = new SymmetrizedSensorNamesMap();

	/**
	 * Système de locomotion à utiliser pour déplacer le robot
	 */
	private Locomotion mLocomotion;

	private	boolean rempliDeBoules=false;

	private int chargementModule=0;

	public int getChargementModule() {
		return chargementModule;
	}

	public void setRempliDeBoules(boolean rempliDeBoules) {
		this.rempliDeBoules = rempliDeBoules;
	}

	public void setChargementModule(int chargementModule) {
		this.chargementModule = chargementModule;
	}

	public boolean isRempliDeBoules() {
		return rempliDeBoules;
	}

	public HashMap<ScriptNames,Boolean> dejaFait=new HashMap<ScriptNames,Boolean>();// Doublet nom de script / déjà fait


	/**
	 * Constructeur
	 *
	 * @param deplacements  système de locomotion
	 * @param config        fichier de config
	 * @param log           fichier de log
	 * @param serialWrapper protocole communication série
	 */
	private Robot(Locomotion deplacements, Config config, Log log, SerialWrapper serialWrapper, Pathfinding pathfinding) {

		this.config = config;
		this.log = log;
		this.pathfinding = pathfinding;
		this.serialWrapper = serialWrapper;
		this.mLocomotion = deplacements;
		updateConfig();
		speed = Speed.SLOW_ALL;
	}


	/****************************
	 * INCONTOURNABLES DE ROBOT *
	 ***************************/


	/**
	 * Utiliser un actuateur par l'ordre fourni
	 * Peut être bloquante le temps de faire l'action
	 *
	 * @param order             l'ordre
	 * @param waitForCompletion si on attends un temps prédéfini pendant l'action
	 * @throws SerialConnexionException
	 */
	public void useActuator(ActuatorOrder order, boolean waitForCompletion) throws SerialConnexionException {
		if (symmetry)
			order = mActuatorCorrespondenceMap.getSymmetrizedActuatorOrder(order);
		serialWrapper.useActuator(order);

		if (waitForCompletion) {
			sleep(order.getDuration());
		}
	}


	/**************
	 * LOCOMOTION *
	 *************/

			/************************
		 	* APPELS AU PATHFINDING *
		 	************************/


	/**
	 * Déplace le robot vers un point en suivant un chemin qui évite les obstacles. (appel du pathfinding)
	 * Cette méthode est bloquante: son exécution ne se termine que lorsque le robot a atteint le point d'arrivée
	 *
	 * @param aim le point de destination du mouvement
	 * @param hooksToConsider les hooks déclenchables durant ce mouvement
	 * @param table la table sur laquelle le robot se deplace
	 * @throws UnableToMoveException losrque quelque chose sur le chemin cloche et que le robot ne peut s'en défaire simplement: bloquage mécanique immobilisant le robot ou obstacle percu par les capteurs
	 */
	public void moveToLocation(Vec2 aim, ArrayList<Hook> hooksToConsider, Table table) throws  UnableToMoveException,PointInObstacleException
	{
		log.debug("Appel de Robot.moveToLocation(" + aim + "," + hooksToConsider + "," + table + ")");
		//On crée bêtement un cercle de rayon nul pour lancer moveToCircle, sachant que la position de ce cercle est extraite pour le pathDiniDing (et après on dit qu'à INTech on code comme des porcs...)
		moveToCircle(new Circle(aim), hooksToConsider, table);
	}

	/**
	 * deplace le robot vers le point du cercle donnné le plus proche, en évitant les obstacles. (appel du pathfinding)
	 * methode bloquante : l'execution ne se termine que lorsque le robot est arrive
	 * @param aim le cercle ou l'on veut se rendre
	 * @param hooksToConsider the hooks to consider
	 * @param table la table sur laquelle on est sensé se déplacer
	 * @throws UnableToMoveException lorsque quelque chose sur le chemin cloche et que le robot ne peut s'en défaire simplement: bloquage mécanique immobilisant le robot ou obstacle percu par les capteurs
	 */
	public void moveToCircle(Circle aim, ArrayList<Hook> hooksToConsider, Table table) throws UnableToMoveException, PointInObstacleException {
		Vec2 aimPosition= Geometry.closestPointOnCircle(this.position,aim);
		this.followPath(this.pathfinding.Astarfoulah(this.getPosition(),aimPosition,this.orientation, this.speed.translationSpeed,this.speed.rotationSpeed ),hooksToConsider, mLocomotion.getDirectionStrategy());
	}

	/**
	 * Appel de locomotion sur un chemin (en général déterminé par le Pathfinding)
	 * @param chemin
	 * @param hooksToConsider
	 * @throws UnableToMoveException
	 */

	@SuppressWarnings("unchecked")
	public void followPath(ArrayList<Vec2> chemin, ArrayList<Hook> hooksToConsider) throws UnableToMoveException
	{
		cheminSuivi = (ArrayList<Vec2>) chemin.clone();
		mLocomotion.followPath(chemin, hooksToConsider, DirectionStrategy.getDefaultStrategy());
	}

	/**
	 * Appel de locomotion sur un chemin (en général déterminé par le Pathfinding), en considérant la directionStrategy
	 * @param chemin
	 * @param hooksToConsider
	 * @param direction
	 * @throws UnableToMoveException
	 */

	@SuppressWarnings("unchecked")
	protected void followPath(ArrayList<Vec2> chemin, ArrayList<Hook> hooksToConsider, DirectionStrategy direction) throws UnableToMoveException
	{
		cheminSuivi = (ArrayList<Vec2>) chemin.clone();
		mLocomotion.followPath(chemin, hooksToConsider, direction);
	}

	/**
	 * Effectue un trajet en ligne droite jusqu'au point désiré
	 * @param pointVise
	 * @throws UnableToMoveException
	 */

	public void goTo(Vec2 pointVise) throws UnableToMoveException {
		goTo(pointVise, new ArrayList<Hook>(), false, true);
	}

	/**
	 * Effectue un trajet en ligne droite jusqu'au point désiré
	 * @param pointVise
	 * @param hooksToConsider
	 * @throws UnableToMoveException
	 */
	public void goTo(Vec2 pointVise, ArrayList<Hook> hooksToConsider) throws UnableToMoveException {
		goTo(pointVise, hooksToConsider, false, true);
	}

	/** Effectue un mouvement en ligne droite jusqu'au point désiré.
	 * @param pointVise
	 * @param hooksToConsider
	 * @param expectedWallImpact
	 * @param isDetect
	 * @throws UnableToMoveException
	 */
	public void goTo(Vec2 pointVise, ArrayList<Hook> hooksToConsider, boolean expectedWallImpact, boolean isDetect) throws UnableToMoveException {
		log.debug("Appel de Robot.goTo :" + pointVise);
		mLocomotion.goTo(pointVise, hooksToConsider, expectedWallImpact, isDetect);
	}




			/***********************
	 		* MOUVEMENTS UNITAIRES *
	 		***********************/


	/**
	 * Méthode pour se tourner vers un point
	 * @param pointVise
	 * @throws UnableToMoveException
	 */
	public void turnTo(Vec2 pointVise) throws UnableToMoveException {
		position = getPosition();
		Vec2 move = pointVise.minusNewVector(position);
		double a = move.getA();
		turn(a);
	}

	/**
	 * Fait tourner le robot (méthode bloquante)
	 * Attention: le pivot sera fait en supposant qu'il n'y a pas de hook a vérifier, et qu'on ne s'attends pas a percuter un obstacle.
	 *
	 * @param angle : valeur absolue en radiant de l'orientation que le robot doit avoir après cet appel
	 * @throws UnableToMoveException losrque quelque chose sur le chemin cloche et que le robot ne peut s'en défaire simplement: bloquage mécanique immobilisant le robot ou obstacle percu par les capteurs
	 */
	public void turn(double angle) throws UnableToMoveException
	{
		turn(angle, null, false, false);
	}

	/**
	 * Fait tourner le robot, et permet de prendre en compte les hooks
	 * Attention: on s'attend à ne pas percuter d'obstacle
	 * @param angle : valeur absolue en radiant de l'orientation que le robot doit avoir après avoir tourné
	 * @throws UnableToMoveException
	 */

	public void turn(double angle, ArrayList<Hook> hooksToConsider) throws UnableToMoveException
	{
		turn(angle, hooksToConsider, false, false);
	}

	/**
	 * Fait tourner le robot, en considérant les hooks et les éventuels calins avec le mur
	 * @param angle
	 * @param hooksToConsider
	 * @param expectsWallImpact
	 * @throws UnableToMoveException
	 */

	public void turn(double angle, ArrayList<Hook> hooksToConsider, boolean expectsWallImpact) throws UnableToMoveException
	{
		turn(angle, hooksToConsider, expectsWallImpact,false);
	}

	/**
	 * Fait tourner le robot, et considère si l'angle est relatif ou non
	 * @param angle
	 * @param hooksToConsider
	 * @param expectsWallImpact
	 * @param isTurnRelative
	 * @throws UnableToMoveException
	 */
	public void turn(double angle, ArrayList<Hook> hooksToConsider, boolean expectsWallImpact, boolean isTurnRelative) throws UnableToMoveException
	{
		turn(angle, hooksToConsider, expectsWallImpact, isTurnRelative, true);
	}

	/**
	 * Fait tourner le robot en considérant TOUT !
	 * @param angle
	 * @param hooksToConsider
	 * @param expectsWallImpact
	 * @param isTurnRelative
	 * @param mustDetect
	 * @throws UnableToMoveException
	 */
	public void turn(double angle, ArrayList<Hook> hooksToConsider, boolean expectsWallImpact, boolean isTurnRelative, boolean mustDetect) throws UnableToMoveException
	{
		if(isTurnRelative)
			angle += getOrientation();
		mLocomotion.turn(angle, hooksToConsider, expectsWallImpact, mustDetect);
	}

	/**
	 * Fait tourner le robot (méthode bloquante)
	 * L'orientation est modifiée si on est équipe jaune: Cette méthode n'adapte pas l'orientation en fonction de la couleur de l'équipe
	 * Attention: le pivot sera fait en supposant qu'il n'y a pas de hook a vérifier, et qu'on ne s'attends pas a percuter un obstacle.
	 *
	 * @param angle : valeur absolue en radiant de l'orientation que le robot doit avoir après cet appel. L'orientation ne sera pas symétrisée, quelle que soit la couleur de l'équipe.
	 * @throws UnableToMoveException losrque quelque chose sur le chemin cloche et que le robot ne peut s'en défaire simplement: bloquage mécanique immobilisant le robot ou obstacle percu par les capteurs
	 */
	public void turnNoSymmetry(double angle) throws UnableToMoveException
	{

		log.debug("appel de Robot.turnNoSymmetry(" + angle + ")");
		// Fais la symétrie deux fois (symétrie de symétrie, c'est l'identité)
		if(symmetry)
			turn(Math.PI-angle, null, false, false);
		else
			turn(angle, null, false, false);
	}

	/**
	 * Fait avancer le robot de la distance spécifiée. Le robot garde son orientation actuelle et va simplement avancer.
	 * Attention, cette méthode suppose qu'il n'y a pas de hooks a considérer, et que l'on est pas sensé percuter un mur.
	 * Cette méthode est bloquante: son exécution ne se termine que lorsque le robot a atteint le point d'arrivée
	 *
	 * @param distance en mm que le robot doit franchir. Si cette distance est négative, le robot va reculer. Attention, en cas de distance négative, cette méthode ne vérifie pas s'il y a un système d'évitement a l'arrère du robot
	 * @throws UnableToMoveException losrque quelque chose sur le chemin cloche et que le robot ne peut s'en défaire simplement: bloquage mécanique immobilisant le robot ou obstacle percu par les capteurs
	 */
	public void moveLengthwise(int distance) throws UnableToMoveException {
		moveLengthwise(distance, new ArrayList<Hook>(), false);
	}


	/**
	 * Fait avancer le robot de la distance spécifiée. Le robot garde son orientation actuelle et va simplement avancer.
	 * Attention, cette méthode suppose que l'on est pas sensé percuter un mur.
	 * Cette méthode est bloquante: son exécution ne se termine que lorsque le robot a atteint le point d'arrivée
	 *
	 * @param distance en mm que le robot doit franchir. Si cette distance est négative, le robot va reculer. Attention, en cas de distance négative, cette méthode ne vérifie pas s'il y a un système d'évitement a l'arrère du robot
	 * @param hooksToConsider les hooks déclenchables durant ce mouvement
	 * @throws UnableToMoveException losrque quelque chose sur le chemin cloche et que le robot ne peut s'en défaire simplement: bloquage mécanique immobilisant le robot ou obstacle percu par les capteurs
	 */
	public void moveLengthwise(int distance, ArrayList<Hook> hooksToConsider) throws UnableToMoveException
	{
		log.debug("appel de Robot.moveLengthwise(" + distance + "," + hooksToConsider + ")");
		moveLengthwise(distance, hooksToConsider, false);
	}


	/**
	 * Fait avancer le robot de la distance spécifiée. Le robot garde son orientation actuelle et va simplement avancer
	 * Cette méthode est bloquante: son exécution ne se termine que lorsque le robot a atteint le point d'arrivée
	 *
	 * @param distance        en mm que le robot doit franchir
	 * @param hooksToConsider hooks a considérer lors de ce déplacement. Le hook n'est déclenché que s'il est dans cette liste et que sa condition d'activation est remplie
	 * @param speed           la vitesse du robot lors de son parcours
	 * @throws UnableToMoveException losrque quelque chose sur le chemin cloche et que le robot ne peut s'en défaire simplement: bloquage mécanique immobilisant le robot ou obstacle percu par les capteurs
	 */
	public void moveLengthwise(int distance, ArrayList<Hook> hooksToConsider, Speed speed) throws UnableToMoveException {

		log.debug("appel de Robot.moveLengthwise(" + distance + "," + hooksToConsider + "," + speed + ")");
		moveLengthwise(distance, hooksToConsider, false, true, speed);
	}


	/**
	 * Fait avancer le robot de la distance spécifiée. Le robot garde son orientation actuelle et va simplement avancer
	 * Cette méthode est bloquante: son exécution ne se termine que lorsque le robot a atteint le point d'arrivée
	 *
	 * @param distance          en mm que le robot doit franchir
	 * @param hooksToConsider   hooks a considérer lors de ce déplacement. Le hook n'est déclenché que s'il est dans cette liste et que sa condition d'activation est remplie
	 * @param expectsWallImpact true si le robot doit s'attendre a percuter un mur au cours du déplacement. false si la route est sensée être dégagée.
	 * @throws UnableToMoveException losrque quelque chose sur le chemin cloche et que le robot ne peut s'en défaire simplement: bloquage mécanique immobilisant le robot ou obstacle percu par les capteurs
	 */
	public void moveLengthwise(int distance, ArrayList<Hook> hooksToConsider, boolean expectsWallImpact) throws UnableToMoveException {
		log.debug("appel de Robot.moveLengthwise(" + distance + "," + hooksToConsider + "," + expectsWallImpact + ")");
		moveLengthwise(distance, hooksToConsider, expectsWallImpact, true);
	}


	/**
	 * Fait avancer le robot de la distance spécifiée. Le robot garde son orientation actuelle et va simplement avancer
	 * Cette méthode est bloquante: son exécution ne se termine que lorsque le robot a atteint le point d'arrivée
	 *
	 * @param distance          en mm que le robot doit franchir
	 * @param hooksToConsider   hooks a considérer lors de ce déplacement. Le hook n'est déclenché que s'il est dans cette liste et que sa condition d'activation est remplie
	 * @param expectsWallImpact true si le robot doit s'attendre a percuter un mur au cours du déplacement. false si la route est sensée être dégagée.
	 * @param mustDetect        vrai si le robot doit detecter les obstacles sur son chemin
	 * @throws UnableToMoveException losrque quelque chose sur le chemin cloche et que le robot ne peut s'en défaire simplement: bloquage mécanique immobilisant le robot ou obstacle percu par les capteurs
	 */
	public void moveLengthwise(int distance, ArrayList<Hook> hooksToConsider, boolean expectsWallImpact, Boolean mustDetect) throws UnableToMoveException {
		log.debug("appel de Robot.moveLengthwise(" + distance + "," + hooksToConsider + "," + expectsWallImpact + "," + mustDetect + ")");
		Speed newSpeed = Speed.SLOW_ALL;
		moveLengthwise(distance, hooksToConsider, expectsWallImpact, mustDetect, newSpeed);
	}


	/**
	 * moveLengthwise mais sans détection
	 */
	public void moveLengthwiseWithoutDetection(int distance, ArrayList<Hook> hooksToConsider, boolean expectsWallImpact) throws UnableToMoveException {
		log.debug("appel de Robot.moveLengthwiseWithoutDetection(" + distance + "," + hooksToConsider + "," + expectsWallImpact + ")");
		Speed newSpeed = Speed.SLOW_ALL;
		moveLengthwise(distance, hooksToConsider, expectsWallImpact, false, newSpeed);
	}


	/**
	 * Fait avancer le robot de la distance spécifiée. Le robot garde son orientation actuelle et va simplement avancer
	 * Cette méthode est bloquante: son exécution ne se termine que lorsque le robot a atteint le point d'arrivée
	 *
	 * @param distance          en mm que le robot doit franchir
	 * @param hooksToConsider   hooks a considérer lors de ce déplacement. Le hook n'est déclenché que s'il est dans cette liste et que sa condition d'activation est remplie
	 * @param expectsWallImpact true si le robot doit s'attendre a percuter un mur au cours du déplacement. false si la route est sensée être dégagée.
	 * @param mustDetect        vrai si le robot doit detecter les obstacles sur son chemin
	 * @throws UnableToMoveException losrque quelque chose sur le chemin cloche et que le robot ne peut s'en défaire simplement: bloquage mécanique immobilisant le robot ou obstacle percu par les capteurs
	 */
	public void moveLengthwise(int distance, ArrayList<Hook> hooksToConsider, boolean expectsWallImpact, Boolean mustDetect, Speed newSpeed) throws UnableToMoveException {
		log.debug("appel de Robot.moveLengthwise(" + distance + "," + hooksToConsider + "," + expectsWallImpact + "," + mustDetect + "," + newSpeed + ")");
		Speed oldSpeed = speed;
		speed = newSpeed;
		mLocomotion.moveLengthwise(distance, hooksToConsider, expectsWallImpact, mustDetect);
		speed = oldSpeed;
	}

    /**
     * Fait avancer le robot de la distance spécifiée. Le robot garde son orientation actuelle et va simplement avancer.
     * Attention, cette méthode suppose qu'il n'y a pas de hooks a considérer, et que l'on est sensé percuter un mur. La vitesse du robor est alors réduite a Speed.INTO_WALL.
     * Cette méthode est bloquante: son exécution ne se termine que lorsque le robot a atteint le point d'arrivée
     * @param distance en mm que le robot doit franchir. Si cette distance est négative, le robot va reculer. Attention, en cas de distance négative, cette méthode ne vérifie pas s'il y a un système d'évitement a l'arrère du robot
     * @param hooksToConsider les hooks déclenchables durant ce mouvement
     * @throws UnableToMoveException losrque quelque chose sur le chemin cloche et que le robot ne peut s'en défaire simplement: bloquage mécanique immobilisant le robot ou obstacle percu par les capteurs
     */
    public void moveLengthwiseTowardWall(int distance, ArrayList<Hook> hooksToConsider) throws UnableToMoveException
    {

        log.debug("appel de Robot.moveLengthwiseTowardWall(" + distance + "," + hooksToConsider + ")");
        Speed oldSpeed = speed;
        setLocomotionSpeed(Speed.SLOW_ALL);
        moveLengthwise(distance, hooksToConsider, true, false);
        setLocomotionSpeed(oldSpeed);
    }


	/*****************
	 * RESTE A TRIER *
	 ****************/

	/**
	 * Active la détection basique
	 * @param basicDetection oui/non
	 */
	public void setBasicDetection(boolean basicDetection) {
		mLocomotion.setBasicDetection(basicDetection);
	}

	/**
	 * Forcer les valeurs des capteurs dans cet objet
	 * @param val les valeurs comme définies dans threadSensors
	 */
	public void setUSvalues(ArrayList<Integer> val) {
		mLocomotion.setUSvalues(val);
	}

	/**
	 * Active le mouvement forcé (on ignore les conditions de blocage du bas-niveau)
	 * @param state oui/non
	 */
	public void setForceMovement(boolean state)
	{
		try {
			mLocomotion.setForceMovement(state);
		} catch (SerialConnexionException e) {
			e.printStackTrace();
			log.critical("Erreur critique série : Forcing non changé !");
			return;
		}
		this.isForcing = true;
	}

	/**
	 * Change l'accélération en plus fluide mais plus lente
	 */
	public void setSmoothAcceleration(boolean state) throws SerialConnexionException
	{
		this.mLocomotion.setSmoothAcceleration(state);
	}

	/**
	 * Renvoie la valeur d'un capteur de contact
	 *
	 * @param sensor le capteur en question
	 * @return l'état logique du capteur
	 * @throws SerialConnexionException
	 */
	public boolean getContactSensorValue(ContactSensors sensor) throws SerialConnexionException {
		// si il n'y a pas de symétrie, on renvoie la valeur brute du bas niveau
		if (!symmetry)
			return serialWrapper.getContactSensorValue(sensor);
		else {
			sensor = mSensorNamesMap.getSymmetrizedContactSensorName(sensor);

			/* attention si les capteurs sont en int[] il faut symétriser ce int[] */

			return serialWrapper.getContactSensorValue(sensor);
		}
	}

	/**
	 * Fait attendre le programme
	 *
	 * @param duree attente en ms
	 */
	public void sleep(long duree) {
		Sleep.sleep(duree);
	}

	/**
	 * Met à jour la configuration de la classe via le fichier de configuration fourni par le sysème de container
	 * et supprime les espaces (si si c'est utile)
	 */
	@Override
	public void updateConfig() {
		try {
			symmetry = config.getProperty("couleur").replaceAll(" ", "").equals("jaune"); // TODO : modifier la couleur adverse
			robotRay = Integer.parseInt(config.getProperty("rayon_robot"));
			robotLength = Integer.parseInt(config.getProperty("longueur_robot"));
			robotWidth = Integer.parseInt(config.getProperty("largeur_robot"));
			position = Table.entryPosition;
			orientation = Math.PI;
			this.dejaFait.put(ScriptNames.SCRIPTED_GO_TO_MODULEFOND,false);
			this.dejaFait.put(ScriptNames.SCRIPTED_GO_TO_CRATERE_PRES_BASE,false);
			this.dejaFait.put(ScriptNames.SCRIPTED_GO_TO_CRATERE_LIVRAISON_BOULES1,false);
			this.dejaFait.put(ScriptNames.SCRIPTED_GO_TO_CRATERE_LIVRAISON_BOULES2,false);
			this.dejaFait.put(ScriptNames.SCRIPTED_GO_TO_LIVRAISON_MODULEFOND,false);
			this.dejaFait.put(ScriptNames.SCRIPTED_GO_TO_CRATEREFOND,false);
			this.dejaFait.put(ScriptNames.SCRIPTED_GO_TO_MODULE_PRES_BASE,false);

		} catch (ConfigPropertyNotFoundException e) {
			log.critical(e.logStack());
			log.debug("Revoir le code : impossible de trouver la propriété " + e.getPropertyNotFound());
		}
	}

    public void immobilise()
    {
		log.debug("appel de Robot.immobilise()");
        mLocomotion.immobilise();
    }

	/**
	 * Active/désactive les capteurs
	 * @throws SerialConnexionException
	 */
	public void switchSensor() throws SerialConnexionException {
		serialWrapper.switchSensor();
	}

	public void enableRotationnalFeedbackLoop()
	{
		log.debug("appel de Robot.enableRotationnalFeedbackLoop()");
		try
		{
			mLocomotion.enableRotationnalFeedbackLoop();
		}
		catch (SerialConnexionException e)
		{
			log.critical( e.logStack());
		}
	}

	public void disableRotationnalFeedbackLoop()
	{
		log.debug("appel de Robot.disableRotationnalFeedbackLoop()");
		try
		{
			mLocomotion.disableRotationnalFeedbackLoop();
		}
		catch (SerialConnexionException e)
		{
			log.critical( e.logStack());
		}
	}
	
	public void enableFeedbackLoop() throws SerialConnexionException
	{
		mLocomotion.enableFeedbackLoop();		
	}

	public void disableFeedbackLoop() throws SerialConnexionException
	{
		mLocomotion.disableFeedbackLoop();
	}
	
	/* 
	 * GETTERS & SETTERS
	 */
	public void setPosition(Vec2 position)
	{
	    mLocomotion.setPosition(position);
	}
	
	public Vec2 getPosition()
	{
    	position = mLocomotion.getPosition();
	    return position;
	}

    /**
     * donne la dernière position connue du robot sur la table
     * cette methode est rapide et ne déclenche pas d'appel série
     * @return la dernière position connue du robot
     */
    public Vec2 getPositionFast()
    {
    	position = mLocomotion.getPosition();
        return position;
    }

    public void setOrientation(double orientation)
	{
	    mLocomotion.setOrientation(orientation);
	}

    public double getOrientation()
    {
    	orientation =  mLocomotion.getOrientation();
        return orientation;
    }

    /**
     * Donne la derniere orientation connue du robot sur la table
     * Cette méthode est rapide et ne déclenche pas d'appel série
     * @return la derniere orientation connue du robot
     */
    public double getOrientationFast()
    {
    	orientation = mLocomotion.getOrientationFast();
        return orientation;
    }

	public boolean setTurningStrategy(TurningStrategy turning)
	{
        if(!(turning == TurningStrategy.FASTEST))
        {
			if(symmetry)
			{
				mLocomotion.setTurningOrders(mTurningStrategyCorrespondenceMap.getSymmetrizedTurningStrategy(turning));
				return true;
			}
            mLocomotion.setTurningOrders(turning);
            return true;
        }
        return false;
	}
	
	public boolean setDirectionStrategy(DirectionStrategy motion)
	{
        if(!(motion == DirectionStrategy.FASTEST))
		{
			mLocomotion.setDirectionOrders(motion);
			return true;
		}
		return false;
	}

	public void setLocomotionSpeed(Speed vitesse)
	{
        try
        {
			mLocomotion.setTranslationnalSpeed(vitesse.translationSpeed);
	        mLocomotion.setRotationnalSpeed(vitesse.rotationSpeed);
	        
	        speed = vitesse;
		} 
        catch (SerialConnexionException e)
        {
			log.critical( e.logStack());
		}
	}

    public void setRobotRadius(int radius)
    {
        this.robotRay = radius;
    }

    public int getRobotRadius()
    {
        return this.robotRay;
    }
	

	public Speed getLocomotionSpeed()
	{
		return speed;
	}
	
	public boolean getIsRobotTurning()
	{
		return mLocomotion.isRobotTurning;
	}
	
	public boolean getIsRobotMovingForward()
	{
		return mLocomotion.isRobotMovingForward;
	}
	
	public boolean getIsRobotMovingBackward()
	{
		return mLocomotion.isRobotMovingBackward;
	}

}
