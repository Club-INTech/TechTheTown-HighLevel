package scripts;

import enums.ActuatorOrder;
import enums.ConfigInfoRobot;
import enums.Speed;
import exceptions.BadVersionException;
import exceptions.ExecuteException;
import exceptions.Locomotion.UnableToMoveException;
import hook.HookFactory;
import pfg.config.Config;
import smartMath.Circle;
import smartMath.Vec2;
import strategie.GameState;
import utils.Log;

public class DeposeCubes extends AbstractScript{

    public DeposeCubes(Config config, Log log, HookFactory hookFactory){
        super(config, log, hookFactory);
        versions= new Integer[]{0,1};
    }

    /**
     * Cette méthode dépose les cubes pris par les deux bras
     * @param stateToConsider
     * @throws ExecuteException
     * @throws UnableToMoveException
     */
    @Override
    public void execute(int version,GameState stateToConsider) throws ExecuteException, UnableToMoveException {
        int d=70; //on pénètre la zone de construction de cette distance
        int dimensionporte=config.getInt(ConfigInfoRobot.DIMENSION_PORTES);
        int distancepush=105;
        //on fait la même suite d'actions, mais pas au même endroit
        if(version==0 || version==1) {
            //On se tourne vers la zone de construction
            stateToConsider.robot.turn(-Math.PI / 2);
            //On rentre dans la zone
            stateToConsider.robot.moveLengthwise(d);
            //On ouvre la porte
            stateToConsider.robot.useActuator(ActuatorOrder.OUVRE_LA_PORTE_AVANT, true);
            //On recule de la largeur de la porte + de la longueur avancée dans la zone
            stateToConsider.robot.moveLengthwise(-d - dimensionporte);
            //On ferme la porte
            stateToConsider.robot.useActuator(ActuatorOrder.FERME_LA_PORTE_AVANT, true);
            //On avance de la cimension de la porte + de la distance poussée
            stateToConsider.robot.moveLengthwise(dimensionporte+distancepush);
            //On recule de la distance de poussée
            stateToConsider.robot.moveLengthwise(-distancepush);
            //on tourne pour déposer la deuxième tour
            stateToConsider.robot.turn(Math.PI/2);
            //on refait la même chose mais avec la porte arrière
            stateToConsider.robot.moveLengthwise(-d);
            stateToConsider.robot.useActuator(ActuatorOrder.OUVRE_LA_PORTE_ARRIERE, true);
            stateToConsider.robot.moveLengthwise(d + dimensionporte);
            stateToConsider.robot.useActuator(ActuatorOrder.FERME_LA_PORTE_ARRIERE, true);
            stateToConsider.robot.moveLengthwise(-dimensionporte-distancepush);
            stateToConsider.robot.moveLengthwise(distancepush);
        }
        //TODO: Trouver une bonne position d'entrée

    }

    @Override
    public Circle entryPosition(int version, Vec2 robotPosition) throws BadVersionException {
        /*coordonnées de la zone de construction
               550<x<1070
                y=175
         */
        int xentry=970;
        int yentry=175+config.getInt(ConfigInfoRobot.ROBOT_RADIUS);
        Vec2 position=new Vec2(xentry,yentry);
        return new Circle(position);
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


}
