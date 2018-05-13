package enums;

import smartMath.Vect;
import smartMath.VectCart;

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
                return new int[]{position.xRelative, position.yRelative};
            }
        }
        return new int[]{0,0};
    }
    public Colors getColor(){
        return this.color;
    }

    public int[] getRelativeCoords(){
        return new int[]{this.xRelative, this.yRelative};
    }

    public Vect getRelativeCoordsVec2(int tasCubes){
        if (tasCubes<3) {
            return new VectCart(this.xRelative, this.yRelative);
        }
        else{
            return new VectCart(-this.xRelative, this.yRelative);
        }
    }

    public Vect getRelativeCoordsVec2(TasCubes tasCubes){
        if (tasCubes.getID()<3){
            return new VectCart(this.xRelative, this.yRelative);
        }
        else{
            return new VectCart(-this.xRelative, this.yRelative);
        }
    }

    public static Cubes getCubeFromColor(Colors colorToSearch){
        for (Cubes cube : Cubes.values()){
            if (colorToSearch==cube.color){
                return cube;
            }
        }
        return Cubes.YELLOW;
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
            if (colorNotInPattern){
                if (color!=Colors.NULL) {
                    return getCubeFromColor(color);
                }
                else{
                    return Cubes.YELLOW;
                }
            }
        }
        return Cubes.YELLOW;
    }
}


