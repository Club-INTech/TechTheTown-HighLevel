package image.analysis;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

public class PictureHSB extends Picture<Float> {

    public PictureHSB() {
        super();
        super.setMinValueInImgArray((float)0);
        super.setMaxValueInImgArray((float)1);
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
    public Integer[][][] getBGRImageArray() {
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
        super.setImage(array, this.minValueInImgArray, this.maxValueInImgArray);
    }

    /**
     * Set une image HSB
     * @param pic image HSB ayant des valeurs de 0 à 1
     */
    public void setImage(PictureHSB pic){
        this.setImage(pic.getImageArray());
    }

    /**
     * Set une image RGB
     * @param array array
     * @param width largeur de l'image
     * @param height hauteur de l'image
     */
    public void setImage(Float[] array, int width, int height) {
        super.setImage(array,width,height,this.minValueInImgArray,this.maxValueInImgArray);
    }
}
