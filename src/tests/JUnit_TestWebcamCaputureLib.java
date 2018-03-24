package tests;

import java.io.IOException;

import org.junit.Test;


public class JUnit_TestWebcamCaputureLib  extends JUnit_Test {

    @Test
    public void test() throws IOException {
        patternRecognition.shootPicture.ShootBufferedStillWebcam.takeBufferedPicture();
    }
}