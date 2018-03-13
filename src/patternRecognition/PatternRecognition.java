package patternRecognition;

import enums.Colors;
import enums.Patterns;
import enums.ConfigInfoRobot;
import patternRecognition.shootPicture.ShootBufferedStillWebcam;
import pfg.config.Config;
import robot.EthWrapper;
import strategie.GameState;
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

    private int finalIndice=-2;
    private Config config;
    private EthWrapper ethWrapper;
    private GameState gameState;

    private boolean isShutdown=false;
    private int lengthSideOfSquareDetection; //in pixels
    private int distanceBetweenTwoColors; //in pixels
    private int[] centerPointPattern;
    private boolean debug;   //Debug
    private boolean alreadyPrintedColorMatchingProba; //Utile pour le debug
    private int alreadyLitUp; //l'image a déjà été éclairée
    private boolean isSavingImages;
    private boolean symmetry;
    //VALEURS PICAM
    //private int imageWidth=2592;
    //private int imageHeight=1944;
    private int imageWidth=640;
    private int imageHeight=480;

    //mediansList est composé de la médiane en R, en G et en B, pour chacune des 3 couleurs de la photo
    //Donc, si on nomme les couleurs 1, 2 et 3, on a :
    //mediansList={{R1,G1,B1},{R2,G2,B2},{R3,G3,B3}}
    private int[][] mediansList = new int[3][3];

    private int[] zoneToPerformLocalisation;
    private int[] zoneToPerformLocalisationVert={1,168,296,288};
    private int[] zoneToPerformLocalisationOrange={(imageWidth-297),168,296,288};

    private double saturationPreModifier;
    private double brightnessPreModifier;
    private boolean alreadyPreModified;
    private static boolean movementLocked=true;
    private static boolean recognitionDone=false;

    /** Instanciation du thread de reconnaissance de couleurs
     * @param config passe la config
     * @param ethWrapper passe l'ethWrapper
     */
    public PatternRecognition(Config config, EthWrapper ethWrapper, GameState stateToConsider){
        this.config=config;
        this.updateConfig();
        this.ethWrapper=ethWrapper;
        this.gameState=stateToConsider;
        if (this.symmetry) {
            this.zoneToPerformLocalisation = zoneToPerformLocalisationOrange;
        }
        else {
            this.zoneToPerformLocalisation = zoneToPerformLocalisationVert;
        }
        this.lengthSideOfSquareDetection=5; //in pixels
        this.distanceBetweenTwoColors=40; //in pixels
        this.debug=true;
        this.alreadyPrintedColorMatchingProba=false;
        this.alreadyLitUp=0;
        this.isSavingImages=true;
        this.saturationPreModifier=1.2;
        this.brightnessPreModifier=1;
        this.alreadyPreModified=false;
    }

    //////////////////////////////////// COLOR MATRIX CREATION /////////////////////////////////////////////

    /**Fonction de conversion d'une image en une matrice de couleurs
     * @param picture BufferedImage de l'image à convertir
     * @return renvoie une matrice 3D matrice[x][y][0,1 ou 2], sachant que
     * x est l'abscisse,
     * y est l'ordonnée,
     * 0,1 ou 2, si on veut R, G ou B
     **/
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

    //////////////////////////////////// GETTING MEDIANS => COLOR COMPARISON => PATTERN COMPARISON /////////////////////////////////////////////

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
            log.debug("First color");
        }
        int[] medianFirstColor = getRGBMedianValues(colorMatrix, xstarts[0], ystarts[0], xends[0], yends[0]);
        if (debug){
            log.debug("Second color");
        }
        int[] medianSecondColor = getRGBMedianValues(colorMatrix, xstarts[1], ystarts[1], xends[1], yends[1]);
        if (debug){
            log.debug("Third color");
        }
        int[] medianThirdColor = getRGBMedianValues(colorMatrix, xstarts[2], ystarts[2], xends[2], yends[2]);
        int[][] mediansListTemp = {medianFirstColor, medianSecondColor, medianThirdColor};
        int[] badResult={-1,-1,-1};
        for (int i=0; i<3; i++) {
            if(mediansListTemp[i]==badResult){
                double[] badResultProbabilities={-1,-1,-1,-1,-1,-1,-1,-1,-1};
                return badResultProbabilities;
            }
            this.mediansList[i] = mediansListTemp[i];
        }
        double[] probabilitiesList = compareThreeRGBsToAllPatterns(this.mediansList);
        return probabilitiesList;
    }
    private double[] computeProximity(int[][][] colorMatrix, int[][] positionsColorsOnImage){
        double[] probabilitiesList = computeProximity(colorMatrix, positionsColorsOnImage[0], positionsColorsOnImage[1], positionsColorsOnImage[2], positionsColorsOnImage[3]);
        return probabilitiesList;
    }

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

    //////////////////////////////////// PATTERN COMPARISON /////////////////////////////////////////////

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
                    log.debug("First color :");
                }
                else if (i==1){
                    log.debug("Second color :");
                }
                else{
                    log.debug("Third color :");
                }
            }
            double[] tempProbabilities=computeProbabilitiesColors(RGBs[i]);
            finalProbability*=tempProbabilities[colorID];
        }
        alreadyPrintedColorMatchingProba=true;
        return finalProbability;
    }

    //////////////////////////////////// COLOR COMPARISON /////////////////////////////////////////////

    /**Fonction permettant de calculer les probabilités que la couleur à évaluer(RGBToEvaluate) corresponde à celles prédéfinies
     * @param RGBToEvaluate RGB correspondant à la oculeur à évaluer, ie couleur dont la distance doit être calculée
     * @return renvoie la liste (double[]) des probabilités que la couleur à évaluer corresponde à celles prédéfinies
     */
    private double[] computeProbabilitiesColors(int[] RGBToEvaluate){
        double[] inverseDistances=computeInverseDistanceToAllColors(RGBToEvaluate);
        double[] normalizedInverseDistances = normalizeDoubleList(inverseDistances);

        if (debug && !alreadyPrintedColorMatchingProba) {
            for (int i = 0; i < normalizedInverseDistances.length; i++) {
                log.debug("Proba corresponde a la couleur "+Colors.getNameFromID(i)+":\t"+normalizedInverseDistances[i]);
            }
            log.debug("");
        }
        return normalizedInverseDistances;
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

    //////////////////////////////////// GETTING MEDIAN VALUES /////////////////////////////////////////////

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
        int[] mediansListToReturn= new int[3];
        for (int i=0; i<3; i++){
            mediansListToReturn[i]=getMedianValue(colorMatrix, xstart, ystart, xend, yend, i);
            if (mediansListToReturn[i]==-1){
                int[] badReturn={-1,-1,-1};
                return badReturn;
            }
            if (debug) {
                log.debug("posRGB:"+i+" MedianValue:"+mediansListToReturn[i]);
            }
        }
        if (debug){
            log.debug("");
        }
        return mediansListToReturn;
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

    /** Convertit une couleur en HSB à partir d'une couleur en RGB
     * @param hue angle de la couleur (rouge, orange, jaune, vert, bleu, violet...)
     * @param saturation saturation de la couleur (coloré ou décoloré)
     * @param brightness luminosité de la couleur (éclairé ou sombre)
     * @return renvoie une couleur en HSB
     */
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

    /** Convertit une couleur RGB en HSB
     * @param R composante rouge (R)
     * @param G composante verte (G)
     * @param B composante bleue (B)
     * @return renvoie la couleur HSB
     */
    private float[] convertRGBtoHSB(int R, int G, int B){
        float[] HSB = new float[3];
        Color.RGBtoHSB(R,G,B,HSB);
        return HSB;
    }

    /** Permet d'augmenter la luminosité et la saturation d'une zone de l'image
     * @param colorMatrixLitUp matrice de couleurs
     * @param xstart début x de la zone
     * @param ystart début y de la zone
     * @param xend fin x de la zone
     * @param yend fin y de la zone
     * @param saturationModifier multiplicateur de saturation
     * @param brightnessModifier multiplicateur de luminosité
     * @return la matrice de couleurs modifiée
     */
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

    //////////////////////////////////// ENREGISTREMENT D'UNE PHOTO ///////////////////////////////////

    /** Sauvegarde d'une image sur la carte SD
     * @param colorMatrix matrice de couleur
     * @param path chemin de l'image
     */
    private static void saveImage(int[][][] colorMatrix, String path){
            BufferedImage image = new BufferedImage(colorMatrix.length,colorMatrix[0].length,BufferedImage.TYPE_INT_RGB);
            for (int x=0; x<colorMatrix.length-1; x++){
                for (int y=0; y<colorMatrix[0].length-1; y++){
                    Color color = new Color(colorMatrix[x][y][0],colorMatrix[x][y][1],colorMatrix[x][y][2]);
                    image.setRGB(x,y,color.getRGB());
                }
            }
            File outputfile = new File(path);
            try {
                ImageIO.write(image, "png", outputfile);
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    /////////////////////////////// CALCULATE CENTER OF PATTERN ///////////////////////////////

    /** Localise le pattern et calcule son centre
     * @param buffImg BufferedImage de l'image pour laquelle le pattern doit être localisé
     * @param zoneToPerformLocalisation zone dans laquelle le pattern doit se trouver sur l'image
     * @return renvoie les coordonnée {x,y} du center de l'image
     */
    private int[] calculateCenterPattern(BufferedImage buffImg, int[] zoneToPerformLocalisation){
        if (debug){
            log.debug("Performing pattern localisation on : (("+zoneToPerformLocalisation[0]+","+zoneToPerformLocalisation[1]+
                    "),("+(zoneToPerformLocalisation[0]+zoneToPerformLocalisation[2])+","+(zoneToPerformLocalisation[1]+zoneToPerformLocalisation[3])+"))");
            LocatePattern.setDebug(true);
        }
        log.debug("41");
        int[] patternZone = LocatePattern.locatePattern(buffImg, zoneToPerformLocalisation);
        log.debug("42");
        int[] centerPattern=new int[]{(patternZone[0]+patternZone[2])/2,(patternZone[1]+patternZone[3])/2};
        if (debug){
            log.debug("Center found : ("+centerPattern[0]+","+centerPattern[1]+")");
        }
        return centerPattern;
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
                log.debug("Modification");
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
            int[][] positionsColorsOnImage = new int[][]
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
        int maxJ = 0;
        double[] badDistanceArray={-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};
        for (int i = 0; i < distanceArrays.length; i++) {
            if (debug) {
                log.debug("");
                log.debug("Proximity (Xshifted by " + ((i + iStartValue) * halfDistanceBetweenTwoColors) + " )");
            }
            if (distanceArrays[i]==badDistanceArray){
                for (int j=0; j<distanceArrays[i].length; j++){
                    distanceArrays[i][j]=0;
                }
            }
            else {
                for (int j = 0; j < distanceArrays[0].length; j++) {
                    if (distanceArrays[i][j] > maxProba) {
                        maxProba = distanceArrays[i][j];
                        maxJ = j;
                    }
                    if (debug) {
                        log.debug(distanceArrays[i][j]);
                    }
                }
            }
        }

        if (maxProba<0.3){
            if (alreadyLitUp<2){
                alreadyLitUp+=1;
                colorMatrix=lightUpSector(colorMatrix,zoneToPerformLocalisation[0],zoneToPerformLocalisation[1],zoneToPerformLocalisation[0]+zoneToPerformLocalisation[2],zoneToPerformLocalisation[1]+zoneToPerformLocalisation[3],1.2,1.2);
                if (debug){
                    log.debug("///////////////////////////////////////////// LIGHTING UP IMAGE /////////////////////////////////////////////////////");
                }
                if (isSavingImages) {
                    saveImage(colorMatrix, "/tmp/imageCenter.png");
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

    //////////////////////////////////// FONCTION RUN DU THREAD /////////////////////////////////////////////

    public void run(){
        this.setPriority(5);

        /*while (ethWrapper.isJumperAbsent()) {
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
        }*/

        //Ancienne version
        //BufferedImage buffImg=ShootBufferedStill.TakeBufferedPicture();
        log.debug("1");
        BufferedImage buffImg= ShootBufferedStillWebcam.takeBufferedPicture();
        log.debug("2");
        movementLocked=false;
        log.debug("3");
        int[][][] colorMatrix=createColorMatrixFromBufferedImage(buffImg);
        log.debug("4");
        centerPointPattern=calculateCenterPattern(buffImg, this.zoneToPerformLocalisation);
        log.debug("5");
        if (!(centerPointPattern[0] == 0 && centerPointPattern[1] == 0)) {
            log.debug("6a");
            analysePattern(colorMatrix);
        }
        else{
            log.debug("6b");
            this.finalIndice=-1;
        }
        log.debug("7");
        gameState.setIndicePattern(this.finalIndice);
        log.debug("8");
        gameState.setRecognitionDone(true);
        log.debug("9");
        recognitionDone=true;
        log.debug("10");
        log.debug("Pattern recognized : " + finalIndice);
        while (!this.isShutdown){
            try {
                this.sleep(100);
            } catch (InterruptedException e) {
                log.debug("Le thread a été interrompu");
                e.printStackTrace();
            }
        }
    }

    public int getFinalIndice(){
        try {
            this.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return this.finalIndice;
    }

    /** Set la valeur de debug
     * @param value valeur de debug
     */
    public void setDebugPatternRecognition(boolean value){
        debug=value;
    }

    /** Set la zone dans laquelle faire la localisation
     * @param zoneToPerformLocalisation {xstart, ystart, width, height}
     */
    public void setZoneToPerformLocalisation(int[] zoneToPerformLocalisation){
        this.zoneToPerformLocalisation=zoneToPerformLocalisation;
    }

    /** Set la modification de saturation avant l'algorithme de reconnaissance de pattern
     * @param saturationPreModifier multiplicateur de la saturation
     */
    public void setSaturationPreModifier(double saturationPreModifier){
        this.saturationPreModifier=saturationPreModifier;
    }

    /** Set la modification de luminosité avant l'algorithme de reconnaissance de pattern
     * @param brightnessPreModifier multiplicateur de la luminosité
     */
    public void setBrightnessPreModifier(double brightnessPreModifier){
        this.brightnessPreModifier=brightnessPreModifier;
    }

    /** Set la distance sur l'axe X (en pixels) sur la photo de 2 centres de carrés adjacents du pattern
     * @param distance distance sur l'axe X en pixels
     */
    public void setDistanceBetweenTwoColors(int distance){
        this.distanceBetweenTwoColors=distance;
    }

    /** Set la longueur du côté du carré dans lequel la valeur médiane sera prise
     * @param length longueur du côté du carré
     */
    public void setLengthSideOfSquareDetection(int length){
        this.lengthSideOfSquareDetection=length;
    }


    public void shutdown(){
        this.isShutdown=true;
    }
    public static boolean isMovementLocked() {
        return movementLocked;
    }
    public static boolean isRecognitionDone() {
        return recognitionDone;
    }

    @Override
    public void updateConfig() {
        Colors.ORANGE.setRGB(config.getInt(ConfigInfoRobot.rorange),config.getInt(ConfigInfoRobot.gorange),config.getInt(ConfigInfoRobot.borange));
        Colors.YELLOW.setRGB(config.getInt(ConfigInfoRobot.rjaune),config.getInt(ConfigInfoRobot.gjaune),config.getInt(ConfigInfoRobot.bjaune));
        Colors.BLUE.setRGB(config.getInt(ConfigInfoRobot.rbleu),config.getInt(ConfigInfoRobot.gbleu),config.getInt(ConfigInfoRobot.bbleu));
        Colors.BLACK.setRGB(config.getInt(ConfigInfoRobot.rnoir),config.getInt(ConfigInfoRobot.gnoir),config.getInt(ConfigInfoRobot.bnoir));
        Colors.GREEN.setRGB(config.getInt(ConfigInfoRobot.rvert),config.getInt(ConfigInfoRobot.gvert),config.getInt(ConfigInfoRobot.bvert));
        this.symmetry=this.config.getString(ConfigInfoRobot.COULEUR).equals("orange");
    }
}