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

import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Tests unitaires pour la configuration... juste épique.
 * @author pf
 *
 */

public class JUnit_Config extends JUnit_Test {

	/**
	 * Test_get.
	 *
	 * @throws Exception the exception
	 */
	//@Test
	public void test_get() throws Exception
	{
		log.debug("JUnit_ReadIniTest.test_get()");
		Assert.assertTrue(config.getProperty("test1").equals("test2"));
	}

	/**
	 * Test_set1.
	 *
	 * @throws Exception the exception
	 */
	//@Test
	public void test_set1() throws Exception
	{
		log.debug("JUnit_ReadIniTest.test_set1()");
		config.set("test1", "test3");
		Assert.assertTrue(config.getProperty("test1").equals("test3"));
	}
	
	/**
	 * Test_set2.
	 *
	 * @throws Exception the exception
	 */
	//@Test

	public void test_set2() throws Exception
	{
		log.debug("JUnit_ReadIniTest.test_set2()");
		config.set("test1", "3");
		Assert.assertTrue(config.getProperty("test1").equals("3"));
	}
	
	/**
	 * Test_write.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void test_write() throws Exception
	{
		log.debug(config.getProperty("couleur"));

		String couleur = "";
		while(!couleur.contains("jaune") && !couleur.contains("vert"))
		{
			log.debug("Rentrez \"vert\" ou \"jaune\" (override de config.ini) : ");
			BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in)); 
			 
			try 
			{
				couleur = keyboard.readLine();
			}
			catch (IOException e) 
			{
				log.debug("Erreur IO: le clavier est il bien branché ?");
			} 
			if(couleur.contains("jaune"))
				config.set("couleur", "jaune");
			else if(couleur.contains("vert"))
				config.set("couleur", "vert");
		}
		log.debug(config.getProperty("couleur"));
		
	}

}
