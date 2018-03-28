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

import enums.Speed;
import exceptions.ContainerException;
import exceptions.Locomotion.PointInObstacleException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.NoPathFound;
import org.junit.Before;
import org.junit.Test;
import robot.Locomotion;
import robot.Robot;
import scripts.ScriptManager;
import smartMath.Circle;
import smartMath.Vec2;
import strategie.GameState;
import table.Table;
import threads.ThreadInterface;
import threads.dataHandlers.ThreadSensor;
import utils.Sleep;

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
		robot.setPosition(new Vec2(0,1000));
		robot.setOrientation(-Math.PI);
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

	// @Test
	public void testStopWhileMove() throws Exception
	{
		log.debug("Test d'arret lors de l'execution d'un script");
		ScriptManager scriptManager = container.getService(ScriptManager.class);
		container.startInstanciedThreads();
		state.robot.switchSensor();
		log.debug("Orientation :" + state.robot.getOrientation());
	}

	// @Test
	public void testEvitement() throws Exception
	{
		log.debug("Test d'évitement");
		try 
		{	
			state.robot.moveLengthwiseWithoutDetection(250, false);
		} 
		catch (UnableToMoveException e1)
		{}

		log.critical("Fin de moveLengthWise");
		while(true)
		{
			try
			{
				state.robot.moveToCircle(new Circle(new Vec2(-700, 900),0), container.getService(Table.class));
			}
			catch (UnableToMoveException | ContainerException e) 
			{
				log.critical("!!!!!! Catch de"+e+" dans testEvitement !!!!!!");
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
