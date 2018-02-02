package tests;

import enums.ActuatorOrder;
import enums.ConfigInfoRobot;
import enums.Speed;
import hook.HookFactory;
import org.junit.Before;
import org.junit.Test;
import robot.Robot;
import scripts.DeposeCubes;
import scripts.ScriptManager;
import scripts.TakeCubes;
import smartMath.Vec2;
import strategie.GameState;
import table.Table;
import threads.ThreadInterface;
import threads.dataHandlers.ThreadSensor;

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
            ThreadSensor threadSensor=container.getService(ThreadSensor.class);
            ThreadInterface threadInterface = container.getService(ThreadInterface.class);
            container.startInstanciedThreads();

            /*
            robotReal.setLocomotionSpeed(Speed.ULTRA_SLOW_ALL);
            robotReal.moveLengthwise(- 100);
            robotReal.turnRelatively(Math.PI/2);
            robotReal.moveLengthwise(100);
            robotReal.turnRelatively(Math.PI/3);
            */
            robotReal.setOrientation(Math.PI);
            Vec2 positionentree=new Vec2(1340,470);
            robotReal.setPosition(positionentree);
            robotReal.moveLengthwise(100);
            robotReal.turnRelatively(-Math.PI/4);
            robotReal.moveLengthwise(200);
            TakeCubes takeCubes = new TakeCubes(config,log,hookFactory);
            takeCubes.goToThenExec(2,state);
            robotReal.moveLengthwise(-250);
            robotReal.turnRelatively(-Math.PI/2);
            robotReal.moveLengthwise(500);
            //DeposeCubes

            /*state.robot.useActuator(ActuatorOrder.ACTIVE_ELECTROVANNE_ARRIERE,false);
            state.robot.useActuator(ActuatorOrder.ACTIVE_ELECTROVANNE_AVANT,false);
            state.robot.useActuator(ActuatorOrder.ACTIVE_LA_POMPE,true);
            takeCubes.takethiscube(state,"avant");
            state.robot.moveLengthwise(58);
            takeCubes.takethiscube(state,"avant");*/
             //state.robot.useActuator(ActuatorOrder.ACTIVE_ELECTROVANNE_ARRIERE,true);
         //   takeCubes.execute(1,state);
            /*int l=config.getInt(ConfigInfoRobot.LONGUEUR_CUBE);
            state.robot.moveLengthwise(-l);*/
            DeposeCubes deposeCubes = new DeposeCubes(config,log,hookFactory);
            deposeCubes.goToThenExec(0,state);
            //deposeCubes.execute(0,state);
            Thread.sleep(500);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
