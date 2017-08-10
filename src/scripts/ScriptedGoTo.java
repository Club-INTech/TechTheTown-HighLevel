package scripts;

import enums.*;
import exceptions.BadVersionException;
import exceptions.BlockedActuatorException;
import exceptions.ConfigPropertyNotFoundException;
import exceptions.ExecuteException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;
import hook.Callback;
import hook.Hook;
import hook.methods.Elevator;
import hook.methods.PrepareToCatchModG;
import hook.methods.RepliAllActionneurs;
import hook.types.HookFactory;
import smartMath.Circle;
import smartMath.Vec2;
import strategie.GameState;
import utils.Config;
import utils.Log;

import java.security.cert.TrustAnchor;
import java.util.ArrayList;


/** FullScript sans pathfinding utilisant goTo (pointVisé)
 * version 0 : prend le module dans a fuée au début
 * @author melanie, gaelle, rem
 */


public class ScriptedGoTo extends AbstractScript
{

    /** PointsVisés, dstances & angles du script, override par la config */

    /** On prend le premier module */
    boolean prisePremierModule=true;

    /** Déplacements jusqu'à la zone du fond */
    Vec2 point1MilieuTable                  = new Vec2(540,800);
    Vec2 point2EntreeFinTable               = new Vec2(805,900);
    Vec2 pointContournementModule           = new Vec2(805, 1560);

    /** Manoeuvre pour attraper le 1er module */
    Vec2 point3AttrapperModule              = new Vec2(805,1725);
    double angleAttraperModule1             = -3*Math.PI/4;

    /** Manoeuvre pour attraper les 1eres boules */
    Vec2 point4arriveDevantCratereFond      = new Vec2(650,1785);
    Vec2 posCratere1                        = new Vec2(420, 1876);
    double angleDevantCratere1               = 0.04;
    int distanceCratereFondAvantBoules      = 65;
    int distanceCratereFondApresBoules      = -170;

    /** Manoeuvre pour drop le 1er module */
    double angleCratereFondAvantDepotModule = Math.PI/4;
    int distanceCratereFondAvantDepotModule = -135;
    int distanceCratereFondApresDepotModule = 55;

    /** Déplacements jusqu'à la zone de départ */
    Vec2 pointSortieCratereFond             = new Vec2(1115,1290);
    Vec2 pointIntermediaireVersModule       = new Vec2(1115, 1005); //new Vec2(1115,850);

    /** Manoeuvre pour attraper le 2e module */
    Vec2 pointAvantModule2                  = new Vec2(985, 742); //anciennement 770
    double angleDropModule2                 = Math.PI;
    int distanceApresModule2                = 60;

    /** Distance de recalage */
    int distanceRecalage                    = -250;

    /** Manoeuvre pour déposer les 1eres boules */
    int distanceAvantDeposeBoules1          = 205;
    int distanceReculApresDepotBoule1       = -180;

    /** Manoeuvre pour prendre les 2emes boules */
    Vec2 posCratere2                        = new Vec2(850, 540);
    int distanceCratereBaseAvantBoules      = 220;
    int distanceCratereBaseApresBoules      = -190;

    /** Manoeuvre pour déposer les 2emes boules */
    double angleAvantDeposeBoules           = -Math.PI/2 + 0.2;
    int distanceAvantDeposeBoules2          = 200;
    double angleDeposeBoules                = -Math.PI/2+0.2;

    /** Action bonus */
    Vec2 bonus1                             = new Vec2(850, 1000);
    Vec2 bonus2                             = new Vec2(490, 1050);
    Vec2 dernierePos                        = new Vec2(580, 300);

    /** Manoeuvre de fin !*/
    int distanceEsquiveRobot                = -150;

    private boolean detect = false;
    private double recalageThresholdOrientation;

    /**
     * Constructeur à appeller lorsqu'un script héritant de la classe AbstractScript est instancié.
     * Le constructeur se charge de renseigner la hookFactory, le système de config et de log.
     *
     * @param hookFactory La factory a utiliser pour générer les hooks dont pourra avoir besoin le script
     * @param config      le fichier de config a partir duquel le script pourra se configurer
     * @param log         le système de log qu'utilisera le script
     */

    protected ScriptedGoTo(HookFactory hookFactory, Config config, Log log)
    {
        super(hookFactory, config, log);
    }


