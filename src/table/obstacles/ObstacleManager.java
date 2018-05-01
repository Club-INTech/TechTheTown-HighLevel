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

package table.obstacles;

import container.Service;
import enums.ConfigInfoRobot;
import enums.TasCubes;
import pfg.config.Config;
import smartMath.Circle;
import smartMath.Geometry;
import smartMath.Segment;
import smartMath.Vec2;
import utils.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Traite tout ce qui concerne la gestion des obstacles sur la table.
 * Les obstacles peuvent être fixes (bordures de la table par exemple) ou bien mobile (et alors considérés temporaires).
 * Un robot ennemi est une obstacle mobile par exemple. 
 *
 * @author pf,
 */

public class ObstacleManager implements Service
{

	/** système de log sur lequel écrire. */
	private Log log;

	/** endroit ou lire la configuration du robot */
	private Config config;

	/** Ensemble des obstacles mobiles/temporaires se trouvant sur la table */
	private CopyOnWriteArrayList<ObstacleProximity> mMobileObstacles;

	/** Ensemble des obstacles circulaires */
	public CopyOnWriteArrayList<ObstacleCircular> mCircularObstacle;

	/**Robot(s) Ennemi(s) qui bouge plus au bout d'une seconde, c'est une liste
	 * qui sera filée au graphe pour qu'il puise l'ajouter comme obstacle**/
	public CopyOnWriteArrayList<ObstacleCircular> mEnnemies;

	/** Ensemble des obstacles mobiles/temporaires a tester pour les placer sur la table */
	private CopyOnWriteArrayList<ObstacleProximity> mUntestedMobileObstacles;

	/** Ensembles des lignes modélisant les bords de la table */
	private CopyOnWriteArrayList<Segment> mLines;

	/** Les obstacles rectangulaires de la table */
	public CopyOnWriteArrayList<ObstacleRectangular> mRectangles;

	/** Rayon de notre robot */
	public int mRobotRadius;

	/** Dimensions de notre robot
	 * Override par la config */
	private int mRobotLenght;
	private int mRobotWidth;

	/** Rayon du robot adverse
	 * Override par la config */
	private int mEnnemyRadius;

	// TODO virer : juste du debug / interface graphique
	private int radiusDetectionDisc=0;
	private Vec2 positionDetectionDisc=new Vec2(0,0);

	/**	Temps donné aux obstacles pour qu'ils soit vérifiés */
	private final int timeToTestObstacle = 500;

	/** Temps de vie d'un robot ennemi */
	private int defaultLifetime = 1000; //OVERRIDE PAR LA CONFIG

	/** Nombre de robots ennemis crashed */
	private int crashRobot = 0;

	/** Si c'est des fous en face...
	 * Override par la config
	 */
	private boolean cDesFousEnFace = false;

	/** Buffer pour fichier de debug */
	private BufferedWriter out;

	/**
	 * Instancie un nouveau gestionnaire d'obstacle.
	 * @param log le système de log sur lequel écrire.
	 * @param config l'endroit ou lire la configuration du robot
	 */
	public ObstacleManager(Log log, Config config)
	{
		this.log = log;
		this.config = config;
		updateConfig();

		//creation des listes qui contiendront les differents types d'obstacles
		mMobileObstacles = new CopyOnWriteArrayList<ObstacleProximity>();
		mCircularObstacle = new CopyOnWriteArrayList<ObstacleCircular>();
		mLines = new CopyOnWriteArrayList<Segment>();
		mRectangles = new CopyOnWriteArrayList<ObstacleRectangular>();
		mEnnemies=new CopyOnWriteArrayList<>();
		mUntestedMobileObstacles= new CopyOnWriteArrayList<ObstacleProximity>();

		initObstacle();

		// Bords de la table
		mLines.add(new Segment(new Vec2(-1500 + mRobotRadius, 0 + mRobotRadius), new Vec2(1500 - mRobotRadius, 0 + mRobotRadius)));
		mLines.add(new Segment(new Vec2(1500 - mRobotRadius, 0 + mRobotRadius), new Vec2(1500 - mRobotRadius, 2000 - mRobotRadius)));
		mLines.add(new Segment(new Vec2(1500 - mRobotRadius, 2000 - mRobotRadius), new Vec2(-1500 + mRobotRadius, 2000 - mRobotRadius)));
		mLines.add(new Segment(new Vec2(-1500 + mRobotRadius, 2000 - mRobotRadius), new Vec2(-1500 + mRobotRadius, 0 + mRobotRadius)));
		try {
			File file = new File("debugDetect.txt");

			if (!file.exists()) {
				//file.delete();
				file.createNewFile();
			}

			out = new BufferedWriter(new FileWriter(file));
		}catch(IOException e){}
	}


	/**************************************
	 * FONCTIONS DE GESTION DES OBSTACLES *
	 **************************************/


