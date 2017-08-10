package scripts;

import enums.ActuatorOrder;
import exceptions.BadVersionException;
import exceptions.BlockedActuatorException;
import exceptions.ConfigPropertyNotFoundException;
import exceptions.ExecuteException;
import exceptions.Locomotion.UnableToMoveException;
import hook.Callback;
import hook.Hook;
import hook.methods.PriseModule;
import hook.methods.ReposLargueModule;
import hook.types.HookFactory;
import smartMath.Circle;
import smartMath.Vec2;
import strategie.GameState;
import utils.Config;
import utils.Log;

import java.util.ArrayList;



/** Script faisant tout un match sauf le début en scripté, pour ne pas appeler le pethfinding dans les zones compliquées
 *
 * @author gaelle
 */
public class FullScripted extends AbstractScript
{

    /** Distances & angles du script, override par la config */

    private double angleToQuitBase = -Math.PI/2;
    private int distanceToQuitBase = -570;
    private double angleToFarZone = 5*Math.PI/4;;
    private int distanceToFarZone = -380;

    private int distanceBeforeCatch1stMod = -130;
    private int distanceAfterCatch1stMod = 190;

    private double angleWeirdMove1 = Math.PI - 0.60;
    private int distanceWeirdMove1 = 270;
    private double angleWeirdMove2 = Math.PI - 0.67;
    private int distanceWeirdMove2 = 25;

    private double angleToDisengage1stCrater = Math.PI - 0.55;
    private int distanceToDisengage1stCrater = -129;

    private int distanceBeforeDrop1stMod = -57;
    private int distanceAfterDrop1stMod = 60;

    private double angleToCloseZone = -Math.PI/3;
    private int distanceToCloseZone = 800;

    private double angleManip2ndMod = -Math.PI/2;
    private int distanceManip2ndMod = 500;
    private double angleBeforeCatch2ndMod = Math.PI;
    private int distanceBeforeCatch2ndMod = -210;
    private int distanceAfterCatch2ndMod = 100;

    private double angleBeforeDrop1stBalls = -1.45;
    private int distanceBeforeDrop1stBalls = 193;
    private int distanceAfterDrop1stBalls = -150;

    private int distanceToDisengageCloseZone = -160;
    private double angleBeforeCatch2ndBalls = -1.45;
    private int distanceBeforeCatch2ndBalls = 161;
    private int finalMove = -150;

    private boolean detect = false;

    /**
     * Constructeur à appeller lorsqu'un script héritant de la classe AbstractScript est instancié.
     * Le constructeur se charge de renseigner la hookFactory, le système de config et de log.
     *
     * @param hookFactory La factory a utiliser pour générer les hooks dont pourra avoir besoin le script
     * @param config      le fichier de config a partir duquel le script pourra se configurer
     * @param log         le système de log qu'utilisera le script
     */
    protected FullScripted(HookFactory hookFactory, Config config, Log log)
    {
        super(hookFactory, config, log);

        versions = new Integer[]{0};
    }

