package patternRecognition;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import static org.opencv.imgcodecs.Imgcodecs.imread;


public class LocatePattern {

    private static boolean debug=false;
    
    public static int[] locatePattern(String path, int[] selectedZone) {
        /**selectedZone={xdebut, ydebut, width, height}*/
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Rect zoneUsed = new Rect(selectedZone[0], selectedZone[1], selectedZone[2], selectedZone[3]);
        Mat src = imread(path);
        //Imgproc.cvtColor(src, src, Imgproc.COLOR_RGB2BGR);
        Mat image = new Mat(src, zoneUsed);
        int[][][] foundRectangles;
        try {
            /**Essentials:*/
            int[][] firstRect = findRectangle(image, 10, 20, 9);
            int[][] secondRect = findRectangle(image, 30, 40, 9);
            int[][] thirdRect = findRectangle(image, 30, 40, 15);
            foundRectangles = new int[][][]{firstRect, secondRect, thirdRect};
        } catch (Exception e) {
            foundRectangles = null;
            e.printStackTrace();
        }
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
        Point center=new Point((int)(xmin+xmax)/2, (int)(ymin+ymax)/2);
        if (debug) {
            System.out.println("FinalCoords:(" + xmin + "," + ymin + "),(" + xmax + "," + ymax + ")");
            String outputName = "resultColor.png";
            Imgproc.drawMarker(image, center, new Scalar(255, 0, 0));
            Imgcodecs.imwrite(outputName, image);
        }
        int[] fullCoords = new int[]{xmin + selectedZone[0], ymin + selectedZone[1], xmax + selectedZone[0], ymax + selectedZone[1]};
        return fullCoords;
    }

    private static int[][] findRectangle(Mat src, int threesold1Value, int threesold2Value, int ksize) throws Exception {
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
            Core.mixChannels(blurredChannel, gray0Channel, new MatOfInt(ch));
            int thresholdLevel = 1;
            for (int t = 0; t < thresholdLevel; t++) {
                if (t == 0) {
                    Imgproc.Canny(gray0, gray, threesold1Value, threesold2Value, 3, true); // true ?
                    Imgproc.dilate(gray, gray, new Mat(), new Point(-1, -1),1); // 1
                    // DE BASE : Imgproc.Canny(gray0, gray, 10, 20, 3, true); // true ?
                    // ?
                } else {
                    Imgproc.adaptiveThreshold(gray0, gray, thresholdLevel,
                            Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
                            Imgproc.THRESH_BINARY,
                            (src.width() + src.height())/200, t);
                }

                Imgproc.findContours(gray, contours, new Mat(),
                        Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
                Imgcodecs.imwrite("gray.png", gray);

                for (MatOfPoint contour : contours) {
                    List<Point> currentContour = contour.toList();
                    double xmin=10000;
                    double xmax=0;
                    double ymin=10000;
                    double ymax=0;
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
                    double deltaX=xmax-xmin;
                    double deltaY=ymax-ymin;
                    if (deltaX>40 && deltaY>80 && deltaY<150 && deltaX<250 && deltaX<2*deltaY) {
                        MatOfPoint2f temp = new MatOfPoint2f(contour.toArray());

                        double area = Imgproc.contourArea(contour);
                        approxCurve = new MatOfPoint2f();
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

                            if (maxCosine < 0.3) {
                                maxArea = area;
                                maxId = contours.indexOf(contour);
                            }
                        }
                    }
                }
            }
        }
        int[][] patternZoneCroppedImage;
        if (maxId >= 0) {
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
            System.out.println("("+xmin+","+ymin+"),("+xmax+","+ymax+")");
            //Imgproc.drawContours(src, contours, maxId, new Scalar(255, 0, 0,.8), 8);
            patternZoneCroppedImage=new int[][]{{(int)xmin,(int)ymin},{(int)xmax,(int)ymax}};
        }
        else {
            patternZoneCroppedImage=new int[][]{{-1,-1},{-1,-1}};
        }
        return patternZoneCroppedImage;
    }

    private static double angle(Point p1, Point p2, Point p0) {
        double dx1 = p1.x - p0.x;
        double dy1 = p1.y - p0.y;
        double dx2 = p2.x - p0.x;
        double dy2 = p2.y - p0.y;
        return (dx1 * dx2 + dy1 * dy2)
                / Math.sqrt((dx1 * dx1 + dy1 * dy1) * (dx2 * dx2 + dy2 * dy2)
                + 1e-10);
    }

    public static void setDebug(boolean value){
        debug=value;
    }
}