	/**
	 * Initialise les obstacles de la table
	 */
	public void initObstacle (){

		//Les différents obstacles fixés sur la table
		//TODO initialiser tout les obstacles de la table

		//mRectangles.add(new ObstacleRectangular(new Vec2(1300, 325), 400 + 2*mRobotRadius, 650 + 2*mRobotRadius)); //-1446, 678, 108, 472
		mRectangles.add(new ObstacleRectangular(new Vec2(-1300, 325),  400 + 2*mRobotRadius, 650 + 2*mRobotRadius));
		mRectangles.add(new ObstacleRectangular(new Vec2(0, 1875),  1212 + 2*mRobotRadius, 250 + 2*mRobotRadius));

		/** Tas de cubes*/
		int d = 10;
		for (int i=0; i<6; i++) {
			mCircularObstacle.add(new ObstacleCircular(new Circle(TasCubes.getTasFromID(i).getCoordsVec2(), 87 + mRobotRadius + d)));
		}

		/**Récupérateur des eaux usées*/
		mCircularObstacle.add(new ObstacleCircular(new Circle(new Vec2( 1500,840), 105 + mRobotRadius)));
		mCircularObstacle.add(new ObstacleCircular(new Circle(new Vec2( -1500, 840), 105 + mRobotRadius)));
		mCircularObstacle.add(new ObstacleCircular(new Circle(new Vec2( 890,2000), 105 + mRobotRadius)));
		mCircularObstacle.add(new ObstacleCircular(new Circle(new Vec2( -890, 2000), 105 + mRobotRadius)));
	}

	/**
	 * Ajoute un obstacle sur la table a la position spécifiée, du rayon specifie (de type obstacleProximity)
	 *
	 * @param position position ou ajouter l'obstacle
	 * @param radius rayon de l'obstacle a ajouter
	 * TODO A réadapter à l'année en cours
	 */
	public synchronized void addObstacle(final Vec2 position, final int radius) {
		//vérification que l'on ne détecte pas un obstacle "normal"
		if (position.getX() > -1500 + mEnnemyRadius && position.getX() < 1500 - mEnnemyRadius
				&& position.getY() > mEnnemyRadius && position.getY() < 2000 - mEnnemyRadius  // Hors de la table
				&& !(position.getX() > 1100 - mEnnemyRadius && position.getY() < 600 + mEnnemyRadius) // Dans la zone de départ
				&& !(position.getX() > 550 - mEnnemyRadius && position.getY() < 170 + mEnnemyRadius)) // Dans la zone de construction
		// TODO: Prévoir les cas où l'on détecte des éléments de jeu dans la condition
		{
			boolean isThereAnObstacleIntersecting = false;
			ArrayList<ObstacleProximity> obstacleToBeRemoved = new ArrayList<>();
			for (int i=0; i<mUntestedMobileObstacles.size(); i++) {
				ObstacleProximity obstacleMobileUntested=mUntestedMobileObstacles.get(i);

				//si l'obstacle est deja dans la liste des obstacles non-testés on l'ajoute dans la liste des obstacles
				if (obstacleMobileUntested.getPosition().distance(position) < obstacleMobileUntested.getRadius()) {
					isThereAnObstacleIntersecting = true;
					obstacleMobileUntested.numberOfTimeDetected++;
					obstacleMobileUntested.setPosition(position);
					obstacleMobileUntested.setRadius(radius);
					obstacleMobileUntested.setLifeTime(timeToTestObstacle);

					// si on l'a deja vu plein de fois
					if (obstacleMobileUntested.numberOfTimeDetected >= obstacleMobileUntested.getMaxNumberOfTimeDetected()) {
						obstacleMobileUntested.numberOfTimeDetected = obstacleMobileUntested.getMaxNumberOfTimeDetected();
					}

					// si on valide sa vision et qu'il n'y a pas d'intersection avec un obstacle qui existe déjà
					if (obstacleMobileUntested.numberOfTimeDetected >= obstacleMobileUntested.getThresholdConfirmedOrUnconfirmed()) {
						boolean intersection=false;
						for(ObstacleCircular obstacleCircularFixe : mCircularObstacle) {
							if(obstacleMobileUntested.getCircle().isInsideEnough(obstacleCircularFixe.getCircle())){
								log.debug("Superposition d'obstacles !");
								intersection=true;
								break;
							}
						}
						if(!intersection){
							ObstacleProximity obstacleToAdd = new ObstacleProximity(obstacleMobileUntested.getCircle(),this.defaultLifetime);
							log.warning("Ajout d'un obstacle en position "+obstacleToAdd.getCircle().getCenter()+" avec lifeTime="+obstacleToAdd.getLifeTime());
							mMobileObstacles.add(obstacleToAdd);
							obstacleToBeRemoved.add(obstacleMobileUntested);
						}
					}
				}
			}
			mUntestedMobileObstacles.removeAll(obstacleToBeRemoved);


			// on vérifie si l'on ne voit pas un obstacle confirmé déjà présent
			for (int i=0; i<mMobileObstacles.size(); i++) {
				ObstacleProximity obstacleMobile=mMobileObstacles.get(i);
				if (obstacleMobile.getPosition().distance(position) < obstacleMobile.getRadius()) {
					isThereAnObstacleIntersecting = true;

					obstacleMobile.numberOfTimeDetected++;
					obstacleMobile.setPosition(position);
					obstacleMobile.setRadius(radius);
					obstacleMobile.setLifeTime(defaultLifetime);

					// si on l'a deja vu plein de fois
					if (obstacleMobile.numberOfTimeDetected >= obstacleMobile.getMaxNumberOfTimeDetected()) {
						obstacleMobile.numberOfTimeDetected = obstacleMobile.getMaxNumberOfTimeDetected();
					}
				}
			}
			if (!isThereAnObstacleIntersecting) {
				mUntestedMobileObstacles.add(new ObstacleProximity(new Circle(position, radius), timeToTestObstacle));
			}

    		/*on ne test pas si la position est dans un obstacle deja existant
    		 *on ne detecte pas les plots ni les gobelets (et si on les detectes on prefere ne pas prendre le risque et on les evites)
    		 * et si on detecte une deuxieme fois l'ennemi on rajoute un obstacle sur lui
    		 */
		/*
		On vérifie si l'obstacle mobile confirmé qu'on a détecté n'est pas un élément de jeu,
		si c'est le cas, on le remove de la liste des obstacles mobiles confirmés
		 */


		}
	}
	/**
	 * Supprime du gestionnaire tout les obstacles dont la date de péremption est antérieure à la date fournie
	 */
	public synchronized void removeOutdatedObstacles()
	{
		// enlève les obstacles confirmés s'ils sont périmés
		ArrayList<ObstacleProximity> confirmedObstaclesToBeRemoved = new ArrayList<>();
		for(ObstacleProximity obstacle : mMobileObstacles) {
			if (obstacle.getOutDatedTime() < System.currentTimeMillis()) {
				confirmedObstaclesToBeRemoved.add(obstacle);
				log.warning("Retire l'obstacle :" + obstacle.getPosition() + "(lifeTime: " + obstacle.getLifeTime()+")");
			}
		}
		mMobileObstacles.removeAll(confirmedObstaclesToBeRemoved);


		ArrayList<ObstacleProximity> untestedObstaclesToBeRemoved = new ArrayList<>();
		// enlève les obstacles en attente s'ils sont périmés
		for(ObstacleProximity obstacle : mUntestedMobileObstacles) {
			if (obstacle.getOutDatedTime() < System.currentTimeMillis()) {
				untestedObstaclesToBeRemoved.add(obstacle);
			}
		}
		mUntestedMobileObstacles.removeAll(untestedObstaclesToBeRemoved);
	}

