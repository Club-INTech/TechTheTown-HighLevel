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

import graphics.Window;
import org.junit.Before;
import org.junit.Test;
import pathfinder.Arete;
import simulator.ThreadSimulator;
import simulator.ThreadSimulatorMotion;
import smartMath.Segment;
import smartMath.Vec2;
import strategie.GameState;
import table.Table;
import threads.ThreadInterface;

import java.util.ArrayList;
import java.util.HashSet;

public class JUnit_Graphics extends JUnit_Test
{
	/** Threads */
	private ThreadInterface anInterface;
	private ThreadSimulator simulator;
	private ThreadSimulatorMotion simulatorMotion;

	/** Le GameState */
	private GameState state;

	@Override
	public void setUp() throws  Exception {
		super.setUp();
		anInterface = container.getService(ThreadInterface.class);
		simulator = container.getService(ThreadSimulator.class);
		simulatorMotion = container.getService(ThreadSimulatorMotion.class);
		state = container.getService(GameState.class);
		container.startInstanciedThreads();
	}

	@Test
	public void testInterface() throws Exception {
		Thread.sleep(5000);
	}
}