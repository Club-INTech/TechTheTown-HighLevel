package tests;

import enums.ScriptNames;
import enums.Speed;
import hook.Hook;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import scripts.ScriptManager;
import strategie.GameState;
import table.Table;
import threads.dataHandlers.ThreadEvents;

import java.util.ArrayList;


/**
 * teste le dépot des boules
 * @author tic-tac
 *
 */
public class JUnit_DropBalls extends JUnit_Test
{
    private GameState mRobot;
    private ScriptManager scriptManager;

    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        log.debug("JUnit_DeplacementsTest.setUp()");
        mRobot = container.getService(GameState.class);
        //La position de depart est mise dans la Table
        mRobot.updateConfig();
        mRobot.robot.setPosition(Table.entryPosition);
        mRobot.robot.setOrientation(Math.PI);
        mRobot.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
        scriptManager = container.getService(ScriptManager.class);

        container.getService(ThreadEvents.class);
        container.startInstanciedThreads();

        scriptManager.getScript(ScriptNames.INITIALISE_ROBOT).goToThenExec(1, mRobot, new ArrayList<Hook>());
    }

    @Test
    public void dropThoseBalls()
    {
        ArrayList<Hook> emptyList = new ArrayList<Hook>();
        try
        {
            //On execute le script
            log.debug("Ramassage des balles");
            scriptManager.getScript(ScriptNames.DROP_BALLS).goToThenExec(0, mRobot, emptyList);
            returnToEntryPosition(mRobot);
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