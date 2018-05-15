package tests;

import imageAnalysis.Picture;
import imageAnalysis.PictureRGB;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class JUnit_Picture extends JUnit_Test{

    @Test
    public void pictureTest() {
        String pathToImage="./images/RobotCities_2018.png";
        File f = new File(pathToImage);
        if (f.exists()){
            BufferedImage img;
            try {
                img = ImageIO.read(f);
            } catch (IOException e) {
                img=null;
                e.printStackTrace();
            }
            BufferedImage convertedImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
            convertedImg.getGraphics().drawImage(img, 0, 0, null);


            Picture a = new PictureRGB();
            a.setImage(convertedImg);
            Object[] b;
            b = a.medianOverCircle(849,599,50,true);
            System.out.println(b[0]+" "+b[1]+" "+b[2]);
        }
    }
}
