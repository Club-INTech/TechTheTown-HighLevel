package patternRecognition.imageAlignment;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class HighPassFilter{
    private static boolean debug=true;

    private static void saveHighPassedImage(int[][][] passeHautMatrix){
        BufferedImage out = new BufferedImage(passeHautMatrix.length,passeHautMatrix[0].length,BufferedImage.TYPE_INT_RGB);
        Color color;
        for (int x=0; x<passeHautMatrix.length; x++){
            for (int y=0; y<passeHautMatrix[0].length; y++){
                color= new Color(passeHautMatrix[x][y][0],passeHautMatrix[x][y][1],passeHautMatrix[x][y][2]);
                int RGB=color.getRGB();
                out.setRGB(x,y,RGB);
            }
        }
        try {
            File outputfile = new File("saved.png");
            ImageIO.write(out, "png", outputfile);
        } catch (IOException e) {
            System.out.println("Problème à l'enregistrement de l'image");
        }
    }


    public static ArrayList<Point> highPassingFilter(int[][][] colorMatrix, int[][] selectedZone, int validPointColorSeuil){
        int xdebut=selectedZone[0][0];
        int ydebut=selectedZone[0][1];
        int xfin=selectedZone[1][0];
        int yfin=selectedZone[1][1];

        if (debug) {
            if (xdebut < 0 || xdebut > colorMatrix.length) {
                System.out.println("xdebut out of image bounds");
                return null;
            }
            if (ydebut < 0 || ydebut > colorMatrix.length) {
                System.out.println("ydebut out of image bounds");
                return null;
            }
            if (xfin < 0 || xfin > colorMatrix[0].length) {
                System.out.println("xfin out of image bounds");
                return null;
            }
            if (yfin < 0 || yfin > colorMatrix[0].length) {
                System.out.println("yfin out of image bounds");
                return null;
            }
        }

        //PasseHaut
        int[][][] passeHautMatrix = new int[xfin-xdebut+1][yfin-ydebut+1][3];
        for (int y = ydebut; y <= yfin; y++) {
            for (int x = xdebut; x <= xfin; x++) {
                int xPasseHautMatrix=x-xdebut;
                int yPasseHautMatrix=y-ydebut;
                passeHautMatrix[xPasseHautMatrix][yPasseHautMatrix][0]=0;
                passeHautMatrix[xPasseHautMatrix][yPasseHautMatrix][1]=0;
                passeHautMatrix[xPasseHautMatrix][yPasseHautMatrix][2]=0;
                boolean edge=false;
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        if (x + i >= xdebut && x + i <= xfin) {
                            if (y + j >= ydebut && y + j <= yfin) {
                                if (i == 0 && j == 0) {
                                    passeHautMatrix[xPasseHautMatrix][yPasseHautMatrix][0] += 8 * colorMatrix[x+i][y+j][0];
                                    passeHautMatrix[xPasseHautMatrix][yPasseHautMatrix][1] += 8 * colorMatrix[x+i][y+j][1];
                                    passeHautMatrix[xPasseHautMatrix][yPasseHautMatrix][2] += 8 * colorMatrix[x+i][y+j][2];
                                } else {
                                    passeHautMatrix[xPasseHautMatrix][yPasseHautMatrix][0] -= colorMatrix[x+i][y+j][0];
                                    passeHautMatrix[xPasseHautMatrix][yPasseHautMatrix][1] -= colorMatrix[x+i][y+j][1];
                                    passeHautMatrix[xPasseHautMatrix][yPasseHautMatrix][2] -= colorMatrix[x+i][y+j][2];
                                }

                            }
                            else{
                                edge=true;
                            }
                        }
                        else{
                            edge=true;
                        }
                    }
                }
                if (edge){
                    passeHautMatrix[xPasseHautMatrix][yPasseHautMatrix][0]=0;
                    passeHautMatrix[xPasseHautMatrix][yPasseHautMatrix][1]=0;
                    passeHautMatrix[xPasseHautMatrix][yPasseHautMatrix][2]=0;
                }
                else{
                    passeHautMatrix[xPasseHautMatrix][yPasseHautMatrix][0]*=(double)1/8;
                    passeHautMatrix[xPasseHautMatrix][yPasseHautMatrix][1]*=(double)1/8;
                    passeHautMatrix[xPasseHautMatrix][yPasseHautMatrix][2]*=(double)1/8;
                }
            }
        }

        //NormaliseOn255
        int maxRed=0;
        int maxGreen=0;
        int maxBlue=0;
        for (int y = 0; y < passeHautMatrix[0].length; y++) {
            for (int x = 0; x < passeHautMatrix.length; x++) {
                if (passeHautMatrix[x][y][0]>maxRed){
                    maxRed=passeHautMatrix[x][y][0];
                }
                if (passeHautMatrix[x][y][1]>maxGreen){
                    maxGreen=passeHautMatrix[x][y][1];
                }
                if (passeHautMatrix[x][y][2]>maxBlue){
                    maxBlue=passeHautMatrix[x][y][2];
                }
            }
        }
        ArrayList<Point> points = new ArrayList<Point>();
        for (int y = 0; y < passeHautMatrix[0].length; y++) {
            for (int x = 0; x < passeHautMatrix.length; x++) {
                boolean toAppend=false;
                passeHautMatrix[x][y][0]*=255;
                passeHautMatrix[x][y][0]=(int)(passeHautMatrix[x][y][0]/(float)maxRed);
                if (passeHautMatrix[x][y][0]<0){
                    passeHautMatrix[x][y][0]=0;
                }
                else if (passeHautMatrix[x][y][0]>validPointColorSeuil){
                    toAppend=true;
                }

                passeHautMatrix[x][y][1]*=255;
                passeHautMatrix[x][y][1]=(int)(passeHautMatrix[x][y][1]/(float)maxGreen);
                if (passeHautMatrix[x][y][1]<0){
                    passeHautMatrix[x][y][1]=0;
                }
                else if (passeHautMatrix[x][y][1]>validPointColorSeuil){
                    toAppend=true;
                }

                passeHautMatrix[x][y][2]*=255;
                passeHautMatrix[x][y][2]=(int)(passeHautMatrix[x][y][2]/(float)maxBlue);
                if (passeHautMatrix[x][y][2]<0){
                    passeHautMatrix[x][y][2]=0;
                }
                else if (passeHautMatrix[x][y][2]>validPointColorSeuil){
                    toAppend=true;
                }
                if (toAppend){
                    points.add(new Point(x+xdebut,y+ydebut));
                }
            }
        }
        saveHighPassedImage(passeHautMatrix);
        return points;
    }

}
