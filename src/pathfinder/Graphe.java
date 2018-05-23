package pathfinder;

import container.Service;
import enums.ConfigInfoRobot;
import enums.TasCubes;
import pfg.config.Config;
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
 * @author rem
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

    /** Le graphe ! */
    private ArrayList<Noeud> nodes;
    private ArrayList<Arete> bonesList;

    /** Paramètres du graphe */
    private int espacementRect;
    private double espCoeff;
    private int nbNoeudObstCirc;

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
        this.nodes = new ArrayList<>();
        bonesList = new ArrayList<>();

        updateConfig();

        long timeStep = System.currentTimeMillis();
        initNodes();
        initRidges();
        log.debug("Time to create graph : " + (System.currentTimeMillis() - timeStep) + " ms");
    }

    /**
     * Méthode générant des noeuds sur la table : on créer des noeuds autour des obstacles
     * (circulaires & rectanngulaires), ainsi que des noeuds fixes
     */

    private void initNodes() {
        for (ObstacleCircular circle : listCircu) {
            placeNodes(circle);
        }
        for (ObstacleRectangular rectangular : listRectangu) {
            placeNodes(rectangular);
        }

        nodes.add(new Noeud(Table.entryPosition)); // 1252 455
        nodes.add(new Noeud(new Vec2(0, 1200)));
        nodes.add(new Noeud(new Vec2(0, 900)));
        nodes.add(new Noeud(new Vec2(0, 600)));
        nodes.add(new Noeud(new Vec2(0, 300)));
        nodes.add(new Noeud(new Vec2(650, 215)));

        int xCentreGravite=(TasCubes.TAS_BASE.getCoordsVec2().getX()+TasCubes.TAS_CHATEAU_EAU.getCoordsVec2().getX()+TasCubes.TAS_STATION_EPURATION.getCoordsVec2().getX())/3;
        int yCentreGravite=(TasCubes.TAS_BASE.getCoordsVec2().getY()+TasCubes.TAS_CHATEAU_EAU.getCoordsVec2().getY()+TasCubes.TAS_STATION_EPURATION.getCoordsVec2().getY())/3;
        nodes.add(new Noeud(new Vec2(xCentreGravite, yCentreGravite)));

        int xCentreGraviteEnnemy=(TasCubes.TAS_BASE_ENNEMI.getCoordsVec2().getX()+TasCubes.TAS_CHATEAU_EAU_ENNEMI.getCoordsVec2().getX()+TasCubes.TAS_STATION_EPURATION_ENNEMI.getCoordsVec2().getX())/3;
        int yCentreGraviteEnnemy=(TasCubes.TAS_BASE_ENNEMI.getCoordsVec2().getY()+TasCubes.TAS_CHATEAU_EAU_ENNEMI.getCoordsVec2().getY()+TasCubes.TAS_STATION_EPURATION_ENNEMI.getCoordsVec2().getY())/3;
        nodes.add(new Noeud(new Vec2(xCentreGraviteEnnemy, yCentreGraviteEnnemy)));
    }

    /**
     * Initialise les arretes, ici les voisins des noeuds et le cout pour accéder à ce voisin
     */
    private void initRidges() {
        for (int i=0; i<nodes.size(); i++) {
            Noeud node1 = nodes.get(i);
            for (int j=i; j<nodes.size(); j++) {
                Noeud node2 = nodes.get(j);
                if (!table.getObstacleManager().intersectAnyObstacle(new Segment(node1.getPosition(), node2.getPosition()))){
                    node1.addVoisin(node2);
                    node2.addVoisin(node1);
                }
            }
        }
    }

    /**
     * Méthode ajoutant un noeud au graphe. Cela consiste à remplir le champ de ses noeuds voisins.
     * Cette méthode est appelée par le pathfinding
     */

    public void addNode(Noeud noeud) {

    }

    /**
     * Cette méthode supprime un noeud, un noeud a des voisins, ce noeud n'existe plus s'il est supprimé de la liste des voisins de ses voisins
     * @param noeud
     */

    public void removeNode(Noeud noeud){

    }

    /**
     * Place des noeuds autour d'un obstacles circulaire, en vérifiant biensur si ce dernier n'est pas dans un autre
     * obstacle
     */
    public void placeNodes(ObstacleCircular circle){
        Vec2 center = circle.getPosition();
        for (int i = 0; i<nbNoeudObstCirc; i++) {
            Vec2 posNode = new Vec2(espCoeff *circle.getRadius(), i*2*Math.PI/nbNoeudObstCirc);
            posNode.plus(center);

            if(!table.getObstacleManager().isPositionInObstacle(posNode) && table.getObstacleManager().isRobotInTable(posNode)) {
                nodes.add(new Noeud(posNode));
            }
        }
    }

    /**
     * Place des noeuds autour d'un obstacle rectangulaire, en vérifiant qu'il n'empiete pas sur les autres obstacles
     */
    public void placeNodes(ObstacleRectangular rect) {
        Vec2 upLeft = rect.getPosition().plusNewVector(new Vec2(-espCoeff*rect.getSizeX()/2, espCoeff*rect.getSizeY()/2));
        Vec2 downLeft = rect.getPosition().plusNewVector(new Vec2(-espCoeff*rect.getSizeX()/2, -espCoeff*rect.getSizeY()/2));

        for (int i=0; i<rect.getSizeX(); i+=espacementRect) {
            Vec2 posNode1 = upLeft.plusNewVector(new Vec2(i, 0));
            Vec2 posNode2 = downLeft.plusNewVector(new Vec2(i, 0));

            if(!table.getObstacleManager().isPositionInObstacle(posNode1) && table.getObstacleManager().isRobotInTable(posNode1)) {
                nodes.add(new Noeud(posNode1));
            }

            if(!table.getObstacleManager().isPositionInObstacle(posNode2) && table.getObstacleManager().isRobotInTable(posNode2)) {
                nodes.add(new Noeud(posNode2));
            }
        }
    }

    /**
     * Méthode réinitialisant le graphe, à appeler après chaque utilisation de findmyway
     */

    public void reInit(Noeud noeudDepart, Noeud noeudArrive) {
        for (Noeud node :this.getNodes()) {
            node.setPred(null);
            node.setCout(Noeud.DEFAULT_COST);
            node.setHeuristique(Noeud.DEFAULT_HEURISTIC);
        }
        this.removeNode(noeudDepart);
        this.removeNode(noeudArrive);
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

    @Override
    public void updateConfig() {
        espacementRect = config.getInt(ConfigInfoRobot.ESPACEMENT_RECT);
        espCoeff = config.getDouble(ConfigInfoRobot.ESPACEMENT_OBSTACLE_COEFF);
        nbNoeudObstCirc = config.getInt(ConfigInfoRobot.NB_NOEUDS_OBST_CIR);
    }

    /** Getters & Setters */
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





