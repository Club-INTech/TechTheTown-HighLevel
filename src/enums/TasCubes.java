package enums;

public enum TasCubes{
    TAS_BASE(650,540),
    TAS_CHATEAU_EAU(1200,1190),
    TAS_STATION_EPURATION(400,1500);

    private int x;
    private int y;
    TasCubes(int x, int y){
        this.x=x;
        this.y=y;
    }

    public int[] getCoords(){
        int[] coords={this.x,this.y};
        return coords;
    }
}