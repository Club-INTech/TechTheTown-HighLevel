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

package debug;

import smartMath.Vect;
import smartMath.VectCart;

/**
 * Tous les paramètres pour une hyperbole
 * @author pf
 *
 */

public class Hyperbola {

	private static Vect[] balises;
    private final static double speedOfSound = 0.34; // in mm/µs

	static
	{
		balises = new Vect[3];
		balises[0] = new VectCart(-1500, 0);
		balises[1] = new VectCart(-1500, 2000);
		balises[2] = new VectCart(1500, 1000);
	}
	
	public Vect p1 = null, p2 = null;
	public double delta; // en mm
	
	public Hyperbola(Vect p1, Vect p2, double delta)
	{
		this.p1 = p1;
		this.p2 = p2;
		this.delta = delta;
	}
	
	/**
	 * 0 est le couple 12
	 * 1 est le couple 02
	 * 2 est le couple 01
	 * @param couple
	 * @param delta
	 */
	public Hyperbola(int couple, double delta)
	{
		for(int j = 0; j < 3; j++)
		{
			if(j != couple)
			{
				if(p1 == null)
					p1 = balises[j];
				else
				{
					p2 = balises[j];
					break;
				}
			}
		}
		this.delta = delta * speedOfSound;
	}
	
}
