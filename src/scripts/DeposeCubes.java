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

    public void execute(int versionToExecute, GameState stateToConsider,Boolean pousse) throws ExecuteException, UnableToMoveException {
        /* d est la distance avec laquelle on recule : on recule d'une distance au moins égale
        à la dimension de la porte pour pouvoir la fermer à nouveau
         */
        int l= config.getInt(ConfigInfoRobot.LONGUEUR_CUBE);
        int d=950;
        int d2=20; //c'est la même distance que d dans entryPosition
        int d3=2*l;//mesure à faire pour savoir exactement
        stateToConsider.robot.turn(-Math.PI/2);
        //la version 0 et 1 corresondent au cas ou on dépose les deux tours
        if (versionToExecute==0 || versionToExecute==1) {
            stateToConsider.robot.useActuator(ActuatorOrder.OUVRE_LA_PORTE_AVANT,true);
            stateToConsider.robot.moveLengthwise(-d-d2);
            stateToConsider.robot.useActuator(ActuatorOrder.FERME_LA_PORTE_AVANT,true);
            stateToConsider.robot.turn(0);
            stateToConsider.robot.moveLengthwise(d3);
            stateToConsider.robot.turn(Math.PI/2);
            stateToConsider.robot.useActuator(ActuatorOrder.OUVRE_LA_PORTE_ARRIERE,true);
            stateToConsider.robot.moveLengthwise(-d-d2);
            stateToConsider.robot.useActuator(ActuatorOrder.FERME_LA_PORTE_ARRIERE,true);
        }
        //on dépose la troisième tour au cas ou on arrive pas à prendre la 4ème
        if(versionToExecute==3){
            stateToConsider.robot.useActuator(ActuatorOrder.OUVRE_LA_PORTE_AVANT,true);
            stateToConsider.robot.moveLengthwise(-d-d2);
            stateToConsider.robot.useActuator(ActuatorOrder.FERME_LA_PORTE_AVANT,true);
        }
    }
    public Circle entryPosition(int ray, Vec2 robotPosition) throws BadVersionException {
        /*coordonnées de la zone de construction
               550<x<1070
                y=175
         */
        int yconstructionzone=175;
        int d=70; //on pénètre la zone de construction de cette distance
        int dimensionport

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
