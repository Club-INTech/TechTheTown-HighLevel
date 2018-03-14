package scripts;

import enums.ActuatorOrder;
import enums.ConfigInfoRobot;
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

public class ActiveAbeille extends AbstractScript {

    /** Active l'abeille */
    private int securityDistance = 60; //distance de sécurité pour ne pas cogner le mur en tournant
    private int radius = config.getInt(ConfigInfoRobot.ROBOT_RADIUS); //rayon du robot

    public ActiveAbeille(Config config, Log log, HookFactory hookFactory){
        super(config,log,hookFactory);
    }

    @Override
    public void execute(int versionToExecute, GameState actualState) throws InterruptedException, UnableToMoveException, ExecuteException, BlockedActuatorException {
        //On se tourne vers l'abeille
        actualState.robot.turn(Math.PI/2);
        //ON s'avance vers l'abeille
        actualState.robot.moveLengthwise(config.getInt(ConfigInfoRobot.distance_abeille));
        //On active le bras
        actualState.robot.useActuator(ActuatorOrder.ACTIVE_BRAS_AVANT_POUR_ABEILLE,true);
        //On tourne de 90° pour lancer l'abeille
        actualState.robot.turn(Math.PI,true);
        //On relève le bras
        actualState.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS_AVANT, true);
        int xEntry = 1500-radius-securityDistance;
        int yEntry = 2000-radius-securityDistance;
        Vec2 aim =new Vec2(xEntry,yEntry);
        actualState.robot.goTo(aim);

    }

    @Override
    public Circle entryPosition(int version, Vec2 robotPosition) throws BadVersionException {
        //Se place tout de suite à la bonne position pour tourner et activer l'abeille
        int xEntry = 1500-radius-securityDistance;
        int yEntry = 2000-radius-securityDistance;
        return new Circle(new Vec2(xEntry,yEntry));
    }

    @Override
    public void finalize(GameState state, Exception e) throws UnableToMoveException {}



    @Override
    public Integer[] getVersion(GameState stateToConsider) {
        return new Integer[0];
    }

    @Override
    public int remainingScoreOfVersion(int version, GameState state) {
        return 0;
    }


}
