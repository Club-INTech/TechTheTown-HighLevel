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

import enums.Speed;
import exceptions.Locomotion.PointInObstacleException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.NoPathFound;
import org.junit.Test;
import simulator.ThreadSimulator;
import simulator.ThreadSimulatorMotion;
import smartMath.Circle;
import smartMath.Vec2;
import strategie.GameState;
import table.Table;
import threads.ThreadInterface;

public class JUnit_Graphics extends JUnit_Test
{
	/** Threads */
	private ThreadInterface anInterface;
	private ThreadSimulator simulator;
	private ThreadSimulatorMotion simulatorMotion;

	/** Le GameState */
	private GameState state;
	private Table table;

	@Override
	public void setUp() throws  Exception {
		super.setUp();
		anInterface = container.getService(ThreadInterface.class);
		simulator = container.getService(ThreadSimulator.class);
		simulatorMotion = container.getService(ThreadSimulatorMotion.class);
		state = container.getService(GameState.class);
		table = container.getService(Table.class);
		container.startInstanciedThreads();
	}

	@Test
	public void testInterface() throws Exception {
		Thread.sleep(5000);
	}

	@Test
	public void testSimpleMove() throws UnableToMoveException, PointInObstacleException, ImmobileEnnemyForOneSecondAtLeast {
		state.robot.setPosition(new Vec2(1200, 400));
		state.robot.setOrientation(3*Math.PI/4);
		state.robot.setLocomotionSpeed(Speed.SLOW_ALL);
		try {
			state.robot.moveToCircle(new Circle(new Vec2(0, 1200), 0), table);
		}

		catch (NoPathFound e){
			log.debug("pas de chemin trouvé");
			e.printStackTrace();
		}
	}
}