package pathfinder;

import smartMath.Geometry;
import smartMath.Segment;
import smartMath.Vec2;
import sun.font.TrueTypeFont;
import table.obstacles.ObstacleCircular;
import table.obstacles.ObstacleManager;
import table.obstacles.ObstacleRectangular;
import tests.container.A;

import java.util.ArrayList;
import java.util.HashMap;


public class Graphe {
    static ArrayList<ObstacleCircular> listCircu;
    static ArrayList<ObstacleRectangular> listRectangu;
    private ObstacleManager obstacleManager;
    public ArrayList<Noeud> nodes;
    //dictionnaire contenant les arêtes associées à chaque noeud
    public HashMap<Noeud, ArrayList<Arete>> nodesbones;


    public ArrayList<Noeud> createNodes() {
        HashMap<Noeud, ArrayList<Arete>> nodesbones;
        int pasX = 300;
        int pasY = 200;
        int x = -1500;
        int y = 0;
        Vec2 nodeposition = new Vec2();
        for (int i = 0; i < pasX * 3000; i++) {
            for (int j = 0; j < pasY * 1500; i++) {
                x = x + pasX * i;
                y = y + pasY * j;
                nodeposition.setX(x);
                nodeposition.setY(y);
                nodes.add(new Noeud(nodeposition, 0));
            }
        }

        nodes = nodesInObstacles(nodes);
        return nodes;
    }

    private ArrayList<Noeud> nodesInObstacles(ArrayList<Noeud> nodes) {
        listCircu = obstacleManager.getmCircularObstacle();
        listRectangu = obstacleManager.getRectangles();
        int n = listCircu.size();
        int m = listRectangu.size();
        int k = nodes.size();
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < m; i++) {
                int xObstaclerectan = listRectangu.get(j).getPosition().getX();
                int yObstaclerectan = listRectangu.get(j).getPosition().getY();
                int dx = listRectangu.get(j).getSizeX() / 2;
                int dy = listRectangu.get(j).getSizeY() / 2;
                int x1 = xObstaclerectan + dx;
                int x2 = xObstaclerectan - dx;
                int y1 = yObstaclerectan + dy;
                int y2 = yObstaclerectan - dy;
                if (nodes.get(i).position.getX() < x1 && nodes.get(i).position.getX() < x2 && nodes.get(i).position.getY() < y1 && nodes.get(i).position.getY() < y2) {
                    nodes.remove(i);
                }


            }
        }
        for (int i = 0; i < k; i++) {
            for (int j = 0; i < n; i++) {
                int xcentreObstacleCircu = listCircu.get(j).getPosition().getX();
                int ycentreObstacleCircu = listCircu.get(j).getPosition().getY();
                int rayonObstacleCircu = listCircu.get(j).getRadius();
                int xNoeud = nodes.get(i).position.getX();
                int yNoeud = nodes.get(i).position.getY();
                int distanceDeSecurite=264;
                if (Math.pow(xNoeud - xcentreObstacleCircu, 2) + Math.pow(yNoeud - ycentreObstacleCircu, 2) < Math.pow(rayonObstacleCircu, 2)) {
                    nodes.remove(i);
                }
                //test pour savoir si les noeuds se trouvent à côté des obstacles circulaires
                if (Math.pow(xNoeud - xcentreObstacleCircu, 2) + Math.pow(yNoeud - ycentreObstacleCircu, 2) < Math.pow(rayonObstacleCircu + distanceDeSecurite, 2)) {
                    nodes.get(i).heuristique = 1;
                }


            }


        }
        return nodes;
    }



    public void createAretes(){
        ArrayList<Noeud> nodes=createNodes();
        nodesbones=new HashMap<>();
        ArrayList<Arete> listaretes=new ArrayList<>();
        Arete arete;

        int n=nodes.size();
        for(int i=0; i<n;i++){
            for(int j=1;j<n;i++){
                Segment segment=new Segment(nodes.get(i).position,nodes.get(j).position);
                for(int k=0;k<listCircu.size();k++){
                    if(!Geometry.intersects(segment,listCircu.get(k).getCircle())){
                        double cost=Segment.squaredLength(nodes.get(i).position,nodes.get(j).position);
                        arete = new Arete(nodes.get(i),nodes.get(j),cost);
                        listaretes.add(arete);
                        nodesbones.put(nodes.get(i),listaretes);

                    }

                }




            }

        }


    }

    public boolean aretebetweentwonodes(Noeud noeud1,Noeud noeud2){
        ArrayList<Arete> aretelist=nodesbones.get(noeud1);
        int n=aretelist.size();
        for(int i=0;i<n;i++){
            if(aretelist.get(i).noeud2==noeud2){
                return true;
            }
        }
        return false;


    }




}
