package tests;

import enums.*;
import hook.HookFactory;
import org.junit.Before;
import org.junit.Test;
import robot.Robot;
import scripts.*;
import smartMath.Vec2;
import strategie.GameState;

public class JUnit_TakeCubes extends JUnit_Test {

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
            //ThreadSensor threadSensor=container.getService(ThreadSensor.class);
            //ThreadInterface threadInterface = container.getService(ThreadInterface.class);
            container.startInstanciedThreads();

            robotReal.setOrientation(Math.PI);
            Vec2 positionDepart=new Vec2(900,850);
            robotReal.setPosition(positionDepart);
            robotReal.setLocomotionSpeed(Speed.ULTRA_SLOW_ALL);
            TakeCubes takeCubes = new TakeCubes(config,log,hookFactory);
            takeCubes.execute(2, TasCubes.TAS_STATION_EPURATION, BrasUtilise.AVANT, Cubes.NULL, state);
            takeCubes.execute(2,TasCubes.TAS_CHATEAU_EAU,BrasUtilise.ARRIERE,Cubes.NULL,state);
            DeposeCubes deposeCubes = new DeposeCubes(config, log, hookFactory);
            deposeCubes.execute(state,true);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