    @Override
    public void execute(int versionToExecute, GameState actualState, ArrayList<Hook> hooksToConsider) throws ExecuteException, UnableToMoveException, BlockedActuatorException,SerialConnexionException
    {

        updateConfig();
        try{

            //Initialisation des hooks pour permettre de replier les actionneurs pendant les déplacements
            Hook repliTout = hookFactory.newPositionHook(new Vec2(760, 1670), (float) Math.PI/4, 60, 400);
            repliTout.addCallback(new Callback(new RepliAllActionneurs(), true, actualState));
            Hook prepareToCatch2ndMod = hookFactory.newPositionHook(pointIntermediaireVersModule, (float) - Math.PI/2, 25, 400);
            prepareToCatch2ndMod.addCallback(new Callback(new PrepareToCatchModG(), true, actualState));
            Hook elevatorSecurity = hookFactory.newPositionHook(posCratere2,(float) (3*Math.PI/4), 400, 500);
            elevatorSecurity.addCallback(new Callback(new Elevator(), true, actualState));

            hooksToConsider.add(repliTout);
            hooksToConsider.add(prepareToCatch2ndMod);
            hooksToConsider.add(elevatorSecurity);

            if(versionToExecute == 0) {

                // Timer afin de savoir combien de temps l'on prend en moyenne
                long debutMatch = System.currentTimeMillis();

                actualState.robot.setLocomotionSpeed(Speed.FAST_T_MEDIUM_R);

                if (prisePremierModule) {

                    actualState.robot.setOrientation(Math.PI);
                    actualState.robot.setPosition(new Vec2(620,194));

                    // Avec le Hook pour prendre le module multicolore pret de la zone de départ
                    actualState.robot.moveLengthwise(75);
                    actualState.robot.turn(2 * Math.PI / 3 + 0.1);   // 250, 580 <- 578, 208
                    actualState.robot.moveLengthwise(550);
                    actualState.robot.useActuator(ActuatorOrder.MID_ATTRAPE_D, true);

                    actualState.robot.setLocomotionSpeed(Speed.SLOW_ALL);
                    actualState.robot.moveLengthwise(-530, hooksToConsider);
                    actualState.robot.turn(Math.PI / 2);
                    actualState.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
                    actualState.robot.moveLengthwise(250);

                    log.debug("position"+actualState.robot.getPosition());
                    log.debug("positionFast"+actualState.robot.getPositionFast());
                    log.debug("Orientation du HL :" + actualState.robot.getOrientationFast());
                }

                // Aller au cratère du fond
                actualState.robot.setDirectionStrategy(DirectionStrategy.FASTEST);

                actualState.robot.goTo(point1MilieuTable);
                actualState.robot.goTo(point2EntreeFinTable);
                actualState.robot.goTo(pointContournementModule);

                // actualState.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
                actualState.robot.useActuator(ActuatorOrder.MID_ATTRAPE_D, true);
                actualState.robot.useActuator(ActuatorOrder.REPLI_CALLE_D, false);

                // Prise du module 1er module (celui du fond)

                actualState.robot.goTo(point3AttrapperModule);

                actualState.robot.setLocomotionSpeed(Speed.SLOW_ALL);
                actualState.robot.turn(angleAttraperModule1);

                actualState.robot.catchModule(Side.RIGHT);
                //on l'ajoute au gamestate

                actualState.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);

                actualState.robot.setChargementModule(actualState.robot.getChargementModule()+1);
                actualState.table.cylindreCratereBase.isStillThere=false;

                actualState.robot.dejaFait.put(ScriptNames.SCRIPTED_GO_TO_CRATEREFOND,true);

                actualState.robot.setDirectionStrategy(DirectionStrategy.FORCE_FORWARD_MOTION);

                actualState.robot.useActuator(ActuatorOrder.MED_PELLETEUSE, false);
                actualState.robot.useActuator(ActuatorOrder.PRET_PELLE, false);

                // Prise des 1eres boules (celles du fond)
                actualState.robot.goTo(point4arriveDevantCratereFond, new ArrayList<Hook>(), false, false);
                actualState.robot.turnTo(posCratere1);
                actualState.robot.turn(angleDevantCratere1, hooksToConsider, true, true);

                actualState.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);

                actualState.robot.moveLengthwise(distanceCratereFondAvantBoules, new ArrayList<Hook>(), true, false);

                actualState.robot.setDirectionStrategy(DirectionStrategy.FASTEST);

                actualState.robot.catchBalls();
                // On actualise la gameState
                actualState.robot.setRempliDeBoules(true);
                actualState.table.ballsCratereBaseLunaire.isStillThere=false;

                // Livraison du 1er module
                actualState.robot.dejaFait.put(ScriptNames.SCRIPTED_GO_TO_LIVRAISON_MODULEFOND,true);

                actualState.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);

