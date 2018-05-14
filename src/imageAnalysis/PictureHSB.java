package imageAnalysis;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

public class PictureHSB extends Picture<Float> {

    public PictureHSB() {
        super();
    }

    public BufferedImage toBufferedImage() {
        Integer[][][] rgbArray = getRGBImageArray();
        BufferedImage buffImg = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_RGB);
        WritableRaster raster = (WritableRaster) buffImg.getData();
        int a = rgbArray.length;
        int b = rgbArray[0].length;
        int[] array = new int[a * b * 3];
        for (int i = 0; i < a; i++) {
            for (int j = 0; j < b; j++) {
                for (int k = 0; k < 3; k++) {
                    array[i * b + j * 3 + k] = rgbArray[i][j][k];
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
        return PictureEncodingConversion.getRGBfromHSB(this,0,0,this.width,this.height,false);
    }

    /**
     * Renvoie une array BGR correspondant à l'image
     */
    public Integer[][][] getBGRImageArray(boolean forceConversion) {
        return PictureEncodingConversion.getBGRfromHSB(this, 0, 0, this.width, this.height, false);
    }

    /**
     * Renvoie une array HSB correspondant à l'image
     */
    public Float[][][] getHSBImageArray() {
        return this.imgArray;
    }


    /**
     * Set une image HSB
     * @param array array HSB ayant des valeurs de 0 à 255
     */
    public void setImage(Float[][][] array) {
        super.setImage(array, 0, 1);
    }

    /**
     * Set une image RGB
     * @param array array
     * @param width largeur de l'image
     * @param height hauteur de l'image
     */
    public void setImage(Float[] array, int width, int height) {
        super.setImage(array,width,height,0,1);
    }
}
