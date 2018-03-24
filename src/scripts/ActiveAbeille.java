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
    int distanceAbeille;
    int xEntry;
    int yEntry;


    public ActiveAbeille(Config config, Log log, HookFactory hookFactory){
        super(config,log,hookFactory);
        updateConfig();
        xEntry = 1500-radius-securityDistance;
        yEntry = 2000-radius-securityDistance;
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
            //On se tourne vers l'abeille
            if (Math.abs(actualState.robot.getOrientation()-Math.PI/4)>Math.PI/6) {
                actualState.robot.turn(Math.PI / 4);
            }
            actualState.robot.goTo(new Vec2(xEntry,yEntry));
            //ON s'avance vers l'abeille
            //On active le bras
            //Déjà fait en hook
            //actualState.robot.useActuator(ActuatorOrder.ACTIVE_BRAS_AVANT_POUR_ABEILLE,false);
            //On tourne de 90° pour lancer l'abeille
            actualState.robot.turn(Math.PI/2,true);
            //On relève le bras
            actualState.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS_AVANT, false);
        }
        else{
            //On refait la même chose avec le bras arrière
            if (Math.abs(actualState.robot.getOrientation()-(-3*Math.PI/4))>Math.PI/6) {
                actualState.robot.turn(-3 * Math.PI / 4);
            }
            actualState.robot.goTo(new Vec2(xEntry,yEntry));
            //Déjà fait en hook
            //actualState.robot.useActuator(ActuatorOrder.ACTIVE_BRAS_ARRIERE_POUR_ABEILLE,true);
            //actualState.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
            actualState.robot.turn(-Math.PI/2,true);
            //actualState.robot.setLocomotionSpeed(Speed.FAST_ALL);
            actualState.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS_ARRIERE, false);
        }


        Vec2 aim =new Vec2(xEntry,yEntry);
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
