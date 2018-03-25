package scripts;

import enums.ActuatorOrder;
import enums.ConfigInfoRobot;
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

public class ActiveAbeille extends AbstractScript {

    /** Active l'abeille */
    private int securityDistance = 60; //distance de sécurité pour ne pas cogner le mur en tournant

    /** Eléments appelés par la config */
    private int radius ; //rayon du robot
    private int distanceAbeille;
    private int xEntry;
    private int yEntry;
    private int xExit;
    private int yExit;


    public ActiveAbeille(Config config, Log log, HookFactory hookFactory){
        super(config,log,hookFactory);
        updateConfig();
        xEntry=1300;
        yEntry=1765;
        xExit=1500-radius-securityDistance;
        yExit=2000-radius-securityDistance;
    }
    @Override
    public void updateConfig() {
        super.updateConfig();
        distanceAbeille = config.getInt(ConfigInfoRobot.DISTANCE_ABEILLE);
        radius = config.getInt(ConfigInfoRobot.ROBOT_RADIUS);
    }

    @Override
    public void execute(int versionToExecute, GameState actualState) throws InterruptedException, UnableToMoveException, ExecuteException, BlockedActuatorException {

        if(actualState.robot.getOrientation()>0 &&actualState.robot.getOrientation()<Math.PI ){
            //On s'avance vers l'abeille
            actualState.robot.goTo(new Vec2(xEntry,yEntry));
            //On active le bras
            //Déjà fait en hook
            //actualState.robot.useActuator(ActuatorOrder.ACTIVE_BRAS_AVANT_POUR_ABEILLE,false);
            //On tourne de 90° pour lancer l'abeille
            Vec2 position = actualState.robot.getPosition();
            Vec2 aim=new Vec2(xExit,yExit);
            Vec2 move = aim.minusNewVector(position);
            double angle = move.getA();
            actualState.robot.turn(angle,true);
            //On relève le bras
            actualState.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS_AVANT, false);
        }
        else{
            //On refait la même chose avec le bras arrière
            actualState.robot.goTo(new Vec2(xEntry,yEntry));
            //Déjà fait en hook
            //actualState.robot.useActuator(ActuatorOrder.ACTIVE_BRAS_ARRIERE_POUR_ABEILLE,true);
            //TODO : à tester
            Vec2 position = actualState.robot.getPosition();
            Vec2 aim=new Vec2(xExit,yExit);
            Vec2 move = aim.minusNewVector(position);
            move.dotFloat(-1);
            double angle = move.getA();
            actualState.robot.turn(angle,true);
            actualState.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS_ARRIERE, false);
        }

        Vec2 aim = new Vec2(xExit,yExit);
        actualState.robot.goTo(aim);
    }

    @Override
    public Circle entryPosition(int version, Vec2 robotPosition) throws BadVersionException {
        log.debug("Position d'entrée ActiveAbeille"+robotPosition);
        return new Circle(robotPosition);
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
