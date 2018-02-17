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
        int xentry=650;
        int yentry=165+config.getInt(ConfigInfoRobot.ROBOT_RADIUS);
        Vec2 aim=new Vec2(xentry,yentry);
        stateToConsider.robot.setLocomotionSpeed(Speed.SLOW_ALL);
        stateToConsider.robot.goTo(aim,false,false);
        int d=70; //on pénètre la zone de construction de cette distance
        int dimensionporte=config.getInt(ConfigInfoRobot.DIMENSION_PORTES);
        int distancepush=105;
        if(version==0) {
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
            //On se tourne vers la droite
            stateToConsider.robot.turn(Math.PI);

            //Première tour déposée
            //On dépose la deuxième tour

            //On recule (on va vers la base) pour éviter de redéposer au même endroit
            stateToConsider.robot.moveLengthwise(-300);
            //On se tourne vers la zone de construction
            stateToConsider.robot.turn(Math.PI / 2);
            //On fait la même chose qu'avant, mais pour la porte arrière
            stateToConsider.robot.moveLengthwise(-d);
            stateToConsider.robot.useActuator(ActuatorOrder.OUVRE_LA_PORTE_ARRIERE, true);
            stateToConsider.robot.moveLengthwise(d + dimensionporte);
            stateToConsider.robot.useActuator(ActuatorOrder.FERME_LA_PORTE_ARRIERE, true);
            stateToConsider.robot.moveLengthwise(-dimensionporte-distancepush);
            stateToConsider.robot.moveLengthwise(distancepush);
        }
        if(version==1){
            //IDEM QUE VERSION 0, MAIS SANS POUSSER
            stateToConsider.robot.turn(-Math.PI / 2);
            stateToConsider.robot.moveLengthwise(d);
            stateToConsider.robot.useActuator(ActuatorOrder.OUVRE_LA_PORTE_AVANT, true);
            stateToConsider.robot.moveLengthwise(-d - dimensionporte);
            stateToConsider.robot.useActuator(ActuatorOrder.FERME_LA_PORTE_AVANT, true);
            stateToConsider.robot.turn(Math.PI);
            //on dépose la deuxième tour là
            stateToConsider.robot.moveLengthwise(-300);
            stateToConsider.robot.turn(Math.PI / 2);
            stateToConsider.robot.moveLengthwise(-d);
            stateToConsider.robot.useActuator(ActuatorOrder.OUVRE_LA_PORTE_ARRIERE, true);
            stateToConsider.robot.moveLengthwise(d + dimensionporte);
            stateToConsider.robot.useActuator(ActuatorOrder.FERME_LA_PORTE_ARRIERE, true);
        }
    }

    @Override
    public Circle entryPosition(int version, Vec2 robotPosition) throws BadVersionException {
        /*coordonnées de la zone de construction
               550<x<1070
                y=175
         */
        int xentry=650;
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
