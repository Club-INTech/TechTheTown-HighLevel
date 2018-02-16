package tests;

import enums.ScriptNames;
import enums.Speed;
import hook.HookFactory;
import org.junit.Before;
import org.junit.Test;
import robot.Robot;
import scripts.ScriptManager;
import smartMath.Vec2;
import strategie.GameState;

public class JUnit_Depose_Cubes extends JUnit_Test{

    private Robot robotReal;
    private ScriptManager scriptManager;
    private GameState state;
    private HookFactory hookFactory;

    @Before
    public void setUp() {
        try {
            super.setUp();
            robotReal = container.getService(Robot.class);
            state=container.getService(GameState.class);
            container.startInstanciedThreads();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void testScript() {
        try {
            //Définition des paramètres de base
            robotReal.setOrientation(Math.PI);
            Vec2 positionDepart=new Vec2(900,850);
            robotReal.setPosition(positionDepart);
            robotReal.setLocomotionSpeed(Speed.SLOW_ALL);

            //goToThenExec
            scriptManager.getScript(ScriptNames.DEPOSE_CUBES).goToThenExec(0,state);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
