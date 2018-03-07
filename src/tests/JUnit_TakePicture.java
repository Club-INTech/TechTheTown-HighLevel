package tests;

import org.junit.Test;
import patternRecognition.shootPicture.piCam.ShootBufferedStill;
import patternRecognition.shootPicture.piCam.ShootStill;

public class JUnit_TakePicture extends JUnit_Test {

    @Test
    public void testTakePicture(){
        ShootStill.TakePicture();
    }

    @Test
    public void testTakeBufferedPicture(){
        ShootBufferedStill.TakeBufferedPicture();
    }
}
