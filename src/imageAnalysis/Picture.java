package imageAnalysis;

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
}
