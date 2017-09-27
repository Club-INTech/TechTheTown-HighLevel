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

import enums.ConfigInfoJUnit;
import enums.ConfigInfoRobot;
import org.junit.Before;
import org.junit.Test;
import pfg.config.Config;
import pfg.config.ConfigInfo;
import threads.dataHandlers.ThreadEth;

/**
 * Tests unitaires pour la configuration... juste épique.
 * @author rem
 *
 */

public class JUnit_Config extends JUnit_Test {

	@Before
	public void setUp() throws Exception{
		super.setUp();
		config = new Config(ConfigInfoRobot.values(), false, "config.txt");
	}

	/**
	 * Test_get.
	 * @throws Exception the exception
	 */
	@Test
	public void test_getDefault() throws Exception
	{
		int rInt;
		double rDouble;
		boolean rBool;
		String rString;

		rInt = config.getInt(ConfigInfoJUnit.RANDOM_INT);
		rDouble = config.getDouble(ConfigInfoJUnit.RANDOM_DOUBLE);
		rBool = config.getBoolean(ConfigInfoJUnit.RANDOM_BOOL);
		rString = config.getString(ConfigInfoJUnit.RANDOM_STRING);

		log.debug("Integer : " + ConfigInfoJUnit.RANDOM_INT + " - " + rInt);
		log.debug("Double : " + ConfigInfoJUnit.RANDOM_DOUBLE + " - " + rInt);
		log.debug("Boolean : " + ConfigInfoJUnit.RANDOM_BOOL + " - " + rInt);
		log.debug("String : " + ConfigInfoJUnit.RANDOM_STRING + " - " + rInt);

		Thread.sleep(10000);
	}

	/**
	 * Test_set1.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void test_set() throws Exception
	{
	}

	/**
	 * Test_set2.
	 *
	 * @throws Exception the exception
	 */
	//@Test

	public void test_set2() throws Exception
	{
	}

	/**
	 * Test_write.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void test_write() throws Exception
	{
	}

}
