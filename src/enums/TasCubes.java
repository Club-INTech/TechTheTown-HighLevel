package enums;

public enum TasCubes{
    TAS_BASE(1,650,540),
    TAS_CHATEAU_EAU(2,1200,1190),
    TAS_STATION_EPURATION(3,400,1500),
    TAS_BASE_ENNEMI(4,-650,540),
    TAS_CHATEAU_EAU_ENNEMI(5,-1200,1190),
    TAS_STATION_EPURATION_ENNEMI(6,-400,1500);

    private int indice;
    private int x;
    private int y;
    TasCubes(int indice, int x, int y){
        this.indice=indice;
        this.x=x;
        this.y=y;
    }

    public int[] getCoords(){
        int[] coords={this.x,this.y};
        return coords;
    }

    public static TasCubes getTasFromID(int id){
        for (TasCubes tas : TasCubes.values()){
            if (tas.indice==id){
                return tas;
            }
        }
        return null;
    }
}