package scripts;

import enums.ActuatorOrder;
import enums.DirectionStrategy;
import enums.ScriptNames;
import enums.Speed;
import exceptions.BadVersionException;
import exceptions.BlockedActuatorException;
import exceptions.ConfigPropertyNotFoundException;
import exceptions.ExecuteException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.Locomotion.UnexpectedObstacleOnPathException;
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



public class ScriptedGoTo_CratereFond extends AbstractScript{

    /** PointsVisés, dstances & angles du script, override par la config */

    /** EntryPosition : dernier point appelé dans le ScriptedGoTo (afin de minimiser les chances fail) */
    Vec2 point3AttrapeModule1               = new Vec2(805, 1725);

    /** Manoeuvre pour attraper les 1eres boules */
    Vec2 point4arriveDevantCratereFond      = new Vec2(650,1785);
    Vec2 posCratere1                        = new Vec2(420, 1876);
    double angleDevantCratere1              = 0.04;
    int distanceCratereFondAvantBoules      = 65;

    private boolean detect = false;

    /**
     * Constructeur à appeller lorsqu'un script héritant de la classe AbstractScript est instancié.
     * Le constructeur se charge de renseigner la hookFactory, le système de config et de log.
     *
     * @param hookFactory La factory a utiliser pour générer les hooks dont pourra avoir besoin le script
     * @param config      le fichier de config a partir duquel le script pourra se configurer
     * @param log         le système de log qu'utilisera le script
     */

    protected ScriptedGoTo_CratereFond(HookFactory hookFactory, Config config, Log log)
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
                actualState.robot.dejaFait.put(ScriptNames.SCRIPTED_GO_TO_CRATEREFOND,true);

                actualState.robot.setDirectionStrategy(DirectionStrategy.FORCE_FORWARD_MOTION);

                actualState.robot.useActuator(ActuatorOrder.MED_PELLETEUSE, false);
                actualState.robot.useActuator(ActuatorOrder.PRET_PELLE, false);

                // Prise des 1eres boules (celles du fond)
                actualState.robot.goTo(point4arriveDevantCratereFond, new ArrayList<Hook>(), false, false);
                actualState.robot.turnTo(posCratere1);
                actualState.robot.turn(angleDevantCratere1,hooksToConsider, true, true);
                actualState.robot.moveLengthwise(distanceCratereFondAvantBoules);

                actualState.robot.setDirectionStrategy(DirectionStrategy.FASTEST);

                actualState.robot.catchBalls();

                actualState.robot.setRempliDeBoules(true);
                actualState.table.ballsCratereBaseLunaire.isStillThere=false;
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
            return new Circle(point3AttrapeModule1);
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
            point4arriveDevantCratereFond      = new Vec2(
                    Integer.parseInt(config.getProperty("point4arriveDevantCratereFond_x")),
                    Integer.parseInt(config.getProperty("point4arriveDevantCratereFond_y")));
            posCratere1                        =new Vec2(
                    Integer.parseInt(config.getProperty("posCratere1_x")),
                    Integer.parseInt(config.getProperty("posCratere1_y")));

            angleDevantCratere1               = Double.parseDouble(config.getProperty("angleDevantCratere1"));
            distanceCratereFondAvantBoules      = Integer.parseInt(config.getProperty("distanceCratereFondAvantBoules"));

        } catch (ConfigPropertyNotFoundException e){
            log.debug("Revoir le code : impossible de trouver la propriété " + e.getPropertyNotFound());
        }
    }
    public void finalize(GameState state, UnableToMoveException e) throws UnableToMoveException
    {
        log.debug("Exception " + e +"dans DropBalls : Lancement du finalize !");
        state.robot.setBasicDetection(false);
        throw e;
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

