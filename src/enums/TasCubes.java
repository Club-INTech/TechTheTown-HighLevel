package enums;

import smartMath.Vec2;

public enum TasCubes{
    TAS_BASE(0,635,540), //Position pas décalée de -15 en X pa rapport à la position théorique
    TAS_CHATEAU_EAU(1,1200,1190),
    TAS_STATION_EPURATION(2,400,1500),
    TAS_STATION_EPURATION_ENNEMI(3,-400,1500),
    TAS_CHATEAU_EAU_ENNEMI(4,-1200,1190),
    TAS_BASE_ENNEMI(5,-650,540);

    private int id;
    private int x;
    private int y;
    TasCubes(int id, int x, int y){
        this.id=id;
        this.x=x;
        this.y=y;
    }

    public int[] getCoords(){
        int[] coords={this.x,this.y};
        return coords;
    }
    public Vec2 getCoordsVec2(){
        Vec2 coords = new Vec2(this.x,this.y);
        return coords;
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
}