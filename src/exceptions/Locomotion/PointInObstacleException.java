package exceptions.Locomotion;

import smartMath.Vec2;

/**
 * Created by shininisan on 08/12/16.
 */
public class PointInObstacleException extends Exception {
    private Vec2 point;
    public PointInObstacleException(Vec2 point)
    {
        super();
        this.point=point;

    }

    public Vec2 getPoint() {
        return point;
    }

    public void setPoint(Vec2 point) {
        this.point = point;
    }
}
