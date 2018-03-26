package tests;

import hook.HookFactory;
import org.junit.Before;
import org.junit.Test;
import patternRecognition.PatternRecognition;
import robot.Locomotion;
import robot.Robot;
import scripts.ScriptManager;
import strategie.GameState;
import threads.ThreadInterface;

import java.io.File;

public class JUnit_PatternRecognition extends JUnit_Test {

    private GameState state;
    private PatternRecognition patternRecognitionThread;

    @Before
    public void setUp() {
        try {
            super.setUp();
            state = container.getService(GameState.class);
            patternRecognitionThread = container.getService(PatternRecognition.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test() {
        boolean noVideoInput=true;
        for (int i=0; i<3; i++) {
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
            patternRecognition.shootPicture.UseWebcam.setPatternPositionWithVideo();
            try {
                container.startInstanciedThreads();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        else{
            log.critical("NoVideoInput");
        }



        while (!state.isRecognitionDone()) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
                log.critical("Thread cannot sleep");
            }
        }
    }
}