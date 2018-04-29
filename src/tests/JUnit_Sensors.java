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

package tests;

import enums.ActuatorOrder;
import enums.Speed;
import exceptions.BadVersionException;
import exceptions.BlockedActuatorException;
import exceptions.ExecuteException;
import exceptions.Locomotion.ImmobileEnnemyForOneSecondAtLeast;
import exceptions.Locomotion.PointInObstacleException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.NoPathFound;
import org.junit.Before;
import org.junit.Test;
import robot.Locomotion;
import robot.Robot;
import scripts.ScriptManager;
import smartMath.Vec2;
import strategie.GameState;
import table.Table;
import threads.ThreadInterface;
import threads.dataHandlers.ThreadSensor;
import utils.Sleep;

import java.util.ArrayList;
import java.util.Random;

/**
 * Test des capteurs : les obstacles doivent être détectés
 *
 */

public class JUnit_Sensors extends JUnit_Test
{

	/** The capteurs. */
	private Robot robot;
	private Table table;
	private Locomotion mLocomotion;
	private ScriptManager scriptManager;
	private GameState state;

	private ThreadSensor threadSensor;
	private ThreadInterface anInterface;

	/* (non-Javadoc)
	 * @see tests.JUnit_Test#setUp()
	 */
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		state = container.getService(GameState.class);
		scriptManager = container.getService(ScriptManager.class);
		threadSensor = container.getService(ThreadSensor.class);
		table = container.getService(Table.class);
		robot = container.getService(Robot.class);
		mLocomotion = container.getService(Locomotion.class);
		anInterface = container.getService(ThreadInterface.class);

