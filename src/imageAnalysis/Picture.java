package imageAnalysis;

import imageAnalysis.imageProcessing.PictureEncodingConversion;

import java.awt.image.BufferedImage;

public class Picture {

    private int width;
    private int height;
    private PictureEncoding pictureEncoding;
    private int[][][] imgRGBArray;
    private int[][][] imgBGRArray;
    private float[][][] imgHSBArray;

    public Picture(){
        this.width=800;
        this.height=600;
        this.pictureEncoding = PictureEncoding.RGB;
        this.initImageArrays();
    }

    public Picture(BufferedImage buffImg){
        if (buffImg.getType()==1 || buffImg.getType()==4){
            setImage(buffImg);
        }
        else{
            System.out.println("Bad BufferedImage type("+buffImg.getType()+"), please use RGB(1) or BGR(4) type");
        }
    }

    /**
     * Remet à zéro la valeur de toutes les arrays
     */
    private void initImageArrays(){
        this.imgRGBArray=null;
        this.imgBGRArray=null;
        this.imgHSBArray=null;
    }

    /**
     * Remet à zéro la valeur de l'array RGB
     */
    private void initRGBImageArray(){
        this.imgRGBArray=null;
    }

    /**
     * Remet à zéro la valeur de l'array BGR
     */
    private void initBGRImageArray(){
        this.imgBGRArray=null;
    }

    /**
     * Remet à zéro la valeur de l'array HSB
     */
    private void initHSBImageArray(){
        this.imgHSBArray=null;
    }

    /**
     * Convertit l'image en RGB
     */
    public void convertToRGB(){
        if (this.pictureEncoding!=PictureEncoding.RGB){
            this.imgRGBArray=getRGBImageArray(true);
            this.pictureEncoding=PictureEncoding.RGB;
            this.imgBGRArray=null;
            this.imgHSBArray=null;
        }
    }

    /**
     * Convertit l'image en BGR
     */
    public void convertToBGR(){
        if (this.pictureEncoding!=PictureEncoding.BGR){
            this.imgBGRArray=getBGRImageArray(true);
            this.pictureEncoding=PictureEncoding.BGR;
            this.imgRGBArray=null;
            this.imgHSBArray=null;
        }
    }

    /**
     * Convertit l'image en HSB
     */
    public void convertToHSB(){
        if (this.pictureEncoding!=PictureEncoding.HSB){
            this.imgHSBArray=getHSBImageArray(true);
            this.pictureEncoding=PictureEncoding.HSB;
            this.imgRGBArray=null;
            this.imgBGRArray=null;
        }
    }

    public BufferedImage toBufferedImage(){
        BufferedImage buffImg;
        if (this.pictureEncoding==PictureEncoding.RGB) {
            buffImg = new BufferedImage(this.width, this.height,1);
        }
        else if(this.pictureEncoding==PictureEncoding.BGR){
            buffImg = new BufferedImage(this.width, this.height, 4);
        }
        else if (this.pictureEncoding==PictureEncoding.HSB){
            convertToRGB();
            buffImg = new BufferedImage(this.width, this.height, 1);
        }
        else{
            System.out.println("Ce cas ne devrait pas arriver");
            buffImg=null;
        }
        return buffImg;
    }


    /////////////// Getters et Setters //////////////////

    /**
     * Renvoie l'encodage (RGB, BGR ou HSB) de l'image
     * @return l'encodage de l'image
     */
    public PictureEncoding getPictureEncoding(){ return this.pictureEncoding; }

    /**
     * Donne la liste des triplés RGB de l'image (si l'image est encodée en RGB)
     * @return renvoie une array RGB correspondant à l'image
     */
    public int[][][] getRGBImageArray(){
        return getRGBImageArray(false);
    }
    /**
     * @param forceConversion si vrai, force la conversion de l'image en encodage RGB
     * @return renvoie une array RGB correspondant à l'image
     */
    public int[][][] getRGBImageArray(boolean forceConversion) {
        if (this.pictureEncoding == PictureEncoding.RGB) {
            return this.imgRGBArray;
        }
        else{
            if (forceConversion) {
                if (pictureEncoding == PictureEncoding.HSB) {
                    return PictureEncodingConversion.getRGBfromHSB(this, 0, 0, this.width, this.height, false);
                } else if (pictureEncoding == PictureEncoding.BGR) {
                    return PictureEncodingConversion.getRGBfromBGR(this, 0, 0, this.width, this.height, false);
                } else {
                    System.out.println("Bad image type (this error should not happen)");
                    return null;
                }
            }
            else{
                System.out.println("Picture was not encoded in RGB and conversion was not forced, returning null");
                return null;
            }
        }
    }

