package tests;

import org.junit.Test;
import patternRecognition.UseWebcam;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class JUnit_ShootBufferedStillWebcam {

    @Test
    public void testTakePictureFULLKRAD(){
        BufferedImage buffImg= UseWebcam.takeBufferedPicture();
        try {
            ImageIO.write(buffImg, "jpg", new File("/tmp/retourImage.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("JUnit_testTakePictureFULLKRAD > problème retour image");
        }
    }

}

