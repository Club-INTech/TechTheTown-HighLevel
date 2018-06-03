package pathfinder;

import container.Service;
import enums.ConfigInfoRobot;
import enums.UnableToMoveReason;
import exceptions.Locomotion.PointInObstacleException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.NoPathFound;
import pfg.config.Config;
import robot.Locomotion;
import smartMath.Vec2;
import strategie.IA.Graph;
import table.Table;
import table.obstacles.Obstacle;
import table.obstacles.ObstacleProximity;
import threads.ThreadPathFollower;
import threads.dataHandlers.ThreadLidar;
import utils.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

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
    private int loopDelay;
    private int distanceToDisengage;

    /** Vitesses du robot */
    private int robot_linear_speed;
    private double robot_angular_speed;

    /** Liste ouverte & fermée */
    private PriorityQueue<Node> openList;
    private ArrayList<Node> closedList;
    private Path path;
    private ConcurrentLinkedQueue<Object> eventQueue;

    /** Noeud de départ et d'arrivé à mettre à jour */
    private Node beginNode;
    private Node aimNode;
    private boolean removeStart;
    private boolean removeAim;

    /** Debug */
    private BufferedWriter out;
    private File pdd;

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
        this.eventQueue = new ConcurrentLinkedQueue<>();
        this.path = new Path(new ConcurrentLinkedQueue());

        try {
            this.pdd = new File("pathfinding.txt");

            if (!pdd.exists()) {
                this.pdd.createNewFile();
            }

            out = new BufferedWriter(new FileWriter(this.pdd));

            out.write("======== Calculs de Chemins par le Pathfinding ========\n");
            out.flush();

        } catch (IOException e){
            e.printStackTrace();
        }
        updateConfig();
    }

    /**
     * Methode surcouche du pathfinding permettant de gérer les obstacles mouvants (LPA*)
     *
     * @param aim le point à atteindre
     */
    public void moveTo(Vec2 aim) throws PointInObstacleException, NoPathFound, UnableToMoveException {
        /* Algo :
        initialisation du Graphe, c-à-d : ajout des noeuds de départ et d'arrivé & calcul d(heuristique
        Premier calcul du plus court chemin
        Lancement du Thread de suivit de trajectoire
        Tant qu'on est pas arrivé :
            Calcul du plus court chemin
            Attente de changements du graphe ou d'un evenement du gestionnaire de trajectoire, c-a-d obstacle sur la ligne droite suivit par le robot
            Mise à jour du graphe en cas d'evenement du gestionnaire de trajectoire, c-a-d retrait du noeud de départ initial et ajout d'un nouveau noeud de départ
         */
        Node next;
        boolean follow = true;
        int counter = 0;
        // TODO Gérer le cas ou les positions de départ et d'arrivé sont déjà des noeuds

        init(aim);
        findmyway(beginNode, aimNode);
        path.getPath().poll();
        (new ThreadPathFollower(log, config, path, eventQueue, locomotion)).start();

        // Tant que l'on a pas démarrer, on attend
        while (!locomotion.getThEvent().isMoving) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        long timeStep = System.currentTimeMillis();

        // On recalcule le chemin tant qu'on est pas immobile et proche de l'arrivé
        while (follow) {
            // Si le graphe a été mis à jour (s'il ne l'a pas été, on ne recalcule pas de chemin...)
            if (graphe.isUpdated()) {
                try {
                    synchronized (path.lock) {
                        counter++;
                        graphe.setUpdated(false);
                        next = graphe.findNode(path.getPath().peek());
                        out.write("Actual Path : " + path.getPath() + "Next : " + next + "\n");
                        out.flush();

                    /*
                    Si la position visée ou le nouveau point de départ du pathfinding est temporairement obstrué(e), on ne recalcule
                    pas le chemin, sous peine de générer une NoPathFound Exception :
                    si le point de départ est obstrué, le robot va s'arréter et lancer une nouvelle recherche de chemin (voir plus bas)
                    si le point d'arrivé est obstrué, on espère qu'il ne le sera plus d'ici à ce qu'il y arrive... Si ce n'est pas le cas,
                    une NoPathFoundException est générée
                    */
                        if (next != null &&
                                !table.getObstacleManager().isPositionInEnnemy(next.getPosition()) &&
                                !table.getObstacleManager().isPositionInEnnemy(aimNode.getPosition()) &&
                                !next.equals(aimNode)) {
                            graphe.reInit();
                            next.setCout(0);
                            openList.add(next);
                            findmyway(next, aimNode);
                            try {
                                out.write("Counter : " + counter + ", Chemin trouvé : " + path.getPath() + "\n");
                                out.flush();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    out.write("\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                // Si l'on a recu un message du ThreadPathFollower
                if (eventQueue.peek() != null) {
                    Object event = eventQueue.poll();
                    if (event instanceof UnableToMoveException) {
                        if (((UnableToMoveException) event).getReason().equals(UnableToMoveReason.OBSTACLE_DETECTED)) {
                            if (table.getObstacleManager().isPositionInEnnemy(locomotion.getPosition())) {
                                Vec2 vec = table.getObstacleManager().getClosestEnnemy(locomotion.getPosition()).getPosition().minusNewVector(locomotion.getPosition());
                                int signe;
                                if (vec.dot(new Vec2(100.0, locomotion.getOrientation())) > 0) {
                                    signe = -1;
                                } else {
                                    signe = 1;
                                }
                                locomotion.moveLengthwise(distanceToDisengage*signe, false, false);
                            }
                            clean();

                            // Si on galère trop, on s'arrête
                            if (System.currentTimeMillis() - timeStep > 5000) {
                                throw new NoPathFound(aim);
                            }

                            Thread.sleep(500);
                            synchronized (graphe.lock) {
                                init(aim);
                                findmyway(beginNode, aimNode);
                                path.getPath().poll();
                            }
                            (new ThreadPathFollower(log, config, path, eventQueue, locomotion)).start();
                        } else if (((UnableToMoveException) event).getReason().equals(UnableToMoveReason.PHYSICALLY_BLOCKED)) {
                            throw ((UnableToMoveException) event);
                        }
                    } else if (event instanceof Boolean) {
                        if((Boolean) event) {
                            log.debug("Fin de suivit du chemin");
                            follow = false;
                        }
                    }
                }

                Thread.sleep(200);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        clean();
    }

    /**
     * Methode basée sur l'algorithme A* renvoyant une liste de vecteurs qui contient le chemin le plus rapide
     * entre les deux positions d'entrée.
     *
     * @param begin la position de départ
     * @param aim la position visée
     * @return un bon chemin jusqu'au point visé
     */
    private void findmyway(Node begin, Node aim) throws NoPathFound {

        int currentCost;
        Set<Node> neighbours;
        int reachableRidges = 0;
        int totalRidges = 0;

        synchronized (graphe.lock) {
            // Algorithme en lui-même
            int conter = 0;
            while (!openList.isEmpty()) {
                conter++;
                Node visited = openList.poll();

                if (visited.equals(aim)) {
                    try {
                        out.write("Nombre de ridge OK : " + reachableRidges + "\n");
                        out.write("Nombre de ridge totale parcourue " + totalRidges + "\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    reconstructPath(begin);
                    return;
                }

                neighbours = visited.getNeighbours().keySet();

                for (Node neighbour : neighbours) {
                    Ridge ridge = visited.getNeighbours().get(neighbour);
                    totalRidges++;
                    if (ridge.isReachable()) {
                        reachableRidges++;
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

            throw new NoPathFound(aim.getPosition(), "Aucun chemin trouvé entre " + begin + " et " + aim + ",\nNombre de tours de boucles : " + conter);
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
        Vec2 begin = locomotion.getCurrentPosition().clone();
        if (!table.getObstacleManager().isRobotInTable(begin) || table.getObstacleManager().isPositionInObstacle(begin)) {
            throw new PointInObstacleException("Position de départ dans un obstacle ", begin);
        }
        if (!table.getObstacleManager().isRobotInTable(aim) || table.getObstacleManager().isPositionInObstacle(aim)) {
            throw new PointInObstacleException("Position visée dans un obstacle ", aim);
        }
        synchronized (graphe.lock) {
            removeAim = false;
            removeStart = false;
            beginNode = graphe.findNode(begin);
            aimNode = graphe.findNode(aim);

            if (beginNode == null) {
                beginNode = new Node (begin);
                graphe.addNode(beginNode);
                removeStart = true;
            }

            if (aimNode == null) {
                aimNode = new Node(aim);
                graphe.addNode(aimNode);
                removeAim = true;
            }

            eventQueue.clear();

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
            eventQueue.clear();

            if (removeStart) {
                graphe.removeNode(beginNode);
            }
            if (removeAim) {
                graphe.removeNode(aimNode);
            }
            graphe.reInit();
        }
    }

    /**
     * Reconstruit le chemin à partir des prédecesseurs de chaque noeud jusqu'au noeud de départ spécifié
     * @param beginNode noeud qui termine la reconstruction de chemin
     * @return le chemin trouvé par le Pathfinding
     */
    private void reconstructPath(Node beginNode) {
        ArrayList<Vec2> toAdd = new ArrayList<>();
        Node visited = aimNode;
        toAdd.add(aimNode.getPosition());

        do {
            visited = visited.getPred();
            toAdd.add(0, visited.getPosition());
        } while (!(visited.equals(beginNode)));
        path.getPath().clear();
        path.getPath().addAll(toAdd);
        openList.clear();
        closedList.clear();
    }

    /** Getters & Setters */
    public Graphe getGraphe() {
        return grapheHandler.getGraph();
    }
    public Path getPath() {
        return path;
    }

    @Override
    public void updateConfig() {
        this.robot_linear_speed = config.getInt(ConfigInfoRobot.ROBOT_LINEAR_SPEED);
        this.robot_angular_speed = config.getDouble(ConfigInfoRobot.ROBOT_ANGULAR_SPEED);
        this.loopDelay = config.getInt(ConfigInfoRobot.FEEDBACK_LOOPDELAY);
        this.distanceToDisengage = config.getInt(ConfigInfoRobot.DISTANCE_TO_DISENGAGE);
    }
}
