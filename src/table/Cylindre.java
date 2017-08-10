package table;

import enums.ColorModule;
import smartMath.Vec2;

/**
 * Created by shininisan on 02.05.17.
 */
public class Cylindre extends GameElement {

    public boolean isStillThere;
    private ColorModule color;
    public Cylindre(Vec2 positiondebut,ColorModule color)
    {
        super(positiondebut);
        this.color=color;
        this.isStillThere=true;
    }
}
