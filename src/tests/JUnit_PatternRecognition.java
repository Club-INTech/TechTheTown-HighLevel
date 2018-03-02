package tests;

import hook.HookFactory;
import org.junit.Before;
import org.junit.Test;
import patternRecognition.PatternRecognition;
import patternRecognition.shootPicture.ShootBufferedStill;
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
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void testReconnaissance(){
        //String pathToImage = "ImageRaspberryPi.png";
        String results="";
        int nbMinusOne=0;
        int nbSuccessful=0;
        for (int i=1; i<=500; i++) {
            results+=i+"\t:\t";
            System.out.println("Image "+i);
            String pathToImage = "500ImagesTest/Image"+i+".png";
            int[] zoneToPerformLocalisation = {0, 0, 0, 0};
            PatternRecognition patternRecognitionThread = new PatternRecognition(config, robotReal.getEthWrapper());
            patternRecognitionThread.setDebugPatternRecognition(false);
            log.debug("Starting PatternRecognition thread...");
            patternRecognitionThread.start();

            int victoryPattern = -2;
            while (victoryPattern == -2) {
                victoryPattern = patternRecognitionThread.returnFinalIndice();
            }
            if (victoryPattern==-1){
                nbMinusOne+=1;
            }
            else if ((i/50)==victoryPattern){
                nbSuccessful+=1;
            }
            results+=victoryPattern+"\n";
            patternRecognitionThread.shutdown();
            log.debug("Pattern found : " + victoryPattern);
        }
        System.out.println(results);
        System.out.println("Nombre de -1 : "+nbMinusOne);
        System.out.println("Nombre de réussites : "+nbSuccessful);
    }

    @Test
    public void test() {
        try {
            int[] zoneToPerformLocalisation = {0, 0, 0, 0};
            PatternRecognition patternRecognitionThread = new PatternRecognition(config,robotReal.getEthWrapper());
            patternRecognitionThread.start();
            int finalindice = patternRecognitionThread.returnFinalIndice();
            while (finalindice == -2) {
                finalindice = patternRecognitionThread.returnFinalIndice();
                patternRecognitionThread.sleep(100);
            }
            System.out.println(finalindice);
        }
        catch(InterruptedException e){
            e.printStackTrace();
        }
    }

}
