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
import static smartMath.Geometry.square;

/**
 * Thread qui ajoute en continu les obstacles détectés par les capteurs,
 * Et enleve ceux qui ont disparu, de meme que verifie les capteurs de contact
 * TODO Compléter la config, sinon NPE
 * @author pf, Krissprolls, discord
 */

public class OLDThreadSensor extends AbstractThread
{
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
    double maxSensorRange;

    /** Distance minimale à laquelle on peut se fier aux capteurs : ne pas detecter notre propre root par exemple
     * Override par la config */
    double minSensorRangeAv;
    double minSensorRangeAr;

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
     * Positions relatives au centre (des roues) du robot
     */
    private final Vec2 positionLF = new Vec2(120, 125);
    private final Vec2 positionRF = new Vec2(120, -125);
    private final Vec2 positionLB = new Vec2(-180,80);
    private final Vec2 positionRB = new Vec2(-180,-80);

    /*****************
     * INFOS & DEBUG *
     ****************/

    /** Delai d'attente avant de lancer le thread
     * Pour éviter de détecter la main du lanceur */
    private static boolean delay = true;

    /** Valeurs des capteurs US {avant-gauche, avant-droit, arrière gauche, arrière-droit} */
    ArrayList<Integer> USvalues = new ArrayList<Integer>(4);

    /** Valeurs de capteurs modifiées pour la suppression d'obstacle
     * Ainsi si l'un des capteurs nous indique 4km, c'est sûrement qu'il n'y a rien devant lui
     * On sépare ce qui sert à détecter de ce qui sert à ne pas détecter (oui c'est trop méta pour toi...)
     * PS : Si il indique 4 km, y'a un pb hein... */
    ArrayList<Integer> USvaluesForDeletion = new ArrayList<>();

    /** Fichier de debug pour le placement d'obstacles */
    private BufferedWriter out;
    private final boolean debug = true;

    /**
     * Crée un nouveau thread de capteurs
     * @param table La table a l'intérieure de laquelle le thread doit croire évoluer
     */
    public OLDThreadSensor (Config config, Log log, Table table, EthWrapper ethWrapper, ThreadEth eth)
    {
        super(config, log);
        this.valuesReceived = eth.getUltrasoundBuffer();
        this.mTable = table;
        this.ethWrapper = ethWrapper;
        Thread.currentThread().setPriority(6);
    }

    /** Ajoute les obstacles a l'obstacleManager */
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
        int R1, R2;
        Vec2 vec = new Vec2();
        boolean isValue = true;

        R1 = USvalues.get(0) + (int) (enRadius*0.8);
        R2 = USvalues.get(1) + (int) (enRadius*0.8);
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
        int R1, R2;
        Vec2 vec = new Vec2();
        boolean isValue = true;

        R1 = USvalues.get(2) + (int)(enRadius*0.8);
        R2 = USvalues.get(3) + (int)(enRadius*0.8);
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
        Double USFL = new Double((double) USvalues.get(0));
        Double USFR = new Double((double) USvalues.get(1));

        if (isLeft){
            // On choisit le point à l'extrémité de l'arc à coté du capteur pour la position de l'ennemie: à courte distance, la position est réaliste,
            // à longue distance (>1m au vue des dimensions), l'ennemie est en réalité de l'autre coté
            Vec2 posDetect = new Vec2(USFL, angleLF + detectionAngle/2);
            double angleEn = angleRF + detectionAngle/2;
            posEn = posDetect.plusNewVector(new Vec2(enRadius*0.8, angleEn)).plusNewVector(positionLF);
        }
        else{
            Vec2 posDetect = new Vec2(USFR, angleRF - detectionAngle/2);
            double angleEn = angleLF - detectionAngle/2;
            posEn = posDetect.plusNewVector(new Vec2(enRadius*0.8, angleEn)).plusNewVector(positionRF);
        }

        this.printDebug(posEn);
        mTable.getObstacleManager().addObstacle(this.changeRef(posEn), enRadius, lifetimeForUntestedObstacle);
    }

    /** Ajoute un obstacle derrière le robot, avec un seul capteur ayant détecté quelque chose
     * @param isLeft si c'est le capteur gauche */
    private void addBackObstacleSingle(boolean isLeft) {
        // De meme qu'avec le front

        Vec2 posEn;
        Double USBL = new Double((double) USvalues.get(2));
        Double USBF = new Double((double) USvalues.get(3));

        if (isLeft){
            Vec2 posDetect = new Vec2(USBL,angleLB - detectionAngle/2);
            double angleEn = angleRB - detectionAngle/2;
            posEn = posDetect.plusNewVector(new Vec2(enRadius*0.8, angleEn)).plusNewVector(positionLB);
        }
        else{
            Vec2 posDetect = new Vec2(USBF,angleRB + detectionAngle/2);
            double angleEn = angleLB + detectionAngle/2;
            posEn = posDetect.plusNewVector(new Vec2(enRadius*0.8, angleEn)).plusNewVector(positionRB);
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
            robotPosAndOr = ethWrapper.getCurrentPositionAndOrientation();

            String[] valuesSReceived;
            ArrayList<Integer> res = new ArrayList<>();

            while(valuesReceived.peek() == null){
                Thread.sleep(5);
            }

            valuesSReceived = valuesReceived.poll().split(" ");

            for(String s : valuesSReceived) {
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

        if(OLDThreadSensor.delay)
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

            if( !USvalues.contains(-1)) // si on n'a pas spammé
            {
                this.removeOutDatedObstacle();
                this.addObstacle();
            }
        }
        log.debug("Fin du thread de capteurs");

    }

    @Override
    public void updateConfig()
    {
        symetry = (config.getString(ConfigInfoRobot.COULEUR).equals("orange"));
        enRadius = config.getInt(ConfigInfoRobot.ROBOT_EN_RADIUS);
        maxSensorRange = config.getInt(ConfigInfoRobot.MAX_SENSOR_RANGE);
        minSensorRangeAv = config.getInt(ConfigInfoRobot.MIN_SENSOR_RANGEAV);
        minSensorRangeAr = config.getInt(ConfigInfoRobot.MIN_SENSOR_RANGEAR);
    }
}