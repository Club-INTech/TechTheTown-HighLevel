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
import exceptions.Locomotion.PointInObstacleException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.NoPathFound;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import robot.Robot;
import smartMath.Vec2;
import table.Table;

public class JUnit_Speed extends JUnit_Test {

	Robot robot;
	Table table;
	
	@Before
	public void setUp() throws Exception 
	{
		super.setUp();
		
		robot = container.getService(Robot.class);
		table = container.getService(Table.class);
		
		robot.setPosition(Table.entryPosition);
		robot.setOrientation(Math.PI);
		
		matchSetUp(robot, false);
	}

	@After
	public void tearDown() throws Exception 
	{
		super.tearDown();
	}

	@Test
	public void test() throws UnableToMoveException, PointInObstacleException, ImmobileEnnemyForOneSecondAtLeast {
		robot.moveLengthwise(250);
		
		for (Speed speed : Speed.values())
		{
			robot.setLocomotionSpeed(speed);
			log.debug("PWM rotation : "+speed.rotationSpeed+"\nPWM translation : "+speed.translationSpeed);
			try {
				robot.moveToLocation(new Vec2(-1000, 1000), table);
				robot.moveToLocation(new Vec2(1000, 1000), table);
			}
			catch (NoPathFound e){
				log.debug("pas de chemin trouvé");
				e.printStackTrace();
			}
		}
		
	}

}
