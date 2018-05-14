package imageAnalysis;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.Arrays;

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
     * Set une image BGR
     * @param array array BGR ayant des valeurs de 0 à 255
     */
    public void setImage(Integer[][][] array) {
        super.setImage(array, 0, 255);
    }

    /**
     * Set une image BGR
     * @param array array
     * @param width largeur de l'image
     * @param height hauteur de l'image
     */
    public void setImage(Integer[] array, int width, int height) {
        super.setImage(array,width,height,0,255);
    }
}