    /**
     * Donne la liste des triplés BGR de l'image (si l'image est encodée en BGR)
     * @return renvoie une array BGR correspondant à l'image
     */
    public int[][][] getBGRImageArray(){
        return getBGRImageArray(false);
    }
    /**
     * @param forceConversion si vrai, force la conversion de l'image en encodage BGR
     * @return renvoie une array BGR correspondant à l'image
     */
    public int[][][] getBGRImageArray(boolean forceConversion) {
        if (this.pictureEncoding == PictureEncoding.BGR) {
            return this.imgBGRArray;
        }
        else{
            if (forceConversion) {
                if (pictureEncoding == PictureEncoding.RGB) {
                    return PictureEncodingConversion.getBGRfromRGB(this, 0, 0, this.width, this.height, false);
                } else if (pictureEncoding == PictureEncoding.HSB) {
                    return PictureEncodingConversion.getBGRfromHSB(this, 0, 0, this.width, this.height, false);
                } else {
                    System.out.println("Bad image type (this error should not happen)");
                    return null;
                }
            }
            else{
                System.out.println("Picture was not encoded in BGR and conversion was not forced, returning null");
                return null;
            }
        }
    }

    /**
     * Donne la liste des triplés HSB de l'image (si l'image est encodée en HSB)
     * @return renvoie une array HSB correspondant à l'image
     */
    public float[][][] getHSBImageArray(){
        return getHSBImageArray(false);
    }

    /**
     * @param forceConversion si vrai, force la conversion de l'image en encodage HSB
     * @return renvoie une array HSB correspondant à l'image
     */
    public float[][][] getHSBImageArray(boolean forceConversion) {
        if (this.pictureEncoding == PictureEncoding.HSB) {
            return this.imgHSBArray;
        }
        else{
            if (forceConversion) {
                if (pictureEncoding == PictureEncoding.RGB) {
                    return PictureEncodingConversion.getHSBfromRGB(this, 0, 0, this.width, this.height, false);
                } else if (pictureEncoding == PictureEncoding.BGR) {
                    return PictureEncodingConversion.getHSBfromBGR(this, 0, 0, this.width, this.height, false);
                } else {
                    System.out.println("Bad image type (this error should not happen)");
                    return null;
                }
            }
            else{
                System.out.println("Picture was not encoded in HSB and conversion was not forced, returning null");
                return null;
            }
        }
    }

    /**
     * Renvoie la largeur de l'image
     */
    public int getWidth(){ return this.width; }

    /**
     * Ranvoie la hauteur de l'image
     */
    public int getHeight(){ return this.height; }


    /**
     * Set une image RGB, BGR ou HSB
     * @param array array RGB ayant des valeurs de 0 à 255
     */
    public void setImage(int[][][] array, PictureEncoding pictureEncoding){
        this.width=array.length;
        this.height=array[0].length;
        if (pictureEncoding == PictureEncoding.RGB){
            this.pictureEncoding = PictureEncoding.RGB;
            for (int x = 0; x < this.width; x++) {
                for (int y = 0; y < this.height; y++) {
                    for (int canal = 0; canal < 3; canal++) {
                        if (array[x][y][canal] > 255 || array[x][y][canal] < 0) {
                            this.initImageArrays();
                            return;
                        }
                        this.imgRGBArray[x][y][canal] = array[x][y][canal];
                    }
                }
            }
            this.initBGRImageArray();
            this.initHSBImageArray();
        }
        else if (pictureEncoding == PictureEncoding.BGR){
            this.pictureEncoding = PictureEncoding.BGR;
            for (int x = 0; x < this.width; x++) {
                for (int y = 0; y < this.height; y++) {
                    for (int canal = 0; canal < 3; canal++) {
                        if (array[x][y][canal] > 255 || array[x][y][canal] < 0) {
                            this.initImageArrays();
                            return;
                        }
                        this.imgBGRArray[x][y][canal] = array[x][y][canal];
                    }
                }
            }
            this.initRGBImageArray();
            this.initHSBImageArray();
        }
        else if (pictureEncoding == PictureEncoding.HSB){
            this.pictureEncoding = PictureEncoding.HSB;
            for (int x = 0; x < this.width; x++) {
                for (int y = 0; y < this.height; y++) {
                    for (int canal = 0; canal < 3; canal++) {
                        if (array[x][y][canal] > 1 || array[x][y][canal] < 0) {
                            this.initImageArrays();
                            return;
                        }
                        this.imgHSBArray[x][y][canal] = array[x][y][canal];
                    }
                }
            }
            this.initRGBImageArray();
            this.initBGRImageArray();
        }
        else{
            System.out.println("Bad picture type");
        }
    }


