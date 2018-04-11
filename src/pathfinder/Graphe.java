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
import table.obstacles.ObstacleProximity;
import table.obstacles.ObstacleRectangular;
import utils.Log;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class Graphe implements Service {


    private Log log;
    private Config config;
    private ArrayList<ObstacleCircular> listCircu;
    private ArrayList<ObstacleRectangular> listRectangu;
    private ArrayList<ObstacleProximity> mobileEnnemies;

    private Table table;
    private ArrayList<Noeud> nodes;
    private ArrayList<Arete> bonesList;

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
        this.log = log;
        this.config = config;
        updateConfig();
        this.listCircu = table.getObstacleManager().getmCircularObstacle();
        this.listRectangu = table.getObstacleManager().getRectangles();
        this.mobileEnnemies = new ArrayList<>();
        this.table = table;
        this.nodes = new ArrayList<>();
        this.bonesList=new ArrayList<>();
        createNodes();
        long time1 = System.currentTimeMillis();
        createAretes();
        long time2 = System.currentTimeMillis() - time1;
        log.debug("Time to create graph (ms): " + time2);
    }

    /** Méthode générant des noeuds sur la table : on crée des noeuds autour
     * des obstacles circulaires (Méthode nodesaroundobstacles) vu que ce sont ces obstacles-ci qu'on devrait
     * esquiver, on rajoute trois autres noeuds afin de choisir les meilleurs chemins
     * pour les actions qu'on veut faire, à savoir l'interrupteur*/

    public void createNodes() {

        this.nodes=new ArrayList<>();
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
        this.bonesList=new ArrayList<>();
        //On ajoute aux listes des obstacles circulaires les ennemis et on crée les arêtes
        log.debug("Nombre d'ennemis confirmés : "+ mobileEnnemies.size());
        int n = nodes.size();
        for (int i = 0; i < n; i++) {
            Noeud nodeI=nodes.get(i);
            ArrayList<Arete> listAretes = new ArrayList<>();
            for (int j = i+1; j < n; j++) {
                Noeud nodeJ=nodes.get(j);
                Segment segment = new Segment(nodeI.getPosition(), nodeJ.getPosition());
                boolean intersectsWithObstacle = false;
                for (ObstacleCircular obstacleCircular : listCircu) {
                    if (obstacleCircular.intersects(segment)){
                        intersectsWithObstacle = true;
                        break;
                    }
                }
                if (!intersectsWithObstacle) {
                    for (ObstacleRectangular obstacleRectangular : listRectangu) {
                        if (obstacleRectangular.intersects(segment)) {
                            intersectsWithObstacle = true;
                            break;
                        }
                    }
                }
                if (!intersectsWithObstacle) {
                    for (ObstacleProximity obstacleMobile : mobileEnnemies) {
                        if (obstacleMobile.intersects(segment)) {
                            intersectsWithObstacle = true;
                            break;
                        }
                    }
                }
                if (!intersectsWithObstacle){
                    Arete arete = new Arete(nodeI, nodeJ);
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
                    break;
                }
            }
            if (!isIntersection) {
                for (ObstacleProximity obstacleCircular : mobileEnnemies) {
                    if (Geometry.intersects(segment, obstacleCircular.getCircle())) {
                        isIntersection = true;
                        break;
                    }
                }
            }
            if (!isIntersection) {
                for (ObstacleRectangular obstacleRectangular : listRectangu) {
                    if (Geometry.intersects(segment, obstacleRectangular.getRectangle())) {
                        isIntersection = true;
                        break;
                    }
                }
            }

            if (!isIntersection) {
                voisins.add(currentNode);
                currentNode.addVoisin(noeud);
            }
        }
        noeud.addVoisins(voisins);
        this.nodes.add(noeud);
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
        ArrayList<Vec2> points=new ArrayList<>();
        int d=30;//distance qu'on ajoute pour que les noeuds ne soient pas dans les obstacles
        /*
        on crée des noeuds autour des obstacles circulaires
         */
        for(ObstacleCircular obstacleCircular : listCircu) {
            Circle obstaclecircle=new Circle(obstacleCircular.getPosition(),obstacleCircular.getRadius()+d);
            ArrayList<Vec2> lcirculaire = obstaclecircle.pointsaroundcircle(10);
            points.addAll(lcirculaire);
        }
        /*
          On crée des noeuds autour des obstacles mobiles
         */
        for(ObstacleProximity obstacleMobile : mobileEnnemies) {
            Circle obstaclecircle=new Circle(obstacleMobile.getPosition(),obstacleMobile.getRadius()+d);
            ArrayList<Vec2> lmobile = obstaclecircle.pointsaroundcircle(10);
            points.addAll(lmobile);
        }

        /*
        On vérifie pour chaque point s'il n'y a pas d'intersection avec les obstacles circulaires
         */
        ArrayList<Vec2> finalPointsToReturn = new ArrayList<>();
        for(Vec2 point : points){
            boolean mustBeRemoved=false;
            for(ObstacleRectangular obstacleRectangular : listRectangu){
                if(table.getObstacleManager().isPositionInObstacle(point,obstacleRectangular)){
                    mustBeRemoved=true;
                    break;
                }
            }
            if (!mustBeRemoved) {
                for (ObstacleCircular obstacleCircular : listCircu) {
                    if (table.getObstacleManager().isPositionInObstacle(point, obstacleCircular)) {
                        mustBeRemoved=true;
                        break;
                    }
                }
            }
            if (!mustBeRemoved) {
                for (ObstacleProximity obstacleMobile : mobileEnnemies) {
                    if (table.getObstacleManager().isPositionInObstacle(point,obstacleMobile)) {
                        mustBeRemoved=true;
                        break;
                    }
                }
            }
            if (!mustBeRemoved) {
                if (!(table.getObstacleManager().isRobotInTable(point))) {
                    mustBeRemoved=true;
                }
            }
            if (!mustBeRemoved){
                finalPointsToReturn.add(point);
            }
        }
        for(Vec2 coords : finalPointsToReturn){
            nodes.add(new Noeud(coords,0,0,new ArrayList<>()));
        }
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

    public void createGraphe(){
        this.listCircu = table.getObstacleManager().getmCircularObstacle();
        this.listRectangu = table.getObstacleManager().getRectangles();
        ArrayList<ObstacleProximity> tempMobileEnnemies = table.getObstacleManager().getMobileObstacles();
        this.mobileEnnemies.clear();
        for (ObstacleProximity obstacleProximity : tempMobileEnnemies){
            log.debug("/////////////////////////////");
            log.debug(obstacleProximity.getCircle().getCenter());
            this.mobileEnnemies.add(new ObstacleProximity(
                    new Circle(obstacleProximity.getCircle().getCenter(),obstacleProximity.getCircle().getRadius()),10000));
        }
        createNodes();
        createAretes();
    }

    public ArrayList<Noeud> getNodes() {
        return nodes;
    }

    public ArrayList<Arete> getBoneslist() {
        return bonesList;
    }

    public ArrayList<ObstacleProximity> getMobileEnnemies() {
        return mobileEnnemies;
    }
}





