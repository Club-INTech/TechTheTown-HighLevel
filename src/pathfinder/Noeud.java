package pathfinder;

import smartMath.Vec2;
import table.obstacles.ObstacleCircular;
import table.obstacles.ObstacleRectangular;

import java.util.ArrayList;

public class Noeud {
    private Vec2 position;
    private int heuristique;


    public Noeud(Vec2 position, int heuristique) {
        this.position = position;
        this.heuristique = heuristique;
    }


    public Vec2 getPosition() {
        return position;
    }

    public int getHeuristique(){
        return heuristique;
    }

    public void setPosition(Vec2 position) {
        this.position = position;
    }

    public void setHeuristique(int heuristique) {
        this.heuristique = heuristique;
    }
}

/*private Vec2 alineate(int xdepart, int ydepart,int xPointoalinate,int yPointoalinate) {
        Vec2 position = new Vec2();
        double r = Math.sqrt((Math.pow(xPointoalinate - xdepart, 2)) - Math.pow(yPointoalinate - ydepart, 2));
        double r1 = 0;
        double teta = Math.atan(ydepart / xdepart) - Math.atan(yPointoalinate / xPointoalinate);
        while ((Math.abs(r - r1) > Math.pow(10, -3)) && (Math.pow(r, 2) != ((Math.pow(xPointoalinate - xdepart, 2) + Math.pow(yPointoalinate - ydepart, 2)))))
            r1 = r1 + 1;

        xPointoalinate = (int) (r * Math.cos(teta));
        yPointoalinate = (int) (r * Math.sin(teta));
        position.setX(xPointoalinate);
        position.setY(yPointoalinate);
        return position;


    }*/
