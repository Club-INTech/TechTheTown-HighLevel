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
import exceptions.Locomotion.BlockedException;
import exceptions.Locomotion.ImmobileEnnemyForOneSecondAtLeast;
import exceptions.Locomotion.UnableToMoveException;
import pfg.config.Config;
import smartMath.Geometry;
import smartMath.Vec2;
import smartMath.XYO;
import table.Table;
import threads.dataHandlers.ThreadEvents;
import utils.Log;
import utils.Sleep;

import java.util.ArrayList;

/**
 * Entre Deplacement (appels à la série) et RobotVrai (déplacements haut niveau), Locomotion
 * s'occupe de la position, de la symétrie, des trajectoires courbes et des blocages.
 * Structure, du bas au haut niveau: symétrie, trajectoire courbe et blocage.
 * Les méthodes "non-bloquantes" se finissent alors que le robot roule encore.
 * (les méthodes non-bloquantes s'exécutent très rapidement)
 * Les méthodes "bloquantes" se finissent alors que le robot est arrêté.
 *
 * @author pf, rem
 * <p>
 * TODO OPTIONNEL faire une gestion complète des trajectoires courbes
 * https://goo.gl/7HU589
 * <p>
 * TODO : Le isMotionEnded() a changé ! detection de robot bloqué à passer en LL
 */

public class Locomotion implements Service {
    /**
     * le log si on a des erreurs
     */
    private Log log;

    /**
     * la config ...
     */
    private Config config;

    /**
     * la table sur laquelle evolue le robot
     */
    private Table table;

    /**
     * le bas-niveau
     */
    private EthWrapper ethWrapper;

    /*******************
     *   DEPLACEMENT   *
     *******************/

    /**
     * Position "bas niveau" du robot, celle du robot
     * La vraie.
     */
    private Vec2 lowLevelPosition = new Vec2();

    /**
     * Position "haut niveau" du robot, celle du robot
     * Celle côté vert
     */
    private Vec2 highLevelPosition = new Vec2();

    /**
     * Position visee au final par le deplacement
     */
    private Vec2 finalAim = new Vec2();

    /**
     * Orientation réelle du robot (symetrisee)
     * non connue par les classes de plus haut niveau
     */
    private double lowLevelOrientation;

    /**
     * Orientation réelle du robot non symetrisée (vert)
     */
    private double highLevelOrientation;

    /**
     * Indique si la symétrie est activée (si le robot démarre du côté x<0)
     * La symétrie s'applique sur les déplacements et les actionneurs
     * Override par la config
     */
    private boolean symetry;

    /**
     * Indique si le robot est en marche avant, utile pour les capteurs
     */
    public boolean isRobotMovingForward;

    /**
     * Indique si le robot est en marche arrière, utile pour les capteurs
     */
    public boolean isRobotMovingBackward;

    /**
     * Donne la stratégie de rotation
     */
    private TurningStrategy turningStrategy = TurningStrategy.FASTEST;

    /**
     * Donne la stratégie de translation
     */
    private DirectionStrategy directionStrategy = DirectionStrategy.FASTEST;

    /**
     * Vitesse de translation
     */
    private double transSpeed = Speed.MEDIUM_ALL.translationSpeed;

    /**
     * Vitesse de rotation
     */
    private double rotSpeed = Speed.MEDIUM_ALL.rotationSpeed;

    /**
     * Si le robot est censé forcer le mouvement
     */
    private boolean isForcing = false;

    /*****************
     *   DETECTION   *
     *****************/

    /**
     * Si la détection basique est activée ou non
     */
    private boolean basicDetectionActivated = true;

    /**
     * Si on utilise la basic detection
     * Override par la config
     */
    private boolean usingBasicDetection;

    /**
     * On regarde si on utilise l'IA ou non
     */
    private boolean advancedDetection;

    /**
     * Rayon du cercle autour du robot pour savoir s'il peut tourner (detectionRay légèrement supérieur à celui du robot)
     * La zone de détection d'obstacle est un disque comme suit:
     * <p>
     * o    o
     * o  +----+  o
     * robot ->  o   |    |   o
     * o   |    |   o
     * o  +----+	o
     * o    o
     * <p>
     * Override par la config
     */
    private int detectionRay;

    /**
     * Distance de detection : rectangle de détection :
     * <p>
     * +-----------+
     * +----+        |          Sens de déplacement du robot: ====>
     * robot ->  |    |        |
     * |    |        |
     * +----+        |
     * +-----------+
     * <------->
     * detectionDistance
     * <p>
     * Override par la config
     */
    private int detectionDistance;

