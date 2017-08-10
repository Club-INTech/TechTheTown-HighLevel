package tests;

import enums.DirectionStrategy;
import enums.ScriptNames;
import enums.Speed;
import enums.UnableToMoveReason;
import exceptions.Locomotion.UnableToMoveException;
import hook.Hook;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import scripts.ScriptManager;
import strategie.GameState;
import strategie.IA;
import table.Table;
import threads.ThreadInterface;
import threads.dataHandlers.ThreadEvents;
import threads.dataHandlers.ThreadSensor;

import java.util.ArrayList;

/**
 * Created by shininisan on 02.05.17.
 */
public class JUnit_MatchIA extends JUnit_Test {
    private GameState mRobot;
    private ScriptManager scriptManager;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        log.debug("JUnit_DeplacementsTest.setUp()");
        mRobot = container.getService(GameState.class);

        //La position de depart est mise dans la Table (l'updtate config va la chercher)
        mRobot.updateConfig();
        mRobot.robot.setPosition(Table.entryPosition);

        mRobot.robot.setOrientation(-Math.PI / 2);

        mRobot.robot.setLocomotionSpeed(Speed.SLOW_ALL);
        scriptManager = container.getService(ScriptManager.class);

        container.getService(ThreadEvents.class);
        container.getService(ThreadSensor.class);
        container.getService(ThreadInterface.class);
        container.startInstanciedThreads();
        mRobot.robot.switchSensor();

        scriptManager.getScript(ScriptNames.INITIALISE_ROBOT).goToThenExec(4, mRobot, new ArrayList<Hook>());
    }

    @Test
    public void catchThoseBalls() {

        ArrayList<Hook> emptyList = new ArrayList<Hook>();
        try
        {
            //On execute le script de match scripté

            mRobot.robot.setDirectionStrategy(DirectionStrategy.FORCE_FORWARD_MOTION);
            scriptManager.getScript(ScriptNames.FULLSCRIPTED).goToThenExec(0, mRobot, new ArrayList<Hook>());
        }
        catch(UnableToMoveException e)
        {
            if(e.reason== UnableToMoveReason.OBSTACLE_DETECTED) // on drop l'action
            {
                if(mRobot.robot.getPosition().getY()>1400)// you're fucked
                {

                }
            }
            if(e.reason==UnableToMoveReason.PHYSICALLY_BLOCKED)
            {

            }
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