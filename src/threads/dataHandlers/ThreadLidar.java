package threads.dataHandlers;

import container.Service;
import enums.ConfigInfoRobot;
import pathfinder.Graphe;
import pfg.config.Config;
import robot.EthWrapper;
import smartMath.Circle;
import smartMath.Vec2;
import smartMath.XYO;
import table.Table;
import table.obstacles.ObstacleProximity;
import threads.AbstractThread;
import utils.Log;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Thread qui récupère les informations du Lidar et les traite en mettant à jour le graphe
 *
 * @author rem
 */
public class ThreadLidar extends AbstractThread implements Service {

    /** Log & Config */
    private Log log;
    private Config config;

    /** Graphe à modifier en fonction des données recues par le Lidar */
    private Graphe graph;

    /** Table sur laquelle on va ajouter les obstacles */
    private Table table;

    /** Sert à connaître la position du robot */
    private EthWrapper ethWrapper;

    /** Gestion de la réception des données du Lidar */
    private ServerSocket server;
    private Socket client;
    private BufferedReader input;

    /** La symetrie ... */
    private boolean symetry;
    private int ennemyRadius;

    /** Fichiers & Buffers de debug */
    private File lidarData;
    private File lidarDataTmp;
    private BufferedWriter out;

    /** Constructeur */
    private ThreadLidar(Log log, Config config, Graphe graph, Table table, EthWrapper ethWrapper) {
        this.log = log;
        this.config = config;
        this.graph = graph;
        this.table = table;
        this.ethWrapper = ethWrapper;

        try {
            this.lidarData = new File("./lidar.txt");
            this.lidarDataTmp = new File("/tmp/lidar.txt");

            if (!lidarData.exists()) {
                this.lidarData.createNewFile();
            }
            if (!lidarDataTmp.exists()) {
                this.lidarDataTmp.createNewFile();
            }

            out = new BufferedWriter(new FileWriter(this.lidarData));
            out.write("Données du Lidar : de la donnée du script python jusqu'à son traitement !");
            out.newLine();
            out.flush();

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /** Initialisation de la connexion */
    private void initSocket() {
        try {
            server = new ServerSocket(15550);
            server.setReuseAddress(true);
            client = server.accept();
            input = new BufferedReader(new InputStreamReader(client.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            log.critical("Initialisation de la connexion Lidar failed");
        }
    }

    /** Méthode appelée à la fin du Thread */
    private void shutdown() {
        try {
            input.close();
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Méthode de changement de référentiel (robot -> table) */
    private Vec2 changeRef(Vec2 vec) {
        XYO robotPosOr = ethWrapper.getCurrentPositionAndOrientation().clone();
        if (symetry) {
            robotPosOr.symetrize();
        }
        vec.setA(vec.getA() + robotPosOr.getOrientation());
        vec.plus(robotPosOr.getPosition());
        return vec;
    }

    @Override
    public void run() {
        String buffer;
        String bufferList[];
        Thread.currentThread().setPriority(8);
        initSocket();
        log.debug("ThreadLidar started");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> shutdown()));

        while (true) {
            try {
                buffer = input.readLine();
                out.write(buffer);
                out.newLine();
                out.flush();

                bufferList = buffer.split(";");

                // Mise à jour de la table
                for (String info : bufferList) {
                    info = info.substring(1,  info.length()-1);
                    Vec2 pos = new Vec2(Double.parseDouble(info.split(",")[0]), Double.parseDouble(info.split(",")[1]));
                    if (symetry) {
                        pos.symetrize();
                    }
                    out.write("Position calculée dans le référentiel du robot : " + pos.toStringEth());
                    out.newLine();
                    out.flush();

                    pos = this.changeRef(pos);
                    out.write("Position caluclée dans le référentiel de la table : " + pos.toStringEth());
                    out.newLine();
                    out.newLine();
                    out.flush();

                    table.getObstacleManager().addObstacle(pos, ennemyRadius);
                }

                // Mise à jour du graphe
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void updateConfig() {
        symetry = (config.getString(ConfigInfoRobot.COULEUR).equals("orange"));
        ennemyRadius = config.getInt(ConfigInfoRobot.ENNEMY_RADIUS);
    }

    /** Getters & Setters */
    public Graphe getGraph() {
        return graph;
    }
}
