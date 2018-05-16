package image.analysis;

public class PictureEncodingConversion {

   /*
                    |-------------------------------------------|
                    |                                           |
                    |            RGB / HSB conversion           |
                    |                                           |
                    |-------------------------------------------|
    */

    /**
     * Donne l'équivalent RGB d'une partie d'image HSB
     * @param picture l'image
     * @param xStart l'abscisse de début de la zone de traitement
     * @param yStart l'ordonnée de début de la zone de traitement
     * @param width la largeur de la zone de traitement
     * @param height la hauteur de la zone de traitement
     * @return renvoie l'équivalent RGB d'une partie d'image HSB
     */
    public static Integer[][][] getRGBfromHSB(PictureHSB picture, int xStart, int yStart, int width, int height, boolean canGoOutOfBounds){
        if (width>0 && height>0) {
            int pictureWidth = picture.getWidth();
            int pictureHeight = picture.getHeight();
            if (xStart < pictureWidth && yStart < pictureHeight) {
                if (canGoOutOfBounds) {
                    width = Math.min(pictureWidth - xStart, width);
                    height = Math.min(pictureHeight - yStart, height);
                    xStart = Math.max(xStart, 0);
                    yStart = Math.max(yStart, 0);
                }
                if (xStart + width <= pictureWidth && yStart + height <= pictureHeight && xStart >= 0 && yStart >= 0) {
                    Float[][][] hsbArray = picture.getImageArray();
                    Integer[][][] rgbArray = new Integer[width][height][3];
                    for (int x = xStart; x < xStart + width; x++) {
                        for (int y = yStart; y < yStart + height; y++) {
                            getRGBfromHSB(hsbArray[x][y], rgbArray[x][y]);
                        }
                    }
                    return rgbArray;
                } else {
                    System.out.println("Bad parameters given:");
                    System.out.println("xStart:" + xStart + " yStart:" + yStart + " width:" + width + " height:" + height);
                    System.out.println("Picture width:" + pictureWidth + " Picture height:" + pictureHeight);
                    return null;
                }
            }
            else{
                System.out.println("Bad size given:");
                System.out.println("xStart:" + xStart + " yStart:" + yStart + " width:" + width + " height:" + height);
                System.out.println("Picture width:" + pictureWidth + " Picture height:" + pictureHeight);
                System.out.println("xStart and yStart can go from 0 to respectively pictureWidth-1 and pictureHeight-1");
                return null;
            }
        }
        else{
            System.out.println("Width("+width+") or height("+height+") is negative or null");
            return null;
        }
    }

    /** Convertit une couleur en RGB à partir d'une couleur en HSB
     * @param fromColor array des composantes de la couleur RGB de laquelle partir
     * @param toColor array des composantes de la couleur HSB qu'on recoit
     */
    public static void getRGBfromHSB(Float[] fromColor, Integer[] toColor){
        if (fromColor[1] == 0) {
            toColor[0] = toColor[1] = toColor[2] = (int) (fromColor[2] * 255.0f + 0.5f);
        } else {
            float h = (fromColor[0] - (float) Math.floor(fromColor[0])) * 6.0f;
            switch ((int) h) {
                case 0:
                    toColor[0] = (int) (fromColor[2] * 255.0f + 0.5f);
                    toColor[1] = (int) (fromColor[2] * (1.0f - (fromColor[1] * (1.0f - (h - (float) java.lang.Math.floor(h))))) * 255.0f + 0.5f);
                    toColor[2] = (int) (fromColor[2] * (1.0f - fromColor[1]) * 255.0f + 0.5f);
                    break;
                case 1:
                    toColor[0] = (int) (fromColor[2] * (1.0f - fromColor[1] * (h - (float) java.lang.Math.floor(h))) * 255.0f + 0.5f);
                    toColor[1] = (int) (fromColor[2] * 255.0f + 0.5f);
                    toColor[2] = (int) (fromColor[2] * (1.0f - fromColor[1]) * 255.0f + 0.5f);
                    break;
                case 2:
                    toColor[0] = (int) (fromColor[2] * (1.0f - fromColor[1]) * 255.0f + 0.5f);
                    toColor[1] = (int) (fromColor[2] * 255.0f + 0.5f);
                    toColor[2] = (int) (fromColor[2] * (1.0f - (fromColor[1] * (1.0f - (h - (float) java.lang.Math.floor(h))))) * 255.0f + 0.5f);
                    break;
                case 3:
                    toColor[0] = (int) (fromColor[2] * (1.0f - fromColor[1]) * 255.0f + 0.5f);
                    toColor[1] = (int) (fromColor[2] * (1.0f - fromColor[1] * (h - (float) java.lang.Math.floor(h))) * 255.0f + 0.5f);
                    toColor[2] = (int) (fromColor[2] * 255.0f + 0.5f);
                    break;
                case 4:
                    toColor[0] = (int) (fromColor[2] * (1.0f - (fromColor[1] * (1.0f - (h - (float) java.lang.Math.floor(h))))) * 255.0f + 0.5f);
                    toColor[1] = (int) (fromColor[2] * (1.0f - fromColor[1]) * 255.0f + 0.5f);
                    toColor[2] = (int) (fromColor[2] * 255.0f + 0.5f);
                    break;
                case 5:
                    toColor[0] = (int) (fromColor[2] * 255.0f + 0.5f);
                    toColor[1] = (int) (fromColor[2] * (1.0f - fromColor[1]) * 255.0f + 0.5f);
                    toColor[2] = (int) (fromColor[2] * (1.0f - fromColor[1] * (h - (float) java.lang.Math.floor(h))) * 255.0f + 0.5f);
                    break;
            }
        }
    }

