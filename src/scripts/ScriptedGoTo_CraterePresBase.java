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

public class ScriptedGoTo_CraterePresBase extends AbstractScript {

    /** Point d'entrée du script */
    private Circle cratere2 = new Circle(new Vec2(850, 540), 300,Math.PI/3, Math.PI, true);

    /** Manoeuvre pour prendre les 2emes boules */
    Vec2 posCratere2                        = cratere2.getCenter();
    int distanceCratereBaseAvantBoules      = 235;
    int distanceCratereBaseApresBoules      = -190;
    int distanceReculApresDepotBoule1       = -180;

    /**
     * Constructeur à appeller lorsqu'un script héritant de la classe AbstractScript est instancié.
     * Le constructeur se charge de renseigner la hookFactory, le système de config et de log.
     *
     * @param hookFactory La factory a utiliser pour générer les hooks dont pourra avoir besoin le script
     * @param config      le fichier de config a partir duquel le script pourra se configurer
     * @param log         le système de log qu'utilisera le script
     */

    protected ScriptedGoTo_CraterePresBase(HookFactory hookFactory, Config config, Log log)
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
                actualState.robot.dejaFait.put(ScriptNames.SCRIPTED_GO_TO_CRATERE_PRES_BASE,true);

                actualState.robot.moveLengthwise(distanceReculApresDepotBoule1);

                // Prise des 2emes boules
                actualState.robot.turnTo(posCratere2);

//                actualState.robot.turn(angleCorrectionCratere2, hooksToConsider,true, true);

                actualState.robot.moveLengthwise(distanceCratereBaseAvantBoules);

                actualState.robot.catchBalls();

                actualState.robot.setRempliDeBoules(true);
                actualState.table.ballsCratereDepart.isStillThere=false;

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
            return cratere2;
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


            distanceReculApresDepotBoule1       = Integer.parseInt(config.getProperty("distanceReculApresDepotBoule1"));

            posCratere2                        = new Vec2(
                    Integer.parseInt(config.getProperty("posCratere2_x")),
                    Integer.parseInt(config.getProperty("posCratere2_y")));
           distanceCratereBaseAvantBoules      = Integer.parseInt(config.getProperty("distanceCratereBaseAvantBoules"));
            distanceCratereBaseApresBoules      = Integer.parseInt(config.getProperty("distanceCratereBaseApresBoules"));


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
    public void finalize(GameState state, UnableToMoveException e) throws UnableToMoveException
    {
        log.debug("Exception " + e +"dans DropBalls : Lancement du finalize !");
        state.robot.setBasicDetection(false);
        throw e;
    }

    @Override
    public Integer[] getVersion(GameState stateToConsider) {
        return versions;
    }
}
