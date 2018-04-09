package exceptions.Locomotion;

import smartMath.Vec2;

public class ImmobileEnnemyForOneSecondAtLeast extends Exception {

    Vec2 aim;
    public ImmobileEnnemyForOneSecondAtLeast(Vec2 aim) {
        super();
        this.aim=aim;
    }

    public Vec2 getAim() {
        return aim;
    }

    public void setAim(Vec2 aim) {
        this.aim = aim;
    }
}
