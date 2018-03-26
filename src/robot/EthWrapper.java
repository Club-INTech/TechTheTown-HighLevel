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
import hook.HookNames;
import pfg.config.Config;
import smartMath.Vec2;
import smartMath.XYO;
import threads.dataHandlers.ThreadEth;
import utils.Log;
import utils.Sleep;

import java.util.Arrays;
import java.util.Locale;

/**
 * Prototype de wrapper pour Ethernet incluant toutes les méthodes. Diffère des différents CardWrapper car tout y
 * est centralisé. Les CardWrapper étaient utiles quand on avait plusieures AVRs pour les différentes fonctions, désormais
 * tout est centralisé sur l'ARM, ces CardWrappers ne sont donc que des reliques à éliminer.
 *
 * @author rem
 */
public class EthWrapper implements Service {

    /** Service de log à utilisé */
    private Log log;

    /** Connexion ethernet */
    private ThreadEth eth;

    /** Gestion de la config */
    private Config config;

    /** Delay pour les loops */
    private int loopDelay;

    /** Etat des capteurs */
    private boolean sensorState;

    /**
     * Constructeur EthWrapper
     * @param config
     * @param log
     * @param threadEth
     */
    public EthWrapper(Config config, Log log, ThreadEth threadEth){
        this.log = log;
        this.config = config;
        this.eth = threadEth;

        // Par défaut, les capteurs sont désactivés
        sensorState = false;
        updateConfig();
    }


    /*****************************
     * LOCOMOTION & USE ACTUATOR *
     *****************************/


    /**
     * Fait avancer le robot. Méthode non bloquante
     * @param distance distance a parcourir par le robot. Une valeur négative fera reculer le robot, une valeur positive le fera avancer.
     */
    public void moveLengthwise(double distance)
    {
        int distanceTruncated=(int)Math.round(distance);
        eth.communicate(0, ActuatorOrder.MOVE_LENTGHWISE.getEthernetOrder(), String.format(Locale.US, "%d", distanceTruncated));
    }

    /**
     * Fait tourner le robot de maniere absolue. Méthode non bloquante
     * utilise TurningStrategy.FASTEST
     * @param angle l'angle de tour
     */
    public void turn(double angle)
    {
        turn(angle, TurningStrategy.FASTEST);
    }

    /**
     * Fait tourner le robot de maniere absolue. Méthode non bloquante
     * @param angle l'angle de tour
     */
    public void turn(double angle, TurningStrategy turning)
    {
        // tronque l'angle que l'on envoit a la série pour éviter les overflows
        float angleTruncated = (float)angle;
        if(turning == TurningStrategy.FASTEST) {
            eth.communicate(0, ActuatorOrder.TURN.getEthernetOrder(), String.format(Locale.US, "%.3f", angleTruncated));
        }
        else if(turning == TurningStrategy.RIGHT_ONLY)
        {
            eth.communicate(0, ActuatorOrder.TURN_RIGHT_ONLY.getEthernetOrder(), String.format(Locale.US, "%.3f", angleTruncated));
        }
        else if(turning == TurningStrategy.LEFT_ONLY)
        {
            eth.communicate(0, ActuatorOrder.TURN_LEFT_ONLY.getEthernetOrder(), String.format(Locale.US, "%.3f", angleTruncated));
        }
    }

    /**
     * Arrête le robot
     */
    public void immobilise()
    {
        log.warning("Immobilisation du robot");
        eth.communicate(0, ActuatorOrder.STOP.getEthernetOrder());// On s'asservit sur la position actuelle
        while(isRobotMoving())
        {
            Sleep.sleep(loopDelay); // On attend d'etre arreté
        }

    }

    /**
     * Regarde si le robot bouge effectivement.
     * Provoque un appel série pour avoir des information a jour. Cette méthode est demande donc un peu de temps.
     * @return true si le robot bouge
     */

    public boolean isRobotMoving()
    {
        return isRobotMovingAndAbnormal()[0];
    }

    /**
     *  Verifie si le robot est arrivé et si c'est anormal
     *  @return Les informations sous forme d'un tableau de booleens
     *  lecture : [est ce qu'on bouge][est ce que c'est Anormal]
     */
    public boolean[] isRobotMovingAndAbnormal()
    {
        // on demande a la carte des information a jour
        // on envois "f" et on lis double (dans l'ordre : bouge, est anormal)
        String[] infosBuffer = eth.communicate(2, ActuatorOrder.IS_ROBOT_MOVING.getEthernetOrder());
        boolean[] parsedInfos = new boolean[2];
        for(int i = 0; i < 2; i++)
        {
            if( infosBuffer[i].equals("0") )
                parsedInfos[i] = false;
            else if ( infosBuffer[i].equals("1") )
                parsedInfos[i]=true;
            else
                log.debug("Probleme de lecture de f");
        }

        return parsedInfos;
    }

