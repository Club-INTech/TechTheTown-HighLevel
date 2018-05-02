package tests;

import enums.Speed;
import exceptions.Locomotion.ImmobileEnnemyForOneSecondAtLeast;
import exceptions.Locomotion.UnableToMoveException;
import org.junit.Before;
import org.junit.Test;
import robot.Robot;
import strategie.GameState;

public class JUnit_Deplacements extends JUnit_Test {

    private Robot robot;
    private GameState state;

    @Before
    public void setUp() {
        try {
            super.setUp();
            robot = container.getService(Robot.class);
            state = container.getService(GameState.class);
            container.startInstanciedThreads();
            robot.setLocomotionSpeed(Speed.DEFAULT_SPEED);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testTranslation() {
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
    @Test
    public void testRotation() {
        try {
            for (int i=0; i<100; i++){
                robot.turn(4*Math.PI/10);
                robot.turn(-4*Math.PI/10);
            }
        } catch (UnableToMoveException e) {
            e.printStackTrace();
        } catch (ImmobileEnnemyForOneSecondAtLeast immobileEnnemyForOneSecondAtLeast) {
            immobileEnnemyForOneSecondAtLeast.printStackTrace();
        }
    }
}
