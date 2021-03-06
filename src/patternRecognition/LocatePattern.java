package patternRecognition;


import org.opencv.core.Core;
import org.opencv.core.Rect;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.CvType;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.List;


/** Permet de localiser le pattern sur l'image prise par la piCam
 */
public class LocatePattern {

    //Variable de debug
    private static boolean debug=true;
    private static boolean isSavingImages=true;

    /** Fonction de localisation du pattern sur l'image prise par la Picam
     * @param buffImg BufferedImage de l'image à analyser
     * @param selectedZone zone de l'image à analyser, de la forme {xstart, ystart, width, height}
     * @return renvoie les coordonnées du pattern sur l'image à analyser
     */
    public static int[] locatePattern(BufferedImage buffImg, int[] selectedZone, String orientation) {
        //Charge la librairie OpenCV
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        //Définit un rectange de la zone utilisée, afin de réduire l'image
        Rect zoneUsed = new Rect(selectedZone[0], selectedZone[1], selectedZone[2], selectedZone[3]);

        //Convertit la BufferedImage en Mat
        Mat src=bufferedImageToMat(buffImg);

        //Convertit l'image de RGB à BGR
        //Imgproc.cvtColor(src, src, Imgproc.COLOR_RGB2BGR);

        //Garde la zone sélectionnée de l'image
        Mat image = new Mat(src, zoneUsed);

        //Multiples passages pour détecter les rectangles
        int[][][] foundRectangles;
        try {
            //Essentials
            //PiCam
            //int[][] firstRect = findRectangle(image, 10, 20, 9);
            //int[][] secondRect = findRectangle(image, 30, 40, 9);
            //int[][] thirdRect = findRectangle(image, 30, 40, 15);
            int[][] firstRect = findRectangle(image, 10, 20, 5, orientation);
            int[][] secondRect = findRectangle(image, 30, 40, 5, orientation);
            int[][] thirdRect = findRectangle(image, 30, 40, 9, orientation);
            //Add-ons
            //int[][] forthRect = findRectangle(image, 30, 40, 21);
            foundRectangles = new int[][][]{firstRect, secondRect,thirdRect};
        } catch (Exception e) {
            foundRectangles = null;
            if (debug){
                System.out.println("Pas de rectangles trouvés");
            }
            e.printStackTrace();
            return new int[]{0,0,0,0};
        }

        //La zone où les patterns ont étés trouvés est rognée afin de garder toutes les détections en un minimal de place
        int xmax = -1;
        int ymax = -1;
        int xmin = 10000;
        int ymin = 10000;
        for (int i = 0; i < foundRectangles.length; i++) {
            if (foundRectangles[i][0][0] < xmin) {
                if (foundRectangles[i][0][0] > 0) {
                    xmin = foundRectangles[i][0][0];
                }
            }
            if (foundRectangles[i][0][1] < ymin) {
                if (foundRectangles[i][0][1] > 0) {
                    ymin = foundRectangles[i][0][1];
                }
            }
            if (foundRectangles[i][1][0] > xmax) {
                if (foundRectangles[i][1][0] > 0) {
                    xmax = foundRectangles[i][1][0];
                }
            }
            if (foundRectangles[i][1][1] > ymax) {
                if (foundRectangles[i][1][1] > 0) {
                    ymax = foundRectangles[i][1][1];
                }
            }
        }
        if (debug) {
            System.out.println("FinalCoords:(" + xmin + "," + ymin + "),(" + xmax + "," + ymax + ")");

            //Affiche le centre de la zone de pattern déterminée
            Point center=new Point((int)(xmin+xmax)/2, (int)(ymin+ymax)/2);
            Imgproc.drawMarker(image, center, new Scalar(0, 255, 0));

            //Enregistre l'image
            if (isSavingImages) {
                Imgcodecs.imwrite("/tmp/LocatedPattern.png", image);
            }
        }
        int[] fullCoords;
        //Renvoie les coordonnées de la zone de localisation rognée.
        if (xmax>=0 && ymax >=0 && xmin<10000 && ymin<10000) {
            fullCoords = new int[]{xmin + selectedZone[0], ymin + selectedZone[1], xmax + selectedZone[0], ymax + selectedZone[1]};
        }
        else{
            fullCoords = new int[]{0,0,0,0};
        }
        return fullCoords;
    }



