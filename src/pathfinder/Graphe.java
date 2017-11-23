package pathfinder;

import smartMath.Geometry;
import smartMath.Segment;
import smartMath.Vec2;
import sun.font.TrueTypeFont;
import table.Table;
import table.obstacles.ObstacleCircular;
import table.obstacles.ObstacleManager;
import table.obstacles.ObstacleRectangular;
import tests.container.A;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


public class Graphe {
    private ArrayList<ObstacleCircular> listCircu;
    private ArrayList<ObstacleRectangular> listRectangu;
    private Table table;
    private ArrayList<Noeud> nodes;
    //dictionnaire contenant les arêtes associées à chaque noeud
    private HashMap<Noeud,ArrayList<Arete>> nodesbones;


    /**Méthode qui crée les noeuds : créer un grillage et éliminer les noeuds
    là où il y'a des obstacles
     */

    public Graphe(Table table){
        this.listCircu=new ArrayList<>();
        this.listCircu = table.getObstacleManager().getmCircularObstacle();
        this.listRectangu=new ArrayList<>();
        listRectangu = table.getObstacleManager().getRectangles();
        this.table=table;
        this.nodes=new ArrayList<Noeud>();
        this.nodes=createNodes();
        long time1=System.currentTimeMillis();
        this.nodesbones=new HashMap<>();
        this.nodesbones=createAretes(nodes);
        long time2=System.currentTimeMillis()-time1;
        System.out.println("Time to create graph (ms): "+time2);

    }

    public ArrayList<Noeud> createNodes() {
        int pasX = 200;
        int pasY = 300;
        int xdebut = -1500;
        int ydebut = 0;
        int x;
        int y;
        int n = listCircu.size();
        int m;
        ArrayList<Noeud> node = new ArrayList<>();
        ArrayList<Noeud> nodesToKeep = new ArrayList<>();
        for (int i = 1; i < 3000 / pasX; i++) {
            x = i * pasX + xdebut;
            for (int j = 1; j < 2000 / pasY; j++) {
                Vec2 nodeposition = new Vec2();
                nodeposition.setX(x);
                y = j * pasY + ydebut;
                nodeposition.setY(y);
                node.add(new Noeud(nodeposition, 0));
            }
        }
        int k = node.size();
        boolean toKeep;
        ArrayList<ObstacleRectangular> listRectangu2 = new ArrayList<>();
        Vec2 position0 = new Vec2();
        for (int i = 0; i < n; i++) {
            ObstacleRectangular obsrectangu = new ObstacleRectangular(position0, 0, 0);
            obsrectangu.setPosition(listCircu.get(i).getPosition());
            obsrectangu.changeDim(listCircu.get(i).getRadius() * 2, listCircu.get(i).getRadius() * 2);
            listRectangu2.add(obsrectangu);
        }
        listRectangu.addAll(listRectangu2);

        m = listRectangu.size();
        for (int i = 0; i < k; i++) {
            int xNoeud = node.get(i).getPosition().getX();
            int yNoeud = node.get(i).getPosition().getY();
            toKeep = true;
            for (int j = 0; j < m; j++) {
                int xObstaclerectan = listRectangu.get(j).getPosition().getX();
                int yObstaclerectan = listRectangu.get(j).getPosition().getY();
                int dx = listRectangu.get(j).getSizeX() / 2;
                int dy = listRectangu.get(j).getSizeY() / 2;
                int x1 = xObstaclerectan + dx;
                int x2 = xObstaclerectan - dx;
                int y1 = yObstaclerectan + dy;
                int y2 = yObstaclerectan - dy;
                if ((xNoeud <= x1) && (xNoeud >= x2) && (yNoeud <= y1) && (yNoeud >= y2)) {
                    toKeep = false;
                }
            }
            if (toKeep) {
                nodesToKeep.add(node.get(i));
            }
        }

        listRectangu.removeAll(listRectangu2);
        System.out.println("oui" + nodesToKeep.size());
        return nodesToKeep;

    }

    /**Méthode qui crée des aretes : une arete c'est un segment avec un cout qui est pour
    l'instant la distance entre les noeuds, on crée les aretes de telle sortes à ce que
    ca ne rencontre jamais un obstacles circulaires, donc la ou il y'a une arete il y'a
    déja un chemin à suivre, à chaque noeud, on associe une liste d'arete qui lui est propre
    donc implicitement une liste de noeuds, le tout stocké dans un dictionnaire
     */


    public HashMap<Noeud,ArrayList<Arete>> createAretes(ArrayList<Noeud>nodes){
        ArrayList<Arete> listaretes=new ArrayList<>();
        Arete arete;
        int n=nodes.size();
        for(int i=0; i<n-1;i++){
            for(int j=i+1;j<n;j++){
                Segment segment=new Segment(nodes.get(i).getPosition(),nodes.get(j).getPosition());
                boolean isIntersection=false;
                for(int k=0;k<listCircu.size();k++){
                    if(Geometry.intersects(segment,listCircu.get(k).getCircle())){
                        isIntersection=true;
                        }
                }
                if (!isIntersection) {
                    double cost = Segment.squaredLength(nodes.get(i).getPosition(), nodes.get(j).getPosition());
                    arete = new Arete(nodes.get(i), nodes.get(j), cost);
                    listaretes.add(arete);
                    nodesbones.put(nodes.get(i), listaretes);
                }


            }

        }
    return nodesbones;


    }

    public ArrayList<Noeud> getNodes() {
        return nodes;
    }

    public HashMap<Noeud, ArrayList<Arete>> getNodesbones() {
        return nodesbones;
    }
}





