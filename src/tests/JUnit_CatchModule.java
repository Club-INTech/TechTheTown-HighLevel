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
 * Test du script CatchModule : Attention aux valeurs d'orientation du robot dans le setup, dépendant de la version
 * testée
 * @author Rem
 */
public class JUnit_CatchModule extends JUnit_Test {

    private GameState mRobot;
    private ScriptManager scriptManager;

    @Before
    public void setUp() throws Exception
    {
        // Initialisation du robot pendant le script: s'il y a plusieurs tests dans le m�me JUnit, le setup n'est executé
        // qu'une fois.
        super.setUp();
        log.debug("JUnit_DeplacementsTest.setUp()");
        mRobot = container.getService(GameState.class);

        // La position de depart est mise dans la Table (l'updtate config va la chercher)
        mRobot.updateConfig();
        mRobot.robot.setPosition(Table.entryPosition);
        mRobot.robot.setOrientation(0);
        // Vitesse à calibrer en fonction du taffe du BL :p
        mRobot.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
        scriptManager = container.getService(ScriptManager.class);

        container.getService(ThreadEvents.class);
        container.startInstanciedThreads();

        //scriptManager.getScript(ScriptNames.INITIALISE_ROBOT).goToThenExec(0, mRobot, new ArrayList<Hook>());
    }

    @Test
    public void catchThoseBalls()
    {
        ArrayList<Hook> emptyList = new ArrayList<Hook>();
        try
        {
            //On execute le script
            int version = 0;
            log.debug("Ramassage des modules");
            scriptManager.getScript(ScriptNames.CATCH_MODULE).goToThenExec(version, mRobot, emptyList);
            if (version != 0) {
                returnToEntryPosition(mRobot);
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
