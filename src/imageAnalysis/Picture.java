package imageAnalysis;


import javafx.collections.transformation.SortedList;

import java.awt.image.BufferedImage;
import java.util.*;

public abstract class Picture<T> {

    protected int width;
    protected int height;
    protected T[][][] imgArray;

    public Picture(){
        this.width=800;
        this.height=600;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public T[][][] getImageArray() {
        return imgArray;
    }

    /**
     * Set une image RGB
     * @param array array RGB ayant des valeurs de 0 à 255
     */
    public void setImage(T[][][] array, int threesholdMin, int threesholdMax) {
        if (array instanceof Float[][][]) {
            Float[][][] convertedArray = (Float[][][])array;
            this.width = array.length;
            this.height = array[0].length;
            for (int x = 0; x < this.width; x++) {
                for (int y = 0; y < this.height; y++) {
                    for (int canal = 0; canal < 3; canal++) {
                        if (convertedArray[x][y][canal] > threesholdMax || convertedArray[x][y][canal] < threesholdMin) {
                            this.imgArray = null;
                            return;
                        }
                        this.imgArray[x][y][canal] = array[x][y][canal];
                    }
                }
            }
        }
        else if (array instanceof Integer[][][]){
            Integer[][][] convertedArray = (Integer[][][])array;
            this.width = array.length;
            this.height = array[0].length;
            for (int x = 0; x < this.width; x++) {
                for (int y = 0; y < this.height; y++) {
                    for (int canal = 0; canal < 3; canal++) {
                        if (convertedArray[x][y][canal] > threesholdMax || convertedArray[x][y][canal] < threesholdMin) {
                            this.imgArray = null;
                            return;
                        }
                        this.imgArray[x][y][canal] = array[x][y][canal];
                    }
                }
            }
        }
        else{
            System.out.println("Array type not implemented (ce cas ne devrait pas arriver");
        }
    }

    /**
     * Set une image RGB
     * @param array  array
     * @param width  largeur de l'image
     * @param height hauteur de l'image
     */
    public void setImage(T[] array, int width, int height, int thresholdMin, int thresholdMax) {
        if (array instanceof Float[]) {
            Float[] convertedArray = (Float[]) array;
            if (array.length == width * height * 3) {
                this.width = width;
                this.height = height;
                for (int i = 0; i < width * height * 3; i += 3) {
                    if (!(convertedArray[i] > thresholdMax || convertedArray[i] < thresholdMin ||
                            convertedArray[i + 1] > thresholdMax || convertedArray[i + 1] < thresholdMin ||
                            convertedArray[i + 2] > thresholdMax || convertedArray[i + 2] < thresholdMin)) {
                        this.imgArray[i / width][i % width][0] = array[i];
                        this.imgArray[i / width][i % width][1] = array[i + 1];
                        this.imgArray[i / width][i % width][2] = array[i + 2];
                    } else {
                        this.imgArray = null;
                        return;
                    }
                }
            }else {
                System.out.println("Bad height and width when setting array:");
                System.out.println("Array length: " + array.length + " // Picture size: " + this.width + "*" + this.height + "*3");
            }
        }
        else if (array instanceof Integer[]){
            Integer[] convertedArray = (Integer[])array;
            if (array.length == width * height * 3) {
                this.width = width;
                this.height = height;
                for (int i = 0; i < width * height * 3; i += 3) {
                    if (!(convertedArray[i] > thresholdMax || convertedArray[i] < thresholdMin ||
                            convertedArray[i + 1] > thresholdMax || convertedArray[i + 1] < thresholdMin ||
                            convertedArray[i + 2] > thresholdMax || convertedArray[i + 2] < thresholdMin)) {
                        this.imgArray[i / width][i % width][0] = array[i];
                        this.imgArray[i / width][i % width][1] = array[i + 1];
                        this.imgArray[i / width][i % width][2] = array[i + 2];
                    } else {
                        this.imgArray = null;
                        return;
                    }
                }
            }else {
                System.out.println("Bad height and width when setting array:");
                System.out.println("Array length: " + array.length + " // Picture size: " + this.width + "*" + this.height + "*3");
            }
        }
        else{
            System.out.println("Array type not implemented (ce cas ne devrait pas apparaître)");
            return;
        }
    }

    /**
     * Set l'image à partir d'une BufferedImage
     * @param buffImg la BufferedImage à utiliser
     */
    public void setImage(BufferedImage buffImg) {
        if (buffImg.getType() == 1 || buffImg.getType() == 4) {
            this.width = buffImg.getWidth();
            this.height = buffImg.getHeight();
            int[] array = new int[this.width * this.height * 3];
            buffImg.getRaster().getPixels(0, 0, this.width, this.height, array);
            Integer[] convertedArray = Arrays.stream( array ).boxed().toArray( Integer[]::new );
            this.setImage((T[]) convertedArray, this.width, this.height,0,255);
        }
        else {
            System.out.println("Bad image type: not RGB or BGR");
        }
    }



    //////////////////// Image analysis ////////////////////

    /**
     * On récupère les médianes sur les 3 composantes de couleurs sur une zone de l'image
     * @param xStart abscisse du début du rectangle sur lequel faire la médiane
     * @param yStart ordonnée du début du rectangle sur lequel faire la médiane
     * @param width largeur du rectangle sur lequel faire la médiane
     * @param height hauteur du rectangle sur lequel faire la médiane
     * @param canGoOutOfBounds si on prévoit le fait qu'on dépasse le l'image ou pas
     * @return renvoie le médianes des 3 composantes de couleurs
     */
    public T[] medianOverRectangle(int xStart, int yStart, int width, int height, boolean canGoOutOfBounds){
        if (xStart<this.width && yStart<this.height && xStart>=0 && yStart>=0 && width>0 && height>0){
            if (canGoOutOfBounds) {
                width = Math.min(this.width - xStart - 1, width);
                height = Math.min(this.height - yStart - 1, height);
                xStart = Math.max(xStart, 0);
                yStart = Math.max(yStart, 0);
            }
            if (xStart+width<this.width && yStart+height<this.height) {
                if (this.imgArray instanceof Float[][][]) {
                    List<Float> firstParamList = new ArrayList<>();
                    List<Float> secondParamList = new ArrayList<>();
                    List<Float> thirdParamList = new ArrayList<>();
                    for (int x = xStart; x < xStart + width; x++) {
                        for (int y = yStart; y < yStart + height; y++) {
                            firstParamList.add((Float)this.imgArray[x][y][0]);
                            secondParamList.add((Float)this.imgArray[x][y][1]);
                            thirdParamList.add((Float)this.imgArray[x][y][2]);
                        }
                    }
                    Collections.sort(firstParamList);
                    Collections.sort(secondParamList);
                    Collections.sort(thirdParamList);
                    T firstMedian = (T)firstParamList.get(firstParamList.size() / 2);
                    T secondMedian = (T)secondParamList.get(secondParamList.size() / 2);
                    T thirdMedian = (T)thirdParamList.get(thirdParamList.size() / 2);
                    return (T[]) new Object[]{firstMedian, secondMedian, thirdMedian};
                }
                else if (this.imgArray instanceof Integer[][][]){
                    List<Integer> firstParamList = new ArrayList<>();
                    List<Integer> secondParamList = new ArrayList<>();
                    List<Integer> thirdParamList = new ArrayList<>();
                    for (int x = xStart; x < xStart + width; x++) {
                        for (int y = yStart; y < yStart + height; y++) {
                            firstParamList.add((Integer)this.imgArray[x][y][0]);
                            secondParamList.add((Integer)this.imgArray[x][y][1]);
                            thirdParamList.add((Integer)this.imgArray[x][y][2]);
                        }
                    }
                    Collections.sort(firstParamList);
                    Collections.sort(secondParamList);
                    Collections.sort(thirdParamList);
                    T firstMedian = (T)firstParamList.get(firstParamList.size() / 2);
                    T secondMedian = (T)secondParamList.get(secondParamList.size() / 2);
                    T thirdMedian = (T)thirdParamList.get(thirdParamList.size() / 2);
                    return (T[]) new Object[]{firstMedian, secondMedian, thirdMedian};
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
                    " canGoOutOfBounds("+canGoOutOfBounds+") imageWidth("+this.width+") imageHeight"+this.height+")");
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
    public T[] medianOverCircle(int xCenter, int yCenter, int radius, boolean canGoOutOfBounds){
        if (xCenter<this.width && yCenter<this.height && xCenter>=0 && yCenter>=0 && radius>0){
            if (!canGoOutOfBounds) {
                if (xCenter + radius >= this.width) {
                    System.out.println("Out of bounds");
                    return null;
                }
                if (xCenter - radius < 0){
                    System.out.println("Out of bounds");
                    return null;
                }
                if (yCenter + radius >= this.height){
                    System.out.println("Out of bounds");
                    return null;
                }
                if (yCenter - radius < 0){
                    System.out.println("Out of bounds");
                    return null;
                }
            }
            int nbPixelsChecked=0;
            if (this.imgArray instanceof Float[][][]) {
                List<Float> firstParamList = new ArrayList<>();
                List<Float> secondParamList = new ArrayList<>();
                List<Float> thirdParamList = new ArrayList<>();
                for (int x = xCenter - radius; x < xCenter + radius; x++) {
                    for (int y = yCenter - radius; y < yCenter + radius; y++) {
                        if ((x - xCenter) * (x - xCenter) + (y - yCenter) * (y - yCenter) < radius*radius) {
                            if (!canGoOutOfBounds) {
                                nbPixelsChecked++;
                                firstParamList.add((Float)this.imgArray[x][y][0]);
                                secondParamList.add((Float)this.imgArray[x][y][1]);
                                thirdParamList.add((Float)this.imgArray[x][y][2]);
                            }
                            else{
                                if (x<this.width && x>=0 && y<this.width && y>=0){
                                    nbPixelsChecked++;
                                    firstParamList.add((Float)this.imgArray[x][y][0]);
                                    secondParamList.add((Float)this.imgArray[x][y][1]);
                                    thirdParamList.add((Float)this.imgArray[x][y][2]);
                                }
                            }
                        }
                    }
                }
                Collections.sort(firstParamList);
                Collections.sort(secondParamList);
                Collections.sort(thirdParamList);
                T firstParamMedian=(T)firstParamList.get(firstParamList.size()/2);
                T secondParamMedian=(T)secondParamList.get(firstParamList.size()/2);
                T thirdParamMedian=(T)thirdParamList.get(firstParamList.size()/2);
                return (T[])new Object[]{firstParamMedian, secondParamMedian, thirdParamMedian};
            }
            else if (this.imgArray instanceof Integer[][][]){
                List<Integer> firstParamList = new ArrayList<>();
                List<Integer> secondParamList = new ArrayList<>();
                List<Integer> thirdParamList = new ArrayList<>();
                for (int x = xCenter - radius; x < xCenter + radius; x++) {
                    for (int y = yCenter - radius; y < yCenter + radius; y++) {
                        if ((x - xCenter) * (x - xCenter) + (y - yCenter) * (y - yCenter) < radius) {
                            if (!canGoOutOfBounds) {
                                nbPixelsChecked++;
                                firstParamList.add((Integer) this.imgArray[x][y][0]);
                                secondParamList.add((Integer) this.imgArray[x][y][1]);
                                thirdParamList.add((Integer) this.imgArray[x][y][2]);
                            }
                            else{
                                if (x<this.width && x>=0 && y<this.width && y>=0){
                                    nbPixelsChecked++;
                                    firstParamList.add((Integer)this.imgArray[x][y][0]);
                                    secondParamList.add((Integer)this.imgArray[x][y][1]);
                                    thirdParamList.add((Integer)this.imgArray[x][y][2]);
                                }
                            }
                        }
                    }
                }
                Collections.sort(firstParamList);
                Collections.sort(secondParamList);
                Collections.sort(thirdParamList);
                T firstParamMedian=(T)firstParamList.get(firstParamList.size()/2);
                T secondParamMedian=(T)secondParamList.get(firstParamList.size()/2);
                T thirdParamMedian=(T)thirdParamList.get(firstParamList.size()/2);
                return (T[])new Object[]{firstParamMedian, secondParamMedian, thirdParamMedian};
            }
            else{
                System.out.println("Ce cas ne devrait pas arriver");
                return null;
            }
        }
        else{
            System.out.println("Bad parameters: xCenter("+xCenter+") yCenter("+yCenter+") radius("+radius+")"+
                    " canGoOutOfBounds("+canGoOutOfBounds+") imageWidth("+this.width+") imageHeight"+this.height+")");
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
    public T[] averageOverRectangle(int xStart, int yStart, int width, int height, boolean canGoOutOfBounds){
        if (xStart<this.width && yStart<this.height && xStart>=0 && yStart>=0 && width>0 && height>0){
            if (canGoOutOfBounds) {
                width = Math.min(this.width - xStart - 1, width);
                height = Math.min(this.height - yStart - 1, height);
                xStart = Math.max(xStart, 0);
                yStart = Math.max(yStart, 0);
            }
            if (xStart+width<this.width && yStart+height<this.height){
                int nbPixelsChecked=0;
                if (this.imgArray instanceof Float[][][]) {
                    float firstParamSum = 0;
                    float secondParamSum = 0;
                    float thirdParamSum = 0;
                    for (int x = xStart; x < xStart + width; x++) {
                        for (int y = yStart; y < yStart + height; y++) {
                            nbPixelsChecked++;
                            firstParamSum += (Float) this.imgArray[x][y][0];
                            secondParamSum += (Float) this.imgArray[x][y][1];
                            thirdParamSum += (Float) this.imgArray[x][y][2];
                        }
                    }
                    float firstParamAverage=firstParamSum/nbPixelsChecked;
                    float secondParamAverage=secondParamSum/nbPixelsChecked;
                    float thirdParamAverage=thirdParamSum/nbPixelsChecked;
                    return (T[])new Object[]{firstParamAverage, secondParamAverage, thirdParamAverage};
                }
                else if (this.imgArray instanceof Integer[][][]){
                    int firstParamSum = 0;
                    int secondParamSum = 0;
                    int thirdParamSum = 0;
                    for (int x = xStart; x < xStart + width; x++) {
                        for (int y = yStart; y < yStart + height; y++) {
                            nbPixelsChecked++;
                            firstParamSum += (Integer) this.imgArray[x][y][0];
                            secondParamSum += (Integer)this.imgArray[x][y][1];
                            thirdParamSum += (Integer) this.imgArray[x][y][2];
                        }
                    }
                    int firstParamAverage=Math.round(firstParamSum*1f/nbPixelsChecked);
                    int secondParamAverage=Math.round(secondParamSum*1f/nbPixelsChecked);
                    int thirdParamAverage=Math.round(thirdParamSum*1f/nbPixelsChecked);
                    return (T[])new Object[]{firstParamAverage,secondParamAverage,thirdParamAverage};
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
                    " canGoOutOfBounds("+canGoOutOfBounds+") imageWidth("+this.width+") imageHeight"+this.height+")");
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
    public T[] avegareOverCircle(int xCenter, int yCenter, int radius, boolean canGoOutOfBounds){
        if (xCenter<this.width && yCenter<this.height && xCenter>=0 && yCenter>=0 && radius>0){
            if (!canGoOutOfBounds) {
                if (xCenter + radius >= this.width) {
                    System.out.println("Out of bounds");
                    return null;
                }
                if (xCenter - radius < 0){
                    System.out.println("Out of bounds");
                    return null;
                }
                if (yCenter + radius >= this.height){
                    System.out.println("Out of bounds");
                    return null;
                }
                if (yCenter - radius < 0){
                    System.out.println("Out of bounds");
                    return null;
                }
            }
            int nbPixelsChecked=0;
            if (this.imgArray instanceof Float[][][]) {
                float firstParamSum = 0;
                float secondParamSum = 0;
                float thirdParamSum = 0;
                for (int x = xCenter - radius; x < xCenter + radius; x++) {
                    for (int y = yCenter - radius; y < yCenter + radius; y++) {
                        if ((x - xCenter) * (x - xCenter) + (y - yCenter) * (y - yCenter) < radius) {
                            if (!canGoOutOfBounds) {
                                nbPixelsChecked++;
                                firstParamSum += (Float) this.imgArray[x][y][0];
                                secondParamSum += (Float) this.imgArray[x][y][1];
                                thirdParamSum += (Float) this.imgArray[x][y][2];
                            }
                            else{
                                if (x<this.width && x>=0 && y<this.width && y>=0){
                                    nbPixelsChecked++;
                                    firstParamSum += (Float) this.imgArray[x][y][0];
                                    secondParamSum += (Float) this.imgArray[x][y][1];
                                    thirdParamSum += (Float) this.imgArray[x][y][2];
                                }
                            }
                        }
                    }
                }
                float firstParamAverage=firstParamSum/nbPixelsChecked;
                float secondParamAverage=secondParamSum/nbPixelsChecked;
                float thirdParamAverage=thirdParamSum/nbPixelsChecked;
                return (T[])new Object[]{firstParamAverage, secondParamAverage, thirdParamAverage};
            }
            else if (this.imgArray instanceof Integer[][][]){
                int firstParamSum=0;
                int secondParamSum=0;
                int thirdParamSum=0;
                for (int x = xCenter - radius; x < xCenter + radius; x++) {
                    for (int y = yCenter - radius; y < yCenter + radius; y++) {
                        if ((x - xCenter) * (x - xCenter) + (y - yCenter) * (y - yCenter) < radius) {
                            if (!canGoOutOfBounds) {
                                nbPixelsChecked++;
                                firstParamSum += (Integer) this.imgArray[x][y][0];
                                secondParamSum += (Integer) this.imgArray[x][y][1];
                                thirdParamSum += (Integer) this.imgArray[x][y][2];
                            }
                            else{
                                if (x<this.width && x>=0 && y<this.width && y>=0){
                                    nbPixelsChecked++;
                                    firstParamSum += (Integer) this.imgArray[x][y][0];
                                    secondParamSum += (Integer) this.imgArray[x][y][1];
                                    thirdParamSum += (Integer) this.imgArray[x][y][2];
                                }
                            }
                        }
                    }
                }
                int firstParamAverage = Math.round(firstParamSum*1f/nbPixelsChecked);
                int secondParamAverage = Math.round(secondParamSum*1f/nbPixelsChecked);
                int thirdParamAverage = Math.round(thirdParamSum*1f/nbPixelsChecked);
                return (T[])new Object[]{firstParamAverage, secondParamAverage, thirdParamAverage};
            }
            else{
                System.out.println("Ce cas ne devrait pas arriver");
                return null;
            }
        }
        else{
            System.out.println("Bad parameters: xCenter("+xCenter+") yCenter("+yCenter+") radius("+radius+")"+
                    " canGoOutOfBounds("+canGoOutOfBounds+") imageWidth("+this.width+") imageHeight"+this.height+")");
            return null;
        }
    }
}
