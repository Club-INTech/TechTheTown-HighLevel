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

package enums;

import threads.AbstractThread;
import threads.ThreadSimulator;
import threads.ThreadTimer;
import threads.dataHandlers.ThreadEth;
import threads.dataHandlers.ThreadEvents;
import threads.dataHandlers.ThreadSensor;

/**
 * Tous les threads à instancier au début du match. Utilisé par le container
 * @author pf
 *
 */

public enum ThreadName
{
	TIMER(ThreadTimer.class),
	EVENTS(ThreadEvents.class),
	SENSOR(ThreadSensor.class),
	ETHERNET(ThreadEth.class),
	SIMULATOR(ThreadSimulator.class),
	;

	public Class<? extends AbstractThread> cls;
	
	private ThreadName(Class<? extends AbstractThread> cls)
	{
		this.cls = cls;
	}

}
