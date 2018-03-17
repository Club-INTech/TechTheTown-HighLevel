package tests;

import enums.ActuatorOrder;
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
import threads.dataHandlers.ThreadSensor;

public class JUnit_Sensor extends JUnit_Test {

    private Table table;
    private Robot robotReal;
    private ScriptManager scriptManager;
    private GameState state;
    private HookFactory hookFactory;
    private ThreadSensor threadSensor;


    @Before
    public void setUp() {
        try {
            super.setUp();
            robotReal = container.getService(Robot.class);
            scriptManager = container.getService(ScriptManager.class);
            state = container.getService(GameState.class);
            table = container.getService(Table.class);
            threadSensor=container.getService(ThreadSensor.class);
            container.startInstanciedThreads();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSensor() {
        try {
            while (true) {
                robotReal.useActuator(ActuatorOrder.SEND_POSITION, true);
                robotReal.switchSensor();
                String distanceDetected = "";
                for (int i = 0; i < 4; i++) {
                    distanceDetected += i + ":" + threadSensor.getSensor(i).getDetectedDistance() + " ";
                }
                System.out.println(distanceDetected);
            }
            /*while(true){
                robotReal.getPosition();
                robotReal.getOrientation();
                robotReal.useActuator(ActuatorOrder.SEND_POSITION,true);
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
