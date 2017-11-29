package pathfinder;

import smartMath.Vec2;
import table.obstacles.ObstacleCircular;
import table.obstacles.ObstacleRectangular;
import tests.container.A;

import java.util.ArrayList;

public class Noeud {
    private Vec2 position;
    private double heuristique;
    private double cout;
    private ArrayList<Noeud> voisins;

    /** Constructeur*/
    public Noeud(Vec2 position, int heuristique, int cout, ArrayList<Noeud> voisins) {
        this.position = position;
        this.heuristique = heuristique;
        this.cout = cout;
        this.voisins = voisins;
    }


    public Vec2 getPosition() {
        return position;
    }

    public double getHeuristique(){
        return heuristique;
    }

    public ArrayList<Noeud> getVoisins() {
        return voisins;
    }

    public double getCout() {
        return cout;
    }

    public void setPosition(Vec2 position) {
        this.position = position;
    }

    public void setHeuristique(double heuristique) {
        this.heuristique = heuristique;
    }

    public void setVoisins(ArrayList<Noeud> voisins){ this.voisins = voisins; }

    public void setCout(double cout) {
        this.cout = cout;
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
