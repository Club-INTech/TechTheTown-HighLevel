package tests;

import hook.HookFactory;
import org.junit.Before;
import org.junit.Test;
import patternRecognition.PatternRecognition;
import robot.Locomotion;
import robot.Robot;
import scripts.ScriptManager;
import simulator.ThreadSimulator;
import simulator.ThreadSimulatorMotion;
import strategie.GameState;
import threads.ThreadInterface;

import java.io.File;

public class JUnit_PatternRecognition extends JUnit_Test {

    private Robot robotReal;
    private ScriptManager scriptManager;
    private GameState state;
    private HookFactory hookFactory;
    private ThreadInterface anInterface;
    private PatternRecognition patternRecognitionThread;
    private Locomotion locomotion;

    @Before
    public void setUp() {
        try {
            super.setUp();
            //robotReal = container.getService(Robot.class);
            //state = container.getService(GameState.class);
            //scriptManager = container.getService(ScriptManager.class);
            patternRecognitionThread = container.getService(PatternRecognition.class);
            //anInterface = container.getService(ThreadInterface.class);
            //locomotion=container.getService(Locomotion.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test() {
        boolean noVideoInput=true;
        for (int i=0; i<5; i++) {
            File f = new File("/dev/video"+i);
            if (f.exists()) {
                log.debug("/dev/video"+i+" exists");
                noVideoInput=false;
            }
            else{
                log.debug("/dev/video"+i+" does not exist");
            }
        }
        if (!noVideoInput) {
            try {
                container.startInstanciedThreads();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        while (true){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
                log.critical("Thread cannot sleep");
            }
        }
    }
}