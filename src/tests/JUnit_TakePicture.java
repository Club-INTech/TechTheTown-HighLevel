package tests;

import org.junit.Test;

public class JUnit_TakePicture extends JUnit_Test {

    @Test
    public void testTakePicture(){
        patternRecognition.shootPicture.ShootStill.TakePicture();
    }

    @Test
    public void testTakeBufferedPicture(){
        patternRecognition.shootPicture.ShootBufferedStill.TakeBufferedPicture();
    }
}
