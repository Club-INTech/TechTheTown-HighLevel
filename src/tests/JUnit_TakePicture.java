package tests;

import org.junit.Test;
import patternRecognition.shootPicture.ShootStill;
import patternRecognition.shootPicture.ShootBufferedStill;

public class JUnit_TakePicture extends JUnit_Test {

    @Test
    public void testTakePicture(){
        patternRecognition.shootPicture.ShootStill.TakeBufferedPicture();
    }

    @Test
    public void testTakeBufferedPicture(){
        patternRecognition.shootPicture.ShootBufferedStill.TakeBufferedPicture();
    }
}