    /**
     * Donne l'équivalent HSB d'une partie d'image RGB
     * @param picture l'image
     * @param xStart l'abscisse de début de la zone de traitement
     * @param yStart l'ordonnée de début de la zone de traitement
     * @param width la largeur de la zone de traitement
     * @param height la hauteur de la zone de traitement
     * @return renvoie l'équivalent HSB d'une partie d'image RGB
     */
    public static Float[][][] getHSBfromRGB(PictureRGB picture, int xStart, int yStart, int width, int height, boolean canGoOutOfBounds){
        if (width>0 && height>0) {
            int pictureWidth = picture.getWidth();
            int pictureHeight = picture.getHeight();
            if (xStart < pictureWidth && yStart < pictureHeight) {
                if (canGoOutOfBounds) {
                    width = Math.min(pictureWidth - xStart, width);
                    height = Math.min(pictureHeight - yStart, height);
                    xStart = Math.max(xStart, 0);
                    yStart = Math.max(yStart, 0);
                }
                if (xStart + width <= pictureWidth && yStart + height <= pictureHeight && xStart >= 0 && yStart >= 0) {
                    Integer[][][] rgbArray = picture.getImageArray();
                    Float[][][] hsbArray = new Float[width][height][3];
                    for (int x = xStart; x < xStart + width; x++) {
                        for (int y = yStart; y < yStart + height; y++) {
                            getHSBfromRGB(rgbArray[x][y],hsbArray[x][y]);
                        }
                    }
                    return hsbArray;
                } else {
                    System.out.println("Bad parameters given:");
                    System.out.println("xStart:" + xStart + " yStart:" + yStart + " width:" + width + " height:" + height);
                    System.out.println("Picture width:" + pictureWidth + " Picture height:" + pictureHeight);
                    return null;
                }
            }
            else{
                System.out.println("Bad size given:");
                System.out.println("xStart:" + xStart + " yStart:" + yStart + " width:" + width + " height:" + height);
                System.out.println("Picture width:" + pictureWidth + " Picture height:" + pictureHeight);
                System.out.println("xStart and yStart can go from 0 to respectively pictureWidth-1 and pictureHeight-1");
                return null;
            }
        }
        else{
            System.out.println("Width("+width+") or height("+height+") is negative or null");
            return null;
        }
    }


