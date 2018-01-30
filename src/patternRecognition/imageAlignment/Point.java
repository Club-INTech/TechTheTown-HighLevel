package patternRecognition.imageAlignment;

public class Point {
    private int x;
    private int y;
    private int r;
    private int g;
    private int b;

    Point(int x, int y){
        this.x=x;
        this.y=y;
        this.r=0;
        this.g=0;
        this.b=0;
    }

    public void setR(int r) {
        this.r = r;
    }

    public void setG(int g) {
        this.g = g;
    }

    public void setB(int b) {
        this.b = b;
    }
}
