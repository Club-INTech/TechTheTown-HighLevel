package pathfinder;

import container.Service;
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




public class Graphe implements Service{

    @Override
    public void updateConfig() {

    }

    private ArrayList<ObstacleCircular> listCircu;
    private ArrayList<ObstacleRectangular> listRectangu;
    private Table table;
    private ArrayList<Noeud> nodes;
    private ArrayList<Arete> boneslist;
    //dictionnaire contenant les arêtes associées à chaque noeud


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
        this.boneslist=new ArrayList<>();
        this.boneslist=createAretes(nodes);
        long time2=System.currentTimeMillis()-time1;
        System.out.println("Time to create graph (ms): "+time2);

    }

    /**
     *
     * Méthode testant la présence d'un noeud dans un obstacle.
     *
     * @param noeud
     * @param graphe
     * @return
     */

    public static boolean nodeInObstacle(Noeud noeud, Graphe graphe) {
        int mRobotRadius=210;   //à modifier
        int n = graphe.listCircu.size();
        ArrayList<ObstacleRectangular> listRectangu2 = new ArrayList<>();
        Vec2 position0 = new Vec2();
        int xNoeud = noeud.getPosition().getX();
        int yNoeud = noeud.getPosition().getY();
        for (int i = 0; i < n; i++) {
            if ( noeud.getPosition().distance(graphe.listCircu.get(i).getPosition())<graphe.listCircu.get(i).getRadius() ){
                return true;
            }
        }
        int m = graphe.listRectangu.size();

        for (int j = 0; j < m; j++) {
            int xObstaclerectan = graphe.listRectangu.get(j).getPosition().getX();
            int yObstaclerectan = graphe.listRectangu.get(j).getPosition().getY();
            int dx = graphe.listRectangu.get(j).getSizeX() / 2;
            int dy = graphe.listRectangu.get(j).getSizeY() / 2;
            int x1 = xObstaclerectan + dx;
            int x2 = xObstaclerectan - dx;
            int y1 = yObstaclerectan + dy;
            int y2 = yObstaclerectan - dy;
            if ((xNoeud <= x1) && (xNoeud >= x2) && (yNoeud <= y1) && (yNoeud >= y2)) {
                return true;
            }
        }

        if(xNoeud< - 1500 + mRobotRadius || xNoeud>1500-mRobotRadius || yNoeud<mRobotRadius || yNoeud>2000-mRobotRadius){
            return true;
        }
        return false;
    }


    public ArrayList<Noeud> createNodes() {
        int pasX = 200;
        int pasY = 200;
        int xdebut = -1500;
        int ydebut = 0;
        int x;
        int y;
        int n = listCircu.size();
        int m;
        ArrayList<Noeud> node = new ArrayList<>();
        ArrayList<Noeud> nodesToKeep = new ArrayList<>();
        for (int i = 1; i < 3000 / pasX-1; i++) {
            x = i * pasX + xdebut;
            for (int j = 1; j < 2000 / pasY; j++) {
                Vec2 nodeposition = new Vec2();
                nodeposition.setX(x);
                y = j * pasY + ydebut;
                nodeposition.setY(y);
                node.add(new Noeud(nodeposition, 999999999, 0, new ArrayList<Noeud>()));
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
     donc implicitement une liste de noeuds, le tout stocké dans un dictionnaire.

     Maj. Cette méthode permet également le compléter pour chaque noeud du graphe
     le champ contenant la liste de ses noeuds voisins.
     */


    public ArrayList<Arete> createAretes(ArrayList<Noeud>nodes){
        Arete arete;
        ArrayList<Arete> boneslist=new ArrayList<>();
        int n=nodes.size();
        for(int i=0; i<n;i++){
            ArrayList<Arete> listaretes=new ArrayList<>();
            ArrayList<Noeud> voisins = new ArrayList<>();
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
                    cost=Math.sqrt(cost);
                    arete = new Arete(nodes.get(i), nodes.get(j), cost);
                    listaretes.add(arete);
                    voisins.add(nodes.get(j));
                }
            }
            nodes.get(i).setVoisins(voisins);
            boneslist.addAll(listaretes);
            System.out.println(boneslist.size());

        }


        return boneslist;
    }




    public ArrayList<Noeud> getNodes() {
        return nodes;
    }

    public ArrayList<Arete> getBoneslist() {
        return boneslist;
    }

    public boolean traceArete(Noeud noeud1, Noeud noeud2){
        Segment segment=new Segment(noeud1.getPosition(),noeud2.getPosition());
        int n=listCircu.size();
        for(int i=0;i<n;i++){
            if(Geometry.intersects(segment,listCircu.get(i).getCircle())){
                return false;
            }
        }
        return true;

    }
    public void clear(Graphe graphe){
        graphe.nodes=null;
        graphe.boneslist=null;
    }


    public ArrayList<Arete> removeDoublons(ArrayList<Arete> areteslist){
        int n=areteslist.size();
        ArrayList<Arete> aretesToreturn=new ArrayList<>();
        for(int i=0;i<n;i++){
            Boolean toadd=true;
            for(int k=0;k<n;k++){
                if((areteslist.get(i).equals(areteslist.get(k)))){
                    toadd=false;
                }
            }
            if(toadd){
                aretesToreturn.add(aretesToreturn.get(i));
            }

        }
        return aretesToreturn;
    }

    public void createAretesV2(ArrayList<Noeud> noeuds){
        //ArrayList<Arete> listaretes=new ArrayList<>();
        //Arete arete;
        int n=noeuds.size();
        for(int i=0; i<n-1;i++){
            ArrayList<Noeud> voisins = new ArrayList<Noeud>();
            for(int j=i+1;j<n;j++){
                Segment segment=new Segment(noeuds.get(i).getPosition(),noeuds.get(j).getPosition());
                boolean isIntersection=false;
                for(int k=0;k<listCircu.size();k++){
                    if(Geometry.intersects(segment,listCircu.get(k).getCircle())){
                        isIntersection=true;
                    }
                }
                if (!isIntersection) {
                    voisins.add(noeuds.get(j));
                    //double cost = Segment.squaredLength(nodes.get(i).getPosition(), nodes.get(j).getPosition());
                    //arete = new Arete(nodes.get(i), nodes.get(j), cost);
                    //listaretes.add(arete);
                    //nodesbones.put(nodes.get(i), listaretes);
                }
            }
            noeuds.get(i).setVoisins(voisins);
        }
    }


}





