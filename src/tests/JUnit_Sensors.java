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

import enums.ScriptNames;
import enums.Speed;
import exceptions.ContainerException;
import exceptions.Locomotion.PointInObstacleException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.Locomotion.UnexpectedObstacleOnPathException;
import exceptions.serial.SerialConnexionException;
import hook.Hook;
import org.junit.Before;
import org.junit.Test;
import pfg.config.Config;
import robot.EthWrapper;
import robot.Locomotion;
import scripts.ScriptManager;
import smartMath.Circle;
import smartMath.Vec2;
import strategie.GameState;
import table.Table;
import threads.dataHandlers.ThreadEth;
import threads.dataHandlers.ThreadSensor;
import utils.Log;
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
	private EthWrapper capteurs;
	private Locomotion mLocomotion;
	private ScriptManager scriptManager;
	private GameState state;

	/* (non-Javadoc)
	 * @see tests.JUnit_Test#setUp()
	 */
	@Before
	public void setUp() throws Exception 
	{
		super.setUp();
		state = container.getService(GameState.class);
		scriptManager = container.getService(ScriptManager.class);

		log.debug("JUnit_ActionneursTest.setUp()");
		capteurs = container.getService(EthWrapper.class);
				
		//Locomotion
		mLocomotion = container.getService(Locomotion.class);

		mLocomotion.setPosition(Table.entryPosition); // milieu de table
		mLocomotion.setOrientation(Math.PI);

        container.getService(ThreadEth.class);
		container.getService(ThreadSensor.class);
        container.getService(Log.class);
        container.getService(Table.class);
	}

	@Test
	public void testDetect() throws Exception
	{
		log.debug("Test de detection");
		container.startInstanciedThreads();
		Sleep.sleep(5000);
		state.robot.setOrientation(-Math.PI/2);

		scriptManager.getScript(ScriptNames.INITIALISE_ROBOT).goToThenExec(0, state,new ArrayList<Hook>());
		Thread.sleep(2000);
		log.debug ("Orientation :" + state.robot.getOrientation());
		log.debug("Position :" + state.robot.getPosition());

		Thread.sleep(1000);
		state.robot.switchSensor();
	}

	// @Test
	public void testStopWhileMove() throws Exception
	{
		log.debug("Test d'arret lors de l'execution d'un script");
		ScriptManager scriptManager = container.getService(ScriptManager.class);
		container.startInstanciedThreads();

		state.robot.switchSensor();
		log.debug("Orientation :" + state.robot.getOrientation());

		try {
			scriptManager.getScript(ScriptNames.INITIALISE_ROBOT).goToThenExec(1, state, new ArrayList<Hook>());
		}catch(Exception e){
			e.printStackTrace();
			log.debug("Suus, ca a fail");
		}
	}

	// @Test
	public void testEvitement() throws Exception
	{
		log.debug("Test d'évitement");
		try 
		{	
			state.robot.moveLengthwiseWithoutDetection(250, new ArrayList<Hook>(), false);
		} 
		catch (UnableToMoveException e1)
		{}

		log.critical("Fin de moveLengthWise");
		while(true)
		{
			try
			{
				state.robot.moveToCircle(new Circle(new Vec2(-700, 900),0),  new ArrayList<Hook>(), container.getService(Table.class));
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
	public void testDetectionTournante()
	{
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
			state.robot.moveLengthwiseWithoutDetection(500, new ArrayList<Hook>(), false);
			state.robot.turn(- Math.PI/2);
		} 
		catch (UnableToMoveException e1)
		{
			log.critical( e1.logStack());
		}
		while (true)
		{
		}
	}
	
	//@Test
	public void testMoveForwardBackward()
	{
		
		try 
		{
			state.robot.moveLengthwiseWithoutDetection(500, new ArrayList<Hook>(), false);
		} 
		catch (UnableToMoveException e1)
		{
			log.critical( e1.logStack());
		}
		while (true)
		{
			try 
			{
				state.robot.moveLengthwiseWithoutDetection(500, new ArrayList<Hook>(), false);
				state.robot.moveLengthwiseWithoutDetection(-500, new ArrayList<Hook>(), false);
			} 
			catch (UnableToMoveException e1)
			{
				log.critical( e1.logStack());
			}
		}
	}
	
	//@Test
	public void testSensorEnnemyInDiscWithoutMovement()
	{
		log.debug("Test d'évitement fixe");
		while(true)
		{
			try
			{
				mLocomotion.detectEnemyInDisk(true, false, state.robot.getPosition());
			}
			catch (UnexpectedObstacleOnPathException unexpectedObstacle)
	        {
                log.critical("Haut: Catch de "+unexpectedObstacle+" dans moveToPointException"); 

            	long detectionTime = System.currentTimeMillis();
                log.critical("Détection d'un ennemi! Abandon du mouvement.");
            	while(System.currentTimeMillis() - detectionTime < 600)
            	{
            		try
            		{
            			mLocomotion.detectEnemyInDisk(true, false, state.robot.getPosition());
            			break;
            		}
            		catch(UnexpectedObstacleOnPathException e2)
            		{
            			log.critical( e2.logStack());
            		}
            	}
			}
		}
	}
	
	//@Test
	public void testSensorEnnemyWithoutMovement() throws InterruptedException, SerialConnexionException {
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
	public void testSensorEnnemyWithMovement()
	{
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
	public void testCapteurDeplacement() throws SerialConnexionException, PointInObstacleException {
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
				state.robot.moveToLocation(new Vec2 (x,y),new ArrayList<Hook>(), state.table);
			} 
			catch (UnableToMoveException e1)
			{
				log.critical("!!!!! Catch de"+e1+" dans testEvitement !!!!!");
				break;
			} 
    	}
	}
}