    /**
     * Temps d'attente lorsqu'il y a un ennemie devant
     * Override par la config
     */
    private int basicDetectionLoopDelay;

    /**
     * Temps d'attente entre deux boucles d'acquitement
     * Override par la config
     */
    private int feedbackLoopDelay;

    /**
     * Valeurs des ultrasons filtrés par le LL pour la détection basique
     */
    private ArrayList<Integer> USvalues;

    /*************************
     *   BLOCAGE MECANIQUE   *
     *************************/

    /**
     * La distance dont le robot va avancer pour se dégager en cas de bloquage mécanique
     * Override par la config
     */
    private int distanceToDisengage;

    /**
     * Nombre d'essais maximum après une BlockedException
     * Override par la config
     */
    private int maxRetriesIfBlocked;

    /**
     * Nombre d'essais en cours après un BlockedException
     */
    private int actualRetriesIfBlocked = 0;

    /**
     * Temps prévu de fin de mouvement
     */
    private long timeExpected = 0;

    /**
     * ThreadEvent
     */
    private ThreadEvents thEvent;

    /**
     * Constructeur de Locomotion
     *
     * @param log        le fichier de log
     * @param config     le fichier de config
     * @param table      la table de jeu
     * @param ethWrapper protocole de communication série
     */
    private Locomotion(Log log, Config config, Table table, EthWrapper ethWrapper, ThreadEvents thEvent) {
        this.log = log;
        this.config = config;
        this.ethWrapper = ethWrapper;
        this.table = table;
        this.USvalues = new ArrayList<Integer>() {{
            for (int i = 0; i < 4; i++) add(0);
        }};
        this.thEvent = thEvent;
        updateConfig();
    }


    /****************************
     * GESTION DE LA TRAJECTOIRE *
     ***************************/


    /**
     * Suit un chemin en ligne brisee
     *
     * @param path le chemin a suivre (un arraylist de Vec2 qui sont les point de rotation du robot)
     * @throws UnableToMoveException si le robot a un bloquage mecanique
     */
    public void followPath(ArrayList<Vec2> path) throws UnableToMoveException,ImmobileEnnemyForOneSecondAtLeast {
        followPath(path, true);// par defaut, on detecte
    }

    /**
     * Suit un chemin en ligne brisee
     *
     * @param path       le chemin a suivre (un arraylist de Vec2 qui sont les point de rotation du robot)
     * @param mustDetect true si on veut detecter, false sinon.
     * @throws UnableToMoveException si le robot a un bloquage mecanique
     */
    public void followPath(ArrayList<Vec2> path, boolean mustDetect) throws UnableToMoveException,ImmobileEnnemyForOneSecondAtLeast {
        //On enleve le premier point, notre propre position
        for (int i = 1; i < path.size(); i++){
            getCurrentPositionAndOrientation();
            Vec2 aim = path.get(i);
            finalAim = aim;
            log.debug("Pathfinding : going to node of coords "+aim.toStringEth());
            moveToPoint(aim, false, mustDetect);
        }
    }

    /**
     * Effectue un trajet en ligne droite vers le point visé en gérant la DirectionStrategy
     * ATTENTION l'intersection avec les obstacles n'est pas gérée...
     *
     * @param pointVise
     * @param expectedWallImpact
     * @param mustDetect
     * @throws UnableToMoveException
     */

    public void moveToPoint(Vec2 pointVise, boolean expectedWallImpact, boolean mustDetect) throws UnableToMoveException,ImmobileEnnemyForOneSecondAtLeast {

        thEvent.setIsMoving(true);
        log.debug("isMoving variable has been defined to True");

        Vec2 move = pointVise.minusNewVector(highLevelPosition);
        int moveR = (int) move.getR();
        double moveA = move.getA();

        if (directionStrategy.equals(DirectionStrategy.FASTEST)) {
            float sens = move.dot(new Vec2(100, highLevelOrientation));
            if (sens >= 0) {    //si il est orienté vers l'avant par rapport au point visé (produit scalaire > 0)
                log.debug("Angle de rotation: " + moveA);
                log.debug("Distance de translation: " + moveR);
                turn(moveA, expectedWallImpact, mustDetect);
                moveLengthwise(moveR, expectedWallImpact, mustDetect);
            }
            //si il est orienté vers l'arrière par rapport au point visé
            else{
                moveA = Geometry.moduloSpec(moveA-Math.PI, Math.PI);
                turn(moveA, expectedWallImpact, mustDetect);
                moveLengthwise(-moveR, expectedWallImpact, mustDetect);
            }
        }
        if (directionStrategy.equals(DirectionStrategy.FORCE_BACK_MOTION)) {
            moveA = Geometry.moduloSpec(moveA + Math.PI, Math.PI);
            turn(moveA, expectedWallImpact, mustDetect);
            moveLengthwise(-moveR, expectedWallImpact, mustDetect);
        } else if (directionStrategy.equals(DirectionStrategy.FORCE_FORWARD_MOTION)) {
            turn(moveA, expectedWallImpact, mustDetect);
            moveLengthwise(moveR, expectedWallImpact, mustDetect);
        }
    }


