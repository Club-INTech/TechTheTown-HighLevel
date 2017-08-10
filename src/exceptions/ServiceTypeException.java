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

/**
 * 
 */
package exceptions;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 
 * Exception lancée lorsque l'on demande une information a un Service dont le type ne permet pas de fournir l'information demandée
 *
 */
public class ServiceTypeException extends Exception
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8462580190897214226L;

	/**
	 * 
	 */
	public ServiceTypeException() 
	{
		super();
	}

	/**
	 * @param message
	 */
	public ServiceTypeException(String message) 
	{
		super(message);
	}

	/**
	 * @param cause
	 */
	public ServiceTypeException(Throwable cause) 
	{
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ServiceTypeException(String message, Throwable cause) 
	{
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public ServiceTypeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) 
	{
		super(message, cause, enableSuppression, writableStackTrace);
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
