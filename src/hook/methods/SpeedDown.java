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

package hook.methods;

import enums.Speed;
import hook.Executable;
import strategie.GameState;

/**
 * Exécutable réduisant la vitesse du robot en cours de mouvement.
 * Implémente le service Executable pour redéfinir la méthode execute.
 * @author CF
 */
public class SpeedDown implements Executable
{

	@Override
	public boolean execute(GameState stateToConsider)
	{
		stateToConsider.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
		return false;
	}

}
