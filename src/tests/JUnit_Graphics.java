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

import exceptions.ContainerException;
import graphics.Window;
import org.junit.Before;
import org.junit.Test;
import smartMath.Vec2;
import table.Table;
import utils.Sleep;

import java.util.ArrayList;

public class JUnit_Graphics extends JUnit_Test
{
	private Window win;
	
    @Before
    public void setUp() throws Exception
    {
        super.setUp();
    	//
    	//win = new Window((Table)container.getService(ServiceNames.TABLE)/*, (Robot)container.getService(ServiceNames.ROBOT)*/);
    }
    
    //test de l'intersection de deux segments
    //@Test
    public void testPanel() throws InterruptedException
    {
    	ArrayList<Vec2> path = new ArrayList<Vec2>();
    	path.add(new Vec2(0, 100));
    	path.add(new Vec2(0, 1900));
    	path.add(new Vec2(-1400, 1900));
    	path.add(new Vec2(-1400, 100));
    	win.getPanel().drawArrayList(path);
    	Thread.sleep(5000);
    }
    
    //@Test
    public void testSensorPanel() throws InterruptedException
    {
    	win = new Window();
    	for(int i = 10; i < 10000; i += 10)
    	{
    		win.drawInt((int)(10*Math.cos((double)i/10)), (int)(20*Math.cos((double)i/20)), (int)(30*Math.cos((double)i/40)), (int)(40*Math.cos((double)i/80)));
    		Sleep.sleep(200);
    	}
    }

    @Test
	public void showTable()
	{
		try {

			win = new Window((Table) container.getService(Table.class));

			win.getPanel().repaint();

			while(true)
			{
				Thread.sleep(500);
			}


		} catch (ContainerException | InterruptedException e) {
			e.printStackTrace();
		}

	}
}