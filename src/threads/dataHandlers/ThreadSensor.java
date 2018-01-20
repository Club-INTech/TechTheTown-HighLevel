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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
    public ArrayList<Sensor> sensorsArray = new ArrayList<Sensor>(4);
    private int nbSensors=sensorsArray.size();

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
	private double maxSensorRange;

	/** Distance minimale à laquelle on peut se fier aux capteurs : ne pas detecter notre propre root par exemple
     * Override par la config */
	private double minSensorRangeAv;
	private double minSensorRangeAr;
    private double minSensorRange;

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
	private double detectionAngle;
	private double sensorPositionAngleF;
	private double sensorPositionAngleB;
	private int lifetimeForUntestedObstacle = 200;


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
    private BufferedWriter out;
    private final boolean debug = true;

    /**
	 * Crée un nouveau thread de capteurs
	 * @param table La table a l'intérieure de laquelle le thread doit croire évoluer
	 */
	public ThreadSensor (Config config, Log log, Table table, EthWrapper ethWrapper, ThreadEth eth)
	{
		super(config, log);
        this.valuesReceived = eth.getUltrasoundBuffer();
        this.mTable = table;
        this.ethWrapper = ethWrapper;
		Thread.currentThread().setPriority(6);
        sensorsArray.set(0,new Sensor(0,120,125,this.sensorPositionAngleF,this.detectionAngle,this.maxSensorRange,this.uncertainty));
        sensorsArray.set(1,new Sensor(1,120,-125,-this.sensorPositionAngleF,this.detectionAngle,this.maxSensorRange,this.uncertainty));
        sensorsArray.set(2,new Sensor(2,-120,125,-this.sensorPositionAngleB+Math.PI,this.detectionAngle,this.maxSensorRange,this.uncertainty));
        sensorsArray.set(3,new Sensor(3,-120,-125,this.sensorPositionAngleB-Math.PI,this.detectionAngle,this.maxSensorRange,this.uncertainty));
	}
	/** Ajoute les obstacles a l'obstacleManager */
	private void addObstacle() {
        try {

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

            if (sensorsArray.get(0).getDetectedDistance() != 0){
                if (sensorsArray.get(1).getDetectedDistance() != 0) {
                    out.write("Detection:Sensor0And1 ");
                    addFrontObstacleBoth();
                }
                else {
                    out.write("Detection:Sensor0 ");
                    addFrontObstacleSingle(true);
                }
            }
            else if (sensorsArray.get(1).getDetectedDistance() != 0){
                out.write("Detection:Sensor1 ");
                addBackObstacleSingle(false);
            }
            if (sensorsArray.get(2).getDetectedDistance() != 0){
                if (sensorsArray.get(3).getDetectedDistance() != 0){
                    out.write("Detection:Sensor2And3 ");
                    addBackObstacleBoth();
                }
                else{
                    out.write("Detection:Sensor2 ");
                    addBackObstacleSingle(true);
                }
            }
            else if (sensorsArray.get(3).getDetectedDistance() != 0){
                out.write("Detection:Sensor3 ");
                addBackObstacleSingle(false);
            }


        }catch(IOException e){
            e.printStackTrace();
        }
    }

    /** Ajoute un obstacle en face du robot, avec les deux capteurs ayant détecté quelque chose
     * Convention: la droite du robot est l'orientation 0 (on travaille dans le repère du robot, et on garde les memes conventions que pour la table) */
    private void addFrontObstacleBoth() {

        // On résoud l'équation du second degrée afin de trouver les deux points d'intersections des deux cercles
        // On joue sur le rayon du robot adverse pour etre sur d'avoir des solutions
        double robotX;
        double robotY;
        double b, delta;
        double R1, R2;
        Vec2 vec = new Vec2();
        boolean isValue = true;

        R1 = sensorsArray.get(0).getDetectedDistance() + (enRadius*0.8);
        R2 = sensorsArray.get(1).getDetectedDistance() + (enRadius*0.8);
        robotY = (R1*R1 - R2*R2)/(double)(4*sensorsArray.get(1).getY());    //sensor avant droit
        Integer Y = new Integer((int) robotY);

        b = -2 * sensorsArray.get(0).getX();                                //sensor avant gauche
        delta = 2*(R1*R1 + R2*R2) - Math.pow(sensorsArray.get(0).getY(),2); //sensor avant gauche

        if (delta > 1) {
            robotX = ((-b + Math.sqrt(delta)) / 2.0);
            Integer X = new Integer((int) robotX);
            vec = new Vec2(X, Y);
        }
        else if (isBetween(delta, -1, 1)){
            robotX = -b/2;
            Integer X = new Integer((int) robotX);
            vec = new Vec2(X, Y);
        }
        else{
            isValue = false;
        }
        if (isValue) {
            printDebug(vec);
            mTable.getObstacleManager().addObstacle(this.changeRef(vec), enRadius, lifetimeForUntestedObstacle);
        }
    }
    /** Ajoute un obstacle derrière le robot, avec les deux capteurs ayant détecté quelque chose */
    private void addBackObstacleBoth()
    {
        // De meme que le front, seule la selection de la bonne solution change
        double robotX;
        double robotY;
        double b, delta;
        double R1, R2;
        Vec2 vec = new Vec2();
        boolean isValue = true;

        R1 = sensorsArray.get(2).getDetectedDistance() + (enRadius*0.8);
        R2 = sensorsArray.get(3).getDetectedDistance() + (enRadius*0.8);
        robotY = (R1*R1 - R2*R2)/(double)(4*sensorsArray.get(3).getY());            //position arrière droit
        Integer Y = new Integer((int) robotY);

        b = -2 * sensorsArray.get(2).getX();                                        //position arrière gauche

        delta = 2*(R1*R1 + R2*R2) + Math.pow(sensorsArray.get(2).getY(),2);         //position arrière gauche
        if (delta > 1) {
            robotX = (int) ((-b - Math.sqrt(delta)) / 2);
            Integer X = new Integer((int) robotX);
            vec = new Vec2(X, Y);
        }
        else if(isBetween(delta, -1, 1)){
            robotX = (int) -b/2;
            Integer X = new Integer((int) robotX);
            vec = new Vec2(X, Y);
        }else{
            isValue = false;
        }
        if (isValue) {
            this.printDebug(vec);
            mTable.getObstacleManager().addObstacle(this.changeRef(vec), enRadius, lifetimeForUntestedObstacle);
        }
    }

    /** Ajoute un obstacle devant le robot, avec un seul capteur ayant détecté quelque chose
     * @param isLeft si c'est le capteur gauche */
    private void addFrontObstacleSingle(boolean isLeft)
    {
        // On modélise les arcs de cercle detecté par l'un des capteurs, puis on prend le point le plus à l'exterieur
        // Et on place le robot ennemie tangent en ce point : la position calculée n'est pas la position réelle du robot adverse mais elle suffit

        Vec2 posEn;
        Double USFL = sensorsArray.get(0).getDetectedDistance();
        Double USFR = sensorsArray.get(1).getDetectedDistance();

        if (isLeft){
            // On choisit le point à l'extrémité de l'arc à coté du capteur pour la position de l'ennemie: à courte distance, la position est réaliste,
            // à longue distance (>1m au vue des dimensions), l'ennemie est en réalité de l'autre coté
            Vec2 posDetect = new Vec2(USFL, sensorsArray.get(0).getDetectionAnglePosition() + detectionAngle/2); //sensor avant gauche
            double angleEn = sensorsArray.get(1).getDetectionAnglePosition() + detectionAngle/2;    //sensor avant droit
            posEn = posDetect.plusNewVector(new Vec2(enRadius*0.8, angleEn)).plusNewVector(sensorsArray.get(0).getVecteur());     //sensor avant gauche
        }
        else{
            Vec2 posDetect = new Vec2(USFR, sensorsArray.get(1).getDetectionAnglePosition() - detectionAngle/2); //sensor avant droit
            double angleEn = sensorsArray.get(0).getDetectionAnglePosition() - detectionAngle/2; //sensor avant gauche
            posEn = posDetect.plusNewVector(new Vec2(enRadius*0.8, angleEn)).plusNewVector(sensorsArray.get(1).getVecteur());     //sensor avant droit
        }

        this.printDebug(posEn);
        mTable.getObstacleManager().addObstacle(this.changeRef(posEn), enRadius, lifetimeForUntestedObstacle);
    }

    /** Ajoute un obstacle derrière le robot, avec un seul capteur ayant détecté quelque chose
     * @param isLeft si c'est le capteur gauche */
    private void addBackObstacleSingle(boolean isLeft) {
        // De meme qu'avec le front

        Vec2 posEn;
        Double USBL = sensorsArray.get(2).getDetectedDistance();
        Double USBF = sensorsArray.get(3).getDetectedDistance();

        if (isLeft){
            Vec2 posDetect = new Vec2(USBL,sensorsArray.get(2).getDetectionAnglePosition() - detectionAngle/2);     //sensor arrière gauche
            double angleEn = sensorsArray.get(3).getDetectionAnglePosition() - detectionAngle/2;            //sensor arrière droit
            posEn = posDetect.plusNewVector(new Vec2(enRadius*0.8, angleEn)).plusNewVector(sensorsArray.get(2).getVecteur());     //sensor arrière gauche
        }
        else{
            Vec2 posDetect = new Vec2(USBF,sensorsArray.get(3).getDetectionAnglePosition() + detectionAngle/2);     //sensor arrière droit
            double angleEn = sensorsArray.get(2).getDetectionAnglePosition() + detectionAngle/2;            //sensor arrière gauche
            posEn = posDetect.plusNewVector(new Vec2(enRadius*0.8, angleEn)).plusNewVector(sensorsArray.get(3).getVecteur());     //sensor arrière droit
        }

        this.printDebug(posEn);
        mTable.getObstacleManager().addObstacle(this.changeRef(posEn), enRadius, lifetimeForUntestedObstacle);
    }

    /** P'tite methode pour print le debug des capteurs
     * @param obPositionRobotRef la position de l'obstacle dans le réferentiel du robot */
    private void printDebug(Vec2 obPositionRobotRef){
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

    /** Passe du référentiel du robot à celui de la table
     * @param pos la position relative dont on cherche les coordonées absolues */
    private Vec2 changeRef(Vec2 pos)
    {
        pos.setA(Geometry.moduloSpec(pos.getA()+robotPosAndOr.getPosition().getA(), Math.PI));
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

            valuesSReceived = valuesReceived.poll().split(" ");


            for(int i=0; i<nbSensors; i++) {

                int distance=Integer.parseInt(valuesSReceived[i].substring(2));
                //Old method
                //res.add(distance);

                //Préparation du novueau code
                sensorsArray.get(i).setDetectedDistance(distance);
            }

            //USvalues = res;

            if(this.debug)
            {
               try {
                   for (int i=0; i<nbSensors; i++) {
                       out.write(sensorsArray.get(i).getStringDetectedDistance());
                       out.newLine();
                   }
                   out.newLine();
                   out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(symetry) //Inversion gauche/droite pour symétriser
            {
                sensorsArray.get(0).switchValues(sensorsArray.get(1));
                sensorsArray.get(2).switchValues(sensorsArray.get(3));
            }

            for(int i=0 ; i<nbSensors ; i++)
            {
                // On met tout les capteurs qui detectent un objet trop proche du robot ou à plus de maxSensorRange a 0
                // TODO : a passer en traitement de bas niveau ? Non, ce traitement peut dépendre de la façon dont on calcule la position adverse

                if ( sensorsArray.get(i).getDetectedDistance() > maxSensorRange)
                {
                    sensorsArray.get(i).setDetectedDistance(0);
                }
                else if(sensorsArray.get(i).getDetectedDistance() < minSensorRange) {
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
        try
        {
            File file = new File("us.txt");

            if (!file.exists()) {
                //file.delete();
                file.createNewFile();
            }
            out = new BufferedWriter(new FileWriter(file));

        } catch (IOException e) {
            e.printStackTrace();
        }

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
        this.symetry = (config.getString(ConfigInfoRobot.COULEUR) == "orange");
        this.enRadius = config.getInt(ConfigInfoRobot.ROBOT_EN_RADIUS);
        this.maxSensorRange = config.getInt(ConfigInfoRobot.MAX_SENSOR_RANGE);
        this.minSensorRangeAv = config.getInt(ConfigInfoRobot.MIN_SENSOR_RANGEAV);
        this.minSensorRangeAr = config.getInt(ConfigInfoRobot.MIN_SENSOR_RANGEAR);
        this.minSensorRange = config.getInt(ConfigInfoRobot.MIN_SENSOR_RANGE);
        this.sensorPositionAngleF = config.getInt(ConfigInfoRobot.SENSOR_POSITION_ANGLE_FRONT);
        this.sensorPositionAngleB = config.getInt(ConfigInfoRobot.SENSOR_POSITION_ANGLE_BACK);
        this.detectionAngle = config.getInt(ConfigInfoRobot.SENSOR_ANGLE_WIDENESS);
	}
}
