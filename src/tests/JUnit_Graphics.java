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
import org.junit.Test;
import pathfinder.Arete;
import smartMath.Segment;
import smartMath.Vec2;
import table.Table;
import java.util.ArrayList;
import java.util.HashSet;

public class JUnit_Graphics extends JUnit_Test
{
	/** La JFrame à tester */
	private Window win;

	/** La table */
	private Table table;

	/** Test du debug pathfinding */
	@Test
	public void testWinTable() throws Exception {
		table = container.getService(Table.class);

		ArrayList<Vec2> clics = new ArrayList<>();

		win = new Window(table);

		while(true) {
			clics = win.waitLRClic();
			Thread.sleep(100);
		}
	}
}