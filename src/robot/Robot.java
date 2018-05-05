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
import enums.Speed;
import enums.SymmetrizedTurningStrategy;
import enums.SymmetrizedActuatorOrderMap;
import enums.SymmetrizedSensorNamesMap;
import enums.ActuatorOrder;
import enums.DirectionStrategy;
import enums.TurningStrategy;
import enums.ConfigInfoRobot;
import enums.ContactSensors;
import exceptions.Locomotion.ImmobileEnnemyForOneSecondAtLeast;
import exceptions.Locomotion.PointInObstacleException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.NoPathFound;
import pathfinder.Pathfinding;
import pfg.config.Config;
import smartMath.Circle;
import smartMath.Geometry;
import smartMath.Vec2;
import table.Table;
import utils.Log;
import utils.Sleep;

import java.util.ArrayList;

/**
 * Effectue le lien entre le code et la réalité (permet de parler aux actionneurs, d'interroger les capteurs, etc.)
 *
 * @author pf
 */
public class Robot implements Service {


    /**
     * Système de log sur lequel écrire.
     */
    protected Log log;

    /**
     * Endroit ou lire la configuration du robot.
     */
    protected Config config;

    /**
     * La table est symétrisée si le robot démarre du côté x<0
     */
    protected boolean symmetry;

    /**
     * Vitesse du robot sur la table.
     */
    protected Speed speed;

    /**
     * La position du robot
     */
    protected Vec2 position;

    /**
     * L'orientation du robot
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
     * Pathfinding
     */
    private Pathfinding pathfinding;

    /**
     * Chemin en court par le robot, utilise par l'interface graphique
     */
    public ArrayList<Vec2> cheminSuivi = new ArrayList<Vec2>();

    /**
     * Si le robot force dans ses mouvements
     */
    protected boolean isForcing = false;

    /**
     * Protocole de communication Ethernet (et ouai !)
     */
    private EthWrapper ethWrapper;

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


