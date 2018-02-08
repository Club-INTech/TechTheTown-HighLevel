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
    }

    /**
     * Cette méthode dépose les cubes pris par les deux bras
     * @param stateToConsider
     * @param pousse : on est obligé de pousser les deux premières tours car on n'aura
     *               pas la place où déposer les deux dernières, par conséquent, on active
     *               cette boolean pour les deux premières tours
     * @throws ExecuteException
     * @throws UnableToMoveException
     */

    public void execute(GameState stateToConsider,Boolean pousse) throws ExecuteException, UnableToMoveException {
        int d=70; //on pénètre la zone de construction de cette distance
        int dimensionporte=config.getInt(ConfigInfoRobot.DIMENSION_PORTES);
        int distancepush=105;
        stateToConsider.robot.turn(Math.PI/2);
        stateToConsider.robot.moveLengthwise(d);
        stateToConsider.robot.useActuator(ActuatorOrder.OUVRE_LA_PORTE_AVANT,true);
        stateToConsider.robot.moveLengthwise(-d-dimensionporte);
        stateToConsider.robot.useActuator(ActuatorOrder.FERME_LA_PORTE_AVANT,true);
        if(pousse){
            stateToConsider.robot.moveLengthwise(dimensionporte);
            stateToConsider.robot.moveLengthwise(distancepush);
            stateToConsider.robot.moveLengthwise(-distancepush);
        }
        stateToConsider.robot.turn(Math.PI);
        //on dépose la deuxième tour là
        stateToConsider.robot.moveLengthwise(350);
        stateToConsider.robot.turn(Math.PI/2);
        stateToConsider.robot.moveLengthwise(d);
        stateToConsider.robot.useActuator(ActuatorOrder.OUVRE_LA_PORTE_AVANT,true);
        stateToConsider.robot.moveLengthwise(-d-dimensionporte);
        stateToConsider.robot.useActuator(ActuatorOrder.FERME_LA_PORTE_AVANT,true);
        if(pousse){
            stateToConsider.robot.moveLengthwise(dimensionporte);
            stateToConsider.robot.moveLengthwise(distancepush);
            stateToConsider.robot.moveLengthwise(-distancepush);
        }
    }


    public Circle entryPosition(int ray, Vec2 robotPosition) throws BadVersionException {
        /*coordonnées de la zone de construction
               550<x<1070
                y=175
         */
        int xentry=650;
        int yentry=175;
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
    @Override
    public Integer[][] getVersion2(GameState stateToConsider) {
        return new Integer[][]{};
    }

}
