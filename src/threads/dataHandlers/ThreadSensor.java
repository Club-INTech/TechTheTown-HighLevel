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
import table.Table;
import threads.AbstractThread;
import threads.ThreadTimer;
import utils.Log;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import static smartMath.Geometry.isBetween;

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

    /** Si l'on doit symétriser */
    private boolean symetry;

    /*********************
     * INFOS DES SENSORS *
     *********************/

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
	private int lifetimeForUntestedObstacle = 200;  //temps de vie pour un obstable non testé


    /*****************
     * INFOS & DEBUG *
     ****************/

    /** Delai d'attente avant de lancer le thread
     * Pour éviter de détecter la main du lanceur */
    private static boolean delay = true;


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
	public ThreadSensor (Config config, Log log, Table table, EthWrapper ethWrapper, ThreadEth eth)
	{
		super(config, log);
		this.updateConfig();
        Thread.currentThread().setPriority(6);
        this.valuesReceived = eth.getUltrasoundBuffer();
        this.mTable = table;
        this.ethWrapper = ethWrapper;
		this.sensorFL=new Sensor(0,-127,100,this.sensorOrientationF,this.detectionAngle,this.maxSensorRange,this.minSensorRange, this.uncertainty);
		this.sensorFR=new Sensor(1,127,100,-this.sensorOrientationF,this.detectionAngle,this.maxSensorRange,this.minSensorRange, this.uncertainty);
		this.sensorBL=new Sensor(2,-127,-100,this.sensorOrientationB-Math.PI,this.detectionAngle,this.maxSensorRange,this.minSensorRange, this.uncertainty);
		this.sensorBR=new Sensor(3,127,-100,-this.sensorOrientationB+Math.PI,this.detectionAngle,this.maxSensorRange,this.minSensorRange, this.uncertainty);
        this.sensorsArray.add(0,sensorFL);
        this.sensorsArray.add(1,sensorFR);
        this.sensorsArray.add(2,sensorBL);
        this.sensorsArray.add(3,sensorBR);
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
             *      |            |
             *      |    Robot   |
             *      |    poney   |
             *      |            |
             *      |            |
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

    /** Ajoute un obstacle en face du robot, avec les deux capteurs ayant détecté quelque chose
     * Convention: la droite du robot est l'orientation 0 (on travaille dans le repère du robot, et on garde les memes conventions que pour la table) */
    private void addFrontObstacleBoth() {
        //TODO facteur 0.5 à changer empiriquement
        int l = sensorFL.getDetectedDistance();
        int r = sensorFR.getDetectedDistance();
        //Si les valeurs sont supérieures à celles-ci, la méthode a du mal à fonctionner
        if (l>500 && r>500){
            if (l>r){
                addFrontObstacleSingle(true);
            }
            else{
                addFrontObstacleSingle(false);
            }
        }
        else{
            //TODO facteur 0.8 à changer empiriquement
            int a = (int) (l + enRadius * 0.8);
            int b = (int) (r + enRadius * 0.8);
            int d = Math.abs(sensorFL.getX() - sensorFR.getX());
            double alpha = Math.acos((b * b - a * a - d * d) / (double) (-2 * a * d));
            int x = (int) (a * Math.cos(alpha));
            int y = (int) (a * Math.sin(alpha));
            Vec2 posObjectFromSensorFL = new Vec2(x, y);
            Vec2 posObjectFromCenterRobot = posObjectFromSensorFL.plusNewVector(sensorFL.getVecteur());
            if (posObjectFromCenterRobot.getA() < 3 * Math.PI / 4 && posObjectFromCenterRobot.getA() > Math.PI / 4) { // pour éviter les faux obstacles
                posObjectFromCenterRobot.setA(posObjectFromCenterRobot.getA() - Math.PI / 2);
                mTable.getObstacleManager().addObstacle(this.changeRef(posObjectFromCenterRobot), enRadius, lifetimeForUntestedObstacle);
            }
        }
    }
    /** Ajoute un obstacle derrière le robot, avec les deux capteurs ayant détecté quelque chose */
    private void addBackObstacleBoth()
    {
        int l = sensorBL.getDetectedDistance();
        int r = sensorBR.getDetectedDistance();
        //Si les valeurs sont supérieures à celles-ci, la méthode a du mal à fonctionner
        if (l>500 && r>500){
            if (l>r){
                addBackObstacleSingle(true);
            }
            else{
                addBackObstacleSingle(false);
            }
        }
        else {
            //TODO facteur 0.8 à changer empiriquement
            int a = (int) (l + enRadius * 0.8);
            int b = (int) (r + enRadius * 0.8);
            int d = Math.abs(sensorBL.getX() - sensorBR.getX());
            double alpha = Math.acos((b * b - a * a - d * d) / (double) (-2 * a * d));
            int x = (int) (a * Math.cos(alpha));
            int y = (int) (a * Math.sin(alpha));
            Vec2 posObjectFromSensorBL = new Vec2(x, y);
            Vec2 posObjectFromCenterRobot = posObjectFromSensorBL.plusNewVector(sensorBL.getVecteur());
            if (posObjectFromCenterRobot.getA() < 3 * Math.PI / 4 && posObjectFromCenterRobot.getA() > Math.PI / 4) { // pour éviter les faux obstacles
                posObjectFromCenterRobot.setA(posObjectFromCenterRobot.getA() - Math.PI / 2);
                posObjectFromCenterRobot.setX(posObjectFromCenterRobot.getX() * -1);
                mTable.getObstacleManager().addObstacle(this.changeRef(posObjectFromCenterRobot), enRadius, lifetimeForUntestedObstacle);
            }
        }
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
            System.out.println("0:"+USFL);
            Vec2 posObjectFromSensorFL = new Vec2(USFL+enRadius*0.8, sensorFL.getSensorOrientation() + sensorFL.getDetectionWideness()/2); //sensor avant gauche
            posObjectFromCenterRobot = posObjectFromSensorFL.plusNewVector(sensorFL.getVecteur());     //sensor avant gauche
        }
        else{
            double USFR = (double)sensorFR.getDetectedDistance();
            System.out.println("1:"+USFR);
            Vec2 posObjectFromSensorFR = new Vec2(USFR+enRadius*0.8, sensorFR.getSensorOrientation() - sensorFR.getDetectionWideness()/2); //sensor avant droit
            posObjectFromCenterRobot = posObjectFromSensorFR.plusNewVector(sensorFR.getVecteur()); //sensor avant droit
        }

        mTable.getObstacleManager().addObstacle(this.changeRef(posObjectFromCenterRobot), enRadius, lifetimeForUntestedObstacle);
    }

    /** Ajoute un obstacle derrière le robot, avec un seul capteur ayant détecté quelque chose
     * @param isLeft si c'est le capteur gauche */
    private void addBackObstacleSingle(boolean isLeft) {
        // De meme qu'avec le front
        Vec2 posObjectFromCenterRobot;
        if (isLeft){
            double USBL = (double)sensorBL.getDetectedDistance();
            System.out.println("2:"+USBL);
            Vec2 posObjectFromSensorBL = new Vec2(USBL+enRadius*0.8, sensorBL.getSensorOrientation() + sensorBL.getDetectionWideness()/2);
            //Vec2 posDetect = new Vec2(USBL+enRadius*0.5,sensorBL.getSensorOrientation() - sensorBL.getDetectionWideness()/2);     //sensor arrière gauche
            posObjectFromCenterRobot = posObjectFromSensorBL.plusNewVector(sensorBL.getVecteur());     //sensor arrière gauche
            posObjectFromCenterRobot.setY(posObjectFromCenterRobot.getY()*-1);
        }
        else{
            double USBR = (double)sensorBR.getDetectedDistance();
            System.out.println("3:"+USBR);
            Vec2 posObjectFromSensorBR = new Vec2(USBR+enRadius*0.8, sensorBR.getSensorOrientation() - sensorBR.getDetectionWideness()/2);
            //Vec2 posDetect = new Vec2(USBF+enRadius*0.5,sensorBR.getSensorOrientation() + sensorBR.getDetectionWideness()/2);     //sensor arrière droit
            posObjectFromCenterRobot = posObjectFromSensorBR.plusNewVector(sensorBR.getVecteur());     //sensor arrière droit
            posObjectFromCenterRobot.setY(posObjectFromCenterRobot.getY()*-1);
        }
        mTable.getObstacleManager().addObstacle(this.changeRef(posObjectFromCenterRobot), enRadius, lifetimeForUntestedObstacle);
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
        pos.setA(Geometry.moduloSpec(pos.getA()+robotPosAndOr.getOrientation(), Math.PI));
        return pos.plusNewVector(robotPosAndOr.getPosition());
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
        try
		{
		    robotPosAndOr = ethWrapper.updatePositionAndOrientation();

            String[] valuesSReceived;
            //ArrayList<Integer> res = new ArrayList<Integer>();

            while(valuesReceived.peek() == null){
                Thread.sleep(5);
            }
            String values = valuesReceived.poll();
            valuesSReceived = values.split(" ");


            for(int i=0; i<nbSensors; i++) {
                int distance=Integer.parseInt(valuesSReceived[i]);
                sensorsArray.get(i).setDetectedDistance(distance*10); //on convertit de cm en mm
            }

            if(symetry) //Inversion gauche/droite pour symétriser
            {
                sensorFL.switchValues(sensorFR);
                sensorBL.switchValues(sensorBR);
            }

            for(int i=0 ; i<nbSensors ; i++)
            {
                // On met tout les capteurs qui detectent un objet trop proche du robot ou à plus de maxSensorRange a 0
                // TODO : a passer en traitement de bas niveau ? Non, ce traitement peut dépendre de la façon dont on calcule la position adverse

                if ( sensorsArray.get(i).getDetectedDistance() > sensorsArray.get(i).getMaximalValidDetectionDistance())
                {
                    sensorsArray.get(i).setDetectedDistance(0);
                }
                else if(sensorsArray.get(i).getDetectedDistance() < sensorsArray.get(i).getMinimalValidDetectionDistance()) {
                    sensorsArray.get(i).setDetectedDistance(0);
                }
            }
		}
		catch(InterruptedException e) {
            e.printStackTrace();
        }
	}

    @Override
    public void run()
    {
        /** Initialisation : fichiers de debug, temps d'attente,...*/

        updateConfig();

        /* while(ethWrapper.isJumperAbsent())
        {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        while(!ethWrapper.isJumperAbsent())
        {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/

        // maintenant que le jumper est retiré, le match a commencé
        ThreadTimer.matchEnded = false;

        if(ThreadSensor.delay)
        {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        /** Boucle principale, celle qui dure tout le match */

        while(!ThreadTimer.matchEnded)
        {
            // on s'arrete si le ThreadManager le demande
            if(stopThreads)
            {
                log.debug("Stop du thread capteurs");
                return;
            }
            this.getSensorInfos();
            this.removeOutDatedObstacle();
            this.addObstacle();
        }
        log.debug("Fin du thread de capteurs");

    }

    @Override
	public void updateConfig()
	{
        this.symetry = (config.getString(ConfigInfoRobot.COULEUR).equals("orange"));
        this.enRadius = config.getInt(ConfigInfoRobot.ROBOT_EN_RADIUS);
        this.maxSensorRange = config.getInt(ConfigInfoRobot.MAX_SENSOR_RANGE);
        this.minSensorRange = config.getInt(ConfigInfoRobot.MIN_SENSOR_RANGE);
        this.sensorOrientationF = config.getDouble(ConfigInfoRobot.SENSOR_ORIENTATION_FRONT);
        this.sensorOrientationB = config.getDouble(ConfigInfoRobot.SENSOR_ORIENTATION_BACK);
        this.detectionAngle = config.getDouble(ConfigInfoRobot.SENSOR_ANGLE_WIDENESS);
	}
}
