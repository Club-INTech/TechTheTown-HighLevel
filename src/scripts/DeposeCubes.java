package scripts;

import enums.ActuatorOrder;
import enums.ConfigInfoRobot;
import enums.Speed;
import exceptions.BadVersionException;
import exceptions.ExecuteException;
import exceptions.Locomotion.UnableToMoveException;
import hook.HookFactory;
import pfg.config.Config;
import pfg.config.ConfigInfo;
import smartMath.Circle;
import smartMath.Vec2;
import strategie.GameState;
import utils.Log;

public class DeposeCubes extends AbstractScript {

    /**
     * Eléments appelés par la config
     */
    int d; //on pénètre la zone de construction de cette distance
    int dimensionporte;
    int distancepush;
    int radius;

    public DeposeCubes(Config config, Log log, HookFactory hookFactory) {
        super(config, log, hookFactory);
        versions = new Integer[]{0, 1};
        updateConfig();
    }

    /**
     * Cette méthode dépose les cubes pris par les deux bras
     *
     * @param stateToConsider
     * @throws ExecuteException
     * @throws UnableToMoveException
     */
    @Override
    public void execute(int version, GameState stateToConsider) throws ExecuteException, UnableToMoveException {
        Vec2 aim = new Vec2(750, 175 + radius);
        //on fait la même suite d'actions, mais pas au même endroit
        if (version == 0) {
            //On se tourne vers la zone de construction
            stateToConsider.robot.turn(Math.PI / 2);
            //On rentre dans la zone
            stateToConsider.robot.moveLengthwise(-d);
            stateToConsider.robot.setLocomotionSpeed(Speed.SLOW_ALL);
            //On ouvre la porte
            stateToConsider.robot.useActuator(ActuatorOrder.OUVRE_LA_PORTE_ARRIERE, true);
            //On recule de la largeur de la porte + de la longueur avancée dans la zone
            stateToConsider.robot.moveLengthwise(d + dimensionporte);
            //On ferme la porte
            stateToConsider.robot.setLocomotionSpeed(Speed.FAST_ALL);
            stateToConsider.robot.useActuator(ActuatorOrder.FERME_LA_PORTE_ARRIERE, false);
            stateToConsider.robot.turn(-Math.PI / 2);
            //stateToConsider.robot.moveLengthwise(-dimensionporte);
            stateToConsider.robot.setLocomotionSpeed(Speed.SLOW_ALL);
            //On avance de la dimension de la porte + de la distance poussée
            stateToConsider.robot.useActuator(ActuatorOrder.OUVRE_LA_PORTE_AVANT, true);
            stateToConsider.robot.moveLengthwise(d + dimensionporte + distancepush);
            stateToConsider.robot.moveLengthwise(-(dimensionporte + distancepush + d));
            stateToConsider.robot.useActuator(ActuatorOrder.FERME_LA_PORTE_AVANT, false);
        }
        //comme la version précédente mais l'accès à la zone est scripté
        else if (version == 1) {
            //On se tourne vers la zone de construction
            stateToConsider.robot.turn(Math.PI / 2);
            //On rentre dans la zone
            stateToConsider.robot.moveLengthwise(-d);
            stateToConsider.robot.setLocomotionSpeed(Speed.SLOW_ALL);
            //On ouvre la porte
            stateToConsider.robot.useActuator(ActuatorOrder.OUVRE_LA_PORTE_ARRIERE, true);
            //On recule de la largeur de la porte + de la longueur avancée dans la zone
            stateToConsider.robot.moveLengthwise(d + dimensionporte);
            //On ferme la porte
            stateToConsider.robot.setLocomotionSpeed(Speed.FAST_ALL);
            stateToConsider.robot.useActuator(ActuatorOrder.FERME_LA_PORTE_ARRIERE, false);
            stateToConsider.robot.turn(-Math.PI / 2);
            //stateToConsider.robot.moveLengthwise(-dimensionporte);
            stateToConsider.robot.setLocomotionSpeed(Speed.SLOW_ALL);
            //On avance de la dimension de la porte + de la distance poussée
            stateToConsider.robot.useActuator(ActuatorOrder.OUVRE_LA_PORTE_AVANT, true);
            stateToConsider.robot.moveLengthwise(d + dimensionporte + distancepush);
            stateToConsider.robot.moveLengthwise(-(dimensionporte + distancepush + d));
            stateToConsider.robot.useActuator(ActuatorOrder.FERME_LA_PORTE_AVANT, false);
        }
        stateToConsider.robot.setLocomotionSpeed(Speed.FAST_ALL);
        //les deux premières sont déposées
        stateToConsider.setTourAvantRemplie(false);
        stateToConsider.setTourArriereRemplie(false);
    }

    @Override
    public Circle entryPosition(int version, Vec2 robotPosition) throws BadVersionException {
        /*coordonnées de la zone de construction
               550<x<1070
                y=175
         */
        if (version == 0) {
            int xentry = 970;
            int yentry = 150 + radius;
            Vec2 position = new Vec2(xentry, yentry);
            return new Circle(position);
        }
        /*
        On va vers cette position en utilisant le pathfinding, apres on scripte l'acces a
        la zone de depose cubes
         */
        else if (version == 1) {
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
        d = config.getInt(ConfigInfoRobot.DISTANCE_PENETRATION_ZONE_DEPOSE_CUBES);
        dimensionporte = config.getInt(ConfigInfoRobot.DIMENSION_PORTES);
        distancepush = config.getInt(ConfigInfoRobot.DISTANCE_PUSH_DEPOSE_CUBES);
        radius = config.getInt(ConfigInfoRobot.ROBOT_RADIUS);
    }
}
