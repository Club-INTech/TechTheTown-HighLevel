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

    @Override
    public Circle entryPosition(int version, Vec2 robotPosition) throws BadVersionException {
        int xEntry=350;
        int yEntry=370;
        Vec2 positionentree=new Vec2(xEntry,yEntry);
        return new Circle(positionentree);
    }

    @Override
    public void execute(int versionToExecute, GameState actualState) throws InterruptedException, UnableToMoveException, ExecuteException, BlockedActuatorException {
        //l'accès est scripté
        Vec2 aim=new Vec2(370,350);
        actualState.robot.goTo(aim);
        actualState.robot.turn(-Math.PI/2);
        actualState.robot.setLocomotionSpeed(Speed.ULTRA_SLOW_ALL);
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
    public int remainingScoreOfVersion(int version, GameState state) {
        return 0;
    }
}