    /** Convertit une couleur en HSB à partir d'une couleur en RGB
     * @param fromColor array des composantes de la couleur HSB de laquelle partir
     * @param toColor array des composantes de la couleur RGB qu'on recoit
     */
    public static void getHSBfromRGB(Integer[] fromColor, Float[] toColor) {
        int cmax = (fromColor[0] > fromColor[1]) ? fromColor[0] : fromColor[1];
        if (fromColor[2] > cmax) cmax = fromColor[2];
        int cmin = (fromColor[0] < fromColor[1]) ? fromColor[0] : fromColor[1];
        if (fromColor[2] < cmin) cmin = fromColor[2];

        toColor[2] = ((float) cmax) / 255.0f;
        if (cmax != 0) {
            toColor[1] = ((float) (cmax - cmin)) / ((float) cmax);
        } else {
            toColor[1] = (float) 0;
        }
        if (toColor[1] == 0) {
            toColor[0] = (float) 0;
        } else {
            float redc = ((float) (cmax - fromColor[0])) / ((float) (cmax - cmin));
            float greenc = ((float) (cmax - fromColor[1])) / ((float) (cmax - cmin));
            float bluec = ((float) (cmax - fromColor[2])) / ((float) (cmax - cmin));
            if (fromColor[0] == cmax) {
                toColor[0] = bluec - greenc;
            } else if (fromColor[1] == cmax) {
                toColor[0] = 2.0f + redc - bluec;
            } else {
                toColor[0] = 4.0f + greenc - redc;
            }
            toColor[0] = toColor[0] / 6.0f;
            if (toColor[0] < 0) {
                toColor[0] = toColor[0] + 1.0f;
            }
        }
    }


    /*
                    |-------------------------------------------|
                    |                                           |
                    |            RGB / BGR conversion           |
                    |                                           |
                    |-------------------------------------------|
     */

    /**
     * Donne l'équivalent RGB d'une partie d'image BGR
     * @param picture l'image
     * @param xStart l'abscisse de début de la zone de traitement
     * @param yStart l'ordonnée de début de la zone de traitement
     * @param width la largeur de la zone de traitement
     * @param height la hauteur de la zone de traitement
     * @return renvoie l'équivalent RGB d'une partie d'image BGR
     */
    public static Integer[][][] getRGBfromBGR(PictureBGR picture, int xStart, int yStart, int width, int height, boolean canGoOutOfBounds) {
        if (width>0 && height>0) {
            int pictureWidth = picture.getWidth();
            int pictureHeight = picture.getHeight();
            if (xStart < pictureWidth && yStart < pictureHeight) {
                if (canGoOutOfBounds) {
                    width = Math.min(pictureWidth - xStart, width);
                    height = Math.min(pictureHeight - yStart, height);
                    xStart = Math.max(xStart, 0);
                    yStart = Math.max(yStart, 0);
                }
                if (xStart + width <= pictureWidth && yStart + height <= pictureHeight && xStart >= 0 && yStart >= 0) {
                    Integer[][][] bgrArray = picture.getImageArray();
                    Integer[][][] rgbArray = new Integer[width][height][3];
                    for (int x = xStart; x < xStart + width; x++) {
                        for (int y = yStart; y < yStart + height; y++) {
                            getRGBfromBGR(bgrArray[x][y], rgbArray[x][y]);
                        }
                    }
                    return rgbArray;
                } else {
                    System.out.println("Bad parameters given:");
                    System.out.println("xStart:" + xStart + " yStart:" + yStart + " width:" + width + " height:" + height);
                    System.out.println("Picture width:" + pictureWidth + " Picture height:" + pictureHeight);
                    return null;
                }
            }
            else{
                System.out.println("Bad size given:");
                System.out.println("xStart:" + xStart + " yStart:" + yStart + " width:" + width + " height:" + height);
                System.out.println("Picture width:" + pictureWidth + " Picture height:" + pictureHeight);
                System.out.println("xStart and yStart can go from 0 to respectively pictureWidth-1 and pictureHeight-1");
                return null;
            }
        }
        else{
            System.out.println("Width("+width+") or height("+height+") is negative or null");
            return null;
        }
    }


