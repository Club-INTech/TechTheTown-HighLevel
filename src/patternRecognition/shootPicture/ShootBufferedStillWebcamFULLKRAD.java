package patternRecognition.shootPicture;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ShootBufferedStillWebcamFULLKRAD {

    public static BufferedImage takeBufferedPicture(){
        BufferedImage picture = null;
        shootPicture();
        try {
            picture = ImageIO.read(new File("/tmp/ImageRaspi.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("ShootBufferedStillWebcamFULLKRAD > Erreur readingSavedPicture");
        }
        return picture;
    }


    private static File shootPicture(){
        List<String> command = new ArrayList<>();
        command.add("fswebcam");
        command.add("-p");
        command.add("YUYV");
        command.add("-r");
        command.add("640x480");
        command.add("--no-banner");
        command.add("/tmp/ImageRaspi.jpg");
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.inheritIO();

// 		System.out.println("Executed this command:\n\t" + command.toString());
// 		pb.redirectErrorStream(true);
// 		pb.redirectOutput(
// 				new File(System.getProperty("user.home") + File.separator +
// 						"Desktop" + File.separator + "RPiCamera.RPiCamera.out"));

        Process p = null;
        try {
            p = pb.start();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("ShootBufferedStillWebcamFULLKRAD > Erreur processBuilder");
        }
        try {
            p.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("ShootBufferedStillWebcamFULLKRAD > Erreur waitfor");
        }
        return new File("/tmp/Image.jpg");
    }
}
