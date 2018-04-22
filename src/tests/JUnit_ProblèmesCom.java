package tests;

import exceptions.Locomotion.ImmobileEnnemyForOneSecondAtLeast;
import exceptions.Locomotion.UnableToMoveException;
import org.junit.Before;
import org.junit.Test;
import robot.Robot;
import strategie.GameState;

public class JUnit_ProblèmesCom extends JUnit_Test {

    private Robot robot;
    private GameState state;

    @Before
    public void setUp() {
        try {
            super.setUp();
            robot = container.getService(Robot.class);
            state = container.getService(GameState.class);
            container.startInstanciedThreads();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testScript() {
        try {
            for (int i=0; i<100; i++){
                robot.moveLengthwise(500);
                robot.moveLengthwise(-500);
            }
        } catch (UnableToMoveException e) {
            e.printStackTrace();
        } catch (ImmobileEnnemyForOneSecondAtLeast immobileEnnemyForOneSecondAtLeast) {
            immobileEnnemyForOneSecondAtLeast.printStackTrace();
        }
    }
}
