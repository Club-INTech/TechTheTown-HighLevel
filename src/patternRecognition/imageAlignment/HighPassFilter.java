package patternRecognition.imageAlignment;

public class HighPassFilter{
    private static boolean debug=true;

    public static int[][][] keepWhite(int[][][] colorMatrix, int seuil){
        for (int y = 0; y < colorMatrix[0].length; y++) {
            for (int x = 0; x < colorMatrix.length; x++) {
                if (colorMatrix[x][y][0]>seuil && colorMatrix[x][y][1]>seuil && colorMatrix[x][y][1]>seuil){
                    colorMatrix[x][y][0]=255;
                    colorMatrix[x][y][1]=255;
                    colorMatrix[x][y][2]=255;
                }
            }
        }
        return colorMatrix;
    }

    public static int[][][] highPassFiltering(int[][][] colorMatrix, int[][] selectedZone){
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
                passeHautMatrix[x][y][0]=0;
                passeHautMatrix[x][y][1]=0;
                passeHautMatrix[x][y][2]=0;
                boolean edge=false;
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        if (x + i >= xdebut && x + i <= xfin) {
                            if (y + j >= ydebut && y + j <= yfin) {
                                if (i == 0 && j == 0) {
                                    passeHautMatrix[x][y][0] += 8 * colorMatrix[x+i][y+j][0];
                                    passeHautMatrix[x][y][1] += 8 * colorMatrix[x+i][y+j][1];
                                    passeHautMatrix[x][y][2] += 8 * colorMatrix[x+i][y+j][2];
                                } else {
                                    passeHautMatrix[x][y][0] -= colorMatrix[x+i][y+j][0];
                                    passeHautMatrix[x][y][1] -= colorMatrix[x+i][y+j][1];
                                    passeHautMatrix[x][y][2] -= colorMatrix[x+i][y+j][2];
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
                passeHautMatrix[x][y][0]*=(double)1/8;
                passeHautMatrix[x][y][1]*=(double)1/8;
                passeHautMatrix[x][y][2]*=(double)1/8;
                if (edge){
                    passeHautMatrix[x][y][0]=0;
                    passeHautMatrix[x][y][1]=0;
                    passeHautMatrix[x][y][2]=0;
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
        for (int y = 0; y < passeHautMatrix[0].length; y++) {
            for (int x = 0; x < passeHautMatrix.length; x++) {
                passeHautMatrix[x][y][0]*=255;
                passeHautMatrix[x][y][0]=(int)(passeHautMatrix[x][y][0]/(float)maxRed);
                if (passeHautMatrix[x][y][0]<0){
                    passeHautMatrix[x][y][0]=0;
                }

                passeHautMatrix[x][y][1]*=255;
                passeHautMatrix[x][y][1]=(int)(passeHautMatrix[x][y][1]/(float)maxGreen);
                if (passeHautMatrix[x][y][1]<0){
                    passeHautMatrix[x][y][1]=0;
                }

                passeHautMatrix[x][y][2]*=255;
                passeHautMatrix[x][y][2]=(int)(passeHautMatrix[x][y][2]/(float)maxBlue);
                if (passeHautMatrix[x][y][2]<0){
                    passeHautMatrix[x][y][2]=0;
                }
            }
        }

        return passeHautMatrix;
    }

}
