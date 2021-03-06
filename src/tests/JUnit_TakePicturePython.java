package tests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import patternRecognition.UseWebcam;


public class JUnit_TakePicturePython extends JUnit_Test {

    @Before
    public void setUp(){
        try{
            super.setUp();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test() throws IOException {
        UseWebcam.takeBufferedPicture();
    }

    @Test
    public void testVideo(){
        List<String> command = new ArrayList<>();
        //Camera FishEye
        command.add("python3");
        command.add("./src/patternRecognition/shootPicture/SetPatternPositionGreen.py");

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.inheritIO();
        Process p = null;
        try {
            p = pb.start();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("UseWebcam > Erreur processBuilder");
        }
        try {
            p.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("UseWebcam > Erreur waitfor");
        }

        System.out.println("Fin de la prise de photo");
        return;
    }
}