    /**
     * Modifie la vitesse en translation du robot sur la table
     * @param speed la nouvelle valeur maximum que peut prenvent prendre les pwm des moteurs lors d'une translation
     */
    public void setTranslationnalSpeed(float speed)
    {
        eth.communicate(0, ActuatorOrder.SET_TRANSLATION_SPEED.getEthernetOrder(), String.format(Locale.US, "%.3f", speed));
    }

    /**
     * Modifie la vitesse en rotation du robot sur la table
     * @param rotationSpeed la nouvelle valeur maximum que peut prenvent prendre les pwm des moteurs lors d'une rotation
     */
    public void setRotationnalSpeed(double rotationSpeed)
    {
        eth.communicate(0, ActuatorOrder.SET_ROTATIONNAL_SPEED.getEthernetOrder(), String.format(Locale.US, "%.3f", (float)rotationSpeed));
    }

    /**
     * Modifie les vitesses de translation et de rotation du robot
     * @param speed
     */
    public void setBothSpeed(Speed speed){
        eth.communicate(0, ActuatorOrder.SET_SPEED.getEthernetOrder(), String.format(Locale.US, "%.3f", speed.translationSpeed), String.format(Locale.US, "%.3f", speed.rotationSpeed));
    }

    /**
     * Demande a la carte d'asservissement la position et l'orientation courrante du robot sur la table.
     * Renvoie x, y et orientation du robot (x en mm, y en mm, et orientation en radiants)
     * @return un tableau de 3 cases: [x, y, orientation]
     */
    public float[] getCurrentPositionAndOrientation()
    {
        // on demande a la carte des information a jour
        // on envois "?xyo" et on lis double (dans l'ordre : abscisse, ordonnée, orientation)
        String[] infosBuffer = eth.communicate(3, ActuatorOrder.SEND_POSITION.getEthernetOrder());
        float[] parsedInfos = new float[3];
        for(int i = 0; i < 3; i++)
        {
            try{
                parsedInfos[i] = Float.parseFloat(infosBuffer[i]);
            } catch (NumberFormatException e)
            {
                log.critical("BAD POSITION RECEIVED BY LL : "+ Arrays.toString(parsedInfos));
                return null;
            }
        }
        return parsedInfos;
    }

    /**
     * Envoie un ordre à la série. Le protocole est défini dans l'enum ActuatorOrder
     * @param order l'ordre a envoyer
     */
    public void useActuator(ActuatorOrder order)
    {
        log.debug("Envoi consigne a la carte actionneur : " + order.toString());
        eth.communicate(0, order.getEthernetOrder());
    }


    /******************
     * ASSERVISSEMENT *
     ******************/


    /**
     * Désactive l'asservissement en vitesse du robot
     */
    public void disableSpeedFeedbackLoop()
    {
        eth.communicate(0, ActuatorOrder.NO_ASSERV_SPEED.getEthernetOrder());
    }

    /**
     * Active l'asservissement en vitesse du robot
     */
    public void enableSpeedFeedbackLoop() {
        eth.communicate(0, ActuatorOrder.ASSERV_SPEED.getEthernetOrder());
    }

    /**
     * Active l'asservissement en translation du robot
     */
    public void enableTranslationnalFeedbackLoop() {
        eth.communicate(0, ActuatorOrder.ASSERV_TRANSLATION.getEthernetOrder());
    }

    /**
     * Active l'asservissement en rotation du robot
     */
    public void enableRotationnalFeedbackLoop() {
        eth.communicate(0, ActuatorOrder.ASSERV_ROTATION.getEthernetOrder());
    }

    /**
     * Désactive l'asservissement en translation du robot
     */
    public void disableTranslationnalFeedbackLoop() {
        eth.communicate(0, ActuatorOrder.NO_ASSERV_TRANSLATION.getEthernetOrder());
    }

    /**
     * Désactive l'asservissement en rotation du robot
     */
    public void disableRotationnalFeedbackLoop() {
        eth.communicate(0, ActuatorOrder.NO_ASSERV_ROTATION.getEthernetOrder());
    }


    /******************
     * INITIALISATION *
     ******************/


    /**
     * Ecrase la position et l'orientation du robot sur la carte
     * @param x
     * @param y
     * @param orientation
     */
    public void setPositionAndOrientation(int x, int y, double orientation)
    {
        float floatX = (float)x;
        float floatY = (float)y;
        float floatO = (float)orientation;
        eth.communicate(0, ActuatorOrder.SET_POSITION.getEthernetOrder(), String.format("%s",floatX), String.format("%s",floatY), String.format("%s",floatO));
    }

    /**
     * Ecrase la position x du robot au niveau de la carte
     * @param x la nouvelle abscisse que le robot doit considérer avoir sur la table
     */
    public void setX(int x)
    {
        float floatX=(float)x; //On transtype car la serie veut des Floats <3
        eth.getPositionAndOrientation().getPosition().setX(x);
        eth.communicate(0, ActuatorOrder.SET_X.getEthernetOrder(), String.format(Locale.US, "%.3f", floatX));
    }

