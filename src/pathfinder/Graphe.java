package pathfinder;

import container.Service;
import enums.ConfigInfoRobot;
import enums.TasCubes;
import pfg.config.Config;
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
    private ArrayList<Node> nodes;

    /** Paramètres du graphe */
    private int espacementRect;
    private int nbNoeudObstCirc;
    private double espCoeff;

    /**
     * Constructeur du graphe, un graphe c'est des noeuds reliés par des arêtes, on utilise la méthode createNodes
     * et createAretes (Voir la documentation de ces méthodes pour plus de détails)
     */
    public Graphe(Log log, Config config, Table table) {
        this.log = log;
        this.config = config;
        this.table = table;
        this.listCircu = table.getObstacleManager().getmCircularObstacle();
        this.listRectangu = table.getObstacleManager().getRectangles();

        this.mobileEnnemies = new CopyOnWriteArrayList<>();
        this.nodes = new ArrayList<>();

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

        nodes.add(new Node(Table.entryPosition)); // 1252 455
        nodes.add(new Node(new Vec2(0, 1200)));
        nodes.add(new Node(new Vec2(0, 900)));
        nodes.add(new Node(new Vec2(0, 600)));
        nodes.add(new Node(new Vec2(0, 300)));
        nodes.add(new Node(new Vec2(650, 215)));

        int xCentreGravite=(TasCubes.TAS_BASE.getCoordsVec2().getX()+TasCubes.TAS_CHATEAU_EAU.getCoordsVec2().getX()+TasCubes.TAS_STATION_EPURATION.getCoordsVec2().getX())/3;
        int yCentreGravite=(TasCubes.TAS_BASE.getCoordsVec2().getY()+TasCubes.TAS_CHATEAU_EAU.getCoordsVec2().getY()+TasCubes.TAS_STATION_EPURATION.getCoordsVec2().getY())/3;
        nodes.add(new Node(new Vec2(xCentreGravite, yCentreGravite)));

        int xCentreGraviteEnnemy=(TasCubes.TAS_BASE_ENNEMI.getCoordsVec2().getX()+TasCubes.TAS_CHATEAU_EAU_ENNEMI.getCoordsVec2().getX()+TasCubes.TAS_STATION_EPURATION_ENNEMI.getCoordsVec2().getX())/3;
        int yCentreGraviteEnnemy=(TasCubes.TAS_BASE_ENNEMI.getCoordsVec2().getY()+TasCubes.TAS_CHATEAU_EAU_ENNEMI.getCoordsVec2().getY()+TasCubes.TAS_STATION_EPURATION_ENNEMI.getCoordsVec2().getY())/3;
        nodes.add(new Node(new Vec2(xCentreGraviteEnnemy, yCentreGraviteEnnemy)));
    }

    /**
     * Initialise les arretes, ici les voisins des noeuds et le cout pour accéder à ce voisin
     */
    private void initRidges() {
        for (int i=0; i<nodes.size(); i++) {
            Node node1 = nodes.get(i);
            for (int j=i; j<nodes.size(); j++) {
                Node node2 = nodes.get(j);
                if (!table.getObstacleManager().intersectAnyObstacle(new Segment(node1.getPosition(), node2.getPosition()))){
                    node1.addVoisin(node2);
                    node2.addVoisin(node1);
                }
            }
        }
    }

    /**
     * Place des noeuds autour d'un obstacles circulaire, en vérifiant biensur si ce dernier n'est pas dans un autre
     * obstacle
     *
     * @param circle l'obstacle circulaire
     */
    private void placeNodes(ObstacleCircular circle){
        Vec2 center = circle.getPosition();
        for (int i = 0; i<nbNoeudObstCirc; i++) {
            Vec2 posNode = new Vec2(espCoeff *circle.getRadius(), i*2*Math.PI/nbNoeudObstCirc);
            posNode.plus(center);

            if(!table.getObstacleManager().isPositionInObstacle(posNode) && table.getObstacleManager().isRobotInTable(posNode)) {
                nodes.add(new Node(posNode));
            }
        }
    }

    /**
     * Place des noeuds autour d'un obstacle rectangulaire, en vérifiant qu'il n'empiete pas sur les autres obstacles
     *
     * @param rect l'obstacle rectangulaire
     */
    private void placeNodes(ObstacleRectangular rect) {
        Vec2 upLeft = rect.getPosition().plusNewVector(new Vec2(-espCoeff*rect.getSizeX()/2, espCoeff*rect.getSizeY()/2));
        Vec2 downLeft = rect.getPosition().plusNewVector(new Vec2(-espCoeff*rect.getSizeX()/2, -espCoeff*rect.getSizeY()/2));

        for (int i=0; i<rect.getSizeX(); i+=espacementRect) {
            Vec2 posNode1 = upLeft.plusNewVector(new Vec2(i, 0));
            Vec2 posNode2 = downLeft.plusNewVector(new Vec2(i, 0));

            if(!table.getObstacleManager().isPositionInObstacle(posNode1) && table.getObstacleManager().isRobotInTable(posNode1)) {
                nodes.add(new Node(posNode1));
            }

            if(!table.getObstacleManager().isPositionInObstacle(posNode2) && table.getObstacleManager().isRobotInTable(posNode2)) {
                nodes.add(new Node(posNode2));
            }
        }
    }

    /**
     * Méthode ajoutant un noeud au graphe. Cela consiste à remplir le champ de ses noeuds voisins.
     * Cette méthode est appelée par le pathfinding
     *
     * @param noeud le noeud à ajouter
     */
    public void addNode(Node noeud) {
        nodes.add(noeud);
        for (Node node : nodes) {
            if (!table.getObstacleManager().intersectAnyObstacle(new Segment(node.getPosition(), noeud.getPosition()))){
                node.addVoisin(noeud);
                noeud.addVoisin(node);
            }
        }
    }

    /**
     * Méthode supprimant un node dans le graphe, c'est-à-dire que tout ses voisins doivent également le retirer
     *
     * @param node le node à supprimer
     */
    public void removeNode(Node node){
        nodes.remove(node);
        for (Node neighbours : node.getVoisins().keySet()) {
            neighbours.getVoisins().remove(node);
        }
    }

    /**
     * Méthode réinitialisant le graphe, à appeler après chaque utilisation de findmyway
     */
    public void reInit() {
        for (Node node : nodes) {
            node.setPred(null);
            node.setCout(Node.DEFAULT_COST);
            node.setHeuristique(Node.DEFAULT_HEURISTIC);
        }
    }

    /**
     * Cette méthode retourne le noeud du graphe qui est le plus proche d'une position
     * Elle servira quand on sera bloqués dans un obstacle
     *
     * @param position
     * @return le noeud du graphe le plus proche
     */
    public Node closestNodeToPosition(Vec2 position){
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
     *
     * @param position
     * @return
     */
    public boolean isAlreadyANode(Vec2 position){
        ArrayList<Vec2> points=new ArrayList<>();
        for(Node node : nodes){
            points.add(node.getPosition());
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
    public ArrayList<Node> getNodes() {
        return nodes;
    }

    public CopyOnWriteArrayList<ObstacleCircular> getListCircu() {
        return listCircu;
    }

    public CopyOnWriteArrayList<ObstacleProximity> getMobileEnnemies() {
        return mobileEnnemies;
    }

}





