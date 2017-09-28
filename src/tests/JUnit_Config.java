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
import org.junit.Test;
import threads.dataHandlers.ThreadEth;

/**
 * Tests unitaires pour la configuration... juste épique.
 * @author rem
 */

public class JUnit_Config extends JUnit_Test {

	/**
	 * Test_get.
	 * @throws Exception the exception
	 */
	@Test
	public void test_getDefault() throws Exception
	{
		int table_x = config.getInt(ConfigInfoRobot.TABLE_X);
		String couleur = config.getString(ConfigInfoRobot.COULEUR);
		boolean sym = (config.getString(ConfigInfoRobot.COULEUR) == "orange");

		log.debug("TABLE : " + table_x + "  Couleur : " + couleur + "  Sym : " + sym);

		Thread.sleep(2000);
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
