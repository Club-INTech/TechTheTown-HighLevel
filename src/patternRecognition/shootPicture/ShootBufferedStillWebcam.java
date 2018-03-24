package patternRecognition.shootPicture;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamImageTransformer;
import com.github.sarxos.webcam.WebcamResolution;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ShootBufferedStillWebcam {

    public static BufferedImage takeBufferedPicture(){
        BufferedImage picture = null;
        shootPicture();
        try {
            picture = ImageIO.read(new File("/tmp/ImageRaspi.jpeg"));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("ShootBufferedStillWebcam > Erreur readingSavedPicture");
        }
        return picture;
    }

    private static void shootPicture(){
        Webcam webcam = Webcam.getDefault();
        webcam.setCustomViewSizes( WebcamResolution.HD.getSize());
        webcam.setViewSize(new Dimension(1280,720));
        webcam.setImageTransformer(new WebcamImageTransformer(){
            @Override
            public BufferedImage transform(BufferedImage input) {
                int width = input.getWidth();
                int height = input.getHeight();
                int[] inputPixelArray = new int[(width)*(height)*3];
                int[] outputPixelArray = new int[(width)*(height)*3];
                SampleModel inputSampleModel = input.getData().getSampleModel();
                input.getData().getPixels(0,0,width,height,inputPixelArray);

                for (int i=2; i<inputPixelArray.length; i+=3) {
                    float[] hsv = new float[3];
                    Color.RGBtoHSB(inputPixelArray[i-2], inputPixelArray[i-1], inputPixelArray[i], hsv);

                    hsv[1] *= 1;
                    if (hsv[1] > 1) {
                        hsv[1] = 1;
                    } else if (hsv[1] < 0) {
                        hsv[1] = 0;
                    }

                    hsv[2] *= 1;
                    if (hsv[2] > 1) {
                        hsv[2] = 1;
                    } else if (hsv[2] < 0) {
                        hsv[2] = 0;
                    }

                    int rgb = Color.HSBtoRGB(hsv[0],hsv[1],hsv[2]);
                    Color c = new Color(rgb);

                    int r = c.getRed();
                    int g = c.getGreen();
                    int b = c.getBlue();

                    outputPixelArray[i-2]=r;
                    outputPixelArray[i-1]=g;
                    outputPixelArray[i]=b;
                }

                BufferedImage output = new BufferedImage(width,height,1);
                WritableRaster newRaster= Raster.createWritableRaster(inputSampleModel,new Point(0,0));
                newRaster.setPixels(0,0,width,height,outputPixelArray);
                output.setData(newRaster);
                return output;
            }
        });
        webcam.open();
        try {
            ImageIO.write(webcam.getImage(), "JPEG", new File("/tmp/ImageRaspi.jpeg"));
        } catch (IOException e) {
            System.out.println("Cannot save picture to /tmp");
            e.printStackTrace();
        }
        webcam.close();

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
            System.out.println("ShootBufferedStillWebcam > Erreur processBuilder");
        }
        try {
            p.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("ShootBufferedStillWebcam > Erreur waitfor");
        }
        */
    }
}
