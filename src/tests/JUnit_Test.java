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

import container.Container;
import exceptions.Locomotion.PointInObstacleException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;
import hook.Hook;
import org.junit.After;
import org.junit.Before;
import robot.Robot;
import robot.SerialWrapper;
import smartMath.Vec2;
import strategie.GameState;
import table.Table;
import threads.ThreadTimer;
import utils.Config;
import utils.Log;

import java.util.ArrayList;

/**
 * The Class JUnit_Test.
 */
public abstract class JUnit_Test
{

	/** The container. */
	protected Container container;
	
	/** The config. */
	protected Config config;
	
	/** The log. */
	protected Log log;
	
	/**
	 * Sets the up.
	 *
	 * @throws Exception the exception
	 */
	@Before
	public void setUp() throws Exception
	{
		container = new Container();
		config = container.getService(Config.class);
		log = container.getService(Log.class);
	}
	
	/**
	 * le set up du match en cours (mise en place des actionneurs)
	 * @param sensorsCard 
	 * @param robot le robot a setuper
	 * @throws SerialConnexionException si l'ordinateur n'arrive pas a communiquer avec les cartes
	 */
	

	
	public void waitMatchBegin(SerialWrapper sensorsCard, Robot robot)
	{

		System.out.println("Robot pret pour le match, attente du retrait du jumper");
		
		// attends que le jumper soit retiré du robot

		while(sensorsCard.isJumperAbsent())
		{
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		while(!sensorsCard.isJumperAbsent())
		{
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// maintenant que le jumper est retiré, le match a commencé
		log.critical("Jumper Retiré ! ");
		ThreadTimer.matchStarted = true;
	}

	public void matchSetUp(Robot robot, boolean isInitialisationQuick) throws SerialConnexionException
	{
		//TODO init du robot
	}

	public void setBeginPosition(Robot robot)
	{
		robot.setPosition(Table.entryPosition);
	}
	
	public void returnToEntryPosition(GameState state) throws UnableToMoveException, PointInObstacleException
	{
		state.robot.moveToLocation(new Vec2(Table.entryPosition.getX()-120, Table.entryPosition.getY()+90),new ArrayList<Hook>(), state.table);
		state.robot.turn(Math.PI-Math.atan(9.0/12));
		state.robot.moveLengthwise(-150);
		state.robot.turn(Math.PI);
	}
	

	/**
	 * Tear down.
	 *
	 * @throws Exception the exception
	 */
	@After
	public void tearDown() throws Exception 
	{
		container.destructor();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
