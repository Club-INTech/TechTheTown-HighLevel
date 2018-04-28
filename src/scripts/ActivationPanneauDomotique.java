package scripts;

import enums.ConfigInfoRobot;
import enums.Speed;
import exceptions.BadVersionException;
import exceptions.BlockedActuatorException;
import exceptions.ExecuteException;
import exceptions.Locomotion.ImmobileEnnemyForOneSecondAtLeast;
import exceptions.Locomotion.UnableToMoveException;
import hook.HookFactory;
import pfg.config.Config;
import smartMath.Circle;
import smartMath.Vec2;
import strategie.GameState;
import utils.Log;


public class ActivationPanneauDomotique extends AbstractScript{
    /** Position d'entrée du script */

    private int xEntry=370;
    private int yEntry=230;

    /** Eléments appelés par la config */

    private int distanceInterrupteur;
    private boolean basicDetection;

    public ActivationPanneauDomotique(Config config, Log log, HookFactory hookFactory){
        super(config,log,hookFactory);
        updateConfig();
    }

    @Override
    public Circle entryPosition(int version, Vec2 robotPosition) throws BadVersionException {
        Vec2 positionEntree=new Vec2(this.xEntry,this.yEntry);
        return new Circle(positionEntree,0);
    }

    @Override
    public void execute(int versionToExecute, GameState state) throws InterruptedException, UnableToMoveException, ExecuteException, BlockedActuatorException, ImmobileEnnemyForOneSecondAtLeast {
        log.debug("////////// Execution ActivePanneauDomotique version "+versionToExecute+" //////////");
        state.robot.turn(-Math.PI/2);
        state.robot.setLocomotionSpeed(Speed.SLOW_ALL);
        if(!(state.isCapteursActivés())){
            state.robot.moveLengthwiseWithoutDetection(distanceInterrupteur,false);
        }
        else{
            state.robot.moveLengthwise(distanceInterrupteur);
        }
        state.addObtainedPoints(25);
        if(!(state.isCapteursActivés())){
            state.robot.goToWithoutDetection(new Vec2(xEntry,yEntry));
        }
        else {
            state.robot.goTo(new Vec2(xEntry, yEntry));
        }
        state.robot.setLocomotionSpeed(Speed.DEFAULT_SPEED);
        state.setPanneauActive(true);
        log.debug("////////// End ActivePanneauDomotique version "+versionToExecute+" //////////");
    }

    @Override
    public void finalize(GameState state, Exception e) throws UnableToMoveException { }

    @Override
    public int[] getVersion(GameState stateToConsider) { return new int[]{0}; }

    @Override
    public int remainingScoreOfVersion(int version, GameState state) {
        return 25;
    }

    @Override
    public void updateConfig() {
        super.updateConfig();
        this.distanceInterrupteur = config.getInt(ConfigInfoRobot.DISTANCE_INTERRUPTEUR);
        this.basicDetection = config.getBoolean(ConfigInfoRobot.BASIC_DETECTION);
    }
}
