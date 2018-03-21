package scripts;

import enums.BrasUtilise;
import enums.Speed;
import exceptions.BadVersionException;
import exceptions.BlockedActuatorException;
import exceptions.ExecuteException;
import exceptions.Locomotion.PointInObstacleException;
import exceptions.Locomotion.UnableToMoveException;
import hook.HookFactory;
import hook.HookNames;
import org.junit.Before;
import patternRecognition.PatternRecognition;
import pfg.config.Config;
import smartMath.Circle;
import smartMath.Vec2;
import strategie.GameState;
import utils.Log;

public class MatchScript extends AbstractScript {

    public MatchScript(Config config, Log log, HookFactory hookFactory){
        super(config,log,hookFactory);
    }

    @Override
    public void execute(int versionToexecute,GameState gameState) throws UnableToMoveException, BadVersionException, ExecuteException, BlockedActuatorException, PointInObstacleException {

        hookFactory.configureHook(HookNames.ACTIVE_BRAS_AVANT_ABEILLE, HookNames.ACTIVE_BRAS_ARRIERE_ABEILLE);

        ActivationPanneauDomotique actPD=new ActivationPanneauDomotique(config,log,hookFactory);
        actPD.goToThenExec(0,gameState);

        gameState.setRecognitionDone(true);
        gameState.setIndicePattern(0);

        gameState.setTakeCubesBras(BrasUtilise.ARRIERE);
        TakeCubes tk2=new TakeCubes(config,log,hookFactory);
        tk2.goToThenExec(2,gameState);

        ActiveAbeille activeAbeille=new ActiveAbeille(config,log,hookFactory);
        Vec2 directionToGo=(activeAbeille.entryPosition(0, gameState.robot.getPosition()).getCenter()).minusNewVector(gameState.robot.getPosition());
        double prodScal=directionToGo.dot(new Vec2(100.0,gameState.robot.getOrientation()));
        if (prodScal>0) {
            hookFactory.enableHook(HookNames.ACTIVE_BRAS_AVANT_ABEILLE);
        }
        else{
            hookFactory.enableHook(HookNames.ACTIVE_BRAS_ARRIERE_ABEILLE);
        }
        activeAbeille.goToThenExec(0,gameState);
        if (prodScal>0) {
            hookFactory.disableHook(HookNames.ACTIVE_BRAS_AVANT_ABEILLE);
        }
        else{
            hookFactory.disableHook(HookNames.ACTIVE_BRAS_ARRIERE_ABEILLE);
        }

        gameState.setTakeCubesBras(BrasUtilise.AVANT);
        TakeCubes tk1=new TakeCubes(config,log,hookFactory);
        tk1.goToThenExec(1,gameState);

        DeposeCubes dpCubes0=new DeposeCubes(config,log,hookFactory);
        dpCubes0.goToThenExec(0,gameState);

        gameState.setTakeCubesBras(BrasUtilise.ARRIERE);
        TakeCubes tk0=new TakeCubes(config,log,hookFactory);
        tk0.goToThenExec(0,gameState);

        DeposeCubes dpCubes1=new DeposeCubes(config,log,hookFactory);
        dpCubes1.goToThenExec(1,gameState);

        log.debug("Fin MatchScript");
    }

    @Override
    public Circle entryPosition(int version, Vec2 robotPosition) throws BadVersionException {
        return new Circle(robotPosition);
    }

    @Override
    public int remainingScoreOfVersion(int version, final GameState state) {
        return 0;
    }

    @Override
    public void finalize(GameState state, Exception e) throws UnableToMoveException {

    }

    @Override
    public Integer[] getVersion(GameState stateToConsider) {
        return versions;
    }

}
