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
import org.junit.Before;
import org.junit.Test;
import threads.dataHandlers.ThreadEth;

/**
 * Tests unitaires pour la configuration... juste épique.
 * @author rem
 *
 */

public class JUnit_Config extends JUnit_Test {

	/**
	 * Test_get.
	 * @throws Exception the exception
	 */
	@Test
	public void test_getDefault() throws Exception
	{
		try {
			int rInt = config.getInt(ConfigInfoJUnit.RANDOM_INT);
			double rDouble = config.getDouble(ConfigInfoJUnit.RANDOM_DOUBLE);
			boolean rBool = config.getBoolean(ConfigInfoJUnit.RANDOM_BOOL);
			String rString = config.getString(ConfigInfoJUnit.RANDOM_STRING);

			log.debug("Integer : " + ConfigInfoJUnit.RANDOM_INT + " - " + rInt);
			log.debug("Double : " + ConfigInfoJUnit.RANDOM_DOUBLE + " - " + rDouble);
			log.debug("Boolean : " + ConfigInfoJUnit.RANDOM_BOOL + " - " + rBool);
			log.debug("String : " + ConfigInfoJUnit.RANDOM_STRING + " - " + rString);

			Thread.sleep(10000);
		}catch (Exception e){
			e.printStackTrace();
		}
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
