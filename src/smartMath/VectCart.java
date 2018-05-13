package smartMath;

public class VectCart extends Vect{

    public VectCart(){
        super();
    }

    public VectCart(int x, int y){
        super();
        double rVect=Math.sqrt(x*x+y*y);
        double aVect=0;
        if (rVect != 0) {
            aVect = Math.min((double) Math.abs(x), Math.abs(y)) / Math.max(Math.abs(x), Math.abs(y));
        }
        super.setX(x);
        super.setY(y);
        super.setR(rVect);
        super.setA(aVect);
    }

    public VectCart(float x, float y){
        super();
        int xVect=Math.round(x);
        int yVect=Math.round(y);
        double rVect=Math.sqrt(x*x+y*y);
        double aVect=0;
        if (rVect != 0) {
            aVect = Math.min((double) Math.abs(x), Math.abs(y)) / Math.max(Math.abs(x), Math.abs(y));
        }
        super.setX(xVect);
        super.setY(yVect);
        super.setR(rVect);
        super.setA(aVect);
    }

    public VectCart(double x, double y){
        super();
        int xVect=(int)Math.round(x);
        int yVect=(int)Math.round(y);
        double rVect=Math.sqrt(x*x+y*y);
        double aVect=0;
        if (rVect != 0) {
            aVect = Math.min(Math.abs(x), Math.abs(y)) / Math.max(Math.abs(x), Math.abs(y));
        }
        super.setX(xVect);
        super.setY(yVect);
        super.setR(rVect);
        super.setA(aVect);
    }

    public VectCart(int x, float y){
        super();
        int yVect=Math.round(y);
        double rVect=Math.sqrt(x*x+y*y);
        double aVect=0;
        if (rVect != 0) {
            aVect = Math.min((double) Math.abs(x), Math.abs(y)) / Math.max(Math.abs(x), Math.abs(y));
        }
        super.setX(x);
        super.setY(yVect);
        super.setR(rVect);
        super.setA(aVect);
    }

    public VectCart(int x, double y){
        super();
        int yVect=(int)Math.round(y);
        double rVect=Math.sqrt(x*x+y*y);
        double aVect=0;
        if (rVect != 0) {
            aVect = Math.min((double) Math.abs(x), Math.abs(y)) / Math.max(Math.abs(x), Math.abs(y));
        }
        super.setX(x);
        super.setY(yVect);
        super.setR(rVect);
        super.setA(aVect);
    }

    public VectCart(float x, int y){
        super();
        int xVect=Math.round(x);
        double rVect=Math.sqrt(x*x+y*y);
        double aVect=0;
        if (rVect != 0) {
            aVect = Math.min((double) Math.abs(x), Math.abs(y)) / Math.max(Math.abs(x), Math.abs(y));
        }
        super.setX(xVect);
        super.setY(y);
        super.setR(rVect);
        super.setA(aVect);
    }


    public VectCart(float x, double y){
        super();
        int xVect=Math.round(x);
        int yVect=(int)Math.round(y);
        double rVect=Math.sqrt(x*x+y*y);
        double aVect=0;
        if (rVect != 0) {
            aVect = Math.min((double) Math.abs(x), Math.abs(y)) / Math.max(Math.abs(x), Math.abs(y));
        }
        super.setX(xVect);
        super.setY(yVect);
        super.setR(rVect);
        super.setA(aVect);
    }
    public VectCart(double x, int y){
        super();
        int xVect=(int)Math.round(x);
        double rVect=Math.sqrt(x*x+y*y);
        double aVect=0;
        if (rVect != 0) {
            aVect = Math.min(Math.abs(x), Math.abs(y)) / Math.max(Math.abs(x), Math.abs(y));
        }
        super.setX(xVect);
        super.setY(y);
        super.setR(rVect);
        super.setA(aVect);
    }
    public VectCart(double x, float y){
        super();
        int xVect=(int)Math.round(x);
        int yVect=Math.round(y);
        double rVect=Math.sqrt(x*x+y*y);
        double aVect=0;
        if (rVect != 0) {
            aVect = Math.min(Math.abs(x), Math.abs(y)) / Math.max(Math.abs(x), Math.abs(y));
        }
        super.setX(xVect);
        super.setY(yVect);
        super.setR(rVect);
        super.setA(aVect);
    }
}
