package tests;

import enums.ScriptNames;
import enums.TasCubes;
import hook.HookFactory;
import org.junit.Before;
import org.junit.Test;
import pathfinder.Pathfinding;
import robot.Robot;
import scripts.ScriptManager;
import smartMath.Vect;
import smartMath.VectCart;
import strategie.GameState;
import java.util.ArrayList;

public class JUnit_TakeCubes extends JUnit_Test {

    private Robot robotReal;
    private ScriptManager scriptManager;
    private GameState state;
    private HookFactory hookFactory;
    private Pathfinding pathfinding;

    @Before
    public void setUp() {
        try {
            super.setUp();
            robotReal = container.getService(Robot.class);
            scriptManager = container.getService(ScriptManager.class);
            state = container.getService(GameState.class);
            container.startInstanciedThreads();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testScript() {
        try {
            //Définition des paramètres de départ du robot
            robotReal.setOrientation(Math.PI);
            Vect positionDepart = new VectCart(1252, 455);
            robotReal.setPosition(positionDepart);
            //robotReal.setLocomotionSpeed(Speed.ULTRA_SLOW_ALL);

            //goToThenExec
            scriptManager.getScript(ScriptNames.TAKE_CUBES).goToThenExec(TasCubes.TAS_CHATEAU_EAU.getID(), state);

            //retour du robot
            pathfinding = container.getService(Pathfinding.class);
            ArrayList<Vect> pathToFollow = new ArrayList<>();
            pathToFollow = pathfinding.findmyway(robotReal.getPosition(), new VectCart(1222, 455));
            robotReal.followPath(pathToFollow);
            robotReal.turn(Math.PI);
            robotReal.moveLengthwise(30);

            //scriptManager.getScript(ScriptNames.DEPOSE_CUBES).goToThenExec(0,state);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
