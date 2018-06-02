package enums;

import smartMath.Vec2;

public enum TasCubes{
    //Position verte décalée de -10 en X par rapport à la position théorique
    //Position orange décalée de +5 en X par rapport à la position théorique
    //Position orange décalée de +15 en Y par rapport à la position théorique
    TAS_BASE(0,new Vec2(640,540), new Vec2(665,555), //MatchScript 0
            new Vec2(645,555), new Vec2(645,555), //MatchScript 2
            new Vec2(625,540), new Vec2(655,550)), //MatchScript 42


    //Position orange décalée de +10 en Y par rapport à la position théorique
    TAS_CHATEAU_EAU(1,new Vec2(1200,1200),  new Vec2(1195,1215),  //MatchScript 0
            new Vec2(1200,1200),  new Vec2(1200,1200), //MatchScript 2
            new Vec2(1200,1200),  new Vec2(1165,1235)), //MatchScript 42


    //Position verte décalée de -10 en X par rapport à la position théorique
    //Position orange décalée de -10 en X par rapport à la position théorique
    TAS_STATION_EPURATION(2,new Vec2(395,1500), new Vec2(397,1520), //MatchScript 0
            new Vec2(395,1510), new Vec2(395,1510), //MatchScript 2
            new Vec2(395,1500), new Vec2(390,1520)), //MatchScript 42


    TAS_STATION_EPURATION_ENNEMI(3,new Vec2(-400,1500), new Vec2(-400,1500), //MatchScript 0
            new Vec2(-400,1500), new Vec2(-400,1500), //MatchScript 2
            new Vec2(-400,1500), new Vec2(-390,1520)), //MatchScript 42


    TAS_CHATEAU_EAU_ENNEMI(4,new Vec2(-1200,1190), new Vec2(-1200,1190), //MatchScript 0
            new Vec2(-1200,1190), new Vec2(-1200,1190), //MatchScript 2
            new Vec2(-1200,1190), new Vec2(-1200,1190)), //MatchScript 42


    TAS_BASE_ENNEMI(5,new Vec2(-650,540), new Vec2(-650,540), //MatchScript 0
            new Vec2(-650,540), new Vec2(-650,540), //MatchScript 2
            new Vec2(-650,540), new Vec2(-650,540)); //MatchScript 42

    private int id;
    private Vec2 greenCoordsMatchScript0;
    private Vec2 orangeCoordsMatchScript0;

    private Vec2 greenCoordsMatchScript2;
    private Vec2 orangeCoordsMatchScript2;

    private Vec2 greenCoordsMatchScript42;
    private Vec2 orangeCoordsMatchScript42;

    private static boolean symetry;
    private static int matchScriptVersion=2;
    TasCubes(int id, Vec2 greenCoordsMatchScript0, Vec2 orangeCoordsMatchScript0, Vec2 greenCoordsMatchScript2, Vec2 orangeCoordsMatchScript2, Vec2 greenCoordsMatchScript42, Vec2 orangeCoordsMatchScript42){
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

    public Vec2 getCoordsVec2(){
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