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
import robot.Robot;


/**
 * The Class JUnit_Robot.
 */
public class JUnit_Robot extends JUnit_Test
{
    
    /** The robotvrai. */
    Robot robotReal;
    
    /* (non-Javadoc)
     * @see tests.JUnit_Test#setUp()
     */
    @Before
    public void setUp() throws Exception {
        super.setUp();
        robotReal = container.getService(Robot.class);
    }

    
    // TODO : tester chaque action de cette facon
    /*
    @Test
    public void test_takefire() throws Exception
    {
        robotvrai.initialiser_actionneurs_deplacements();
        Sleep.sleep(2000);
        robotvrai.takefire(Cote.GAUCHE, Cote.GAUCHE);
    }
*/
}
