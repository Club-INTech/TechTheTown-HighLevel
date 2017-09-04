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

import exceptions.ConfigPropertyNotFoundException;
import graphics.Window;
import robot.Robot;
import robot.SerialWrapper;
import smartMath.Vec2;
import table.Table;
import threads.AbstractThread;
import threads.ThreadTimer;
import utils.Config;
import utils.Log;
import utils.Sleep;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

import static smartMath.Geometry.isBetween;
import static smartMath.Geometry.square;

/**
 * Thread qui ajoute en continu les obstacles détectés par les capteurs,
 * Et enleve ceux qui ont disparu, de meme que verifie les capteurs de contact
 *
 * @author pf, Krissprolls, discord
 */

public class ThreadSensor extends AbstractThread
{
	/** Le robot */
	private Robot mRobot;

    /** La table */
    private Table mTable;

	/** La stm avec laquelle on doit communiquer */
	private SerialWrapper serialWrapper;

    /** Buffer de valeurs */
    private ConcurrentLinkedQueue<String> valuesReceived;
	
	/** interface graphique */
	public Window window;
	
	// Valeurs par défaut s'il y a un problème de config
	
	/** fréquence de mise a jour des valeurs renvoyés par les capteurs. Valeurs par défaut de 5 fois par seconde s'il y a un problème de config
	* Overide par la config */
	private int sensorFrequency=15;

    /**
     * Temps maximal entre deux séries de valeurs (ms) : si cette série est incomplète, on la vire; cela évite les déclages
     */
    private int thresholdUSseries = 20;

    /**
     * Si l'on doit symétriser
     */
    private boolean symetry;

    /**
     * Rayon du robot adverse
     */
    private int radius;

    /**
     * Permet de désactiver les capteurs de la porte que récupère du sable, évite de récupérer des fausses valeurs
     */
    private static boolean modeBorgne = false;

	/**
	 * Distance maximale fiable pour les capteurs : au dela, valeurs abberentes
	 * Override par la config
	 */
	double maxSensorRange;

	/**
	 * Distance minimale à laquelle on peut se fier aux capteurs : ne pas detecter notre propre root par exemple
     * Override par la config
	 */
	double minSensorRangeAv;
	double minSensorRangeAr;

    private BufferedWriter out;

    private final boolean debug = true;
	
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
	double detectionAngle;
	double sensorPositionAngleF;
	double sensorPositionAngleB;
	int lifetimeForUntestedObstacle = 200;

    /**
     * Angles des capteurs relatifs à l'axe avant-arrière du robot (radians)
     * Convention: on effectue les calculs dans le reprère du robot, ce dernier étant orienté vers 0 (axe x)
     * Pour changer de repère, il faut effectuer une rotation des vecteurs de l'orientation du robot + une translation sur sa position.
     */
    private final double angleLF = sensorPositionAngleF;
    private final double angleRF = -sensorPositionAngleF;
    private final double angleLB = -sensorPositionAngleB + Math.PI;
    private final double angleRB = sensorPositionAngleB - Math.PI;

    /**
     * Positions relatives au centre du robot
     */

    private final Vec2 positionLF = new Vec2(120, 125);
    private final Vec2 positionRF = new Vec2(120, -125);
    private final Vec2 positionLB = new Vec2(-180,80);
    private final Vec2 positionRB = new Vec2(-180,-80);

    /**
     * Delai d'attente avant de lancer le thread
     * Pour éviter de détecter la main du lanceur
     */
    private static boolean delay = true;

    /**
     * Valeurs des capteurs US {avant-gauche, avant-droit, arrière gauche, arrière-droit}
     */
    ArrayList<Integer> USvalues = new ArrayList<Integer>(4);

