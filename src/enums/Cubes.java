package enums;

public enum Cubes{
    ORANGE(1,0,Colors.ORANGE),
    BLUE(0,1,Colors.BLUE),
    GREEN(-1,0,Colors.GREEN),
    BLACK(0,-1,Colors.BLACK),
    YELLOW(0,0,Colors.YELLOW),
    NULL(0,0,Colors.NULL);

    private int xRelative;
    private int yRelative;
    private Colors color;

    Cubes(int xRelative, int yRelative, Colors color){
        this.xRelative=xRelative;
        this.yRelative=yRelative;
        this.color=color;
    }

    public static int[] findRelativeCoordsWithColor(Colors colorToSearch){
        for (Cubes position : Cubes.values()){
            if (colorToSearch==position.color){
                int[] toReturn={position.xRelative, position.yRelative};
                return toReturn;
            }
        }
        return null;
    }
    public Colors getColor(){
        return this.color;
    }
    public int[] getRelativeCoords(){
        int[] toReturn={this.xRelative, this.yRelative};
        return toReturn;
    }
}


