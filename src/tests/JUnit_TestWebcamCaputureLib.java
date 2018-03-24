package tests;

import java.awt.*;
import java.io.IOException;

import au.edu.jcu.v4l4j.V4L4JConstants;
import com.github.sarxos.v4l4j.V4L4J;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamDriver;
import com.github.sarxos.webcam.WebcamPanel;
import org.junit.Test;

import javax.swing.*;


public class JUnit_TestWebcamCaputureLib  extends JUnit_Test {

    @Test
    public void test() throws IOException {
        patternRecognition.shootPicture.ShootBufferedStillWebcam.takeBufferedPicture();
    }

    @Test
    public void testPanel(){
        Webcam w = Webcam.getDefault();
        w.setCustomViewSizes(new Dimension(1280,720));
        w.setViewSize(new Dimension(1280,720)); // set camera resolution
        WebcamPanel panel = new WebcamPanel(w, new Dimension(1280,720), true); // create panel

        JFrame window = new JFrame("Test webcam panel");
        window.setResizable(true);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);
        window.add(panel);
        window.pack();
        Graphics g = window.getGraphics();
        int[] rgb=new int[]{0,255,0};
        g.setColor(new Color(rgb[0],rgb[1],rgb[2]));
        int i=0;
        while (true){
            i+=1;
            if (i==20) {
                int tmp = rgb[0];
                rgb[0] = rgb[2];
                rgb[2] = rgb[1];
                rgb[1] = tmp;
                g.setColor(new Color(rgb[0], rgb[1], rgb[2]));
                i=0;
            }
            g.drawRect(100, 240, 5, 5);
            g.drawRect(120, 220, 5, 5);
            g.drawRect(140, 200, 5, 5);
            g.drawRect(1280-100, 240, 5, 5);
            g.drawRect(1280-120, 220, 5, 5);
            g.drawRect(1280-140, 200, 5, 5);
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}