package pathfinder;

import smartMath.Vec2;
import sun.font.TrueTypeFont;
import table.obstacles.ObstacleCircular;
import table.obstacles.ObstacleManager;
import table.obstacles.ObstacleRectangular;
import tests.container.A;

import java.util.ArrayList;




public class Graphe {
    private ObstacleManager obstacleManager;
    private ArrayList<ObstacleCircular> listCircu;
    private ArrayList<ObstacleRectangular> listRectangu;
    private ArrayList<Vec2> tabposition;
    private ArrayList<Noeud> nodes;


    public void int_aretes() {
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

        return nodesInObstacles(nodes);
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



    private Vec2 alineate(int xdepart, int ydepart,int xPointoalinate,int yPointoalinate) {
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


    }

}
