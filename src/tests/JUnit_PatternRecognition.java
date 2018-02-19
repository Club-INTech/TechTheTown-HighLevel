package tests;


import hook.HookFactory;
import org.junit.Before;
import org.junit.Test;
import patternRecognition.PatternRecognition;
import robot.Robot;
import scripts.ScriptManager;
import strategie.GameState;

public class JUnit_PatternRecognition extends JUnit_Test {

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
    public void testReconnaissance(){
        //String pathToImage = "ImageRaspberryPi.png";
        for (int i=1; i<=500; i++) {
            String pathToImage = "500ImagesTest/Image"+i+".png";
            int[] zoneToPerformLocalisation = {0, 0, 0, 0};
            PatternRecognition patternRecognitionThread = new PatternRecognition(config, pathToImage, zoneToPerformLocalisation);
            boolean debug = false;
            patternRecognitionThread.setDebugPatternRecognition(debug);
            log.debug("Starting PatternRecognition thread...");
            patternRecognitionThread.start();

            int victoryPattern = -2;
            while (victoryPattern == -2) {
                victoryPattern = patternRecognitionThread.returnFinalIndice();
            }
            patternRecognitionThread.shutdown();
            log.debug("Pattern found : " + victoryPattern);
        }
    }
}
