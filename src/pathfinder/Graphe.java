package pathfinder;

import container.Service;
import enums.ConfigInfoRobot;
import enums.TasCubes;
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

/**
 * Classe paramétrant la table en noeuds et arrête permettant d'y naviguer via un algorithme de pathfinding
 *
 * @author ?
 */
public class Graphe implements Service {

    /** Config & Log */
    private Log log;
    private Config config;

    /** La table... */
    private Table table;
    private CopyOnWriteArrayList<ObstacleCircular> listCircu;
    private CopyOnWriteArrayList<ObstacleRectangular> listRectangu;
    private CopyOnWriteArrayList<ObstacleProximity> mobileEnnemies;

    private ArrayList<Noeud> nodes;
    private ArrayList<Arete> bonesList;
    private boolean basicDetection;

    @Override
    public void updateConfig() {
        int r=config.getInt(ConfigInfoRobot.ROBOT_RADIUS);
        this.basicDetection=config.getBoolean(ConfigInfoRobot.BASIC_DETECTION);
    }


    /**
     * Constructeur du graphe, un graphe c'est des noeuds reliés par des arêtes,
     * on utilise la méthode createNodes et createAretes (Voir la documentation de
     * ces méthodes pour plus de détails)
     */

    public Graphe(Log log, Config config, Table table) {
        this.log = log;
        this.config = config;
        this.table = table;
        this.listCircu = table.getObstacleManager().getmCircularObstacle();
        this.listRectangu = table.getObstacleManager().getRectangles();
        this.mobileEnnemies = new CopyOnWriteArrayList<>();

        createNodes();
        createAretes();
    }

    /** Méthode générant des noeuds sur la table : on crée des noeuds autour
     * des obstacles circulaires (Méthode nodesaroundobstacles) vu que ce sont ces obstacles-ci qu'on devrait
     * esquiver, on rajoute trois autres noeuds afin de choisir les meilleurs chemins
     * pour les actions qu'on veut faire, à savoir l'interrupteur*/

