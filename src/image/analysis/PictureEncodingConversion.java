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
                            rgbArray[x][y] = getRGBfromHSB(hsbArray[x][y][0], hsbArray[x][y][1], hsbArray[x][y][2]);
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
     * @param hue angle de la couleur (rouge, orange, jaune, vert, bleu, violet...)
     * @param saturation saturation de la couleur (coloré ou décoloré)
     * @param brightness luminosité de la couleur (éclairé ou sombre)
     * @return renvoie une couleur en RGB
     */
    public static Integer[] getRGBfromHSB(float hue, float saturation, float brightness){
        int r=0;
        int g=0;
        int b=0;
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
        return new Integer[]{r,g,b};
    }

    /**
     * Convertit une couleur HSB en RGB
     * @param hsb le triplé ordonné HSB
     * @return renvoie le triplé ordonné RGB
     */
    public static Integer[] getRGBfromHSB(float[] hsb){
        if (hsb.length==3){
            return getRGBfromHSB(hsb[0],hsb[1],hsb[2]);
        }
        else{
            System.out.println("Bad length of given HSB");
            return null;
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
                            hsbArray[x][y] = getHSBfromRGB(rgbArray[x][y][0], rgbArray[x][y][1], rgbArray[x][y][2]);
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


    /** Convertit une couleur RGB en HSB
     * @param r composante rouge (R)
     * @param g composante verte (G)
     * @param b composante bleue (B)
     * @return renvoie la couleur HSB
     */
    public static Float[] getHSBfromRGB(int r, int g, int b){
        float hue, saturation, brightness;
        int cmax = (r > g) ? r : g;
        if (b > cmax) cmax = b;
        int cmin = (r < g) ? r : g;
        if (b < cmin) cmin = b;

        brightness = ((float) cmax) / 255.0f;
        if (cmax != 0) {
            saturation = ((float) (cmax - cmin)) / ((float) cmax);
        }
        else {
            saturation = 0;
        }
        if (saturation == 0) {
            hue = 0;
        }
        else {
            float redc = ((float) (cmax - r)) / ((float) (cmax - cmin));
            float greenc = ((float) (cmax - g)) / ((float) (cmax - cmin));
            float bluec = ((float) (cmax - b)) / ((float) (cmax - cmin));
            if (r == cmax) {
                hue = bluec - greenc;
            }
            else if (g == cmax) {
                hue = 2.0f + redc - bluec;
            }
            else {
                hue = 4.0f + greenc - redc;
            }
            hue = hue / 6.0f;
            if (hue < 0) {
                hue = hue + 1.0f;
            }
        }
        return new Float[]{hue, saturation, brightness};
    }

    /**
     * Convertit une couleur RGB en HSB
     * @param rgb le triplé ordonné RGB
     * @return renvoie le triplé ordonné HSB
     */
    public static Float[] getHSBfromRGB(int[] rgb){
        if (rgb.length==3){
            return getHSBfromRGB(rgb[0],rgb[1],rgb[2]);
        }
        else{
            System.out.println("Bad length of given RGB");
            return null;
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
                            rgbArray[x][y] = getRGBfromBGR(bgrArray[x][y][0], bgrArray[x][y][1], bgrArray[x][y][2]);
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

    /** Convertit une couleur BGR en RGB
     * @param b composante bleue (B)
     * @param g composante verte (G)
     * @param r composante rouge (R)
     * @return renvoie la couleur RGB
     */
    public static Integer[] getRGBfromBGR(int b, int g, int r){
        return new Integer[]{r,g,b};
    }

    /**
     * Convertit une couleur BGR en RGB
     * @param bgr le triplé ordonné BGR
     * @return renvoie le triplé ordonné RGB
     */
    public static Integer[] getRGBfromBGR(int[] bgr){
        if (bgr.length==3){
            return new Integer[]{bgr[2],bgr[1],bgr[0]};
        }
        else{
            System.out.println("Bad length of given BGR");
            return null;
        }
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
                            bgrArray[x][y] = getRGBfromBGR(rgbArray[x][y][0], rgbArray[x][y][1], rgbArray[x][y][2]);
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
    /** Convertit une couleur RGB en BGR
     * @param r composante rouge (R)
     * @param g composante verte (G)
     * @param b composante bleue (B)
     * @return renvoie la couleur BGR
     */
    public static int[] getBGRfromRGB(int r, int g, int b){
        return new int[]{b,g,r};
    }

    /**
     * Convertit une couleur RGB en BGR
     * @param rgb le triplé ordonné RGB
     * @return renvoie le triplé ordonné BGR
     */
    public static int[] getBGRfromRGB(int[] rgb){
        if (rgb.length==3){
            return new int[]{rgb[2],rgb[1],rgb[0]};
        }
        else{
            System.out.println("Bad length of given RGB");
            return null;
        }
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
                            bgrArray[x][y] = getBGRfromHSB(hsbArray[x][y][0], hsbArray[x][y][1], hsbArray[x][y][2]);
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

    /** Convertit une couleur en HSB en une couleur BGR
     * @param hue angle de la couleur (rouge, orange, jaune, vert, bleu, violet...)
     * @param saturation saturation de la couleur (coloré ou décoloré)
     * @param brightness luminosité de la couleur (éclairé ou sombre)
     * @return renvoie une couleur en BGR
     */
    public static Integer[] getBGRfromHSB(float hue, float saturation, float brightness){
        int r=0;
        int g=0;
        int b=0;
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
        return new Integer[]{b,g,r};
    }

    /**
     * Convertit une couleur HSB en BGR
     * @param hsb le triplé ordonné HSB
     * @return renvoie le triplé ordonné BGR
     */
    public static Integer[] getBGRfromHSB(float[] hsb){
        if (hsb.length==3){
            return getBGRfromHSB(hsb[0],hsb[1],hsb[2]);
        }
        else{
            System.out.println("Bad length of given HSB");
            return null;
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
                            hsbArray[x][y] = getHSBfromBGR(bgrArray[x][y][0], bgrArray[x][y][1], bgrArray[x][y][2]);
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


    /** Convertit une couleur BGR en HSB
     * @param b composante bleue (B)
     * @param g composante verte (G)
     * @param r composante rouge (R)
     * @return renvoie la couleur HSB
     */
    public static Float[] getHSBfromBGR(int b, int g, int r){
        float hue, saturation, brightness;
        int cmax = (r > g) ? r : g;
        if (b > cmax) cmax = b;
        int cmin = (r < g) ? r : g;
        if (b < cmin) cmin = b;

        brightness = ((float) cmax) / 255.0f;
        if (cmax != 0) {
            saturation = ((float) (cmax - cmin)) / ((float) cmax);
        }
        else {
            saturation = 0;
        }
        if (saturation == 0) {
            hue = 0;
        }
        else {
            float redc = ((float) (cmax - r)) / ((float) (cmax - cmin));
            float greenc = ((float) (cmax - g)) / ((float) (cmax - cmin));
            float bluec = ((float) (cmax - b)) / ((float) (cmax - cmin));
            if (r == cmax) {
                hue = bluec - greenc;
            }
            else if (g == cmax) {
                hue = 2.0f + redc - bluec;
            }
            else {
                hue = 4.0f + greenc - redc;
            }
            hue = hue / 6.0f;
            if (hue < 0) {
                hue = hue + 1.0f;
            }
        }
        return new Float[]{hue,saturation,brightness};
    }

    /**
     * Convertit une couleur BGR en HSB
     * @param bgr le triplé ordonné BGR
     * @return renvoie le triplé ordonné HSB
     */
    public static Float[] getHSBfromBGR(int[] bgr){
        if (bgr.length==3){
            return getHSBfromBGR(bgr[0],bgr[1],bgr[2]);
        }
        else{
            System.out.println("Bad length of given BGR");
            return null;
        }
    }


}
