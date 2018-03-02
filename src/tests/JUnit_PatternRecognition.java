package tests;

import hook.HookFactory;
import org.junit.Before;
import org.junit.Test;
import patternRecognition.PatternRecognition;
import patternRecognition.shootPicture.ShootBufferedStill;
import robot.Robot;
import scripts.ScriptManager;
import simulator.ThreadSimulator;
import strategie.GameState;

public class JUnit_PatternRecognition extends JUnit_Test {

    private Robot robotReal;
    private ScriptManager scriptManager;
    private GameState state;
    private HookFactory hookFactory;
    private ThreadSimulator simulator;

    @Before
    public void setUp() {
        try {
            super.setUp();
            robotReal = container.getService(Robot.class);
            state = container.getService(GameState.class);
            scriptManager=container.getService(ScriptManager.class);
            simulator=container.getService(ThreadSimulator.class);
            container.startInstanciedThreads();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void testReconnaissance(){
        String results="";
        int nbMinusOne=0;
        int nbSuccessful=0;
        for (int i=1; i<=500; i++) {
            results+=i+"\t:\t";
            System.out.println("Image "+i);
            PatternRecognition patternRecognitionThread = new PatternRecognition(config, robotReal.getEthWrapper(), this.state);
            patternRecognitionThread.setDebugPatternRecognition(false);
            log.debug("Starting PatternRecognition thread...");
            patternRecognitionThread.start();

            int victoryPattern = -2;
            while (victoryPattern == -2) {
                victoryPattern = patternRecognitionThread.getFinalIndice();
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
            PatternRecognition patternRecognitionThread = new PatternRecognition(config, robotReal.getEthWrapper(), this.state);
            patternRecognitionThread.start();
            int finalindice = patternRecognitionThread.getFinalIndice();
            while (finalindice == -2) {
                finalindice = patternRecognitionThread.getFinalIndice();
                patternRecognitionThread.sleep(100);
            }
            System.out.println(finalindice);
        }
        catch(InterruptedException e){
            e.printStackTrace();
        }
    }

}