    /**
     * Set une image RGB, BGR ou HSB
     * @param array array
     * @param width largeur de l'image
     * @param height hauteur de l'image
     * @param pictureEncoding type de l'image (RGB, BGR ou HSB)
     */
    public void setImage(int[] array, int width, int height, PictureEncoding pictureEncoding){
        if (array.length == width*height*3){
            this.width = width;
            this.height = height;
            if (pictureEncoding == PictureEncoding.RGB) {
                this.pictureEncoding = pictureEncoding;
                for (int i = 0; i < width * height * 3; i += 3) {
                    if (!(array[i]>255 && array[i]<0 && array[i+1]>255 && array[i+1]<0 && array[i+2]>255 && array[i+2]<0)) {
                        this.imgRGBArray[i / width][i%width][0] = array[i];
                        this.imgRGBArray[i / width][i%width][1] = array[i + 1];
                        this.imgRGBArray[i / width][i%width][2] = array[i + 2];
                    }
                    else {
                        this.initImageArrays();
                        return;
                    }
                }
                this.initBGRImageArray();
                this.initHSBImageArray();
            }
            else if (pictureEncoding == PictureEncoding.BGR) {
                this.pictureEncoding = pictureEncoding;
                for (int i = 0; i < width * height * 3; i += 3) {
                    if (!(array[i]>255 && array[i]<0 && array[i+1]>255 && array[i+1]<0 && array[i+2]>255 && array[i+2]<0)) {
                        this.imgBGRArray[i / width][i%width][0] = array[i];
                        this.imgBGRArray[i / width][i%width][1] = array[i + 1];
                        this.imgBGRArray[i / width][i%width][2] = array[i + 2];
                    }
                    else {
                        this.initImageArrays();
                        return;
                    }
                }
                this.initRGBImageArray();
                this.initHSBImageArray();
            }
            else if (pictureEncoding == PictureEncoding.HSB){
                this.pictureEncoding = pictureEncoding;
                for (int i = 0; i < width * height * 3; i += 3) {
                    if (!(array[i]>1 && array[i]<0 && array[i+1]>1 && array[i+1]<0 && array[i+2]>1 && array[i+2]<0)) {
                        this.imgHSBArray[i / width][i%width][0] = array[i];
                        this.imgHSBArray[i / width][i%width][1] = array[i + 1];
                        this.imgHSBArray[i / width][i%width][2] = array[i + 2];
                    }
                    else {
                        this.initImageArrays();
                        return;
                    }
                }
                this.initRGBImageArray();
                this.initBGRImageArray();
            }
            else{
                System.out.println("Bad picture type");
            }
        }
        else{
            System.out.println("Bad height and width when setting array:");
            System.out.println("Array length: "+array.length+" // Picture size: "+this.width+"*"+this.height+"*3");
        }
    }

    /**
     * Set une image RGB ou BGR
     * @param buffImg l'image en question
     */
    public void setImage(BufferedImage buffImg){
        if (buffImg.getType()==1){
            this.pictureEncoding = PictureEncoding.RGB;
            this.width=buffImg.getWidth();
            this.height=buffImg.getHeight();
            int[] array = new int[this.width*this.height*3];
            buffImg.getRaster().getPixels(0,0,this.width,this.height,array);
            this.setImage(array,this.width,this.height, PictureEncoding.RGB);
        }
        else if (buffImg.getType()==4){
            this.pictureEncoding = PictureEncoding.BGR;
            this.width=buffImg.getWidth();
            this.height=buffImg.getHeight();
            int[] array = new int[this.width*this.height*3];
            buffImg.getRaster().getPixels(0,0,this.width,this.height,array);
            this.setImage(array,this.width,this.height, PictureEncoding.BGR);
        }
        else{
            System.out.println("Bad image type (not RGB or BGR)");
        }
    }
}
