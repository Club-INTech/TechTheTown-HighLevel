package tests;

import org.junit.Before;
import org.junit.Test;
import imageAnalysis.PatternRecognition;
import imageAnalysis.UseWebcam;
import strategie.GameState;

import java.io.File;

public class JUnit_PatternRecognition extends JUnit_Test {

    private GameState state;
    private PatternRecognition patternRecognitionThread;
    private boolean noVideoInput;

    @Before
    public void setUp() {
        try {
            super.setUp();
            state = container.getService(GameState.class);
            patternRecognitionThread = container.getService(PatternRecognition.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.noVideoInput=true;
        for (int i=0; i<3; i++) {
            File f = new File("/dev/video"+i);
            if (f.exists()) {
                log.debug("/dev/video"+i+" exists");
                this.noVideoInput=false;
            }
            else{
                log.debug("/dev/video"+i+" does not exist");
            }
        }
    }

    @Test
    public void testSetPatternPosition() {
        if (!this.noVideoInput) {
            UseWebcam.setPatternPositionWithVideo();
        }
        else{
            log.critical("NoVideoInput");
        }
    }

    @Test
    public void testCaptureImage(){
        UseWebcam.startCapturing();
        if (!this.noVideoInput){
            UseWebcam.takeBufferedPicture();
        }
        else{
            log.critical("NoVideoInput");
        }
    }

    @Test
    public void testReconnaissanceWithSettingPositions(){
        //On set les positions de patterns
        if (!this.noVideoInput) {
            UseWebcam.setPatternPositionWithVideo();
            try {
                container.startInstanciedThreads();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        else{
            log.critical("NoVideoInput");
        }

        //On lance la reconnaissance de pattern
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