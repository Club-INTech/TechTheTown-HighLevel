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

package hook.types;

import hook.Hook;
import pfg.config.Config;
import smartMath.Vec2;
import strategie.GameState;
import utils.Log;

/**
 * déclenchement avec condition sur position ET orientation
 * @author Pingu
 */

public class HookIsPositionAndOrientationCorrect extends Hook
{

	/**centre du cercle qui nous interesse pour activer le hook*/
	private Vec2 mPoint;
	
	/**orientation de declenchement du hook */
	private float mOrientation;
		
	/** Tolerance qu'on accorde au robot pour activer le hook, en mm*/ 
	private float mTolerancyPoint;
		
	/**Tolerance en orientation qu'on accorde au robot pour activer le hook, en miliradians */ 
	private float mTolerancyOrientation;
	
	public HookIsPositionAndOrientationCorrect(Config config, Log log, GameState realState,
											   Vec2 point, float orientation, float tolerancyPoint, float tolerancyOrientation)
	{
		super(config, log, realState);
		
		this.mPoint=point;
		this.mOrientation =orientation;
		this.mTolerancyPoint=tolerancyPoint;
		this.mTolerancyOrientation=tolerancyOrientation;
		
	}

	@Override
	public boolean evaluate()
	{
		if(  mState.robot.getPosition().distance(mPoint) < mTolerancyPoint ) //verification de la distance au point
		{
			if(Math.abs(mState.robot.getOrientationFast()-mOrientation) < mTolerancyOrientation)// verification de l'orientation
			{
				log.debug("en position ("+mState.robot.getPositionFast().getX()+", "+mState.robot.getPositionFast().getY()+") et orientation "+mState.robot.getOrientationFast()+" au trigger du Hook de position et d'orientation");
				return trigger();
			}
		}
		return false;
	}
}