    /**
     * Constructeur
     *
     * @param deplacements système de locomotion
     * @param config       fichier de config
     * @param log          fichier de log
     * @param ethWrapper   protocole communication série
     */
    public Robot(Locomotion deplacements, Config config, Log log, EthWrapper ethWrapper, Pathfinding pathfinding) {
        this.config = config;
        this.log = log;
        updateConfig();
        this.pathfinding = pathfinding;
        this.ethWrapper = ethWrapper;
        this.mLocomotion = deplacements;
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
     */
    public void useActuator(ActuatorOrder order, boolean waitForCompletion) {
        if (symmetry) {
            order = mActuatorCorrespondenceMap.getSymmetrizedActuatorOrder(order);
        }
        ethWrapper.useActuator(order);
        if (waitForCompletion) {
            sleep(order.getDuration());
        }
    }

    /**************
     * LOCOMOTION *
     **************/

    /*************************
     * APPELS AU PATHFINDING *
     *************************/


    /**
     * Déplace le robot vers un point en suivant un chemin qui évite les obstacles. (appel du pathfinding)
     * Cette méthode est bloquante: son exécution ne se termine que lorsque le robot a atteint le point d'arrivée
     *
     * @param aim   le point de destination du mouvement
     * @param table la table sur laquelle le robot se deplace
     * @throws UnableToMoveException losrque quelque chose sur le chemin cloche et que le robot ne peut s'en défaire simplement: bloquage mécanique immobilisant le robot ou obstacle percu par les capteurs
     */
    public void moveToLocation(Vec2 aim, Table table) throws UnableToMoveException, PointInObstacleException, NoPathFound,ImmobileEnnemyForOneSecondAtLeast {
        log.debug("Appel de Robot.moveToLocation(" + aim + "," + table + ")");
        //On crée bêtement un cercle de rayon nul pour lancer moveToCircle, sachant que la position de ce cercle est extraite pour le pathDiniDing (et après on dit qu'à INTech on code comme des porcs...)
        moveToCircle(new Circle(aim), table);
    }

    /**
     * deplace le robot vers le point du cercle donnné le plus proche, en évitant les obstacles. (appel du pathfinding)
     * methode bloquante : l'execution ne se termine que lorsque le robot est arrive
     *
     * @param aim   le cercle ou l'on veut se rendre
     * @param table la table sur laquelle on est sensé se déplacer
     * @throws UnableToMoveException lorsque quelque chose sur le chemin cloche et que le robot ne peut s'en défaire simplement: bloquage mécanique immobilisant le robot ou obstacle percu par les capteurs
     */
    public void moveToCircle(Circle aim, Table table) throws UnableToMoveException, PointInObstacleException, NoPathFound,ImmobileEnnemyForOneSecondAtLeast {
        Vec2 aimPosition = Geometry.closestPointOnCircle(this.getPosition(), aim);
        // TODO : Appel du followpath & Pathfinding !
        followPath(pathfinding.findmyway(position, aimPosition));
    }

    /**
     * Appel de locomotion sur un chemin (en général déterminé par le Pathfinding)
     *
     * @param chemin
     * @throws UnableToMoveException
     */

    @SuppressWarnings("unchecked")
    public void followPath(ArrayList<Vec2> chemin) throws UnableToMoveException,ImmobileEnnemyForOneSecondAtLeast {
        cheminSuivi = (ArrayList<Vec2>) chemin.clone();
        mLocomotion.followPath(chemin);
    }

    /**
     * Appel de locomotion sur un chemin (en général déterminé par le Pathfinding), en considérant la directionStrategy
     *
     * @param chemin
     * @param direction
     * @throws UnableToMoveException
     */

    @SuppressWarnings("unchecked")
    protected void followPath(ArrayList<Vec2> chemin, DirectionStrategy direction) throws UnableToMoveException,ImmobileEnnemyForOneSecondAtLeast {
        cheminSuivi = (ArrayList<Vec2>) chemin.clone();
        mLocomotion.followPath(chemin);
    }

    /**
     * Effectue un trajet en ligne droite jusqu'au point désiré
     *
     * @param pointVise
     * @throws UnableToMoveException
     */

    public void goTo(Vec2 pointVise) throws UnableToMoveException,ImmobileEnnemyForOneSecondAtLeast {
        goTo(pointVise, false, true);
    }

    public void goToWithoutDetection(Vec2 pointVise) throws UnableToMoveException,ImmobileEnnemyForOneSecondAtLeast {
        goTo(pointVise, false, false);
    }

    public void goToWithoutDetection(Vec2 pointVise, boolean expectedWallImpact) throws UnableToMoveException,ImmobileEnnemyForOneSecondAtLeast {
        goTo(pointVise, expectedWallImpact, false);
    }

    /**
     * Effectue un mouvement en ligne droite jusqu'au point désiré.
     *
     * @param pointVise
     * @param expectedWallImpact
     * @param isDetect
     * @throws UnableToMoveException
     */
    public void goTo(Vec2 pointVise, boolean expectedWallImpact, boolean isDetect) throws UnableToMoveException,ImmobileEnnemyForOneSecondAtLeast {
        log.debug("Appel de Robot.goTo : " + pointVise);
        mLocomotion.moveToPoint(pointVise, expectedWallImpact, isDetect);
    }


    /***********************
     * MOUVEMENTS UNITAIRES *
     ***********************/


    /**
     * Méthode pour se tourner vers un point
     *
     * @param pointVise
     * @throws UnableToMoveException
     */
    public void turnTo(Vec2 pointVise) throws UnableToMoveException,ImmobileEnnemyForOneSecondAtLeast {
        position = getPosition();
        Vec2 move = pointVise.minusNewVector(position);
        double a = move.getA();
        turn(a);
    }
    public void turnToWithoutDetection(Vec2 pointVise) throws UnableToMoveException,ImmobileEnnemyForOneSecondAtLeast {
        position = getPosition();
        Vec2 move = pointVise.minusNewVector(position);
        double a = move.getA();
        turnWithoutDetection(a,false,false);
    }

    /**
     * Méthode pour se tourner vers un point
     *
     * @param pointVise
     * @throws UnableToMoveException
     */
    public void turnTo(Vec2 pointVise, boolean expectsWallImpact) throws UnableToMoveException,ImmobileEnnemyForOneSecondAtLeast {
        position = getPosition();
        Vec2 move = pointVise.minusNewVector(position);
        double a = move.getA();
        turn(a,expectsWallImpact);
    }

    /**
     * Fait tourner le robot (méthode bloquante)
     * Attention: le pivot sera fait en supposant qu'il n'y a pas de hook a vérifier, et qu'on ne s'attends pas a percuter un obstacle.
     *
     * @param angle : valeur absolue en radiant de l'orientation que le robot doit avoir après cet appel
     * @throws UnableToMoveException losrque quelque chose sur le chemin cloche et que le robot ne peut s'en défaire simplement: bloquage mécanique immobilisant le robot ou obstacle percu par les capteurs
     */
    public void turn(double angle) throws UnableToMoveException,ImmobileEnnemyForOneSecondAtLeast {
        turn(angle, false, false);
    }

    /**
     * Comme turn sauf que les angles sont relatifs, tourne par exemple de Pi/2 à partir de sa position
     **/
    public void turnRelatively(double angle) throws UnableToMoveException ,ImmobileEnnemyForOneSecondAtLeast {
        turn(angle, false, true);
    }

    /**
     * Fait tourner le robot, en considérant les hooks et les éventuels calins avec le mur
     *
     * @param angle
     * @param expectsWallImpact
     * @throws UnableToMoveException
     */
    public void turn(double angle, boolean expectsWallImpact) throws UnableToMoveException,ImmobileEnnemyForOneSecondAtLeast {
        turn(angle, expectsWallImpact, false);
    }

    /**
     * Fait tourner le robot, et considère si l'angle est relatif ou non
     *
     * @param angle
     * @param expectsWallImpact
     * @param isTurnRelative
     * @throws UnableToMoveException
     */
    public void turn(double angle, boolean expectsWallImpact, boolean isTurnRelative) throws UnableToMoveException,ImmobileEnnemyForOneSecondAtLeast {
        turn(angle, expectsWallImpact, isTurnRelative, true);
    }
    public void turnWithoutDetection(double angle, boolean expectsWallImpact, boolean isTurnRelative) throws UnableToMoveException,ImmobileEnnemyForOneSecondAtLeast {
        turn(angle, expectsWallImpact, isTurnRelative, false);
    }

    /**
     * Fait tourner le robot en considérant TOUT !
     *
     * @param angle
     * @param expectsWallImpact
     * @param isTurnRelative
     * @param mustDetect
     * @throws UnableToMoveException
     */
    public void turn(double angle, boolean expectsWallImpact, boolean isTurnRelative, boolean mustDetect) throws UnableToMoveException,ImmobileEnnemyForOneSecondAtLeast {
        if (isTurnRelative) {
            angle += getOrientation();
        }
        mLocomotion.turn(angle, expectsWallImpact, mustDetect);
    }

    /**
     * Fait tourner le robot (méthode bloquante)
     * L'orientation est modifiée si on est équipe jaune: Cette méthode n'adapte pas l'orientation en fonction de la couleur de l'équipe
     * Attention: le pivot sera fait en supposant qu'il n'y a pas de hook a vérifier, et qu'on ne s'attends pas a percuter un obstacle.
     *
     * @param angle : valeur absolue en radiant de l'orientation que le robot doit avoir après cet appel. L'orientation ne sera pas symétrisée, quelle que soit la couleur de l'équipe.
     * @throws UnableToMoveException losrque quelque chose sur le chemin cloche et que le robot ne peut s'en défaire simplement: bloquage mécanique immobilisant le robot ou obstacle percu par les capteurs
     */
    public void turnNoSymmetry(double angle) throws UnableToMoveException,ImmobileEnnemyForOneSecondAtLeast {
        log.debug("appel de Robot.turnNoSymmetry(" + angle + ")");
        // Fais la symétrie deux fois (symétrie de symétrie, c'est l'identité)
        if (symmetry) {
            turn(Math.PI - angle, false, false);
        }
        else{
            turn(angle, false, false);
        }
    }

    /**
     * Oriente le robot vers un point.
     *
     * @param point
     * @throws UnableToMoveException
     */

    public void turnToPoint(Vec2 point) throws UnableToMoveException,ImmobileEnnemyForOneSecondAtLeast {
        Vec2 vec = point.minusNewVector(position);
        double angle = vec.getA();
        log.debug("appel de Robot.turnToPoint(" + angle + ")");
        turn(angle, false, false);
    }

    /**
     * Fait avancer le robot de la distance spécifiée. Le robot garde son orientation actuelle et va simplement avancer.
     * Attention, cette méthode suppose qu'il n'y a pas de hooks a considérer, et que l'on est pas sensé percuter un mur.
     * Cette méthode est bloquante: son exécution ne se termine que lorsque le robot a atteint le point d'arrivée
     *
     * @param distance en mm que le robot doit franchir. Si cette distance est négative, le robot va reculer. Attention, en cas de distance négative, cette méthode ne vérifie pas s'il y a un système d'évitement a l'arrère du robot
     * @throws UnableToMoveException losrque quelque chose sur le chemin cloche et que le robot ne peut s'en défaire simplement: bloquage mécanique immobilisant le robot ou obstacle percu par les capteurs
     */
    public void moveLengthwise(int distance) throws UnableToMoveException,ImmobileEnnemyForOneSecondAtLeast {
        moveLengthwise(distance, false);
    }

    /**
     * Fait avancer le robot de la distance spécifiée. Le robot garde son orientation actuelle et va simplement avancer
     * Cette méthode est bloquante: son exécution ne se termine que lorsque le robot a atteint le point d'arrivée
     *
     * @param distance en mm que le robot doit franchir
     * @param speed    la vitesse du robot lors de son parcours
     * @throws UnableToMoveException losrque quelque chose sur le chemin cloche et que le robot ne peut s'en défaire simplement: bloquage mécanique immobilisant le robot ou obstacle percu par les capteurs
     */
    public void moveLengthwise(int distance, Speed speed) throws UnableToMoveException,ImmobileEnnemyForOneSecondAtLeast {
        log.debug("appel de Robot.moveLengthwise(" + distance + "," + speed + ")");
        moveLengthwise(distance, false, true, speed);
    }


    /**
     * Fait avancer le robot de la distance spécifiée. Le robot garde son orientation actuelle et va simplement avancer
     * Cette méthode est bloquante: son exécution ne se termine que lorsque le robot a atteint le point d'arrivée
     *
     * @param distance          en mm que le robot doit franchir
     * @param expectsWallImpact true si le robot doit s'attendre a percuter un mur au cours du déplacement. false si la route est sensée être dégagée.
     * @throws UnableToMoveException losrque quelque chose sur le chemin cloche et que le robot ne peut s'en défaire simplement: bloquage mécanique immobilisant le robot ou obstacle percu par les capteurs
     */
    public void moveLengthwise(int distance, boolean expectsWallImpact) throws UnableToMoveException,ImmobileEnnemyForOneSecondAtLeast {
        log.debug("appel de Robot.moveLengthwise(" + distance + "," + expectsWallImpact + ")");
        moveLengthwise(distance, expectsWallImpact, true);
    }

    /**
     * Fait avancer le robot de la distance spécifiée. Le robot garde son orientation actuelle et va simplement avancer
     * Cette méthode est bloquante: son exécution ne se termine que lorsque le robot a atteint le point d'arrivée
     *
     * @param distance          en mm que le robot doit franchir
     * @param expectsWallImpact true si le robot doit s'attendre a percuter un mur au cours du déplacement. false si la route est sensée être dégagée.
     * @param mustDetect        vrai si le robot doit detecter les obstacles sur son chemin
     * @throws UnableToMoveException losrque quelque chose sur le chemin cloche et que le robot ne peut s'en défaire simplement: bloquage mécanique immobilisant le robot ou obstacle percu par les capteurs
     */
    public void moveLengthwise(int distance, boolean expectsWallImpact, Boolean mustDetect) throws UnableToMoveException,ImmobileEnnemyForOneSecondAtLeast {
        log.debug("appel de Robot.moveLengthwise(" + distance + "," + expectsWallImpact + "," + mustDetect + ")");
        Speed newSpeed = Speed.SLOW_ALL;
        moveLengthwise(distance, expectsWallImpact, mustDetect, newSpeed);
    }


    /**
     * moveLengthwise mais sans détection
     */
    public void moveLengthwiseWithoutDetection(int distance, boolean expectsWallImpact) throws UnableToMoveException,ImmobileEnnemyForOneSecondAtLeast {
        log.debug("appel de Robot.moveLengthwiseWithoutDetection(" + distance + "," + expectsWallImpact + ")");
        moveLengthwise(distance, expectsWallImpact, false);
    }

    /**
     * moveLengthwise mais sans détection
     */
    public void moveLengthwiseWithoutDetection(int distance) throws UnableToMoveException,ImmobileEnnemyForOneSecondAtLeast {
        log.debug("appel de Robot.moveLengthwiseWithoutDetection(" + distance + "," + ")");
        moveLengthwise(distance, false, false);
    }


    /**
     * Fait avancer le robot de la distance spécifiée. Le robot garde son orientation actuelle et va simplement avancer
     * Cette méthode est bloquante: son exécution ne se termine que lorsque le robot a atteint le point d'arrivée
     *
     * @param distance          en mm que le robot doit franchir
     * @param expectsWallImpact true si le robot doit s'attendre a percuter un mur au cours du déplacement. false si la route est sensée être dégagée.
     * @param mustDetect        vrai si le robot doit detecter les obstacles sur son chemin
     * @throws UnableToMoveException losrque quelque chose sur le chemin cloche et que le robot ne peut s'en défaire simplement: bloquage mécanique immobilisant le robot ou obstacle percu par les capteurs
     */
    public void moveLengthwise(int distance, boolean expectsWallImpact, Boolean mustDetect, Speed newSpeed) throws UnableToMoveException,ImmobileEnnemyForOneSecondAtLeast {
        log.debug("appel de Robot.moveLengthwise(" + distance + "," + expectsWallImpact + "," + mustDetect + "," + newSpeed + ")");
        Speed oldSpeed = speed;
        speed = newSpeed;
        mLocomotion.moveLengthwise(distance, expectsWallImpact, mustDetect);
        speed = oldSpeed;
    }

    /**
     * Fait avancer le robot de la distance spécifiée. Le robot garde son orientation actuelle et va simplement avancer.
     * Attention, cette méthode suppose qu'il n'y a pas de hooks a considérer, et que l'on est sensé percuter un mur. La vitesse du robor est alors réduite a Speed.INTO_WALL.
     * Cette méthode est bloquante: son exécution ne se termine que lorsque le robot a atteint le point d'arrivée
     *
     * @param distance en mm que le robot doit franchir. Si cette distance est négative, le robot va reculer. Attention, en cas de distance négative, cette méthode ne vérifie pas s'il y a un système d'évitement a l'arrère du robot
     * @throws UnableToMoveException losrque quelque chose sur le chemin cloche et que le robot ne peut s'en défaire simplement: bloquage mécanique immobilisant le robot ou obstacle percu par les capteurs
     */
    public void moveLengthwiseTowardWall(int distance) throws UnableToMoveException,ImmobileEnnemyForOneSecondAtLeast {
        log.debug("appel de Robot.moveLengthwiseTowardWall(" + distance + ")");
        Speed oldSpeed = speed;
        setLocomotionSpeed(Speed.SLOW_ALL);
        moveLengthwise(distance, true, false);
        setLocomotionSpeed(oldSpeed);
    }


    /**
     * Fait tourner le robot vers un point, puis le fait avancer jusqu'à une certaine distance du point
     *
     * @param aim          distance visée
     * @param distanceNear distance à laquelle le robot doit s'arrêter avant le point
     * @throws UnableToMoveException
     * @author Nayht
     */
    public void moveNearPoint(Vec2 aim, double distanceNear, String direction) throws UnableToMoveException,ImmobileEnnemyForOneSecondAtLeast {
        Vec2 relativeCoords = aim.minusNewVector(getPosition());
        long distance = Math.round(relativeCoords.getR() - distanceNear);
        if (direction.equals("backward")) {
            turnTo(getPosition().minusNewVector(relativeCoords));
            distance *= -1;
        } else {
            turnTo(aim);
        }
        moveLengthwise((int) distance);
    }

    public void moveNearPointWithoutDetection(Vec2 aim, double distanceNear, String direction) throws UnableToMoveException,ImmobileEnnemyForOneSecondAtLeast {
        Vec2 relativeCoords = aim.minusNewVector(getPosition());
        long distance = Math.round(relativeCoords.getR() - distanceNear);
        if (direction.equals("backward")) {
            turnToWithoutDetection(getPosition().minusNewVector(relativeCoords));
            distance *= -1;
        } else {
            turnToWithoutDetection(aim);
        }
        moveLengthwiseWithoutDetection((int) distance,false);
    }



    /********************************
     * ASSERV', VITESSE & STRATEGIE *
     ********************************/

    /**
     * Active le mouvement forcé (on ignore les conditions de blocage du bas-niveau)
     *
     * @param state oui/non
     */
    public void setForceMovement(boolean state) {
        mLocomotion.setForceMovement(state);
        this.isForcing = true;
    }

    /**
     * Active/Desactive l'asserv'
     */
    public void enableRotationnalFeedbackLoop() {
        mLocomotion.enableRotationnalFeedbackLoop();
    }

    public void disableRotationnalFeedbackLoop() {
        mLocomotion.disableRotationnalFeedbackLoop();
    }

    public void enableFeedbackLoop() {
        mLocomotion.enableFeedbackLoop();
    }

    public void disableFeedbackLoop() {
        mLocomotion.disableFeedbackLoop();
    }

    /**
     * Change la vitesse du robot
     *
     * @param vitesse
     */
    public void setLocomotionSpeed(Speed vitesse) {
        mLocomotion.setTranslationnalSpeed(vitesse.translationSpeed);
        mLocomotion.setRotationnalSpeed(vitesse.rotationSpeed);
        speed = vitesse;
    }

    /**
     * Change la TurningStrategy (au plus rapide, toujours vers la droite, etc...)
     *
     * @param turning
     * @return
     */
    public boolean setTurningStrategy(TurningStrategy turning) {
        if (!(turning == TurningStrategy.FASTEST)) {
            if (symmetry) {
                mLocomotion.setTurningOrders(mTurningStrategyCorrespondenceMap.getSymmetrizedTurningStrategy(turning));
                return true;
            }
            mLocomotion.setTurningOrders(turning);
            return true;
        }
        return false;
    }

    /**
     * Change la DirectionStrategy (au plus rapide, toujours vers l'avant, etc...)
     *
     * @param motion
     * @return
     */
    public boolean setDirectionStrategy(DirectionStrategy motion) {
        if (!(motion == DirectionStrategy.FASTEST)) {
            mLocomotion.setDirectionOrders(motion);
            return true;
        }
        return false;
    }


    /**********
     * DIVERS *
     **********/


    /**
     * Immobilise le robot
     */
    public void immobilise() {
        log.debug("appel de Robot.immobilise()");
        mLocomotion.immobilise();
    }

    /**
     * Coupe la connexion au LL
     */
    public void closeConnexion() {
        ethWrapper.close();
    }

    /**
     * Active/désactive les capteurs
     */
    public void switchSensor() {
        ethWrapper.switchSensor();
    }

    /**
     * Fait attendre le programme
     *
     * @param duree attente en ms
     */
    public void sleep(long duree) {
        Sleep.sleep(duree);
    }


    /***********************************
     * GETTERS & SETTERS (du LL aussi) *
     ***********************************/


    /**
     * Active la détection basique
     *
     * @param basicDetection oui/non
     */
    public void setBasicDetection(boolean basicDetection) {
        mLocomotion.setBasicDetection(basicDetection);
    }

    /**
     * Forcer les valeurs des capteurs dans cet objet
     *
     * @param val les valeurs comme définies dans threadSensors
     */
    public void setUSvalues(ArrayList<Integer> val) {
        mLocomotion.setUSvalues(val);
    }

    /**
     * Renvoie la valeur d'un capteur de contact
     *
     * @param sensor le capteur en question
     * @return l'état logique du capteur
     */
    public boolean getContactSensorValue(ContactSensors sensor) {
        // si il n'y a pas de symétrie, on renvoie la valeur brute du bas niveau
        if (!symmetry)
            return ethWrapper.getContactSensorValue(sensor);
        else {
            sensor = mSensorNamesMap.getSymmetrizedContactSensorName(sensor);

            /* attention si les capteurs sont en int[] il faut symétriser ce int[] */

            return ethWrapper.getContactSensorValue(sensor);
        }
    }

    /**
     * Getters & Setters des positions
     *
     * @param position
     */
    public void setPosition(Vec2 position) {
        mLocomotion.setPosition(position);
    }

    public Vec2 getPosition() {
        position = mLocomotion.getPosition();
        return position;
    }

    public void setOrientation(double orientation) {
        mLocomotion.setOrientation(orientation);
    }

    public double getOrientation() {
        orientation = mLocomotion.getOrientation();
        return orientation;
    }

    public void setRobotRadius(int radius) {
        this.robotRay = radius;
    }

    public int getRobotRadius() {
        return this.robotRay;
    }

    public Speed getLocomotionSpeed() {
        return speed;
    }

    public boolean getIsRobotMovingForward() {
        return mLocomotion.isRobotMovingForward;
    }

    public boolean getIsRobotMovingBackward() {
        return mLocomotion.isRobotMovingBackward;
    }

    public EthWrapper getEthWrapper() {
        return ethWrapper;
    }

    public Pathfinding getPathfinding() {
        return pathfinding;
    }

    /**
     * Met à jour la configuration de la classe via le fichier de configuration fourni par le sysème de container
     * et supprime les espaces (si si c'est utile)
     */
    @Override
    public void updateConfig() {
        symmetry = (config.getString(ConfigInfoRobot.COULEUR).equals("orange")); // TODO : modifier la couleur adverse
        log.debug("contenu de couleur : " + config.getString(ConfigInfoRobot.COULEUR));
        log.debug("symmetry : " + symmetry);
        robotRay = config.getInt(ConfigInfoRobot.ROBOT_RADIUS);
        robotLength = config.getInt(ConfigInfoRobot.ROBOT_LENGTH);
        robotWidth = config.getInt(ConfigInfoRobot.ROBOT_WIDTH);
        position = Table.entryPosition;
        orientation = Math.PI;
    }

    public Locomotion getmLocomotion() {
        return mLocomotion;
    }

}
