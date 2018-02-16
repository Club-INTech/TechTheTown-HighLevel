package tests;

import enums.*;
import hook.HookFactory;
import org.junit.Before;
import org.junit.Test;
import robot.Robot;
import scripts.*;
import smartMath.Vec2;
import strategie.GameState;
import table.Table;

public class JUnit_TakeCubes extends JUnit_Test {

    private Robot robotReal;
    private ScriptManager scriptManager;
    private GameState state;
    private HookFactory hookFactory;

    @Before
    public void setUp() {
        try {
            super.setUp();
            robotReal = container.getService(Robot.class);
            scriptManager = container.getService(ScriptManager.class);
            state = container.getService(GameState.class);
            container.startInstanciedThreads();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void testScript() {
        try {
            robotReal.setOrientation(Math.PI);
            Vec2 positionDepart=new Vec2(900,850);
            robotReal.setPosition(positionDepart);
            robotReal.setLocomotionSpeed(Speed.ULTRA_SLOW_ALL);
            TakeCubes takeCubes = new TakeCubes(config,log,hookFactory);
            takeCubes.execute(TasCubes.TAS_STATION_EPURATION.getID(), state);
            takeCubes.execute(TasCubes.TAS_CHATEAU_EAU.getID(),state);
            DeposeCubes deposeCubes = new DeposeCubes(config, log, hookFactory);
            deposeCubes.execute(0,state);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