    /** Convertit une couleur en BGR à partir d'une couleur en RGB
     * @param fromColor array des composantes de la couleur BGR de laquelle partir
     * @param toColor array des composantes de la couleur RGB qu'on recoit
     */
    public static void getRGBfromBGR(Integer[] fromColor, Integer[] toColor){
        toColor[0]=fromColor[2];
        toColor[1]=fromColor[1];
        toColor[2]=fromColor[0];
    }
    /**
     * Donne l'équivalent BGR d'une partie d'image RGB
     * @param picture l'image
     * @param xStart l'abscisse de début de la zone de traitement
     * @param yStart l'ordonnée de début de la zone de traitement
     * @param width la largeur de la zone de traitement
     * @param height la hauteur de la zone de traitement
     * @return renvoie l'équivalent BGR d'une partie d'image RGB
     */
    public static Integer[][][] getBGRfromRGB(PictureRGB picture, int xStart, int yStart, int width, int height, boolean canGoOutOfBounds) {
        if (width>0 && height>0) {
            int pictureWidth = picture.getWidth();
            int pictureHeight = picture.getHeight();
            if (xStart < pictureWidth && yStart < pictureHeight) {
                if (canGoOutOfBounds) {
                    width = Math.min(pictureWidth - xStart, width);
                    height = Math.min(pictureHeight - yStart, height);
                    xStart = Math.max(xStart, 0);
                    yStart = Math.max(yStart, 0);
                }
                if (xStart + width <= pictureWidth && yStart + height <= pictureHeight && xStart >= 0 && yStart >= 0) {
                    Integer[][][] rgbArray = picture.getImageArray();
                    Integer[][][] bgrArray = new Integer[width][height][3];
                    for (int x = xStart; x < xStart + width; x++) {
                        for (int y = yStart; y < yStart + height; y++) {
                            getBGRfromRGB(rgbArray[x][y], bgrArray[x][y]);
                        }
                    }
                    return bgrArray;
                } else {
                    System.out.println("Bad parameters given:");
                    System.out.println("xStart:" + xStart + " yStart:" + yStart + " width:" + width + " height:" + height);
                    System.out.println("Picture width:" + pictureWidth + " Picture height:" + pictureHeight);
                    return null;
                }
            }
            else{
                System.out.println("Bad size given:");
                System.out.println("xStart:" + xStart + " yStart:" + yStart + " width:" + width + " height:" + height);
                System.out.println("Picture width:" + pictureWidth + " Picture height:" + pictureHeight);
                System.out.println("xStart and yStart can go from 0 to respectively pictureWidth-1 and pictureHeight-1");
                return null;
            }
        }
        else{
            System.out.println("Width("+width+") or height("+height+") is negative or null");
            return null;
        }
    }

    /** Convertit une couleur en BGR à partir d'une couleur en RGB
     * @param fromColor array des composantes de la couleur RGB de laquelle partir
     * @param toColor array des composantes de la couleur BGR qu'on recoit
     */
    public static void getBGRfromRGB(Integer[] fromColor, Integer[] toColor){
        toColor[0]=fromColor[2];
        toColor[1]=fromColor[1];
        toColor[2]=fromColor[0];
    }


    /*
                    |-------------------------------------------|
                    |                                           |
                    |            HSB / BGR conversion           |
                    |                                           |
                    |-------------------------------------------|
     */


