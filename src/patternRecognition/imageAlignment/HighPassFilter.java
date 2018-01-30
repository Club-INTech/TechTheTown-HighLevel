package patternRecognition.imageAlignment;

import tests.container.A;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class HighPassFilter{
    private static boolean debug=true;
    private static int width=2000;
    private static int height=2000;

    //Savegarde d'une matrice
    private static void saveHighPassedImage(int[][][] highPassedMatrix){
        BufferedImage out = new BufferedImage(highPassedMatrix.length,highPassedMatrix[0].length,BufferedImage.TYPE_INT_RGB);
        Color color;
        for (int x=0; x<highPassedMatrix.length; x++){
            for (int y=0; y<highPassedMatrix[0].length; y++){
                color= new Color(highPassedMatrix[x][y][0],highPassedMatrix[x][y][1],highPassedMatrix[x][y][2]);
                int RGB=color.getRGB();
                out.setRGB(x,y,RGB);
            }
        }
        try {
            File outputfile = new File("savedHighPassedImage.png");
            ImageIO.write(out, "png", outputfile);
        } catch (IOException e) {
            System.out.println("Problème à l'enregistrement de l'image");
        }
    }
    private static void saveHighPassedImage(int[][] highPassedMatrix){
        BufferedImage out = new BufferedImage(highPassedMatrix.length,highPassedMatrix[0].length,BufferedImage.TYPE_INT_RGB);
        Color color;
        for (int x=0; x<highPassedMatrix.length; x++){
            for (int y=0; y<highPassedMatrix[0].length; y++){
                color= new Color(highPassedMatrix[x][y],highPassedMatrix[x][y],highPassedMatrix[x][y]);
                int RGB=color.getRGB();
                out.setRGB(x,y,RGB);
            }
        }
        try {
            File outputfile = new File("savedHighPassedImage.png");
            ImageIO.write(out, "png", outputfile);
        } catch (IOException e) {
            System.out.println("Problème à l'enregistrement de l'image");
        }
    }




    //Filtre passe-haut sur une zone sélectionnée
    private static int[][][] highPassingFilter(int[][][] colorMatrix, int[][] selectedZone, int validPointColorSeuil){
        int xdebut=selectedZone[0][0];
        int ydebut=selectedZone[0][1];
        int xfin=selectedZone[1][0];
        int yfin=selectedZone[1][1];

        if (debug) {
            if (xdebut < 0 || xdebut > colorMatrix.length) {
                System.out.println("xdebut out of image bounds");
                //return null;
            }
            if (ydebut < 0 || ydebut > colorMatrix.length) {
                System.out.println("ydebut out of image bounds");
                //return null;
            }
            if (xfin < 0 || xfin > colorMatrix[0].length) {
                System.out.println("xfin out of image bounds");
                //return null;
            }
            if (yfin < 0 || yfin > colorMatrix[0].length) {
                System.out.println("yfin out of image bounds");
                //return null;
            }
        }

        //PasseHaut
        int[][][] highPassedMatrix = new int[xfin-xdebut+1][yfin-ydebut+1][3];
        for (int y = ydebut; y <= yfin; y++) {
            for (int x = xdebut; x <= xfin; x++) {
                int xPasseHautMatrix=x-xdebut;
                int yPasseHautMatrix=y-ydebut;
                highPassedMatrix[xPasseHautMatrix][yPasseHautMatrix][0]=0;
                highPassedMatrix[xPasseHautMatrix][yPasseHautMatrix][1]=0;
                highPassedMatrix[xPasseHautMatrix][yPasseHautMatrix][2]=0;
                boolean edge=false;
                int matrixRadius=10;
                for (int i = -matrixRadius; i <= matrixRadius; i++) {
                    for (int j = -matrixRadius; j <= matrixRadius; j++) {
                        if (x + i >= xdebut && x + i <= xfin) {
                            if (y + j >= ydebut && y + j <= yfin) {
                                if (i == 0 && j == 0) {
                                    highPassedMatrix[xPasseHautMatrix][yPasseHautMatrix][0] += (Math.pow(2*matrixRadius+1,2)-1) * colorMatrix[x+i][y+j][0];
                                    highPassedMatrix[xPasseHautMatrix][yPasseHautMatrix][1] += (Math.pow(2*matrixRadius+1,2)-1) * colorMatrix[x+i][y+j][1];
                                    highPassedMatrix[xPasseHautMatrix][yPasseHautMatrix][2] += (Math.pow(2*matrixRadius+1,2)-1) * colorMatrix[x+i][y+j][2];
                                } else {
                                    highPassedMatrix[xPasseHautMatrix][yPasseHautMatrix][0] -= colorMatrix[x+i][y+j][0];
                                    highPassedMatrix[xPasseHautMatrix][yPasseHautMatrix][1] -= colorMatrix[x+i][y+j][1];
                                    highPassedMatrix[xPasseHautMatrix][yPasseHautMatrix][2] -= colorMatrix[x+i][y+j][2];
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
                    highPassedMatrix[xPasseHautMatrix][yPasseHautMatrix][0]=0;
                    highPassedMatrix[xPasseHautMatrix][yPasseHautMatrix][1]=0;
                    highPassedMatrix[xPasseHautMatrix][yPasseHautMatrix][2]=0;
                }
                else{
                    highPassedMatrix[xPasseHautMatrix][yPasseHautMatrix][0]*=(double)1/Math.pow(2*matrixRadius+1,2);
                    highPassedMatrix[xPasseHautMatrix][yPasseHautMatrix][1]*=(double)1/Math.pow(2*matrixRadius+1,2);
                    highPassedMatrix[xPasseHautMatrix][yPasseHautMatrix][2]*=(double)1/Math.pow(2*matrixRadius+1,2);
                }
            }
        }
        return highPassedMatrix;
    }

    //Normalisation d'une matrice
    private static int[][][] normaliseOver255(int[][][]highPassMatrix){
        int maxRed=0;
        int maxGreen=0;
        int maxBlue=0;
        for (int y = 0; y < highPassMatrix[0].length; y++) {
            for (int x = 0; x < highPassMatrix.length; x++) {
                if (highPassMatrix[x][y][0]>maxRed){
                    maxRed=highPassMatrix[x][y][0];
                }
                if (highPassMatrix[x][y][1]>maxGreen){
                    maxGreen=highPassMatrix[x][y][1];
                }
                if (highPassMatrix[x][y][2]>maxBlue){
                    maxBlue=highPassMatrix[x][y][2];
                }
            }
        }
        for (int y = 0; y < highPassMatrix[0].length; y++) {
            for (int x = 0; x < highPassMatrix.length; x++) {
                highPassMatrix[x][y][0]*=255;
                highPassMatrix[x][y][0]=(int)(highPassMatrix[x][y][0]/(float)maxRed);
                if (highPassMatrix[x][y][0]<0){
                    highPassMatrix[x][y][0]=0;
                }
                highPassMatrix[x][y][1]*=255;
                highPassMatrix[x][y][1]=(int)(highPassMatrix[x][y][1]/(float)maxGreen);
                if (highPassMatrix[x][y][1]<0){
                    highPassMatrix[x][y][1]=0;
                }

                highPassMatrix[x][y][2]*=255;
                highPassMatrix[x][y][2]=(int)(highPassMatrix[x][y][2]/(float)maxBlue);
                if (highPassMatrix[x][y][2]<0){
                    highPassMatrix[x][y][2]=0;
                }
            }
        }
        return highPassMatrix;
    }
    private static int[][] normaliseOver255(int[][] highPassMatrix){
        int maxGrey=0;
        for (int y = 0; y < highPassMatrix[0].length; y++) {
            for (int x = 0; x < highPassMatrix.length; x++) {
                if (highPassMatrix[x][y]>maxGrey){
                    maxGrey=highPassMatrix[x][y];
                }
            }
        }
        for (int y = 0; y < highPassMatrix[0].length; y++) {
            for (int x = 0; x < highPassMatrix.length; x++) {
                highPassMatrix[x][y]*=255;
                highPassMatrix[x][y]=(int)(highPassMatrix[x][y]/(float)maxGrey);
                if (highPassMatrix[x][y]<0){
                    highPassMatrix[x][y]=0;
                }
            }
        }
        return highPassMatrix;
    }

    //Convertit une matrice de couleur en nuances de gris
    private static int[][] toGreyMatrix(int[][][] colorMatrix){
        int[][] greyMatrix = new int[colorMatrix.length][colorMatrix[0].length];
        for (int x=0; x<colorMatrix.length;x++) {
            for (int y = 0; y < colorMatrix[0].length; y++) {
                greyMatrix[x][y]=0;
                for (int i=0; i<3;i++) {
                    greyMatrix[x][y]+=colorMatrix[x][y][i];
                }
                greyMatrix[x][y]/=(double)3;
            }
        }
        return greyMatrix;
    }

    //Binarisation d'une matrice
    private static int[][][] binarize(int[][][] colorMatrix, int seuil){
        for (int y = 0; y < colorMatrix[0].length; y++) {
            for (int x = 0; x < colorMatrix.length; x++) {
                if ((colorMatrix[x][y][0]+colorMatrix[x][y][1]+colorMatrix[x][y][2])/3>seuil){
                    colorMatrix[x][y][0]=255;
                    colorMatrix[x][y][1]=255;
                    colorMatrix[x][y][2]=255;
                }
                else{
                    colorMatrix[x][y][0]=0;
                    colorMatrix[x][y][1]=0;
                    colorMatrix[x][y][2]=0;
                }
            }
        }
        return colorMatrix;
    }
    private static int[][] binarize(int[][] colorMatrix, int seuil) {
        for (int y = 0; y < colorMatrix[0].length; y++) {
            for (int x = 0; x < colorMatrix.length; x++) {
                if (colorMatrix[x][y] > seuil && colorMatrix[x][y] > seuil && colorMatrix[x][y] > seuil) {
                    colorMatrix[x][y] = 255;
                }
                else{
                    colorMatrix[x][y]=0;
                }
            }
        }
        return colorMatrix;
    }

    //Check des zones de détection de couleur
    private static int[] checkDetectionZones(int[][] colorMatrix, int[][] selectedZone, int[][] positionColorsOnImage) {
        int xdebut = selectedZone[0][0];
        int ydebut = selectedZone[0][1];
        int[] nbWhiteCasesArray = new int[3];
        for (int i = 0; i < 3; i++) {
            int nbWhiteCases = 0;
            int xcenter = ((positionColorsOnImage[0][i] - xdebut) + (positionColorsOnImage[2][i] - xdebut)) / 2;
            int ycenter = ((positionColorsOnImage[1][i] - ydebut) + (positionColorsOnImage[3][i] - ydebut)) / 2;
            if (debug) {
                System.out.println("Pixels checked (on filtered image) from (" +
                        (positionColorsOnImage[0][i] - xdebut) + "," + (positionColorsOnImage[1][i] - ydebut)
                        + ") to (" +
                        (positionColorsOnImage[2][i] - xdebut) + "," + (positionColorsOnImage[3][i] - ydebut)
                        + ")");
            }
            for (int x = positionColorsOnImage[0][i] - xdebut; x < positionColorsOnImage[2][i] - xdebut; x++) {
                for (int y = positionColorsOnImage[1][i] - ydebut; y < positionColorsOnImage[3][i] - ydebut; y++) {
                    if (colorMatrix[x][y] == 255) {
                        nbWhiteCases += 1;
                    }
                }
            }
            if (debug) {
                System.out.println("Zone " + i + " : " + nbWhiteCases + " cases blanches");
            }
            nbWhiteCasesArray[i] = nbWhiteCases;
        }
        return nbWhiteCasesArray;
    }


    private static int[][][] checkDetectionZonesWithOffset(int[][] colorMatrix, int[][] selectedZone, int[][] positionColorsOnImage){
        int xdebut=selectedZone[0][0];
        int ydebut=selectedZone[0][1];

        int[][] xOffsetList=new int[3][2];
        int[][] yOffsetList=new int[3][2];
        for (int i=0; i<3; i++){
            int nbWhiteCases=0;
            int leftWhiteX=10000;
            int upWhiteY=10000;
            int rightWhiteX=-1;
            int downWhiteY=-1;
            if (debug){
                System.out.println("Pixels checked (on filtered image) from ("+
                        (positionColorsOnImage[0][i]-xdebut)+ ","+(positionColorsOnImage[1][i]-ydebut)
                        +") to ("+
                        (positionColorsOnImage[2][i]-xdebut)+"," +(positionColorsOnImage[3][i]-ydebut)
                        +")");
            }
            for (int x=positionColorsOnImage[0][i]-xdebut; x<positionColorsOnImage[2][i]-xdebut; x++){
                for (int y=positionColorsOnImage[1][i]-ydebut; y<positionColorsOnImage[3][i]-ydebut; y++){
                    if (colorMatrix[x][y]==255){
                        nbWhiteCases+=1;
                        if (leftWhiteX>x){
                            leftWhiteX=x;
                        }
                        if (rightWhiteX<x){
                            rightWhiteX=x;
                        }
                        if (upWhiteY>y){
                            upWhiteY=y;
                        }
                        if (downWhiteY<y){
                            downWhiteY=y;
                        }
                    }
                }
            }
            if (debug){
                System.out.println("Zone "+i+" : "+nbWhiteCases+" cases blanches");
            }
            if (nbWhiteCases>5){
                int[] xToMove = new int[2];
                int[] yToMove = new int[2];
                xToMove[0]=(positionColorsOnImage[0][i]-rightWhiteX);
                xToMove[1]=(positionColorsOnImage[2][i]-leftWhiteX);
                if (xToMove[0]<0 || xToMove[0]>width){
                    xToMove[0]=0;
                }
                if (xToMove[1]<0 || xToMove[1]>width){
                    xToMove[1]=0;
                }
                yToMove[0]=(positionColorsOnImage[1][i]-downWhiteY);
                yToMove[1]=(positionColorsOnImage[3][i]-upWhiteY);
                if (yToMove[0]<0 || yToMove[0]>height){
                    yToMove[0]=0;
                }
                if (yToMove[1]<0 || yToMove[1]>height){
                    yToMove[1]=0;
                }
                xOffsetList[i]=xToMove;
                yOffsetList[i]=yToMove;
            }
            else{
                xOffsetList[i]=new int[]{0,0};
                yOffsetList[i]=new int[]{0,0};
            }
        }
        int[][][] offsetList={xOffsetList,yOffsetList};
        return offsetList;
    }

    //Main
    public static void process(int[][][] colorMatrix, int[][] selectedZone, int[][] positionsColorsOnImage){
        int validPointColorSeuil=50;
        int[][][] highPassedMatrix=highPassingFilter(colorMatrix,selectedZone,validPointColorSeuil);
        highPassedMatrix=normaliseOver255(highPassedMatrix);
        int[][] greyMatrix=toGreyMatrix(highPassedMatrix);
        int[][] binaryGreyMatrix = binarize(greyMatrix,validPointColorSeuil);
        int[][][] offsetList = checkDetectionZonesWithOffset(binaryGreyMatrix, selectedZone,  positionsColorsOnImage);

        saveHighPassedImage(binaryGreyMatrix);
    }


    public static void setDebugHighPassFilter(boolean value) {
        debug = value;
    }
}
