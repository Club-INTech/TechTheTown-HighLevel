/*
 * Copyright (c) 2016, INTech.
 *
 * This file is part of INTech's HighLevel.
 *
 *  INTech's HighLevel is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  INTech's HighLevel is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with it.  If not, see <http://www.gnu.org/licenses/>.
 */

package threads.dataHandlers;

import enums.ConfigInfoRobot;
import pfg.config.Config;
import robot.EthWrapper;
import sensor.Sensor;
import smartMath.Geometry;
import smartMath.Vec2;
import smartMath.XYO;
import strategie.GameState;
import table.Table;
import threads.AbstractThread;
import threads.ThreadTimer;
import utils.Log;

import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Thread qui ajoute en continu les obstacles détectés par les capteurs,
 * Et enleve ceux qui ont disparu, de meme que verifie les capteurs de contact
 * TODO Compléter la config, sinon NPE
 * @author pf, Krissprolls, discord
 */

public class ThreadSensor extends AbstractThread
{
    /** Array de sensor */
    public ArrayList<Sensor> sensorsArray = new ArrayList<Sensor>();
    private int nbSensors;

    /** La table */
    private Table mTable;

    /** La stm avec laquelle on doit communiquer */
    private EthWrapper ethWrapper;

    /** Buffer de valeurs */
    private ConcurrentLinkedQueue<String> valuesReceived;

    /** GameState */
    private GameState gameState;

    /** Si l'on doit symétriser */
    private boolean symetry;

    /*********************
     * INFOS DES SENSORS *
     *********************/

    /** Rayon de notre robot */
    private int ourRadius;

    /** Rayon du robot adverse */
    private int enRadius;

    /** Position du robot */
    private XYO robotPosAndOr;

    /** Distance maximale fiable pour les capteurs : au dela, valeurs abberentes
     * Override par la config */
    private int maxSensorRange;

    /** Distance minimale à laquelle on peut se fier aux capteurs : ne pas detecter notre propre root par exemple
     * Override par la config */
    private int minSensorRange;

    /** Incertitude sur la mesure*/
    private double uncertainty;

    /** Magic number pour quand on détecte avec 2 capteurs */
    private double magicNumber;

    /**
     *  Angle de visibilité qu'a le capteur
     * Override par la config
     *
     * Calcul de l'angle :
     *
     *   |angle/ \angle|
     * 	 |	  /	  \    |
     * 	 |	 /	   \   |
     * 	 |	/	    \  |
     * 	 |o/         \o|
     * 		  Robot			o : capteur
     *
     */
    private double detectionAngle;          //largeur de l'angle de détection
    private double sensorOrientationF;      //orientation des capteurs avant, par rapport à la ligne de visée avant du robot
    private double sensorOrientationB;      //orientation des capteurs arrière, par rapport à la ligne de visée AVANT du robot


    /*****************
     * INFOS & DEBUG *
     ****************/

    /** Delai d'attente avant de lancer le thread
     * Pour éviter de détecter la main du lanceur */
    private static boolean delay = true;

    private boolean usingJumper;

    private boolean advancedDetection;


    /** Valeurs des capteurs US {avant-gauche, avant-droit, arrière gauche, arrière-droit} */
    //ArrayList<Integer> USvalues = new ArrayList<Integer>(4);

    /** Valeurs de capteurs modifiées pour la suppression d'obstacle
     * Ainsi si l'un des capteurs nous indique 4km, c'est sûrement qu'il n'y a rien devant lui
     * On sépare ce qui sert à détecter de ce qui sert à ne pas détecter (oui c'est trop méta pour toi...)
     * PS : Si il indique 4 km, y'a un pb hein... */
    //ArrayList<Integer> USvaluesForDeletion = new ArrayList<>();

    /** Fichier de debug pour le placement d'obstacles */
    private final boolean debug = true;

    private Sensor sensorFL;//Front left
    private Sensor sensorFR;//Front right
    private Sensor sensorBL;//Back left
    private Sensor sensorBR;//Back right

    public Sensor getSensor(int id){
        if (id>this.sensorsArray.size()){
            log.critical("Sensor requested not registered (maxID:"+sensorsArray.size()+", id:"+id+")");
        }
        else if (id<0){
            log.critical("Sensor requested not existing (id<0)");
        }
        return this.sensorsArray.get(id);
    }

