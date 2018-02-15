package tests;


import org.junit.Test;
import patternRecognition.PatternRecognition;

public class JUnit_PatternRecognition extends JUnit_Test {

    @Test
    public void testReconnaissance(){
        //String pathToImage = "ImageRaspberryPi.png";
        String pathToImage = "testLitDown.jpg";
        int[] zoneToPerformLocalisation={0,800,700,800};
        PatternRecognition patternRecognitionThread = new PatternRecognition(pathToImage, zoneToPerformLocalisation);
        boolean debug=true;
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