    /**
     * Valeurs de capteurs modifiées pour la suppression d'obstacle
     * Ainsi si l'un des capteurs nous indique 4km, c'est sûrement qu'il n'y a rien devant lui
     * On sépare ce qui sert à détecter de ce qui sert à ne pas détecter (oui c'est trop méta pour toi...)
     * PS : Si il indique 4 km, y'a un pb hein...
     */
    ArrayList<Integer> USvaluesForDeletion = new ArrayList<>();

	/**
	 * Largeur du robot recuperée sur la config
	 */
	int robotWidth;
	
	/**
	 * 	Longueur du robot recuperée sur la config
	 */
	int robotLenght;

    /**
	 * Crée un nouveau thread de capteurs
	 *
	 * @param table La table a l'intérieure de laquelle le thread doit croire évoluer
	 * @param sensorsCardWrapper La carte capteurs avec laquelle le thread va parler
	 */
	public ThreadSensor (Config config, Log log, Table table, Robot robot, SerialWrapper sensorsCardWrapper, ThreadSerial serial)
	{
		super(config, log);
		this.serialWrapper = sensorsCardWrapper;
        this.valuesReceived = serial.getUltrasoundBuffer();
		Thread.currentThread().setPriority(6);
		mRobot = robot;
        mTable = table;
	}
	