	/**
	 * Retourne l'ennemie le plus proche, afin de pouvoir en faire un obstacle permanant
	 * (appelé seulement en cas de crash du robot adverse)
	 * @param position la position de notre robot
	 * @param direction direction selon laquelle on doit considérer les ennemies
	 * @return l'ennemie le plus proche
	 */
	public synchronized Obstacle getClosestObstacle (Vec2 position, Vec2 direction){

		try {
			//si aucun ennemi n'est détecté, on suppose que l'ennemi le plus proche est à 1m)

			float distanceToClosestObstacle = 10000000;
			float distanceToObstacleTested = 10000000;

			Obstacle closestObstacle = null;

			//trouve l'ennemi le plus proche parmis les obstacles confirmés
			for (int i = 0; i < mCircularObstacle.size(); i++) {
				Vec2 obstacleRelativeCoords = mCircularObstacle.get(i).getPosition().minusNewVector(position);
				distanceToObstacleTested = obstacleRelativeCoords.length() - mCircularObstacle.get(i).getRadius();

				if (distanceToObstacleTested < distanceToClosestObstacle) {
					distanceToClosestObstacle = distanceToObstacleTested;
					closestObstacle = mCircularObstacle.get(i);
				}
			}
			for (int i = 0; i < mRectangles.size(); i++) {
				Vec2 obstacleRelativeCoords = mRectangles.get(i).getPosition().minusNewVector(position);
				if(Math.abs(obstacleRelativeCoords.getY())<Math.abs(mRectangles.get(i).getSizeY()/2) &&
						Math.abs(obstacleRelativeCoords.getX())<Math.abs(mRectangles.get(i).getSizeY()/2))//On est dans l'obstacle
				{
					log.debug("Warning on est dans un obstacle et c'est pas sensé �tre possible là");
					distanceToObstacleTested = 0;
				}
				else if(Math.abs(obstacleRelativeCoords.getY())<Math.abs(mRectangles.get(i).getSizeY()/2) &&
						Math.abs(obstacleRelativeCoords.getX())>Math.abs(mRectangles.get(i).getSizeY()/2))//On est dans en X
				{
					distanceToObstacleTested = Math.abs(obstacleRelativeCoords.getX());
				}

				else if(Math.abs(obstacleRelativeCoords.getY())>Math.abs(mRectangles.get(i).getSizeY()/2) &&
						Math.abs(obstacleRelativeCoords.getX())>Math.abs(mRectangles.get(i).getSizeY()/2))//On est en diagonale on fait une approx
				{
					distanceToObstacleTested = obstacleRelativeCoords.length() - (new Vec2(mRectangles.get(i).getSizeX()/2, mRectangles.get(i).getSizeY())).length();
				}
				else if(Math.abs(obstacleRelativeCoords.getY())>Math.abs(mRectangles.get(i).getSizeY()/2) &&
						Math.abs(obstacleRelativeCoords.getX())<Math.abs(mRectangles.get(i).getSizeY()/2))//On est dans en Y
				{
					distanceToObstacleTested = Math.abs(obstacleRelativeCoords.getY());
				}
				else
				{
					log.debug("on a des trucs qui défient les mathématiques dans getClosestObstacle");
				}
				if (distanceToObstacleTested < distanceToClosestObstacle) {
					distanceToClosestObstacle = distanceToObstacleTested;
					closestObstacle = mRectangles.get(i);
				}
			}
			return closestObstacle;
		}
		catch(IndexOutOfBoundsException e)
		{
			log.critical("Ah bah oui, out of bound");
			throw e;

		}

	}


	/***********************************
	 * FONCTION DE GESTION DE L'ENNEMI *
	 ***********************************/


