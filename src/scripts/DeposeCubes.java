package scripts;

import enums.ActuatorOrder;
import enums.ConfigInfoRobot;
import enums.Speed;
import exceptions.BadVersionException;
import exceptions.ExecuteException;
import exceptions.Locomotion.ImmobileEnnemyForOneSecondAtLeast;
import exceptions.Locomotion.UnableToMoveException;
import hook.HookFactory;
import pfg.config.Config;
import smartMath.Circle;
import smartMath.Vec2;
import strategie.GameState;
import utils.Log;

public class DeposeCubes extends AbstractScript {

    /**
     * Eléments appelés par la config
     */
    private int distancePenetrationZone; //on pénètre la zone de construction de cette distance
    private int dimensionporte;
    private int radius;

    public DeposeCubes(Config config, Log log, HookFactory hookFactory) {
        super(config, log, hookFactory);
        versions = new Integer[]{0, 1};
        updateConfig();
    }

    /**
     * Cette méthode dépose les cubes pris par les deux bras
     * @param stateToConsider
     * @throws ExecuteException
     * @throws UnableToMoveException
     */
    @Override
    public void execute(int version, GameState stateToConsider) throws ExecuteException, UnableToMoveException, ImmobileEnnemyForOneSecondAtLeast {
        //On se tourne vers la zone de construction
        log.debug("////////// Execution DeposeCubes version "+version+" //////////");
        Vec2 directionToGo=null;
        double prodScal=0;
        try {
            directionToGo = (this.entryPosition(version, stateToConsider.robot.getPosition()).getCenter()).plusNewVector(new Vec2(0,-50)).minusNewVector(stateToConsider.robot.getPosition());
            prodScal=directionToGo.dot(new Vec2(100.0,stateToConsider.robot.getOrientation()));
        } catch (BadVersionException e) {
            e.printStackTrace();
            log.debug("BadVersionException: version "+version+" specified");
        }

        boolean aDejaDeposeUneTour=false;
        if (prodScal>0){
            if (stateToConsider.isTourAvantRemplie()) {
                stateToConsider.robot.turn(-Math.PI / 2);
                stateToConsider.robot.setLocomotionSpeed(Speed.SLOW_ALL);
                //On ouvre la porte
                stateToConsider.robot.useActuator(ActuatorOrder.OUVRE_LA_PORTE_AVANT, false);

                stateToConsider.robot.moveLengthwise(distancePenetrationZone);

                //On recule de la largeur de la porte + de la longueur avancée dans la zone
                stateToConsider.robot.setLocomotionSpeed(Speed.FAST_ALL);
                stateToConsider.robot.moveLengthwise(-(distancePenetrationZone + 2 * dimensionporte));

                //On ferme la porte
                stateToConsider.robot.useActuator(ActuatorOrder.FERME_LA_PORTE_AVANT, false);
                aDejaDeposeUneTour=true;
            }
            if (stateToConsider.isTourArriereRemplie()) {
                stateToConsider.robot.turn(Math.PI / 2);

                //On avance de la dimension de la porte + de la distance poussée
                stateToConsider.robot.useActuator(ActuatorOrder.OUVRE_LA_PORTE_ARRIERE, true);
                stateToConsider.robot.setLocomotionSpeed(Speed.SLOW_ALL);
                if (aDejaDeposeUneTour) {
                    stateToConsider.robot.moveLengthwise(-(distancePenetrationZone + 2 * dimensionporte));
                }
                else{
                    stateToConsider.robot.moveLengthwise(-distancePenetrationZone);
                }
                stateToConsider.robot.setLocomotionSpeed(Speed.FAST_ALL);
                stateToConsider.robot.moveLengthwise(dimensionporte + distancePenetrationZone);
                stateToConsider.robot.useActuator(ActuatorOrder.FERME_LA_PORTE_ARRIERE, false);
            }
        }
        else{
            if (stateToConsider.isTourArriereRemplie()) {
                stateToConsider.robot.turn(Math.PI / 2);
                stateToConsider.robot.setLocomotionSpeed(Speed.SLOW_ALL);
                //On ouvre la porte
                stateToConsider.robot.useActuator(ActuatorOrder.OUVRE_LA_PORTE_ARRIERE, false);
                //On rentre dans la zone
                stateToConsider.robot.moveLengthwise(-distancePenetrationZone);
                //On recule de la largeur de la porte + de la longueur avancée dans la zone
                stateToConsider.robot.setLocomotionSpeed(Speed.FAST_ALL);
                stateToConsider.robot.moveLengthwise(distancePenetrationZone + 2 * dimensionporte);
                //On ferme la porte
                stateToConsider.robot.useActuator(ActuatorOrder.FERME_LA_PORTE_ARRIERE, false);
                aDejaDeposeUneTour=true;
            }
            if (stateToConsider.isTourAvantRemplie()) {
                stateToConsider.robot.turn(-Math.PI / 2);

                //On avance de la dimension de la porte + de la distance poussée
                stateToConsider.robot.useActuator(ActuatorOrder.OUVRE_LA_PORTE_AVANT, true);
                stateToConsider.robot.setLocomotionSpeed(Speed.SLOW_ALL);
                if (aDejaDeposeUneTour) {
                    stateToConsider.robot.moveLengthwise(distancePenetrationZone + 2 * dimensionporte);
                }
                else{
                    stateToConsider.robot.moveLengthwise(distancePenetrationZone);
                }
                stateToConsider.robot.setLocomotionSpeed(Speed.FAST_ALL);
                stateToConsider.robot.moveLengthwise(-(dimensionporte + distancePenetrationZone));
                stateToConsider.robot.useActuator(ActuatorOrder.FERME_LA_PORTE_AVANT, false);
            }
        }
        stateToConsider.robot.setLocomotionSpeed(Speed.FAST_ALL);
        //les deux premières sont déposées
        stateToConsider.setTourAvantRemplie(false);
        stateToConsider.setTourArriereRemplie(false);
        log.debug("////////// End DeposeCubes version "+version+" //////////");
    }

    @Override
    public Circle entryPosition(int version, Vec2 robotPosition) throws BadVersionException {
        //Zone de dépose des cubes proche de la base
        if (version==0) {
            int xentry = 970;
            int yentry = 150 + radius;
            Vec2 position = new Vec2(xentry, yentry);
            return new Circle(position);
        }
        //Zone de dépose des cubes proche du pattern
        else if (version==1) {
            int xEntry = 600;
            int yEntry = 150 + radius;
            Vec2 positionentree = new Vec2(xEntry, yEntry);
            return new Circle(positionentree);
        } else {
            throw new BadVersionException();
        }
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

    @Override
    public void updateConfig() {
        super.updateConfig();
        distancePenetrationZone = config.getInt(ConfigInfoRobot.DISTANCE_PENETRATION_ZONE_DEPOSE_CUBES);
        dimensionporte = config.getInt(ConfigInfoRobot.DIMENSION_PORTES);
        radius = config.getInt(ConfigInfoRobot.ROBOT_RADIUS);
    }
}
