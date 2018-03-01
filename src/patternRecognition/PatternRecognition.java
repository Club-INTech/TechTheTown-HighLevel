package patternRecognition;

import enums.Colors;
import enums.Patterns;
import enums.ConfigInfoRobot;
import patternRecognition.shootPicture.ShootBufferedStill;
import pfg.config.Config;
import robot.EthWrapper;
import threads.AbstractThread;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;



/** Classe permettant de faire la reconnaissance de patterns
 * @author Nayht
 */
public class PatternRecognition extends AbstractThread{

    public void setDebugPatternRecognition(boolean value){
        debug=value;
    }

    //////////////////////////////////// GLOBAL VARIABLES DEFINITION /////////////////////////////////////////////

    //Défini l'objet mediansList pour qu'il soit accessible sans être retourné
    private static int[][] mediansList = new int[3][3];
    //mediansList est composé de la médiane en R, en G et en B, pour chacune des 3 couleurs de la photo
    //Donc, si on nomme les couleurs 1, 2 et 3, on a :
    //mediansList={{R1,G1,B1},{R2,G2,B2},{R3,G3,B3}}
    //Cette valeur, une fois calculée, reste inchangée tout au long de l'exécution du programme

    //////////////////////////////////// NORMALIZATION FUNCTIONS /////////////////////////////////////////////

    /**Fonction de normalisation d'une liste de doubles
     * @param list liste de doubles à normaliser
     * @return renvoie une liste normalisée*/
    private double[] normalizeDoubleList(double[] list){
        //On calcule la somme des valeurs de tous les éléments de la liste d'entrée
        double sumAllValues=0;
        int length=list.length;
        for (double value : list){
            sumAllValues+=value;
        }
        //Si chaque valeur de la liste d'entrée est 0, on prend la somme de toutes les valeurs comme égale à 0.1
        //(afin de ne pas diviser pas 0 lors de la normalisation)
        if (sumAllValues==0){
            sumAllValues=0.1;
        }
        //Normalisation
        double[] normalizedList = new double[length];
        for (int i=0; i<length; i++){
            normalizedList[i]=list[i]/sumAllValues;
        }
        //Renvoi de la liste normalisée
        return normalizedList;
    }

    //////////////////////////////////// IMAGE ANALYSIS /////////////////////////////////////////////

    /**Fonction de conversion d'une image en une matrice de couleurs
     * @param pathname chemin vers l'image à convertir
     * @return renvoie une matrice 3D matrice[x][y][0,1 ou 2], sachant que
     * x est l'abscisse,
     * y est l'ordonnée,
     * 0,1 ou 2, si on veut R, G ou B**/
    private int[][][] createColorMatrix(String pathname) {
        int[][][] colorMatrix;
        BufferedImage picture;
        try {
            picture = ImageIO.read(new File(pathname));
        } catch (IOException e) {
            try {
                String pathnameBis="Photos/"+pathname;
                picture = ImageIO.read(new File(pathnameBis));
            }
            catch (IOException e2){
                try {
                    String pathnameTris = "images/patternRecognition/" + pathname;
                    picture = ImageIO.read(new File(pathnameTris));
                }
                catch (IOException e3) {
                    picture = new BufferedImage(2592, 1944, 1);
                    System.out.println("Image not found");
                }
            }
        }
        int width = picture.getWidth();
        int height = picture.getHeight();
        colorMatrix = new int[width][height][3];
        //Méthode pour récupérer les paramètres R, G et B de chaque pixel
        for (int x=0; x<width-1; x++){
            for (int y=0; y<height-1; y++) {
                Color color =  new Color(picture.getRGB(x, y));
                colorMatrix[x][y][0]=color.getRed();
                colorMatrix[x][y][1]=color.getGreen();
                colorMatrix[x][y][2]=color.getBlue();
                }
            }
        return colorMatrix;
    }

    /**Fonction de conversion d'une image en une matrice de couleurs
     * @param picture BufferedImage de l'image à convertir
     * @return renvoie une matrice 3D matrice[x][y][0,1 ou 2], sachant que
     * x est l'abscisse,
     * y est l'ordonnée,
     * 0,1 ou 2, si on veut R, G ou B**/
    private int[][][] createColorMatrixFromBufferedImage(BufferedImage picture) {
        int[][][] colorMatrix;
        int width = picture.getWidth();
        int height = picture.getHeight();
        colorMatrix = new int[width][height][3];
        //Méthode pour récupérer les paramètres R, G et B de chaque pixel
        for (int x=0; x<width-1; x++){
            for (int y=0; y<height-1; y++) {
                Color color =  new Color(picture.getRGB(x, y));
                colorMatrix[x][y][0]=color.getRed();
                colorMatrix[x][y][1]=color.getGreen();
                colorMatrix[x][y][2]=color.getBlue();
            }
        }
        return colorMatrix;
    }




