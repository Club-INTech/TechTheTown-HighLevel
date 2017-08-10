package tests;

import enums.DirectionStrategy;
import enums.ScriptNames;
import enums.Speed;
import hook.Hook;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import scripts.ScriptManager;
import strategie.GameState;
import table.Table;
import threads.ThreadInterface;
import threads.ThreadTimer;
import threads.dataHandlers.ThreadEvents;
import threads.dataHandlers.ThreadSensor;

import java.util.ArrayList;

/**
 * Created by gaelle on 05/04/17.
 */
public class JUnit_Match_scripted extends JUnit_Test{
    private GameState mRobot;
    private ScriptManager scriptManager;

    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        log.debug("JUnit_DeplacementsTest.setUp()");
        mRobot = container.getService(GameState.class);

        // La position de depart est mise dans la Table (l'updtate config va la chercher)
        mRobot.updateConfig();
        mRobot.robot.setPosition(Table.entryPosition);

        mRobot.robot.setOrientation(-Math.PI/2);

        mRobot.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
        scriptManager = container.getService(ScriptManager.class);

        container.getService(ThreadEvents.class);
        container.getService(ThreadSensor.class);
        container.getService(ThreadInterface.class);
        container.getService(ThreadTimer.class);

        container.startInstanciedThreads();

        //scriptManager.getScript(ScriptNames.INITIALISE_ROBOT).goToThenExec(4, mRobot, new ArrayList<Hook>());

    }

    @Test
    public void matchScripted()
    {
        ArrayList<Hook> emptyList = new ArrayList<Hook>();
        try
        {
            // On execute le script
            log.debug("90 secondes pour faire des points Billy");

            mRobot.robot.setDirectionStrategy(DirectionStrategy.FASTEST);
            mRobot.robot.setOrientation(-Math.PI/2);
            scriptManager.getScript(ScriptNames.SCRIPTED_GO_TO).goToThenExec(0, mRobot, new ArrayList<Hook>());
/*
            scriptManager.getScript(ScriptNames.INITIALISE_ROBOT).goToThenExec(4, mRobot, emptyList);
            mRobot.robot.setDirectionStrategy(DirectionStrategy.FORCE_FORWARD_MOTION);
            scriptManager.getScript(ScriptNames.FULLSCRIPTED).goToThenExec(0, mRobot, emptyList);
*/
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