    @Override
    public void execute(int versionToExecute, GameState actualState, ArrayList<Hook> hooksToConsider) throws ExecuteException, UnableToMoveException, BlockedActuatorException
    {
        updateConfig();

        try{
            Hook PriseModule = hookFactory.newPositionHook(new Vec2(860, 1670), (float) -Math.PI/2, 200, 3140);
            PriseModule.addCallback(new Callback(new PriseModule(), true, actualState));
            hooksToConsider.add(PriseModule);
            Hook ReposLargueModule = hookFactory.newPositionHook(new Vec2(700, 1670), (float) -Math.PI/4, 200, 3140);
            ReposLargueModule.addCallback(new Callback(new ReposLargueModule(), true, actualState));

            ArrayList<Hook> emptyHook= new ArrayList<Hook>();

            hooksToConsider.add(ReposLargueModule);

            if (versionToExecute==0)
            {
                actualState.robot.moveLengthwise(-650);
                actualState.robot.turn(angleToQuitBase);
                actualState.robot.moveLengthwise(distanceToQuitBase);
                actualState.robot.turn(angleToFarZone);
                actualState.robot.moveLengthwise(distanceToFarZone);

                //Attraper le module avec le côté droit

                // Manoeuvre pour arriver au niveau du module et être prêt à choper le module
                actualState.robot.useActuator(ActuatorOrder.MID_ATTRAPE_D, true);
                actualState.robot.useActuator(ActuatorOrder.REPLI_CALLE_D, false);
                actualState.robot.useActuator(ActuatorOrder.REPOS_ATTRAPE_D, false);
                actualState.robot.moveLengthwise(distanceBeforeCatch1stMod);

                // Attraper le module
                actualState.robot.useActuator(ActuatorOrder.PREND_MODULE_D, true);
                actualState.robot.useActuator(ActuatorOrder.REPOS_ATTRAPE_D, true);
                actualState.robot.useActuator(ActuatorOrder.LIVRE_CALLE_D, true);
                actualState.robot.useActuator(ActuatorOrder.REPOS_CALLE_D, true);
                actualState.robot.useActuator(ActuatorOrder.LIVRE_CALLE_D, true);

                actualState.robot.moveLengthwise(distanceAfterCatch1stMod, hooksToConsider);

                actualState.robot.turn(angleWeirdMove1);
                actualState.robot.moveLengthwise(distanceWeirdMove1);
                // actualState.robot.turn(angleWeirdMove2);
                //actualState.robot.moveLengthwise(distanceWeirdMove2);

                // Prepare la pelleteuse avant déploiement(bras relevés mais légèrement abaissés pour ne pas bloquer la rotation de la pelle, puis pelle mise à 300°)
                actualState.robot.useActuator(ActuatorOrder.MED_PELLETEUSE, true);
                actualState.robot.useActuator(ActuatorOrder.PRET_PELLE, true);

                // Déploie la pelleteuse (descendre les bras, avec pelle toujours à 300 °)
                actualState.robot.useActuator(ActuatorOrder.LIVRAISON_PELLETEUSE, true);

                // Fait tourner la pelleteuse (jusqu'à ~150 ou 200°)
                actualState.robot.useActuator(ActuatorOrder.PREND_PELLE, true);

                // "Lèves les bras Maurice, c'est plus rigolo quand tu lèves les bras !", RIP King Julian
                actualState.robot.useActuator(ActuatorOrder.TIENT_BOULES,false);
                actualState.robot.useActuator(ActuatorOrder.MED_PELLETEUSE, false);
                actualState.robot.turn(angleToDisengage1stCrater);

                actualState.robot.moveLengthwise(distanceToDisengage1stCrater);
                actualState.robot.turn(Math.PI/4);
                actualState.robot.moveLengthwise(distanceBeforeDrop1stMod);

                // Drop un module
                actualState.robot.useActuator(ActuatorOrder.POUSSE_LARGUEUR_LENT, true);
                actualState.robot.moveLengthwise(distanceAfterDrop1stMod, hooksToConsider);

                actualState.robot.turn(angleToCloseZone);
                actualState.robot.moveLengthwise(distanceToCloseZone,emptyHook);

                //deuxième partie du match
                actualState.robot.turn(angleManip2ndMod);
                actualState.robot.moveLengthwise(distanceManip2ndMod, emptyHook);
                //actualState.robot.useActuator(ActuatorOrder.MID_ATTRAPE_G, true);
                actualState.robot.useActuator(ActuatorOrder.REPLI_CALLE_G, false);
                actualState.robot.useActuator(ActuatorOrder.REPOS_ATTRAPE_G, false);

                actualState.robot.turn(angleBeforeCatch2ndMod);

                actualState.robot.moveLengthwise(distanceBeforeCatch2ndMod,emptyHook);
                // Attraper le module
                actualState.robot.useActuator(ActuatorOrder.PREND_MODULE_G, true);
                actualState.robot.useActuator(ActuatorOrder.REPOS_ATTRAPE_G, true);
                actualState.robot.useActuator(ActuatorOrder.LIVRE_CALLE_G, true);
                actualState.robot.useActuator(ActuatorOrder.REPOS_CALLE_G, true);
                actualState.robot.useActuator(ActuatorOrder.LIVRE_CALLE_G, true);

                // Et remonte-le à l'aide de l'ascenceur
                actualState.robot.useActuator(ActuatorOrder.MID_ATTRAPE_D, true);
                actualState.robot.useActuator(ActuatorOrder.REPOS_LARGUEUR,false);
                actualState.robot.useActuator(ActuatorOrder.REPOS_CALLE_G, false);
                actualState.robot.useActuator(ActuatorOrder.REPOS_CALLE_D, true);
                actualState.robot.useActuator(ActuatorOrder.LEVE_ASC, true);
                actualState.robot.useActuator(ActuatorOrder.BAISSE_ASC, true);

                // Replie le tout
                actualState.robot.useActuator(ActuatorOrder.LIVRE_CALLE_D, false);
                actualState.robot.useActuator(ActuatorOrder.LIVRE_CALLE_G, true);
                actualState.robot.useActuator(ActuatorOrder.PREND_MODULE_D, false);
                actualState.robot.useActuator(ActuatorOrder.PREND_MODULE_G, false);

                //Drop le module
                actualState.robot.useActuator(ActuatorOrder.POUSSE_LARGUEUR_LENT, true);

                actualState.robot.useActuator(ActuatorOrder.REPOS_LARGUEUR, false);
                actualState.robot.moveLengthwise(distanceAfterCatch2ndMod,emptyHook);


                actualState.robot.turn(angleBeforeDrop1stBalls);

                actualState.robot.moveLengthwise(distanceBeforeDrop1stBalls,emptyHook);

                //abaisser les bras au plus bas
                actualState.robot.useActuator(ActuatorOrder.LIVRAISON_PELLETEUSE, true);

                //rotation de la pelle jusqu'à la position de livraison
                actualState.robot.useActuator(ActuatorOrder.LIVRE_PELLE, true);

                //éventuellement, attendre le temps que les boules tombent (en millisecondes)
                //actualstate.robot.sleep(1000);

                //lever les bras jusqu'à la position intermédiaire
                actualState.robot.useActuator(ActuatorOrder.MED_PELLETEUSE, true);

                //Reculer un peu
                actualState.robot.moveLengthwise(distanceAfterDrop1stBalls,emptyHook);

                //tourner la pelle jusqu'à la position initiale
                actualState.robot.useActuator(ActuatorOrder.PRET_PELLE, true);

                //monter les bras le plus haut \o/
                actualState.robot.useActuator(ActuatorOrder.REPLIER_PELLETEUSE, true);


                // Calcule de l'angle pour se diriger vers le centre du robot
                Vec2 posCratere= new Vec2(850, 540);
                Vec2 posRobot=actualState.robot.getPosition();
                Vec2 vec = posCratere.minusNewVector(posRobot);

                // Manoeuvre pour se diriger vers le cratère
                //stateToConsider.robot.useActuator(ActuatorOrder.PREND_MODULE_D, true);
                actualState.robot.turn(vec.getA()+0.05);
                actualState.robot.moveLengthwise(distanceBeforeCatch2ndBalls,emptyHook);

                // Prepare la pelleteuse avant déploiement(bras relevés mais légèrement abaissés pour ne pas bloquer la rotation de la pelle, puis pelle mise à 300°)
                actualState.robot.useActuator(ActuatorOrder.MED_PELLETEUSE, true);
                actualState.robot.useActuator(ActuatorOrder.PRET_PELLE, true);

                // Déploie la pelleteuse (descendre les bras, avec pelle toujours à 300 °)
                actualState.robot.useActuator(ActuatorOrder.LIVRAISON_PELLETEUSE, true);

                // Fait tourner la pelleteuse (jusqu'à ~150 ou 200°)
                actualState.robot.useActuator(ActuatorOrder.PREND_PELLE, true);

                // "Lèves les bras Maurice, c'est plus rigolo quand tu lèves les bras !", RIP King Julian
                actualState.robot.useActuator(ActuatorOrder.TIENT_BOULES,false);
                actualState.robot.useActuator(ActuatorOrder.MED_PELLETEUSE, false);

                actualState.robot.moveLengthwise(distanceToDisengageCloseZone,emptyHook);
                actualState.robot.turn(angleBeforeCatch2ndBalls);
                actualState.robot.moveLengthwise(distanceBeforeCatch2ndBalls,emptyHook);

                //abaisser les bras au plus bas
                actualState.robot.useActuator(ActuatorOrder.LIVRAISON_PELLETEUSE, true);

                //rotation de la pelle jusqu'à la position de livraison
                actualState.robot.useActuator(ActuatorOrder.LIVRE_PELLE, true);

                //éventuellement, attendre le temps que les boules tombent (en millisecondes)
                //actualstate.robot.sleep(1000);

                //lever les bras jusqu'à la position intermédiaire
                actualState.robot.useActuator(ActuatorOrder.MED_PELLETEUSE, true);

                //Reculer un peu
                actualState.robot.moveLengthwise(finalMove,emptyHook);

                //tourner la pelle jusqu'à la position initiale
                actualState.robot.useActuator(ActuatorOrder.PRET_PELLE, true);

                //monter les bras le plus haut \o/
                actualState.robot.useActuator(ActuatorOrder.REPLIER_PELLETEUSE, true);
                if(detect){
                    actualState.robot.switchSensor(); // Capteurs off
                }
            }
        }
        catch(Exception e)
        {
            log.critical("Robot ou actionneur bloqué dans DropBalls");
            finalize(actualState, e);
        }
    }

