package tests;

import enums.Speed;
import hook.HookFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.JUnitSystem;
import robot.Robot;
import scripts.ScriptManager;
import smartMath.Vec2;
import strategie.GameState;
import table.Table;

public class JUnit_Sensor extends JUnit_Test {

    private Table table;
    private Robot robotReal;
    private ScriptManager scriptManager;
    private GameState state;
    private HookFactory hookFactory;


    @Before
    public void setUp() {
        try {
            super.setUp();
            robotReal = container.getService(Robot.class);
            scriptManager = container.getService(ScriptManager.class);
            state = container.getService(GameState.class);
            table = container.getService(Table.class);
            container.startInstanciedThreads();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSensor() {
        try {
            robotReal = container.getService(Robot.class);
            robotReal.getPosition();
            robotReal.getOrientation();

            robotReal.setLocomotionSpeed(Speed.SLOW_ALL);
            robotReal.moveLengthwise(100);
            robotReal.moveLengthwise(-100);
            robotReal.setOrientation(Math.PI/2);
            robotReal.setPosition(new Vec2(0, 500));



            Thread.sleep(500);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
