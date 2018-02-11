package tests;


import org.junit.Test;
import patternRecognition.PatternRecognition;

public class JUnit_PatternRecognition extends JUnit_Test {

    @Test
    public void testReconnaissance(){
        String pathToImage = "ImageRaspberryPi.png";
        boolean debug=true;
        int[] zoneToPerformLocalisation={0,700,800,800};
        PatternRecognition patternRecognitionThread = new PatternRecognition(pathToImage, zoneToPerformLocalisation);
        patternRecognitionThread.setDebugPatternRecognition(debug);
        log.debug("Starting PatternRecognition thread...");
        patternRecognitionThread.start();

        int victoryPattern=-2;
        while (victoryPattern==-2) {
            victoryPattern = patternRecognitionThread.returnFinalIndice();
        }
        patternRecognitionThread.shutdown();
        log.debug("Pattern found : "+victoryPattern);
    }
}