	/**
	 * Retourne l'obstacle mobile le plus proche
	 * @param position la position de billy
	 */
	public synchronized ObstacleProximity getClosestEnnemy(Vec2 position){
		try
		{
			//si aucun ennemi n'est détecté, on suppose que l'ennemi le plus proche est à 1m)

			int distanceToClosestEnemy = 10000000;
			int distanceToEnemyTested=10000000;

			ObstacleProximity closestEnnemy = null;

			if(mMobileObstacles.size() == 0){
				return null;
			}

			//trouve l'ennemi le plus proche parmis les obstacles confirmés
			for(int i=0; i<mMobileObstacles.size(); i++)
			{
				Vec2 ennemyRelativeCoords = mMobileObstacles.get(i).getPosition().minusNewVector(position);
				distanceToEnemyTested = (int) ennemyRelativeCoords.length();

				if(distanceToEnemyTested < distanceToClosestEnemy)
				{
					distanceToClosestEnemy = distanceToEnemyTested;
					closestEnnemy = mMobileObstacles.get(i);
				}
			}

			return closestEnnemy;
		}
		catch(IndexOutOfBoundsException e)
		{
			log.critical("Ah bah oui, out of bound");
			throw e;
		}
	}

	/**
	 * retourne la distance à l'ennemi le plus proche (en mm) dans toute les directions
	 * utile lorque le robot tourne sur lui meme
	 * @param position la position a laquelle on doit mesurer la proximité des ennemis
	 * @return la distance à l'ennemi le plus proche (>= 0)
	 */
	public synchronized int distanceToClosestEnemy(Vec2 position)
	{
		try
		{
			//si aucun ennemi n'est détecté, on suppose que l'ennemi le plus proche est à 1m)
			int squaredDistanceToClosestEnemy = 10000000;
			int squaredDistanceToEnemyTested;
			ObstacleCircular closestEnnemy = null;
			if(mMobileObstacles.size() == 0) {
				return 1000;
			}
			//trouve l'ennemi le plus proche parmis les obstacles confirmés
			for(int i=0; i<mMobileObstacles.size(); i++)
			{
				Vec2 ennemyRelativeCoords = mMobileObstacles.get(i).getPosition().minusNewVector(position);
				squaredDistanceToEnemyTested = ennemyRelativeCoords.squaredLength();
				if(squaredDistanceToEnemyTested < squaredDistanceToClosestEnemy)
				{
					squaredDistanceToClosestEnemy = squaredDistanceToEnemyTested;
					closestEnnemy = mMobileObstacles.get(i);
				}
			}

			if(squaredDistanceToClosestEnemy <= 0) {
				return 0;
			}

			if(closestEnnemy != null)
			{
				//log.debug("Position de l'ennemi le plus proche, non testé, d'après distanceToClosestEnnemy: "+mUntestedMobileObstacles.get(indexOfClosestEnnemy).getPosition(), this);
				return (int)Math.sqrt((double)squaredDistanceToClosestEnemy);
			}
			else {
				return 1000;
			}
		}
		catch(IndexOutOfBoundsException e)
		{
			log.critical("Ah bah oui, out of bound");
			throw e;
		}
	}

