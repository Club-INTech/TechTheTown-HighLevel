package tests;

import org.junit.Assert;
import org.junit.Test;
import smartMath.Circle;
import smartMath.Geometry;
import smartMath.Segment;
import smartMath.Vec2;

public class JUnit_Calculs {

    @Test
    public void test(){
        double orientation=-Math.PI*2;
        while (orientation<Math.PI*2+1) {
            System.out.println("o : "+orientation);
            System.out.println("r1: "+Geometry.moduloSpec(orientation - Math.PI, Math.PI));
            System.out.println("r2: "+Geometry.moduloSpec(orientation + Math.PI, Math.PI));
            System.out.println(" ");
            orientation+=Math.PI/4;
        }
    }

    @Test
    public void testIntersect() {
        Circle c1 = new Circle(new Vec2(400, 400), 100);
        Circle c2 = new Circle(new Vec2(-400, 500), 100);
        Segment s1 = new Segment(new Vec2(50, 50), new Vec2(600, 590));

        Assert.assertTrue(Geometry.intersects(s1, c1));
        Assert.assertFalse(Geometry.intersects(s1, c2));
    }

}
