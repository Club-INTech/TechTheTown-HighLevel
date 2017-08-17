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

import java.util.HashMap;
import java.util.Map;

/**
 * Map contenant un actionneur pour clé, et son symétrique pour valeur
 * @author Etienne
 *
 */
public class SymmetrizedActuatorOrderMap
{
	/** Map contenant un actionneur pour clé, et son symétrique pour valeur */
    Map<ActuatorOrder, ActuatorOrder> mCorrespondenceMap = new HashMap<ActuatorOrder, ActuatorOrder>();
    
    /**
     * construit la map de correspondances
     */
    public SymmetrizedActuatorOrderMap()
    {
    	// exemple : mCorrespondenceMap.put(ActuatorOrder.CLOSE_DOOR_RIGHT, ActuatorOrder.CLOSE_DOOR_LEFT);
    	// TODO : ajouter les ordres symétriques*

    }
    
    /**
     * 
     * @param order l'actionneur à symétriser
     * @return l'actionneur à symétriser
     */
    public ActuatorOrder getSymmetrizedActuatorOrder(ActuatorOrder order)
    {
    	return mCorrespondenceMap.get(order);
    }
}