    /** Fonction permettant d'obtenir la valeur médiane de R, G OU B pour une des 3 couleurs de la photo
     *  Sélectionne la couleur en créant un rectangle par ses sommets opposés :
     *  -------------------------
     *  |A                      |
     *  |                       |
     *  |                       |
     *  |                       |
     *  |                      B|
     *  -------------------------
     *  A de coordonnées (xstart, ystart)
     *  B de coordonnées (xend, yend)
     * @param colorMatrix image sous forme de matrice 3D (cf createColorMatrix)
     * @param xstart abscisse de A (cf graphique docstring)
     * @param ystart ordonnée de A (cf graphique docstring)
     * @param xend abscisse de B (cf graphique docstring)
     * @param yend ordonnée de B (cf graphique docstring)
     * @param posRGB choix de la couleur à déterminer : 0, 1 ou 2 pour R, G ou B
     * @return renvoie la valeur (int) médiane de R, G ou B de la couleur choisie
     */
    private int getMedianValue(int[][][] colorMatrix, int xstart, int ystart, int xend, int yend, int posRGB) {
        int width = xend - xstart;
        int height = yend - ystart;
        int[] listAllPoints = new int[width*height];
        for (int i = 0; i < width * height; i++){
            int a=i%width + xstart;
            int b=i/width + ystart;
            if (a<0){
                a=1;
            }
            else if (a>=this.imageWidth){
                a=this.imageWidth-1;
            }
            if (b<0){
                b=1;
            }
            else if (b>=this.imageHeight){
                b=this.imageHeight-1;
            }
            listAllPoints[i] = colorMatrix[a][b][posRGB];
        }
        java.util.Arrays.sort(listAllPoints);
        int len_list=listAllPoints.length;
        if (len_list==0){
            return -1;
        }
        else {
            int medianValue = listAllPoints[len_list / 2];
            return medianValue;
        }
    }

    /** Fonction permettant d'obtenir la valeur médiane de R, G ET B pour une des 3 couleurs de la photo
     *  Sélectionne la couleur en créant un rectangle par ses sommets opposés :
     *  -------------------------
     *  |A                      |
     *  |                       |
     *  |                      B|
     *  -------------------------
     *  A de coordonnées (xstart, ystart)
     *  B de coordonnées (xend, yend)
     * @param colorMatrix image sous forme de matrice 3D (cf createColorMatrix)
     * @param xstart abscisse de A (cf graphique docstring)
     * @param ystart ordonnée de A (cf graphique docstring)
     * @param xend abscisse de B (cf graphique docstring)
     * @param yend ordonnée de B (cf graphique docstring)
     * @return renvoie la liste de valeurs (int[3]) R,G et B de la couleur choisie
     */
    private int[] getRGBMedianValues(int[][][] colorMatrix, int xstart, int ystart, int xend, int yend){
        int[] mediansList= new int[3];
        for (int i=0; i<3; i++){
            mediansList[i]=getMedianValue(colorMatrix, xstart, ystart, xend, yend, i);
            if (mediansList[i]==-1){
                int[] badReturn={-1,-1,-1};
                return badReturn;
            }
            if (debug) {
                System.out.println("posRGB:"+i+" MedianValue:"+mediansList[i]);
            }
        }
        if (debug){
            System.out.println("");
        }
        return mediansList;
    }

    //////////////////////////////////// COLOR COMPARISON /////////////////////////////////////////////

    /**Fonction permettant de calculer l'inverse de la distance quadratique d'une couleur (RGBToEvaluate) à une autre prédéfinie (color)
     * @param RGBToEvaluate RGB correspondant à la couleur à évaluer, ie couleur dont la distance doit être calculée
     * @param color RGB correspondant à une couleur prédéfinie
     * @return renvoie l'inverse de la distance quadratique (double) entre les 2 couleurs
     */
    private double computeInverseDistanceToSingleColor(int[] RGBToEvaluate, int[] color){
        double distance = 0;
        for (int i=0; i<3; i++){
            distance+=Math.pow(RGBToEvaluate[i]-color[i],2);
        }
        double inverseDistance;
        if (distance==0){
            inverseDistance = 5;
        }
        else {
            inverseDistance = 1/distance;
        }
        return inverseDistance;
    }