		container.startInstanciedThreads();
		log.debug("JUnit_Sensors.setUp()");
	}

	@Test
	public void testDetect() throws Exception
	{
		log.debug("Test de detection");
		robot.setPosition(Table.entryPosition);
		robot.setOrientation(Table.entryOrientation);
		log.debug ("Orientation :" + state.robot.getOrientation());
		log.debug("Position :" + state.robot.getPosition());
//		state.robot.switchSensor();
		int count=0;
		while(true){
			robot.getPosition();
			robot.getOrientation();
			String distanceDetected = "";
			count+=1;
			if (count==1000000) {
				for (int i = 0; i < 4; i++) {
					distanceDetected += i + ":" + threadSensor.getSensor(i).getDetectedDistance() + " ";
				}
				System.out.println(distanceDetected);
				count=0;
			}
		}
	}

	@Test
	public void testStopWhileMoveBasic() throws Exception
	{
		state.robot.useActuator(ActuatorOrder.BASIC_DETECTION_ENABLE,true);
		state.robot.setPosition(Table.entryPosition);
		state.robot.setOrientation(Table.entryOrientation);
		state.robot.setLocomotionSpeed(Speed.SLOW_ALL);
		state.robot.goTo(new Vec2(0,1000));
		log.debug("Test d'arret lors de l'execution d'un script");
		log.debug("Orientation :" + state.robot.getOrientation());
	}

	@Test
	public void testStopThenEsquiveWhileMove_NonBasic(){
		state.robot.setPosition(Table.entryPosition);
		state.robot.setOrientation(Table.entryOrientation);
		state.robot.setLocomotionSpeed(Speed.SLOW_ALL);
		try {
			state.robot.goTo(new Vec2(0,1000));
		} catch (UnableToMoveException e) {
			e.printStackTrace();
		} catch (ImmobileEnnemyForOneSecondAtLeast immobileEnnemyForOneSecondAtLeast) {
			Vec2 aim = immobileEnnemyForOneSecondAtLeast.getAim();
			log.debug("immobileEnnemyAimis"+aim);
			boolean ennemyDodged = false;
			int attemps=0;
			while (!ennemyDodged && attemps<10) {
				attemps++;
				try {
					log.debug("L'esquive a commencé");
					Vec2 directionToGo = (aim.minusNewVector(gameState.robot.getPosition()));
					double prodScal = directionToGo.dot(new Vec2(100.0, gameState.robot.getOrientation()));
					//On regarde si le point où l'on veut reculer est dans un obstacle, si c'est le cas, on throw PointInObstacleException
					if (prodScal>0) {
						Vec2 wantToBackUpTo=gameState.robot.getPosition().plusNewVector(new Vec2(-50.0,gameState.robot.getOrientation()));
						if (!gameState.table.getObstacleManager().isPositionInObstacle(wantToBackUpTo)) {
							gameState.robot.moveLengthwise(-50);
						}
						else{
							log.debug("Point in obstacle : on va au noeud le plus proche");
							//On sort de l'obstacle
							try {
								gameState.robot.goTo(gameState.robot.getPathfinding().getGraphe().closestNodeToPosition(gameState.robot.getPosition()).getPosition());
								log.debug("on est au noeud le plus proche : on est sortis!");
							} catch (UnableToMoveException e1) {
								e1.printStackTrace();
							} catch (ImmobileEnnemyForOneSecondAtLeast ennemyImm) {
								ennemyImm.printStackTrace();
							}
						}
					}
					else{
						Vec2 wantToBackUpTo=gameState.robot.getPosition().plusNewVector(new Vec2(50.0,gameState.robot.getOrientation()));
						if (!gameState.table.getObstacleManager().isPositionInObstacle(wantToBackUpTo)) {
							gameState.robot.moveLengthwise(50);
						}
						else{
							log.debug("Point in obstacle");
							log.debug("Point in obstacle : on va au noeud le plus proche");
							//On sort de l'obstacle
							try {
								gameState.robot.goTo(gameState.robot.getPathfinding().getGraphe().closestNodeToPosition(gameState.robot.getPosition()).getPosition());
								log.debug("on est au noeud le plus proche : on est bien sortis!");
							} catch (UnableToMoveException e1) {
								e1.printStackTrace();
							} catch (ImmobileEnnemyForOneSecondAtLeast ennemyImm) {
								ennemyImm.printStackTrace();
							}
						}
					}

					//On cherche un nouveau chemin pour y aller
					ArrayList<Vec2> pathToFollow = gameState.robot.getPathfinding().findmyway(gameState.robot.getPosition(), aim);
					gameState.robot.followPath(pathToFollow);
					ennemyDodged = true;
				} catch (ImmobileEnnemyForOneSecondAtLeast ennemy) {
					ennemy.printStackTrace();
					log.debug("L'ennemi est toujours là");
				} catch (PointInObstacleException e1) {
					log.debug("PointInObstacleException, on part au noeud le plus proche");
				} catch (UnableToMoveException e1) {
					log.debug("UnableToMoveException");
					e1.printStackTrace();
				} catch (NoPathFound noPathFound) {
					log.debug("NoPathFound");
					noPathFound.printStackTrace();
				} finally {
					try {
						Thread.sleep(250);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
			}
		}
	}


	@Test
	public void testDetecting() throws Exception
	{
		log.debug("Test de détection");
		container.startInstanciedThreads();

		state.robot.switchSensor();
		state.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
		state.robot.setPosition(new Vec2(580, 208));
		state.robot.setOrientation(-Math.PI/2);
		try {
			state.robot.moveLengthwise(-600);
			// state.robot.turn(-Math.PI / 4);
			Sleep.sleep(5000);
			log.debug("LocomotionSpeed " + state.robot.getLocomotionSpeed());
		}catch (Exception e){
			e.printStackTrace();
		}
		state.robot.switchSensor();
	}

//	@Test
	public void testDetectionTournante() throws ImmobileEnnemyForOneSecondAtLeast {
		log.debug("Test d'évitement");
		
	/*	try 
		{
			state.robot.moveLengthwise(250);
		} 
		catch (UnableToMoveException e) 
		{
		//SUUUUUUUUUS
			log.critical( e.logStack(), this);
		}*/

		while(true)
		{
			try
			{
				state.robot.turn(- Math.PI/2);
				state.robot.sleep(500);
				state.robot.turn( Math.PI);
				state.robot.sleep(500);
			}
			catch (UnableToMoveException e1)
			{
				log.critical( e1.logStack());
			}
		}
	}

	//@Test
	public void testMoveThenDetect()
	{

		try
		{
			state.robot.moveLengthwiseWithoutDetection(500, false);
			state.robot.turn(- Math.PI/2);
		}
		catch (UnableToMoveException e1)
		{
			log.critical( e1.logStack());
		} catch (ImmobileEnnemyForOneSecondAtLeast immobileEnnemyForOneSecondAtLeast) {
			immobileEnnemyForOneSecondAtLeast.printStackTrace();
		}
		while (true)
		{
		}
	}

	//@Test

	public void testMoveForwardBackward() throws ImmobileEnnemyForOneSecondAtLeast {
		
		try 
		{
			state.robot.moveLengthwiseWithoutDetection(500, false);
		}
		catch (UnableToMoveException e1)
		{
			log.critical( e1.logStack());
		}
		while (true)
		{
			try
			{
				state.robot.moveLengthwiseWithoutDetection(500, false);
				state.robot.moveLengthwiseWithoutDetection(-500, false);
			}
			catch (UnableToMoveException e1)
			{
				log.critical( e1.logStack());
			}
		}
	}

	//@Test
	public void testSensorEnnemyWithoutMovement() throws InterruptedException {
		log.debug("Test des capteurs fixe");
		state.robot.disableFeedbackLoop();
		while(true)
		{
			state.robot.getPosition();
			Thread.sleep(100);
		}
	}

	//@Test
	public void testDistaanceToClosestEnnemy()
	{
		while(true)
		{
			state.table.getObstacleManager().distanceToClosestEnemy(state.robot.getPosition());
		}
	}


	//@Test
	public void testSensorEnnemyWithMovement() throws ImmobileEnnemyForOneSecondAtLeast {
		log.debug("Test des capteurs fixe");
		while(true)
		{
			try
			{
				state.robot.moveLengthwise(50);
				state.robot.sleep(500);
			}
			catch (UnableToMoveException e)
			{
				try {
					state.robot.moveLengthwise(-50);
				} catch (UnableToMoveException e1) {
					e1.printStackTrace();
				}
			}
			try
			{
				state.robot.moveLengthwise(-50);
				state.robot.sleep(500);
			}
			catch (UnableToMoveException e)
			{
				try {
					state.robot.moveLengthwise(50);
				} catch (UnableToMoveException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

   // @Test
	public void testCapteurDeplacement() throws PointInObstacleException, ImmobileEnnemyForOneSecondAtLeast {
    	matchSetUp(state.robot, false);
    	try 
    	{
			state.robot.moveLengthwise(300);
		}
		catch (UnableToMoveException e2)
		{
			log.critical( e2.logStack());
		}
		log.debug("Test d'évitement");
		Random rand = new Random();
		while(true)
		{
			int x=0,y=0;
			try
			{
				x = rand.nextInt(3000)-1500;
				y = rand.nextInt(2000);
				state.robot.moveToLocation(new Vec2 (x,y), state.table);
			}
			catch (UnableToMoveException e1)
			{
				log.critical("!!!!! Catch de"+e1+" dans testEvitement !!!!!");
				break;
			}
			catch (NoPathFound e){
				log.debug("pas de chemin trouvé");
				e.printStackTrace();
			}
		}
	}
}
