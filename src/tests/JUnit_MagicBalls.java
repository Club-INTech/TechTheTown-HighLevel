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
import threads.dataHandlers.ThreadEvents;
import threads.dataHandlers.ThreadSensor;

import java.util.ArrayList;

/**
 * Tests les 2 scripts l'un après l'autre ! (c'est plus propre que d'appeler les 2 JUnits l'un après l'autre)
 * @autor Rem, Ug
 */
public class JUnit_MagicBalls extends JUnit_Test {
    private GameState mRobot;
    private ScriptManager scriptManager;

    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        log.debug("JUnit_DeplacementsTest.setUp()");
        mRobot = container.getService(GameState.class);



        //La position de depart est mise dans la Table (l'updtate config va la chercher)
        mRobot.updateConfig();
        mRobot.robot.setPosition(Table.entryPosition);

        mRobot.robot.setOrientation(Math.PI); //(position départ 615,203)

        mRobot.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
        scriptManager = container.getService(ScriptManager.class);

        container.getService(ThreadEvents.class);
        container.getService(ThreadSensor.class);
        container.getService(ThreadInterface.class);
        container.startInstanciedThreads();


        scriptManager.getScript(ScriptNames.INITIALISE_ROBOT).goToThenExec(1, mRobot, new ArrayList<Hook>());
    }

    @Test
    public void catchThoseBalls()
    {
        ArrayList<Hook> emptyList = new ArrayList<Hook>();
        try
        {
            //On execute le script
            log.debug("Ramassage des balles");
            mRobot.robot.setDirectionStrategy(DirectionStrategy.FASTEST);
            scriptManager.getScript(ScriptNames.CATCH_BALLS).goToThenExec(1, mRobot, emptyList);
            mRobot.robot.setDirectionStrategy(DirectionStrategy.FASTEST);

            scriptManager.getScript(ScriptNames.DROP_BALLS).goToThenExec(2, mRobot, emptyList);
            mRobot.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
            //scriptManager.getScript(ScriptNames.CATCH_BALLS).goToThenExec(2, mRobot,emptyList);
            //scriptManager.getScript(ScriptNames.DROP_BALLS).goToThenExec(1, mRobot, emptyList);

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
