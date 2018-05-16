package image.analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PictureAnalysis {

    /**
     * On récupère les médianes sur les 3 composantes de couleurs sur une zone de l'image
     * @param xStart abscisse du début du rectangle sur lequel faire la médiane
     * @param yStart ordonnée du début du rectangle sur lequel faire la médiane
     * @param width largeur du rectangle sur lequel faire la médiane
     * @param height hauteur du rectangle sur lequel faire la médiane
     * @param canGoOutOfBounds si on prévoit le fait qu'on dépasse le l'image ou pas
     * @return renvoie le médianes des 3 composantes de couleurs
     */
    public static Object[] medianOverRectangle(Picture pic, int xStart, int yStart, int width, int height, boolean canGoOutOfBounds){
        int picWidth=pic.getWidth();
        int picHeight=pic.getHeight();
        Object[][][] picImgArray=pic.getImageArray();
        if (xStart<picWidth && yStart<picHeight && xStart>=0 && yStart>=0 && width>0 && height>0){
            if (canGoOutOfBounds) {
                width = Math.min(picWidth - xStart, width);
                height = Math.min(picHeight - yStart, height);
                xStart = Math.max(xStart, 0);
                yStart = Math.max(yStart, 0);
            }
            if (xStart+width<=picWidth && yStart+height<=picHeight) {
                if (picImgArray instanceof Float[][][]) {
                    List<Float> firstParamList = new ArrayList<>();
                    List<Float> secondParamList = new ArrayList<>();
                    List<Float> thirdParamList = new ArrayList<>();
                    for (int x = xStart; x < xStart + width; x++) {
                        for (int y = yStart; y < yStart + height; y++) {
                            firstParamList.add((Float)picImgArray[x][y][0]);
                            secondParamList.add((Float)picImgArray[x][y][1]);
                            thirdParamList.add((Float)picImgArray[x][y][2]);
                        }
                    }
                    Collections.sort(firstParamList);
                    Collections.sort(secondParamList);
                    Collections.sort(thirdParamList);
                    Float firstMedian = firstParamList.get(firstParamList.size() / 2);
                    Float secondMedian = secondParamList.get(secondParamList.size() / 2);
                    Float thirdMedian = thirdParamList.get(thirdParamList.size() / 2);
                    return new Float[]{firstMedian, secondMedian, thirdMedian};
                }
                else if (picImgArray instanceof Integer[][][]){
                    List<Integer> firstParamList = new ArrayList<>();
                    List<Integer> secondParamList = new ArrayList<>();
                    List<Integer> thirdParamList = new ArrayList<>();
                    for (int x = xStart; x < xStart + width; x++) {
                        for (int y = yStart; y < yStart + height; y++) {
                            firstParamList.add((Integer)picImgArray[x][y][0]);
                            secondParamList.add((Integer)picImgArray[x][y][1]);
                            thirdParamList.add((Integer)picImgArray[x][y][2]);
                        }
                    }
                    Collections.sort(firstParamList);
                    Collections.sort(secondParamList);
                    Collections.sort(thirdParamList);
                    Integer firstMedian = firstParamList.get(firstParamList.size() / 2);
                    Integer secondMedian = secondParamList.get(secondParamList.size() / 2);
                    Integer thirdMedian = thirdParamList.get(thirdParamList.size() / 2);
                    return new Integer[]{firstMedian, secondMedian, thirdMedian};
                }
                else{
                    System.out.println("Ce cas ne devrait pas arriver");
                    return null;
                }
            }
            else {
                System.out.println("Out of bounds");
                return null;
            }
        }
        else{
            System.out.println("Bad parameters: xStart("+xStart+") yStart("+yStart+") width("+width+") height("+height+")"+
                    " canGoOutOfBounds("+canGoOutOfBounds+") imageWidth("+picWidth+") imageHeight("+picHeight+")");
            return null;
        }
    }

    /**
     * On récupère les médianes sur les 3 composantes de couleurs sur une zone de l'image
     * @param xCenter abscisse du centre du cercle
     * @param yCenter ordonnée du centre du cercle
     * @param radius rayon du cercle
     * @param canGoOutOfBounds si on prévoit qu'on va sortir de l'image ou non
     * @return renvoie les médianes des 3 composantes de couleurs
     */
    public static Object[] medianOverCircle(Picture pic, int xCenter, int yCenter, int radius, boolean canGoOutOfBounds){
        int picWidth=pic.getWidth();
        int picHeight=pic.getHeight();
        Object[][][] picImgArray=pic.getImageArray();
        if (xCenter<picWidth && yCenter<picHeight && xCenter>=0 && yCenter>=0 && radius>0){
            if (!canGoOutOfBounds){
                if (xCenter + radius >= picWidth){
                    System.out.println("Out of bounds");
                    return null;
                }
                if (xCenter - radius < 0){
                    System.out.println("Out of bounds");
                    return null;
                }
                if (yCenter + radius >= picHeight){
                    System.out.println("Out of bounds");
                    return null;
                }
                if (yCenter - radius < 0){
                    System.out.println("Out of bounds");
                    return null;
                }
            }
            if (picImgArray instanceof Float[][][]) {
                List<Float> firstParamList = new ArrayList<>();
                List<Float> secondParamList = new ArrayList<>();
                List<Float> thirdParamList = new ArrayList<>();
                for (int x = xCenter - radius; x < xCenter + radius; x++) {
                    for (int y = yCenter - radius; y < yCenter + radius; y++) {
                        if ((x - xCenter) * (x - xCenter) + (y - yCenter) * (y - yCenter) < radius*radius) {
                            if (!canGoOutOfBounds) {
                                firstParamList.add((Float)picImgArray[x][y][0]);
                                secondParamList.add((Float)picImgArray[x][y][1]);
                                thirdParamList.add((Float)picImgArray[x][y][2]);
                            }
                            else{
                                if (x<picWidth && x>=0 && y<picHeight && y>=0){
                                    firstParamList.add((Float)picImgArray[x][y][0]);
                                    secondParamList.add((Float)picImgArray[x][y][1]);
                                    thirdParamList.add((Float)picImgArray[x][y][2]);
                                }
                            }
                        }
                    }
                }
                Collections.sort(firstParamList);
                Collections.sort(secondParamList);
                Collections.sort(thirdParamList);
                Float firstParamMedian=firstParamList.get(firstParamList.size()/2);
                Float secondParamMedian=secondParamList.get(firstParamList.size()/2);
                Float thirdParamMedian=thirdParamList.get(firstParamList.size()/2);
                return new Float[]{firstParamMedian, secondParamMedian, thirdParamMedian};
            }
            else if (picImgArray instanceof Integer[][][]){
                List<Integer> firstParamList = new ArrayList<>();
                List<Integer> secondParamList = new ArrayList<>();
                List<Integer> thirdParamList = new ArrayList<>();
                for (int x = xCenter - radius; x < xCenter + radius; x++) {
                    for (int y = yCenter - radius; y < yCenter + radius; y++) {
                        if ((x - xCenter) * (x - xCenter) + (y - yCenter) * (y - yCenter) < radius) {
                            if (!canGoOutOfBounds) {
                                firstParamList.add((Integer)picImgArray[x][y][0]);
                                secondParamList.add((Integer)picImgArray[x][y][1]);
                                thirdParamList.add((Integer)picImgArray[x][y][2]);
                            }
                            else{
                                if (x<picWidth && x>=0 && y<picHeight && y>=0){
                                    firstParamList.add((Integer)picImgArray[x][y][0]);
                                    secondParamList.add((Integer)picImgArray[x][y][1]);
                                    thirdParamList.add((Integer)picImgArray[x][y][2]);
                                }
                            }
                        }
                    }
                }
                Collections.sort(firstParamList);
                Collections.sort(secondParamList);
                Collections.sort(thirdParamList);
                Integer firstParamMedian=firstParamList.get(firstParamList.size()/2);
                Integer secondParamMedian=secondParamList.get(firstParamList.size()/2);
                Integer thirdParamMedian=thirdParamList.get(firstParamList.size()/2);
                return new Integer[]{firstParamMedian, secondParamMedian, thirdParamMedian};
            }
            else{
                System.out.println("Ce cas ne devrait pas arriver");
                return null;
            }
        }
        else{
            System.out.println("Bad parameters: xCenter("+xCenter+") yCenter("+yCenter+") radius("+radius+")"+
                    " canGoOutOfBounds("+canGoOutOfBounds+") imageWidth("+picWidth+") imageHeight("+picHeight+")");
            return null;
        }
    }

    /**
     * On récupère les moyennes sur les 3 composantes de couleurs sur une zone de l'image
     * @param xStart abscisse du début du rectangle
     * @param yStart ordonnée du début du rectangle
     * @param width largeur du rectangle
     * @param height hauteur du rectangle
     * @param canGoOutOfBounds si on prévoit le fait qu'on dépasse le l'image ou pas
     * @return renvoie les moyennes des 3 composantes de couleurs
     */
    public static Object[] averageOverRectangle(Picture pic, int xStart, int yStart, int width, int height, boolean canGoOutOfBounds){
        int picWidth=pic.getWidth();
        int picHeight=pic.getHeight();
        Object[][][] picImgArray=pic.getImageArray();
        if (xStart<picWidth && yStart<picHeight && xStart>=0 && yStart>=0 && width>0 && height>0){
            if (canGoOutOfBounds) {
                width = Math.min(picWidth - xStart, width);
                height = Math.min(picHeight - yStart, height);
                xStart = Math.max(xStart, 0);
                yStart = Math.max(yStart, 0);
            }
            if (xStart+width<=picWidth && yStart+height<=picHeight){
                int nbPixelsChecked=0;
                if (picImgArray instanceof Float[][][]) {
                    float firstParamSum = 0;
                    float secondParamSum = 0;
                    float thirdParamSum = 0;
                    for (int x = xStart; x < xStart + width; x++) {
                        for (int y = yStart; y < yStart + height; y++) {
                            nbPixelsChecked++;
                            firstParamSum += (Float)picImgArray[x][y][0];
                            secondParamSum += (Float)picImgArray[x][y][1];
                            thirdParamSum += (Float)picImgArray[x][y][2];
                        }
                    }
                    Float firstParamAverage=firstParamSum/nbPixelsChecked;
                    Float secondParamAverage=secondParamSum/nbPixelsChecked;
                    Float thirdParamAverage=thirdParamSum/nbPixelsChecked;
                    return new Float[]{firstParamAverage, secondParamAverage, thirdParamAverage};
                }
                else if (picImgArray instanceof Integer[][][]){
                    int firstParamSum = 0;
                    int secondParamSum = 0;
                    int thirdParamSum = 0;
                    for (int x = xStart; x < xStart + width; x++) {
                        for (int y = yStart; y < yStart + height; y++) {
                            nbPixelsChecked++;
                            firstParamSum += (Integer)picImgArray[x][y][0];
                            secondParamSum += (Integer)picImgArray[x][y][1];
                            thirdParamSum += (Integer)picImgArray[x][y][2];
                        }
                    }
                    int firstParamAverage=Math.round(firstParamSum*1f/nbPixelsChecked);
                    int secondParamAverage=Math.round(secondParamSum*1f/nbPixelsChecked);
                    int thirdParamAverage=Math.round(thirdParamSum*1f/nbPixelsChecked);
                    return new Integer[]{firstParamAverage,secondParamAverage,thirdParamAverage};
                }
                else{
                    System.out.println("Ce cas ne devrait pas arriver");
                    return null;
                }
            }
            else {
                System.out.println("Out of bounds");
                return null;
            }
        }
        else{
            System.out.println("Bad parameters: xStart("+xStart+") yStart("+yStart+") width("+width+") height("+height+")"+
                    " canGoOutOfBounds("+canGoOutOfBounds+") imageWidth("+picWidth+") imageHeight("+picHeight+")");
            return null;
        }
    }

    /**
     * On récupère les moyennes sur les 3 composantes de couleurs sur une zone de l'image
     * @param xCenter abscisse du centre du cercle
     * @param yCenter ordonnée du centre du cercle
     * @param radius rayon du cercle
     * @param canGoOutOfBounds si on prévoit qu'on va sortir de l'image ou non
     * @return renvoie les moyennes des 3 composantes de couleurs
     */
    public static Object[] avegareOverCircle(Picture pic, int xCenter, int yCenter, int radius, boolean canGoOutOfBounds){
        int picWidth=pic.getWidth();
        int picHeight=pic.getHeight();
        Object[][][] picImgArray=pic.getImageArray();
        if (xCenter<picWidth && yCenter<picHeight && xCenter>=0 && yCenter>=0 && radius>0){
            if (!canGoOutOfBounds) {
                if (xCenter + radius >= picWidth) {
                    System.out.println("Out of bounds");
                    return null;
                }
                if (xCenter - radius < 0){
                    System.out.println("Out of bounds");
                    return null;
                }
                if (yCenter + radius >= picHeight){
                    System.out.println("Out of bounds");
                    return null;
                }
                if (yCenter - radius < 0){
                    System.out.println("Out of bounds");
                    return null;
                }
            }
            int nbPixelsChecked=0;
            if (picImgArray instanceof Float[][][]) {
                float firstParamSum = 0;
                float secondParamSum = 0;
                float thirdParamSum = 0;
                for (int x = xCenter - radius; x < xCenter + radius; x++) {
                    for (int y = yCenter - radius; y < yCenter + radius; y++) {
                        if ((x - xCenter) * (x - xCenter) + (y - yCenter) * (y - yCenter) < radius) {
                            if (!canGoOutOfBounds) {
                                nbPixelsChecked++;
                                firstParamSum += (Float)picImgArray[x][y][0];
                                secondParamSum += (Float)picImgArray[x][y][1];
                                thirdParamSum += (Float)picImgArray[x][y][2];
                            }
                            else{
                                if (x<picWidth && x>=0 && y<picHeight && y>=0){
                                    nbPixelsChecked++;
                                    firstParamSum += (Float)picImgArray[x][y][0];
                                    secondParamSum += (Float)picImgArray[x][y][1];
                                    thirdParamSum += (Float)picImgArray[x][y][2];
                                }
                            }
                        }
                    }
                }
                float firstParamAverage=firstParamSum/nbPixelsChecked;
                float secondParamAverage=secondParamSum/nbPixelsChecked;
                float thirdParamAverage=thirdParamSum/nbPixelsChecked;
                return new Float[]{firstParamAverage, secondParamAverage, thirdParamAverage};
            }
            else if (picImgArray instanceof Integer[][][]){
                int firstParamSum=0;
                int secondParamSum=0;
                int thirdParamSum=0;
                for (int x = xCenter - radius; x < xCenter + radius; x++) {
                    for (int y = yCenter - radius; y < yCenter + radius; y++) {
                        if ((x - xCenter) * (x - xCenter) + (y - yCenter) * (y - yCenter) < radius) {
                            if (!canGoOutOfBounds) {
                                nbPixelsChecked++;
                                firstParamSum += (Integer)picImgArray[x][y][0];
                                secondParamSum += (Integer)picImgArray[x][y][1];
                                thirdParamSum += (Integer)picImgArray[x][y][2];
                            }
                            else{
                                if (x<picWidth && x>=0 && y<picHeight && y>=0){
                                    nbPixelsChecked++;
                                    firstParamSum += (Integer)picImgArray[x][y][0];
                                    secondParamSum += (Integer)picImgArray[x][y][1];
                                    thirdParamSum += (Integer)picImgArray[x][y][2];
                                }
                            }
                        }
                    }
                }
                int firstParamAverage = Math.round(firstParamSum*1f/nbPixelsChecked);
                int secondParamAverage = Math.round(secondParamSum*1f/nbPixelsChecked);
                int thirdParamAverage = Math.round(thirdParamSum*1f/nbPixelsChecked);
                return new Integer[]{firstParamAverage, secondParamAverage, thirdParamAverage};
            }
            else{
                System.out.println("Ce cas ne devrait pas arriver");
                return null;
            }
        }
        else{
            System.out.println("Bad parameters: xCenter("+xCenter+") yCenter("+yCenter+") radius("+radius+")"+
                    " canGoOutOfBounds("+canGoOutOfBounds+") imageWidth("+picWidth+") imageHeight("+picHeight+")");
            return null;
        }
    }

    public static float computeDistanceBetweenTwoPixels(Picture pic1, int x1, int y1, Picture pic2, int x2, int y2){
        return computeDistanceBetweenTwoColors(getPixel(pic1,x1,y1), getPixel(pic2,x2,y2));
    }

    public static float computeDistanceBetweenPixelAndColor(Picture pic, int x, int y, Object[] color){
        return computeDistanceBetweenTwoColors(getPixel(pic,x,y), color);
    }

    public static float computeDistanceBetweenTwoColors(Object[] firstColor, Object[] secondColor){
        if (firstColor instanceof Integer[] && secondColor instanceof Integer[]){
            int distance=0;
            distance+=((Integer)firstColor[0]-(Integer)firstColor[0])*((Integer)firstColor[0]-(Integer)firstColor[0]);
            distance+=((Integer)firstColor[1]-(Integer)firstColor[1])*((Integer)firstColor[1]-(Integer)firstColor[1]);
            distance+=((Integer)firstColor[2]-(Integer)firstColor[2])*((Integer)firstColor[2]-(Integer)firstColor[2]);
            return distance;
        }
        else if (firstColor instanceof Float[][][] && secondColor instanceof Float[]){
            float distance=0;
            distance+=((Float)firstColor[0]-(Float)firstColor[0])*((Float)firstColor[0]-(Float)firstColor[0]);
            distance+=((Float)firstColor[1]-(Float)firstColor[1])*((Float)firstColor[1]-(Float)firstColor[1]);
            distance+=((Float)firstColor[2]-(Float)firstColor[2])*((Float)firstColor[2]-(Float)firstColor[2]);
            return distance;
        }
        else{
            System.out.println("Ce cas ne devrait pas arriver");
            System.out.println("Colors: first: "+firstColor[0]+" "+firstColor[1]+" "+firstColor[2]+
                    " / "+secondColor[0]+" "+secondColor[1]+" "+secondColor[2]);
            return -1;
        }
    }

    /**
     * Renvoie la couleur d'un pixel d'un image en particuliers
     * @param pic image sur laquelle chercher le pixel
     * @param x abscisse du pixel
     * @param y ordonnée du pixel
     * @return renvoie une array contenant les 3 composantes de la couleur du pixel choisi
     */
    public static Object[] getPixel(Picture pic, int x, int y){
        return pic.getImageArray()[x][y];
    }


    /**
     * Extrait une sous-image à partir d'une autre image
     * @param pic image source
     * @param xStart ordonnée de début de la sous image
     * @param yStart abscisse de début de la sous image
     * @param width largeur de la nouvelle image
     * @param height hauteur de la nouvelle image
     * @param canGoOutOfBounds si on peut dépasser sans qu'il n'y ait d'erreurs
     * @return renvoie la sous-iamge
     */
    public static Picture getSubPicutre(Picture pic, int xStart, int yStart, int width, int height, boolean canGoOutOfBounds){
        int picWidth=pic.getWidth();
        int picHeight=pic.getHeight();
        Object[][][] picImgArray=pic.getImageArray();
        if (xStart<picWidth && yStart<picHeight && xStart>=0 && yStart>=0 && width>0 && height>0){
            if (canGoOutOfBounds) {
                width = Math.min(picWidth - xStart, width);
                height = Math.min(picHeight - yStart, height);
                xStart = Math.max(xStart, 0);
                yStart = Math.max(yStart, 0);
            }
            if (xStart+width<=picWidth && yStart+height<=picHeight){
                if (picImgArray instanceof Float[][][]) {
                    Float[][][] subImageArray = new Float[width][height][3];
                    for (int x = xStart; x < xStart + width; x++) {
                        for (int y = yStart; y < yStart + height; y++) {
                            subImageArray[x-xStart][y-yStart][0] = (Float)picImgArray[x][y][0];
                            subImageArray[x-xStart][y-yStart][1] = (Float)picImgArray[x][y][1];
                            subImageArray[x-xStart][y-yStart][2] = (Float)picImgArray[x][y][2];
                        }
                    }
                    if (pic instanceof PictureHSB){
                        PictureHSB subPic = new PictureHSB();;
                        subPic.setImage(subImageArray);
                        return subPic;
                    }
                    else{
                        System.out.println("Ce cas ne devrait pas arriver");
                        return null;
                    }
                }
                else if (picImgArray instanceof Integer[][][]){
                    Integer[][][] subImageArray = new Integer[width][height][3];
                    for (int x = xStart; x < xStart + width; x++) {
                        for (int y = yStart; y < yStart + height; y++) {
                            subImageArray[x-xStart][y-yStart][0] = (Integer)picImgArray[x][y][0];
                            subImageArray[x-xStart][y-yStart][1] = (Integer)picImgArray[x][y][1];
                            subImageArray[x-xStart][y-yStart][2] = (Integer)picImgArray[x][y][2];
                        }
                    }
                    if (pic instanceof PictureRGB){
                        PictureRGB subPic = new PictureRGB();
                        subPic.setImage(subImageArray);
                        return subPic;
                    }
                    else if (pic instanceof PictureBGR){
                        PictureBGR subPic = new PictureBGR();;
                        subPic.setImage(subImageArray);
                        return subPic;
                    }
                    else{
                        System.out.println("Ce cas ne devrait pas arriver");
                        return null;
                    }
                }
                else{
                    System.out.println("Ce cas ne devrait pas arriver");
                    return null;
                }
            }
            else {
                System.out.println("Out of bounds");
                return null;
            }
        }
        else{
            System.out.println("Bad parameters: xStart("+xStart+") yStart("+yStart+") width("+width+") height("+height+")"+
                    " canGoOutOfBounds("+canGoOutOfBounds+") imageWidth("+picWidth+") imageHeight("+picHeight+")");
            return null;
        }
    }
}