    public void createNodes() {

        this.nodes = new ArrayList<>();
        this.createNodesAroundCircularObstacles();

        nodes.add(new Noeud(new Vec2(0, 1000)));
        nodes.add(new Noeud(Table.entryPosition)); // 1252 455
        nodes.add(new Noeud(new Vec2(650, 215)));

        int xCentreGravite=(TasCubes.TAS_BASE.getCoordsVec2().getX()+TasCubes.TAS_CHATEAU_EAU.getCoordsVec2().getX()+TasCubes.TAS_STATION_EPURATION.getCoordsVec2().getX())/3;
        int yCentreGravite=(TasCubes.TAS_BASE.getCoordsVec2().getY()+TasCubes.TAS_CHATEAU_EAU.getCoordsVec2().getY()+TasCubes.TAS_STATION_EPURATION.getCoordsVec2().getY())/3;
        nodes.add(new Noeud(new Vec2(xCentreGravite, yCentreGravite)));

        int xCentreGraviteEnnemy=(TasCubes.TAS_BASE_ENNEMI.getCoordsVec2().getX()+TasCubes.TAS_CHATEAU_EAU_ENNEMI.getCoordsVec2().getX()+TasCubes.TAS_STATION_EPURATION_ENNEMI.getCoordsVec2().getX())/3;
        int yCentreGraviteEnnemy=(TasCubes.TAS_BASE_ENNEMI.getCoordsVec2().getY()+TasCubes.TAS_CHATEAU_EAU_ENNEMI.getCoordsVec2().getY()+TasCubes.TAS_STATION_EPURATION_ENNEMI.getCoordsVec2().getY())/3;
        nodes.add(new Noeud(new Vec2(xCentreGravite, yCentreGravite)));
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
        ArrayList<Vec2> pointsFixes=new ArrayList<>();
        ArrayList<Vec2> pointsMobiles=new ArrayList<>();
        int d=30;//distance qu'on ajoute pour que les noeuds ne soient pas dans les obstacles
        /*
        on crée des noeuds autour des obstacles circulaires
         */
        for(ObstacleCircular obstacleCircular : listCircu) {
            Circle obstaclecircle=new Circle(obstacleCircular.getPosition(),obstacleCircular.getRadius()+d);
            ArrayList<Vec2> lcirculaire = obstaclecircle.pointsaroundcircle(10);
            pointsFixes.addAll(lcirculaire);
            points.addAll(lcirculaire);
        }
        /*
          On crée des noeuds autour des obstacles mobiles
         */
        if(!basicDetection) {
            for (ObstacleProximity obstacleMobile : mobileEnnemies) {
                Circle obstaclecircle = new Circle(obstacleMobile.getPosition(), obstacleMobile.getRadius() + d);
                ArrayList<Vec2> lmobile = obstaclecircle.pointsaroundcircle(10);
                pointsMobiles.addAll(lmobile);
                points.addAll(lmobile);
            }
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
            nodes.add(new Noeud(coords));
        }
        //C'est pour la méthode qui permet de get les noeuds les plus proches
        ArrayList<Vec2> finalPointsFixesToReturn = new ArrayList<>();
        for(Vec2 pointFixe : pointsFixes){
            boolean mustBeRemoved=false;
            for(ObstacleRectangular obstacleRectangular : listRectangu){
                if(table.getObstacleManager().isPositionInObstacle(pointFixe,obstacleRectangular)){
                    mustBeRemoved=true;
                    break;
                }
            }
            if (!mustBeRemoved) {
                for (ObstacleCircular obstacleCircular : listCircu) {
                    if (table.getObstacleManager().isPositionInObstacle(pointFixe, obstacleCircular)) {
                        mustBeRemoved=true;
                        break;
                    }
                }
            }
            if (!mustBeRemoved) {
                if (!(table.getObstacleManager().isRobotInTable(pointFixe))) {
                    mustBeRemoved=true;
                }
            }
            if (!mustBeRemoved){
                finalPointsFixesToReturn.add(pointFixe);
            }
        }
        for(Vec2 coords : finalPointsFixesToReturn){
            nodes.add(new Noeud(coords));
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
        CopyOnWriteArrayList<ObstacleProximity> tempMobileEnnemies = table.getObstacleManager().getMobileObstacles();
        this.mobileEnnemies.clear();
        for (ObstacleProximity obstacleProximity : tempMobileEnnemies){
            log.warning("Ajout d'un obstacle mobile en : "+obstacleProximity.getCircle().getCenter());
            this.mobileEnnemies.add(new ObstacleProximity(
                    new Circle(obstacleProximity.getCircle().getCenter(),obstacleProximity.getCircle().getRadius()),10000));
        }
        createNodes();
        createAretes();
    }

    /**
     * Cette méthode retourne le noeud du graphe qui est le plus proche d'une position
     * Elle servira quand on sera bloqués dans un obstacle
     * @param position
     * @return le noeud du graphe le plus proche
     */

    public Noeud closestNodeToPosition(Vec2 position){
        float distanceMin=nodes.get(0).getPosition().distance(position);
        int iMin=0;
        for(int i=1; i<nodes.size();i++){
            if(nodes.get(i).getPosition().distance(position)<distanceMin){
                distanceMin=nodes.get(i).getPosition().distance(position);
                iMin=i;
            }
        }
        return nodes.get(iMin);

    }

    /**
     * Cette méthode retourne true si le point indiqué correspond déjà à un noeud
     * @param position
     * @return
     */
    public boolean isAlreadyANode(Vec2 position){
        ArrayList<Vec2> points=new ArrayList<>();
        for(Noeud noeud : nodes){
            points.add(noeud.getPosition());
        }
        return points.contains(position);
    }


    public ArrayList<Noeud> getNodes() {
        return nodes;
    }

    public ArrayList<Arete> getBoneslist() {
        return bonesList;
    }

    public CopyOnWriteArrayList<ObstacleCircular> getListCircu() {
        return listCircu;
    }

    public CopyOnWriteArrayList<ObstacleProximity> getMobileEnnemies() {
        return mobileEnnemies;
    }

}





