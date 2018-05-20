package image.analysis;

public class PictureEnhancement {

    public static void multiplyPictureFirstComponent(Picture pic, double multiplier){
        multiplyPicutureComponent(pic,0,multiplier);
    }
    public static void multiplyPictureSecondComponent(Picture pic, double multiplier){
        multiplyPicutureComponent(pic,1,multiplier);
    }
    public static void multiplyPictureThirdComponent(Picture pic, double multiplier){
        multiplyPicutureComponent(pic,2,multiplier);
    }

    private static void multiplyPicutureComponent(Picture pic, int whichComponent, double multiplier){
        Object thresholdMin = pic.getMinValueInImgArray();
        Object thresholdMax = pic.getMaxValueInImgArray();
        if (thresholdMin instanceof Float){
            Float[][][] convertedArray = (Float[][][]) pic.getImageArray();
            Float thresholdMinConverted = (Float) thresholdMin;
            Float thresholdMaxConverted = (Float) thresholdMax;
            for (int x=0; x<convertedArray.length; x++){
                for (int y=0; y<convertedArray[0].length; y++){
                    convertedArray[x][y][whichComponent]=(float)(convertedArray[x][y][whichComponent]*multiplier);
                    if (convertedArray[x][y][whichComponent]>thresholdMaxConverted){
                        convertedArray[x][y][whichComponent]=thresholdMaxConverted;
                    }
                    else if (convertedArray[x][y][whichComponent]<thresholdMinConverted){
                        convertedArray[x][y][whichComponent]=thresholdMinConverted;
                    }
                }
            }
        }
        else if (thresholdMin instanceof Integer){
            Integer[][][] convertedArray = (Integer[][][]) pic.getImageArray();
            Integer thresholdMinConverted = (Integer) thresholdMin;
            Integer thresholdMaxConverted = (Integer) thresholdMax;
            for (int x=0; x<convertedArray.length; x++){
                for (int y=0; y<convertedArray[0].length; y++){
                    convertedArray[x][y][whichComponent]=(int)Math.round(convertedArray[x][y][whichComponent]*multiplier);
                    if (convertedArray[x][y][whichComponent]>thresholdMaxConverted){
                        convertedArray[x][y][whichComponent]=thresholdMaxConverted;
                    }
                    else if (convertedArray[x][y][whichComponent]<thresholdMinConverted){
                        convertedArray[x][y][whichComponent]=thresholdMinConverted;
                    }
                }
            }
        }
        else{
            System.out.println("Array type not implemented yet (ce cas ne devrait pas arriver)");
        }
    }

}
