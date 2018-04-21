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

import org.junit.Before;
import org.junit.Test;
import robot.EthWrapper;
import scripts.ScriptManager;
import smartMath.Vec2;
import strategie.GameState;

public class JUnit_Symetry extends JUnit_Test
{
	GameState real_state;
	ScriptManager scriptmanager;
	EthWrapper ethWrapper;
	
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		real_state = container.getService(GameState.class);
		scriptmanager = container.getService(ScriptManager.class);
		ethWrapper = container.getService(EthWrapper.class);

		// La position est setée qu'on soit jaune ou vert
		real_state.robot.setPosition(new Vec2 (1103,1000));
		real_state.robot.setOrientation(Math.PI); 
		
		real_state.robot.updateConfig();	
	}
	

	//@Test
	public void testDeplacement()
	{
		try 
		{
			real_state.robot.moveLengthwise(100);
			real_state.robot.turn(Math.PI);
			real_state.robot.turn(-Math.PI/2);
			real_state.robot.turn(0);
		}
		catch (Exception e)
		{
			log.critical(e);
		}
		
		
	}
	
	/**
	 * Verifie si le robot en a quelque chose à faire des plots ennemis 
	 */
	@Test 
	public void testObstaclesColor()
	{
		try 
		{
			real_state.robot.moveLengthwise(500);
			real_state.robot.moveToLocation(new Vec2(-500,800), real_state.table);
			real_state.robot.moveToLocation(new Vec2(-500,400), real_state.table);
		}
		catch (Exception e)
		{
			log.critical(e);
		}
	}
}