    /*********************************************
     * FONCTIONS GERANT LES MOUVEMENTS UNITAIRES *
     ********************************************/

    /**
     * Fait tourner le robot (méthode bloquante)
     * Une manière de tourner qui réutilise le reste du code, car tourner
     * n'en devient plus qu'un cas particulier (celui où... on n'avance pas)
     *
     * @param angle      l'angle vise (en absolut)
     * @param mustDetect true si on veut detecter, false sinon.
     * @throws UnableToMoveException si le robot a un bloquage mecanique
     */
    public void turn(double angle, boolean expectWallImpact, boolean mustDetect) throws UnableToMoveException,ImmobileEnnemyForOneSecondAtLeast {
        thEvent.setIsMoving(true);
        log.debug("isMoving variable has been defined to True");
        log.debug("Tourner vers " + Double.toString(angle));

        actualRetriesIfBlocked = 0;
        getCurrentPositionAndOrientation();

        /**
         * calcul de la position visee du haut niveau
         *   on vise une position eloignee mais on ne s'y deplacera pas, le robot ne fera que tourner
         */
        Vec2 aim = highLevelPosition.plusNewVector(new Vec2(1000.0, angle));
        finalAim = aim;

        moveToPointHandledExceptions(aim, true, expectWallImpact, true, mustDetect);
        isRobotMovingForward = false;
        isRobotMovingBackward = false;
    }

    /**
     * Fait avancer le robot de "distance" (en mm), et vérifie avant s'il n'y a pas d'obstacle proche sur le chemin avant d'avancer
     * Si la distance spécifiée est négative, le robot recule !
     *
     * @param distance         la distance dont le robot doit se deplacer
     * @param expectWallImpact vrai si on supppose qu'on vas se cogner dans un mur (et qu'il ne faut pas pousser dessus)
     * @param mustDetect       true si on veut detecter, false sinon.
     * @throws UnableToMoveException si le robot a un bloquage mecanique
     */
    public void moveLengthwise(int distance, boolean expectWallImpact, boolean mustDetect) throws UnableToMoveException,ImmobileEnnemyForOneSecondAtLeast {
        thEvent.setIsMoving(true);
        log.debug("isMoving variable has been defined to True");

        log.debug("Avancer de " + Integer.toString(distance));

        actualRetriesIfBlocked = 0;
        getCurrentPositionAndOrientation();

        Vec2 aim = highLevelPosition.plusNewVector(new Vec2((double)distance, highLevelOrientation));
        finalAim = aim;

        /** TODO A adapté à l'annee en cours */
        int totalTime = 0;
        boolean isEnemy = table.getObstacleManager().isEnnemyForwardOrBackWard(detectionDistance, highLevelPosition, aim, highLevelOrientation);

        if (distance >= 0) {
            isRobotMovingForward = true;
        }
        else {
            isRobotMovingBackward = true;
        }

        moveToPointHandledExceptions(aim, (distance>=0), expectWallImpact, false, mustDetect);

        isRobotMovingForward = false;
        isRobotMovingBackward = false;
    }

