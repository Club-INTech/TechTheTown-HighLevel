package tests;

import enums.Speed;
import org.junit.Before;
import org.junit.Test;
import robot.Robot;
import scripts.ScriptManager;
import smartMath.Vec2;
import strategie.GameState;

public class JUnit_Depose_Cubes extends JUnit_Test{

    private Robot robotReal;
    private ScriptManager scriptManager;
    private GameState state;

    @Before
    public void setUp() {
        try {
            super.setUp();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void testScript() {
        try {
            robotReal = container.getService(Robot.class);
            container.startInstanciedThreads();

            robotReal.setLocomotionSpeed(Speed.ULTRA_SLOW_ALL);
            robotReal.moveLengthwise(10);

            Thread.sleep(500);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
