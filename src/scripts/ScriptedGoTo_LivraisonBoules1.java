package scripts;

import enums.*;
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

/**
 * Created by melanie on 09/05/17.
 */



public class ScriptedGoTo_LivraisonBoules1 extends AbstractScript {


        /** Point d'entrée du script */
        Vec2 entryPos = new Vec2(1180,1000);


    /** Déplacements jusqu'à la zone de départ */

    int distanceCratereFondApresDepotModule = 55;

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

    private double recalageThresholdOrientation;

        /**
         * Constructeur à appeller lorsqu'un script héritant de la classe AbstractScript est instancié.
         * Le constructeur se charge de renseigner la hookFactory, le système de config et de log.
         *
         * @param hookFactory La factory a utiliser pour générer les hooks dont pourra avoir besoin le script
         * @param config      le fichier de config a partir duquel le script pourra se configurer
         * @param log         le système de log qu'utilisera le script
         */

        protected ScriptedGoTo_LivraisonBoules1(HookFactory hookFactory, Config config, Log log)
        {
            super(hookFactory, config, log);

            versions = new Integer[]{0};
        }


        @Override
        public void execute(int versionToExecute, GameState actualState, ArrayList<Hook> hooksToConsider) throws ExecuteException, UnableToMoveException, BlockedActuatorException
        {
            updateConfig();
            try{

                if (versionToExecute==0)
                {

                    actualState.robot.dejaFait.put(ScriptNames.SCRIPTED_GO_TO_CRATERE_LIVRAISON_BOULES1,true);

                    actualState.robot.moveLengthwise(distanceCratereFondApresDepotModule);

                    // Aller vers la zone de départ
                    actualState.robot.goTo(pointSortieCratereFond, hooksToConsider);

                    actualState.robot.turn(-Math.PI/2);

                    actualState.robot.goTo(pointIntermediaireVersModule);

                    // Prise du 2e module (celui de la zone de départ)
                    actualState.robot.goTo(pointAvantModule2, hooksToConsider);

                    actualState.robot.setDirectionStrategy(DirectionStrategy.FORCE_BACK_MOTION);

                    actualState.robot.turn(angleDropModule2);


                    // Recalage
                    actualState.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
                    actualState.robot.moveLengthwise(distanceRecalage, new ArrayList<Hook>(), true, false);
                    Vec2 newPos = actualState.robot.getPosition();
                    newPos.setX(1220);
                    actualState.robot.setPosition(newPos);

                    log.debug("Orientation :" + actualState.robot.getOrientationFast());

                    if (Math.abs(actualState.robot.getOrientationFast() - Math.PI)%(2*Math.PI) < recalageThresholdOrientation){
                        log.debug("Recalage en orientation :" + Math.abs(actualState.robot.getOrientationFast() - Math.PI)%(2*Math.PI));
                        actualState.robot.setOrientation(Math.PI);
                    }

                    actualState.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);

                    actualState.robot.catchModule(Side.LEFT);

                    actualState.table.ballsCratereDepart.isStillThere=false;
                    actualState.robot.setChargementModule(actualState.robot.getChargementModule()+1);

                    actualState.robot.useActuator(ActuatorOrder.MED_PELLETEUSE, false);

                    // Replie des actionneurs arrières et drop le second module
                    actualState.robot.useActuator(ActuatorOrder.LIVRE_CALLE_D, false);
                    actualState.robot.useActuator(ActuatorOrder.LIVRE_CALLE_G, true);
                    actualState.robot.useActuator(ActuatorOrder.PREND_MODULE_D, false);
                    actualState.robot.useActuator(ActuatorOrder.PREND_MODULE_G, false);
                    actualState.robot.useActuator(ActuatorOrder.POUSSE_LARGUEUR, true);

                    actualState.robot.setChargementModule(actualState.robot.getChargementModule()-1);

                    actualState.robot.useActuator(ActuatorOrder.REPOS_LARGUEUR, false);

                    actualState.robot.setDirectionStrategy(DirectionStrategy.FASTEST);

                    // Livraison des 1eres boules
                    actualState.robot.moveLengthwise(distanceApresModule2);
                    actualState.robot.turn(-Math.PI/2+0.15);
                    actualState.robot.moveLengthwise(distanceAvantDeposeBoules1, hooksToConsider, true, true);
                    actualState.robot.turn(-Math.PI/2);

                    actualState.robot.dropBalls();

                    actualState.robot.setRempliDeBoules(false);

                }

            }
            catch(UnableToMoveException e)
            {
                log.critical("Robot ou actionneur bloqué dans DropBalls");
                finalize(actualState, e);
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

            recalageThresholdOrientation = Double.parseDouble(config.getProperty("tolerance_orientation_recalage"));

        } catch (ConfigPropertyNotFoundException e){
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

