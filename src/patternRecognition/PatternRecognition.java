package patternRecognition;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;




public class PatternRecognition {

    //////////////////////////////////// GLOBAL VARIABLES DEFINITION /////////////////////////////////////////////

    //Défini l'objet mediansList pour qu'il soit accessible sans être retourné
    public static int[][] mediansList = new int[3][3];
    //mediansList est composé de la médiane en R, en G et en B, pour chacune des 3 couleurs de la photo
    //Donc, si on nomme les couleurs 1, 2 et 3, on a :
    //mediansList={{R1,G1,B1},{R2,G2,B2},{R3,G3,B3}}
    //Cette valeur, une fois calculée, reste inchangée tout au long de l'exécution du programme

    //////////////////////////////////// NORMALIZATION FUNCTIONS /////////////////////////////////////////////

    /**Fonction de normalisation d'une liste de doubles
     * @param list liste de doubles à normaliser
     * @return renvoie une liste normalisée*/
    public static double[] normalizeDoubleList(double[] list){
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
    public static int getMedianValue(int[][][] colorMatrix, int xstart, int ystart, int xend, int yend, int posRGB) {
        int width = xend - xstart;
        int height = yend - ystart;
        int[] listAllPoints = new int[width*height];
        for (int i = 0; i < width * height; i++){
            listAllPoints[i] = colorMatrix[i%height + xstart][i/height + ystart][posRGB];
        }
        java.util.Arrays.sort(listAllPoints);
        int len_list=listAllPoints.length;
        int medianValue=listAllPoints[len_list/2];
        System.out.println(medianValue);
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
    public static int[] getRGBMedianValues(int[][][] colorMatrix, int xstart, int ystart, int xend, int yend){
        int[] mediansList= new int[3];
        for (int i=0; i<3; i++){
            mediansList[i]=getMedianValue(colorMatrix, xstart, ystart, xend, yend, i);
        }
        return mediansList;
    }

    //////////////////////////////////// COLOR COMPARISON /////////////////////////////////////////////

    /**Fonction permettant de calculer l'inverse de la distance quadratique d'une couleur (RGBToEvaluate) à une autre prédéfinie (color)
     * @param RGBToEvaluate RGB correspondant à la couleur à évaluer, ie couleur dont la distance doit être calculée
     * @param color RGB correspondant à une couleur prédéfinie
     * @return renvoie l'inverse de la distance quadratique (double) entre les 2 couleurs
     */
    public static double computeInverseDistanceToSingleColor(int[] RGBToEvaluate, int[] color){
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
    public static double[] computeInverseDistanceToAllColors(int[] RGBToEvaluate){
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
    public static double[] computeProbabilitiesColors(int[] RGBToEvaluate){
        double[] inverseDistances=computeInverseDistanceToAllColors(RGBToEvaluate);
        double[] normalizedInverseDistances = normalizeDoubleList(inverseDistances);

        for (int i=0; i<normalizedInverseDistances.length; i++) {
            System.out.println(normalizedInverseDistances[i]);
        }
        System.out.println("");

        return normalizedInverseDistances;
    }

    //////////////////////////////////// PATTERN COMPARISON /////////////////////////////////////////////

    /**Fonction permettant de comparer les valeurs de RGB des 3 couleurs de la photo à un pattern prédéfini
     * @param RGBs valeurs de RGB des 3 couleurs de la photo
     * @param patternID ID du pattern à évaluer
     * @return renvoie la probabilité (double) que les couleurs de la photo correspondent au pattern choisi
     */
    public static double compareThreeRGBsToPattern(int[][] RGBs, int patternID){
        Colors[] pattern= Patterns.getPatternFromID(patternID);
        double finalProbability = 1;
        for (int i=0; i<3; i++){
            int colorID=pattern[i].getID();
            double[] tempProbabilities=computeProbabilitiesColors(RGBs[i]);
            finalProbability*=tempProbabilities[colorID];
        }
        return finalProbability;
    }


    /**Fonction permettant de comparer les valeurs de RGB des 3 couleurs de la photo à tous les autres patterns
     * @param RGBs valeurs de RGB des 3 couleurs de la photo
     * @return renvoie la liste (double[nbPatterns=10]) des probabilités que les couleurs de la photo correspondent à un des pattern
     */
    public static double[] compareThreeRGBsToAllPatterns(int[][] RGBs){
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
    public static double[] encapsulationThreeFourFive(int[][][] colorMatrix, int[] xstarts, int[] ystarts, int[] xends, int[] yends) {
        int[] medianFirstColor = getRGBMedianValues(colorMatrix, xstarts[0], ystarts[0], xends[0], yends[0]);
        int[] medianSecondColor = getRGBMedianValues(colorMatrix, xstarts[1], ystarts[1], xends[1], yends[1]);
        int[] medianThirdColor = getRGBMedianValues(colorMatrix, xstarts[2], ystarts[2], xends[2], yends[2]);
        int[][] mediansListTemp = {medianFirstColor, medianSecondColor, medianThirdColor};
        for (int i=0; i<3; i++) {
            mediansList[i] = mediansListTemp[i];
        }
        double[] probabilitiesList = compareThreeRGBsToAllPatterns(mediansList);
        double[] normalizedList=normalizeDoubleList(probabilitiesList);
        return normalizedList;
    }

    //////////////////////////////////// DISCRIMINATION IN CASE OF CONFLICT /////////////////////////////////////////////

    /**Fonction permettant de déterminer les patterns qui sont viables au vu de la liste de probabilités
     * @param probabilitiesList liste normalisée de probabilités que le pattern pris en photo corresponde aux patterns prédéfinis
     * @return renvoie la liste des indices des patterns plausibles
     */
    public static ArrayList<Integer> selectBestProbabilities(double[] probabilitiesList){
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

    /**Discrimine le meilleur indice en cas de conflit de patterns (mode de discrimination : prend le plus probable)
     * @param probabilitiesList liste des probabilités qu'un pattern connu soit identifié
     * @param selectedProbabilitiesIndice id des patterns plausibles
     * @return indice (int) du pattern sélectionné
     */
    public static int discriminateLastIndice(double[] probabilitiesList, ArrayList<Integer> selectedProbabilitiesIndice){
        //Si des conflits de patterns se présentent, on peut calibrer la luminosité et le contraste de l'image en fonction de la zone au dessus des patterns sur la table de la coupe
        //OU si conflits de patterns, on peut augmenter la luminosité et le contraste de +30 +30 (valeurs de gimp, en % ?)
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
     * @param mediansList liste médiane (définie au début de la classe), contenant la médiane en R, G et B, pour chacune des 3 couleurs de la photo
     * @return renvoie l'indice finalement choisi
     */
    public static int computeFinalIndice(double[] probabilitiesList, int[][]mediansList) {
        ArrayList selectionnedProbabilitiesIndice = selectBestProbabilities(probabilitiesList);
        System.out.println(selectionnedProbabilitiesIndice.toString());
        int finalIndice = 10;
        if (selectionnedProbabilitiesIndice.size() > 1) {
            finalIndice = discriminateLastIndice(probabilitiesList, selectionnedProbabilitiesIndice);
        } else {
            finalIndice = (Integer) selectionnedProbabilitiesIndice.get(0);
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

    //AVANCEMENT :
    // 1) Fait et testé ( int[][][] colorMatrix <== createColorMatrix(String pathToImage) )
    // 2) Non fait
    // 2.5) Non fait
    // 3) Fait ( int[] medianFirst/Second/ThirdColor <== getRGBMedianValues(int[][][] colorMatrix, int xstart, int ystart, int xend, int yend) )
    // 4) Fait ( double[] probabilitiesList <== compareThreeRGBsToAllPatterns(int[][] RGBs) )
    // 5) Fait ( String[] colorNames <== selectBestProbability(double[] probabilitiesList) )

    //GIMP TRAITEMENT :
    //Ajout Luminosité/Contraste : indices 50/50

    //////////////////////////////////// MAIN /////////////////////////////////////////////

    public static void main(String[] args) {
        //ZONE DE TEST DU MAIN

        //String pathToImage = "Patterns.jpg";
        //String pathToImage = "PatternsLitUp.jpg";

        //Test positions for Patterns
        //int[][] positionsColorsOnImage={{253,481,717},{840,835,846},{402,603,861},{991,999,993}}; //Pattern 0 WORKS NORMAL/LIT 0.97/0.99
        //int[][] positionsColorsOnImage={{253,492,718},{1130,1130,1130},{400,633,862},{1275,1275,1275}}; //Pattern 1 WORKS LIT faux/0.94
        //int[][] positionsColorsOnImage={{255,489,717},{1408,1408,1408},{398,629,852},{1553,1553,1553}}; //Pattern 2 WORKS LIT DISCRIMINATION
        //int[][] positionsColorsOnImage={{254,490,714},{1687,1687,1687},{394,628,861},{1840,1840,1840}}; //Pattern 3 WORKS NORMAL/LIT /0.99
        //int[][] positionsColorsOnImage={{254,490,714},{1975,1975,1975},{394,624,855},{2133,2133,2133}}; //Pattern 4 WORKS NORMAL/LIT /0.99
        //int[][] positionsColorsOnImage={{972,1209,1449},{838,832,832},{1117,1354,1582},{987,987,987}}; //Pattern 5 WORKS LIT faux/0.93
        //int[][] positionsColorsOnImage={{972,1210,1450},{1126,1126,1126},{1119,1350,1580},{1272,1272,1272}}; //Pattern 6 WORKS NORMAL/LIT /0.99
        //int[][] positionsColorsOnImage={{979,1210,1450},{1409,1409,1409},{1119,1350,1580},{1558,1558,1558}}; //Pattern 7 WORKS NORMAL/LIT /0.99
        //int[][] positionsColorsOnImage={{979,1210,1450},{1700,1700,1700},{1119,1350,1580},{1847,1847,1847}};// Pattern 8 WORKS NORMAL/LIT /0.88
        //int[][] positionsColorsOnImage={{979,1210,1450},{1980,1980,1980},{1119,1350,1850},{2133,2133,2133}}; //Pattern 9 WORKS LIT faux/0.81

        String pathToImage = "ImageRaspberryPi3+30+30.png";


        //ImageRaspberryPi3.png
        int[][] posVert={{1381,903},{1428,951}};
        int[][] posNoir={{1313,687},{1357,758}};
        int[][] posBleu={{1399,679},{1441,738}};
        int[][] posJaune={{1464,866},{1509,918}};
        int[][] posOrange={{1552,832},{1591,879}};


        /*
        //ImageRaspberryPi5.png
        int[][] posVert={{966,405},{999,455}};
        int[][] posNoir={{892,173},{933,265}};
        int[][] posBleu={{967,202},{1004,277}};
        int[][] posJaune={{1047,397},{1083,445}};
        int[][] posOrange={{1125,386},{1155,421}};
        */

        //Changer les tests de patterns ICI
        //Si on souhaite mettre le pattern YWGNBK, alors on écrit : pat={posYellow,posGreen,posBlack}
        //(terminologie des noms des patterns dans la classe Patterns.java)
        //int[][][] pat={posOrange,posNoir,posVert};
        //int[][][] pat={posJaune,posNoir,posBleu};
        //int[][][] pat={posBleu,posVert,posOrange};
        //int[][][] pat={posJaune,posVert,posNoir};
        //int[][][] pat={posNoir,posJaune,posOrange};
        //int[][][] pat={posVert,posJaune,posBleu};
        //int[][][] pat={posBleu,posOrange,posNoir};
        //int[][][] pat={posVert,posOrange,posJaune};
        //int[][][] pat={posNoir,posBleu,posVert};
        int[][][] pat={posOrange,posBleu,posJaune};


        //Ghetto, mais ne pas toucher tant qu'on est en test
        int[][] positionsColorsOnImage={{pat[0][0][0],pat[1][0][0],pat[2][0][0]},{pat[0][0][1],pat[1][0][1],pat[2][0][1]},{pat[0][1][0],pat[1][1][0],pat[2][1][0]},{pat[0][1][1],pat[1][1][1],pat[2][1][1]}};


        //CALIBRER SUR UNE IMAGE SOMBRE
        //PARTIE A NE PAS TOUCHER DU MAIN
        int[][][] colorMatrix = createColorMatrix(pathToImage);
        double[] probabilitiesList = encapsulationThreeFourFive(colorMatrix, positionsColorsOnImage[0], positionsColorsOnImage[1], positionsColorsOnImage[2], positionsColorsOnImage[3]);

        int finalIndice=computeFinalIndice(probabilitiesList,mediansList);
        for (int i = 0; i<10; i++) {
            System.out.println(probabilitiesList[i]);
        }
        System.out.println("Victory "+finalIndice);
    }
}