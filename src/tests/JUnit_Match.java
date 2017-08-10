package tests;

import enums.ActuatorOrder;
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
import threads.dataHandlers.ThreadEvents;

import java.util.ArrayList;

/**
 * Tentative de match
 * @autor Gaelle
 */
public class JUnit_Match extends JUnit_Test {
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
        mRobot.robot.setOrientation(-Math.PI/2);
        mRobot.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
        scriptManager = container.getService(ScriptManager.class);

        container.getService(ThreadEvents.class);
        //container.getService(ThreadSensor.class);
        //container.getService(ThreadInterface.class);
        container.startInstanciedThreads();
        //mRobot.robot.switchSensor();

        //départ en arrière
        scriptManager.getScript(ScriptNames.INITIALISE_ROBOT).goToThenExec(4, mRobot, new ArrayList<Hook>());
    }

    @Test
    public void makeThesePoints()
    {
        ArrayList<Hook> emptyList = new ArrayList<Hook>();
        try
        {
            //On execute le script
            log.debug("Aller Billy, t'as 90 secondes, fait des points; ou prend l'autre robot et enculez-vous en cercle, m'en bat les couilles frère.");
            mRobot.robot.setDirectionStrategy(DirectionStrategy.FASTEST);


            //Module cratère fond, et balles, plus drop module
            scriptManager.getScript(ScriptNames.CATCH_BALLS).goToThenExec(2, mRobot, emptyList);

            //drop les deux autres modules
            scriptManager.getScript(ScriptNames.CATCH_BALLS).goToThenExec(3, mRobot, emptyList);

            mRobot.robot.moveLengthwise(-100);
            mRobot.robot.turn(-Math.PI/2);
            mRobot.robot.moveLengthwise(50);

            //drop balls
            //abaisser les bras au plus bas
            mRobot.robot.useActuator(ActuatorOrder.DEPLOYER_PELLETEUSE, true);

            //rotation de la pelle jusqu'à la position de livraison
            mRobot.robot.useActuator(ActuatorOrder.LIVRE_PELLE, true);

            //lever les bras jusqu'à la position intermédiaire
            mRobot.robot.useActuator(ActuatorOrder.MED_PELLETEUSE, true);

            //tourner la pelle jusqu'à la position initiale
            mRobot.robot.useActuator(ActuatorOrder.PRET_PELLE, true);

            //monter les bras le plus haut \o/
            mRobot.robot.useActuator(ActuatorOrder.REPLIER_PELLETEUSE, true);

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
