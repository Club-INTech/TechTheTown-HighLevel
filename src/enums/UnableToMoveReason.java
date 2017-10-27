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

/**
 * Principales raisons bloquant le robot.
 * A utiliser avec les exceptions UnableToMoveException
 */
public enum UnableToMoveReason
{
	OBSTACLE_DETECTED("o"), // obstacle vu par les capteurs
	PHYSICALLY_BLOCKED("p"), // blocage physique (bord de table, décor...)
	;

	/** Ce que le LL envoie pour spécifier l'UnableToMoveException */
	private String serialOrder;

	/** Constructeur */
	UnableToMoveReason(String serialOrder){
		this.serialOrder = serialOrder;
	}

	public String getSerialOrder() {
		return serialOrder;
	}
}
