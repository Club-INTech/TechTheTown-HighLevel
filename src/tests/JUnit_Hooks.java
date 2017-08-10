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
import hook.Hook;
import hook.types.HookFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import scripts.ScriptManager;
import strategie.GameState;
import table.Table;

import java.util.ArrayList;

//import hook.methods.SpeedDown;

/**
 *  Classe de test pour les Hooks
 */


public class JUnit_Hooks extends JUnit_Test 
{
	private GameState theRobot;
	private ScriptManager scriptManager;
	private HookFactory hookFactory;
	private ArrayList<Hook> emptyHook = new ArrayList<Hook>();
	
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
	public void testSpeed()
	{
		//Hook speed = hookFactory.newXLesserHook(1000);
		//speed.addCallback(new Callback(new SpeedDown(),true,theRobot));
		//emptyHook.add(speed);
		log.debug("Début de test !");
		try 
		{
			theRobot.robot.moveLengthwise(500,emptyHook,false);
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
