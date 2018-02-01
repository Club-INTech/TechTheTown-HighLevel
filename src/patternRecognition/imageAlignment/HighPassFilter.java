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


    private static int[] checkDetectionZonesWithOffset(int[][] binaryMatrix, int[][] referenceBinaryMatrix, int[][] selectedZone, int[][] positionColorsOnImage){
        int xdebut=selectedZone[0][0];
        int ydebut=selectedZone[0][1];

        int xOffsetLeftTemp=0;
        int xOffsetRightTemp=0;
        int yOffsetDownTemp=0;
        int yOffsetUpTemp=0;
        int xOffsetLeftAverage=0;
        int xOffsetRightAverage=0;
        int yOffsetDownAverage=0;
        int yOffsetUpAverage=0;
        int maxOffset=0;
        for (int i=0; i<3; i++){
            int nbWhiteCases=0;
            int leftWhiteX=5000;
            int upWhiteY=5000;
            int rightWhiteX=-1;
            int downWhiteY=-1;
            if (debug){
                System.out.println("Pixels checked (on filtered image) from ("+
                        (positionColorsOnImage[0][i]-xdebut)+","+(positionColorsOnImage[1][i]-ydebut)
                        +") to ("+
                        (positionColorsOnImage[2][i]-xdebut)+"," +(positionColorsOnImage[3][i]-ydebut)
                        +")");
            }
            for (int x=positionColorsOnImage[0][i]-xdebut; x<positionColorsOnImage[2][i]-xdebut; x++){
                for (int y=positionColorsOnImage[1][i]-ydebut; y<positionColorsOnImage[3][i]-ydebut; y++){
                    if (binaryMatrix[x][y]==255){
                        nbWhiteCases+=1;
                        if (leftWhiteX>(x+xdebut-positionColorsOnImage[0][i])){
                            leftWhiteX=(x+xdebut-positionColorsOnImage[0][i]);
                        }
                        if (rightWhiteX<(x+xdebut-positionColorsOnImage[0][i])){
                            rightWhiteX=(x+xdebut-positionColorsOnImage[0][i]);
                        }
                        if (upWhiteY>(y+ydebut-positionColorsOnImage[1][i])){
                            upWhiteY=(y+ydebut-positionColorsOnImage[1][i]);
                        }
                        if (downWhiteY<(y+ydebut-positionColorsOnImage[1][i])){
                            downWhiteY=(y+ydebut-positionColorsOnImage[1][i]);
                        }
                    }
                }
            }
            if (debug){
                System.out.println("Zone "+i+" : "+nbWhiteCases+" cases blanches");
            }
            if (nbWhiteCases>5){
                xOffsetLeftTemp=-rightWhiteX; //Formule OK
                xOffsetRightTemp=-leftWhiteX;
                if (debug) {
                    System.out.println("xOffsetLeftTemp:" + xOffsetLeftTemp);
                    System.out.println("xOffsetRightTemp:" + xOffsetRightTemp);
                    System.out.println("yOffsetUpTemp:" + yOffsetUpTemp);
                    System.out.println("yOffsetDownTemp:" + yOffsetDownTemp);
                }
                if (xOffsetLeftTemp+positionColorsOnImage[0][i]<0 || xOffsetLeftTemp+positionColorsOnImage[0][i]>width){
                    xOffsetLeftAverage+=0;
                }
                else{
                    xOffsetLeftAverage+=xOffsetLeftTemp;
                }
                if (xOffsetRightTemp+positionColorsOnImage[2][i]<0 || xOffsetRightTemp+positionColorsOnImage[2][i]>width){
                    xOffsetRightAverage+=0;
                }
                else{
                    xOffsetRightAverage+=xOffsetRightTemp;
                }
                yOffsetUpTemp=-downWhiteY; //Formule OK
                yOffsetDownTemp=-upWhiteY;
                if (yOffsetUpTemp+positionColorsOnImage[1][i]<0 || yOffsetUpTemp+positionColorsOnImage[1][i]>height){
                    yOffsetUpAverage+=0;
                }
                else{
                    yOffsetUpAverage+=yOffsetUpTemp;
                }
                if (yOffsetDownTemp+positionColorsOnImage[3][i]<0 || yOffsetDownTemp+positionColorsOnImage[3][i]>height){
                    yOffsetDownAverage+=0;
                }
                else{
                    yOffsetDownAverage+=yOffsetDownTemp;
                }
            }
            else{
                xOffsetLeftAverage+=0;
                xOffsetRightAverage+=0;
                yOffsetUpAverage+=0;
                yOffsetDownAverage+=0;
            }
        }
        xOffsetLeftAverage/=3;
        xOffsetRightAverage/=3;
        yOffsetUpAverage/=3;
        yOffsetDownAverage/=3;
        if (Math.abs(xOffsetLeftAverage)>maxOffset){
            maxOffset=Math.abs(xOffsetLeftAverage);
        }
        if (Math.abs(xOffsetRightAverage)>maxOffset){
            maxOffset=Math.abs(xOffsetRightAverage);
        }
        if (Math.abs(yOffsetUpAverage)>maxOffset){
            maxOffset=Math.abs(yOffsetUpAverage);
        }
        if (Math.abs(yOffsetDownAverage)>maxOffset){
            maxOffset=Math.abs(yOffsetDownAverage);
        }

        if (debug){
            System.out.println("xOffsetLeftAverage: "+xOffsetLeftAverage);
            System.out.println("xOffsetRightAverage: "+xOffsetRightAverage);
            System.out.println("yOffsetUpAverage: "+yOffsetUpAverage);
            System.out.println("yOffsetDownAverage: "+yOffsetDownAverage);
            System.out.println("maxOffset: "+maxOffset);
        }

        if (debug){
            System.out.println("xDebutSelectedZone: "+selectedZone[0][0]);
            System.out.println("yDebutSelectedZone: "+selectedZone[0][1]);
            System.out.println("xFinSelectedZone: "+selectedZone[1][0]);
            System.out.println("yFinSelectedZone: "+selectedZone[1][1]);
        }
        int[] nbDifferringCasesArray={0,0,0,0,0,0,0,0};
        for (int x=maxOffset; x<binaryMatrix.length-maxOffset;x++){
            for (int y=maxOffset; y<binaryMatrix[0].length-maxOffset; y++){
                if (binaryMatrix[x+xOffsetLeftAverage][y+yOffsetUpAverage]!=referenceBinaryMatrix[x][y]){
                    nbDifferringCasesArray[0]+=1;
                }
                if (binaryMatrix[x][y+yOffsetUpAverage]!=referenceBinaryMatrix[x][y]){
                    nbDifferringCasesArray[1]+=1;
                }
                if (binaryMatrix[x+xOffsetRightAverage][y+yOffsetUpAverage]!=referenceBinaryMatrix[x][y]){
                    nbDifferringCasesArray[2]+=1;
                }
                if (binaryMatrix[x+xOffsetLeftAverage][y]!=referenceBinaryMatrix[x][y]){
                    nbDifferringCasesArray[3]+=1;
                }
                if (binaryMatrix[x+xOffsetRightAverage][y]!=referenceBinaryMatrix[x][y]){
                    nbDifferringCasesArray[4]+=1;
                }
                if (binaryMatrix[x+xOffsetLeftAverage][y+yOffsetDownAverage]!=referenceBinaryMatrix[x][y]){
                    nbDifferringCasesArray[5]+=1;
                }
                if (binaryMatrix[x][y+yOffsetDownAverage]!=referenceBinaryMatrix[x][y]){
                    nbDifferringCasesArray[6]+=1;
                }
                if (binaryMatrix[x][y+yOffsetDownAverage]!=referenceBinaryMatrix[x][y]){
                    nbDifferringCasesArray[7]+=1;
                }
            }
        }
        int minDiffIndice=0;
        for (int i=0; i<8; i++){
            if (debug){
                System.out.println("Position n°"+i+":"+nbDifferringCasesArray[i]+" pixels différents");
            }
            if (nbDifferringCasesArray[minDiffIndice]>nbDifferringCasesArray[i]){
                minDiffIndice=i;
            }
        }
        if (debug) {
            System.out.println("minDiffIndice:" + minDiffIndice);
        }
        int xOffset=0;
        int yOffset=0;
        if (minDiffIndice==0){
            xOffset=xOffsetLeftAverage;
            yOffset=yOffsetUpAverage;
        }
        else if(minDiffIndice==1){
            xOffset=0;
            yOffset=yOffsetUpAverage;
        }
        else if(minDiffIndice==2){
            xOffset=xOffsetRightAverage;
            yOffset=yOffsetUpAverage;
        }
        else if(minDiffIndice==3){
            xOffset=xOffsetLeftAverage;
            yOffset=0;
        }
        else if(minDiffIndice==4){
            xOffset=xOffsetRightAverage;
            yOffset=0;
        }
        else if(minDiffIndice==5){
            xOffset=xOffsetLeftAverage;
            yOffset=yOffsetDownAverage;
        }
        else if(minDiffIndice==6){
            xOffset=0;
            yOffset=yOffsetDownAverage;
        }
        else if(minDiffIndice==7){
            xOffset=xOffsetRightAverage;
            yOffset=yOffsetDownAverage;
        }
        int[] offset={xOffset,yOffset};
        return offset;
    }

    //Main
    public static void process(int[][][] colorMatrix, int[][][] referenceFilteredColorMatrix, int[][] selectedZone, int[][] positionsColorsOnImage){
        int validPointColorSeuil=50;
        long timeStart=0;
        if (debug) {
            timeStart = System.currentTimeMillis();
        }

        int[][][] highPassedReferenceMatrix=highPassingFilter(colorMatrix,selectedZone,validPointColorSeuil);
        highPassedReferenceMatrix=normaliseOver255(highPassedReferenceMatrix);
        int[][] greyReferenceMatrix=toGreyMatrix(highPassedReferenceMatrix);
        int[][] binaryGreyReferenceMatrix = binarize(greyReferenceMatrix,validPointColorSeuil);


        int[][][] highPassedMatrix=highPassingFilter(colorMatrix,selectedZone,validPointColorSeuil);
        highPassedMatrix=normaliseOver255(highPassedMatrix);
        int[][] greyMatrix=toGreyMatrix(highPassedMatrix);
        int[][] binaryGreyMatrix = binarize(greyMatrix,validPointColorSeuil);
        int[] offset = checkDetectionZonesWithOffset(binaryGreyMatrix, binaryGreyReferenceMatrix, selectedZone, positionsColorsOnImage);
        if (debug){
            long timeEnd=System.currentTimeMillis();
            System.out.println("Time : "+(timeEnd-timeStart));
            System.out.println("Offset : ("+offset[0]+","+offset[1]+")");
        }
        saveHighPassedImage(binaryGreyMatrix);
    }

    public static void setDebugHighPassFilter(boolean value) {
        debug = value;
    }
}
