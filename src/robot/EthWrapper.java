package robot;

import container.Service;
import enums.TurningStrategy;
import exceptions.ConfigPropertyNotFoundException;
import exceptions.serial.SerialConnexionException;
import threads.dataHandlers.ThreadEth;
import utils.Config;
import utils.Log;
import utils.Sleep;

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
        updateConfig();
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
        eth.communicate(chaines, 0);
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
     * @throws SerialConnexionException en cas de problème de communication avec la
     * carte d'asservissement
     */
    public void immobilise() throws SerialConnexionException
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
     * @throws SerialConnexionException en cas de problème de communication avec la carte d'asservissement
     */

    public boolean isRobotMoving() throws SerialConnexionException
    {
        return isRobotMovingAndAbnormal()[0];
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

    @Override
    public void updateConfig(){
        try {
            loopDelay = Integer.parseInt(config.getProperty("Eth_loopDelay"));
        }catch (ConfigPropertyNotFoundException e){
            e.printStackTrace();
        }
    }
}
