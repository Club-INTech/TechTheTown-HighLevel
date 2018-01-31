package tests;

import enums.ActuatorOrder;
import enums.ConfigInfoRobot;
import enums.Speed;
import hook.HookFactory;
import org.junit.Before;
import org.junit.Test;
import robot.Robot;
import scripts.ScriptManager;
import scripts.TakeCubes;
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


            /*
            robotReal.setLocomotionSpeed(Speed.ULTRA_SLOW_ALL);
            robotReal.moveLengthwise(- 100);
            robotReal.turnRelatively(Math.PI/2);
            robotReal.moveLengthwise(100);
            robotReal.turnRelatively(Math.PI/3);
            */

            TakeCubes takeCubes = new TakeCubes(config,log,hookFactory);

            robotReal.setOrientation(0);
            /*state.robot.useActuator(ActuatorOrder.ACTIVE_ELECTROVANNE_AVANT,false);
            state.robot.useActuator(ActuatorOrder.ACTIVE_ELECTROVANNE_ARRIERE,false);
            state.robot.useActuator(ActuatorOrder.ACTIVE_LA_POMPE,true);
            takeCubes.takethiscube(state,"avant");
             state.robot.useActuator(ActuatorOrder.ACTIVE_ELECTROVANNE_ARRIERE,true);*/
            takeCubes.execute(2,state);
            /*int l=config.getInt(ConfigInfoRobot.LONGUEUR_CUBE);
            state.robot.moveLengthwise(-l);*/


            Thread.sleep(500);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
