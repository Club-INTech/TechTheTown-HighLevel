package tests;

import container.Container;
import exceptions.ContainerException;
import exceptions.Locomotion.ImmobileEnnemyForOneSecondAtLeast;
import exceptions.Locomotion.UnableToMoveException;
import graphics.Window;
import org.junit.Before;
import org.junit.Test;
import robot.Robot;
import scripts.ScriptManager;
import smartMath.Vec2;
import strategie.GameState;
import table.Table;
import threads.dataHandlers.ThreadLidar;
import utils.Log;

import java.io.File;
import java.io.IOException;

public class JUnit_Lidar extends JUnit_Test {

    /** Le Thread de récupération des données et de gestion du graphe */
    private ThreadLidar graphHandler;

    /** La Table */
    private Table table;

    /** Le descripteur du processus python */
    private Process process;
    private ProcessBuilder pBuilder;

    /** GameState & ScriptManager */
    private GameState gameState;
    private ScriptManager scriptManager;

    /** Méthode de mise à jour de la frame */
    private void show(Window frame) {
        try {
            while (true) {
                frame.repaint();
                Thread.sleep(20);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /** Méthode d'arrêt du mainThread */
    private void shutdown() {
        process.destroy();
    }
    private void removeObstacle() {
        while (true) {
            try {
                table.getObstacleManager().removeOutdatedObstacles();
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Before
    public void setUp() throws ContainerException, InterruptedException {
        container = new Container();
        log = container.getService(Log.class);
        config = container.getConfig();
        graphHandler = container.getService(ThreadLidar.class);
        table = container.getService(Table.class);
        gameState = container.getService(GameState.class);
        scriptManager = container.getService(ScriptManager.class);

        // Instanciation d'une classe ThreadInterface anonyme
        (new Thread(() -> this.show(new Window(table, gameState, scriptManager, false)))).start();

        // Démarrage du script du Lidar !
        pBuilder = new ProcessBuilder("python3", "main.py");
        pBuilder.directory(new File("../lidar"));
    }

    @Test
    public void testReceivedValue() {
        try {
            container.startInstanciedThreads();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> shutdown()));

            Thread.sleep(500);
            process = pBuilder.start();
            log.debug("Process python lancé");

            (new Thread(() -> this.removeObstacle())).start();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while (true);
    }

    @Test
    public void testLocomotionStop() {
        try {
            container.startInstanciedThreads();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> shutdown()));

            Thread.sleep(500);
            process = pBuilder.start();
            log.debug("Process python lancé");

            for (int i=0; i<5; i++) {
                gameState.robot.goTo(new Vec2(-200, 600));
                gameState.robot.goTo(new Vec2(-200, 1000));
                gameState.robot.goTo(new Vec2(200, 1000));
                gameState.robot.goTo(new Vec2(200, 600));
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UnableToMoveException e) {
            e.printStackTrace();
        } catch (ImmobileEnnemyForOneSecondAtLeast e) {
            e.printStackTrace();
        }
    }
}
