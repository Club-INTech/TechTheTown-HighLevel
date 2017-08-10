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

package exceptions;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExecuteException extends Exception
{
	private static final long serialVersionUID = 6657089731926485052L;
	/**
	 * Exception levée lorsqu'un script échoue dans sa méthode Execute
	 * @author Théo
	 */
	
	// La raison du probleme dans l'execute
	Exception exception;
	
	public ExecuteException(Exception e)
	{
		super();
		exception=e;
	}
	
	public ExecuteException(String m, Exception e)
	{
		super(m);
		exception=e;
	}
	
	public String logStack()
	{
		StringWriter sw = new StringWriter();
		this.printStackTrace(new PrintWriter(sw));
		
		String exceptionAsString = sw.toString();	
		exceptionAsString = exceptionAsString.replaceAll("(\r\n|\n\r|\r|\n)", " -> ");
		
		return exceptionAsString;
	}
	
	public Exception getExceptionThrownByExecute()
	{
		return exception;
	}
	
	public boolean compareInitialException(Exception otherException)
	{
		return exception.getClass().equals(otherException.getClass());
	}
}
