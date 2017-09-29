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
import enums.ActuatorOrder;
import enums.ConfigInfoRobot;
import enums.ContactSensors;
import enums.TurningStrategy;
import exceptions.ConfigPropertyNotFoundException;
import pfg.config.Config;
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
        int distanceTruncated = (int)distance;
        String chaines[] = {"d", String.format(Locale.US, "%d", distanceTruncated)};
        eth.communicate(chaines, 0);
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
            String chaines[] = {"t", String.format(Locale.US, "%.3f", angleTruncated)};
            eth.communicate(chaines, 0);
        }
        else if(turning == TurningStrategy.RIGHT_ONLY)
        {
            String chaines[] = {"tor", String.format(Locale.US, "%.3f", angleTruncated)};
            eth.communicate(chaines, 0);
        }
        else if(turning == TurningStrategy.LEFT_ONLY)
        {
            String chaines[] = {"tol", String.format(Locale.US, "%.3f", angleTruncated)};
            eth.communicate(chaines, 0);
        }
    }

    /**
     * Arrête le robot
     */
    public void immobilise()
    {
        log.warning("Immobilisation du robot");

        eth.communicate("stop", 0);// On s'asservit sur la position actuelle
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
        String[] infosBuffer = eth.communicate("f", 2);
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
        // envoie a la carte d'asservissement le nouveau maximum du pwm
        String chaines[] = {"ctv", String.format(Locale.US, "%.3f", speed)};
        eth.communicate(chaines, 0);
    }

    /**
     * Modifie la vitesse en rotation du robot sur la table
     * @param rotationSpeed la nouvelle valeur maximum que peut prenvent prendre les pwm des moteurs lors d'une rotation
     */
    public void setRotationnalSpeed(double rotationSpeed)
    {
        // envoie a la carte d'asservissement le nouveau maximum du pwm
        String chaines[] = {"crv", String.format(Locale.US, "%.3f", (float)rotationSpeed)};
        eth.communicate(chaines, 0);
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
        String[] infosBuffer = eth.communicate("?xyo", 3);
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
        eth.communicate(order.getSerialOrder(), 0);
    }


    /******************
     * ASSERVISSEMENT *
     ******************/


    /**
     * Désactive l'asservissement en vitesse du robot
     */
    public void disableSpeedFeedbackLoop()
    {
        eth.communicate("cv0", 0);
    }

    /**
     * Active l'asservissement en vitesse du robot
     */
    public void enableSpeedFeedbackLoop() {
        eth.communicate("cv1", 0);
    }

    /**
     * Active l'asservissement en translation du robot
     */
    public void enableTranslationnalFeedbackLoop()
    {
        eth.communicate("ct1", 0);
    }

    /**
     * Active l'asservissement en rotation du robot
     */
    public void enableRotationnalFeedbackLoop()
    {
        eth.communicate("cr1", 0);
    }

    /**
     * Désactive l'asservissement en translation du robot
     */
    public void disableTranslationnalFeedbackLoop()
    {
        eth.communicate("ct0", 0);
    }

    /**
     * Désactive l'asservissement en rotation du robot
     */
    public void disableRotationnalFeedbackLoop()
    {
        eth.communicate("cr0", 0);
    }


    /******************
     * INITIALISATION *
     ******************/


    /**
     * Ecrase la position x du robot au niveau de la carte
     * @param x la nouvelle abscisse que le robot doit considérer avoir sur la table
     */
    public void setX(int x)
    {
        float floatX=(float)x; //On transtype car la serie veut des Floats <3
        String chaines[] = {"cx", String.format(Locale.US, "%.3f", floatX)};
        eth.communicate(chaines, 0);
    }

    /**
     * Ecrase la position y du robot au niveau de la carte
     * @param y la nouvelle ordonnée que le robot doit considérer avoir sur la table
     */
    public void setY(int y)
    {
        float floatY=(float)y;//On transtype car la serie veut des Floats
        String chaines[] = {"cy", String.format(Locale.US, "%.3f", floatY)};
        eth.communicate(chaines, 0);
    }

    /**
     * Ecrase l'orientation du robot au niveau de la carte
     * @param orientation la nouvelle orientation que le robot doit considérer avoir sur la table
     */
    public void setOrientation(double orientation)
    {
        //log.debug("setOrientation "+orientation);
        float floatOrientation =(float) orientation; //On transtype car la serie veut des Floats (T_T)
        String chaines[] = {"co", String.format(Locale.US, "%.3f", floatOrientation)};
        eth.communicate(chaines, 0);
    }


    /**********
     * DIVERS *
     **********/


    /**
     * Demande a la carte capteurs de nous indiquer si le jumper de début de match est présent ou non
     * @return vrai si le jumper est absent, faux sinon
     */
    public boolean isJumperAbsent()
    {
        try {
            // demande a la carte si le jumper est présent, parse sa réponse, et si on lit 1 c'est que le jumper n'est pas/plus la
            return Integer.parseInt(eth.communicate("j", 1)[0]) != 0;
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
        String[] sensorAnswer = eth.communicate(sensor.getSerialCommunication(),1);
        return (!sensorAnswer[0].equals("0"));
    }

    /** Active/desactive les capteurs en fonction de leur état courant
     */
    public void switchSensor()
    {
        eth.communicate("sus", 0);
        sensorState = !sensorState;
    }

    /**
     * Change le type de mouvement forcé/normal
     * @param choice true pour forcer les mouvements
     */
    public synchronized void setForceMovement(boolean choice)
    {
        if(choice)
        {
            String chaines[] = {"efm"};
            eth.communicate(chaines, 0);
        }
        else
        {
            String chaines[] = {"dfm"};
            eth.communicate(chaines, 0);
        }
    }

    /**
     * Active l'interface de debug pour l'asserv' (si ca existe encore dans le LL)
     * @return des infos (?)
     */
    public synchronized double[] pfdebug()
    {
        String chaines[] = {"pfdebug"};
        String[] infosBuffer = eth.communicate(chaines, 5);
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
