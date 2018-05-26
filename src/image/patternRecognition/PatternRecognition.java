package image.patternRecognition;

import enums.Colors;
import enums.Patterns;
import enums.ConfigInfoRobot;
import pfg.config.Config;
import robot.EthWrapper;
import strategie.GameState;
import threads.AbstractThread;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;


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
    private int imageWidth;
    private int imageHeight;
    private boolean useJumper;

    //mediansList est composé de la médiane en R, en G et en B, pour chacune des 3 couleurs de la photo
    //Donc, si on nomme les couleurs 1, 2 et 3, on a :
    //mediansList={{R1,G1,B1},{R2,G2,B2},{R3,G3,B3}}
    private int[][] mediansList = new int[3][3];

    private int[] zoneToPerformLocalisationAutomatic;
    private int[] zoneToPerformLocalisationManual;
    private int[][] positionsColorsOnImage;

    private double saturationPreModifier;
    private double brightnessPreModifier;
    private double saturationModifierLightingUp;
    private double brightnessModifierLightingUp;
    private boolean alreadyPreModified;
    private boolean movementLocked;
    private boolean recognitionDone;

    private boolean localizationAutomated;
    private Colors firstColorShown;
    private Colors secondColorShown;
    private Colors thirdColorShown;

    private double tempMaxProba;
    private int tempMaxIndice;

    /** Instanciation du thread de reconnaissance de couleurs
     * @param config passe la config
     * @param ethWrapper passe l'ethWrapper
     */
    public PatternRecognition(Config config, EthWrapper ethWrapper, GameState stateToConsider){
        this.config=config;
        this.updateConfig();

        //Instanciation des modules nécessaires
        this.ethWrapper=ethWrapper;
        this.gameState=stateToConsider;

        //TODO : faire en sorte que le script python accepte une certaine zone à localiser

        //Zone dans laquelle la localisation doit être faite
        /** De la forme : {xstart, ystart, width, height} */
        if (this.symmetry) {
            this.zoneToPerformLocalisationAutomatic = new int[]{(this.imageWidth-1),100,200,250};
            this.zoneToPerformLocalisationManual = new int[]{(this.imageWidth-1),100,200,250};
        }
        else {
            this.zoneToPerformLocalisationAutomatic = new int[]{0,100,200,250};
            this.zoneToPerformLocalisationManual = new int[]{0,100,200,250};
        }

        //Paramètres permettant à la localisation automatique d'avoir les coordonnées des patterns
        //avec le centre du rectangle détecté
        this.lengthSideOfSquareDetection=5; //in pixels
        this.distanceBetweenTwoColors=8; //in pixels

        //Paramètres de débug
        this.alreadyPrintedColorMatchingProba=false;
        this.debug=true;
        this.isSavingImages=true;

        //Paramètres de prémodification de l'image avant la reconnaissance
        this.alreadyPreModified=false;

        //Patramètres de modification de l'image si aucun pattern n'est assez significatif
        this.alreadyLitUp=0;

        //Locks
        this.movementLocked=true;
        this.recognitionDone=false;

        //Utiles à la reconnaissance de pattern
        this.tempMaxIndice=0;
        this.tempMaxProba=0;
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

        int width = picture.getWidth();
        int height = picture.getHeight();
        int[] array = new int[width*height*3];
        picture.getRaster().getPixels(0, 0, width, height, array);
        int[][][] colorMatrix = new int[width][height][3];
        //Méthode pour récupérer les paramètres R, G et B de chaque pixel
        for (int x=0; x<width; x++){
            for (int y=0; y<height; y++) {
                colorMatrix[x][y][0]=(int)array[x*height+y*3];
                colorMatrix[x][y][1]=(int)array[x*height+y*3+1];
                colorMatrix[x][y][2]=(int)array[x*height+y*3+2];
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
     * @return renvoie la liste (double[nbPatterns=20]) des probabilités que les couleurs de la photo correspondent à un des pattern
     */
    private double[] compareThreeRGBsToAllPatterns(int[][] RGBs){
        int nbPatterns=20;
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
            if (debug && !this.alreadyPrintedColorMatchingProba){
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
        this.alreadyPrintedColorMatchingProba=true;
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

        if (debug && !this.alreadyPrintedColorMatchingProba) {
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
        log.debug("xend:"+xend+" xstart:"+xstart+" yend:"+yend+" ystart:"+ystart);
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
        for (double currentProba : probabilitiesList){
            if (currentProba>bestProba){
                bestProba=currentProba;
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

    ///////////////////////////// AUGMENTATION DE LUMINOSITE ET CONTRASTE /////////////////////////////

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
                int[] intRGB=convertHSBtoRGB(HSB[0],HSB[1],HSB[2]);
                for (int i=0; i<intRGB.length; i++){
                    RGB[i]=(int)intRGB[i];
                }
                colorMatrixLitUp[x][y]=RGB;
            }
        }
        return colorMatrixLitUp;
    }

    private int[][][] preModifyImage(int[][][] colorMatrix, boolean automatedMode){
        if (!(this.alreadyPreModified)) {
            if (debug) {
                log.debug("Applying pre-modifications");
            }
            if (automatedMode) {
                colorMatrix = lightUpSector(colorMatrix,
                        this.zoneToPerformLocalisationAutomatic[0], this.zoneToPerformLocalisationAutomatic[1],
                        this.zoneToPerformLocalisationAutomatic[0] + this.zoneToPerformLocalisationAutomatic[2],
                        this.zoneToPerformLocalisationAutomatic[1] + this.zoneToPerformLocalisationAutomatic[3],
                        this.saturationPreModifier, this.brightnessPreModifier);
            }
            else{
                colorMatrix = lightUpSector(colorMatrix,
                        this.zoneToPerformLocalisationManual[0], this.zoneToPerformLocalisationManual[1],
                        this.zoneToPerformLocalisationManual[0] + this.zoneToPerformLocalisationManual[2],
                        this.zoneToPerformLocalisationManual[1] + this.zoneToPerformLocalisationManual[3],
                        this.saturationPreModifier, this.brightnessPreModifier);
            }
            this.alreadyPreModified = true;
        }
        return colorMatrix;
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
            ImageIO.write(image, "jpg", outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /////////////////////////////// CALCULATE CENTER OF PATTERN ///////////////////////////////

    /** Localise le pattern et calcule son centre
     * @param zoneToPerformLocalisation zone dans laquelle le pattern doit se trouver sur l'image
     * @return renvoie les coordonnée {x,y} du center de l'image
     */
    private int[] calculateCenterPattern(int[] zoneToPerformLocalisation){
        //Nom de la photo : "/tmp/ImageRaspi.jpeg"
        if (debug){
            log.debug("Performing automated pattern localisation on : (("+zoneToPerformLocalisation[0]+","+zoneToPerformLocalisation[1]+
                    "),("+(zoneToPerformLocalisation[0]+zoneToPerformLocalisation[2])+","+(zoneToPerformLocalisation[1]+zoneToPerformLocalisation[3])+"))");
            LocatePattern.setDebug(true);
        }
        int[] patternZone = LocatePatternAutomated.LocatePattern(zoneToPerformLocalisation);
        int[] centerPattern=new int[]{(patternZone[0]+patternZone[2])/2,(patternZone[1]+patternZone[3])/2};
        if (debug){
            log.debug("Center found : ("+centerPattern[0]+","+centerPattern[1]+")");
        }
        return centerPattern;
    }

    //////////////////////////////////// ANALYSE DE PATTERN /////////////////////////////////////////////

    /**Méthode permettant de faire la reconnaissance de pattenrs, dans le cas où une localisation automatique a due être faite
     * @return l'id du pattern (int de 0 à 9, bornes comprises)
     */
    private int analysePatternAfterAutomaticLocalization(int[][][] colorMatrix) {
        if (!(this.centerPointPattern[0] == 0 && this.centerPointPattern[1] == 0)) {
            double[][] distanceArrays = new double[5][20];
            int halfLengthSideOfSquareDetection = this.lengthSideOfSquareDetection / 2;
            int halfDistanceBetweenTwoColors = this.distanceBetweenTwoColors / 2;
            colorMatrix=preModifyImage(colorMatrix,localizationAutomated);
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
                int imageWidthMinusOne = this.imageWidth - 1;
                int imageHeightMinusOne = this.imageHeight - 1;
                this.positionsColorsOnImage = new int[][]
                        {{Math.max(Math.min(this.centerPointPattern[0] - halfLengthSideOfSquareDetection - this.distanceBetweenTwoColors + i * halfDistanceBetweenTwoColors, imageWidthMinusOne), 0),
                          Math.max(Math.min(this.centerPointPattern[0] - halfLengthSideOfSquareDetection + i * halfDistanceBetweenTwoColors, imageWidthMinusOne), 0),
                          Math.max(Math.min(this.centerPointPattern[0] - halfLengthSideOfSquareDetection + this.distanceBetweenTwoColors + i * halfDistanceBetweenTwoColors, imageWidthMinusOne), 0)},
                         {Math.max(this.centerPointPattern[1] - halfLengthSideOfSquareDetection, 0),
                          Math.max(this.centerPointPattern[1] - halfLengthSideOfSquareDetection, 0),
                          Math.max(this.centerPointPattern[1] - halfLengthSideOfSquareDetection, 0)},
                         {Math.max(Math.min(this.centerPointPattern[0] + halfLengthSideOfSquareDetection - this.distanceBetweenTwoColors + i * halfDistanceBetweenTwoColors, imageWidthMinusOne), 0),
                          Math.max(Math.min(this.centerPointPattern[0] + halfLengthSideOfSquareDetection + i * halfDistanceBetweenTwoColors, imageWidthMinusOne), 0),
                          Math.max(Math.min(this.centerPointPattern[0] + halfLengthSideOfSquareDetection + this.distanceBetweenTwoColors + i * halfDistanceBetweenTwoColors, imageWidthMinusOne), 0)},
                         {Math.min(this.centerPointPattern[1] + halfLengthSideOfSquareDetection, imageHeightMinusOne),
                          Math.min(this.centerPointPattern[1] + halfLengthSideOfSquareDetection, imageHeightMinusOne),
                          Math.min(this.centerPointPattern[1] + halfLengthSideOfSquareDetection, imageHeightMinusOne)}};
                distanceArrays[i - iStartValue] = computeProximity(colorMatrix, this.positionsColorsOnImage);
            }
            double maxProba = this.tempMaxProba;
            int maxJ = this.tempMaxIndice;
            double[] badDistanceArray = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
            for (int i = 0; i < distanceArrays.length; i++) {
                if (debug) {
                    log.debug("");
                    log.debug("Proximity (Xshifted by " + ((i + iStartValue) * halfDistanceBetweenTwoColors) + " )");
                }
                if (distanceArrays[i] == badDistanceArray) {
                    for (int j = 0; j < distanceArrays[i].length; j++) {
                        distanceArrays[i][j] = 0;
                    }
                } else {
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
            this.tempMaxProba=maxProba;
            this.tempMaxIndice=maxJ;

            if (maxProba < 0.2) {
                if (this.alreadyLitUp < 2) {
                    this.alreadyLitUp += 1;
                    colorMatrix = lightUpSector(colorMatrix,
                            this.zoneToPerformLocalisationAutomatic[0], this.zoneToPerformLocalisationAutomatic[1],
                            this.zoneToPerformLocalisationAutomatic[0] + this.zoneToPerformLocalisationAutomatic[2],
                            this.zoneToPerformLocalisationAutomatic[1] + this.zoneToPerformLocalisationAutomatic[3],
                            this.saturationModifierLightingUp, this.brightnessModifierLightingUp);
                    if (debug) {
                        log.debug("///////////////////////////////////////////// LIGHTING UP IMAGE /////////////////////////////////////////////////////");
                    }
                    if (isSavingImages) {
                        saveImage(colorMatrix, "/tmp/ImageCenter.jpg");
                    }
                    int finalIndiceAfterLightingUp = analysePatternAfterAutomaticLocalization(colorMatrix);
                    if (finalIndiceAfterLightingUp>9){
                        finalIndiceAfterLightingUp-=10;
                    }
                    this.finalIndice=finalIndiceAfterLightingUp;
                    return finalIndiceAfterLightingUp;
                } else {
                    if (maxJ>9){
                        maxJ-=10;
                    }
                    this.finalIndice = maxJ;
                    return maxJ;
                }
            } else {
                if (maxJ>9){
                    maxJ-=10;
                }
                this.finalIndice = maxJ;
                return maxJ;
            }
        }
        else{
            this.finalIndice=-1;
            return -1;
        }
    }


    /**Méthode permettant de faire la reconnaissance de pattenrs, dans le cas où une localisation de pattern a été faite à la main
     * @return l'id du pattern (int de 0 à 9, bornes comprises)
     */
    private int analysePatternAfterManualLocalization(int[][][] colorMatrix) {
        File file = new File("/tmp/CoordsPatternVideo.txt");
        String data = "";
        if (file.exists()) {
            try {
                data = new String(Files.readAllBytes(Paths.get("/tmp/CoordsPatternVideo.txt")));
            } catch (Exception e) {
                e.printStackTrace();
                log.critical("Exception /tmp/CoordsPatternVideo.txt");
                this.localizationAutomated = true;
            }
            if (!this.localizationAutomated) {
                String[] infos = data.split(" ");
                int[] coords = new int[6];
                for (int i = 0; i < 6; i++) {
                    coords[i] = Integer.parseInt(infos[i]);
                }
                log.debug("Pattern manally located on: ("+coords[0]+","+coords[1]+"), ("+coords[2]+","+coords[3]+"), ("+coords[4]+","+coords[5]+")");
                /** Coords de la forme :
                 * {xCenterFirstColor, yCenterFirstColor, xCenterSecondColor, yCenterSecondColor, xCenterThirdColor, yCenterSecondColor}
                 */
                int maxX = Math.max(Math.max(coords[0], coords[2]), coords[4]);
                int minX = Math.min(Math.min(coords[0], coords[2]), coords[4]);
                int maxY = Math.max(Math.max(coords[1], coords[3]), coords[5]);
                int minY = Math.min(Math.min(coords[1], coords[3]), coords[5]);
                /** De la forme : {xstart, ystart, width, height} */
                this.zoneToPerformLocalisationManual = new int[]
                        {Math.min(Math.max(minX-100, 0),this.imageWidth - 1),
                         Math.min(Math.max(minY-100, 0),this.imageHeight - 1),
                         Math.min(Math.max((maxX-minX)+200, 0), this.imageWidth - 1),
                         Math.min(Math.max((maxY-minY)+200, 0), this.imageHeight - 1)};
                colorMatrix = preModifyImage(colorMatrix, this.localizationAutomated);
                int halfLengthSideOfSquareDetection = this.lengthSideOfSquareDetection / 2;
                int imageWidthMinusOne = this.imageWidth - 1;
                int imageHeightMinusOne = this.imageHeight - 1;
                /** On définit où l'algorithme doit chercher ses couleurs
                 * positionColorsOnImage=
                 * {
                 * {xStartFirstColor,xStartSecondColor,xStartThirdColor},
                 * {yStartFirstColor,yStartSecondColor,yStartThirdColor},
                 * {xEndFirstColor,xEndSecondColor,xEndThirdColor},
                 * {yEndFirstColor,yEndSecondColor,yEndThirdColor},
                 * }
                 */
                this.positionsColorsOnImage = new int[][]{
                        {Math.max(coords[0] - halfLengthSideOfSquareDetection, 0),
                         Math.max(coords[2] - halfLengthSideOfSquareDetection, 0),
                         Math.max(coords[4] - halfLengthSideOfSquareDetection, 0)
                        },
                        {Math.max(coords[1] - halfLengthSideOfSquareDetection, 0),
                         Math.max(coords[3] - halfLengthSideOfSquareDetection, 0),
                         Math.max(coords[5] - halfLengthSideOfSquareDetection, 0)
                        },
                        {Math.min(coords[0] + halfLengthSideOfSquareDetection, imageWidthMinusOne),
                         Math.min(coords[2] + halfLengthSideOfSquareDetection, imageWidthMinusOne),
                         Math.min(coords[4] + halfLengthSideOfSquareDetection, imageWidthMinusOne)
                        },
                        {Math.min(coords[1] + halfLengthSideOfSquareDetection, imageHeightMinusOne),
                         Math.min(coords[3] + halfLengthSideOfSquareDetection, imageHeightMinusOne),
                         Math.min(coords[5] + halfLengthSideOfSquareDetection, imageHeightMinusOne)
                        }
                };
                double[] distanceArray = computeProximity(colorMatrix, this.positionsColorsOnImage);
                double[] badDistanceArray = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
                double maxProba = this.tempMaxProba;
                int maxI = this.tempMaxIndice;
                if (debug) {
                    log.debug("");
                    log.debug("Proximity (Manual detection)");
                }
                if (distanceArray == badDistanceArray) {
                    for (int i = 0; i < distanceArray.length; i++) {
                        distanceArray[i] = 0;
                    }
                } else {
                    for (int i = 0; i < distanceArray.length; i++) {
                        if (distanceArray[i] > maxProba) {
                            maxProba = distanceArray[i];
                            maxI = i;
                        }
                        if (debug) {
                            log.debug(i+": "+distanceArray[i]);
                        }
                    }
                }
                this.tempMaxProba=maxProba;
                this.tempMaxIndice=maxI;
                int finalMaxIndice=this.tempMaxIndice;

                if (maxProba < 0.2) {
                    if (this.alreadyLitUp < 2) {
                        this.alreadyLitUp += 1;
                        colorMatrix = lightUpSector(colorMatrix,
                                this.zoneToPerformLocalisationManual[0], this.zoneToPerformLocalisationManual[1],
                                this.zoneToPerformLocalisationManual[0] + this.zoneToPerformLocalisationManual[2],
                                this.zoneToPerformLocalisationManual[1] + this.zoneToPerformLocalisationManual[3],
                                this.saturationModifierLightingUp, this.brightnessModifierLightingUp);
                        if (debug) {
                            log.debug("///////////////////////////////////////////// LIGHTING UP IMAGE /////////////////////////////////////////////////////");
                        }
                        if (isSavingImages) {
                            saveImage(colorMatrix, "/tmp/ImageCenter.jpg");
                        }
                        int finalIndiceAfterLightingUp = analysePatternAfterManualLocalization(colorMatrix);
                        if (finalIndiceAfterLightingUp>9){
                            finalIndiceAfterLightingUp-=10;
                        }
                        this.finalIndice=finalIndiceAfterLightingUp;
                        return finalIndiceAfterLightingUp;
                    } else {
                        if (finalMaxIndice>9){
                            finalMaxIndice-=10;
                        }
                        this.finalIndice = finalMaxIndice;
                        return finalMaxIndice;
                    }
                } else {
                    if (finalMaxIndice>9){
                        finalMaxIndice-=10;
                    }
                    this.finalIndice = finalMaxIndice;
                    return finalMaxIndice;
                }
            }
            else{
                this.finalIndice=-1;
                return -1;
            }
        }
        else{
            this.localizationAutomated=true;
            this.finalIndice=-1;
            return -1;
        }
    }

    //////////////////////////////////// FONCTION RUN DU THREAD /////////////////////////////////////////////

    /**
     * On run le thread
     */
    public void run(){
        this.setPriority(5);
        //On lance le programme de capture de la webcam
        if (this.firstColorShown==Colors.NULL || this.secondColorShown==Colors.NULL || this.thirdColorShown==Colors.NULL) {
            log.debug("Ouverture de la caméra");
            UseWebcam.startCapturing();
        }

        if (this.useJumper) {
            while (!gameState.wasJumperRemoved()) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        if (this.firstColorShown==Colors.NULL || this.secondColorShown==Colors.NULL || this.thirdColorShown==Colors.NULL) {
            log.debug("Début de la prise de photo");
            //On garde la dernière frame prise par la webcam
            BufferedImage buffImg = UseWebcam.takeBufferedPicture();
            log.debug("Fin de la prise de photo");
            this.setMovementLocked(false);
            int[][][] colorMatrix = createColorMatrixFromBufferedImage(buffImg);
            if (!this.localizationAutomated) {
                analysePatternAfterManualLocalization(colorMatrix);
            }
            if (this.localizationAutomated) {
                centerPointPattern = calculateCenterPattern(this.zoneToPerformLocalisationAutomatic);
                analysePatternAfterAutomaticLocalization(colorMatrix);
            }
            this.tempMaxProba=0;
            this.tempMaxIndice=0;
        }
        else{
            boolean patternHasBeenFound=false;
            for (Patterns pattern : Patterns.values()){
                Colors[] colors = pattern.getPattern();
                if(colors[0].equals(this.firstColorShown) && colors[1].equals(this.secondColorShown) && colors[2].equals(this.thirdColorShown)){
                    int indice=pattern.getNumber();
                    if (indice>9){
                        indice-=10;
                    }
                    this.finalIndice=indice;
                    patternHasBeenFound=true;
                    break;
                }
            }
            if (!patternHasBeenFound){
                this.finalIndice=-1;
            }
        }

        this.setFinalIndice(this.finalIndice);
        log.debug("Pattern recognized : " + finalIndice);
        this.setRecognitionDone(true);
        while (!this.isShutdown){
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                log.debug("Le thread a été interrompu");
                e.printStackTrace();
            }
        }
    }

    public int getFinalIndice(){
        return this.finalIndice;
    }

    public void setFinalIndice(int indice){
        if (indice<-2 || indice>19){
            log.critical("Bad indice value set");
        }
        else{
            if (indice>9){
                indice-=10;
            }
            this.finalIndice=indice;
            gameState.setIndicePattern(this.finalIndice);
        }
    }

    /** Set la valeur de debug
     * @param value valeur de debug
     */
    public void setDebugPatternRecognition(boolean value){
        debug=value;
    }

    /** Set la zone dans laquelle faire la localisation
     * @param zoneToPerformLocalisationAutomatic {xstart, ystart, width, height}
     */
    public void setZoneToPerformLocalisationAutomatic(int[] zoneToPerformLocalisationAutomatic){
        this.zoneToPerformLocalisationAutomatic = zoneToPerformLocalisationAutomatic;
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

    /** Permet de savoir si la photo a été prise, car le robot ne doit pas bouger en attendant la fin de la prise de photo
     */
    public boolean isMovementLocked() {
        return this.movementLocked;
    }

    public void setMovementLocked(boolean value){
        if (value){
            log.debug("Movement locked");
        }
        else{
            log.debug("Movement unlocked");
        }
        this.movementLocked=value;
    }

    /** Permet de savoir si la reconnaissance a été faite, car le robot ne doit pas ramasser de cubes avant de savoir quel est le pattern reconnu
     */
    public boolean isRecognitionDone() {
        return this.recognitionDone;
    }

    public void setRecognitionDone(boolean value){
        if (value){
            log.debug("Recognition done");
        }
        else{
            log.debug("Recognition to do");
        }
        gameState.setRecognitionDone(value);
        this.recognitionDone=value;
    }

    @Override
    public void updateConfig() {
        Colors.ORANGE.setRGB(this.config.getInt(ConfigInfoRobot.rorange),config.getInt(ConfigInfoRobot.gorange),config.getInt(ConfigInfoRobot.borange));
        Colors.YELLOW.setRGB(this.config.getInt(ConfigInfoRobot.rjaune),config.getInt(ConfigInfoRobot.gjaune),config.getInt(ConfigInfoRobot.bjaune));
        Colors.BLUE.setRGB(this.config.getInt(ConfigInfoRobot.rbleu),config.getInt(ConfigInfoRobot.gbleu),config.getInt(ConfigInfoRobot.bbleu));
        Colors.BLACK.setRGB(this.config.getInt(ConfigInfoRobot.rnoir),config.getInt(ConfigInfoRobot.gnoir),config.getInt(ConfigInfoRobot.bnoir));
        Colors.GREEN.setRGB(this.config.getInt(ConfigInfoRobot.rvert),config.getInt(ConfigInfoRobot.gvert),config.getInt(ConfigInfoRobot.bvert));
        this.saturationPreModifier=this.config.getDouble(ConfigInfoRobot.saturationPreModifier);
        this.brightnessPreModifier=this.config.getDouble(ConfigInfoRobot.brightnessPreModifier);
        this.saturationModifierLightingUp=this.config.getDouble(ConfigInfoRobot.saturationModifierLightingUp);
        this.brightnessModifierLightingUp=this.config.getDouble(ConfigInfoRobot.brightnessModifierLightingUp);
        this.imageHeight=this.config.getInt(ConfigInfoRobot.IMAGE_HEIGHT);
        this.imageWidth=this.config.getInt(ConfigInfoRobot.IMAGE_WIDTH);
        this.localizationAutomated=this.config.getBoolean(ConfigInfoRobot.LOCALIZATION_AUTOMATED);
        this.firstColorShown=Colors.getColorFromName(this.config.getString(ConfigInfoRobot.FIRST_COLOR));
        this.secondColorShown=Colors.getColorFromName(this.config.getString(ConfigInfoRobot.SECOND_COLOR));
        this.thirdColorShown=Colors.getColorFromName(this.config.getString(ConfigInfoRobot.THIRD_COLOR));
        this.symmetry=this.config.getString(ConfigInfoRobot.COULEUR).equals("orange");
        this.useJumper=this.config.getBoolean(ConfigInfoRobot.ATTENTE_JUMPER);
    }
}