    /**
     * Crée un nouveau thread de capteurs
     * @param table La table a l'intérieure de laquelle le thread doit croire évoluer
     */
    public ThreadSensor (Config config, Log log, Table table, EthWrapper ethWrapper, ThreadEth eth, GameState gameState)
    {
        super(config, log);
        this.updateConfig();
        Thread.currentThread().setPriority(6);
        this.valuesReceived = eth.getUltrasoundBuffer();
        this.mTable = table;
        this.ethWrapper = ethWrapper;
        this.gameState = gameState;
        this.sensorFL=new Sensor(0,100,127,this.sensorOrientationF,this.detectionAngle,this.maxSensorRange,this.minSensorRange, this.uncertainty);
        this.sensorFR=new Sensor(1,100,-127,-this.sensorOrientationF,this.detectionAngle,this.maxSensorRange,this.minSensorRange, this.uncertainty);
        this.sensorBL=new Sensor(2,-100,127,this.sensorOrientationB-Math.PI,this.detectionAngle,this.maxSensorRange, this.minSensorRange, this.uncertainty);
        this.sensorBR=new Sensor(3,-100,-127,-this.sensorOrientationB+Math.PI,this.detectionAngle,this.maxSensorRange, this.minSensorRange, this.uncertainty);
        this.sensorsArray.add(sensorFL);
        this.sensorsArray.add(sensorFR);
        this.sensorsArray.add(sensorBL);
        this.sensorsArray.add(sensorBR);
        this.magicNumber=0.285;
        nbSensors = sensorsArray.size();
    }


    /** Ajoute les obstacles a l'obstacleManager */
    private void addObstacle() {

        /**Schéma du robot :
         *
         *           Front
         *
         *   \     /      \     /
         *    \   /        \   /
         *     \ /          \ /
         *      0------------1
         *      |            |                  x
         *      |    Robot   |                  /\
         *      |    poney   |                  |
         *      |            |                  |
         *      |            |           y <----0
         *      |            |
         *      2------------3
         *     / \          / \
         *    /   \        /   \
         *   /     \      /     \
         *
         *           Back
         */

        if (sensorFL.getDetectedDistance() != 0){
            if (sensorFR.getDetectedDistance() != 0) {
                //System.out.println("Detection:Sensor0And1 ");
                addFrontObstacleBoth();
            }
            else {
                //System.out.println("Detection:Sensor0 ");
                addFrontObstacleSingle(true);
            }
        }
        else if (sensorFR.getDetectedDistance() != 0){
            //System.out.println("Detection:Sensor1 ");
            addFrontObstacleSingle(false);
        }
        if (sensorBL.getDetectedDistance() != 0){
            if (sensorBR.getDetectedDistance() != 0){
                //System.out.println("Detection:Sensor2And3 ");
                addBackObstacleBoth();
            }
            else{
                //System.out.println("Detection:Sensor2 ");
                addBackObstacleSingle(true);
            }
        }
        else if (sensorBR.getDetectedDistance() != 0){
            //System.out.println("Detection:Sensor3 ");
            addBackObstacleSingle(false);
        }

    }