	@Override
	public void run()
	{
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

        while(serialWrapper.isJumperAbsent())
        {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        while(!serialWrapper.isJumperAbsent())
        {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

		// maintenant que le jumper est retiré, le match a commencé
		ThreadTimer.matchEnded = false;

        if(ThreadSensor.delay)
        {
            try
            {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
		
		// boucle principale, celle qui dure tout le match
		// log.debug("Activation des capteurs");
		while(!ThreadTimer.matchEnded)
		{

			// on s'arrete si le ThreadManager le demande
			if(stopThreads)
			{
				log.debug("Stop du thread capteurs");
				return;
			}
            //long time = System.currentTimeMillis();

			getDistances();
            
            if( !USvalues.contains(-1)) // si on n'a pas spammé
			{
				// On enleve les obstacles qu'on sait absents de la table : si le robot ennemi a bougé,
				// On l'enleve de notre memoire
                mRobot.getPositionFast();
                removeObstacle();

                for(int i=0 ; i<USvalues.size(); i++)
                {
                    if(USvalues.get(i) != 0)
                        USvalues.set(i, USvalues.get(i)/*+radius/2*/);
                }

				//ajout d'obstacles mobiles dans l'obstacleManager
				addObstacle();
			}
		}
        log.debug("Fin du thread de capteurs");
		
	}
	
	/**
	 * ajoute les obstacles a l'obstacleManager
	 */
	private void addObstacle() {
        try {

            if (USvalues.get(0) != 0 && USvalues.get(1) != 0) {
                out.write("FrontBoth ");
                addFrontObstacleBoth();

            } else if ((USvalues.get(0) != 0 || USvalues.get(1) != 0)) {
                out.write("FrontSingle ");
                addFrontObstacleSingle(USvalues.get(0) != 0);
            }

            if (USvalues.get(2) != 0 && USvalues.get(3) != 0) {
                out.write("BackBoth ");
                addBackObstacleBoth();

            } else if ((USvalues.get(2) != 0 || USvalues.get(3) != 0)) {
                out.write("BackSingle ");
                addBackObstacleSingle(USvalues.get(2) != 0);
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Ajoute un obstacle en face du robot, avec les deux capteurs ayant détecté quelque chose
     * Convention: la droite du robot est l'orientation 0 (on travaille dans le repère du robot, et on garde les memes conventions que pour la table)
     */

    private void addFrontObstacleBoth() {

        // On résoud l'équation du second degrée afin de trouver les deux points d'intersections des deux cercles
        // On joue sur le rayon du robot adverse pour etre sur d'avoir des solutions
        double robotX;
        double robotY;
        double b, c, delta;
        int R1, R2;
        Vec2 vec = new Vec2();
        boolean isValue = true;

        R1 = USvalues.get(0) + (int) (radius*0.8);
        R2 = USvalues.get(1) + (int) (radius*0.8);
        robotY = ((square(R1) - square(R2))/(double)(4*positionRF.getY()));
        Integer Y = new Integer((int) robotY);

        b = -2 * positionLF.getX();
        delta = 2*(square(R1) + square(R2)) - square(positionLF.getY());

        if (delta > 1) {
            robotX = ((-b + Math.sqrt(delta)) / 2.0);
            Integer X = new Integer((int) robotX);
            vec = new Vec2(X, Y);
        }
        else if (isBetween(delta, -1, 1)){
            robotX = -b/2;
            Integer X = new Integer((int) robotX);
            vec = new Vec2(X, Y);
        }else{
            isValue = false;
        }

         if (isValue) {
             try{
                 out.write("Position calculée (référentiel du robot :" + vec);
                 out.newLine();
                 vec = changeRef(vec);
                 out.write("Position calculée (référentiel de la table) :" + vec);
                 out.newLine();
                 out.write("Position du robot :" + mRobot.getPositionFast());
                 out.newLine();
                 out.newLine();
                 out.flush();
             }catch(Exception e){
                 e.printStackTrace();
             }

             mTable.getObstacleManager().addObstacle(vec, radius, lifetimeForUntestedObstacle);
         }
    }
    /**
     * Ajoute un obstacle derrière le robot, avec les deux capteurs ayant détecté quelque chose
     */
    private void addBackObstacleBoth()
    {
        // De meme que le front, seule la selection de la bonne solution change
        double robotX;
        double robotY;
        double b, c, delta;
        int R1, R2;
        Vec2 vec = new Vec2();
        boolean isValue = true;

        R1 = USvalues.get(2) + (int)(radius*0.8);
        R2 = USvalues.get(3) + (int)(radius*0.8);
        robotY = ((square(R1) - square(R2))/(double)(4*positionRF.getY()));
        Integer Y = new Integer((int) robotY);

        b = -2 * positionLB.getX();

        delta = 2*(square(R1) + square(R2)) + square(positionLB.getY());
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
            try {
                out.write("Position calculée (référentiel du robot) :" + vec);
                out.newLine();
                vec = changeRef(vec);
                out.write("Position calculée (référentiel de la table) :" + vec);
                out.newLine();
                out.write("Position du robot :" + mRobot.getPositionFast());
                out.newLine();
                out.newLine();
                out.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }

            mTable.getObstacleManager().addObstacle(vec, radius, lifetimeForUntestedObstacle);
        }
    }

    /**
     * Ajoute un obstacle devant le robot, avec un seul capteur ayant détecté quelque chose
     * @param isLeft si c'est le capteur gauche
     */
    private void addFrontObstacleSingle(boolean isLeft)
    {
        // On modélise les arcs de cercle detecté par l'un des capteurs, puis on prend le point le plus à l'exterieur
        // Et on place le robot ennemie tangent en ce point : la position calculée n'est pas la position réelle du robot adverse mais elle suffit

        Vec2 posEn;
        Double USFL = new Double((double) USvalues.get(0));
        Double USFR = new Double((double) USvalues.get(1));

        if (isLeft){
            // On choisit le point à l'extrémité de l'arc à coté du capteur pour la position de l'ennemie: à courte distance, la position est réaliste,
            // à longue distance (>1m au vue des dimensions), l'ennemie est en réalité de l'autre coté
            Vec2 posDetect = new Vec2(USFL, angleLF + detectionAngle/2);
            double angleEn = angleRF + detectionAngle/2;
            posEn = posDetect.plusNewVector(new Vec2(radius*0.8, angleEn)).plusNewVector(positionLF);
        }
        else{
            Vec2 posDetect = new Vec2(USFR, angleRF - detectionAngle/2);
            double angleEn = angleLF - detectionAngle/2;
            posEn = posDetect.plusNewVector(new Vec2(radius*0.8, angleEn)).plusNewVector(positionRF);
        }

        try{
            out.write("Position calculée (référentiel du robot) :" + posEn);
            out.newLine();
            posEn = changeRef(posEn);
            out.write("Position calculée (référentiel de la table) :" + posEn);
            out.newLine();
            out.write("Position du robot :" + mRobot.getPositionFast());
            out.newLine();
            out.newLine();
            out.flush();
        }catch(Exception e){
            e.printStackTrace();
        }

        mTable.getObstacleManager().addObstacle(posEn, radius, lifetimeForUntestedObstacle);
    }

    /**
     * Ajoute un obstacle derrière le robot, avec un seul capteur ayant détecté quelque chose
     * @param isLeft si c'est le capteur gauche
     */
    private void addBackObstacleSingle(boolean isLeft) {
        // De meme qu'avec le front

        Vec2 posEn;
        Double USBL = new Double((double) USvalues.get(2));
        Double USBF = new Double((double) USvalues.get(3));

        if (isLeft){
            Vec2 posDetect = new Vec2(USBL,angleLB - detectionAngle/2);
            double angleEn = angleRB - detectionAngle/2;
            posEn = posDetect.plusNewVector(new Vec2(radius*0.8, angleEn)).plusNewVector(positionLB);
        }
        else{
            Vec2 posDetect = new Vec2(USBF,angleRB + detectionAngle/2);
            double angleEn = angleLB + detectionAngle/2;
            posEn = posDetect.plusNewVector(new Vec2(radius*0.8, angleEn)).plusNewVector(positionRB);
        }

        try{
            out.write("Position calculée (référentiel du robot) :" + posEn);
            out.newLine();
            posEn = changeRef(posEn);
            out.write("Position calculée (référentiel de la table) :" + posEn);
            out.newLine();
            out.write("Position du robot :" + mRobot.getPositionFast());
            out.newLine();
            out.newLine();
            out.flush();
        }catch(Exception e){
            e.printStackTrace();
        }

        mTable.getObstacleManager().addObstacle(posEn, radius, lifetimeForUntestedObstacle);
    }


    /**
     * Passe du référentiel du robot à celui de la table
     * @param pos la position relative dont on cherche les coordonées absolues
     */
    private Vec2 changeRef(Vec2 pos)
    {
        pos.setA(pos.getA()+mRobot.getOrientationFast());
        return pos.plusNewVector(mRobot.getPositionFast());
    }

	/**
	 * Recupere la distance lue par les ultrasons 
	 * @return la distance selon les ultrasons
	 */
	@SuppressWarnings("unchecked")
    public void getDistances()
	{
        try
		{
            ArrayList<String> r = new ArrayList<>();
            ArrayList<Integer> res = new ArrayList<>();
            byte count=0;
            long timeBetween;
            String toKeep;
            long timeToKeep;

            ArrayList<Long> sensorTime = new ArrayList<Long>(4);

            while(count < 4)
            {
                // On attend tant que l'on a pas reçu 4 valeurs
                if(valuesReceived.peek() != null)
                {
                    sensorTime.add(System.currentTimeMillis());
                    r.add(valuesReceived.poll());

                    if (count !=0){
                        timeBetween = sensorTime.get(count) - sensorTime.get(count-1);
                        try{
                            // Si l'on a attendu trop longtemps entre 2 valeurs, c'est que la dernière fait partie d'une nouvelle série et
                            // que la série actuelle est incomplète; on clear cette série de valeurs et on prend la suivante
                            if (timeBetween > thresholdUSseries) {

                                toKeep = r.get(count);
                                timeToKeep = sensorTime.get(count);
                                r.clear();
                                sensorTime.clear();
                                r.add(toKeep);
                                sensorTime.add(timeToKeep);
                                count = 0;
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    count++;
                }
                else
                    Sleep.sleep(5);
            }

            for(String s : r) {
                res.add(Integer.parseInt(s.substring(2)));
            }

            USvalues = res;

            if(this.debug)
            {
               try {
                    out.write(USvalues.get(0).toString());
                    out.newLine();
                    out.write(USvalues.get(1).toString());
                    out.newLine();
                    out.write(USvalues.get(2).toString());
                    out.newLine();
                    out.write(USvalues.get(3).toString());
                    out.newLine();
                    out.newLine();
                    out.flush();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(symetry) //Inversion gauche/droite pour symétriser
            {
                int temp = USvalues.get(0);
                USvalues.set(0, USvalues.get(1));
                USvalues.set(1, temp);
                temp = USvalues.get(2);
                USvalues.set(2, USvalues.get(3));
                USvalues.set(3, temp);
            }

            if(modeBorgne)
                USvalues.set(1, 0);

            mRobot.setUSvalues((ArrayList<Integer>) USvalues.clone());

            USvaluesForDeletion.clear();
            for(int i=0 ; i<4 ; i++)
            {
                USvaluesForDeletion.add((int)(USvalues.get(i).intValue()*0.8));
            }

            for(int i=0 ; i<USvalues.size() ; i++)
            {
                // On met tout les capteurs qui detectent un objet trop proche du robot ou à plus de maxSensorRange a 0
                // TODO : a passer en traitement de bas niveau ? Non, ce traitement peut dépendre de la façon dont on calcule la position adverse
                if ( USvalues.get(i) > maxSensorRange)
                {
                    USvalues.set(i, 0);
                    USvaluesForDeletion.set(i, (int)(maxSensorRange*0.9));
                }
                else if(i<2 && USvalues.get(i) < minSensorRangeAv)
                {
                    USvalues.set(i, 0);
                    USvaluesForDeletion.set(i, 0);
                }
                else if(i>=2 && USvalues.get(i) < minSensorRangeAr){
                    USvalues.set(i, 0);
                    USvaluesForDeletion.set(i, 0);
                }
                else if(i == 1 && modeBorgne)
                {
                    USvaluesForDeletion.set(i, 0);
                }
            }
		}
		catch(Exception e)
        {}
	}

	public void updateConfig()
	{
		try
		{
			sensorFrequency = Integer.parseInt(config.getProperty("capteurs_frequence"));
			//plus que cette distance (environ 50cm) on est beaucoup moins precis sur la position adverse (donc on ne l'ecrit pas !)

			maxSensorRange = Integer.parseInt(config.getProperty("horizon_capteurs"));
			minSensorRangeAv = Integer.parseInt(config.getProperty("portee_mini_capteurs_av"));
			minSensorRangeAr = Integer.parseInt(config.getProperty("portee_mini_capteurs_ar"));
			sensorPositionAngleF = Float.parseFloat(config.getProperty("angle_position_capteur_av"));
			sensorPositionAngleB = Float.parseFloat(config.getProperty("angle_position_capteur_ar"));
			detectionAngle = Float.parseFloat(config.getProperty("angle_detection_capteur"));

            symetry = config.getProperty("couleur").replaceAll(" ","").equals("jaune");

			robotLenght = Integer.parseInt(config.getProperty("longueur_robot"));
            radius = Integer.parseInt(config.getProperty("rayon_robot_adverse"));

            lifetimeForUntestedObstacle = Integer.parseInt(config.getProperty("temps_untested_obstacle"));

		}
		catch (ConfigPropertyNotFoundException e)
		{
    		log.debug("Revoir le code : impossible de trouver la propriété "+e.getPropertyNotFound());
        }
	}

	
	/**
	 *  On enleve les obstacles qu'on ne voit pas
	 */
	private void removeObstacle()
	{
	    mTable.getObstacleManager().removeOutdatedObstacles();
	}

    /**
     * Active/desactive le mode borgne
     * @param value oui/non
     */
    public static void modeBorgne(boolean value)
    {
        ThreadSensor.modeBorgne = value;
    }

    public static void noDelay()
    {
        ThreadSensor.delay = false;
    }

}
