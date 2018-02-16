package scripts;

import enums.Speed;
import exceptions.BadVersionException;
import exceptions.BlockedActuatorException;
import exceptions.ExecuteException;
import exceptions.Locomotion.UnableToMoveException;
import hook.HookFactory;
import pfg.config.Config;
import smartMath.Circle;
import smartMath.Vec2;
import strategie.GameState;
import utils.Log;


public class ActivationPanneauDomotique extends AbstractScript{

    public ActivationPanneauDomotique(Config config, Log log, HookFactory hookFactory){
        super(config,log,hookFactory);
    }


    public Circle entryPosition(int version, int ray, Vec2 robotPosition) throws BadVersionException {
        int xEntry=370;
        int yEntry=350;
        Vec2 positionentree=new Vec2(xEntry,yEntry);
        return new Circle(positionentree);
    }

    @Override
    public void execute(int versionToExecute, GameState actualState) throws InterruptedException, UnableToMoveException, ExecuteException, BlockedActuatorException {
        actualState.robot.setLocomotionSpeed(Speed.ULTRA_SLOW_ALL);
        actualState.robot.setOrientation(Math.PI/2);
        actualState.robot.moveLengthwise(350);
    }

    @Override
    public void finalize(GameState state, Exception e) throws UnableToMoveException {

    }

    @Override
    public Integer[] getVersion(GameState stateToConsider) {
        return new Integer[0];
    }

    @Override
    public Integer[][] getVersion2(GameState stateToConsider) {
        return new Integer[0][];
    }

    @Override
    public int remainingScoreOfVersion(int version, GameState state) {
        return 0;
    }
}
