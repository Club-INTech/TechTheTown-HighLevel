package imageAnalysis;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

public class PictureBGR extends Picture<Integer> {

    public PictureBGR() {
        super();
    }

     /**
     * Convertit l'image en BufferedImage
     * @return renvoie une bufferedImage
     */
    public BufferedImage toBufferedImage() {
        BufferedImage buffImg = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_BGR);
        WritableRaster raster = (WritableRaster) buffImg.getData();
        int a = this.imgArray.length;
        int b = this.imgArray[0].length;
        int[] array = new int[a * b * 3];
        for (int i = 0; i < a; i++) {
            for (int j = 0; j < b; j++) {
                for (int k = 0; k < 3; k++) {
                    array[i * b + j * 3 + k] = this.imgArray[i][j][k];
                }
            }
        }
        raster.setPixels(0, 0, this.width, this.height, array);
        buffImg.setData(raster);
        return buffImg;
    }


    /**
     * Renvoie une array RGB correspondant à l'image
     */
    public Integer[][][] getRGBImageArray() {
        return PictureEncodingConversion.getRGBfromBGR(this,0,0,this.width,this.height,false);
    }

    /**
     * Renvoie une array BGR correspondant à l'image
     */
    public Integer[][][] getBGRImageArray(boolean forceConversion) {
        return this.imgArray;
    }

    /**
     * Renvoie une array HSB correspondant à l'image
     */
    public Float[][][] getHSBImageArray() {
        return PictureEncodingConversion.getHSBfromBGR(this, 0, 0, this.width, this.height, false);
    }


    /**
     * Set une image RGB
     * @param array array RGB ayant des valeurs de 0 à 255
     */
    public void setImage(int[][][] array) {
        this.width = array.length;
        this.height = array[0].length;
        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
                for (int canal = 0; canal < 3; canal++) {
                    if (array[x][y][canal] > 255 || array[x][y][canal] < 0) {
                        this.imgArray = null;
                        return;
                    }
                    this.imgArray[x][y][canal] = array[x][y][canal];
                }
            }
        }
    }

    /**
     * Set une image RGB
     * @param array  array
     * @param width  largeur de l'image
     * @param height hauteur de l'image
     */
    public void setImage(int[] array, int width, int height) {
        if (array.length == width * height * 3) {
            this.width = width;
            this.height = height;
            for (int i = 0; i < width * height * 3; i += 3) {
                if (!(array[i] > 255 || array[i] < 0 || array[i + 1] > 255 || array[i + 1] < 0 || array[i + 2] > 255 || array[i + 2] < 0)) {
                    this.imgArray[i / width][i % width][0] = array[i];
                    this.imgArray[i / width][i % width][1] = array[i + 1];
                    this.imgArray[i / width][i % width][2] = array[i + 2];
                } else {
                    this.imgArray = null;
                    return;
                }
            }
        } else {
            System.out.println("Bad height and width when setting array:");
            System.out.println("Array length: " + array.length + " // Picture size: " + this.width + "*" + this.height + "*3");
        }
    }

    /**
     * Set une image RGB
     * @param buffImg l'image en question
     */
    public void setImage(BufferedImage buffImg) {
        if (buffImg.getType() == 1) {
            this.width = buffImg.getWidth();
            this.height = buffImg.getHeight();
            int[] array = new int[this.width * this.height * 3];
            buffImg.getRaster().getPixels(0, 0, this.width, this.height, array);
            this.setImage(array, this.width, this.height);
        } else {
            System.out.println("Bad image type: not RGB");
        }
    }
}