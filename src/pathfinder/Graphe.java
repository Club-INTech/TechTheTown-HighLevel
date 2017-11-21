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
        System.out.println("haha");
        this.nodes=createNodes();
        long time1=System.currentTimeMillis();
        System.out.println("je suis là");
        this.nodesbones=new HashMap<>();
        this.nodesbones=createAretes();
        System.out.println("Coucou");
        long time2=System.currentTimeMillis()-time1;
        System.out.println("Time to create graph (ms): "+time2);

    }

    public ArrayList<Noeud> createNodes() {
        int pasX = 500;
        int pasY = 400;
        int xdebut = -1500;
        int ydebut = 0;
        int x;
        int y;
        int n = listCircu.size();
        int m = listRectangu.size();
        System.out.println("listRectangu" + m);
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
        System.out.println("nodesize" + k);
        boolean toKeep;
        for (int i = 0; i < k; i++) {
            int xNoeud = node.get(i).getPosition().getX();
            int yNoeud = node.get(i).getPosition().getY();
            toKeep=true;
            for (int j = 0; j < m; j++) {
                int xObstaclerectan =listRectangu.get(j).getPosition().getX();
                int yObstaclerectan = listRectangu.get(j).getPosition().getY();
                int dx = listRectangu.get(j).getSizeX() / 2;
                int dy = listRectangu.get(j).getSizeY() / 2;
                int x1 = xObstaclerectan + dx;
                int x2 = xObstaclerectan - dx;
                int y1 = yObstaclerectan + dy;
                int y2 = yObstaclerectan - dy;
                if ((xNoeud <= x1) && (xNoeud >=x2) && (yNoeud <=y1) && (yNoeud >=y2)){
                    toKeep=false;
                }
            }
            if (toKeep){
                nodesToKeep.add(node.get(i));
            }
        }
        /*for (int i = 0; i < k; i++) {
            int xNoeud = node.get(i).getPosition().getX();
            int yNoeud = node.get(i).getPosition().getY();
            for (int j = 0; j < n; j++) {
                int xcentreObstacleCircu = listCircu.get(j).getPosition().getX();
                int ycentreObstacleCircu = listCircu.get(j).getPosition().getY();
                int rayonObstacleCircu = listCircu.get(j).getRadius();

                if (Math.pow(xNoeud - xcentreObstacleCircu, 2) + Math.pow(yNoeud - ycentreObstacleCircu, 2) <= Math.pow(rayonObstacleCircu, 2)) {
                    nodestoremove.add(node.get(i));
                    System.out.println("coucouuuuuuuuu");
                }
                //int distanceDeSecurite=264;
                //test pour savoir si les noeuds se trouvent à côté des obstacles circulaires
                /*if (Math.pow(xNoeud - xcentreObstacleCircu, 2) + Math.pow(yNoeud - ycentreObstacleCircu, 2) < Math.pow(rayonObstacleCircu + distanceDeSecurite, 2)) {
                    nodes.get(i).heuristique = 1;
                }
                */

            //}

       // }
        System.out.println("taille de nodesTokeep"+nodesToKeep.size());
        return nodesToKeep;
    }

    //Méthode pour tester si les noeuds rencontrent des obstacles

    /*private ArrayList<Noeud> nodesInObstacles(ArrayList<Noeud> noeuds) {
        int n = listCircu.size();
        int m = listRectangu.size();
        int k = noeuds.size();
        ArrayList<Noeud> noeudstoremove=new ArrayList<>();
        /*for (int i = 0; i < k; i++) {
            for (int j = 0; j < m; j++) {
                int xObstaclerectan = listRectangu.get(j).getPosition().getX();
                int yObstaclerectan = listRectangu.get(j).getPosition().getY();
                int dx = listRectangu.get(j).getSizeX() / 2;
                int dy = listRectangu.get(j).getSizeY() / 2;
                int x1 = xObstaclerectan + dx;
                int x2 = xObstaclerectan - dx;
                int y1 = yObstaclerectan + dy;
                int y2 = yObstaclerectan - dy;
                if (noeuds.get(i).position.getX()<x1 && noeuds.get(i).position.getX()<x2 && noeuds.get(i).position.getY()<y1 && noeuds.get(i).position.getY()<y2){
                    noeuds.remove(i);
                }
            }
        }
        System.out.println("listCircu"+listCircu.size());
        for (int i = 0; i < k; i++) {
            int xNoeud = noeuds.get(i).getPosition().getX();
            int yNoeud = noeuds.get(i).getPosition().getY();
            for (int j = 0; j < n; j++) {
                int xcentreObstacleCircu = listCircu.get(j).getPosition().getX();
                int ycentreObstacleCircu = listCircu.get(j).getPosition().getY();
                int rayonObstacleCircu = listCircu.get(j).getRadius();
                System.out.println("xNoeud"+xNoeud);
                System.out.println("yNoeud"+yNoeud);

                //int distanceDeSecurite=264;
                if (Math.pow(xNoeud - xcentreObstacleCircu, 2) + Math.pow(yNoeud - ycentreObstacleCircu, 2) <= Math.pow(rayonObstacleCircu, 2)) {
                    noeudstoremove.add(nodes.get(i));
                    System.out.println("coucouuuuuuuuu");
                }
                //test pour savoir si les noeuds se trouvent à côté des obstacles circulaires
                /*if (Math.pow(xNoeud - xcentreObstacleCircu, 2) + Math.pow(yNoeud - ycentreObstacleCircu, 2) < Math.pow(rayonObstacleCircu + distanceDeSecurite, 2)) {
                    nodes.get(i).heuristique = 1;
                }

            }
        System.out.println("le nombre de noeuds à supprimer"+noeudstoremove.size());
        return noeuds;
        }
        */
    /**Méthode qui crée des aretes : une arete c'est un segment avec un cout qui est pour
    l'instant la distance entre les noeuds, on crée les aretes de telle sortes à ce que
    ca ne rencontre jamais un obstacles circulaires, donc la ou il y'a une arete il y'a
    déja un chemin à suivre, à chaque noeud, on associe une liste d'arete qui lui est propre
    donc implicitement une liste de noeuds, le tout stocké dans un dictionnaire
     */

    public HashMap<Noeud,ArrayList<Arete>> createAretes(){
        ArrayList<Arete> listaretes=new ArrayList<>();
        Arete arete;

        int l=0;
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
                    l=l+1;
                    listaretes.add(arete);
                    nodesbones.put(nodes.get(i), listaretes);
                }


            }

        }
    System.out.println("l"+l);
    return nodesbones;


    }

    public ArrayList<Noeud> getNodes() {
        return nodes;
    }

    public HashMap<Noeud, ArrayList<Arete>> getNodesbones() {
        return nodesbones;
    }
}





