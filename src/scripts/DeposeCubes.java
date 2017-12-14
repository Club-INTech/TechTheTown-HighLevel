package scripts;

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
        versions = new Integer[]{0,1,2,3,4,5};
    }
    @Override
    public void execute(int versionToExecute, GameState stateToConsider) throws ExecuteException, UnableToMoveException {
    }
    public Circle entryPosition(int version, int ray, Vec2 robotPosition) throws BadVersionException {
        try {
            if (version == 0) {
                int xEntry =;
                int yEntry =;
                Vec2 position = new Vec2(xEntry, yEntry);
                return new Circle(position);
            } else {
                if (version == 1) {
                    int xEntry = ;
                    int yEntry =;
                    Vec2 position = new Vec2(xEntry, yEntry);
                    return new Circle(position);
                } else {
                    if (version == 2) {
                        int xEntry = ;
                        int yEntry = ;
                        Vec2 position = new Vec2(xEntry, yEntry);
                        return new Circle(position);
                    } else {
                        if (version == 3) {
                            int xEntry =;
                            int yEntry = ;
                            Vec2 position = new Vec2(xEntry, yEntry);
                            return new Circle(position);
                        } else {
                            if (version == 4) {
                                int xEntry = ;
                                int yEntry = ;
                                Vec2 position = new Vec2(xEntry, yEntry);
                                return new Circle(position);
                            } else {
                                if (version == 5) {
                                    int xEntry =;
                                    int yEntry =;
                                    Vec2 position = new Vec2(xEntry, yEntry);
                                    return new Circle(position);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.critical("Version invalide");
        }
    }
}
