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
import java.util.concurrent.CopyOnWriteArrayList;

public class Graphe implements Service {


    private Log log;
    private Config config;
    private ArrayList<ObstacleCircular> listCircu;
    private ArrayList<ObstacleRectangular> listRectangu;
    private Table table;
    private CopyOnWriteArrayList<Noeud> nodes;
    private CopyOnWriteArrayList<Arete> boneslist;

    @Override
    public void updateConfig() {
        int r=config.getInt(ConfigInfoRobot.ROBOT_RADIUS);
    }


    /**
     * Constructeur du graphe, un graphe c'est des noeuds reliés par des arêtes,
     * on utilise la méthode createNodes et createAretes (Voir la documentation de
     * ces méthodes pour plus de détails)
     */

    public Graphe(Log log, Config config, Table table) {
        this.listCircu = table.getObstacleManager().getmCircularObstacle();
        this.listRectangu = table.getObstacleManager().getRectangles();
        this.table = table;
        this.nodes = createNodes();
        long time1 = System.currentTimeMillis();
        this.boneslist = createAretes();
        long time2 = System.currentTimeMillis() - time1;
        this.log = log;
        this.config = config;
        log.debug("Time to create graph (ms): " + time2);
    }

    /** Méthode générant des noeuds sur la table : on crée des noeuds autour
     * des obstacles circulaires (Méthode nodesaroundobstacles) vu que ce sont ces obstacles-ci qu'on devrait
     * esquiver, on rajoute trois autres noeuds afin de choisir les meilleurs chemins
     * pour les actions qu'on veut faire, à savoir l'interrupteur*/

    public CopyOnWriteArrayList<Noeud> createNodes() {

        CopyOnWriteArrayList<Noeud> nodesToKeep = new CopyOnWriteArrayList<>();
        ArrayList<Noeud> nodestoaddaroundobstacles=this.createNodesAroundCircularObstacles();
        nodesToKeep.addAll(nodestoaddaroundobstacles);

        Vec2 positionmilieu=new Vec2(0,1000);
        Noeud nodemilieu=new Noeud(positionmilieu,0,0, new ArrayList<>());
        nodesToKeep.add(nodemilieu);

        Vec2 positiondepart=new Vec2(1252, 455);
        Noeud nodepart=new Noeud(positiondepart,0,0, new ArrayList<>());
        nodesToKeep.add(nodepart);

        Vec2 positioninterr=new Vec2(650,215);
        Noeud noeudinterr=new Noeud(positioninterr,0,0, new ArrayList<>());
        nodesToKeep.add(noeudinterr);

        return nodesToKeep;
    }

    /**
     * Il s'agit d'une méthode qui crée des aretes, une arete est un segment qui relie deux noeuds,
     * on ne peut tracer une arete que si le segment ne passe pas par des obstacles
     * circulaires et rectangulaires
     */


    public CopyOnWriteArrayList<Arete> createAretes() {
        Arete arete;
        CopyOnWriteArrayList<Arete> boneslist = new CopyOnWriteArrayList<>();
        int n = nodes.size();
        for (int i = 0; i < n; i++) {
            ArrayList<Arete> listaretes = new ArrayList<>();
            ArrayList<Noeud> voisins = new ArrayList<>();
            for (int j = i+1; j < n; j++) {
                Segment segment = new Segment(nodes.get(i).getPosition(), nodes.get(j).getPosition());
                boolean intersectsWithCircularObstacles = false;
                boolean intersectsWithRectangularObstacles = false;
                for (int k = 0; k < listCircu.size(); k++) {
                    if (Geometry.intersects(segment, listCircu.get(k).getCircle())){
                        intersectsWithCircularObstacles = true;
                    }
                }
                for (int l = 0; l<listRectangu.size(); l++) {
                    if(Geometry.intersects(segment, listRectangu.get(l).getRectangle())){
                        intersectsWithRectangularObstacles=true;
                    }
                }
                if (!(intersectsWithCircularObstacles) && !(intersectsWithRectangularObstacles)){
                    double cost = Segment.squaredLength(nodes.get(i).getPosition(), nodes.get(j).getPosition());
                    cost = Math.sqrt(cost);
                    arete = new Arete(nodes.get(i), nodes.get(j), cost);
                    listaretes.add(arete);
                    nodes.get(i).addVoisin(nodes.get(j));
                    nodes.get(j).addVoisin(nodes.get(i));
                }
            }
            boneslist.addAll(listaretes);
        }
        return boneslist;
    }

    /** Méthode ajoutant un noeud au graphe. Cela consiste à remplir le champ de ses noeuds voisins.
     * Cette méthode est appelée par le pathfinding
     * */

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
                nodes.get(j).addVoisins(noeuds);
            }
        }
        noeud.addVoisins(voisins);
        nodes.add(noeud);
    }


    public CopyOnWriteArrayList<Noeud> getNodes() {
        return nodes;
    }

    public CopyOnWriteArrayList<Arete> getBoneslist() {
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
     * Cette méthode crée des noeuds autour des obstacles circulaires
     * , on crée des points autour des points circualires,
     * on ne garde donc que les noeuds qui vérifient tous les critères à
     * savoir le fait d'être à l'intérieur de la table et ne pas être
     * dans un obstacle
     * @return
     */
    public ArrayList<Noeud> createNodesAroundCircularObstacles(){
        int n=listCircu.size();
        int m=listRectangu.size();
        ArrayList<Vec2> pointstoreturn=new ArrayList<>();
        ArrayList<Noeud> nodestoreturn=new ArrayList<>();
        int d=30;//distance qu'on ajoute pour que les noeuds ne soient pas dans les obstacles
        /*
        on crée des noeuds autour des obstacles circulaires, puis on ne garde que les noeuds qui
        remplissent toutes les conditions
         */
        for(int i=0;i<n;i++) {
            Circle obstaclecircle=new Circle(listCircu.get(i).getPosition(),listCircu.get(i).getRadius()+d);
            ArrayList<Vec2> l = obstaclecircle.pointsaroundcircle(10);
            int p=l.size();
            for(int j=0;j<p;j++){
                if(!(table.getObstacleManager().isPositionInObstacle(l.get(j),listCircu.get(i)))&& table.getObstacleManager().isRobotInTable(l.get(j))){
                    pointstoreturn.add(l.get(j));
                }
            }

        }
        int p=pointstoreturn.size();
        ArrayList<Vec2> points=(ArrayList<Vec2> )pointstoreturn.clone();
        for(int i=0;i<p;i++){
            for(int j=0;j<m;j++){
                if((table.getObstacleManager().isPositionInObstacle(points.get(i),listRectangu.get(j)))&& table.getObstacleManager().isRobotInTable(points.get(i))){
                    pointstoreturn.remove(points.get(i));
                }
            }
        }
        int f=pointstoreturn.size();
        for(int i=0;i<f;i++){
            Noeud nodetoadd=new Noeud(pointstoreturn.get(i),0,0,new ArrayList<>());
            nodestoreturn.add(nodetoadd);
        }
        return nodestoreturn;

    }

    /**
     * Méthode permettant de supprimer un obstacle
     */
    public void removeObstacle(){
        this.listCircu = table.getObstacleManager().getmCircularObstacle();
        this.listRectangu = table.getObstacleManager().getRectangles();
        this.nodes = createNodes();
        this.boneslist = createAretes();
    }

}





