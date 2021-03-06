package scripts;

import enums.ActuatorOrder;
import enums.ConfigInfoRobot;
import enums.ScriptNames;
import enums.Speed;
import exceptions.BadVersionException;
import exceptions.BlockedActuatorException;
import exceptions.ExecuteException;
import exceptions.Locomotion.ImmobileEnnemyForOneSecondAtLeast;
import exceptions.Locomotion.PointInObstacleException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.NoPathFound;
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
    private boolean usingBasicDetection;
    private boolean usingAdvancedDetection;

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
    public void execute(int versionToExecute, GameState state) throws UnableToMoveException, ImmobileEnnemyForOneSecondAtLeast {
        log.debug("////////// Execution ActivePanneauDomotique version "+versionToExecute+" //////////");
        if (usingAdvancedDetection) {
            state.robot.useActuator(ActuatorOrder.SUS_OFF,true);
            state.setCapteursActivated(false);
        }
        if(usingBasicDetection){
            state.robot.setBasicDetection(false);
        }
        state.robot.turn(-Math.PI/2);
        state.robot.setLocomotionSpeed(Speed.SLOW_ALL);
        //Il se peut qu'on fonce dans un mur
        state.robot.goToWithoutDetection(new Vec2(this.xEntry, this.yEntry-distanceInterrupteur),true);
        state.addObtainedPoints(25);
        state.setPanneauActive(true);
        if (usingAdvancedDetection) {
            state.robot.useActuator(ActuatorOrder.SUS_ON,true);
            state.setCapteursActivated(true);
        }
        if(usingBasicDetection){
            state.robot.setBasicDetection(true);
        }
        state.robot.goTo(new Vec2(xEntry, yEntry));
        state.robot.setLocomotionSpeed(Speed.DEFAULT_SPEED);
        log.debug("////////// End ActivePanneauDomotique version "+versionToExecute+" //////////");
    }

    @Override
    public void finalize(GameState state, Exception e) {
        state.robot.setLocomotionSpeed(Speed.DEFAULT_SPEED);
    }

    @Override
    public int[] getVersion(GameState stateToConsider) { return new int[]{0}; }

    @Override
    public int remainingScoreOfVersion(int version, GameState state) {
        return 25;
    }

    @Override
    public void goToThenExec(int versionToExecute, GameState state) throws PointInObstacleException, BadVersionException, NoPathFound, ExecuteException, BlockedActuatorException, UnableToMoveException, ImmobileEnnemyForOneSecondAtLeast {
        state.setLastScript(ScriptNames.ACTIVATION_PANNEAU_DOMOTIQUE);
        state.setLastScriptVersion(versionToExecute);
        super.goToThenExec(versionToExecute, state);
    }

    @Override
    public void updateConfig() {
        super.updateConfig();
        this.distanceInterrupteur = config.getInt(ConfigInfoRobot.DISTANCE_INTERRUPTEUR);
        this.usingBasicDetection = config.getBoolean(ConfigInfoRobot.BASIC_DETECTION);
        this.usingAdvancedDetection = config.getBoolean(ConfigInfoRobot.ADVANCED_DETECTION);
    }
}
