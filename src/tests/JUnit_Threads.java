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

import enums.ConfigInfoRobot;
import org.junit.Assert;
import org.junit.Test;
import robot.Robot;
import smartMath.Vec2;
import table.Table;
import threads.ThreadTimer;

/**
 * Tests unitaires des threads.
 *
 * @author pf
 */

public class JUnit_Threads extends JUnit_Test {

	/**
	 * Test_arret.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void test_arret() throws Exception
	{
/*		EthWrapper deplacements = container.getService(EthWrapper.class);
		deplacements.setX(0);
		deplacements.setY(1500);
		deplacements.setOrientation(0);
		deplacements.setTranslationnalSpeed(80);
		Robot robotvrai = container.getService(Robot.class);
		// TODO démarrer thread position
		container.startAllThreads();
		Thread.sleep(100);
		Assert.assertTrue(robotvrai.getPosition().equals(new Vec2(0,1500)));
		container.stopAllThreads();
		deplacements.setX(100);
		deplacements.setY(1400);
		Thread.sleep(100);
		Assert.assertTrue(robotvrai.getPosition().equals(new Vec2(0,1500)));
*/	}

	/**
	 * Test_detection_obstacle.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void test_detection_obstacle() throws Exception
	{
		Robot robotvrai = container.getService(Robot.class);
		robotvrai.setPosition(new Vec2(0, 900));
		robotvrai.setOrientation(0);
		
		Table table = container.getService(Table.class);
		Assert.assertTrue(table.getObstacleManager().getMobileObstaclesCount() == 0);
		
		Thread.sleep(300);
		Assert.assertTrue(table.getObstacleManager().getMobileObstaclesCount() >= 1);

	}
	
	/**
	 * Test_fin_match.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void test_fin_match() throws Exception
	{
		config.override(ConfigInfoRobot.TEMPS_MATCH, 4);
		long t1 = System.currentTimeMillis();
		container.startAllThreads();
		while(!ThreadTimer.matchEnded)
		{
			Thread.sleep(500);
			if(System.currentTimeMillis()-t1 >= 4000)
				break;
		}
		Assert.assertTrue(System.currentTimeMillis()-t1 < 4000);
	}
	
	/**
	 * Test_demarrage_match.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void test_demarrage_match() throws Exception
	{
		Assert.assertTrue(!ThreadTimer.matchStarted);
		System.out.println("Veuillez retirer le jumper");
		Thread.sleep(2000);
		Assert.assertTrue(ThreadTimer.matchStarted);
	}

	/**
	 * Test_serie.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void test_serie() throws Exception
	{
		Robot robotvrai = container.getService(Robot.class);
		robotvrai.setPosition(new Vec2(1000, 1400));
		robotvrai.setOrientation((float)Math.PI);
		container.startAllThreads();
		Thread.sleep(200);
		robotvrai.moveLengthwise(1000);
	}
	
}