    /**
     *         \.</----: Point qu'on détecte, la distance entre 0 et
     *         \ /       le point c'est l, et entre 1 et le point c'est r
     *  \      \/        (on a donc 3 longueurs, on applique Al Kashi, l'angle d'intérêt
     *   \     /\   /      c'est cet angle trouvé par Al Kashi-PI/2)
     *    \   / \  /
     *     \ /  \ /
     *      0----1
     *        d : distance entre les capteurs 0 et 1
     *
     * */
    private void addFrontObstacleBoth() {
        int l = sensorFL.getDetectedDistance();
        int r = sensorFR.getDetectedDistance();
        //TODO facteur 0.8 à changer empiriquement
        int d = Math.abs(sensorFL.getY()) + Math.abs(sensorFR.getY());
        int a = (int) (l + enRadius * 0.8);
        int b = (int) (r + enRadius * 0.8);
        double toAcos = Math.abs(Geometry.moduloSpec((b * b - a * a - d * d) / (-2.0 * a * d), Math.PI));
        if (!(Math.abs(toAcos) <= 1)) {
            addFrontObstacleSingleMiddle();
        } else {
            double alpha = -Math.abs(Math.acos(toAcos) - Math.PI / 2);
            if (Math.abs(alpha) > detectionAngle) {
                addFrontObstacleSingleMiddle();
            } else {
                int x = (int) (a * Math.cos(alpha));
                int y = (int) (a * Math.sin(alpha));
                Vec2 posObjectFromSensorFL = new Vec2(x, y);
                Vec2 posObjectFromCenterRobot = posObjectFromSensorFL.plusNewVector(sensorFL.getVecteur());
                if (posObjectFromCenterRobot.getA() > -Math.PI / 3 && posObjectFromCenterRobot.getA() < Math.PI / 3) { // pour éviter les faux obstacles
                    mTable.getObstacleManager().addObstacle(this.changeRef(posObjectFromCenterRobot), enRadius + ourRadius + 10);
                }

            }
        }
    }
    /** Ajoute un obstacle derrière le robot, avec les deux capteurs ayant détecté quelque chose
     *        d:distance entre les capteurs 2 et 3
     *      2---3
     *     /\  /\    l : distance entre 2 et le point à détecter
     *    /  \/  \   r: distance entre 3 et le point à détecter
     *   /   /\   \   On a trois longueurs, on applique Al Kashi, l'angle d'intérêt
     *  /   /  \   \  c'est Pi/2-cet angle trouvé par Al Kashi
     * /   /    \   \
     *    /   .<-\--- : point à détecter
     *
     * */
    private void addBackObstacleBoth() {
        int l = sensorBL.getDetectedDistance();
        int r = sensorBR.getDetectedDistance();
        //TODO facteur 0.8 à changer empiriquement
        int d = Math.abs(sensorBL.getY()) + Math.abs(sensorBR.getY());
        int a = (int) (l + enRadius * 0.8);
        int b = (int) (r + enRadius * 0.8);
        double toAcos= Math.abs(Geometry.moduloSpec((b * b - a * a - d * d)/(-2.0 * a * d),Math.PI));
        //Si on ne fait pas cette condition, quand l'arcos est supérieur à 1, on a superposition entre le robot ennemi et le notre
        if (!(Math.abs(toAcos)<=1)){
            addBackObstacleSingleMiddle();
        }
        else {
            double alpha = Math.abs((3*Math.PI / 2-Math.acos(toAcos)));
            if(alpha>detectionAngle){
                addBackObstacleSingleMiddle();
            }
            else {
                int x = (int) (a * Math.cos(alpha));
                int y = (int) (a * Math.sin(alpha));
                Vec2 posObjectFromSensorBL = new Vec2(x, y);
                Vec2 posObjectFromCenterRobot = posObjectFromSensorBL.plusNewVector(sensorBL.getVecteur());
                if (posObjectFromCenterRobot.getA() > -Math.PI / 3 || posObjectFromCenterRobot.getA() < Math.PI / 3) { // pour éviter les faux obstacles
                    mTable.getObstacleManager().addObstacle(this.changeRef(posObjectFromCenterRobot), enRadius + ourRadius + 10);
                }
            }
        }
    }

    private void addBackObstacleSingleMiddle(){
        int l = sensorBL.getDetectedDistance();
        int r = sensorBR.getDetectedDistance();
        //TODO facteur 0.8 à changer empiriquement
        int a = (int) (l + enRadius * 0.5);
        int b = (int) (r + enRadius * 0.5);
        Vec2 posFromCenterRobot = new Vec2( (a + b)/2.0 + (Math.abs(sensorBR.getX())+Math.abs(sensorBL.getX()))/2.0 ,-Math.PI);
        mTable.getObstacleManager().addObstacle(this.changeRef(posFromCenterRobot), enRadius + ourRadius + 10);
    }

    private void addFrontObstacleSingleMiddle(){
        int l = sensorFL.getDetectedDistance();
        int r = sensorFR.getDetectedDistance();
        //TODO facteur 0.8 à changer empiriquement
        int a = (int) (l + enRadius * 0.5);
        int b = (int) (r + enRadius * 0.5);
        Vec2 posFromCenterRobot = new Vec2((a + b)/2.0 + (Math.abs(sensorFR.getX())+Math.abs(sensorFL.getX()))/2.0,0);
        mTable.getObstacleManager().addObstacle(this.changeRef(posFromCenterRobot), enRadius + ourRadius + 10);
    }


