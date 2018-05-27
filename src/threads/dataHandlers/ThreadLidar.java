package threads.dataHandlers;

import container.Service;
import enums.ConfigInfoRobot;
import pathfinder.Graphe;
import pfg.config.Config;
import robot.EthWrapper;
import robot.Locomotion;
import smartMath.Vec2;
import smartMath.XYO;
import table.Table;
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
    private XYO highlevelXYO;

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
    private ThreadLidar(Log log, Config config, Graphe graph, Table table, Locomotion locomotion) {
        this.log = log;
        this.config = config;
        this.graph = graph;
        this.table = table;
        this.highlevelXYO = locomotion.getHighLevelXYO();

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
            out.write("========Données du Lidar : de la donnée du script python jusqu'à son traitement !========");
            out.newLine();
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
        vec.setA(vec.getA() + highlevelXYO.getOrientation());
        vec.plus(highlevelXYO.getPosition());
        return vec;
    }

    @Override
    public void run() {
        String buffer;
        String bufferList[];
        Thread.currentThread().setPriority(8);
        updateConfig();
        initSocket();
        log.debug("ThreadLidar started");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> shutdown()));
        long time = System.currentTimeMillis();
        long timeStep;
        Vec2 pos;

        while (true) {
            try {
                buffer = input.readLine();
                timeStep = System.currentTimeMillis();
                out.write(buffer);
                out.newLine();
                out.flush();

                bufferList = buffer.split(";");

                // Mise à jour de la table
                for (String info : bufferList) {
                    info = info.substring(1, info.length() - 1);
                    pos = new Vec2(Double.parseDouble(info.split(",")[0]), Double.parseDouble(info.split(",")[1]));

                    // Le référentiel des données Lidar repère les angles dans le sens horaire, c'est pourquoi l'on doit symetriser les vecteurs par rapport à x,
                    // Dans le cas de le symétrie l'on doit aussi symétriser (faire un dessin)
                    if(!symetry) {
                        pos.setA(-pos.getA());
                    }

                    out.write("[" + (System.currentTimeMillis() - time) / 1000 + "] Position calculée dans le référentiel du robot : " + pos.toStringEth());
                    out.newLine();
                    out.flush();

                    pos = this.changeRef(pos);

                    out.write("Position caluclée dans le référentiel de la table : " + pos.toStringEth());
                    out.newLine();
                    out.newLine();
                    out.flush();

                    table.getObstacleManager().addObstacle(pos, ennemyRadius);
                    table.getObstacleManager().removeOutdatedObstacles();
                }

                // Mise à jour du graphe
                synchronized (graph.lock) {
                    graph.updateRidges();
                }

                out.write("Durée total du traitement : " + (System.currentTimeMillis() - timeStep));
                out.newLine();
                out.flush();

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
