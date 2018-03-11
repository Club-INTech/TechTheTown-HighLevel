package scripts;

import exceptions.BadVersionException;
import exceptions.BlockedActuatorException;
import exceptions.ExecuteException;
import exceptions.Locomotion.PointInObstacleException;
import exceptions.Locomotion.UnableToMoveException;
import hook.HookFactory;
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

        ActivationPanneauDomotique actPD=new ActivationPanneauDomotique(config,log,hookFactory);
        actPD.goToThenExec(0,gameState);
        gameState.setRecognitionDone(true);
        gameState.setIndicePattern(0);
        TakeCubes tk2=new TakeCubes(config,log,hookFactory);
        tk2.goToThenExec(0,gameState);
        ActiveAbeille activeAbeille=new ActiveAbeille(config,log,hookFactory);
        activeAbeille.goToThenExec(0,gameState);
        TakeCubes tk1=new TakeCubes(config,log,hookFactory);
        tk1.goToThenExec(1,gameState);
        DeposeCubes dpCubes0=new DeposeCubes(config,log,hookFactory);
        dpCubes0.goToThenExec(0,gameState);
        TakeCubes tk0=new TakeCubes(config,log,hookFactory);
        tk0.goToThenExec(2,gameState);
        DeposeCubes dpCubes1=new DeposeCubes(config,log,hookFactory);
        dpCubes1.goToThenExec(1,gameState);
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
