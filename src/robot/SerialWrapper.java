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
import enums.ContactSensors;
import enums.TurningStrategy;
import exceptions.ConfigPropertyNotFoundException;
import exceptions.serial.SerialConnexionException;
import threads.dataHandlers.ThreadSerial;
import utils.Config;
import utils.Log;
import utils.Sleep;

import java.util.Arrays;
import java.util.Locale;

/**
 * Prototype de wrapper pour la série incluant toutes les méthodes série. Diffère des différents CardWrapper car tout y
 * est centralisé. Les CardWrapper étaient utiles quand on avait plusieures AVRs pour les différentes fonctions, désormais
 * tout est centralisé sur l'ARM, ces CardWrappers ne sont donc que des reliques à éliminer.
 **
 * @author pf, paul, discord
 */
public class SerialWrapper implements Service
{

    /**
     *  pour écrire dans le log en cas de problème
     */
    private Log log;

    /**
     * connexion série avec la carte ARM
     */
    private ThreadSerial serial;

    /**
     * Le fichier de configuration
     */
    private Config config;

    /**
     * Vrai si les capteurs sont allumés, faux si les capteurs sont ignorés
     */
    private boolean areSensorsActive = false;

    /**
     *  nombre de miliseconde de tolérance entre la détection d'un patinage et la levée de l'exeption. Trop basse il y aura des faux positifs, trop haute on va forcer dans les murs pendant longtemps
     */
    private int blockedTolerancy;

    /**
     * Temps d'attente entre deux envois à la serie en ms
     */
    private int delayBetweenSend = 100;

    /**
     * Constructeur série
     * @param config
     * @param log
     * @param serie
     */

