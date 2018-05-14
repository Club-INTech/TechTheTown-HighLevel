package imageAnalysis;


import java.awt.image.BufferedImage;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public T[] medianOverRectangle(int xStart, int yStart, int width, int height, boolean canGoOutOfBounds){
        if (xStart<this.width && yStart<this.height && xStart>=0 && yStart>=0 && width>0 && height>0){
            if (canGoOutOfBounds) {
                width = Math.min(this.width - xStart - 1, width);
                height = Math.min(this.height - yStart - 1, height);
                xStart = Math.max(xStart, 0);
                yStart = Math.max(yStart, 0);
            }
            if (xStart+width<this.width && yStart+height<this.height) {
                List<T> firstParamList = new ArrayList<T>();
                List<T> secondParamList = new ArrayList<T>();
                List<T> thirdParamList = new ArrayList<T>();
                for (int x=xStart; x<xStart+width; x++){
                    for (int y=yStart; y<yStart+height; y++){
                        firstParamList.add(this.imgArray[x][y][0]);
                        secondParamList.add(this.imgArray[x][y][1]);
                        thirdParamList.add(this.imgArray[x][y][2]);
                    }
                }
                T firstMedian = firstParamList.get(firstParamList.size()/2);
                T secondMedian = secondParamList.get(secondParamList.size()/2);
                T thirdMedian = thirdParamList.get(thirdParamList.size()/2);
                return (T[])new Object[]{firstMedian,secondMedian,thirdMedian};
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
                            firstParamSum += (float) this.imgArray[x][y][0];
                            secondParamSum += (float)this.imgArray[x][y][1];
                            thirdParamSum += (float) this.imgArray[x][y][2];
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
                            firstParamSum += (int) this.imgArray[x][y][0];
                            secondParamSum += (int)this.imgArray[x][y][1];
                            thirdParamSum += (int) this.imgArray[x][y][2];
                        }
                    }
                    int firstParamAverage=Math.round(firstParamSum*1f/nbPixelsChecked);
                    int secondParamAverage=Math.round(secondParamSum*1f/nbPixelsChecked);
                    int thirdParamAverage=Math.round(thirdParamSum*1f/nbPixelsChecked);
                    return (T[])new Object[]{firstParamAverage,secondParamAverage,thirdParamAverage};
                }
                else{
                    System.out.println("Ce cas ne devrait pas arriver");
                    System.out.println("firstParamSum = 0");
                    System.out.println("secondParamSum = 0");
                    System.out.println("thirdParamSum = 0");
                    return (T[])new Object[]{0,0,0};
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
}
