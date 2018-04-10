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
    private ArrayList<ObstacleCircular> mEnnemies;

    private Table table;
    private CopyOnWriteArrayList<Noeud> nodes;
    private CopyOnWriteArrayList<Arete> bonesList;

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
        this.mEnnemies=new ArrayList<>();
        this.table = table;
        createNodes();
        long time1 = System.currentTimeMillis();
        createAretes();
        long time2 = System.currentTimeMillis() - time1;
        this.log = log;
        this.config = config;
        log.debug("Time to create graph (ms): " + time2);
    }

    /** Méthode générant des noeuds sur la table : on crée des noeuds autour
     * des obstacles circulaires (Méthode nodesaroundobstacles) vu que ce sont ces obstacles-ci qu'on devrait
     * esquiver, on rajoute trois autres noeuds afin de choisir les meilleurs chemins
     * pour les actions qu'on veut faire, à savoir l'interrupteur*/

    public void createNodes() {


        nodes = new CopyOnWriteArrayList<>();
        CopyOnWriteArrayList<Noeud> nodesToKeep = new CopyOnWriteArrayList<>();
        this.createNodesAroundCircularObstacles();


        Vec2 positionmilieu=new Vec2(0,1000);
        Noeud nodemilieu=new Noeud(positionmilieu,0,0, new ArrayList<>());
        nodes.add(nodemilieu);

        Vec2 positiondepart=new Vec2(1252, 455);
        Noeud nodepart=new Noeud(positiondepart,0,0, new ArrayList<>());
        nodes.add(nodepart);

        Vec2 positioninterr=new Vec2(650,215);
        Noeud noeudinterr=new Noeud(positioninterr,0,0, new ArrayList<>());
        nodes.add(noeudinterr);


    }

    /**
     * Il s'agit d'une méthode qui crée des aretes, une arete est un segment qui relie deux noeuds,
     * on ne peut tracer une arete que si le segment ne passe pas par des obstacles
     * circulaires et rectangulaires
     */


    public void createAretes() {
        Arete arete;
        bonesList = new CopyOnWriteArrayList<>();
        Noeud nodeI;
        Noeud nodeJ;
        ArrayList<Arete> listAretes;
        //On ajoute aux listes des obstacles circulaires les ennemis et on crée les arêtes
        listCircu.addAll(mEnnemies);
        int n = nodes.size();
        for (int i = 0; i < n; i++) {
            nodeI=nodes.get(i);
            listAretes = new ArrayList<>();
            for (int j = i+1; j < n; j++) {
                nodeJ=nodes.get(j);
                Segment segment = new Segment(nodes.get(i).getPosition(), nodes.get(j).getPosition());
                boolean intersectsWithCircularObstacles = false;
                boolean intersectsWithRectangularObstacles = false;
                for (ObstacleCircular obstacleCircular : listCircu) {
                    if (obstacleCircular.intersects(segment)){
                        intersectsWithCircularObstacles = true;
                        break;
                    }
                }
                for (ObstacleRectangular obstacleRectangular : listRectangu) {
                    if(obstacleRectangular.intersects(segment)){
                        intersectsWithRectangularObstacles=true;
                        break;
                    }
                }
                if (!(intersectsWithCircularObstacles) && !(intersectsWithRectangularObstacles)){
                    arete = new Arete(nodeI, nodeJ);
                    listAretes.add(arete);
                    nodeI.addVoisin(nodeJ);
                    nodeJ.addVoisin(nodeI);
                }
            }
            bonesList.addAll(listAretes);
        }
    }

    /** Méthode ajoutant un noeud au graphe. Cela consiste à remplir le champ de ses noeuds voisins.
     * Cette méthode est appelée par le pathfinding
     * */

    public void addNodeInGraphe(Noeud noeud) {
        ArrayList<Noeud> voisins = new ArrayList<>();

        for (Noeud currentNode : nodes) {

            Segment segment = new Segment(noeud.getPosition(), currentNode.getPosition());
            boolean isIntersection = false;

            for (ObstacleCircular obstacleCircular : listCircu) {
                if (Geometry.intersects(segment, obstacleCircular.getCircle())) {
                    isIntersection = true;
                }
            }
            if (!isIntersection) {
                voisins.add(currentNode);
                ArrayList<Noeud> noeuds = currentNode.getVoisins();
                noeuds.add(noeud);
                currentNode.addVoisins(noeuds);
            }
        }
        noeud.addVoisins(voisins);
        nodes.add(noeud);
    }



    /**
     * Cette méthode supprime un noeud, un noeud a des voisins, ce noeud n'existe plus s'il est supprimé de la liste des voisins de ses voisins
     * @param noeud
     */


    public void removeNode(Noeud noeud){
        ArrayList<Noeud> voisins=noeud.getVoisins();
        for(Noeud node : voisins){
            if(node.getVoisins().contains(noeud)){
                node.getVoisins().remove(noeud);
            }
        }
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
    public void createNodesAroundCircularObstacles(){
        //mEnnemies=table.getObstacleManager().getmEnnemies();
        ArrayList<Vec2> pointsToReturn=new ArrayList<>();
        int d=30;//distance qu'on ajoute pour que les noeuds ne soient pas dans les obstacles
        /*
        on crée des noeuds autour des obstacles circulaires
         */
        for(ObstacleCircular obstacleCircular : listCircu) {
            Circle obstaclecircle=new Circle(obstacleCircular.getPosition(),obstacleCircular.getRadius()+d);
            ArrayList<Vec2> l = obstaclecircle.pointsaroundcircle(10);
            pointsToReturn.addAll(l);
        }
        /*
          On crée des noeuds autour des obstacles mobiles
         */

        for(ObstacleCircular obstacleCircular : mEnnemies) {
            Circle obstaclecircle=new Circle(obstacleCircular.getPosition(),obstacleCircular.getRadius()+d);
            ArrayList<Vec2> l = obstaclecircle.pointsaroundcircle(10);
            pointsToReturn.addAll(l);
        }


        /*
        On vérifie pour chaque point s'il n'y a pas d'intersection avec les obstacles circulaires
         */
        ArrayList<Vec2> points=(ArrayList<Vec2>)pointsToReturn.clone();
        for(Vec2 point : points){
            for(ObstacleRectangular obstacleRectangular : listRectangu){
                if(table.getObstacleManager().isPositionInObstacle(point,obstacleRectangular) ){
                    pointsToReturn.remove(point);
                }
            }
            for(ObstacleCircular obstacleCircular : listCircu){
                if(!(table.getObstacleManager().isRobotInTable(point)) || table.getObstacleManager().isPositionInObstacle(point,obstacleCircular )){
                    pointsToReturn.remove(point);
                }
            }
        }
        int f=pointsToReturn.size();
        for(int i=0;i<f;i++){
            Noeud nodetoadd=new Noeud(pointsToReturn.get(i),0,0,new ArrayList<>());
            nodes.add(nodetoadd);
        }


    }

    /**
     * Méthode permettant de supprimer un obstacle
     */
    public void removeObstacle(){
        this.listCircu = table.getObstacleManager().getmCircularObstacle();
        this.listRectangu = table.getObstacleManager().getRectangles();
        createNodes();
        createAretes();
    }
    /**
     * Méthode réinitialisant le graphe, à appeler après chaque utilisation de findmyway
     */

    public void reInitGraphe(Noeud noeudDepart, Noeud noeudArrive) {
        for (Noeud node :this.getNodes()) {
            node.setPred(null);
            node.setCout(-1);
            node.setHeuristique(999999999);
            node.removeNeighbour(noeudDepart);
            node.removeNeighbour(noeudArrive);
        }
        this.removeNode(noeudDepart);
        this.removeNode(noeudArrive);
    }
    public CopyOnWriteArrayList<Noeud> getNodes() {
        return nodes;
    }

    public CopyOnWriteArrayList<Arete> getBoneslist() {
        return bonesList;
    }

}





