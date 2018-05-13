package enums;

import smartMath.Vect;
import smartMath.VectCart;
import smartMath.VectPol;

public enum TasCubes{
    //Position verte décalée de -10 en X par rapport à la position théorique
    //Position orange décalée de +5 en X par rapport à la position théorique
    //Position orange décalée de +15 en Y par rapport à la position théorique
    TAS_BASE(0,new VectCart(640,540), new VectCart(665,555), //MatchScript 0
            new VectCart(645,555), new VectCart(645,555), //MatchScript 2
            new VectCart(634,554), new VectCart(655,550)), //MatchScript 42


    //Position orange décalée de +10 en Y par rapport à la position théorique
    TAS_CHATEAU_EAU(1,new VectCart(1200,1200),  new VectCart(1195,1215),  //MatchScript 0
            new VectCart(1200,1200),  new VectCart(1200,1200), //MatchScript 2
            new VectCart(1172,1202),  new VectCart(1190,1220)), //MatchScript 42


    //Position verte décalée de -10 en X par rapport à la position théorique
    //Position orange décalée de -10 en X par rapport à la position théorique
    TAS_STATION_EPURATION(2,new VectCart(395,1500), new VectCart(397,1520), //MatchScript 0
            new VectCart(395,1510), new VectCart(395,1510), //MatchScript 2
            new VectCart(397,1500), new VectCart(382,1515)), //MatchScript 42


    TAS_STATION_EPURATION_ENNEMI(3,new VectCart(-400,1500), new VectCart(-400,1500), //MatchScript 0
            new VectCart(-400,1500), new VectCart(-400,1500), //MatchScript 2
            new VectCart(-400,1500), new VectCart(-400,1500)), //MatchScript 42


    TAS_CHATEAU_EAU_ENNEMI(4,new VectCart(-1200,1190), new VectCart(-1200,1190), //MatchScript 0
            new VectCart(-1200,1190), new VectCart(-1200,1190), //MatchScript 2
            new VectCart(-1200,1190), new VectCart(-1200,1190)), //MatchScript 42


    TAS_BASE_ENNEMI(5,new VectPol(-650,540), new VectCart(-650,540), //MatchScript 0
            new VectCart(-650,540), new VectCart(-650,540), //MatchScript 2
            new VectCart(-650,540), new VectCart(-650,540)); //MatchScript 42

    private int id;
    private Vect greenCoordsMatchScript0;
    private Vect orangeCoordsMatchScript0;

    private Vect greenCoordsMatchScript2;
    private Vect orangeCoordsMatchScript2;

    private Vect greenCoordsMatchScript42;
    private Vect orangeCoordsMatchScript42;

    private static boolean symetry;
    private static int matchScriptVersion=2;
    TasCubes(int id, Vect greenCoordsMatchScript0, Vect orangeCoordsMatchScript0, Vect greenCoordsMatchScript2, Vect orangeCoordsMatchScript2, Vect greenCoordsMatchScript42, Vect orangeCoordsMatchScript42){
        this.id=id;
        this.greenCoordsMatchScript0=greenCoordsMatchScript0;
        this.orangeCoordsMatchScript0=orangeCoordsMatchScript0;
        this.greenCoordsMatchScript2=greenCoordsMatchScript2;
        this.orangeCoordsMatchScript2=orangeCoordsMatchScript2;
        this.greenCoordsMatchScript42=greenCoordsMatchScript42;
        this.orangeCoordsMatchScript42=orangeCoordsMatchScript42;
    }

    public int[] getCoords(){
        if (matchScriptVersion==0) {
            if (symetry) {
                return new int[]{this.orangeCoordsMatchScript0.getX(), this.orangeCoordsMatchScript0.getY()};
            } else {
                return new int[]{this.greenCoordsMatchScript0.getX(), this.greenCoordsMatchScript0.getY()};
            }
        }
        else if (matchScriptVersion==42){
            if (symetry) {
                return new int[]{this.orangeCoordsMatchScript42.getX(), this.orangeCoordsMatchScript42.getY()};
            } else {
                return new int[]{this.greenCoordsMatchScript42.getX(), this.greenCoordsMatchScript42.getY()};
            }
        }
        else{
            if (symetry) {
                return new int[]{this.orangeCoordsMatchScript2.getX(), this.orangeCoordsMatchScript2.getY()};
            } else {
                return new int[]{this.greenCoordsMatchScript2.getX(), this.greenCoordsMatchScript2.getY()};
            }
        }
    }

    public Vect getCoordsVec2(){
        if (matchScriptVersion==0) {
            if (symetry) {
                return this.orangeCoordsMatchScript0;
            } else {
                return this.greenCoordsMatchScript0;
            }
        }
        else if (matchScriptVersion==42){
            if (symetry) {
                return this.orangeCoordsMatchScript42;
            } else {
                return this.greenCoordsMatchScript42;
            }
        }
        else{
            if (symetry) {
                return this.orangeCoordsMatchScript2;
            } else {
                return this.greenCoordsMatchScript2;
            }
        }
    }

    public int getID(){
        return this.id;
    }
    public static TasCubes getTasFromID(int id){
        for (TasCubes tas : TasCubes.values()){
            if (tas.id==id){
                return tas;
            }
        }
        return null;
    }

    public static void setSymetry(boolean value) {
        symetry = value;
    }
    public static void setMatchScriptVersion(int value) { matchScriptVersion = value;}
}