    /**Fonction permettant de calculer l'inverse de la distance quadratique d'une couleur (RGBToEvaluate) à une autre prédéfinie (color)
     * @param RGBToEvaluate RGB correspondant à la couleur à évaluer, ie couleur dont la distance doit être calculée
     * @param color RGB correspondant à une couleur prédéfinie
     * @return renvoie l'inverse de la distance quadratique (double) entre les 2 couleurs
     */
    public static double computeDistancesToColor(int[] RGBToEvaluate, int[] color){
        double distance = 0;
        for (int i=0; i<3; i++){
            distance+=Math.pow(RGBToEvaluate[i]-color[i],2);
        }
        double inverseDistance;
        if (distance==0){
            inverseDistance = 5;
        }
        else {
            inverseDistance = 1/distance;
        }
        return inverseDistance;
    }


    /** Fonction permettant de calculer l'inversee de la distance quadratique d'une couleur (RGBToEvaluate) à toutes les autres couleurs des cubes
     * @param RGBToEvaluate RGB correspondant à la couleur à évaluer, ie couleur dont la distance doit être calculée
     * @return renvoie la liste (double[5]) de l'inverse des distances quadratiques entre la couleur à évaluer et toutes les autres couleus des cubes
     */
    private double[] computeInverseDistanceToAllColors(int[] RGBToEvaluate){
        double[] inverseDistances=new double[5];
        for (int idColor=0; idColor<5; idColor++){
            int[] color= Colors.getRGBFromID(idColor);
            inverseDistances[idColor]=computeInverseDistanceToSingleColor(RGBToEvaluate, color);
        }
        return inverseDistances;
    }

    /**Fonction permettant de calculer les probabilités que la couleur à évaluer(RGBToEvaluate) corresponde à celles prédéfinies
     * @param RGBToEvaluate RGB correspondant à la oculeur à évaluer, ie couleur dont la distance doit être calculée
     * @return renvoie la liste (double[]) des probabilités que la couleur à évaluer corresponde à celles prédéfinies
     */
    private double[] computeProbabilitiesColors(int[] RGBToEvaluate){
        double[] inverseDistances=computeInverseDistanceToAllColors(RGBToEvaluate);
        double[] normalizedInverseDistances = normalizeDoubleList(inverseDistances);

        if (debug && !alreadyPrintedColorMatchingProba) {
            for (int i = 0; i < normalizedInverseDistances.length; i++) {
                System.out.println("Proba corresponde a la couleur "+Colors.getNameFromID(i)+":\t"+normalizedInverseDistances[i]);
            }
            System.out.println("");
        }
        return normalizedInverseDistances;
    }

    //////////////////////////////////// PATTERN COMPARISON /////////////////////////////////////////////

    /**Fonction permettant de comparer les valeurs de RGB des 3 couleurs de la photo à un pattern prédéfini
     * @param RGBs valeurs de RGB des 3 couleurs de la photo
     * @param patternID ID du pattern à évaluer
     * @return renvoie la probabilité (double) que les couleurs de la photo correspondent au pattern choisi
     */
    private double compareThreeRGBsToPattern(int[][] RGBs, int patternID){
        Colors[] pattern=Patterns.getPatternFromID(patternID);
        double finalProbability = 1;
        for (int i=0; i<3; i++){
            int colorID=pattern[i].getID();
            if (debug && !alreadyPrintedColorMatchingProba){
                if (i==0) {
                    System.out.println("First color :");
                }
                else if (i==1){
                    System.out.println("Second color :");
                }
                else if (i==2){
                    System.out.println("Third color :");
                }
            }
            double[] tempProbabilities=computeProbabilitiesColors(RGBs[i]);
            finalProbability*=tempProbabilities[colorID];
        }
        alreadyPrintedColorMatchingProba=true;
        return finalProbability;
    }


    /**Fonction permettant de comparer les valeurs de RGB des 3 couleurs de la photo à tous les autres patterns
     * @param RGBs valeurs de RGB des 3 couleurs de la photo
     * @return renvoie la liste (double[nbPatterns=10]) des probabilités que les couleurs de la photo correspondent à un des pattern
     */
    private double[] compareThreeRGBsToAllPatterns(int[][] RGBs){
        int nbPatterns=10;
        double[] probabilitiesList= new double[nbPatterns];
        for (int i=0; i<nbPatterns; i++){
            probabilitiesList[i]=compareThreeRGBsToPattern(RGBs, i);
        }
        return probabilitiesList;
    }

