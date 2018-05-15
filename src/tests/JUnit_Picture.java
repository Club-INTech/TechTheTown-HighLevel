package tests;

import image.analysis.PictureHSB;
import image.analysis.PictureRGB;
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


            PictureRGB rgb = new PictureRGB();
            rgb.setImage(convertedImg);
            Integer[] b;
            b = rgb.medianOverRectangle(400,50,20,20,false);
            System.out.println(b[0]+" "+b[1]+" "+b[2]);

            PictureHSB hsb = new PictureHSB();
            hsb.setImage(rgb.getHSBImageArray());
            Float[] c;
            c = hsb.medianOverRectangle(400,50,20,20,true);
            c = hsb.averageOverRectangle(400,50,20,20,true);
            c = hsb.medianOverCircle(400,50,20,true);
            c = hsb.averageOverCircle(400,50,20,true);
            System.out.println(c[0]+" "+c[1]+" "+c[2]);
        }
    }
}
