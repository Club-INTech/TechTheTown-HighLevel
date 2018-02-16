package tests;

import enums.ActuatorOrder;
import enums.Speed;
import hook.HookFactory;
import org.junit.Before;
import org.junit.Test;
import robot.Robot;
import scripts.DeposeCubes;
import scripts.ScriptManager;
import smartMath.Vec2;
import strategie.GameState;
import tests.container.A;

public class JUnit_Depose_Cubes extends JUnit_Test{

    private Robot robotReal;
    private ScriptManager scriptManager;
    private GameState state;
    private HookFactory hookFactory;

    @Before
    public void setUp() {
        try {
            super.setUp();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void testScript() {
        try {
            robotReal = container.getService(Robot.class);
            state=container.getService(GameState.class);
            container.startInstanciedThreads();
            robotReal.setOrientation(Math.PI);
            Vec2 positionDepart=new Vec2(900,850);
            robotReal.setPosition(positionDepart);
            DeposeCubes deposeCubes=new DeposeCubes(config,log,hookFactory);
            deposeCubes.execute(state,false);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