    //////////////////////////////////// ENCAPSULATION 3,4 AND 5 /////////////////////////////////////////////

    /**Fonction servant d'encapsulation aux fonctions à enchaîner
     * @param colorMatrix matrice 3D de la photo
     * @param xstarts liste des abscisses des points A (cf graphique docstring fonction GetRGBMedianValues)
     * @param ystarts liste des ordonnées des points A (cf graphique docstring fonction GetRGBMedianValues)
     * @param xends liste des abscisses des points B (cf graphique docstring fonction GetRGBMedianValues)
     * @param yends liste des ordonnées des points B (cf graphique docstring fonction GetRGBMedianValues)
     * @return renvoe la liste normalisée des probabilités que la pattern pris en photo soit un des patterns de la liste
     * (correspondance entre les indices de la liste renvoyée et l'ID des patterns)
     */
    private double[] computeProximity(int[][][] colorMatrix, int[] xstarts, int[] ystarts, int[] xends, int[] yends) {
        if (debug){
            System.out.println("First color");
        }
        int[] medianFirstColor = getRGBMedianValues(colorMatrix, xstarts[0], ystarts[0], xends[0], yends[0]);
        if (debug){
            System.out.println("Second color");
        }
        int[] medianSecondColor = getRGBMedianValues(colorMatrix, xstarts[1], ystarts[1], xends[1], yends[1]);
        if (debug){
            System.out.println("Third color");
        }
        int[] medianThirdColor = getRGBMedianValues(colorMatrix, xstarts[2], ystarts[2], xends[2], yends[2]);
        int[][] mediansListTemp = {medianFirstColor, medianSecondColor, medianThirdColor};
        int[] badResult={-1,-1,-1};
        for (int i=0; i<3; i++) {
            if(mediansListTemp[i]==badResult){
                double[] badResultProbabilities={-1,-1,-1,-1,-1,-1,-1,-1,-1};
                return badResultProbabilities;
            }
            mediansList[i] = mediansListTemp[i];
        }
        double[] probabilitiesList = compareThreeRGBsToAllPatterns(mediansList);
        return probabilitiesList;
    }
    private double[] computeProximity(int[][][] colorMatrix, int[][] positionsColorsOnImage){
        double[] probabilitiesList = computeProximity(colorMatrix, positionsColorsOnImage[0], positionsColorsOnImage[1], positionsColorsOnImage[2], positionsColorsOnImage[3]);
        return probabilitiesList;
    }

    //////////////////////////////////// DISCRIMINATION /////////////////////////////////////////////

    /**Fonction permettant de déterminer les patterns qui sont viables au vu de la liste de probabilités
     * @param probabilitiesList liste normalisée de probabilités que le pattern pris en photo corresponde aux patterns prédéfinis
     * @return renvoie la liste des indices des patterns plausibles
     */
    private ArrayList<Integer> selectBestProbabilities(double[] probabilitiesList){
        int length=probabilitiesList.length;
        double bestProba=0;
        for (int i=0; i<length; i++){
            if (probabilitiesList[i]>bestProba){
                bestProba=probabilitiesList[i];
            }
        }
        ArrayList<Integer> selectionnedProbabilitiesIndice=new ArrayList<>();
        for (int i=0; i<length; i++){
            if (bestProba/probabilitiesList[i]<3){
                selectionnedProbabilitiesIndice.add(i);
            }
        }
        return selectionnedProbabilitiesIndice;
    }

