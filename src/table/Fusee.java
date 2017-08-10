package table;

import enums.ColorModule;
import smartMath.Vec2;

/**
 * Created by shininisan on 02.05.17.
 */
public class Fusee extends GameElement{
    private int nombreCylindre;
    private ColorModule color;
    public Fusee(Vec2 position,ColorModule color)
    {
        super(position);
        nombreCylindre=4;
        this.color=color;
    }
}
