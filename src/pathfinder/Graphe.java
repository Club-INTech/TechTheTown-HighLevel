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
    private ArrayList<ObstacleCircular> listCircu;
    private ArrayList<ObstacleRectangular> listRectangu;
    private ObstacleManager obstacleManager;
    public ArrayList<Noeud> nodes;
    //dictionnaire contenant les arêtes associées à chaque noeud
    public HashMap<Noeud, ArrayList<Arete>> nodesbones;


    public ArrayList<Noeud> createNodes() {

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

        nodes =nodesInObstacles(nodes);
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
                for(int k=0;k<Noeud.listCircu.size();k++){
                    if(!Geometry.intersects(segment,Noeud.listCircu.get(k).getCircle())){
                        double cost=Segment.squaredLength(nodes.get(i).position,nodes.get(j).position);
                        arete = new Arete(nodes.get(i),nodes.get(j),cost);
                        listaretes.add(arete);
                        nodesbones.put(nodes.get(i),listaretes);
                    }

                }




            }

        }


    }

    /*public void int_aretes() {
        ArrayList<Arete> aretes;
        aretes = new ArrayList<Arete>();
        float distance;
        double delta;
        float xa;
        float xb;
        float ya;
        float yb;
        float xc;
        float yc;
        float dx;
        float dy;

        for (Noeud noeud1 : nodes) {

            for (Noeud noeud2 : nodes) {
                distance = (float) Math.sqrt(noeud1.position.getX() * noeud2.position.getX() + noeud1.position.getY() * noeud2.position.getY());
                for (ObstacleCircular obstaclecircular : listCircu) {
                    xa = noeud1.position.getX();
                    xb = noeud2.position.getX();
                    ya = noeud1.position.getY();
                    yb = noeud2.position.getY();
                    xc = obstaclecircular.getCircle().getCenter().getX();
                    yc = obstaclecircular.getCircle().getCenter().getY();
                    dx = xb - xa;
                    dy = yb - ya;
                    delta = Math.pow(2 * (dx * (xa - xc) + dy * (ya - yc)), 2) - 4 * (Math.pow((xb - xa), 2) + Math.pow((yb - ya), 2)) * (Math.pow((xa - xc), 2) + Math.pow((ya - yc), 2) - obstaclecircular.getRadius() * obstaclecircular.getRadius());
                    if (delta < 0) {
                        aretes.add(new Arete(noeud1, noeud2, distance));
                    }

                }

            }
        }
    }
*/




}