    @Override
    public int remainingScoreOfVersion(int version, GameState state)
    {

        int score = 0;
        return score;
    }

    @Override
    public Circle entryPosition(int version, int ray, Vec2 robotPosition) throws BadVersionException
    {
        if (version == 0) {
            return new Circle(robotPosition);
        }

        else
        {
            log.debug("erreur : mauvaise version de script");
            throw new BadVersionException();
        }
    }

    @Override
    public void updateConfig()
    {
        try{
            /* angleToQuitBase = Double.parseDouble(config.getProperty("angle_quit_base"));
            distanceToQuitBase = Integer.parseInt(config.getProperty("distance_quit_base"));
            angleToFarZone = Double.parseDouble(config.getProperty("angle_zone_1erCratere"));
            distanceToFarZone = Integer.parseInt(config.getProperty("distance_zone_1erCratere"));

            distanceBeforeCatch1stMod = Integer.parseInt(config.getProperty("distance_av_prise_1erMod"));
            distanceAfterCatch1stMod = Integer.parseInt(config.getProperty("distance_ap_prise_1erMod"));

            angleWeirdMove1 = Double.parseDouble(config.getProperty("angle_man_chelou_1"));
            distanceWeirdMove1 = Integer.parseInt(config.getProperty("distance_man_chelou_1"));
            angleWeirdMove2 = Double.parseDouble(config.getProperty("angle_man_chelou_2"));
            distanceWeirdMove2 = Integer.parseInt(config.getProperty("distance_man_chelou_2"));

            angleToDisengage1stCrater = Double.parseDouble(config.getProperty("angle_sortie_1erCratere"));
            distanceToDisengage1stCrater = Integer.parseInt(config.getProperty("distance_sortie_1erCratere"));

            distanceBeforeDrop1stMod = Integer.parseInt(config.getProperty("distance_av_drop_1erMod"));
            distanceAfterDrop1stMod = Integer.parseInt(config.getProperty("distance_ap_drop_1erMod"));

            angleToCloseZone = Double.parseDouble(config.getProperty("angle_zone_2eCratere"));
            distanceToCloseZone = Integer.parseInt(config.getProperty("distance_zone_2eCratere"));

            angleManip2ndMod = Double.parseDouble(config.getProperty("angle_manip_2ndMod"));
            distanceManip2ndMod = Integer.parseInt(config.getProperty("distance_manip_2ndMod"));
            angleBeforeCatch2ndMod = Double.parseDouble(config.getProperty("angle_av_catch_2ndMod"));
            distanceBeforeCatch2ndMod = Integer.parseInt(config.getProperty("distance_av_catch_2ndMod"));
            distanceAfterCatch2ndMod = Integer.parseInt(config.getProperty("distance_ap_catch_2ndMod"));

            angleBeforeDrop1stBalls = Double.parseDouble(config.getProperty("angle_av_drop_1stBalls"));
            distanceBeforeDrop1stBalls = Integer.parseInt(config.getProperty("distance_av_drop_1stBalls"));
            distanceAfterDrop1stBalls = Integer.parseInt(config.getProperty("distance_ap_drop_1stBalls"));

            distanceToDisengageCloseZone = Integer.parseInt(config.getProperty("distance_degagement_zone_2ndCratere"));
            angleBeforeCatch2ndBalls = Double.parseDouble(config.getProperty("angle_av_catch_2ndBalls"));
            distanceBeforeCatch2ndBalls = Integer.parseInt(config.getProperty("distance_av_catch_2ndBalls"));
            finalMove = Integer.parseInt(config.getProperty("dernier_move"));*/

            detect = Boolean.parseBoolean(config.getProperty("capteurs_on"));

        }catch (ConfigPropertyNotFoundException e){
            log.debug("Revoir le code : impossible de trouver la propriété " + e.getPropertyNotFound());
        }
    }

    @Override
    public void finalize(GameState state, Exception e) throws UnableToMoveException
    {
        log.debug("Exception " + e +"dans DropBalls : Lancement du finalize !");
        state.robot.setBasicDetection(false);
    }

    @Override
    public Integer[] getVersion(GameState stateToConsider) {
        return versions;
    }
}
