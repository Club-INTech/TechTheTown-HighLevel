package image.patternRecognition;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UseWebcam {

    private static String pythonCommand = "python";

    public static BufferedImage takeBufferedPicture() {
        BufferedImage picture = null;

        //On dit au processus qui tourne de prendre la dernière frame qu'il a en tant que photo
        File taskFile = new File("/tmp/TakePicture.task");
        try {
            taskFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //On attend que la photo ait été bien enregistrée
        File doneFile = new File("/tmp/TakePicture.done");
        while (!doneFile.exists()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //On lit la photo enregistrée pour la transformer en BufferedImage
        try {
            picture = ImageIO.read(new File("/tmp/ImageRaspi.png"));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("UseWebcam > Erreur readingSavedPicture");
        }
        return picture;
    }

    public static void setPatternPositionWithVideo() {
        List<String> command = new ArrayList<>();

        command.add(pythonCommand);
        command.add("./src/image/setPatternPosition/SetPatternPositionGreen.py");

        //On crée le processus qui va lancer python pour set la position des patterns
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.inheritIO();
        Process p = null;

        //On lance le processus
        try {
            p = pb.start();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("UseWebcam > Erreur processBuilder");
        }

        //On attend que le processus termine
        try {
            p.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("UseWebcam > Erreur waitfor");
        }
    }

    public static void startCapturing() {
        File taskFile = new File("/tmp/TakePicture.task");
        File doneFile = new File("/tmp/TakePicture.done");
        if (taskFile.exists()){
            taskFile.delete();
        }
        if (doneFile.exists()){
            doneFile.delete();
        }

        List<String> command = new ArrayList<>();
        //Camera FishEye
        command.add(pythonCommand);
        command.add("./src/image/shootPicture/CaptureImage.py");

        //On crée le processus qui va lancer python pour ouvrir la caméra
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.inheritIO();


        //On lance ce processus
        try {
            pb.start();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("UseWebcam > Erreur processBuilder");
        }

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
