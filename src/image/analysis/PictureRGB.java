package image.analysis;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

public class PictureRGB extends Picture<Integer> {

    public PictureRGB() {
        super();
        super.setMinValueInImgArray(0);
        super.setMaxValueInImgArray(255);
    }

    /**
     * Convertit l'image en BufferedImage
     * @return renvoie une bufferedImage
     */
    public BufferedImage toBufferedImage() {
        BufferedImage buffImg = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_RGB);
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
        return this.imgArray;
    }

    /**
     * Renvoie une array BGR correspondant à l'image
     */
    public Integer[][][] getBGRImageArray(boolean forceConversion) {
        return PictureEncodingConversion.getBGRfromRGB(this, 0, 0, this.width, this.height, false);
    }

    /**
     * Renvoie une array HSB correspondant à l'image
     */
    public Float[][][] getHSBImageArray() {
        return PictureEncodingConversion.getHSBfromRGB(this, 0, 0, this.width, this.height, false);
    }

    /**
     * Set une image RGB
     * @param array array RGB ayant des valeurs de 0 à 255
     */
    public void setImage(Integer[][][] array) {
        super.setImage(array, this.minValueInImgArray, this.maxValueInImgArray);
    }

    /**
     * Set une image RGB
     * @param pic image RGB ayant des valeurs de 0 à 255
     */
    public void setImage(PictureRGB pic){
        this.setImage(pic.getImageArray());
    }

    /**
     * Set une image RGB
     * @param array array
     * @param width largeur de l'image
     * @param height hauteur de l'image
     */
    public void setImage(Integer[] array, int width, int height) {
        super.setImage(array,width,height,this.minValueInImgArray,this.maxValueInImgArray);
    }
}
