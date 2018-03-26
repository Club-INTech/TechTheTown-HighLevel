package patternRecognition.shootPicture;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class UseWebcam {

    private static String pythonCommand="python3";

    public static BufferedImage takeBufferedPicture(){
        BufferedImage picture = null;
        shootPicture();
        try {
            picture = ImageIO.read(new File("/tmp/ImageRaspi.jpeg"));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("UseWebcam > Erreur readingSavedPicture");
        }
        return picture;
    }

    public static void setPatternPositionWithVideo(){
        List<String> command = new ArrayList<>();
        //Camera FishEye
        command.add(pythonCommand);
        command.add("./src/patternRecognition/shootPicture/SetPatternPosition.py");

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.inheritIO();
        Process p = null;
        try {
            p = pb.start();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("UseWebcam > Erreur processBuilder");
        }
        try {
            p.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("UseWebcam > Erreur waitfor");
        }
        return;
    }

    private static void shootPicture(){
        List<String> command = new ArrayList<>();
        //Camera FishEye
        command.add(pythonCommand);
        command.add("./src/patternRecognition/shootPicture/CaptureImage.py");

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.inheritIO();
        Process p = null;
        try {
            p = pb.start();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("UseWebcam > Erreur processBuilder");
        }
        try {
            p.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("UseWebcam > Erreur waitfor");
        }
        return;

        /*
        List<String> command = new ArrayList<>();
        //Camera FishEye
        command.add("streamer");
        command.add("-f");
        command.add("jpeg");
        command.add("-s");
        command.add("1280x720");
        command.add("-j");
        command.add("100");
        command.add("-o");
        command.add("/tmp/ImageRaspi.jpeg");

        //fswebcam
        //CameraFishEye
        command.add("fswebcam");
        command.add("-s");
        command.add("brightness=60%");
        command.add("-s");
        command.add("contrast=50%");
        command.add("-s");
        command.add("saturation=100%");
        command.add("-s");
        command.add("sharpness=6");
        command.add("-r");
        command.add("1280x720");
        command.add("--scale");
        command.add("1280x720");
        command.add("--rotate");
        command.add("180");
        command.add("--no-banner");
        command.add("/tmp/ImageRaspi.jpeg");



        //Caméra pourrav
        command.add("fswebcam");
        command.add("-p");
        command.add("YUYV");
        command.add("-r");
        command.add("640x480");
        command.add("--no-banner");
        command.add("-d");
        command.add(videoFileToRead);
        System.out.println("Using "+videoFileToRead);
        command.add("/tmp/ImageRaspi.jpeg");
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
            System.out.println("UseWebcam > Erreur processBuilder");
        }
        try {
            p.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("UseWebcam > Erreur waitfor");
        }
        */
    }
}
