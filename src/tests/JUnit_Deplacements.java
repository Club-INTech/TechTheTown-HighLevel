package tests;

import enums.Speed;
import exceptions.Locomotion.ImmobileEnnemyForOneSecondAtLeast;
import exceptions.Locomotion.UnableToMoveException;
import graphics.Window;
import org.junit.Before;
import org.junit.Test;
import robot.EthWrapper;
import robot.Locomotion;
import robot.Robot;
import smartMath.Vec2;
import strategie.GameState;
import table.Table;
import threads.ThreadTimer;

public class JUnit_Deplacements extends JUnit_Test {

    private Robot robot;
    private GameState state;
    private Table table;
    private Locomotion locomotion;
    private EthWrapper mEthWrapper;

    @Before
    public void setUp() {
        try {
            super.setUp();
            robot = container.getService(Robot.class);
            state = container.getService(GameState.class);
            locomotion = container.getService(Locomotion.class);
            mEthWrapper = container.getService(EthWrapper.class);
            table = container.getService(Table.class);
            container.getService(ThreadTimer.class);
            container.startInstanciedThreads();
            robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
            robot.setPosition(new Vec2(0,1000));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testTranslation() {
        try {
            for (int i=0; i<10; i++){
                robot.moveLengthwise(500);
                robot.moveLengthwise(-500);
                Thread.sleep(100);
            }
        } catch (UnableToMoveException e) {
            e.printStackTrace();
        } catch (ImmobileEnnemyForOneSecondAtLeast immobileEnnemyForOneSecondAtLeast) {
            immobileEnnemyForOneSecondAtLeast.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
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
