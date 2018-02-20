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
import utils.Log;


/**
 * Classe de Tests sur le log : son ecriture dans le fichier, ses couleurs...
 * @author theo
 *
 */
public class JUnit_Log extends JUnit_Test
{
	Log log;
	
    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        log = container.getService(Log.class);
    }
    
    /**
     * Teste les differentes couleurs du Log
     */
   @Test
    public void testColor()
    {
		log.warning("Test warning 1");
		log.critical("Test critical 2");
    	log.debug("Test debug 3");
    	log.debug("Test debug 4");
		log.critical("Test critical 5");
		log.critical("Test critical 6");
    	log.debug("Test debug 7");

    }
    
}