    /**
     * Donne l'équivalent BGR d'une partie d'image HSB
     * @param picture l'image
     * @param xStart l'abscisse de début de la zone de traitement
     * @param yStart l'ordonnée de début de la zone de traitement
     * @param width la largeur de la zone de traitement
     * @param height la hauteur de la zone de traitement
     * @return Renvoie l'équivalent BGR d'une partie d'image HSB
     */
    public static Integer[][][] getBGRfromHSB(PictureHSB picture, int xStart, int yStart, int width, int height, boolean canGoOutOfBounds){
        if (width>0 && height>0) {
            int pictureWidth = picture.getWidth();
            int pictureHeight = picture.getHeight();
            if (xStart < pictureWidth && yStart < pictureHeight) {
                if (canGoOutOfBounds) {
                    width = Math.min(pictureWidth - xStart, width);
                    height = Math.min(pictureHeight - yStart, height);
                    xStart = Math.max(xStart, 0);
                    yStart = Math.max(yStart, 0);
                }
                if (xStart + width <= pictureWidth && yStart + height <= pictureHeight && xStart >= 0 && yStart >= 0) {
                    Float[][][] hsbArray = picture.getImageArray();
                    Integer[][][] bgrArray = new Integer[width][height][3];
                    for (int x = xStart; x < xStart + width; x++) {
                        for (int y = yStart; y < yStart + height; y++) {
                            getBGRfromHSB(hsbArray[x][y], bgrArray[x][y]);
                        }
                    }
                    return bgrArray;
                } else {
                    System.out.println("Bad parameters given:");
                    System.out.println("xStart:" + xStart + " yStart:" + yStart + " width:" + width + " height:" + height);
                    System.out.println("Picture width:" + pictureWidth + " Picture height:" + pictureHeight);
                    return null;
                }
            }
            else{
                System.out.println("Bad size given:");
                System.out.println("xStart:" + xStart + " yStart:" + yStart + " width:" + width + " height:" + height);
                System.out.println("Picture width:" + pictureWidth + " Picture height:" + pictureHeight);
                System.out.println("xStart and yStart can go from 0 to respectively pictureWidth-1 and pictureHeight-1");
                return null;
            }
        }
        else{
            System.out.println("Width("+width+") or height("+height+") is negative or null");
            return null;
        }
    }

    /** Convertit une couleur en BGR à partir d'une couleur en HSB
     * @param fromColor array des composantes de la couleur HSB de laquelle partir
     * @param toColor array des composantes de la couleur BGR qu'on recoit
     */
    public static void getBGRfromHSB(Float[] fromColor, Integer[] toColor){
        if (fromColor[1] == 0) {
            toColor[0] = toColor[1] = toColor[2] = (int) (fromColor[2] * 255.0f + 0.5f);
        } else {
            float h = (fromColor[0] - (float) Math.floor(fromColor[0])) * 6.0f;
            switch ((int) h) {
                case 0:
                    toColor[2] = (int) (fromColor[2] * 255.0f + 0.5f);
                    toColor[1] = (int) (fromColor[2] * (1.0f - (fromColor[1] * (1.0f - (h - (float) java.lang.Math.floor(h))))) * 255.0f + 0.5f);
                    toColor[0] = (int) (fromColor[2] * (1.0f - fromColor[1]) * 255.0f + 0.5f);
                    break;
                case 1:
                    toColor[2] = (int) (fromColor[2] * (1.0f - fromColor[1] * (h - (float) java.lang.Math.floor(h))) * 255.0f + 0.5f);
                    toColor[1] = (int) (fromColor[2] * 255.0f + 0.5f);
                    toColor[0] = (int) (fromColor[2] * (1.0f - fromColor[1]) * 255.0f + 0.5f);
                    break;
                case 2:
                    toColor[2] = (int) (fromColor[2] * (1.0f - fromColor[1]) * 255.0f + 0.5f);
                    toColor[1] = (int) (fromColor[2] * 255.0f + 0.5f);
                    toColor[0] = (int) (fromColor[2] * (1.0f - (fromColor[1] * (1.0f - (h - (float) java.lang.Math.floor(h))))) * 255.0f + 0.5f);
                    break;
                case 3:
                    toColor[2] = (int) (fromColor[2] * (1.0f - fromColor[1]) * 255.0f + 0.5f);
                    toColor[1] = (int) (fromColor[2] * (1.0f - fromColor[1] * (h - (float) java.lang.Math.floor(h))) * 255.0f + 0.5f);
                    toColor[0] = (int) (fromColor[2] * 255.0f + 0.5f);
                    break;
                case 4:
                    toColor[2] = (int) (fromColor[2] * (1.0f - (fromColor[1] * (1.0f - (h - (float) java.lang.Math.floor(h))))) * 255.0f + 0.5f);
                    toColor[1] = (int) (fromColor[2] * (1.0f - fromColor[1]) * 255.0f + 0.5f);
                    toColor[0] = (int) (fromColor[2] * 255.0f + 0.5f);
                    break;
                case 5:
                    toColor[2] = (int) (fromColor[2] * 255.0f + 0.5f);
                    toColor[1] = (int) (fromColor[2] * (1.0f - fromColor[1]) * 255.0f + 0.5f);
                    toColor[0] = (int) (fromColor[2] * (1.0f - fromColor[1] * (h - (float) java.lang.Math.floor(h))) * 255.0f + 0.5f);
                    break;
            }
        }
    }

