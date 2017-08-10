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

package threads;

import container.Container;
import exceptions.ContainerException;


/**
 * Thread qui sera exécuté à la fin du programme
 * @author pf
 *
 */

public class ThreadExit extends Thread
{
	protected Container container;
	private static ThreadExit instance = null;
	
	public static ThreadExit getInstance()
	{
		return instance;
	}
	
	public static ThreadExit makeInstance(Container container)
	{
		return instance = new ThreadExit(container);
	}

	private ThreadExit(Container container)
	{
		this.container = container;
	}

	@Override
	public void run()
	{
		Thread.currentThread().setName("ThreadRobotExit");
		try {
			container.destructor();
		} catch (ContainerException | InterruptedException e) {
			System.out.println(e);
		}
	}
}