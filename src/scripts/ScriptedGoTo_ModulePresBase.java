package scripts;

import enums.ActuatorOrder;
import enums.ScriptNames;
import enums.Speed;
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



public class ScriptedGoTo_ModulePresBase extends AbstractScript{


    /** PointsVisés, dstances & angles du script, override par la config */

    boolean prisePremierModule=true;




    /**
     * Constructeur à appeller lorsqu'un script héritant de la classe AbstractScript est instancié.
     * Le constructeur se charge de renseigner la hookFactory, le système de config et de log.
     *
     * @param hookFactory La factory a utiliser pour générer les hooks dont pourra avoir besoin le script
     * @param config      le fichier de config a partir duquel le script pourra se configurer
     * @param log         le système de log qu'utilisera le script
     */

    protected ScriptedGoTo_ModulePresBase(HookFactory hookFactory, Config config, Log log)
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
                actualState.robot.dejaFait.put(ScriptNames.SCRIPTED_GO_TO_MODULE_PRES_BASE,true);

                if (prisePremierModule) {

                    actualState.robot.setOrientation(Math.PI);
                    actualState.robot.setPosition(new Vec2(605,194));

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



                //Initialisation des hooks pour permettre de replier les actionneurs pendant les déplacements
                //Hook prise module 1
                Hook PriseModule = hookFactory.newPositionHook(new Vec2(80, 1850), (float) Math.PI/2, 100, 10000);
                PriseModule.addCallback(new Callback(new PriseModule(), true, actualState));
                hooksToConsider.add(PriseModule);
                //Hook repli du largue module
                Hook ReposLargueModule = hookFactory.newPositionHook(new Vec2(550, 1650), (float) -Math.PI/4, 100, 10000);
                ReposLargueModule.addCallback(new Callback(new ReposLargueModule(), true, actualState));
                hooksToConsider.add(ReposLargueModule);

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

            prisePremierModule=Boolean.parseBoolean(config.getProperty("prisePremierModule"));

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

