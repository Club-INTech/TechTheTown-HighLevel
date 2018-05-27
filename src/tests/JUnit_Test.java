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
import enums.ConfigInfoRobot;
import exceptions.Locomotion.ImmobileEnnemyForOneSecondAtLeast;
import exceptions.Locomotion.PointInObstacleException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.NoPathFound;
import org.junit.After;
import org.junit.Before;
import pfg.config.Config;
import robot.EthWrapper;
import robot.Robot;
import smartMath.Vec2;
import strategie.GameState;
import table.Table;
import threads.ThreadTimer;
import utils.Log;

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

	/** The EthWrapper */
	protected EthWrapper mEthWrapper;

	/** The GameState */
	protected GameState gameState;
	protected Robot robot;

	/** On regarde si on utilise ou non le jumper */
	private boolean usingJumper;


	/**
	 * Sets the up.
	 *
	 * @throws Exception the exception
	 */
	@Before
	public void setUp() throws Exception
	{
		container = new Container();
		config = container.getConfig();
		log = container.getService(Log.class);
		mEthWrapper = container.getService(EthWrapper.class);
		gameState = container.getService(GameState.class);
		this.usingJumper=config.getBoolean(ConfigInfoRobot.ATTENTE_JUMPER);
	}

	/**
	 * le set up du match en cours (mise en place des actionneurs)
	 * @param sensorsCard
	 * @param robot le robot a setuper
	 */
	public void waitMatchBegin(EthWrapper sensorsCard, Robot robot)
	{

		System.out.println("Robot pret pour le match, attente du retrait du jumper");

		// attends que le jumper soit retiré du robot

		if (this.usingJumper) {
			mEthWrapper.waitForJumperRemoval();
			System.out.println("Robot pret pour le match, attente du retrait du jumper");
			while (!gameState.wasJumperRemoved()) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			//On attend encore 50ms pour que le jumper soit bien retiré
			gameState.setJumperRemoved(true);
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// maintenant que le jumper est retiré, le match a commencé
			log.critical("Jumper Retiré ! ");
		}
		else{
			log.critical("Pas de jumper utilisé, on démarre !");
		}
		ThreadTimer.matchStarted = true;
	}

	public void matchSetUp(Robot robot, boolean isInitialisationQuick)
	{
		//TODO init du robot
	}

	public void setBeginPosition(Robot robot)
	{
		robot.setPosition(Table.entryPosition);
	}

	public void returnToEntryPosition(GameState state) throws UnableToMoveException, PointInObstacleException,ImmobileEnnemyForOneSecondAtLeast
	{
		try {
			state.robot.moveToLocation(new Vec2(Table.entryPosition.getX() - 120, Table.entryPosition.getY() + 90));
		}
		catch(NoPathFound e){
			log.debug("pas de chemin trouvé");
			e.printStackTrace();
		}

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
