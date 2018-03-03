package tests;

import enums.ActuatorOrder;
import enums.ScriptNames;
import enums.Speed;
import hook.HookFactory;
import org.junit.Before;
import org.junit.Test;
import patternRecognition.PatternRecognition;
import robot.Robot;
import scripts.ScriptManager;
import strategie.GameState;
import table.Table;
import threads.ThreadInterface;

public class JUnit_MatchScript extends JUnit_Test {

    private Robot robotReal;
    private ScriptManager scriptManager;
    private GameState state;
    private HookFactory hookFactory;
    private PatternRecognition patternRecognitionThread;

    @Before
    public void setUp() {
        try {
            super.setUp();
            robotReal = container.getService(Robot.class);
            state = container.getService(GameState.class);
            scriptManager = container.getService(ScriptManager.class);
            ThreadInterface threadInterface = container.getService(ThreadInterface.class);
            PatternRecognition patternRecognitonth=container.getService(PatternRecognition.class);
            container.startInstanciedThreads();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testScript() {
        try {
            //Définition des paramètres de base
//            robotReal.setOrientation(Math.PI);
//            Vec2 positionDepart = new Vec2(1252, 455);
//            robotReal.setPosition(positionDepart);
            //robotReal.useActuator(ActuatorOrder.SEND_POSITION,true);
            robotReal.setPosition(Table.entryPosition);
            robotReal.setOrientation(Table.entryOrientation);
            robotReal.setLocomotionSpeed(Speed.MEDIUM_ALL);
            //robotReal.useActuator(ActuatorOrder.SEND_POSITION,true);
            scriptManager.getScript(ScriptNames.MATCH_SCRIPT).goToThenExec(0, state);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