                actualState.robot.moveLengthwise(distanceCratereFondApresBoules);
                actualState.robot.turn(angleCratereFondAvantDepotModule);

                actualState.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
                actualState.robot.moveLengthwise(distanceCratereFondAvantDepotModule, new ArrayList<Hook>(), true, false);

                // Recalage en orientation

                if(Math.abs(Math.abs(actualState.robot.getOrientation()) - Math.PI/4)< recalageThresholdOrientation) {
                    actualState.robot.setOrientation(Math.PI / 4);
                }

                actualState.robot.setRempliDeBoules(true);
                actualState.table.ballsCratereBaseLunaire.isStillThere=false;


                actualState.robot.dejaFait.put(ScriptNames.SCRIPTED_GO_TO_LIVRAISON_MODULEFOND,true);

                actualState.robot.setLocomotionSpeed(Speed.FAST_T_MEDIUM_R);
                actualState.robot.useActuator(ActuatorOrder.LEVE_ASC,true);
		        actualState.robot.useActuator(ActuatorOrder.POUSSE_LARGUEUR_LENT, true);

                actualState.robot.setChargementModule(actualState.robot.getChargementModule()-1);

                actualState.robot.useActuator(ActuatorOrder.REPOS_LARGUEUR, false);

                actualState.robot.dejaFait.put(ScriptNames.SCRIPTED_GO_TO_CRATERE_LIVRAISON_BOULES1,true);

                actualState.robot.moveLengthwise(distanceCratereFondApresDepotModule);

                // Aller vers la zone de départ
		        actualState.robot.setDirectionStrategy(DirectionStrategy.FORCE_FORWARD_MOTION);
                actualState.robot.goTo(pointSortieCratereFond, hooksToConsider);

                // actualState.robot.turn(-Math.PI/2);

                actualState.robot.goTo(pointIntermediaireVersModule);

                // Prise du 2e module (celui de la zone de départ)
                actualState.robot.goTo(pointAvantModule2, hooksToConsider);

                actualState.robot.setDirectionStrategy(DirectionStrategy.FORCE_BACK_MOTION);

                actualState.robot.turn(angleDropModule2);

                // Recalage
                actualState.robot.setLocomotionSpeed(Speed.SLOW_ALL);
                actualState.robot.moveLengthwise(distanceRecalage, new ArrayList<Hook>(), true, false);
                Vec2 newPos = actualState.robot.getPosition();
                log.debug("Ancienne position :" + actualState.robot.getPositionFast());
                newPos.setX(1220);
                log.debug("Nouvelle position :" + actualState.robot.getPositionFast());
                actualState.robot.setPosition(newPos);

                log.debug("Orientation :" + actualState.robot.getOrientationFast());

                if (Math.abs(actualState.robot.getOrientationFast() - Math.PI)%(2*Math.PI) < recalageThresholdOrientation){
                    log.debug("Recalage en orientation :" + Math.abs(actualState.robot.getOrientationFast() - Math.PI)%(2*Math.PI));
                    actualState.robot.setOrientation(Math.PI);
                }

                actualState.robot.setLocomotionSpeed(Speed.FAST_T_MEDIUM_R);

                actualState.robot.catchModule(Side.LEFT);

                actualState.table.ballsCratereDepart.isStillThere=false;
                actualState.robot.setChargementModule(actualState.robot.getChargementModule()+1);

                actualState.robot.useActuator(ActuatorOrder.MED_PELLETEUSE, false);

                // Replie des actionneurs arrières et drop le second module
                actualState.robot.useActuator(ActuatorOrder.LIVRE_CALLE_D, false);
                actualState.robot.useActuator(ActuatorOrder.LIVRE_CALLE_G, true);
                actualState.robot.useActuator(ActuatorOrder.PREND_MODULE_D, false);
                actualState.robot.useActuator(ActuatorOrder.PREND_MODULE_G, false);
                actualState.robot.useActuator(ActuatorOrder.POUSSE_LARGUEUR_LENT, true);