    /**
     * Donne l'équivalent HSB d'une partie d'image BGR
     * @param picture l'image
     * @param xStart l'abscisse de début de la zone de traitement
     * @param yStart l'ordonnée de début de la zone de traitement
     * @param width la largeur de la zone de traitement
     * @param height la hauteur de la zone de traitement
     * @return Renvoie l'équivalent HSB d'une partie d'image BGR
     */
    public static Float[][][] getHSBfromBGR(PictureBGR picture, int xStart, int yStart, int width, int height, boolean canGoOutOfBounds){
        if (width>0 && height>0) {
            int pictureWidth = picture.getWidth();
            int pictureHeight = picture.getHeight();
            if (xStart < pictureWidth && yStart < pictureHeight) {
                if (canGoOutOfBounds) {
                    width = Math.min(pictureWidth - xStart, width);
                    height = Math.min(pictureHeight - yStart, height);
                    xStart = Math.max(xStart, 0);
                    yStart = Math.max(yStart, 0);
                }
                if (xStart + width <= pictureWidth && yStart + height <= pictureHeight && xStart >= 0 && yStart >= 0) {
                    Integer[][][] bgrArray = picture.getImageArray();
                    Float[][][] hsbArray = new Float[width][height][3];
                    for (int x = xStart; x < xStart + width; x++) {
                        for (int y = yStart; y < yStart + height; y++) {
                            getHSBfromBGR(bgrArray[x][y],hsbArray[x][y]);
                        }
                    }
                    return hsbArray;
                } else {
                    System.out.println("Bad parameters given:");
                    System.out.println("xStart:" + xStart + " yStart:" + yStart + " width:" + width + " height:" + height);
                    System.out.println("Picture width:" + pictureWidth + " Picture height:" + pictureHeight);
                    return null;
                }
            }
            else{
                System.out.println("Bad size given:");
                System.out.println("xStart:" + xStart + " yStart:" + yStart + " width:" + width + " height:" + height);
                System.out.println("Picture width:" + pictureWidth + " Picture height:" + pictureHeight);
                System.out.println("xStart and yStart can go from 0 to respectively pictureWidth-1 and pictureHeight-1");
                return null;
            }
        }
        else{
            System.out.println("Width("+width+") or height("+height+") is negative or null");
            return null;
        }
    }


    /** Convertit une couleur en HSB à partir d'une couleur en BGR
     * @param fromColor array des composantes de la couleur BGR de laquelle partir
     * @param toColor array des composantes de la couleur HSB qu'on recoit
     */
    public static void getHSBfromBGR(Integer[] fromColor, Float[] toColor){
        int cmax = (fromColor[2] > fromColor[1]) ? fromColor[2] : fromColor[1];
        if (fromColor[0] > cmax) cmax = fromColor[0];
        int cmin = (fromColor[2] < fromColor[1]) ? fromColor[2] : fromColor[1];
        if (fromColor[0] < cmin) cmin = fromColor[0];

        toColor[2] = ((float) cmax) / 255.0f;
        if (cmax != 0) {
            toColor[1] = ((float) (cmax - cmin)) / ((float) cmax);
        }
        else {
            toColor[1] = (float)0;
        }
        if (toColor[1] == 0) {
            toColor[0] = (float)0;
        }
        else {
            float redc = ((float) (cmax - fromColor[0])) / ((float) (cmax - cmin));
            float greenc = ((float) (cmax - fromColor[1])) / ((float) (cmax - cmin));
            float bluec = ((float) (cmax - fromColor[2])) / ((float) (cmax - cmin));
            if (fromColor[2] == cmax) {
                toColor[0] = bluec - greenc;
            }
            else if (fromColor[1] == cmax) {
                toColor[0] = 2.0f + redc - bluec;
            }
            else {
                toColor[0] = 4.0f + greenc - redc;
            }
            toColor[0] = toColor[0] / 6.0f;
            if (toColor[0] < 0) {
                toColor[0] = toColor[0] + 1.0f;
            }
        }
    }
}
