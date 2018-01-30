package tests;


import org.junit.Test;
import patternRecognition.PatternRecognition;

public class JUnit_PatternRecognition extends JUnit_Test {

    @Test
    public void testReconnaissanceTotale(){
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

        String pathToImage = "ImageRaspberryPi5.png";

        /*
        //ImageRaspberryPi3.png
        int[][] posVert={{1381,903},{1428,951}};
        int[][] posNoir={{1313,687},{1357,758}};
        int[][] posBleu={{1399,679},{1441,738}};
        int[][] posJaune={{1464,866},{1509,918}};
        int[][] posOrange={{1552,832},{1591,879}};
        */


        //ImageRaspberryPi5.png
        int[][] posVert={{966,405},{999,455}};
        int[][] posNoir={{892,173},{933,265}};
        int[][] posBleu={{967,202},{1004,277}};
        int[][] posJaune={{1047,397},{1083,445}};
        int[][] posOrange={{1125,386},{1155,421}};


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
        int[][][] pat={posVert,posOrange,posJaune};
        //int[][][] pat={posNoir,posBleu,posVert};
        //int[][][] pat={posOrange,posBleu,posJaune};

        //Si symmetry
        boolean symmetry=false;
        if (symmetry) {
            int[][] temp = pat[0];
            pat[0] = pat[2];
            pat[2] = temp;
        }

        //Ghetto, mais ne pas toucher tant qu'on est en test
        int[][] positionsColorsOnImage=
                {{pat[0][0][0],pat[1][0][0],pat[2][0][0]},
                 {pat[0][0][1],pat[1][0][1],pat[2][0][1]},
                 {pat[0][1][0],pat[1][1][0],pat[2][1][0]},
                 {pat[0][1][1],pat[1][1][1],pat[2][1][1]}};

        boolean debug=false;
        PatternRecognition.setDebugPatternRecognition(debug);

        int victoryPattern=PatternRecognition.analysePattern(pathToImage, pat, positionsColorsOnImage);
        log.debug("Pattern found : "+victoryPattern);
    }

}