    /**
     * Bloquant
     * Gère les exceptions, c'est-à-dire les rencontres avec l'ennemi et les câlins avec un mur.
     *
     * @param aim               la position visee sur la table (consigne donné par plus haut niveau donc non symetrise)
     * @param isMovementForward vrai si on va en avant et faux si on va en arriere
     * @param expectWallImpact  vrai si on suppose qu'on vas se cogner dans un mur (et qu'on veut s'arreter des qu'on cogne)
     * @param turnOnly          vrai si on veut uniquement tourner (et pas avancer)
     * @param mustDetect        true si on veut detecter, false sinon.
     * @throws UnableToMoveException si le robot a un bloquage mecanique
     */
    private void moveToPointHandledExceptions(Vec2 aim, boolean isMovementForward, boolean expectWallImpact, boolean turnOnly, boolean mustDetect) throws UnableToMoveException,ImmobileEnnemyForOneSecondAtLeast {
        boolean doItAgain;
        do {
            doItAgain = false;

            try {
                moveToPointDetectExceptions(aim, isMovementForward, turnOnly, mustDetect);
                isRobotMovingForward = false;
                isRobotMovingBackward = false;
            }
//            catch (BlockedException e) {
//                log.critical(e.logStack());
//                log.critical("Haut : Catch de " + e.getMessage() + " dans moveToPointException");
//
//                /** Si on ne s'y attendait pas, on réagit en se dégageant légèrement avant de retenter : si on n'y
//                 * arrive pas, on balance une UnableToMoveException(PHYSICALLY_BLOCKED) à l'IA
//                 * TODO A adapter à l'année en cours
//                 */
//                if (!expectWallImpact && !isForcing) {
//                    immobilise();
//                    actualRetriesIfBlocked++;
//
//                    try {
//                        log.warning("On est bloqué : on se dégage !");
//                        if (turnOnly) {
//                            /** TODO A faire ! cas ou on doit se dégager alors que l'on a cogné un mur en tournant...
//                             /* Bonne chance pour trouver le sens vers lequel on doit tourner :p ! */
//                        } else if (isMovementForward) {
//                            isRobotMovingForward = false;
//                            isRobotMovingBackward = true;
//                            moveToPointDetectExceptions(highLevelPosition.minusNewVector(new Vec2(distanceToDisengage, highLevelOrientation)), false, false, true);
//                        } else {
//                            isRobotMovingForward = true;
//                            isRobotMovingBackward = false;
//                            moveToPointDetectExceptions(highLevelPosition.plusNewVector(new Vec2(distanceToDisengage, highLevelOrientation)), true, false, true);
//                        }
//                        // Si l'on arriver jusq'ici, c'est qu'aucune exception n'a été levé
//                        doItAgain = (actualRetriesIfBlocked < maxRetriesIfBlocked);
//                    } catch (BlockedException definitivelyBlocked) {
//                        /** Cas très improbable... on balance à l'IA */
//                        immobilise();
//                        log.critical(definitivelyBlocked.logStack());
//                        log.debug("Catch de " + definitivelyBlocked + " dans moveToPointHandleException");
//                        log.critical("Lancement de UnableToMoveException dans MoveToPointException, visant " + finalAim.toString() + " cause physique");
//                        throw new UnableToMoveException(finalAim, UnableToMoveReason.PHYSICALLY_BLOCKED);
//                    }
//                } else if (!expectWallImpact) {
//                    log.critical("Lancement de UnableToMoveException dans MoveToPointException, visant " + finalAim.getX() + " :: " + finalAim.getY() + " cause physique");
//                    throw new UnableToMoveException(finalAim, UnableToMoveReason.PHYSICALLY_BLOCKED);
//                }
//            }
            catch(BlockedException e){
                if (expectWallImpact || isForcing) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    immobilise();
                }
                else{
                    immobilise();
                    throw new UnableToMoveException(finalAim, UnableToMoveReason.PHYSICALLY_BLOCKED);
                }
            }

        }
        while (doItAgain);
    }

    /**
     * Bloquant.
     * Gère la détection de l'adversaire
     *
     * @param aim               la position visee sur la table (consigne donné par plus haut niveau donc non symetrise)
     * @param isMovementForward vrai si on va en avant et faux si on va en arriere
     * @param turnOnly          vrai si on veut uniquement tourner (et pas avancer)
     * @param mustDetect        true si on veut detecter, false sinon.
     * @throws BlockedException
     */
    private void moveToPointDetectExceptions(Vec2 aim, boolean isMovementForward, boolean turnOnly, boolean mustDetect) throws BlockedException, ImmobileEnnemyForOneSecondAtLeast, UnableToMoveException {
        thEvent.setIsMoving(true);
        boolean orderSent=false;
        while (thEvent.getIsMoving()) {

            getCurrentPositionAndOrientation();

            if (thEvent.getUnableToMoveEvent().peek() != null) {
                String unableToMoveReason = thEvent.getUnableToMoveEvent().poll();
                if (unableToMoveReason.equals(UnableToMoveReason.PHYSICALLY_BLOCKED.getSerialOrder())) {
                    throw new BlockedException();
                }
            }

            // TODO A adapté à l'année en cours
            if (mustDetect) {
                if (!advancedDetection){
                    if (usingBasicDetection) {
                        if (basicDetectionActivated) {
                            boolean obstacleDetected = basicDetect();
                            boolean wasImmobilised = false;
                            if (obstacleDetected) {
                                immobilise();
                                wasImmobilised = true;
                            }
                            while (obstacleDetected) {
                                try {
                                    Thread.sleep(basicDetectionLoopDelay);
                                    obstacleDetected = basicDetect();
                                } catch (InterruptedException e) {
                                    log.debug("Interruption du sleep de la basicDetection, on sort de la boucle");
                                    obstacleDetected = false;
                                    e.printStackTrace();
                                }
                            }
                            if (wasImmobilised) {
                                updateCurrentPositonAndOrientation();
                                moveToPointDetectExceptions(aim, isMovementForward, turnOnly, mustDetect);
                            }
                        }
                    }
                }
                else{
                    if (basicDetectionActivated){
                        boolean obstacleDetected=basicDetect();
                        if (obstacleDetected){
                            immobilise();
                            throw new UnableToMoveException(aim,UnableToMoveReason.OBSTACLE_DETECTED);
                        }
                    }
                    if (turnOnly){
                        try{
                            boolean hasDetectedSomething = detectEnemyArroundPosition(detectionRay);
                            if (hasDetectedSomething){
                                updateCurrentPositonAndOrientation();
                                moveToPointDetectExceptions(aim, isMovementForward, turnOnly, mustDetect);
                            }
                        }
                        catch (InterruptedException e){
                            e.printStackTrace();
                        }
                        catch(ImmobileEnnemyForOneSecondAtLeast e){
                            log.debug("aimImmobileEnnemySecondAtLeast : " + aim);
                            throw new ImmobileEnnemyForOneSecondAtLeast(aim);
                        }
                    }
                    else{
                        try{
                            boolean hasDetectedSomething = detectEnemyAtDistance(detectionDistance, aim.minusNewVector(highLevelPosition));
                            if (hasDetectedSomething){
                                updateCurrentPositonAndOrientation();
                                moveToPointDetectExceptions(aim, isMovementForward, turnOnly, mustDetect);
                            }
                        }
                        catch(InterruptedException e){
                            e.printStackTrace();
                        }
                        catch(ImmobileEnnemyForOneSecondAtLeast e){
                            log.debug("aimImmobileEnnemySecondAtLeast : " + aim);
                            throw new ImmobileEnnemyForOneSecondAtLeast(aim);
                        }
                    }
                }
                //On le met à la fin, afin de savoir si on détecte déjà un obstacle, et le cas échéant directement renoyer une exception
                if (!orderSent) {
                    orderSent=true;
                    moveToPointSymmetry(aim, turnOnly);
                }
            }
        }
    }

    /**
     * Non bloquant.
     * Gère la symétrie (si on est en marche arriere le aim doit etre modifié pour que la consigne vers le bas niveau soit bonne)
     *
     * @param aim      la position visee sur la tab le (consigne donné par plus haut niveau donc non symetrise)
     * @param turnOnly vrai si on veut uniquement tourner (et pas avancer)
     */
    private void moveToPointSymmetry(Vec2 aim, boolean turnOnly) {
        thEvent.setIsMoving(true);
        getCurrentPositionAndOrientation();

        Vec2 positionSymetrized = highLevelPosition.clone();
        Vec2 aimSymetrized = aim.clone();

        if (symetry) {
            positionSymetrized.setX(-positionSymetrized.getX());
            aimSymetrized.setX(-aimSymetrized.getX());
        }

        Vec2 delta = aimSymetrized.minusNewVector(positionSymetrized);
        log.debug("HighLevelOrientation: "+highLevelOrientation+" / HighLevelPosition: "+highLevelPosition);
        if (!turnOnly) {
            double produitScalaire = delta.dot(new Vec2(100.0, lowLevelOrientation));
            if (produitScalaire > 0) {
                moveToPointEthernetOrder(delta.getA(), delta.getR(), turnOnly);
            } else {
                moveToPointEthernetOrder(delta.getA(), -delta.getR(), turnOnly);
            }
        } else {
            moveToPointEthernetOrder(delta.getA(), delta.getR(), turnOnly);
        }
    }

    /**
     * Non bloquant.
     * Avance, envoi a la serie
     *
     * @param angle    l'angle dont il faut tourner (ordre pour la serie)
     * @param distance la distance dont il faut avancer (ordre pour la serie)
     * @param turnOnly vrai si on veut uniquement tourner (et pas avancer)
     */
    private void moveToPointEthernetOrder(double angle, double distance, boolean turnOnly) {
        thEvent.setIsMoving(true);
        if (turnOnly) {
            ethWrapper.turn(angle, turningStrategy);
        } else {
            ethWrapper.moveLengthwise(distance);
        }
    }


    /**************************
     * FONCTIONS DE DETECTION *
     *************************/

    /**
     * Il s'agit d'une méthode qui throw une exception si la basic detection est activée,
     * que le LL détecte qqch à une distance qu'on set, cette exception sera catched par
     * le movetopointhandledexceptions qui immobilisera le robot
     */
    private boolean basicDetect() {
        return thEvent.isObstacleBasicDetected();
    }

    /**
     * Lance une exception si un ennemi se trouve a une distance inférieure a celle spécifiée
     *
     * @param distance distance jusqu'a un ennemi en mm en dessous de laquelle on doit abandonner le mouvement
     */
    public boolean detectEnemyArroundPosition(int distance) throws InterruptedException,ImmobileEnnemyForOneSecondAtLeast {
        int closest = table.getObstacleManager().distanceToClosestEnemy(highLevelPosition);
        boolean hasDetectedSomething=false;
        if (closest <= distance) {
            hasDetectedSomething=true;
            log.debug("Closest ennemy detected (arroundPosition) at distance: "+closest);
            log.debug("DetectEnemyAtDistance voit un ennemi trop proche pour continuer le déplacement (distance de "
                    + closest + " mm)");
            immobilise();
            int count = 0;


            while(count < 100)
            {
                //on teste si l'ennemi n'a pas bougé depuis, au bout d'une seconde on l'ajoute dans la liste des obstacles à fournir au graphe
                closest = table.getObstacleManager().distanceToClosestEnemy(highLevelPosition);
                if(closest > distance){
                    break;
                }
                Thread.sleep(10);
                count++;
            }

            if(closest <= distance){
                table.getObstacleManager().getMobileObstacles().add(table.getObstacleManager().getClosestEnnemy(highLevelPosition));
                log.debug("ImmobileEnnemy est thrown");
                throw new ImmobileEnnemyForOneSecondAtLeast(new Vec2());
            }
        }
        return hasDetectedSomething;
    }

    /**
     * Lance une exception si un ennemi se trouve sur le chemin à "detection Distance"
     *
     * @param moveDirection direction du robot
     */
    public boolean detectEnemyAtDistance(int distance, Vec2 moveDirection) throws InterruptedException,ImmobileEnnemyForOneSecondAtLeast{
        boolean a =table.getObstacleManager().isEnnemyForwardOrBackWard(distance, highLevelPosition, moveDirection, highLevelOrientation);
        boolean hasDetectedSomething = false;
        if (a) {
            hasDetectedSomething=true;
            log.debug("Ennemy detected at distance(<"+distance+"mm): "+a);
            log.debug("DetectEnemyAtDistance voit un ennemi sur le chemin : le robot va s'arrêter");
            immobilise();
            int count = 0;

            while(count < 50)
            {
                if(!table.getObstacleManager().isEnnemyForwardOrBackWard((int)(distance*1.2), highLevelPosition, moveDirection, highLevelOrientation)){
                    break;
                }
                Thread.sleep(10);
                count++;
            }

            //on teste si l'ennemi n'a pas bougé depuis, au bout d'une seconde on l'ajoute dans la liste des obstacles à fournir au graphe
            if(table.getObstacleManager().isEnnemyForwardOrBackWard(distance, highLevelPosition, moveDirection, highLevelOrientation)){
                log.debug("ImmobileEnnemy est throw");
                throw new ImmobileEnnemyForOneSecondAtLeast(new Vec2());
            }
        }
        return hasDetectedSomething;
    }


    /*****************************************
     * FONCTIONS DE COMMUNICATION AVEC LE LL *
     ****************************************/


    /**
     * Boucle d'acquittement générique. Retourne des valeurs spécifiques en cas d'arrêt anormal (blocage, capteur)
     * <p>
     * false : si on roule
     * true : si on est arrivé à destination
     * exception : si patinage
     *
     * @return oui si le robot est arrivé à destination, non si encore en mouvement
     * @throws BlockedException si patinage (donc bloquage mecanique)
     */
    private boolean isMotionEnded() throws BlockedException {

        // récupérations des informations d'acquittement
        boolean[] infos = ethWrapper.isRobotMovingAndAbnormal();
        // 0-false : le robot ne bouge pas

        //log.debug("Test deplacement : reponse "+ infos[0] +" :: "+ infos[1], this);

        if (!infos[0])//si le robot ne bouge plus
        {
            if (infos[1])//si le robot patine, il est bloqué
            {
                log.critical("Robot bloqué, lancement de BlockedException dans isMotionEnded");
                throw new BlockedException();
            } else {
                return !infos[0];//On est arrivés
            }
        } else if (isForcing && System.currentTimeMillis() > this.timeExpected) {
            log.critical("Le robot force, on l'arrête.");
            this.immobilise();
            throw new BlockedException();
        } else {
            return !infos[0];//toujours pas arrivé
        }
    }

    /**
     * Met à jour la position du robot dans Locomotion avec la dernière position renvoyée par le LL
     */
    private void getCurrentPositionAndOrientation() {
        XYO positionAndOrientation = ethWrapper.getCurrentPositionAndOrientation();

        lowLevelPosition = positionAndOrientation.getPosition();
        lowLevelOrientation = Geometry.moduloSpec(positionAndOrientation.getOrientation(), Math.PI);

        highLevelPosition = lowLevelPosition.clone();
        highLevelOrientation = lowLevelOrientation;

        if (symetry) {
            highLevelPosition.setX(-highLevelPosition.getX());
            highLevelOrientation = Geometry.moduloSpec(Math.PI - lowLevelOrientation, Math.PI);
        }
    }

    /**
     * Force l'envoi de la position actuelle par le LL, et la met à jour dans Locomotion et ThreadEth
     */
    private void updateCurrentPositonAndOrientation(){
        XYO positionAndOrientation = ethWrapper.updateCurrentPositionAndOrientation();

        lowLevelPosition = positionAndOrientation.getPosition();
        lowLevelOrientation = Geometry.moduloSpec(positionAndOrientation.getOrientation(), Math.PI);

        highLevelPosition = lowLevelPosition.clone();
        highLevelOrientation = lowLevelOrientation;

        if (symetry) {
            highLevelPosition.setX(-highLevelPosition.getX());
            highLevelOrientation = Geometry.moduloSpec(Math.PI - lowLevelOrientation, Math.PI);
        }
    }

    /**
     * Arrête le robot.
     */
    public void immobilise() {
        log.warning("Arrêt du robot en " + lowLevelPosition);
        ethWrapper.immobilise();
        thEvent.setIsMoving(false);
        log.debug("isMoving variable has been defined to FALSE in Locomotion");
    }


    /********************
     * GUETTER & SETTER *
     *******************/


    /**
     * Met à jour la position. A ne faire qu'en début de match, ou en cas de recalage
     * @param positionWanted
     */
    public void setPosition(Vec2 positionWanted) {
        this.lowLevelPosition = positionWanted.clone();
        this.highLevelPosition = positionWanted.clone();
        if (symetry) {
            this.lowLevelPosition.setX(-this.lowLevelPosition.getX()); // on lui met la vraie position
        }
        ethWrapper.setX(this.lowLevelPosition.getX());
        ethWrapper.setY(this.lowLevelPosition.getY());

        Sleep.sleep(300);
    }

    /**
     * @return la position du robot en debut de match
     */
    public Vec2 getPosition() {
        getCurrentPositionAndOrientation();
        Vec2 out = highLevelPosition.clone();
        return out;
    }

    /**
     * Met à jour l'orientation. A ne faire qu'en début de match, ou en cas de recalage
     *
     * @param orientation
     */
    public void setOrientation(double orientation) {
        this.lowLevelOrientation = orientation;
        this.highLevelOrientation = orientation;
        if (symetry) {
            this.lowLevelOrientation = Math.PI - this.lowLevelOrientation; // la vraie orientation
        }
        ethWrapper.setOrientation(this.lowLevelOrientation);
    }

    /**
     * @return l'orientation du robot en debut de match
     */
    public double getOrientation() {
        getCurrentPositionAndOrientation();
        return highLevelOrientation;
    }

    /**
     * De meme que la position mais pour l'orientation
     */
    public double getOrientationFast() {
        return highLevelOrientation;
    }

    /**
     * Permet au ThreadSensor de mettre à jour la valeur des capteurs,
     * utile pour la BasicDetection et pour la vérification d'obstacles lors d'appels directes à la série
     * (le dégagement dans la BlockedException)
     */
    public void setUSvalues(ArrayList<Integer> val) {
        this.USvalues = val;
    }

    /**
     * Stratégie de déplacement
     */
    public TurningStrategy getTurningOrders() {
        return turningStrategy;
    }

    public void setTurningOrders(TurningStrategy turning) {
        this.turningStrategy = turning;
    }

    public DirectionStrategy getDirectionStrategy() {
        return directionStrategy;
    }

    public void setDirectionOrders(DirectionStrategy motion) {
        this.directionStrategy = motion;
    }

    /**
     * Désactive l'asservissement en position et celui en vitesse (YOLO)
     */
    public void disableFeedbackLoop() {
        ethWrapper.disableRotationnalFeedbackLoop();
        ethWrapper.disableTranslationnalFeedbackLoop();
        ethWrapper.disableSpeedFeedbackLoop();
    }

    /**
     * Active l'asservissement en position et celui en vitesse
     */
    public void enableFeedbackLoop() {
        ethWrapper.enableRotationnalFeedbackLoop();
        ethWrapper.enableTranslationnalFeedbackLoop();
        ethWrapper.enableSpeedFeedbackLoop();
    }

    /**
     * Activation/désactivation des différents asservissements
     */
    public void disableRotationnalFeedbackLoop() {
        ethWrapper.disableRotationnalFeedbackLoop();
    }

    public void enableRotationnalFeedbackLoop() {
        ethWrapper.enableRotationnalFeedbackLoop();
    }

    public void disableTranslationalFeedbackLoop() {
        ethWrapper.disableTranslationnalFeedbackLoop();
    }

    public void enableTranslationalFeedbackLoop() {
        ethWrapper.enableTranslationnalFeedbackLoop();
    }

    public void disableSpeedFeedbackLoop() {
        ethWrapper.disableSpeedFeedbackLoop();
    }

    public void enableSpeedFeedbackLoop() {
        ethWrapper.enableSpeedFeedbackLoop();
    }

    /**
     * Vitesse de déplacement
     */
    public void setRotationnalSpeed(double rotationSpeed) {
        ethWrapper.setRotationnalSpeed(rotationSpeed);
        this.rotSpeed = rotationSpeed;
    }

    public void setTranslationnalSpeed(float speed) {
        ethWrapper.setTranslationnalSpeed(speed);
        this.transSpeed = speed;
    }

    public void setBothSpeed(Speed speed){
        this.setTranslationnalSpeed(speed.getTranslationSpeed());
        this.setRotationnalSpeed(speed.getRotationSpeed());
    }

    /**
     * Change le type de mouvement forcé/normal
     *
     * @param choice true pour forcer les mouvements
     */
    public synchronized void setForceMovement(boolean choice) {
        if (isForcing != choice) {
            ethWrapper.setForceMovement(choice);
            this.isForcing = choice;
        }
    }

    /**
     * Active/désactive la basicDetection
     */
    public void setBasicDetection(boolean basicDetection) {
        this.basicDetectionActivated = basicDetection;
    }

    @Override
    public void updateConfig() {
        //TODO : remplir la config !!
        symetry = (config.getString(ConfigInfoRobot.COULEUR).equals("orange"));

        /** Detection & Ennemy */
        detectionDistance = config.getInt(ConfigInfoRobot.DETECTION_DISTANCE);
        detectionRay = config.getInt(ConfigInfoRobot.DETECTION_RAY);
        feedbackLoopDelay = config.getInt(ConfigInfoRobot.FEEDBACK_LOOPDELAY);
        usingBasicDetection=config.getBoolean(ConfigInfoRobot.BASIC_DETECTION);
        advancedDetection=config.getBoolean(ConfigInfoRobot.ADVANCED_DETECTION);

        basicDetectionLoopDelay = config.getInt(ConfigInfoRobot.BASIC_DETECTION_LOOP_DELAY);

        /** BlockedException */
        distanceToDisengage = config.getInt(ConfigInfoRobot.DISTANCE_TO_DISENGAGE);
        maxRetriesIfBlocked = config.getInt(ConfigInfoRobot.MAX_RETRIES_IF_BLOCKED);
    }

    public ThreadEvents getThEvent() {
        return thEvent;
    }
    /**************************************************
     * 					JUNITS                        *
     **************************************************/

    // Aller Clément, tu peux le faire !
    // Je te laisse le carte blanche, tu fera mieux cette fois ci ^^ (2013-2014 !)
}