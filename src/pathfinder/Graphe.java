package pathfinder;

import container.Service;
import enums.ConfigInfoRobot;
import pfg.config.Config;
import smartMath.Circle;
import smartMath.Geometry;
import smartMath.Segment;
import smartMath.Vec2;
import table.Table;
import table.obstacles.ObstacleCircular;
import table.obstacles.ObstacleRectangular;
import utils.Log;

import java.util.ArrayList;


public class Graphe implements Service {

    private Log log;
    private Config config;
    private ArrayList<ObstacleCircular> listCircu;
    private ArrayList<ObstacleRectangular> listRectangu;
    private Table table;
    private ArrayList<Noeud> nodes;
    private ArrayList<Arete> boneslist;

    /**
     * Méthode qui crée les noeuds : créer un grillage et éliminer les noeuds
     * là où il y'a des obstacles
     */

    public Graphe(Log log, Config config, Table table) {
        this.listCircu = new ArrayList<>();
        this.listCircu = table.getObstacleManager().getmCircularObstacle();
        this.listRectangu = new ArrayList<>();
        listRectangu = table.getObstacleManager().getRectangles();
        this.table = table;
        this.nodes = createNodes();
        long time1 = System.currentTimeMillis();
        this.boneslist = createAretes(nodes);
        long time2 = System.currentTimeMillis() - time1;
        this.log = log;
        this.config = config;
        log.debug("Time to create graph (ms): " + time2);

    }

    /**
     * Méthode testant la présence d'un noeud dans un obstacle.
     */


    public boolean nodeInObstacle(Noeud noeud) {
        int n=listCircu.size();
        int m=listRectangu.size();
        boolean inobstacle=false;
        for(int i=0;i<n;i++){
            if(table.getObstacleManager().isPositionInObstacle(noeud.getPosition(),listCircu.get(i))){
                inobstacle=true;
            }
        }
        for(int i=0;i<m;i++){
            if(table.getObstacleManager().isPositionInObstacle(noeud.getPosition(),listRectangu.get(i))){
                inobstacle=true;
            }
        }
       return inobstacle;
    }

    /** Méthode générant des noeuds sur la table   */

    public ArrayList<Noeud> createNodes() {
        int pasX = 200;
        int pasY = 200;
        int xdebut = -1500;
        int ydebut = 0;
        int x;
        int y;
        ArrayList<Noeud> node = new ArrayList<>();
        ArrayList<Noeud> nodesToKeep = new ArrayList<>();
        ArrayList<Noeud> nodestoaddaroundobstacles=this.createNodesObstaclesCirculaires();
        /*for (int i = 1; i < 3000 / pasX - 1; i++) {
            x = i * pasX + xdebut;
            for (int j = 1; j < 2000 / pasY - 1; j++) {
                Vec2 nodeposition = new Vec2();
                nodeposition.setX(x);
                y = j * pasY + ydebut;
                nodeposition.setY(y);
                node.add(new Noeud(nodeposition, 999999999, -1, new ArrayList<Noeud>()));
            }
        }*/
        int k = node.size();
        for (int i = 0; i < k; i++) {
            if (!(nodeInObstacle(node.get(i)))) {
                nodesToKeep.add(node.get(i));
            }
        }
        nodesToKeep.addAll(nodestoaddaroundobstacles);
        Vec2 positionmilieu=new Vec2(0,1000);
        Noeud nodemilieu=new Noeud(positionmilieu,0,0,null);
        this.addNodeInGraphe(nodemilieu);
        nodesToKeep.add(nodemilieu);
        Vec2 positiondepart=new Vec2(1252, 455);
        Noeud nodepart=new Noeud(positiondepart,0,0,null);
        this.addNodeInGraphe(nodepart);
        nodesToKeep.add(nodepart);
        return nodesToKeep;

    }

    /**
     * Méthode qui crée des aretes : une arete c'est un segment avec un cout qui est pour
     * l'instant la distance entre les noeuds, on crée les aretes de telle sortes à ce que
     * ca ne rencontre jamais un obstacles circulaires, donc la ou il y'a une arete il y'a
     * déja un chemin à suivre, à chaque noeud, on associe une liste d'arete qui lui est propre
     * donc implicitement une liste de noeuds, le tout stocké dans un dictionnaire.
     * <p>
     * Maj. Cette méthode permet également le compléter pour chaque noeud du graphe
     * le champ contenant la liste de ses noeuds voisins.
     */