    /** Convertit une image BufferedImage en Mat pour OpenCV
     * @param bufferedImage image à convertir
     * @return renvoie le Mat qui correspond à l'image
     */
    public static Mat bufferedImageToMat(BufferedImage bufferedImage) {
        Mat mat = new Mat(bufferedImage.getHeight(), bufferedImage.getWidth(), CvType.CV_8UC3);
        byte[] data = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
        mat.put(0, 0, data);
        return mat;
    }


    /** Fonction permettant de détecter les rectangles
     * @param src image source
     * @param threesold1Value valeur de threesold 1
     * @param threesold2Value valeur de threesold 2
     * @param ksize grossièreté du floutage de l'image (doit être un nombre impair)
     * @return renvoie les coordonnées des 2 points créant un rectangle entourant la zone de pattern déterminé
     */
    private static int[][] findRectangle(Mat src, int threesold1Value, int threesold2Value, int ksize, String orientation){
        Mat blurred = src.clone();
        Imgproc.medianBlur(src, blurred, ksize);
        Mat gray0 = new Mat(blurred.size(), CvType.CV_8U), gray = new Mat();
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        List<Mat> blurredChannel = new ArrayList<Mat>();
        blurredChannel.add(blurred);
        List<Mat> gray0Channel = new ArrayList<Mat>();
        gray0Channel.add(gray0);
        MatOfPoint2f approxCurve;
        double maxArea = 0;
        int maxId = -1;
        for (int c = 0; c < 3; c++) {
            int ch[] = { c, 0 };
            //Mélange les images grises et floues
            Core.mixChannels(blurredChannel, gray0Channel, new MatOfInt(ch));
            int thresholdLevel = 1;
            for (int t = 0; t < thresholdLevel; t++) {
                if (t == 0) {
                    //Fonction canny : très utile à la reconnaissance
                    Imgproc.Canny(gray0, gray, threesold1Value, threesold2Value, 3, true); // true ?
                    //Avant modifications : Imgproc.Canny(gray0, gray, 10, 20, 3, true); // true ?
                    //Fonction dilate : grossi les pixels de l'image
                    Imgproc.dilate(gray, gray, new Mat(), new Point(-1, -1),1); // 1
                } else {
                    Imgproc.adaptiveThreshold(gray0, gray, thresholdLevel,
                            Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
                            Imgproc.THRESH_BINARY,
                            (src.width() + src.height())/200, t);
                }

                //Fonctions permettant de trouver les contours de l'image
                Imgproc.findContours(gray, contours, new Mat(),
                        Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
                if (debug) {
                    //Enregistre l'image en gris
                    Imgcodecs.imwrite("/tmp/gray.png", gray);
                }

                //Détermination du meilleur contour
                for (MatOfPoint contour : contours) {
                    List<Point> currentContour = contour.toList();
                    double xmin=10000;
                    double xmax=-1;
                    double ymin=10000;
                    double ymax=-1;
                    for (int i=0; i<currentContour.size(); i++){
                        if (currentContour.get(i).x<xmin){
                            xmin=currentContour.get(i).x;
                        }
                        if(currentContour.get(i).x>xmax){
                            xmax=currentContour.get(i).x;
                        }
                        if (currentContour.get(i).y<ymin){
                            ymin=currentContour.get(i).y;
                        }
                        if(currentContour.get(i).y>ymax){
                            ymax=currentContour.get(i).y;
                        }
                    }
                    //Delta X = taille de la zone détectée en X
                    double deltaX=xmax-xmin;
                    //Delta Y = taille de la zone détectée en Y
                    double deltaY=ymax-ymin;
                    //On valide le contour s'il correspond aux spécifications du pattern:
                    //deltaX>60 pixels
                    //deltaY>100 pixels
                    //deltaX<250 pixels
                    //deltaY<300 pixels
                    //deltaX<deltaY
                    /**Valeurs de la taille des carrés à détecter
                     */
                    //VALEURS PICAM
                    //if (deltaX > 60 && deltaY > 100 && deltaX < 250 && deltaY < 200 && deltaX < deltaY)
                    int threesoldMaxDeltaX=50;
                    int threesoldMinDeltaX=10;
                    int threesoldMaxDeltaY=75;
                    int threesoldMinDeltaY=30;
                    double threesoldRatio=1;
                    if (orientation.equals("face")) {
                        //ditstance : 50cm, angle d'incidence +-10°
                        //TODO : à faire
                        threesoldMaxDeltaX = 100;
                        threesoldMinDeltaX = 70;
                        threesoldMaxDeltaY = 100;
                        threesoldMinDeltaY = 70;
                        threesoldRatio = 2;
                    }
                    else if (orientation.equals("montlhery")){
                        //distance : 1m, angle d'incidence : +-10°
                        threesoldMaxDeltaX = 60;
                        threesoldMinDeltaX = 30;
                        threesoldMaxDeltaY = 60;
                        threesoldMinDeltaY = 30;
                        threesoldRatio = 2;
                    }
                    else if (orientation.equals("side")){
                        //distance : 1,4m, angle d'incidence : 70°
                        threesoldMaxDeltaX = 50;
                        threesoldMinDeltaX = 10;
                        threesoldMaxDeltaY = 75;
                        threesoldMinDeltaY = 30;
                        threesoldRatio = 1;
                    }


                    if (deltaX > threesoldMinDeltaX && deltaY > threesoldMinDeltaY &&
                        deltaX < threesoldMaxDeltaX && deltaY < threesoldMaxDeltaY &&
                        deltaX < threesoldRatio*deltaY) {
                        //dimensions relatives interrupteur
                        //valeur plutot bonnes : deltaY>2.1*deltaX  deltaY<2.5*deltaX
                        //valeur à test : deltaY>1.8*deltaX  deltaY<2.3*deltaX
                        //valeur à test : deltaY>1.9*deltaX  deltaY<2.5*deltaX
                        if (true){
                        //if (!(deltaY>1.9*deltaX && deltaY<2.5*deltaX)){
                            MatOfPoint2f temp = new MatOfPoint2f(contour.toArray());
                            double area = Imgproc.contourArea(contour);
                            approxCurve = new MatOfPoint2f();
                            //On détecte les rectangles
                            Imgproc.approxPolyDP(temp, approxCurve,
                                    Imgproc.arcLength(temp, true) * 0.02, true);
                            if (approxCurve.total() == 4 && area >= maxArea) {
                                double maxCosine = 0;
                                List<Point> curves = approxCurve.toList();
                                for (int j = 2; j < 5; j++) {
                                    double cosine = Math.abs(angle(curves.get(j % 4),
                                            curves.get(j - 2), curves.get(j - 1)));
                                    maxCosine = Math.max(maxCosine, cosine);
                                }
                                if (maxCosine < 0.4) {
                                    maxArea = area;
                                    maxId = contours.indexOf(contour);
                                }
                            }
                        }
                    }
                }
            }
        }

        int[][] patternZoneCroppedImage;
        //Si un contour a été sélectionné
        if (maxId >= 0) {
            //La zone où les patterns ont étés trouvés est rognée afin de garder toutes les détections en un minimal de place
            double xmin=10000;
            double xmax=-1;
            double ymin=10000;
            double ymax=-1;
            List<Point> retainedContours = contours.get(maxId).toList();
            for (int i=0; i<retainedContours.size(); i++){
                if (retainedContours.get(i).x<xmin){
                    xmin=retainedContours.get(i).x;
                }
                if(retainedContours.get(i).x>xmax){
                    xmax=retainedContours.get(i).x;
                }
                if (retainedContours.get(i).y<ymin){
                    ymin=retainedContours.get(i).y;
                }
                if(retainedContours.get(i).y>ymax){
                    ymax=retainedContours.get(i).y;
                }
            }
            Imgproc.drawContours(src, contours, maxId, new Scalar(255, 0, 0,.8), 8);
            patternZoneCroppedImage = new int[][]{{(int) xmin, (int) ymin}, {(int) xmax, (int) ymax}};
        }
        else {
            patternZoneCroppedImage=new int[][]{{-1,-1},{-1,-1}};
        }
        //Renvoie les points permettant de définir un rectangle autour de la zone croppée.
        return patternZoneCroppedImage;
    }

    /** Fonction d'angle, je sais pas trop ce qu'lle fait, ça a l'air d'être Al-quashi
     */
    private static double angle(Point p1, Point p2, Point p0) {
        double dx1 = p1.x - p0.x;
        double dy1 = p1.y - p0.y;
        double dx2 = p2.x - p0.x;
        double dy2 = p2.y - p0.y;
        return (dx1 * dx2 + dy1 * dy2)
                / Math.sqrt((dx1 * dx1 + dy1 * dy1) * (dx2 * dx2 + dy2 * dy2)
                + 1e-10);
    }

    /**Set le debug
     * @param value valeur à mettre à debug
     */
    public static void setDebug(boolean value){
        debug=value;
    }
}