    private SerialWrapper(Config config, Log log, ThreadSerial serie)
    {
        this.log = log;
        this.config = config;
        this.serial = serie;
        updateConfig();
        // Lancement de l'asservissement de la STM
        try
        {
            enableTranslationnalFeedbackLoop();
            enableRotationnalFeedbackLoop();
        }
        catch (SerialConnexionException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void updateConfig() {
        try
        {
            blockedTolerancy=(Integer.parseInt(config.getProperty("tolerance_patinage_ms")));
            areSensorsActive = Boolean.parseBoolean(config.getProperty("capteurs_on"));
        }
        catch (ConfigPropertyNotFoundException e)
        {
            log.debug("Code à revoir  : impossible de trouver la propriete "+e.getPropertyNotFound());
        }
    }

    //====================================
    // Appels pour Locomotion
    //====================================

    /**
     * Regarde si le robot bouge effectivement.
     * Provoque un appel série pour avoir des information a jour. Cette méthode est demande donc un peu de temps.
     * @return true si le robot bouge
     * @throws SerialConnexionException en cas de problème de communication avec la carte d'asservissement
     */

    public boolean isRobotMoving() throws SerialConnexionException
    {
        return isRobotMovingAndAbnormal()[0];
    }

    /**
     * Fait avancer le robot. Méthode non bloquante
     * @param distance distance a parcourir par le robot. Une valeur négative fera reculer le robot, une valeur positive le fera avancer.
     * @throws SerialConnexionException en cas de problème de communication avec la carte d'asservissement
     */
    public void moveLengthwise(double distance) throws SerialConnexionException
    {
        int distanceTruncated = (int)distance;
        String chaines[] = {"d", String.format(Locale.US, "%d", distanceTruncated)};
        serial.communiquer(chaines, 0);
    }

    /**
     * Fait tourner le robot de maniere absolue. Méthode non bloquante
     * utilise TurningStrategy.FASTEST
     * @param angle l'angle de tour
     * @throws SerialConnexionException en cas de problème de communication avec la carte d'asservissement
     */
    public void turn(double angle) throws SerialConnexionException
    {
        turn(angle, TurningStrategy.FASTEST);
    }

    /**
     * Fait tourner le robot de maniere absolue. Méthode non bloquante
     * @param angle l'angle de tour
     * @throws SerialConnexionException en cas de problème de communication avec la carte d'asservissement
     */
    public void turn(double angle, TurningStrategy turning) throws SerialConnexionException
    {
        // tronque l'angle que l'on envoit a la série pour éviter les overflows
        float angleTruncated = (float)angle;
        if(turning == TurningStrategy.FASTEST) {
            String chaines[] = {"t", String.format(Locale.US, "%.3f", angleTruncated)};
            serial.communiquer(chaines, 0);
        }
        else if(turning == TurningStrategy.RIGHT_ONLY)
        {
            String chaines[] = {"tor", String.format(Locale.US, "%.3f", angleTruncated)};
            serial.communiquer(chaines, 0);
        }
        else if(turning == TurningStrategy.LEFT_ONLY)
        {
            String chaines[] = {"tol", String.format(Locale.US, "%.3f", angleTruncated)};
            serial.communiquer(chaines, 0);
        }
    }

    /**
     * Arrête le robot
     * @throws SerialConnexionException en cas de problème de communication avec la carte d'asservissement
     */
    public void immobilise() throws SerialConnexionException
    {
        log.warning("Immobilisation du robot");

        serial.communiquer("stop", 0);// On s'asservit sur la position actuelle
        while(isRobotMoving())
        {
            Sleep.sleep(delayBetweenSend); // On attend d'etre arreté
        }

    }

    /**
     * Ecrase la position x du robot au niveau de la carte
     * @param x la nouvelle abscisse que le robot doit considérer avoir sur la table
     * @throws SerialConnexionException en cas de problème de communication avec la carte d'asservissement
     */
    public void setX(int x) throws SerialConnexionException
    {
        float floatX=(float)x; //On transtype car la serie veut des Floats <3
        String chaines[] = {"cx", String.format(Locale.US, "%.3f", floatX)};
        serial.communiquer(chaines, 0);
    }

    /**
     * Ecrase la position y du robot au niveau de la carte
     * @param y la nouvelle ordonnée que le robot doit considérer avoir sur la table
     * @throws SerialConnexionException en cas de problème de communication avec la carte d'asservissement
     */
    public void setY(int y) throws SerialConnexionException
    {
        float floatY=(float)y;//On transtype car la serie veut des Floats
        String chaines[] = {"cy", String.format(Locale.US, "%.3f", floatY)};
        serial.communiquer(chaines, 0);
    }

    /**
     * Ecrase l'orientation du robot au niveau de la carte
     * @param orientation la nouvelle orientation que le robot doit considérer avoir sur la table
     * @throws SerialConnexionException en cas de problème de communication avec la carte d'asservissement
     */
    public void setOrientation(double orientation) throws SerialConnexionException
    {
        //log.debug("setOrientation "+orientation);
        float floatOrientation =(float) orientation; //On transtype car la serie veut des Floats (T_T)
        String chaines[] = {"co", String.format(Locale.US, "%.3f", floatOrientation)};
        serial.communiquer(chaines, 0);
    }

    /**
     * Active l'asservissement en translation du robot
     * @throws SerialConnexionException en cas de problème de communication avec la carte d'asservissement
     */
    public void enableTranslationnalFeedbackLoop() throws SerialConnexionException
    {
        serial.communiquer("ct1", 0);
    }

    /**
     * Active l'asservissement en rotation du robot
     * @throws SerialConnexionException en cas de problème de communication avec la carte d'asservissement
     */
    public void enableRotationnalFeedbackLoop() throws SerialConnexionException
    {
        serial.communiquer("cr1", 0);
    }

    /**
     * Désactive l'asservissement en translation du robot
     * @throws SerialConnexionException en cas de problème de communication avec la carte d'asservissement
     */
    public void disableTranslationnalFeedbackLoop() throws SerialConnexionException
    {
        serial.communiquer("ct0", 0);
    }

    /**
     * Désactive l'asservissement en rotation du robot
     * @throws SerialConnexionException en cas de problème de communication avec la carte d'asservissement
     */
    public void disableRotationnalFeedbackLoop() throws SerialConnexionException
    {
        serial.communiquer("cr0", 0);
    }

    /**
     * Modifie la vitesse en translation du robot sur la table
     * @param speed la nouvelle valeur maximum que peut prenvent prendre les pwm des moteurs lors d'une translation
     * @throws SerialConnexionException en cas de problème de communication avec la carte d'asservissement
     */
    public void setTranslationnalSpeed(float speed) throws SerialConnexionException
    {
        // envoie a la carte d'asservissement le nouveau maximum du pwm
        String chaines[] = {"ctv", String.format(Locale.US, "%.3f", speed)};
        serial.communiquer(chaines, 0);
    }

    /**
     * Modifie la vitesse en rotation du robot sur la table
     * @param rotationSpeed la nouvelle valeur maximum que peut prenvent prendre les pwm des moteurs lors d'une rotation
     * @throws SerialConnexionException en cas de problème de communication avec la carte d'asservissement
     */
    public void setRotationnalSpeed(double rotationSpeed) throws SerialConnexionException
    {
        // envoie a la carte d'asservissement le nouveau maximum du pwm
        String chaines[] = {"crv", String.format(Locale.US, "%.3f", (float)rotationSpeed)};
        serial.communiquer(chaines, 0);
    }


    /**
     * Change le type de mouvement forcé/normal
     * @param choice true pour forcer les mouvements
     */

    public synchronized void setForceMovement(boolean choice) throws SerialConnexionException
    {
        if(choice)
        {
            String chaines[] = {"efm"};
            serial.communiquer(chaines, 0);
        }
        else
        {
            String chaines[] = {"dfm"};
            serial.communiquer(chaines, 0);
        }
    }

    public synchronized double[] pfdebug() throws SerialConnexionException
    {
            String chaines[] = {"pfdebug"};
            String[] infosBuffer = serial.communiquer(chaines, 5);
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
     * Change l'accélération en plus fluide mais plus lente
     */
    public synchronized void setSmoothAcceleration(boolean choice) throws SerialConnexionException
    {
        if(choice)
        {
            String chaines[] = {"ssa"};
            serial.communiquer(chaines, 0);
        }
        else
        {
            String chaines[] = {"sva"};
            serial.communiquer(chaines, 0);
        }
    }

    /**
     * Demande a la carte d'asservissement la position et l'orientation courrante du robot sur la table.
     * Renvoie x, y et orientation du robot (x en mm, y en mm, et orientation en radiants)
     * @return un tableau de 3 cases: [x, y, orientation]
     * @throws SerialConnexionException en cas de problème de communication avec la carte d'asservissement
     */
    public float[] getCurrentPositionAndOrientation() throws SerialConnexionException
    {
        // on demande a la carte des information a jour
        // on envois "?xyo" et on lis double (dans l'ordre : abscisse, ordonnée, orientation)
        String[] infosBuffer = serial.communiquer("?xyo", 3);
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
     * Ferme la connexion série avec la carte d'asservissements
     */
    public void closeLocomotion()
    {
        serial.close();
    }


    /**
     * Eteint la STM
     * Attention, la STM ne répondra plus jusqu'a ce qu'elle soit manuellement ralummée
     * @throws SerialConnexionException en cas de problème de communication avec la carte d'asservissement
     */
    public void shutdownSTM() throws SerialConnexionException
    {
        serial.communiquer("poweroff", 0);
    }

    /**
     *  Verifie si le robot est arrivé et si c'est anormal
     *  @return Les informations sous forme d'un tableau de booleens
     *  lecture : [est ce qu'on bouge][est ce que c'est Anormal]
     * @throws SerialConnexionException
     */
    public boolean[] isRobotMovingAndAbnormal() throws SerialConnexionException
    {
        // on demande a la carte des information a jour
        // on envois "f" et on lis double (dans l'ordre : bouge, est anormal)
        String[] infosBuffer = serial.communiquer("f", 2);
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
     * Désactive l'asservissement en vitesse du robot
     * @throws SerialConnexionException en cas de problème de communication avec la carte d'asservissement
     */
    public void disableSpeedFeedbackLoop() throws SerialConnexionException
    {
        serial.communiquer("cv0", 0);
    }

    /**
     * Active l'asservissement en vitesse du robot
     * @throws SerialConnexionException en cas de problème de communication avec la carte d'asservissement
     */
    public void enableSpeedFeedbackLoop() throws SerialConnexionException {
        serial.communiquer("cv1", 0);
    }

    //========================================
    // Appels pour Actuator (actionneurs)
    //========================================

    /**
     * Envoie un ordre à la série. Le protocole est défini dans l'enum ActuatorOrder
     * @param order l'ordre a envoyer
     * @throws SerialConnexionException en cas de problème de communication avec la carte actionneurs
     */
    public void useActuator(ActuatorOrder order) throws SerialConnexionException
    {
        log.debug("Envoi consigne a la carte actionneur : " + order.toString());
        serial.communiquer(order.getSerialOrder(), 0);
    }

    /**
     * Donne une vitesse aux AX12
     * @param speed vitesse de 0 à 255
     */
    public void setAX12Speed(int speed) throws SerialConnexionException
    {
        String chaines[] = {"caxs", Integer.toString(speed)};
        serial.communiquer(chaines, 0);
    }

    //========================================
    // Appels pour Sensor (capteurs)
    //========================================

    /**
     * demande aux capteurs de fournir la distance entre le robot et le prochain obstacle
     * @return la distance en mm estimée par les capteurs avant un obstacle. Une valeur de 3000 est considérée infinie
     */
    public int getSensedDistance()
    {
        if(!areSensorsActive)
            return 3000;


        String infoBuffer;

        // demande au capteur la distance qu'il détecte
        try
        {
            infoBuffer = serial.communiquer("us", 1)[0];

        }
        catch(SerialConnexionException e)
        {
            log.critical("La carte capteurs ne répond pas !");
            log.critical( e.logStack());
            return 3000; // valeur considérée comme infinie
        }

        // parse la distance que les capteurs nous ont donné et renvois cette valeur a l'utilisateur
        int distance = Integer.parseInt(infoBuffer);
        return distance;
    }

    /**
     * Demande a la carte capteurs de nous indiquer si le jumper de début de match est présent ou non
     * Suuuuuuuuuuuus
     * @return vrai si le jumper est absent, faux sinon
     */
    public boolean isJumperAbsent()
    {
        try
        {
            // demande a la carte si le jumper est présent, parse sa réponse, et si on lit 1 c'est que le jumper n'est pas/plus la
            return Integer.parseInt(serial.communiquer("j", 1)[0]) != 0;
        }
        catch (NumberFormatException e)
        {
            log.critical("réponse corrompue du jumper !");
            e.printStackTrace();
            return false;
        }
        catch (SerialConnexionException e)
        {
            log.critical(" Problème de communication avec la carte capteurs en essayent de parler au jumper.");
            log.debug( e.logStack());
            return false;
        }
    }

    /**
     * recupere la valeur d'un capteur de contact
     * @param sensor le capteur dont on veut recuperer la valeur
     * @return la valeur du capteur
     * @throws SerialConnexionException si erreur de connexion avec le capteur
     */
    public boolean getContactSensorValue(ContactSensors sensor) throws SerialConnexionException
    {
        String[] sensorAnswer = serial.communiquer(sensor.getSerialCommunication(),1);
        return (!sensorAnswer[0].equals("0"));
    }

    /** Active/desactive les capteurs en fonction de leur état courant
     * @throws SerialConnexionException
     */
    public void switchSensor() throws SerialConnexionException
    {
        serial.communiquer("sus", 0);
        areSensorsActive = !areSensorsActive;
    }

    public void switchAuto() throws SerialConnexionException
    {
        serial.communiquer("auto", 0);
    }

}