package tests;

import enums.DirectionStrategy;
import enums.ScriptNames;
import enums.Speed;
import hook.Hook;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import scripts.ScriptManager;
import smartMath.Vec2;
import strategie.GameState;
import threads.dataHandlers.ThreadEvents;
import threads.dataHandlers.ThreadSensor;
import utils.Sleep;

import java.util.ArrayList;


/**
 * teste le ramassage des balles par la version 0 du script
 * @author gaelle
 *
 */
public class JUnit_CatchBalls extends JUnit_Test
{
    private GameState mRobot;
    private ScriptManager scriptManager;
    private ArrayList<Hook> listHook = new ArrayList<Hook>();

    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        log.debug("JUnit_DeplacementsTest.setUp()");
        mRobot = container.getService(GameState.class);
        //La position de depart est mise dans la Table (l'updtate config va la chercher)
        mRobot.updateConfig();
        mRobot.robot.setPosition(mRobot.table.entryPosition);
        mRobot.robot.setOrientation(Math.PI);
        mRobot.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
        scriptManager = container.getService(ScriptManager.class);

        container.getService(ThreadSensor.class);

        container.startInstanciedThreads();
    }

    @Test
    public void catchThoseBalls()
    {
        try
        {
            //On execute le script
            log.debug("Ramassage des balles");
            mRobot.robot.switchSensor();
            mRobot.robot.catchBalls();
            mRobot.robot.switchSensor();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    @After
    public void finish()
    {
        mRobot.robot.immobilise();
    }
}