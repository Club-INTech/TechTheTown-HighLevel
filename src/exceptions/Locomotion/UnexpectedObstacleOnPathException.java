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

package exceptions.Locomotion;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Exception levée en cas de détection d'un ennemi proche (par les capteurs, ultrason, infrarouge, etc.)
 * @author pf
 *
 */
public class UnexpectedObstacleOnPathException extends Exception
{

	private static final long serialVersionUID = -3791360446545658528L;

	public UnexpectedObstacleOnPathException()
	{
		super();
	}
	
	public UnexpectedObstacleOnPathException(String m)
	{
		super(m);
	}

	
	public String logStack()
	{
		StringWriter sw = new StringWriter();
		this.printStackTrace(new PrintWriter(sw));
		
		String exceptionAsString = sw.toString();	
		exceptionAsString = exceptionAsString.replaceAll("(\r\n|\n\r|\r|\n)", " -> ");
		
		return exceptionAsString;
	}
}
