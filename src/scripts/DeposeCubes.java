package scripts;

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
         *  on va prendre les six, et du coup, six positions d'entrées donc six versions*/
        versions = new Integer[]{0,1,2,3,4,5};
    }
    @Override
    public void execute(int versionToExecute, GameState stateToConsider) throws ExecuteException, UnableToMoveException {
        // d est la distance avec laquelle on recule (mesures à effectuer)
        int d=120;
        stateToConsider.robot.turn(-Math.PI/2);
        if (versionToExecute==0 ||versionToExecute==1 || versionToExecute==2 ||versionToExecute==3
                || versionToExecute==4 ||versionToExecute==5) {
            stateToConsider.robot.moveLengthwise(-d);
        }
    }
    public Circle entryPosition(int version, int ray, Vec2 robotPosition) throws BadVersionException {
        int r = config.getInt(ConfigInfoRobot.ROBOT_RADIUS);
        int cte=150;
        int dimporte=60;
        /**mesures à effectuer pour yEntry*/
            if (version == 0) {
                int xEntry=630;
                int yEntry=r+cte+dimporte;
                Vec2 position = new Vec2(xEntry, yEntry);
                return new Circle(position);
            }
            else {
                if (version == 1) {
                    int xEntry=698 ;
                    int yEntry=r+cte+dimporte ;
                    Vec2 position = new Vec2(xEntry, yEntry);
                    return new Circle(position);
                } else {
                    if (version == 2) {
                        int xEntry=766 ;
                        int yEntry=r+cte+dimporte ;
                        Vec2 position = new Vec2(xEntry, yEntry);
                        return new Circle(position);
                    } else {
                        if (version == 3) {
                            int xEntry=834 ;
                            int yEntry=r+cte+dimporte;
                            Vec2 position = new Vec2(xEntry, yEntry);
                            return new Circle(position);
                        } else {
                            if (version == 4) {
                                int xEntry=902 ;
                                int yEntry=r+cte+dimporte ;
                                Vec2 position = new Vec2(xEntry, yEntry);
                                return new Circle(position);
                            } else {
                                if (version == 5) {
                                    int xEntry=970 ;
                                    int yEntry=r+cte+dimporte ;
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