    /** Ajoute un obstacle d=sensorsArray.size()evant le robot, avec un seul capteur ayant détecté quelque chose
     * @param isLeft si c'est le capteur gauche */
    private void addFrontObstacleSingle(boolean isLeft)
    {
        // On modélise les arcs de cercle detecté par l'un des capteurs, puis on prend le point le plus à l'exterieur
        // Et on place le robot ennemi sur la ligne de détection maximale : la position calculée n'est pas la position réelle du robot adverse mais elle suffit

        Vec2 posObjectFromCenterRobot;
        if (isLeft){
            // On choisit le point à l'extrémité de l'arc à coté du capteur pour la position de l'ennemie: à courte distance, la position est réaliste,
            // à longue distance (>1m au vue des dimensions), l'ennemie est en réalité de l'autre coté
            double USFL = (double)sensorFL.getDetectedDistance();
            Vec2 posObjectFromSensorFL = new Vec2(USFL+enRadius*0.5, sensorFL.getSensorOrientation()); //sensor avant gauche
            posObjectFromCenterRobot = posObjectFromSensorFL.plusNewVector(sensorFL.getVecteur());     //sensor avant gauche
        }
        else{
            double USFR = (double)sensorFR.getDetectedDistance();
            Vec2 posObjectFromSensorFR = new Vec2(USFR+enRadius*0.5, sensorFR.getSensorOrientation()); //sensor avant droit
            posObjectFromCenterRobot = posObjectFromSensorFR.plusNewVector(sensorFR.getVecteur()); //sensor avant droit
        }

        mTable.getObstacleManager().addObstacle(this.changeRef(posObjectFromCenterRobot), enRadius+ourRadius+10);
    }

    /** Ajoute un obstacle derrière le robot, avec un seul capteur ayant détecté quelque chose
     * @param isLeft si c'est le capteur gauche */
    private void addBackObstacleSingle(boolean isLeft) {
        // De meme qu'avec le front
        Vec2 posObjectFromCenterRobot;
        if (isLeft){
            double USBL = (double)sensorBL.getDetectedDistance();
            Vec2 posObjectFromSensorBL = new Vec2(USBL+enRadius*0.5, sensorBL.getSensorOrientation());
            //Vec2 posDetect = new Vec2(USBL+enRadius*0.5,sensorBL.getSensorOrientation() - sensorBL.getDetectionWideness()/2);     //sensor arrière gauche
            posObjectFromCenterRobot = posObjectFromSensorBL.plusNewVector(sensorBL.getVecteur());     //sensor arrière gauche
        }
        else{
            double USBR = (double)sensorBR.getDetectedDistance();
            Vec2 posObjectFromSensorBR = new Vec2(USBR+enRadius*0.5, sensorBR.getSensorOrientation());
            //Vec2 posDetect = new Vec2(USBF+enRadius*0.5,sensorBR.getSensorOrientation() + sensorBR.getDetectionWideness()/2);     //sensor arrière droit
            posObjectFromCenterRobot = posObjectFromSensorBR.plusNewVector(sensorBR.getVecteur());     //sensor arrière droit
        }
        mTable.getObstacleManager().addObstacle(this.changeRef(posObjectFromCenterRobot), enRadius+ourRadius+10);
    }

    /** P'tite methode pour print le debug des capteurs
     * @param obPositionRobotRef la position de l'obstacle dans le réferentiel du robot */
   /* private void printDebug(Vec2 obPositionRobotRef){
        try {
            out.write("Position calculée (référentiel du robot) :" + obPositionRobotRef);
            out.newLine();
            obPositionRobotRef = changeRef(obPositionRobotRef);
            out.write("Position calculée (référentiel de la table) :" + obPositionRobotRef);
            out.newLine();
            out.write("Position du robot :" + robotPosAndOr.getPosition());
            out.newLine();
            out.newLine();
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
            log.debug("IOException sur le Debug...");
        }
    }
    */

