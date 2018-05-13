package smartMath;

public class VectPol extends Vect{

    public VectPol(){
        super();
    }

    public VectPol(float r, float a){
        super();
        int xVect;
        int yVect;
        double rVect;
        double aVect;
        if (r < 0){
            rVect = Math.abs(r);
            aVect = Geometry.moduloSpec(a + Math.PI, Math.PI);
        }
        else{
            rVect = r;
            aVect = a;
        }
        //Attention, si r*Math.cos(a) torp grand, le cast du long en int provoque des pertes de données
        xVect = (int) Math.round(r * Math.cos(a));
        yVect = (int) Math.round(r * Math.sin(a));
        super.setX(xVect);
        super.setY(yVect);
        super.setR(rVect);
        super.setA(aVect);
    }

    public VectPol(double r, double a){
        super();
        int xVect;
        int yVect;
        double rVect;
        double aVect;
        if (r < 0){
            rVect = Math.abs(r);
            aVect = Geometry.moduloSpec(a + Math.PI, Math.PI);
        }
        else{
            rVect = r;
            aVect = a;
        }
        //Attention, si r*Math.cos(a) torp grand, le cast du long en int provoque des pertes de données
        xVect = (int) Math.round(r * Math.cos(a));
        yVect = (int) Math.round(r * Math.sin(a));
        super.setX(xVect);
        super.setY(yVect);
        super.setR(rVect);
        super.setA(aVect);
    }

    public VectPol(int r, float a){
        super();
        int xVect;
        int yVect;
        double rVect;
        double aVect;
        if (r < 0){
            rVect = Math.abs(r);
            aVect = Geometry.moduloSpec(a + Math.PI, Math.PI);
        }
        else{
            rVect = r;
            aVect = a;
        }
        //Attention, si r*Math.cos(a) torp grand, le cast du long en int provoque des pertes de données
        xVect = (int) Math.round(r * Math.cos(a));
        yVect = (int) Math.round(r * Math.sin(a));
        super.setX(xVect);
        super.setY(yVect);
        super.setR(rVect);
        super.setA(aVect);
    }

    public VectPol(int r, double a){
        super();
        int xVect;
        int yVect;
        double rVect;
        double aVect;
        if (r < 0){
            rVect = Math.abs(r);
            aVect = Geometry.moduloSpec(a + Math.PI, Math.PI);
        }
        else{
            rVect = r;
            aVect = a;
        }
        //Attention, si r*Math.cos(a) torp grand, le cast du long en int provoque des pertes de données
        xVect = (int) Math.round(r * Math.cos(a));
        yVect = (int) Math.round(r * Math.sin(a));
        super.setX(xVect);
        super.setY(yVect);
        super.setR(rVect);
        super.setA(aVect);
    }

    public VectPol(float r, double a){
        super();
        int xVect;
        int yVect;
        double rVect;
        double aVect;
        if (r < 0){
            rVect = Math.abs(r);
            aVect = Geometry.moduloSpec(a + Math.PI, Math.PI);
        }
        else{
            rVect = r;
            aVect = a;
        }
        //Attention, si r*Math.cos(a) torp grand, le cast du long en int provoque des pertes de données
        xVect = (int) Math.round(r * Math.cos(a));
        yVect = (int) Math.round(r * Math.sin(a));
        super.setX(xVect);
        super.setY(yVect);
        super.setR(rVect);
        super.setA(aVect);
    }

    public VectPol(double r, float a){
        super();
        int xVect;
        int yVect;
        double rVect;
        double aVect;
        if (r < 0){
            rVect = Math.abs(r);
            aVect = Geometry.moduloSpec(a + Math.PI, Math.PI);
        }
        else{
            rVect = r;
            aVect = a;
        }
        //Attention, si r*Math.cos(a) torp grand, le cast du long en int provoque des pertes de données
        xVect = (int) Math.round(r * Math.cos(a));
        yVect = (int) Math.round(r * Math.sin(a));
        super.setX(xVect);
        super.setY(yVect);
        super.setR(rVect);
        super.setA(aVect);
    }
}