    /**
     * Ecrase la position y du robot au niveau de la carte
     * @param y la nouvelle ordonnée que le robot doit considérer avoir sur la table
     */
    public void setY(int y)
    {
        float floatY=(float)y;//On transtype car la serie veut des Floats
        eth.getPositionAndOrientation().getPosition().setY(y);
        eth.communicate(0, ActuatorOrder.SET_Y.getEthernetOrder(), String.format(Locale.US, "%.3f", floatY));
    }

    /**
     * Ecrase l'orientation du robot au niveau de la carte
     * @param orientation la nouvelle orientation que le robot doit considérer avoir sur la table
     */
    public void setOrientation(double orientation)
    {
        float floatOrientation =(float) orientation; //On transtype car la serie veut des Floats (T_T)
        eth.getPositionAndOrientation().setOrientation(orientation);
        eth.communicate(0, ActuatorOrder.SET_ORIENTATION.getEthernetOrder(), String.format(Locale.US, "%.3f", floatOrientation));
    }

    /**
     * Configure les hooks pour le LL
     * @param id
     * @param posTrigger
     * @param order
     */


    public void configureHook(int id, Vec2 posTrigger, int tolerency, double orientation,double tolerencyAngle,String order){
        eth.communicate(0, ActuatorOrder.INITIALISE_HOOK.getEthernetOrder(), String.format("%d", id), posTrigger.toStringEth(), String.format("%d", tolerency),String.format("%f",orientation), String.format("%f",tolerencyAngle),order);
    }

    /**
     * Active un hook
     * @param hook
     */
    public void enableHook(HookNames hook){
        eth.communicate(0, ActuatorOrder.ENABLE_HOOK.getEthernetOrder(), String.format("%d", hook.getId()));
    }

    /**
     * Desactive le hook
     * @param hook
     */
    public void disableHook(HookNames hook){
        eth.communicate(0, ActuatorOrder.DISABLE_HOOK.getEthernetOrder(), String.format("%d", hook.getId()));
    }


    /**********
     * DIVERS *
     **********/

    /**
     * Met à jour l'orientaton et la position du robot
     */
    public XYO updatePositionAndOrientation(){
        return eth.getPositionAndOrientation();
    }

    /**
     * Demande a la carte capteurs de nous indiquer si le jumper de début de match est présent ou non
     * @return vrai si le jumper est absent, faux sinon
     */
    public boolean isJumperAbsent()
    {
        try {
            // demande a la carte si le jumper est présent, parse sa réponse, et si on lit 1 c'est que le jumper n'est pas/plus la
            return Integer.parseInt(eth.communicate(1, ActuatorOrder.JUMPER_STATE.getEthernetOrder())[0]) != 0;
        }
        catch (NumberFormatException e)
        {
            log.critical("réponse corrompue du jumper !");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * recupere la valeur d'un capteur de contact
     * @param sensor le capteur dont on veut recuperer la valeur
     * @return la valeur du capteur
     */
    public boolean getContactSensorValue(ContactSensors sensor)
    {
        String[] sensorAnswer = eth.communicate(1, sensor.getSerialCommunication());
        return (!sensorAnswer[0].equals("0"));
    }

    /** Active/desactive les capteurs en fonction de leur état courant
     */
    public void switchSensor()
    {
        eth.communicate(0, "sus");
        sensorState = !sensorState;
    }

    /**
     * Change le type de mouvement forcé/normal
     * @param choice true pour forcer les mouvements
     */
    public synchronized void setForceMovement(boolean choice)
    {
        if(choice){
            eth.communicate(0, ActuatorOrder.ENABLE_FORCE_MOVEMENT.getEthernetOrder());
        }
        else{
            eth.communicate(0, ActuatorOrder.DISABLE_FORCE_MOVEMENT.getEthernetOrder());
        }
    }

    /**
     * Active l'interface de debug pour l'asserv' (si ca existe encore dans le LL)
     * @return des infos (?)
     */
    public synchronized double[] pfdebug()
    {
        String[] infosBuffer = eth.communicate(5, ActuatorOrder.DEBUG.getEthernetOrder());
        double[] parsedInfos = new double[5];
        for(int i = 0; i < 5; i++)
        {
            try{
                parsedInfos[i] = Float.parseFloat(infosBuffer[i]);
            } catch (NumberFormatException e)
            {
                return null;
            }
        }
        return parsedInfos;
    }

    /**
     * Coupe la connexion ethernet
     */
    public void close(){
        eth.close();
    }

    @Override
    public void updateConfig(){
        loopDelay = config.getInt(ConfigInfoRobot.ETH_DELAY);
    }
    public boolean getSensorState(){
        return sensorState;
    }
}
