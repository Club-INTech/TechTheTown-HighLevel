package exceptions.Locomotion;

import smartMath.Vect;

public class ImmobileEnnemyForOneSecondAtLeast extends Exception {

    private Vect finalAim;
    public ImmobileEnnemyForOneSecondAtLeast(Vect aim) {
        super();
        this.finalAim=aim;
    }

    public Vect getAim() {
        return finalAim;
    }

    public void setAim(Vect aim) {
        this.finalAim = aim;
    }
}
