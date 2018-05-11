package enums;

import smartMath.Vec2;

public enum TasCubes{
    //Position verte décalée de -10 en X par rapport à la position théorique
    //Position orange décalée de +5 en X par rapport à la position théorique
    //Position orange décalée de +15 en Y par rapport à la position théorique
    TAS_BASE(0,new Vec2(640,540), new Vec2(655,550)),

    //Position orange décalée de +10 en Y par rapport à la position théorique
    TAS_CHATEAU_EAU(1,new Vec2(1200,1200),  new Vec2(1200,1210)),


    //Position verte décalée de -10 en X par rapport à la position théorique
    //Position orange décalée de -10 en X par rapport à la position théorique
    TAS_STATION_EPURATION(2,new Vec2(395,1510),  new Vec2(390,1510)),


    TAS_STATION_EPURATION_ENNEMI(3,new Vec2(-400,1500),  new Vec2(-400,1500)),


    TAS_CHATEAU_EAU_ENNEMI(4,new Vec2(-1200,1190),  new Vec2(-1200,1190)),


    TAS_BASE_ENNEMI(5,new Vec2(-650,540),  new Vec2(-650,540));

    private int id;
    private Vec2 greenCoords;
    private Vec2 orangeCoords;

    private static boolean symetry;
    TasCubes(int id, Vec2 greenCoords, Vec2 orangeCoords){
        this.id=id;
        this.greenCoords=greenCoords;
        this.orangeCoords=orangeCoords;
    }

    public int[] getCoords(){
        if (symetry) {
            return new int[]{this.orangeCoords.getX(), this.orangeCoords.getY()};
        }
        else{
            return new int[]{this.greenCoords.getX(), this.greenCoords.getY()};
        }
    }

    public Vec2 getCoordsVec2(){
        if (symetry) {
            return this.orangeCoords;
        }
        else{
            return this.greenCoords;
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
}