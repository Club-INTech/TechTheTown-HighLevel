package tests;

import enums.ActuatorOrder;
import enums.Speed;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import scripts.ScriptManager;
import strategie.GameState;
import table.Table;
import tests.JUnit_Test;

/**
 * Created by tic-tac on 06/01/17.
 */
public class JUnit_TestLength extends JUnit_Test
{
    private GameState mRobot;
    private ScriptManager scriptManager;

    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        log.debug("JUnit_DeplacementsTest.setUp()");
        mRobot = container.getService(GameState.class);

        //La position de depart est mise dans le updateConfig() //TODO
        mRobot.updateConfig();
        mRobot.robot.setPosition(Table.entryPosition);
        mRobot.robot.setOrientation(0);
        mRobot.robot.setLocomotionSpeed(Speed.SLOW_ALL);
        scriptManager = container.getService(ScriptManager.class);
        mRobot.robot.useActuator(ActuatorOrder.REPLIER_PELLETEUSE, true);

        //container.getService(ServiceNames.THREAD_INTERFACE);
        //container.startInstanciedThreads();
    }

    @Test
    public  void testlength() {
        try {


            mRobot.robot.moveLengthwise(200);
        } catch (Exception e) {
            log.debug("suce");
        }
    }

    @After
    public void finish()
    {
        mRobot.robot.immobilise();
    }
}
