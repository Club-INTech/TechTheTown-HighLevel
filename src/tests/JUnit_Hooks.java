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
import exceptions.Locomotion.UnableToMoveException;
import hook.HookFactory;
import hook.HookNames;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import scripts.ScriptManager;
import strategie.GameState;
import table.Table;
import threads.dataHandlers.ThreadEth;

/**
 *  Classe de test pour les Hooks
 */

public class JUnit_Hooks extends JUnit_Test 
{
	private GameState theRobot;
	private ScriptManager scriptManager;
	private HookFactory hookFactory;
	
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		scriptManager = container.getService(ScriptManager.class);
		theRobot = container.getService(GameState.class);
		hookFactory = container.getService(HookFactory.class);
		
		theRobot.robot.setOrientation(Math.PI);
		theRobot.robot.setPosition(Table.entryPosition);
		theRobot.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
	}

	@Test
	public void testInitializeHook() throws Exception {
		hookFactory.configureHook(HookNames.SPEED_DOWN);
		hookFactory.enableHook(HookNames.SPEED_DOWN);
		Thread.sleep(5000);
		hookFactory.disableHook(HookNames.SPEED_DOWN);
	}

	@Test
	public void testSpeed()
	{
		log.debug("Début de test !");
		try 
		{
			theRobot.robot.moveLengthwise(500,false);
		}
		catch (UnableToMoveException e) 
		{
			log.debug("Haha, fat chance !");
		}
	}
	
	@After
	public void after()
	{
		theRobot.robot.immobilise();
		log.debug("Fin de test !");
	}
}