    //////////////////////////////////// DISCRIMINATION IN CASE OF CONFLICT /////////////////////////////////////////////
    private int[] convertHSBtoRGB(float hue, float saturation, float brightness){
        int r = 0, g = 0, b = 0;
        if (saturation == 0) {
            r = g = b = (int) (brightness * 255.0f + 0.5f);
        } else {
            float h = (hue - (float) Math.floor(hue)) * 6.0f;
            float f = h - (float) java.lang.Math.floor(h);
            float p = brightness * (1.0f - saturation);
            float q = brightness * (1.0f - saturation * f);
            float t = brightness * (1.0f - (saturation * (1.0f - f)));
            switch ((int) h) {
                case 0:
                    r = (int) (brightness * 255.0f + 0.5f);
                    g = (int) (t * 255.0f + 0.5f);
                    b = (int) (p * 255.0f + 0.5f);
                    break;
                case 1:
                    r = (int) (q * 255.0f + 0.5f);
                    g = (int) (brightness * 255.0f + 0.5f);
                    b = (int) (p * 255.0f + 0.5f);
                    break;
                case 2:
                    r = (int) (p * 255.0f + 0.5f);
                    g = (int) (brightness * 255.0f + 0.5f);
                    b = (int) (t * 255.0f + 0.5f);
                    break;
                case 3:
                    r = (int) (p * 255.0f + 0.5f);
                    g = (int) (q * 255.0f + 0.5f);
                    b = (int) (brightness * 255.0f + 0.5f);
                    break;
                case 4:
                    r = (int) (t * 255.0f + 0.5f);
                    g = (int) (p * 255.0f + 0.5f);
                    b = (int) (brightness * 255.0f + 0.5f);
                    break;
                case 5:
                    r = (int) (brightness * 255.0f + 0.5f);
                    g = (int) (p * 255.0f + 0.5f);
                    b = (int) (q * 255.0f + 0.5f);
                    break;
            }
        }
        int[] RGB={r,g,b};
        return RGB;
    }

    private float[] convertRGBtoHSB(int R, int G, int B){
        float[] HSB = new float[3];
        Color.RGBtoHSB(R,G,B,HSB);
        return HSB;
    }

    private int[][][] lightUpSector(int[][][] colorMatrixLitUp, int xstart, int ystart, int xend, int yend, double saturationModifier, double brightnessModifier){
        for (int x=xstart; x<xend; x++){
            for (int y=ystart; y<yend; y++){
                    int[] RGB = colorMatrixLitUp[x][y];
                    float[] HSB=convertRGBtoHSB(RGB[0],RGB[1],RGB[2]);

                    //Improve saturation
                    HSB[1]*=saturationModifier;
                    if (HSB[1]>1){
                        HSB[1]=1;
                    }
                    //Improve brightness
                    HSB[2]*=brightnessModifier;
                    if (HSB[2]>1){
                        HSB[2]=1;
                    }
                    RGB=convertHSBtoRGB(HSB[0],HSB[1],HSB[2]);
                colorMatrixLitUp[x][y]=RGB;
            }
        }
        return colorMatrixLitUp;
    }

    private int[][][] lightUpColors(int[][][] colorMatrix, int[] xstarts, int[] ystarts, int[] xends, int[] yends){
        int[][][] colorMatrixLitUp=colorMatrix.clone();
        for (int i=0; i<xstarts.length; i++){
            colorMatrixLitUp=lightUpSector(colorMatrixLitUp, xstarts[i], ystarts[i], xends[i], yends[i],1,1.5);
        }
        return colorMatrixLitUp;
    }

    //////////////////////////////////// COMMENTAIRES DEBUG /////////////////////////////////////////////

    //MODE OPERATOIRE :
    // 1) Créer la matrice RGB
    // 2) Délimiter l'endroit où se trouve le pattern
    // 2.5) Délimiter les zones de couleur dans le pattern
    // 3) Déterminer les médianes R,G et B pour chaque zone de couleur
    // 4) Comparer les valeurs RGB obtenues pour chaque zone avec tous les patterns
    // 5) Déterminer le pattern ayant la probabilité la plus grande
    // 5.5) Si doute, détermination plus précise

    //GIMP TRAITEMENT :
    //Ajout Luminosité/Contraste : indices 30/30

    //////////////////////////////////// ENREGISTREMENT D'UNE PHOTO ///////////////////////////////////

