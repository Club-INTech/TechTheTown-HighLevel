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
import hook.Hook;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import scripts.ScriptManager;
import strategie.GameState;
import table.Table;
import threads.dataHandlers.ThreadSensor;
import utils.Sleep;

import java.util.ArrayList;

/**
 * teste la fermeture des portes par la version 0 du script
 * @author julian
 *
 */
public class JUnit_CloseDoors extends JUnit_Test
{
	private GameState mRobot;
	private ScriptManager scriptManager;

	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		log.debug("JUnit_DeplacementsTest.setUp()");
		mRobot = container.getService(GameState.class);
		//La position de depart est mise dans le updateConfig()
		mRobot.updateConfig();

        //mRobot.robot.switchSensor();
        container.getService(ThreadSensor.class);
        container.startInstanciedThreads();

		mRobot.robot.setPosition(Table.entryPosition);
		mRobot.robot.setOrientation(Math.PI);
		//scriptManager = container.getService(ScriptManager.class);
		mRobot.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
		mRobot.robot.moveLengthwise(600);
		mRobot.robot.moveLengthwise(-100);
		mRobot.robot.moveLengthwise(-100);
		mRobot.robot.moveLengthwise(-100);
		mRobot.robot.moveLengthwise(-100);

		while(true) Sleep.sleep(600);
	}
	
	@Test
	public void closeThatDoors()
	{
		ArrayList<Hook> emptyList = new ArrayList<Hook>();
		try
		{
			//On execute le script
			log.debug("Fermeture des portes.");
			//scriptManager.getScript(ScriptNames.CLOSE_DOORS).goToThenExec(0, mRobot, emptyList);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

	@After
	public void finish()
	{
		mRobot.robot.immobilise();
	}
}