    /** Passe du référentiel du robot à celui de la table
     * @param pos la position relative dont on cherche les coordonées absolues */
    private Vec2 changeRef(Vec2 pos)
    {
        double robotOr;
        Vec2 robotPos;
        if (symetry) {
            robotOr = Geometry.moduloSpec(Math.PI-robotPosAndOr.getOrientation(),Math.PI);
            robotPos=robotPosAndOr.getPosition().clone();
            robotPos.setX(robotPos.getX()*-1);
        }
        else{
            robotOr = robotPosAndOr.getOrientation();
            robotPos=robotPosAndOr.getPosition().clone();
        }
        pos.setA(Geometry.moduloSpec(pos.getA()+robotOr, Math.PI));
        return pos.plusNewVector(robotPos);
    }

    /**
     *  On enleve les obstacles qu'on ne voit pas
     */
    private void removeOutDatedObstacle()
    {
        mTable.getObstacleManager().removeOutdatedObstacles();
    }

    /** Recupere la distance lue par les ultrasons
     * @return la distance selon les ultrasons */
    @SuppressWarnings("unchecked")
    public void getSensorInfos()
    {
        if(gameState.isCapteursActivated()){
            robotPosAndOr = ethWrapper.getCurrentPositionAndOrientation();
            String[] valuesSReceived;

            long timeStart = System.currentTimeMillis();
            long timeEllapsed=0;
            while (valuesReceived.peek() == null && timeEllapsed<200) {
                timeEllapsed=System.currentTimeMillis()-timeStart;
            }
            if (timeEllapsed<200) {
                String values = valuesReceived.poll();
                valuesSReceived = values.split(" ");

                for (int i = 0; i < nbSensors; i++) {
                    int distance = Integer.parseInt(valuesSReceived[i]);
                    sensorsArray.get(i).setDetectedDistance(distance*10); //on convertit des cm en mm
                    gameState.robot.setUSvalues(sensorsArray.get(i).getDetectedDistance(), i);
                }

                //Inversion gauche/droite pour symétriser
                if (symetry) {
                    sensorFL.switchValues(sensorFR);
                    sensorBL.switchValues(sensorBR);
                }
            }
            else{
                valuesReceived.clear();
                for (Sensor sensor : sensorsArray){
                    sensor.setDetectedDistance(0);
                }
            }
        }
        //Si on éteint les capteurs
        else {
            for (Sensor sensor : sensorsArray) {
                sensor.setDetectedDistance(0);
            }
        }
    }


    @Override
    public void run()
    {
        /** Initialisation : fichiers de debug, temps d'attente,...*/

        updateConfig();

        System.out.println("test");
        if (this.usingJumper) {
            while (!gameState.wasJumperRemoved()) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        // maintenant que le jumper est retiré, le match a commencé
        ThreadTimer.matchEnded = false;

        if(ThreadSensor.delay)
        {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        /** Boucle principale, celle qui dure tout le match */

        while(!ThreadTimer.matchEnded)
        {
            // on s'arrete si le ThreadManager le demande
            if(stopThreads) {
                log.debug("Stop du thread capteurs");
                return;
            }
            this.getSensorInfos();
            if (advancedDetection) {
                this.removeOutDatedObstacle();
                this.addObstacle();
            }
        }
        log.debug("Fin du thread de capteurs");

    }

    @Override
    public void updateConfig()
    {
        this.symetry = (config.getString(ConfigInfoRobot.COULEUR).equals("orange"));
        this.advancedDetection = config.getBoolean(ConfigInfoRobot.ADVANCED_DETECTION);
        this.ourRadius = 0;
        this.enRadius = config.getInt(ConfigInfoRobot.ROBOT_EN_RADIUS);
        this.maxSensorRange = config.getInt(ConfigInfoRobot.MAX_SENSOR_RANGE);
        this.minSensorRange = config.getInt(ConfigInfoRobot.MIN_SENSOR_RANGE);
        this.sensorOrientationF = config.getDouble(ConfigInfoRobot.SENSOR_ORIENTATION_FRONT);
        this.sensorOrientationB = config.getDouble(ConfigInfoRobot.SENSOR_ORIENTATION_BACK);
        this.detectionAngle = config.getDouble(ConfigInfoRobot.SENSOR_ANGLE_WIDENESS);
        this.usingJumper = config.getBoolean(ConfigInfoRobot.ATTENTE_JUMPER);
    }
}
