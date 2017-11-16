package pathfinder;

import smartMath.Vec2;
import table.obstacles.ObstacleCircular;
import table.obstacles.ObstacleManager;
import table.obstacles.ObstacleRectangular;

import java.util.ArrayList;

public class Noeud {
    public Vec2 position;
    int heuristique;
    private static ArrayList<Noeud> nodes;

    public static ArrayList<ObstacleCircular> listCircu;
    private static ArrayList<ObstacleRectangular> listRectangu;

    public Noeud(Vec2 position, int heuristique) {
        this.position = position;
        this.heuristique = heuristique;
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
    //Cette méthode retourne le noeud le plus proche à une position
   /*public static Noeud closestNode(Vec2 position){
        int r=1;
        int x0=position.getX();
        int y0=position.getY();
        int n =nodes.size();
        for(int i=0; i<n;i++){
            int x=nodes.get(i).position.getX();
            int y=nodes.get(i).position.getY();
            if(Math.pow(x-x0,2)+Math.pow(y-y0,2)<Math.pow(r,2)){
                return nodes.get(i);
            }
        }
      */


    }


