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




public class ScriptedGoTo_ModuleFond extends AbstractScript {

    /** PointsVisés, dstances & angles du script, override par la config */

    private Vec2 point1MilieuTable = new Vec2(580,800);
    private Vec2 point2EntreeFinTable = new Vec2(850,1400);
    private Vec2 point3AttrapperModule1 = new Vec2(850,1760);
    Vec2 pointContournementModule           = new Vec2(805, 1560);
    double angleAttraperModule1             = -3*Math.PI/4;

    private boolean detect = false;



    /**
     * Constructeur à appeller lorsqu'un script héritant de la classe AbstractScript est instancié.
     * Le constructeur se charge de renseigner la hookFactory, le système de config et de log.
     *
     * @param hookFactory La factory a utiliser pour générer les hooks dont pourra avoir besoin le script
     * @param config      le fichier de config a partir duquel le script pourra se configurer
     * @param log         le système de log qu'utilisera le script
     */

    protected ScriptedGoTo_ModuleFond(HookFactory hookFactory, Config config, Log log)
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
                actualState.robot.dejaFait.put(ScriptNames.SCRIPTED_GO_TO_MODULEFOND,true);
                //Aller au cratère du fond
                actualState.robot.setDirectionStrategy(DirectionStrategy.FASTEST);

                actualState.robot.goTo(point1MilieuTable);
                actualState.robot.goTo(point2EntreeFinTable);

                actualState.robot.goTo(pointContournementModule);

                actualState.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);

                actualState.robot.useActuator(ActuatorOrder.MID_ATTRAPE_D, true);
                actualState.robot.useActuator(ActuatorOrder.REPLI_CALLE_D, false);

                // Prise du module 1er module (celui du fond)

                actualState.robot.goTo(point3AttrapperModule1);
                actualState.robot.turn(angleAttraperModule1);

                actualState.robot.catchModule(Side.RIGHT);

                actualState.robot.setChargementModule(actualState.robot.getChargementModule()+1);
                actualState.table.cylindreCratereBase.isStillThere=false;





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

            detect = Boolean.parseBoolean(config.getProperty("capteurs_on"));

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

