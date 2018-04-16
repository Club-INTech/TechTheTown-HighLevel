package tests;

import enums.ActuatorOrder;
import enums.ScriptNames;
import enums.Speed;
import graphics.Window;
import hook.HookFactory;
import hook.HookNames;
import org.junit.Before;
import org.junit.Test;
import robot.Locomotion;
import robot.Robot;
import scripts.ScriptManager;
import simulator.ThreadSimulator;
import smartMath.Vec2;
import strategie.GameState;
import table.Table;

public class JUnit_ActiveAbeille extends JUnit_Test {
    private Robot robotReal;
    private ScriptManager scriptManager;
    private GameState state;
    private ThreadSimulator anInterface;
    private Table table;
    private Locomotion locomotion;
    private HookFactory hookFactory;

    @Before
    public void setUp() {
        try {
            super.setUp();
            robotReal = container.getService(Robot.class);
            state = container.getService(GameState.class);
            scriptManager = container.getService(ScriptManager.class);
            anInterface=container.getService(ThreadSimulator.class);
            locomotion=container.getService(Locomotion.class);
            hookFactory=container.getService(HookFactory.class);
            container.startInstanciedThreads();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testScript() {
        try {
            //Définition des paramètres de base
            robotReal.setOrientation(Table.entryOrientation);
            robotReal.setPosition(Table.entryPosition);
            robotReal.setLocomotionSpeed(Speed.MEDIUM_ALL);
            hookFactory.configureHook(HookNames.ACTIVE_BRAS_AVANT_ABEILLE,HookNames.ACTIVE_BRAS_ARRIERE_ABEILLE);
            scriptManager.getScript(ScriptNames.ACTIVE_ABEILLE).goToThenExec(0, state);
            //Vec2 positionarrivee=new Vec2(890,347);
            //robotReal.goTo(positionarrivee);

            //robotReal.goTo(positionDepart);
            //robotReal.turn(Math.PI/2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
