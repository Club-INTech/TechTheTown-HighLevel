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

import enums.UnableToMoveReason;
import smartMath.Vec2;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Problème générique de déplacement du robot, que ce soit a cause d'un robot ennemi
 * (détecté par les capteurs) qui bloque le passage, ou d'un bloquage mécanique (type mur)
 * @author pf, theo
 */
public class UnableToMoveException extends Exception
{
	/** La position où on voulait aller au moment de l'exception */
	private Vec2 aim;
	
	/** La raison du blocage */
	private UnableToMoveReason reason;
	
	private static final long serialVersionUID = -8139322860107594266L;

	/**
	 * @param aim  position où on voulait aller au moment de l'exception
	 * @param reason raison de l'exception
	 */
	public UnableToMoveException(Vec2 aim, UnableToMoveReason reason)
	{
		super();
		this.aim = aim;
		this.reason=reason;
	}
	
	/**
	 * @param m 
	 * @param aim  position où on voulait aller au moment de l'exception
	 * @param reason raison de l'exception
	 */
	public UnableToMoveException(String m, Vec2 aim, UnableToMoveReason reason)
	{
		super(m);
		this.aim = aim;
		this.reason=reason;
	}
	
	public String logStack()
	{
		StringWriter sw = new StringWriter();
		this.printStackTrace(new PrintWriter(sw));
		
		String exceptionAsString = sw.toString();	
		exceptionAsString = exceptionAsString.replaceAll("(\r\n|\n\r|\r|\n)", " -> ");
		
		return exceptionAsString;
	}

	public Vec2 getAim(){
		return this.aim;
	}

	public UnableToMoveReason getReason() {
		return reason;
	}
}
 