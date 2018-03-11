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

    /** Position d'entrée du script */

    int xEntry=370;
    int yEntry=220;

    public ActivationPanneauDomotique(Config config, Log log, HookFactory hookFactory){
        super(config,log,hookFactory);
    }

    @Override
    public Circle entryPosition(int version, Vec2 robotPosition) throws BadVersionException {

        Vec2 positionentree=new Vec2(xEntry,yEntry);
        return new Circle(positionentree,0);
    }

    @Override
    public void execute(int versionToExecute, GameState actualState) throws InterruptedException, UnableToMoveException, ExecuteException, BlockedActuatorException {
        actualState.robot.turn(-Math.PI/2);
        actualState.robot.setLocomotionSpeed(Speed.SLOW_ALL);
        actualState.robot.moveLengthwise(135);
        actualState.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
        actualState.robot.goTo(new Vec2(xEntry,yEntry));
    }

    @Override
    public void finalize(GameState state, Exception e) throws UnableToMoveException {

    }

    @Override
    public Integer[] getVersion(GameState stateToConsider) {
        return new Integer[0];
    }



    @Override
    public int remainingScoreOfVersion(int version, GameState state) {
        return 0;
    }
}
