package patternRecognition.shootPicture;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ShootBufferedStillWebcam {

    public static BufferedImage takeBufferedPicture(){
        BufferedImage picture = null;
        shootPicture();
        try {
            picture = ImageIO.read(new File("/tmp/ImageRaspi.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("ShootBufferedStillWebcam > Erreur readingSavedPicture");
        }
        return picture;
    }


    private static void shootPicture(){
        String videoFileToRead="/dev/video0";

        List<String> command = new ArrayList<>();

        //Camera FishEye
        command.add("fswebcam");
        command.add("-s");
        command.add("brightness=60%");
        command.add("-s");
        command.add("contrast=50%");
        command.add("-s");
        command.add("saturation=100%");
        command.add("-r");
        command.add("1280x720");
        command.add("--scale");
        command.add("1280x720");
        command.add("--rotate");
        command.add("180");
        command.add("--no-banner");
        command.add("/tmp/ImageRaspi.jpg");

        /*
        //Caméra pourrav
        command.add("fswebcam");
        command.add("-p");
        command.add("YUYV");
        command.add("-r");
        command.add("640x480");
        command.add("--no-banner");
        //command.add("--list-controls");
        command.add("-d");
        command.add(videoFileToRead);
        System.out.println("Using "+videoFileToRead);
        command.add("/tmp/ImageRaspi.jpg");
        */
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
            System.out.println("ShootBufferedStillWebcam > Erreur processBuilder");
        }
        try {
            p.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("ShootBufferedStillWebcam > Erreur waitfor");
        }
        return;
    }
}
