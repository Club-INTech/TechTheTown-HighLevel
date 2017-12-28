package scripts;

import enums.ActuatorOrder;
import enums.ConfigInfoRobot;
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
        /**6  versions pour les 6 blocs de cubes :
         *  quand on va prendre 4 cubes (pour l'instant 4 le 5 ème à venir ^^) , ilfaudra déposer la
         *  tour construite à partir d'un bloc dans la zone de construction, comme on est très forts,
         *  on va prendre les six, et du coup, six positions d'entrées donc six versions, pour les
         *  trois premières verisons, on ne fait qu'ouvrir la porte et reculer, pour les trois autres
         *  versions, il faudra pousser la tour de cubes, et puis ouvrir la porte et reculer*/
        versions = new Integer[]{0,1,2,3,4,5};
    }
    @Override
    public void execute(int versionToExecute, GameState stateToConsider) throws ExecuteException, UnableToMoveException {
        // d est la distance avec laquelle on recule (mesures à effectuer)
        int d=950;
        stateToConsider.robot.turn(-Math.PI/2);
        if (versionToExecute==0 ||versionToExecute==1 || versionToExecute==2 ||versionToExecute==3
                || versionToExecute==4 ||versionToExecute==5) {
            stateToConsider.robot.useActuator(ActuatorOrder.OUVRE_LA_PORTE,true);
            stateToConsider.robot.moveLengthwise(-d);
        }
    }
    public Circle entryPosition(int version, int ray, Vec2 robotPosition) throws BadVersionException {
        int r = config.getInt(ConfigInfoRobot.ROBOT_RADIUS);
        int yconstructionzone=150;
        int d=20; //distance à mesurer pour pénétrer dans la zone de construction (c'est plus beau)
        /**mesures à effectuer pour yconstructionzone*/
            if (version == 0 || version==3)  {
                int xEntry=630;
                int yEntry=r+yconstructionzone+d;
                Vec2 position = new Vec2(xEntry, yEntry);
                return new Circle(position);
            }
            else {
                if (version == 1 || version==4) {
                    int xEntry=630+r ;
                    int yEntry=r+yconstructionzone+d ;
                    Vec2 position = new Vec2(xEntry, yEntry);
                    return new Circle(position);
                } else {
                    if (version == 2 || version==5) {
                        int xEntry=630+2*r;
                        int yEntry=r+yconstructionzone+d ;
                        Vec2 position = new Vec2(xEntry, yEntry);
                        return new Circle(position);
                    }
                    else{
                        log.critical("Version invalide");
                        throw new BadVersionException();
                    }
                }
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
    public Integer[][] getVersion2(GameState stateToConsider) {
        return new Integer[][]{};
    }

}