    public static void saveImage(int[][][] colorMatrix, String name){
            BufferedImage image = new BufferedImage(colorMatrix.length,colorMatrix[0].length,BufferedImage.TYPE_INT_RGB);
            for (int x=0; x<colorMatrix.length-1; x++){
                for (int y=0; y<colorMatrix[0].length-1; y++){
                    Color color = new Color(colorMatrix[x][y][0],colorMatrix[x][y][1],colorMatrix[x][y][2]);
                    image.setRGB(x,y,color.getRGB());
                }
            }
            File outputfile = new File(name);
            try {
                ImageIO.write(image, "png", outputfile);
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    //////////////////////////////////// ANALYSE DE PATTERN /////////////////////////////////////////////

    /**Méthode permettant de faire la reconnaissance de pattenrs
     * @return l'id du pattern (int de 0 à 9, bornes comprises)
     */
    private int analysePattern(int[][][] colorMatrix) {
        double[][] distanceArrays = new double[5][10];
        int halfLengthSideOfSquareDetection = lengthSideOfSquareDetection / 2;
        int halfDistanceBetweenTwoColors = distanceBetweenTwoColors / 2;
        if (!(this.alreadyPreModified)){
            if (debug){
                System.out.println("Modification");
            }
            colorMatrix=lightUpSector(colorMatrix,zoneToPerformLocalisation[0],zoneToPerformLocalisation[1],zoneToPerformLocalisation[0]+zoneToPerformLocalisation[2],zoneToPerformLocalisation[1]+zoneToPerformLocalisation[3],saturationPreModifier,brightnessPreModifier);
            this.alreadyPreModified=true;
        }

        int iStartValue = -2;
        for (int i = iStartValue; i <= 2; i++) {
            /**On définit où l'algorithme doit chercher ses couleurs
             * positionColorsOnImage=
             * {
             * {xStartFirstColor,xStartSecondColor,xStartThirdColor},
             * {yStartFirstColor,yStartSecondColor,yStartThirdColor},
             * {xEndFirstColor,xEndSecondColor,xEndThirdColor},
             * {yEndFirstColor,yEndSecondColor,yEndThirdColor},
             * }
             */
            int imageWidthMinusOne=imageWidth-1;
            int imageHeightMinusOne=imageHeight-1;
            positionsColorsOnImage = new int[][]
                    {{Math.max(Math.min(centerPointPattern[0] - halfLengthSideOfSquareDetection - distanceBetweenTwoColors + i * halfDistanceBetweenTwoColors, imageWidthMinusOne),0),
                      Math.max(Math.min(centerPointPattern[0] - halfLengthSideOfSquareDetection + i * halfDistanceBetweenTwoColors, imageWidthMinusOne),0),
                      Math.max(Math.min(centerPointPattern[0] - halfLengthSideOfSquareDetection + distanceBetweenTwoColors + i * halfDistanceBetweenTwoColors, imageWidthMinusOne),0)},
                     {Math.max(centerPointPattern[1] - halfLengthSideOfSquareDetection,0),
                      Math.max(centerPointPattern[1] - halfLengthSideOfSquareDetection,0),
                      Math.max(centerPointPattern[1] - halfLengthSideOfSquareDetection,0)},
                     {Math.max(Math.min(centerPointPattern[0] + halfLengthSideOfSquareDetection - distanceBetweenTwoColors + i * halfDistanceBetweenTwoColors, imageWidthMinusOne),0),
                      Math.max(Math.min(centerPointPattern[0] + halfLengthSideOfSquareDetection + i * halfDistanceBetweenTwoColors, imageWidthMinusOne),0),
                      Math.max(Math.min(centerPointPattern[0] + halfLengthSideOfSquareDetection + distanceBetweenTwoColors + i * halfDistanceBetweenTwoColors, imageWidthMinusOne),0)},
                     {Math.min(centerPointPattern[1] + halfLengthSideOfSquareDetection,imageHeightMinusOne),
                      Math.min(centerPointPattern[1] + halfLengthSideOfSquareDetection,imageHeightMinusOne),
                      Math.min(centerPointPattern[1] + halfLengthSideOfSquareDetection,imageHeightMinusOne)}};
            distanceArrays[i - iStartValue] = computeProximity(colorMatrix, positionsColorsOnImage);
        }
        double maxProba = 0;
        int maxI = 0;
        int maxJ = 0;
        double[] badDistanceArray={-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};
        for (int i = 0; i < distanceArrays.length; i++) {
            if (debug) {
                System.out.println("");
                System.out.println("Proximity (Xshifted by " + ((i + iStartValue) * halfDistanceBetweenTwoColors) + " )");
            }
            if (distanceArrays[i]==badDistanceArray){
                for (int j=0; j<distanceArrays[i].length; j++){
                    distanceArrays[i][j]=0;
                }
            }
            else
            for (int j = 0; j < distanceArrays[0].length; j++) {
                if (distanceArrays[i][j] > maxProba) {
                    maxProba = distanceArrays[i][j];
                    maxI = i;
                    maxJ = j;
                }
                if (debug) {
                    System.out.println(distanceArrays[i][j]);
                }
            }
        }
        if (maxProba<0.3){
            if (alreadyLitUp<2){
                alreadyLitUp+=1;
                if (debug){
                    System.out.println("///////////////////////////////////////////// LIGHTING UP IMAGE /////////////////////////////////////////////////////");
                }
                colorMatrix=lightUpSector(colorMatrix,zoneToPerformLocalisation[0],zoneToPerformLocalisation[1],zoneToPerformLocalisation[0]+zoneToPerformLocalisation[2],zoneToPerformLocalisation[1]+zoneToPerformLocalisation[3],1.2,1.2);
                if (isSavingImages) {
                    String imageName="images/patternRecognition/500ImagesTest/output"+Double.toString(Math.random())+".png";
                    saveImage(colorMatrix, imageName);
                }
                analysePattern(colorMatrix);
            }
            else{
                this.finalIndice = maxJ;
                return maxJ;
            }
        }
        else{
            this.finalIndice = maxJ;
            return maxJ;
        }
        return -1;
    }

    /////////////////////////////// CALCULATING CENTER OF LOCATED PATTERN ///////////////////////////////

    private int[] calculateCenterPattern(String pathToImage, int[] zoneToPerformLocalisation){
        if (debug){
            System.out.println("PathToImage : "+pathToImage);
            System.out.println("Performing pattern localisation on : (("+zoneToPerformLocalisation[0]+","+zoneToPerformLocalisation[1]+
                    "),("+(zoneToPerformLocalisation[0]+zoneToPerformLocalisation[2])+","+(zoneToPerformLocalisation[1]+zoneToPerformLocalisation[3])+"))");
        }
        String pathToImageLocation="images/patternRecognition/"+pathToImage;
        LocatePattern.setDebug(true);
        int[] patternZone = LocatePattern.locatePattern(pathToImageLocation, zoneToPerformLocalisation);
        int[] centerPattern=new int[]{(patternZone[0]+patternZone[2])/2,(patternZone[1]+patternZone[3])/2};
        if (debug){
            System.out.println("Center found : ("+centerPattern[0]+","+centerPattern[1]+")");
        }
        return centerPattern;
    }



    /////////////////////////// REPLACING THE LOCATED ZONE //////////////////////////

    /*private boolean doCenterHasToBeXShifted(int[][][] colorMatrix, int[] center, int lengthSideOfSquareDetection){
        int[] countingArray=new int[]{0,0,0,0,0};
        for (int x=-lengthSideOfSquareDetection/2; x<lengthSideOfSquareDetection/2; x++){
            double[] RGBprobabilities=computeInverseDistanceToAllColors(colorMatrix[center[0]+x][center[1]]);
            int maxIndice=0;
            double max=0;
            for (int i=0; i<5; i++) {
                if (RGBprobabilities[i] > max) {
                    max = RGBprobabilities[i];
                    maxIndice = i;
                }
            }
            countingArray[maxIndice]+=1;
        }
        int maxSameColorPixels=0;
        for (int i=0; i<countingArray.length; i++){
            if (countingArray[i]>maxSameColorPixels){
                maxSameColorPixels=countingArray[i];
            }
        }
        if (debug){
            System.out.print("ZoneHasToBeShifted CountingColorsArray:(");
            for (int i=0; i<countingArray.length; i++){
                System.out.print(countingArray[i]+",");
            }
            System.out.println(")");
        }
        if (maxSameColorPixels>(0.8*lengthSideOfSquareDetection)){
            return false;
        }
        else{
            return true;
        }
    }*/


    private String pathToImage;
    private int[][] positionsColorsOnImage;
    private int finalIndice=-2;
    private Config config;
    private boolean isShutdown=false;
    private int[] zoneToPerformLocalisation;
    private int lengthSideOfSquareDetection; //in pixels
    private int distanceBetweenTwoColors; //in pixels
    private int[] centerPointPattern;
    private boolean debug;   //Debug
    private boolean alreadyPrintedColorMatchingProba; //Utile pour le debug
    private boolean mustSelectAValidPattern; //Est-ce qu'on peut renvoyer qu'aucun pattern n'a été trouvé ?
    private int alreadyLitUp; //l'image a déjà été éclairée
    private boolean isSavingImages;
    private boolean symmetry;
    private int imageWidth=2592;
    private int imageHeight=1944;
    private int[] zoneToPerformLocalisationVert={1,700,1200,1200};
    private int[] zoneToPerformLocalisationOrange={(imageWidth-1201),700,1200,1200};
    private double saturationPreModifier;
    private double brightnessPreModifier;
    private boolean alreadyPreModified;
    private BufferedImage buffImg;
    private EthWrapper ethWrapper;
    private boolean movinglock=false;
    private boolean recognitionlock=false;

    /** Instanciation du thread de reconnaissance de couleurs
     * @param buffImage image contenue en buffer
     * @param zoneToPerformLocalisation zone dans laquelle la localisation de pattern va devoir se faire.
     */
    public PatternRecognition(Config config,EthWrapper ethWrapper ,BufferedImage buffImage, int[] zoneToPerformLocalisation, double saturationPreModifier, double brightnessPreModifier){
        this.config=config;
        this.pathToImage="BUFFEREDIMAGEDETROMPEUR";
        this.buffImg=buffImage;
        this.symmetry=this.config.getString(ConfigInfoRobot.COULEUR).equals("orange");
        this.ethWrapper=ethWrapper;
        if (!(zoneToPerformLocalisation[0]==0 && zoneToPerformLocalisation[1]==0 && zoneToPerformLocalisation[2]==0 && zoneToPerformLocalisation[3]==0)) {
            this.zoneToPerformLocalisation = zoneToPerformLocalisation;
        }
        else{
            if (this.symmetry) {
                this.zoneToPerformLocalisation = zoneToPerformLocalisationOrange;
            }
            else {
                this.zoneToPerformLocalisation = zoneToPerformLocalisationVert;
            }
        }
        this.lengthSideOfSquareDetection=20; //in pixels
        this.distanceBetweenTwoColors=70; //in pixels
        this.debug=false;
        this.alreadyPrintedColorMatchingProba=false;
        this.mustSelectAValidPattern=false;
        this.alreadyLitUp=0;
        this.isSavingImages=true;
        this.saturationPreModifier=saturationPreModifier;
        this.brightnessPreModifier=brightnessPreModifier;
        this.alreadyPreModified=false;
        this.updateConfig();
    }


    public void run(){
        this.setPriority(5);
        while (ethWrapper.isJumperAbsent()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // puis attend son retrait
        while (!ethWrapper.isJumperAbsent()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //TODO: vérifier si c'est bien cela pour prendre une photo
        ShootBufferedStill.TakeBufferedPicture();
        movinglock=true;
        int[][][] colorMatrix;
        if(this.pathToImage.equals("BUFFEREDIMAGEDETROMPEUR")) {
            colorMatrix = createColorMatrixFromBufferedImage(this.buffImg);
        }
        else{
            colorMatrix = createColorMatrix(this.pathToImage);
        }
        centerPointPattern=calculateCenterPattern(this.pathToImage, this.zoneToPerformLocalisation);
        if (!(centerPointPattern[0] == 0 && centerPointPattern[1] == 0)) {
            analysePattern(colorMatrix);
            //TODO : vérifier si c'est bien la ou la boolean recognitionlock doit passer à true
            recognitionlock=true;
        }
        else{
            this.finalIndice=-1;
        }
        if (debug) {
            System.out.println("Pattern recognized : " + finalIndice);
        }
        while (!this.isShutdown){
            try {
                this.sleep(100);
            } catch (InterruptedException e) {
                log.debug("Le thread a été interrompu");
                e.printStackTrace();
            }
        }
    }

    public int returnFinalIndice(){
        try {
            this.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return this.finalIndice;
    }

    public void shutdown(){
        this.isShutdown=true;
    }

    public boolean isMovinglock() {
        return movinglock;
    }

    public boolean isRecognitionlock() {
        return recognitionlock;
    }

    @Override
    public void updateConfig() {
        Colors.ORANGE.setRGB(config.getInt(ConfigInfoRobot.rorange),config.getInt(ConfigInfoRobot.gorange),config.getInt(ConfigInfoRobot.borange));
        Colors.YELLOW.setRGB(config.getInt(ConfigInfoRobot.rjaune),config.getInt(ConfigInfoRobot.gjaune),config.getInt(ConfigInfoRobot.bjaune));
        Colors.BLUE.setRGB(config.getInt(ConfigInfoRobot.rbleu),config.getInt(ConfigInfoRobot.gbleu),config.getInt(ConfigInfoRobot.bbleu));
        Colors.BLACK.setRGB(config.getInt(ConfigInfoRobot.rnoir),config.getInt(ConfigInfoRobot.gnoir),config.getInt(ConfigInfoRobot.bnoir));
        Colors.GREEN.setRGB(config.getInt(ConfigInfoRobot.rvert),config.getInt(ConfigInfoRobot.gvert),config.getInt(ConfigInfoRobot.bvert));
    }
}