                actualState.robot.setChargementModule(actualState.robot.getChargementModule()-1);

                actualState.robot.useActuator(ActuatorOrder.REPOS_LARGUEUR, false);

                actualState.robot.setDirectionStrategy(DirectionStrategy.FASTEST);

                // Livraison des 1eres boules
                actualState.robot.moveLengthwise(distanceApresModule2);
                actualState.robot.turn(angleAvantDeposeBoules);

                actualState.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);

                actualState.robot.moveLengthwise(distanceAvantDeposeBoules1, hooksToConsider, true, true);
                //actualState.robot.turn(angleDeposeBoules, new ArrayList<Hook>(), true, false);

                actualState.robot.setLocomotionSpeed(Speed.FAST_T_MEDIUM_R);

                actualState.robot.dropBalls();

                actualState.robot.setRempliDeBoules(false);
                actualState.robot.dejaFait.put(ScriptNames.SCRIPTED_GO_TO_CRATERE_PRES_BASE,true);

                actualState.robot.moveLengthwise(distanceReculApresDepotBoule1);

                // Prise des 2emes boules
                actualState.robot.turnTo(posCratere2);

                actualState.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
                actualState.robot.moveLengthwise(distanceCratereBaseAvantBoules, new ArrayList<Hook>(), true, true);

                actualState.robot.catchBalls();

                actualState.robot.setRempliDeBoules(true);
                actualState.table.ballsCratereDepart.isStillThere=false;

                actualState.robot.dejaFait.put(ScriptNames.SCRIPTED_GO_TO_CRATERE_LIVRAISON_BOULES2,true);

                actualState.robot.setLocomotionSpeed(Speed.FAST_T_MEDIUM_R);

                // Livraison des 2emes boules
                actualState.robot.moveLengthwise(distanceCratereBaseApresBoules);
                actualState.robot.turn(angleAvantDeposeBoules, new ArrayList<Hook>(), true, false);

                actualState.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);

                actualState.robot.moveLengthwise(distanceAvantDeposeBoules2, hooksToConsider, true, true);
                actualState.robot.turn(angleDeposeBoules, new ArrayList<Hook>(), true, false);

                actualState.robot.setLocomotionSpeed(Speed.FAST_T_MEDIUM_R);

                actualState.robot.dropBalls();

                actualState.robot.setRempliDeBoules(false);

                actualState.robot.moveLengthwise(distanceEsquiveRobot);
                actualState.robot.useActuator(ActuatorOrder.MID_ATTRAPE_G, false);
                actualState.robot.useActuator(ActuatorOrder.REPOS_ATTRAPE_G, false);

                log.debug("Temps du match : " + (System.currentTimeMillis() - debutMatch));

                log.debug("On tente une action bonus si on a le temps");

                actualState.robot.setDirectionStrategy(DirectionStrategy.FORCE_FORWARD_MOTION);

                // actualState.robot.goTo(bonus1);
                // actualState.robot.goTo(bonus2);
                // actualState.robot.goTo(dernierePos);

                // actualState.robot.useActuator(ActuatorOrder.MED_PELLETEUSE, false);
            }
        }
        catch(Exception e)
        {
            log.critical("Robot ou actionneur bloqué dans ScriptedGoTo");
            finalize(actualState, e);
            throw e;
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

            prisePremierModule=Boolean.parseBoolean(config.getProperty("prisePremierModule"));

            point1MilieuTable                  =new Vec2(
                    Integer.parseInt(config.getProperty("point1MilieuTable_x")),
                    Integer.parseInt(config.getProperty("point1MilieuTable_y")));

            point2EntreeFinTable= new Vec2(
                    Integer.parseInt(config.getProperty("point2EntreeFinTable_x")),
                    Integer.parseInt(config.getProperty("point2EntreeFinTable_y")));


            pointContournementModule          = new Vec2(
                    Integer.parseInt(config.getProperty("pointContournementModule_x")),
                    Integer.parseInt(config.getProperty("pointContournementModule_y")));

            point3AttrapperModule            = new Vec2(
                    Integer.parseInt(config.getProperty("point3AttrapperModule1_x")),
                    Integer.parseInt(config.getProperty("point3AttrapperModule1_y")));

            angleAttraperModule1             = Double.parseDouble(config.getProperty("angleAttraperModule1"));

            point4arriveDevantCratereFond      = new Vec2(
                    Integer.parseInt(config.getProperty("point4arriveDevantCratereFond_x")),
                    Integer.parseInt(config.getProperty("point4arriveDevantCratereFond_y")));
            posCratere1                        =new Vec2(
                    Integer.parseInt(config.getProperty("posCratere1_x")),
                    Integer.parseInt(config.getProperty("posCratere1_y")));

            angleDevantCratere1               = Double.parseDouble(config.getProperty("angleDevantCratere1"));
            distanceCratereFondAvantBoules      = Integer.parseInt(config.getProperty("distanceCratereFondAvantBoules"));
            distanceCratereFondApresBoules      = Integer.parseInt(config.getProperty("distanceCratereFondApresBoules"));

            angleCratereFondAvantDepotModule = Double.parseDouble(config.getProperty("angleCratereFondAvantDepotModule"));
            distanceCratereFondAvantDepotModule = Integer.parseInt(config.getProperty("distanceCratereFondAvantDepotModule"));
            distanceCratereFondApresDepotModule = Integer.parseInt(config.getProperty("distanceCratereFondApresDepotModule"));

            pointSortieCratereFond             = new Vec2(
                    Integer.parseInt(config.getProperty("pointSortieCratereFond_x")),
                    Integer.parseInt(config.getProperty("pointSortieCratereFond_y")));

            pointIntermediaireVersModule      = new Vec2(
                    Integer.parseInt(config.getProperty("pointIntermediaireVersModule_x")),
                    Integer.parseInt(config.getProperty("pointIntermediaireVersModule_y")));

            pointAvantModule2                 = new Vec2(
                    Integer.parseInt(config.getProperty("pointAvantModule2_x")),
                    Integer.parseInt(config.getProperty("pointAvantModule2_y")));

            angleDropModule2                 = Double.parseDouble(config.getProperty("angleDropModule2"));
            distanceApresModule2                = Integer.parseInt(config.getProperty("distanceApresModule2"));

            distanceRecalage                    = Integer.parseInt(config.getProperty("distanceRecalage"));

            distanceAvantDeposeBoules1          = Integer.parseInt(config.getProperty("distanceAvantDeposeBoules1"));
            distanceReculApresDepotBoule1       = Integer.parseInt(config.getProperty("distanceReculApresDepotBoule1"));

            posCratere2                        = new Vec2(
                    Integer.parseInt(config.getProperty("posCratere2_x")),
                    Integer.parseInt(config.getProperty("posCratere2_y")));

            distanceCratereBaseAvantBoules      = Integer.parseInt(config.getProperty("distanceCratereBaseAvantBoules"));
            distanceCratereBaseApresBoules      = Integer.parseInt(config.getProperty("distanceCratereBaseApresBoules"));

            angleAvantDeposeBoules           = Double.parseDouble(config.getProperty("angleAvantDeposeBoules"));
            distanceAvantDeposeBoules2          = Integer.parseInt(config.getProperty("distanceAvantDeposeBoules2"));
            angleDeposeBoules                = Double.parseDouble(config.getProperty("angleDeposeBoules"));

            distanceEsquiveRobot                = Integer.parseInt(config.getProperty("distanceEsquiveRobot"));

            bonus1                             = new Vec2(
                     Integer.parseInt(config.getProperty("bonus1_x")),
                     Integer.parseInt(config.getProperty("bonus1_y")));

            bonus2                             = new Vec2(
                    Integer.parseInt(config.getProperty("bonus2_x")),
                    Integer.parseInt(config.getProperty("bonus2_y")));

            dernierePos                        = new Vec2(
                    Integer.parseInt(config.getProperty("dernierePos_x")),
                    Integer.parseInt(config.getProperty("dernierePos_y")));




            detect = Boolean.parseBoolean(config.getProperty("capteurs_on"));
            recalageThresholdOrientation = Double.parseDouble(config.getProperty("tolerance_orientation_recalage"));

        } catch (ConfigPropertyNotFoundException e){
            log.debug("Revoir le code : impossible de trouver la propriété " + e.getPropertyNotFound());
        }
    }


    public void finalize(GameState state, Exception e) throws UnableToMoveException
    {
        log.debug("Exception " + e +"dans scriptedGOTO : Lancement du finalize !");
        state.robot.setBasicDetection(false);


    }

    @Override
    public Integer[] getVersion(GameState stateToConsider) {
        return versions;
    }
}
