package tests;

import enums.ActuatorOrder;
import enums.ScriptNames;
import enums.Speed;
import graphics.Window;
import hook.HookFactory;
import org.junit.Before;
import org.junit.Test;
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
    private HookFactory hookFactory;

    @Before
    public void setUp() {
        try {
            super.setUp();
            robotReal = container.getService(Robot.class);
            state = container.getService(GameState.class);
            scriptManager = container.getService(ScriptManager.class);
            table=container.getService(Table.class);
            anInterface=container.getService(ThreadSimulator.class);
            container.startInstanciedThreads();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testScript() {
        Window window=new Window(table);
        try {
            //Définition des paramètres de base
            robotReal.setOrientation(Math.PI);
            Vec2 positionDepart = new Vec2(0, 1000);
            robotReal.setPosition(positionDepart);
            robotReal.setLocomotionSpeed(Speed.MEDIUM_ALL);
            window.setPoint(new Vec2(1500-210-10,2000-210-10));
           // scriptManager.getScript(ScriptNames.ACTIVE_ABEILLE).goToThenExec(0, state);
            //Vec2 positionarrivee=new Vec2(890,347);
            //robotReal.goTo(positionarrivee);

            //robotReal.goTo(positionDepart);
            //robotReal.turn(Math.PI/2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
