package tests;

import enums.ActuatorOrder;
import enums.ScriptNames;
import enums.Speed;
import hook.HookFactory;
import org.junit.Before;
import org.junit.Test;
import robot.Robot;
import scripts.ScriptManager;
import smartMath.Vec2;
import strategie.GameState;
import table.Table;
import threads.ThreadInterface;

public class JUnit_ActivationPanneauDomotique extends JUnit_Test {
    private Robot robotReal;
    private ScriptManager scriptManager;
    private GameState state;
    private HookFactory hookFactory;
    private ThreadInterface anInterface;

    @Before
    public void setUp() {
        try {
            super.setUp();
            robotReal = container.getService(Robot.class);
            state=container.getService(GameState.class);
            scriptManager=container.getService(ScriptManager.class);
            anInterface = container.getService(ThreadInterface.class);
            container.startInstanciedThreads();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Test
    public void testScript() {
        try {
            //Définition des paramètres de base
            robotReal.setOrientation(Table.entryOrientation);
            robotReal.setPosition(Table.entryPosition);
            robotReal.setLocomotionSpeed(Speed.SLOW_ALL);
            scriptManager.getScript(ScriptNames.ACTIVATION_PANNEAU_DOMOTIQUE).goToThenExec(0,state);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
