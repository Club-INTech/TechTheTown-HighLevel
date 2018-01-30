package patternRecognition;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


/** Classe permettant de faire la reconnaissance de patterns
 * @author Nayht
 */
public class PatternRecognition{

    private static boolean debug = false;   //Debug
    private static boolean alreadyPrintedColorMatchingProba=false; //Utile pour le debug


    private static boolean mustSelectAValidPattern = false; //Est-ce qu'on peut renvoyer qu'aucun pattern n'a été trouvé ?
    private static boolean symmetry = false;    //Est-ce qu'on est du côté orange (alors symmetry = true) ou vert (alors symmetry=false)



    public static void setDebugPatternRecognition(boolean value){
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
    private static double[] normalizeDoubleList(double[] list){
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
    public static int[][][] createColorMatrix(String pathname) {
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
                    colorMatrix = null;
                    picture = new BufferedImage(2000, 2000, 1);
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
    private static int getMedianValue(int[][][] colorMatrix, int xstart, int ystart, int xend, int yend, int posRGB) {
        int width = xend - xstart;
        int height = yend - ystart;
        int[] listAllPoints = new int[width*height];
        for (int i = 0; i < width * height; i++){
            listAllPoints[i] = colorMatrix[i%height + xstart][i/height + ystart][posRGB];
        }
        java.util.Arrays.sort(listAllPoints);
        int len_list=listAllPoints.length;
        int medianValue=listAllPoints[len_list/2];
        return medianValue;
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
    private static int[] getRGBMedianValues(int[][][] colorMatrix, int xstart, int ystart, int xend, int yend){
        int[] mediansList= new int[3];
        for (int i=0; i<3; i++){
            mediansList[i]=getMedianValue(colorMatrix, xstart, ystart, xend, yend, i);
            if (debug==true) {
                System.out.println("posRGB:"+i+" MedianValue:"+mediansList[i]);
            }
        }
        if (debug==true){
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
    private static double computeInverseDistanceToSingleColor(int[] RGBToEvaluate, int[] color){
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
    private static double[] computeInverseDistanceToAllColors(int[] RGBToEvaluate){
        double[] inverseDistances=new double[5];
        for (int idColor=0; idColor<5; idColor++){
            int[] color=Colors.getRGBFromID(idColor);
            inverseDistances[idColor]=computeInverseDistanceToSingleColor(RGBToEvaluate, color);
        }
        return inverseDistances;
    }

    /**Fonction permettant de calculer les probabilités que la couleur à évaluer(RGBToEvaluate) corresponde à celles prédéfinies
     * @param RGBToEvaluate RGB correspondant à la oculeur à évaluer, ie couleur dont la distance doit être calculée
     * @return renvoie la liste (double[]) des probabilités que la couleur à évaluer corresponde à celles prédéfinies
     */
    private static double[] computeProbabilitiesColors(int[] RGBToEvaluate){
        double[] inverseDistances=computeInverseDistanceToAllColors(RGBToEvaluate);
        double[] normalizedInverseDistances = normalizeDoubleList(inverseDistances);

        if (debug==true && alreadyPrintedColorMatchingProba==false) {
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
    private static double compareThreeRGBsToPattern(int[][] RGBs, int patternID){
        Colors[] pattern= Patterns.getPatternFromID(patternID);
        double finalProbability = 1;
        for (int i=0; i<3; i++){
            int colorID=pattern[i].getID();
            if (debug==true && alreadyPrintedColorMatchingProba==false){
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
    private static double[] compareThreeRGBsToAllPatterns(int[][] RGBs){
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
    private static double[] encapsulationThreeFourFive(int[][][] colorMatrix, int[] xstarts, int[] ystarts, int[] xends, int[] yends) {
        if (debug==true){
            System.out.println("First color");
        }
        int[] medianFirstColor = getRGBMedianValues(colorMatrix, xstarts[0], ystarts[0], xends[0], yends[0]);
        if (debug==true){
            System.out.println("Second color");
        }
        int[] medianSecondColor = getRGBMedianValues(colorMatrix, xstarts[1], ystarts[1], xends[1], yends[1]);
        if (debug==true){
            System.out.println("Third color");
        }
        int[] medianThirdColor = getRGBMedianValues(colorMatrix, xstarts[2], ystarts[2], xends[2], yends[2]);
        int[][] mediansListTemp = {medianFirstColor, medianSecondColor, medianThirdColor};
        for (int i=0; i<3; i++) {
            mediansList[i] = mediansListTemp[i];
        }
        double[] probabilitiesList = compareThreeRGBsToAllPatterns(mediansList);
        double[] normalizedList=normalizeDoubleList(probabilitiesList);
        return normalizedList;
    }

    //////////////////////////////////// DISCRIMINATION /////////////////////////////////////////////

    /**Fonction permettant de déterminer les patterns qui sont viables au vu de la liste de probabilités
     * @param probabilitiesList liste normalisée de probabilités que le pattern pris en photo corresponde aux patterns prédéfinis
     * @return renvoie la liste des indices des patterns plausibles
     */
    private static ArrayList<Integer> selectBestProbabilities(double[] probabilitiesList){
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
    private static int[] convertHSBtoRGB(float hue, float saturation, float brightness){
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

    private static float[] convertRGBtoHSB(int R, int G, int B){
        float[] HSB = new float[3];
        Color.RGBtoHSB(R,G,B,HSB);
        return HSB;
    }

    private static int[][][] lightUpSector(int[][][] colorMatrixLitUp, int xstart, int ystart, int xend, int yend){
        for (int x=xstart; x<xend; x++){
            for (int y=ystart; y<yend; y++){
                    int[] RGB = colorMatrixLitUp[x][y];
                    float[] HSB=convertRGBtoHSB(RGB[0],RGB[1],RGB[2]);

                    //Improve saturation
                    HSB[1]*=1.30;
                    if (HSB[1]>1){
                        HSB[1]=1;
                    }
                    //Improve brightness
                    HSB[2]*=1.30;
                    if (HSB[2]>1){
                        HSB[2]=1;
                    }
                    RGB=convertHSBtoRGB(HSB[0],HSB[1],HSB[2]);
                colorMatrixLitUp[x][y]=RGB;
            }
        }
        return colorMatrixLitUp;
    }

    private static int[][][] lightUpColors(int[][][] colorMatrix, int[] xstarts, int[] ystarts, int[] xends, int[] yends){
        int[][][] colorMatrixLitUp=colorMatrix.clone();
        for (int i=0; i<xstarts.length; i++){
            colorMatrixLitUp=lightUpSector(colorMatrixLitUp, xstarts[i], ystarts[i], xends[i], yends[i]);
        }
        return colorMatrixLitUp;
    }

    /**Discrimine le meilleur indice en cas de conflit de patterns (mode de discrimination : prend le plus probable)
     * @param probabilitiesList liste des probabilités qu'un pattern connu soit identifié
     * @param selectedProbabilitiesIndice id des patterns plausibles
     * @return indice (int) du pattern sélectionné
     */
    private static int getMostProbablePattern(double[] probabilitiesList, ArrayList<Integer> selectedProbabilitiesIndice){
        double max=0;
        int indiceMax=0;
        for (int i=0; i<selectedProbabilitiesIndice.size(); i++){
            if (probabilitiesList[selectedProbabilitiesIndice.get(i)]>max){
                indiceMax=i;
                max=probabilitiesList[selectedProbabilitiesIndice.get(i)];
            }
        }
        return selectedProbabilitiesIndice.get(indiceMax);
    }

    //////////////////////////////////// DETECT VICTORY INDICE /////////////////////////////////////////////

    /**Fontion permettant de déterminer le pattern identifié. Surtout utile en cas de conflits (et quand les fonctions de conflit auront un bon facteur discriminant)
     * @param probabilitiesList liste normalisées des probabilités de correspondance du pattern de la photo à un des patterns prédéfinis
     * @param colorMatrix matrice de couleur réalisée à partir de la photo prise
     * @param positionsColorsOnImage positions des couleurs à analyser sur la photo
     * @return renvoie l'indice finalement choisi
     */
    private static int computeFinalIndice(double[] probabilitiesList, int[][][] colorMatrix, int[][] positionsColorsOnImage) {
        ArrayList<Integer> selectionnedProbabilitiesIndice = selectBestProbabilities(probabilitiesList);
        int finalIndice = 10;
        if (debug==true) {
            System.out.println(selectionnedProbabilitiesIndice.toString());
            System.out.println("robabilities");
            for (int i = 0; i < 10; i++) {
                System.out.println(probabilitiesList[i]);
            }
        }
        if (selectionnedProbabilitiesIndice.size() == 1) {
            finalIndice = getMostProbablePattern(probabilitiesList, selectionnedProbabilitiesIndice);
        } else {
            if (debug) {
                System.out.println("////////////// Lighting up the color matrix ////////////////");
            }
            int[][][] colorMatrixLitUp=lightUpColors(colorMatrix,positionsColorsOnImage[0],positionsColorsOnImage[1],positionsColorsOnImage[2],positionsColorsOnImage[3]);
            double[] probabilitiesListLitUp = encapsulationThreeFourFive(colorMatrixLitUp, positionsColorsOnImage[0], positionsColorsOnImage[1], positionsColorsOnImage[2], positionsColorsOnImage[3]);
            ArrayList<Integer> selectionnedProbabilitiesIndiceLitUp = selectBestProbabilities(probabilitiesListLitUp);

            if (debug==true) {
                System.out.println(selectionnedProbabilitiesIndiceLitUp.toString());
                System.out.println("LitUpProbabilities");
                for (int i = 0; i < 10; i++) {
                    System.out.println(probabilitiesListLitUp[i]);
                }
            }
            if (mustSelectAValidPattern) {
                finalIndice = getMostProbablePattern(probabilitiesListLitUp, selectionnedProbabilitiesIndiceLitUp);
            }
            else {
                if (selectionnedProbabilitiesIndiceLitUp.size() == 1) {
                    finalIndice = getMostProbablePattern(probabilitiesListLitUp, selectionnedProbabilitiesIndiceLitUp);
                } else {
                    return -1;
                }
            }
        }
        return finalIndice;
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

    //////////////////////////////////// MAIN /////////////////////////////////////////////

    /**Méthode permettant de faire la reconnaissance de pattenrs
     * @return l'id du pattern (int de 0 à 9, bornes comprises)
     */
    public static int analysePattern(String pathToImage, int[][][] pat, int[][] positionsColorsOnImage) {
        //CALIBRER SUR UNE IMAGE SOMBRE
        //PARTIE A NE PAS TOUCHER DU MAIN
        int[][][] colorMatrix = createColorMatrix(pathToImage);
        if (symmetry){
            for (int i=0; i<4; i++) {
                int temp = positionsColorsOnImage[i][2];
                positionsColorsOnImage[i][2] = positionsColorsOnImage[i][0];
                positionsColorsOnImage[i][0] = temp;
            }
        }
        double[] probabilitiesList = encapsulationThreeFourFive(colorMatrix, positionsColorsOnImage[0], positionsColorsOnImage[1], positionsColorsOnImage[2], positionsColorsOnImage[3]);
        int finalIndice=computeFinalIndice(probabilitiesList,colorMatrix,positionsColorsOnImage);

        //Debug
        if (debug==true){
            System.out.println("Pattern recognized : "+finalIndice);
        }
        return finalIndice;
    }
}