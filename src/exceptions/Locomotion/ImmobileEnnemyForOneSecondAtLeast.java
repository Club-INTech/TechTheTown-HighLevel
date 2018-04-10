package exceptions.Locomotion;

import smartMath.Vec2;

public class ImmobileEnnemyForOneSecondAtLeast extends Exception {

    Vec2 finalAim;
    public ImmobileEnnemyForOneSecondAtLeast(Vec2 aim) {
        super();
        this.finalAim=aim;
    }

    public Vec2 getAim() {
        return finalAim;
    }

    public void setAim(Vec2 aim) {
        this.finalAim = aim;
    }
}
