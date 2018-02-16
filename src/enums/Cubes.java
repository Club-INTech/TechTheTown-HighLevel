package enums;

import java.util.regex.Pattern;

public enum Cubes{
    ORANGE(-1,0,Colors.ORANGE),
    BLUE(0,1,Colors.BLUE),
    GREEN(1,0,Colors.GREEN),
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
    public static Cubes getCubeFromColor(Colors colorToSearch){
        for (Cubes cube : Cubes.values()){
            if (colorToSearch==cube.color){
                return cube;
            }
        }
        return null;
    }
    public static Cubes getCubeNotInPattern(int givenPatternID){
        Colors[] pattern=Patterns.getPatternFromID(givenPatternID);
        for (Colors color : Colors.values()){
            boolean colorNotInPattern=true;
            for (int i=0; i<pattern.length; i++){
                if (pattern[i]==color){
                    colorNotInPattern=false;
                }
            }
            if (!colorNotInPattern){
                return getCubeFromColor(color);
            }
        }
        return null;
    }
}