    public ArrayList<Arete> createAretes(ArrayList<Noeud> nodes) {
        Arete arete;
        ArrayList<Arete> boneslist = new ArrayList<>();
        int n = nodes.size();
        for (int i = 0; i < n; i++) {
            ArrayList<Arete> listaretes = new ArrayList<>();
            ArrayList<Noeud> voisins = new ArrayList<>();
            for (int j = i + 1; j < n; j++) {
                Segment segment = new Segment(nodes.get(i).getPosition(), nodes.get(j).getPosition());
                boolean isIntersection = false;
                for (int k = 0; k < listCircu.size(); k++) {
                    if (Geometry.intersects(segment, listCircu.get(k).getCircle())) {
                        isIntersection = true;
                    }
                }
                if (!isIntersection) {
                    double cost = Segment.squaredLength(nodes.get(i).getPosition(), nodes.get(j).getPosition());
                    cost = Math.sqrt(cost);
                    arete = new Arete(nodes.get(i), nodes.get(j), cost);
                    listaretes.add(arete);
                    voisins.add(nodes.get(j));
                }
            }
            nodes.get(i).setVoisins(voisins);
            boneslist.addAll(listaretes);
        }
        return boneslist;
    }

    /** Méthode ajoutant un au graphe. Cela consiste à remplir le champ de ses noeuds voisins.   */

    public void addNodeInGraphe(Noeud noeud) {
        ArrayList<Noeud> voisins = new ArrayList<>();

        for (int j = 0; j < nodes.size(); j++) {

            Segment segment = new Segment(noeud.getPosition(), nodes.get(j).getPosition());
            boolean isIntersection = false;

            for (int k = 0; k < listCircu.size(); k++) {

                if (Geometry.intersects(segment, listCircu.get(k).getCircle())) {
                    isIntersection = true;
                }
            }
            if (!isIntersection) {

                voisins.add(nodes.get(j));
                ArrayList<Noeud> noeuds = nodes.get(j).getVoisins();
                noeuds.add(noeud);
                nodes.get(j).setVoisins(noeuds);
            }
        }
        noeud.setVoisins(voisins);
        nodes.add(noeud);
    }


    public ArrayList<Noeud> getNodes() {
        return nodes;
    }

    public ArrayList<Arete> getBoneslist() {
        return boneslist;
    }

    public boolean traceArete(Noeud noeud1, Noeud noeud2) {
        Segment segment = new Segment(noeud1.getPosition(), noeud2.getPosition());
        int n = listCircu.size();
        for (int i = 0; i < n; i++) {
            if (Geometry.intersects(segment, listCircu.get(i).getCircle())) {
                return false;
            }
        }
        return true;

    }


    public void removeNode(Noeud noeud){
        nodes.remove(noeud);
    }

    /**
     * Cette méthode crée des noeuds autour des obstacles
     * @return
     */
    public ArrayList<Noeud> createNodesObstaclesCirculaires(){
        int n=listCircu.size();
        ArrayList<Vec2> pointstoreturn=new ArrayList<>();
        ArrayList<Noeud> nodes=new ArrayList<>();
        int d=30;//distance qu'on ajoute pour que les noeuds ne soient pas dans les obstacles
        for(int i=0;i<n;i++) {
            Circle obstaclecircle=new Circle(listCircu.get(i).getPosition(),listCircu.get(i).getRadius()+d);
            ArrayList<Vec2> l = obstaclecircle.pointsaroundcircle(12);
            pointstoreturn.addAll(l);
        }
        int m=pointstoreturn.size();
        for(int i=0;i<m;i++){
            Noeud nodetoadd=new Noeud(pointstoreturn.get(i),0,0,null);
            this.addNodeInGraphe(nodetoadd);
            nodes.add(nodetoadd);
        }
        int l=nodes.size();
        ArrayList<Noeud> nodestoreturn=new ArrayList<>();
        for(int i=0;i<l;i++){
            if (!(nodeInObstacle(nodes.get(i))) & table.getObstacleManager().isRobotInTable(nodes.get(i).getPosition())) {
                nodestoreturn.add(nodes.get(i));
            }
        }
        return nodestoreturn;
    }

    @Override
    public void updateConfig() {
        int r=config.getInt(ConfigInfoRobot.ROBOT_RADIUS);
    }
}





