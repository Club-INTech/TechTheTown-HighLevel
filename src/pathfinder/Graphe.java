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
import java.util.TreeSet;

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
    private ArrayList<ObstacleCircular> listCircu;
    private ArrayList<ObstacleRectangular> listRectangu;
    private ArrayList<ObstacleProximity> mobileEnnemies;

    /** Le graphe ! */
    private ArrayList<Node> nodes;
    private ArrayList<Ridge> ridges;

    /** Paramètres du graphe */
    private int espacementRect;
    private int nbNoeudObstCirc;
    private double espCoeff;

    /** Lock */
    public final String lock = "GraphLock";
    private boolean updated = false;

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
        this.mobileEnnemies = table.getObstacleManager().getMobileObstacles();
        this.nodes = new ArrayList<>();
        this.ridges = new ArrayList<>();

        updateConfig();

        long timeStep = System.currentTimeMillis();
        init();
        log.debug("Time to create graph : " + (System.currentTimeMillis() - timeStep) + " ms");
        table.setGraph(this);
    }

    /**
     * Calcule la position des noeuds et leurs liens (arêtes)
     */
    public void init() {
        nodes.clear();
        initNodes();
        initRidges();
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
        placeNodes();

        nodes.add(new Node(Table.entryPosition.clone())); // 1252 455

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
        Segment seg;
        int n = 0;

        ridges.clear();
        for (int i = 0; i < nodes.size(); i++) {
            Node node1 = nodes.get(i);
            for (int j = i + 1; j < nodes.size(); j++) {
                Node node2 = nodes.get(j);
                seg = new Segment(node1.getPosition(), node2.getPosition());
                if (!table.getObstacleManager().intersectAnyObstacle(seg)) {
                    Ridge ridge = new Ridge(node1.getPosition().intDistance(node2.getPosition()), seg);
                    ridges.add(ridge);
                    node1.addNeighbour(node2, ridge);
                    node2.addNeighbour(node1, ridge);
                    n++;
                }
            }
        }
        log.debug("Nombre de noeuds : " + nodes.size() + ", Nombre moyen de voisins par noeud : " + (float)n/nodes.size());
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
     * Créer un quadriallage de noeuds sur la table, afin de pouvoir aisément esquiver l'adversaire
     */
    private void placeNodes() {
        Vec2 position;

        for (int line = 0; line < 2000/espacementRect; line++) {
            for (int i = -1500; i < 1500; i += espacementRect + 20) {
                if (line % 2 == 0) {
                    position = new Vec2(i, espacementRect * line + 20);
                } else {
                    position = new Vec2(i + espacementRect / 2 + 10, espacementRect * line + 20);
                }
                if (!table.getObstacleManager().isPositionInObstacle(position) && table.getObstacleManager().isRobotInTable(position)) {
                    nodes.add(new Node(position));
                }
            }
        }
    }

    /**
     * Méthode ajoutant un noeud au graphe. Cela consiste à remplir le champ de ses noeuds voisins.
     * Cette méthode est appelée par le pathfinding
     *
     * @param node le noeud à ajouter
     */
    public void addNode(Node node) {
        Segment seg;
        for (Node neighbour : nodes) {
            seg = new Segment(node.getPosition(), neighbour.getPosition());
            if (!table.getObstacleManager().intersectAnyObstacle(seg)) {
                Ridge ridge = new Ridge(node.getPosition().intDistance(neighbour.getPosition()), seg);
                ridges.add(ridge);
                node.addNeighbour(neighbour, ridge);
                neighbour.addNeighbour(node, ridge);
            }
        }
        nodes.add(node);
    }

    /**
     * Méthode supprimant un node dans le graphe, c'est-à-dire que tout ses voisins doivent également le retirer
     *
     * @param node le node à supprimer
     */
    public void removeNode(Node node){
        nodes.remove(node);
        for (Node neighbour : node.getNeighbours().keySet()) {
            ridges.remove(neighbour.getNeighbours().get(node));
            neighbour.getNeighbours().remove(node);
        }
    }

    /**
     * Méthode réinitialisant le graphe, à appeler après chaque utilisation de findmyway
     */
    public void reInit() {
        for (Node node : nodes) {
            node.setPred(null);
            node.setCout(Node.getDefaultCost());
            node.setHeuristique(Node.getDefaultHeuristic());
        }
    }

    /**
     * Méthode appelée par le Gestionnaire de graphe : elle permet de mettre à jour les arrêtes en indiquant
     * lesquelles sont encore franchissables
     */
    public void updateRidges() {
        int counter = 0;
        for (Ridge ridge: ridges) {
            ridge.setReachable(true);
            for (ObstacleProximity obstacleProximity : mobileEnnemies) {
                if (Geometry.intersects(ridge.getSeg(), obstacleProximity.getCircle())) {
                    ridge.setReachable(false);
                    counter++;
                    break;
                }
            }
        }
        log.debug("Nombre d'arrêtes plus accessibles : " + counter);
    }

    /**
     * Retourne le noeud dont la position est spécifiée
     * @param position position du noeud recherché
     * @return
     */
    public Node findNode(Vec2 position) {
        for (Node node : nodes) {
            if (node.getPosition().equals(position)) {
                return node;
            }
        }
        return null;
    }

    /**
     * Cette méthode retourne le noeud du graphe qui est le plus proche d'une position
     * Elle servira quand on sera bloqués dans un obstacle
     *
     * @param position
     * @return le noeud du graphe le plus proche
     */
    public Node closestNodeToPosition(Vec2 position) {
        Node best = nodes.get(0);
        float min = best.getPosition().distance(position);

        for (int i=1; i<nodes.size(); i++) {
            if (nodes.get(i).getPosition().distance(position) < min) {
                min = nodes.get(i).getPosition().distance(position);
                best = nodes.get(i);
            }
        }
        return best;
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
        Ridge.setStaticCost(config.getInt(ConfigInfoRobot.COUT_FIXE));
    }

    /** Getters & Setters */
    public ArrayList<Node> getNodes() {
        return nodes;
    }
    public synchronized boolean isUpdated() {
        return updated;
    }
    public synchronized void setUpdated(boolean updated) {
        this.updated = updated;
    }
    public ArrayList<Ridge> getRidges() {
        return ridges;
    }
}