	/** Retourne true si le robot ennemie se trouve dans un rectangle devant / derriere de robot, ce rectangle étant
	 * la zone où notre cher Billy pourrait percuter le robot ennemie s'il avancait (sans tourner)
	 * @param aim la position de visé
	 * @param pos la position de billy
	 * @param orientation l'orientation de billy
	 */
	public boolean isEnnemyForwardOrBackWard(int distance, Vec2 pos, Vec2 aim, double orientation){

		try {
			ObstacleCircular closestEnnemy = getClosestEnnemy(pos);
			if (closestEnnemy == null){
				log.debug("Pas d'ennemis trouvés par isEnnemyForwardOrBackward");
				return false;
			}

			int obstacleRadius=closestEnnemy.getRadius();

			out.newLine();
			out.write("Position de l'ennemi le plus proche (référentiel de la table) :" + closestEnnemy.getPosition());

			// Changement de référentiel (de la table au robot)
			Vec2 ennemyPos = closestEnnemy.getPosition().minusNewVector(pos);
			ennemyPos.setA(ennemyPos.getA() - orientation);

			out.newLine();
			out.write("Position de l'ennemi le plus proche (référentiel du robot) :" + ennemyPos);

			Vec2 newAim = aim.minusNewVector(pos);
			newAim.setA(aim.getA() - orientation);

			out.newLine();
			out.newLine();

			out.write("Position de visée (référentiel du robot) :" + newAim);
			out.newLine();
			out.write("Position du robot (table) :" + pos);

			if (Math.abs(ennemyPos.getY()) < (obstacleRadius + mRobotRadius + 40) &&
					Math.abs(ennemyPos.getX()) < (distance + obstacleRadius)){
				out.newLine();
				out.write("Condition rectangle vérifiée");
				out.newLine();
				out.write("Produit scalaire :" + ennemyPos.dot(newAim));
				out.flush();
				return (ennemyPos.dot(newAim) > 0);
			}
			out.flush();
			return false;

		}catch (IOException e){
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Ajoute l'obstacle mobile le plus proche (l'ennemi quoi) aux obstacles permanants
	 * La fonction est appelée uniquement si l'ennemie ne bouge pas pendant un TimeOut
	 * @param position la position de notre robot
	 * @param direction direction selon laquelle on doit considérer les ennemies
	 * @return l'ennemie le plus proche
	 */
	public synchronized void crashEnnemyAdd (Vec2 position, Vec2 direction){
		try
		{
			int squaredDistanceToClosestEnemy = 10000000;
			int squaredDistanceToEnemyTested = 10000000;

			ObstacleProximity closestEnnemy = null;

			// Trouve l'ennemi le plus proche parmis les obstacles confirmés
			for (int i = 0; i < mMobileObstacles.size(); i++) {
				Vec2 ennemyRelativeCoords = mMobileObstacles.get(i).getPosition().minusNewVector(position);
				if (direction.dot(ennemyRelativeCoords) > 0) {
					squaredDistanceToEnemyTested = ennemyRelativeCoords.squaredLength();
					if (squaredDistanceToEnemyTested < squaredDistanceToClosestEnemy) {
						squaredDistanceToClosestEnemy = squaredDistanceToEnemyTested;
						closestEnnemy = mMobileObstacles.get(i);
					}
				}
			}

			if (closestEnnemy != null) {
				if(crashRobot == 0) {
					log.debug("Premier crash de l'ennemi, on l'ajoute ici :" + closestEnnemy.getPosition());
					ObstacleCircular ennemyToAdd = closestEnnemy.clone();
					ennemyToAdd.setRadius(mEnnemyRadius + mRobotRadius);
					mCircularObstacle.add(0,ennemyToAdd);
					crashRobot+=1;
				}

				else if(!cDesFousEnFace){
					ObstacleCircular ennemyCrashed = mCircularObstacle.get(0);
					int distanceToEnemyCrashed = (int) closestEnnemy.getPosition().minusNewVector(ennemyCrashed.getPosition()).length();

					if(distanceToEnemyCrashed < mEnnemyRadius){
						log.debug("Detection de l'ennemi crashé en :" + closestEnnemy.getPosition());
					}
					else if(distanceToEnemyCrashed < mEnnemyRadius + mRobotRadius) {
						log.debug("Detection de l'ennemi crashé, qui s'est déplacé en :" + closestEnnemy.getPosition());
						mCircularObstacle.get(0).setPosition(closestEnnemy.getPosition());
					}
					else{
						log.debug("Detection de l'ennemi crashé bien loin; il s'était pas crash enfaite... On modifie sa position en :" + closestEnnemy.getPosition());
						mCircularObstacle.get(0).setPosition(closestEnnemy.getPosition());
					}
				}
				else{
					// TODO Prévoir le cas où un ou plusieurs robot(s) vient/viennent nous faire chier, si jamais c'est des fous en face
				}
			}
		}
		catch(IndexOutOfBoundsException e)
		{
			log.critical("Ah bah oui, out of bound");
			throw e;
		}
	}


	/***************
	 * UTILITAIRES *
	 ***************/


	/**
	 * Supprime la première occurence de cet obstacle
	 * @param obs l'obstacle
	 */
	public synchronized void removeObstacle(ObstacleCircular obs)
	{
		mCircularObstacle.remove(obs);
	}

	/**
	 * Supprime la première occurence de cet obstacle
	 * @param obs l'obstacle
	 */
	public synchronized void removeObstacle(ObstacleRectangular obs)
	{
		mRectangles.remove(obs);
	}

	/**
	 * Renvoie true si un obstacle chevauche un disque. (uniquement un obstacle detecte par les capteurs)
	 *
	 * @param discCenter le centre du disque a vérifier
	 * @param radius le rayon du disque
	 * @return true, si au moins un obstacle chevauche le disque
	 */
	public synchronized boolean isDiscObstructed(final Vec2 discCenter, int radius)
	{
		radiusDetectionDisc=radius;
		positionDetectionDisc=discCenter;

		for(int i=0; i<mMobileObstacles.size(); i++)
		{
			if ((radius+mMobileObstacles.get(i).getRadius())*(radius+mMobileObstacles.get(i).getRadius())
					> (discCenter.getX()-mMobileObstacles.get(i).getPosition().getX())*(discCenter.getX()-mMobileObstacles.get(i).getPosition().getX())
					+ (discCenter.getY()-mMobileObstacles.get(i).getPosition().getY())*(discCenter.getY()-mMobileObstacles.get(i).getPosition().getY()))
			{
				log.debug("Disque obstructed avec l'obstacle "+mMobileObstacles.get(i).getPosition()+"de rayon"+mMobileObstacles.get(i).getRadius());
				log.debug("Disque en "+discCenter+" de rayon "+radius);
				return true;
			}
		}
		return false;
	}

	/**
	 * Vérifie si le position spécifié est dans l'obstacle spécifié ou non
	 * Attention : l'obstacle doit etre issu des classes ObstacleCircular ou ObstacleRectangular sous peine d'exception
	 * Attention : verifie si le point (et non le robot) est dans l'obstacle.
	 *
	 * @param pos la position a vérifier
	 * @param obstacle l'obstacle a considérer
	 * @return true, si la position est dans l'obstacle
	 */
	public synchronized boolean isPositionInObstacle(Vec2 pos, Obstacle obstacle)
	{
		if(obstacle instanceof ObstacleCircular || obstacle instanceof ObstacleProximity)
		{
			ObstacleCircular obstacleCircular = (ObstacleCircular)obstacle;
			return pos.minusNewVector(obstacle.getPosition()).getR() < obstacleCircular.getRadius();
		}
		if(obstacle instanceof ObstacleRectangular)
		{
			ObstacleRectangular obstacleRectangular = (ObstacleRectangular)obstacle;
			return pos.getX()<(obstacleRectangular.position.getX()+(obstacleRectangular.sizeX/2))
					&& pos.getX()>(obstacleRectangular.position.getX()-(obstacleRectangular.sizeX/2))
					&& pos.getY()<(obstacleRectangular.position.getY()+(obstacleRectangular.sizeY/2))
					&& pos.getY()>(obstacleRectangular.position.getY()-(obstacleRectangular.sizeY/2));
		}
		else {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Cette méthode vérifie si un point est dans un obstacle
	 * @param position
	 * @return
	 */
	public synchronized boolean isPositionInObstacle(Vec2 position){
		for(Obstacle obstacle : mCircularObstacle){
			if(isPositionInObstacle(position,obstacle)){
				return true;
			}
		}
		for(Obstacle obstacle : mRectangles){
			if(isPositionInObstacle(position,obstacle)){
				return true;
			}
		}
		return false;
	}



	/**
	 * Rend le gestionnaire d'obstacle fourni en argument explicite égal a ce gestionnaire.
	 * @param other les gestionnaire a modifier
	 */
	public void copy(ObstacleManager other)
	{
		//TODO innutilise
	}

	/**
	 *  Cette instance est elle dans le même état que celle fournie en argument explicite ?
	 *
	 * @param other l'autre instance a comparer
	 * @return true, si les deux instances sont dans le meme etat
	 */
	public boolean equals(ObstacleManager other)
	{
		//TODO inutilise
		boolean IDontKnow = false;
		return IDontKnow;
	}

	/**
	 * True si le robot est dans la table
	 * @param position
	 * @return
	 */
	public boolean isRobotInTable(Vec2 position){
		return ((Math.abs(position.getX())+mRobotRadius) < 1500 && (Math.abs(position.getY() - 1000)+mRobotRadius) < 1000);
	}


	/*********************
	 * GETTERS & SETTERS *
	 *********************/



	/** Utilis� par le pathfinding.
	 * Retourne tout les les obstacles temporaires/mobiles. (détectés par la balise laser, les capteurs de distance, etc.)
	 * @return la liste des obstacles temporaires/mobiles de la table
	 */
	public CopyOnWriteArrayList<ObstacleProximity> getMobileObstacles()
	{
		return mMobileObstacles;
	}

	/**
	 * Utilisé pour les tests.
	 * Renvois le nombre d'obstacles mobiles actuellement en mémoire
	 *
	 * @return le nombre d'obstacles mobiles actuellement en mémoire
	 */
	public int getMobileObstaclesCount()
	{
		return mMobileObstacles.size();
	}

	public CopyOnWriteArrayList<ObstacleProximity> getUntestedArrayList()
	{
		return mUntestedMobileObstacles;
	}

	/** Utilis� par le pathfinding.
	 * Retourne tout les les obstacles fixes de la table.
	 * @return la liste des obstacles fixes de la table
	 */
	public CopyOnWriteArrayList<ObstacleCircular> getmCircularObstacle()
	{
		return mCircularObstacle;
	}

	/**
	 * @return la liste des lignes formant les bords des obstacles sous forme de segments
	 */
	public CopyOnWriteArrayList<Segment> getLines()
	{
		return mLines;
	}

	/**
	 * @return la liste des rectangles formant les obstacles rectangulaires
	 */
	public CopyOnWriteArrayList<ObstacleRectangular> getRectangles()
	{
		return mRectangles;
	}

	/**
	 * @return le rayon de notre robot
	 */
	public int getRobotRadius()
	{
		return mRobotRadius;
	}

	/**
	 * @return la "longueur" de notre robot (oui parce que convention bizarre...)
	 */
	public int getmRobotLenght() {
		return mRobotLenght;
	}

	/**
	 * @return la "largeur" de notre robot
	 */
	public int getmRobotWidth() {
		return mRobotWidth;
	}

	/**
	 * Change le position d'un robot adverse.
	 *
	 * @param ennemyID numéro du robot
	 * @param position nouvelle position du robot
	 */
	public synchronized void setEnnemyNewLocation(int ennemyID, final Vec2 position)
	{
		//TODO innutilise
		//changer la position de l'ennemi demandé
		//cela sera utilise par la strategie, la methode sera ecrite si besoin
		mMobileObstacles.get(ennemyID).setPosition(position);
	}

	/**
	 * Utilis� par le thread de stratégie. (pas implemente : NE PAS UTILISER!!!)
	 * renvoie la position du robot ennemi voulu sur la table.
	 * @param ennemyID l'ennemi dont on veut la position
	 *
	 * @return la position de l'ennemi spécifié
	 */
	public Vec2 getEnnemyLocation(int ennemyID)
	{
		//TODO innutilise
		//donner la position de l'ennemi demandé
		//cela sera utilise par la strategie, la methode sera ecrite si besoin
		return  mMobileObstacles.get(ennemyID).position;
	}

	/**
	 * Debug / interface graphique
	 * @return
	 */
	@SuppressWarnings("javadoc")
	public int getDiscRadius()
	{
		return radiusDetectionDisc;
	}
	public Vec2 getDiscPosition()
	{
		return positionDetectionDisc;
	}

	@Override
	public void updateConfig()
	{
		mRobotRadius = config.getInt(ConfigInfoRobot.ROBOT_RADIUS);
		mEnnemyRadius = config.getInt(ConfigInfoRobot.ENNEMY_RADIUS);
		mRobotLenght = config.getInt(ConfigInfoRobot.ROBOT_LENGTH);
		mRobotWidth = config.getInt(ConfigInfoRobot.ROBOT_WIDTH);
		defaultLifetime = config.getInt(ConfigInfoRobot.PEREMP_OBST);
		cDesFousEnFace = config.getBoolean(ConfigInfoRobot.C_DES_FOUS_EN_FACE);
	}


	/**************
	 * INUTILISES *
	 **************/


	/**
	 * Vérifie si la position donnée est dégagée ou si elle est dans l'un des obstacles sur la table (tous les obstacles)
	 *
	 * @param position la position a vérifier
	 * @return true, si la position est dans un obstacle
	 */
	public synchronized boolean isObstructed(Vec2 position)
	{
		boolean isObstructed = false;
		for(int i = 0; i< mCircularObstacle.size(); i++) {
			if (isPositionInObstacle(position, mCircularObstacle.get(i))) {
				return true;
			}
		}
		for(int i=0; i<mRectangles.size(); i++){
			if (isPositionInObstacle(position, mRectangles.get(i))){
				return true;
			}
		}
		return isObstructed;
	}

	/**
	 *  On enlève les obstacles présents sur la table virtuelle mais non detectés
	 * @param position
	 * @param orientation
	 * @param detectionRadius
	 * @param detectionAngle
	 *  @return true si on a enlevé un obstacle, false sinon
	 */
	public synchronized boolean removeNonDetectedObstacles(Vec2 position, double orientation, double detectionRadius, double detectionAngle)
	{
		boolean obstacleDeleted=false;
		//check non testés ;--;et si <=0 remove
		// check testés ; -- ; et si <maxnon goto nonteste  remove de testés


		//parcours des obstacles
		for(int i = 0; i < mUntestedMobileObstacles.size(); i++)
		{
			Vec2 positionEnnemy = mUntestedMobileObstacles.get(i).position;
			int ennemyRay = mUntestedMobileObstacles.get(i).getRadius();
			// On verifie que l'ennemi est dans le cercle de detection actuel
			if((positionEnnemy.distance(position) < (detectionRadius+ennemyRay)*(detectionRadius+ennemyRay)))
			{
				if(isEnnemyInCone(positionEnnemy, position, detectionRadius, orientation,  detectionAngle, ennemyRay) )
				{
					mUntestedMobileObstacles.get(i).numberOfTimeDetected--;

					if(mUntestedMobileObstacles.get(i).numberOfTimeDetected <= 0)
					{
						mUntestedMobileObstacles.remove(i--);
						obstacleDeleted=true;
						log.debug("Ennemi untested en "+positionEnnemy+" enlevé !");
					}
				}
			}
		}
		for(int i = 0; i < mMobileObstacles.size(); i++)
		{
			Vec2 positionEnnemy = mMobileObstacles.get(i).position;
			int ennemyRay = mMobileObstacles.get(i).getRadius();
			// On verifie que l'ennemi est dans le cercle de detection actuel
			if((positionEnnemy.distance(position) < (detectionRadius+ennemyRay)*(detectionRadius+ennemyRay)))
			{
				if(isEnnemyInCone(positionEnnemy, position, detectionRadius, orientation,  detectionAngle, ennemyRay) )
				{
					mMobileObstacles.get(i).numberOfTimeDetected--;

					if(mMobileObstacles.get(i).numberOfTimeDetected < mMobileObstacles.get(i).getThresholdConfirmedOrUnconfirmed())
					{
						mMobileObstacles.get(i).setLifeTime(2000);
						mUntestedMobileObstacles.add(mMobileObstacles.get(i));
						mMobileObstacles.remove(i--);

						obstacleDeleted=true;
						log.debug("Ennemi en "+positionEnnemy+" enlevé !");
					}
				}
			}
		}
		return obstacleDeleted;
	}

	/**
	 * Renvoie true si l'ennemi se trouve dans un des deux cones de detection (droite et gauche)
	 * @param positionEnnemy
	 * @param position
	 * @param detectionRadius
	 * @param orientation
	 * @param detectionAngle
	 * @param ennemyRay
	 * @return
	 */
	public boolean isEnnemyInCone(Vec2 positionEnnemy, Vec2 position, double detectionRadius, double orientation, double detectionAngle, int ennemyRay)
	{
		double ennemyAngle = Math.atan2(positionEnnemy.getX() - position.getX(), positionEnnemy.getY() - position.getY());

		// si le centre de l'obstacle est dans le cone 
		// ou 
		// si on intersecte avec le coté gauche 
		// ou
		// si on interesecte avec le coté droit
		Segment coteGaucheCone = new Segment(position,
				new Vec2( position.getX() + (int)(detectionRadius*Math.cos(orientation + detectionAngle/2)),
						position.getY() + (int)(detectionRadius*Math.sin(orientation + detectionAngle/2)) ) );
		Segment coteDroitCone = new Segment(position,
				new Vec2( position.getX() + (int)(detectionRadius*Math.cos(orientation - detectionAngle/2)),
						position.getY() + (int)(detectionRadius*Math.sin(orientation - detectionAngle/2)) ) );

		return (ennemyAngle < (orientation + detectionAngle/2)
				&& ennemyAngle > (orientation - detectionAngle/2)
				|| ( ( Geometry.intersects( coteGaucheCone ,
				new Circle(positionEnnemy, ennemyRay)) )
				|| ( Geometry.intersects(	coteDroitCone,
				new Circle(positionEnnemy, ennemyRay))) )  );
	}

	/**
	 *  On enleve les obstacles qui sont en confrontation avec nous :
	 *  Cela evite de se retrouver dans un obstacle
	 * @param position
	 */
	public void removeObstacleInUs(Vec2 position)
	{
		for(int i=0; i<mMobileObstacles.size(); i++)
		{
			if( (   (position.getX()-mMobileObstacles.get(i).getPosition().getX())*(position.getX()-mMobileObstacles.get(i).getPosition().getX())
					+   (position.getY()-mMobileObstacles.get(i).getPosition().getY())*(position.getY()-mMobileObstacles.get(i).getPosition().getY()) )
					<=( (mRobotRadius+mMobileObstacles.get(i).getRadius())*(mRobotRadius+mMobileObstacles.get(i).getRadius())) )
				mMobileObstacles.remove(mMobileObstacles.get(i));
		}
	}

	/**
	 * supprime les obstacles fixes dans le disque
	 *
	 * @param position
	 * @param radius
	 */
	public void removeFixedObstaclesInDisc(Vec2 position, int radius)
	{
		for(int i = 0; i< mCircularObstacle.size(); i++)
			if((position.getX()- mCircularObstacle.get(i).getPosition().getX())*(position.getX()- mCircularObstacle.get(i).getPosition().getX())
					+ (position.getY()- mCircularObstacle.get(i).getPosition().getY())*(position.getY()- mCircularObstacle.get(i).getPosition().getY())
					<= mRobotRadius*mRobotRadius)
				mCircularObstacle.remove(mCircularObstacle.get(i));
	}

	public void printObstacleFixedList()
	{
		for(int i = 0; i< mCircularObstacle.size(); i++)
			mCircularObstacle.get(i).printObstacleMemory();
	}

	/**
	 * Permet de update les obstacles avec un nouveau rayon de robot
	 * @param newRobotRadius le nouveau rayon
	 */
	public void updateObstacles(int newRobotRadius)
	{
		if(this.mRobotRadius == newRobotRadius)
			return;

		for(int i=0; i<mRectangles.size(); i++)
		{
			ObstacleRectangular obs=mRectangles.get(i);
			obs.changeDim(obs.getSizeX()-2*mRobotRadius+2*newRobotRadius, obs.getSizeY()-2*mRobotRadius+2*newRobotRadius);
		}

		for(int i=0; i<mCircularObstacle.size(); i++)
		{
			ObstacleCircular obs=mCircularObstacle.get(i);
			obs.setRadius(obs.getRadius()-mRobotRadius+newRobotRadius);
		}

		for(int i=0; i<mUntestedMobileObstacles.size(); i++)
		{
			ObstacleProximity obs = mUntestedMobileObstacles.get(i);
			obs.setRadius(obs.getRadius()-mRobotRadius+newRobotRadius);
		}

		for(int i=0; i<mMobileObstacles.size(); i++)
		{
			ObstacleProximity obs = mMobileObstacles.get(i);
			obs.setRadius(obs.getRadius()-mRobotRadius+newRobotRadius);
		}

		this.mRobotRadius = newRobotRadius;
     /*   while (!ThreadWorker.isGraphReady())
        {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
		ThreadWorker.recalculateGraph();*/
	}

	/**
	 * Supprime tous les obstacles fixes qui superposent le point donné
	 * Utile pour forcer le passage si les obstacles vont subir un changement
	 * @param point le point à dégager
	 * @return les obstacles supprimés
	 */
	public ArrayList<Obstacle> freePoint(Vec2 point)
	{
		ArrayList<Obstacle> deleted = new ArrayList<>();

		for (int i = 0; i< mCircularObstacle.size(); i++)
		{
			if(mCircularObstacle.get(i).isInObstacle(point))
			{
				deleted.add(mCircularObstacle.get(i));
				removeObstacle(mCircularObstacle.get(i));
			}
		}

		for (int i=0;i< mRectangles.size();i++)
		{
			if(mRectangles.get(i).isInObstacle(point))
			{
				deleted.add(mRectangles.get(i));
				removeObstacle(mRectangles.get(i));
			}
		}
		return deleted;
	}

	/**
	 * Cette méthode retourne l'obstacle circulaire fixe (l'ennemi n'est pas concerné)
	 * le plus proche du robot
	 * @param positionRobot
	 * @return
	 */

	public ObstacleCircular getClosestObstacleCircular(Vec2 positionRobot){
		float distanceMin=mCircularObstacle.get(0).getCircle().getCenter().distance(positionRobot);
		int iMin=0;
		for(int i=1;i<mCircularObstacle.size();i++){
			if(mCircularObstacle.get(i).getCircle().getCenter().distance(positionRobot)<=distanceMin){
				distanceMin=mCircularObstacle.get(i).getCircle().getCenter().distance(positionRobot);
				iMin=i;
			}
		}
		return mCircularObstacle.get(iMin);
	}

	/**
	 * Supprime TOUS les obstacles fixes de la table
	 * http://cdn.meme.am/instances/500x/21541512.jpg
	 */
	public void destroyEverything()
	{
		mRectangles.clear();
		mLines.clear();
		mCircularObstacle.clear();
		mMobileObstacles.clear();
		mUntestedMobileObstacles.clear();
	}

	public CopyOnWriteArrayList<ObstacleCircular> getmEnnemies() {
		return mEnnemies;
	}
}
