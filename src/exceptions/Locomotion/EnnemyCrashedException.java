package exceptions.Locomotion;

import enums.UnableToMoveReason;
import smartMath.Vec2;

/**
 * Created by rem on 5/16/17.
 */
public class EnnemyCrashedException extends UnableToMoveException {

    public EnnemyCrashedException(Vec2 aim, UnableToMoveReason reason){
        super(aim, reason);
    }
}
