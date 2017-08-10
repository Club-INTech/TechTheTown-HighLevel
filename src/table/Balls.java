package table;

import smartMath.Vec2;

/**
 * Created by shininisan on 02.05.17.
 */
public class Balls extends GameElement{
    public boolean isStillThere;
    public Balls(Vec2 position)
    {
        super(position);
        this.isStillThere=true;
    }
}
