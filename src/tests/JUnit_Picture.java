package tests;

import image.analysis.PictureEnhancement;
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


            System.out.println(System.currentTimeMillis());
            BufferedImage convertedImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
            convertedImg.getGraphics().drawImage(img, 0, 0, null);
            System.out.println(System.currentTimeMillis());
            System.out.println(System.currentTimeMillis());
            System.out.println(System.currentTimeMillis());
            System.out.println(" ");

            PictureRGB rgb = new PictureRGB();
            rgb.setImage(convertedImg);
            System.out.println(System.currentTimeMillis());
            System.out.println(System.currentTimeMillis());
            System.out.println(System.currentTimeMillis());
            System.out.println(" ");

            Integer[] b = rgb.medianOverRectangle(400,50,200,200,false);
            System.out.println(b[0]+" "+b[1]+" "+b[2]);
            System.out.println(System.currentTimeMillis());
            System.out.println(System.currentTimeMillis());
            System.out.println(System.currentTimeMillis());
            System.out.println(" ");

            PictureRGB rgb2 = new PictureRGB();
            rgb2.setImage(rgb.getSubPicture(400,50,200,200,false));
            PictureHSB hsb = new PictureHSB();
            PictureHSB hsb2 = new PictureHSB();
            hsb.setImage(rgb2.getHSBImageArray());
            hsb2.setImage(rgb2.getHSBImageArray());
            PictureEnhancement.multiplyPictureThirdComponent(hsb,0.1);
            rgb.pasteArrayAtCoords(hsb.getRGBImageArray(),400,50);
            System.out.println(System.currentTimeMillis());
            System.out.println(System.currentTimeMillis());
            System.out.println(System.currentTimeMillis());
            System.out.println(" ");


            Integer[] c = rgb.medianOverRectangle(400,50,200,200,false);
            System.out.println(c[0]+" "+c[1]+" "+c[2]);
            System.out.println(System.currentTimeMillis());
            System.out.println(System.currentTimeMillis());
            System.out.println(System.currentTimeMillis());
            System.out.println(" ");
        }
    }
}
