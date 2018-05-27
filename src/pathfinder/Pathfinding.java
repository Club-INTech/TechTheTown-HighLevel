package pathfinder;

import container.Service;
import enums.ConfigInfoRobot;
import exceptions.Locomotion.PointInObstacleException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.NoPathFound;
import pfg.config.Config;
import robot.Locomotion;
import smartMath.Vec2;
import strategie.IA.Graph;
import table.Table;
import threads.dataHandlers.ThreadLidar;
import utils.Log;

import java.util.ArrayList;
import java.util.PriorityQueue;

/**
 * Pathfinding du robot ! Contient l'algorithme
 *
 * @author Yousra, Sam, rem
 */

public class Pathfinding implements Service {

    /** Log & config */
    private Log log;
    private Config config;

    /** Graphe & Table */
    private ThreadLidar grapheHandler;
    private Graphe graphe;
    private Table table;

    /** Le pathfinding gère aussi la trajectoire via un autre Thread */
    private Locomotion locomotion;

    /** Vitesses du robot */
    private int robot_linear_speed;
    private double robot_angular_speed;

    /** Liste ouverte & fermée */
    private PriorityQueue<Node> openList;
    private ArrayList<Node> closedList;

    /** Noeud de départ et d'arrivé à mettre à jour */
    private Node beginNode;
    private Node aimNode;

    /** Constructeur */
    public Pathfinding(Log log, Config config, Locomotion locomotion, ThreadLidar graphHandler, Table table) {
        this.log = log;
        this.config = config;
        this.locomotion = locomotion;
        this.grapheHandler = graphHandler;
        this.graphe = graphHandler.getGraph();
        this.table = table;
        this.openList = new PriorityQueue<>(new BetterNode());
        this.closedList = new ArrayList<>();
        updateConfig();
    }

    /**
     * Methode surcouche du pathfinding permettant de gérer les obstacles mouvants (LPA*)
     *
     * @param aim le point à atteindre
     */
    public void moveTo(Vec2 aim) throws PointInObstacleException, NoPathFound{
        /* Algo :
        initialisation du Graphe, c-à-d : ajout des noeuds de départ et d'arrivé & calcul d(heuristique
        Tant qu'on est pas arrivé :
            Calcul du plus court chemin
            Lancement du thread de suivit de trajectoire
            Attente de changements du graphe ou d'un evenement du gestionnaire de trajectoire, c-a-d obstacle sur la ligne droite suivit par le robot
            Mise à jour du graphe en cas d'evenement du gestionnaire de trajectoire, c-a-d retrait du noeud de départ initial et ajout d'un nouveau noeud de départ
         */
        try {
            init(aim);
            locomotion.followPath(findmyway(aimNode));
            clean();
        } catch (UnableToMoveException e) {
            e.printStackTrace();
        }
    }

    /**
     * Methode basée sur l'algorithme A* renvoyant une liste de vecteurs qui contient le chemin le plus rapide
     * entre les deux positions d'entrée.
     *
     * @param aim la position visée
     * @return un bon chemin jusqu'au point visé
     */
    public ArrayList<Vec2> findmyway(Node aim) throws NoPathFound {

        int currentCost;
        synchronized (graphe.lock) {
            // Algorithme en lui-même
            while (!openList.isEmpty()) {
                Node visited = openList.poll();

                if (visited.equals(aim)) {
                    return reconstructPath();
                }

                for (Node neighbour : visited.getNeighbours().keySet()) {
                    Ridge ridge = visited.getNeighbours().get(neighbour);
                    if (ridge.isReachable()) {
                        currentCost = visited.getCout() + ridge.getCost();
                        // Si l'on a déjà évalué ce noeud et que visited est un meilleur prédecesseur, on l'update !
                        if ((openList.contains(neighbour) || closedList.contains(neighbour)) && currentCost < neighbour.getCout()) {
                            neighbour.setCout(currentCost);
                            neighbour.setPred(visited);
                            if (closedList.contains(neighbour)) {
                                closedList.remove(neighbour);
                                openList.add(neighbour);
                            }
                        }
                        // Si c'est un noeud que l'on a jamais visité, on le visite !
                        else if (!(openList.contains(neighbour) || closedList.contains(neighbour))) {
                            neighbour.setCout(currentCost);
                            neighbour.setPred(visited);
                            openList.add(neighbour);
                        }
                    }
                }
                closedList.add(visited);
            }

            throw new NoPathFound(aim.getPosition());
        }
    }

    /** Permet de calculer le temps pour se rendre à une position. */
    public double howManyTime(Vec2 positionDepart, Vec2 positionArrive) throws NoPathFound, PointInObstacleException {
        // TODO à adapter
        /* ArrayList<Vec2> path = findmyway(positionDepart,positionArrive);
        double time = 0;
        for(int i = 0; i < path.size() - 1; i++){
            time += robot_angular_speed*path.get(i).minusNewVector(path.get(i+1)).angle();
            time += robot_linear_speed*path.get(i).minusNewVector(path.get(i+1)).length();
        }
        return time;
        */
        return -1;
    }

    /**
     * Initialisation du Pathfinding
     * @param aim position visée
     */
    private void init(Vec2 aim) throws PointInObstacleException {
        if (!table.getObstacleManager().isRobotInTable(locomotion.getPosition()) || table.getObstacleManager().isPositionInObstacle(locomotion.getPosition())) {
            throw new PointInObstacleException("Position de départ dans un obstacle ", locomotion.getPosition());
        }
        if (!table.getObstacleManager().isRobotInTable(aim) || table.getObstacleManager().isPositionInObstacle(aim)) {
            throw new PointInObstacleException("Position visée dans un obstacle ", aim);
        }
        beginNode = new Node(locomotion.getPosition());
        aimNode = new Node(aim);

        synchronized (graphe.lock) {
            graphe.addNode(beginNode);
            graphe.addNode(aimNode);
            graphe.reInit();

            BetterNode.setAim(aim);
            beginNode.setCout(0);
            openList.add(beginNode);
        }
    }

    /**
     * Nettoyage du graphe
     */
    private void clean() {
        synchronized (graphe.lock) {
            graphe.removeNode(aimNode);
            graphe.removeNode(beginNode);
            graphe.reInit();
        }
    }

    /**
     * Reconstruit le chemin à partir des prédecesseurs de chaque noeud
     * @return le chemin trouvé par le Pathfinding
     */
    private ArrayList<Vec2> reconstructPath() {
        ArrayList<Vec2> toReturn = new ArrayList<>();
        Node visited = aimNode;
        toReturn.add(aimNode.getPosition());

        do {
            visited = visited.getPred();
            toReturn.add(0, visited.getPosition());
        } while (!(visited.equals(beginNode)));
        openList.clear();
        closedList.clear();
        return toReturn;
    }

    /** Getters & Setters */
    public Graphe getGraphe() {
        return grapheHandler.getGraph();
    }

    @Override
    public void updateConfig() {
        Node.setFixCost(config.getInt(ConfigInfoRobot.COUT_FIXE));
        this.robot_linear_speed = config.getInt(ConfigInfoRobot.ROBOT_LINEAR_SPEED);
        this.robot_angular_speed = config.getDouble(ConfigInfoRobot.ROBOT_ANGULAR_SPEED);
    }
}
