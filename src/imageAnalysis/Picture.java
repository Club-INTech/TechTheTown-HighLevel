package imageAnalysis;


import java.awt.image.BufferedImage;
import java.util.Arrays;

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

    //////////////////// Image analysis ////////////////////

    public T[] medianOverRectangle(int xStart, int yStart, int width, int height, boolean canGoOutOfBounds){
        return this.imgArray[xStart][yStart];
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
}
