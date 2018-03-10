package tests;

import org.junit.Test;
import smartMath.Geometry;

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


}
