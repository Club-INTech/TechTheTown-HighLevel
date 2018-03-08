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
        mCorrespondenceMap.put(ActuatorOrder.ACTIVE_LA_POMPE,ActuatorOrder.ACTIVE_LA_POMPE);
        mCorrespondenceMap.put(ActuatorOrder.DESACTIVE_LA_POMPE,ActuatorOrder.DESACTIVE_LA_POMPE);
        mCorrespondenceMap.put(ActuatorOrder.BAISSE_LE_BRAS_AVANT,ActuatorOrder.BAISSE_LE_BRAS_AVANT);
        mCorrespondenceMap.put(ActuatorOrder.RELEVE_LE_BRAS_AVANT,ActuatorOrder.RELEVE_LE_BRAS_AVANT);
        mCorrespondenceMap.put(ActuatorOrder.OUVRE_LA_PORTE_AVANT,ActuatorOrder.OUVRE_LA_PORTE_AVANT);
        mCorrespondenceMap.put(ActuatorOrder.FERME_LA_PORTE_AVANT,ActuatorOrder.FERME_LA_PORTE_AVANT);
        mCorrespondenceMap.put(ActuatorOrder.OUVRE_LA_PORTE_AVANT_UNPEU,ActuatorOrder.OUVRE_LA_PORTE_AVANT_UNPEU);
        mCorrespondenceMap.put(ActuatorOrder.OUVRE_LA_PORTE_ARRIERE_UNPEU,ActuatorOrder.OUVRE_LA_PORTE_ARRIERE_UNPEU);
        mCorrespondenceMap.put(ActuatorOrder.ACTIVE_ELECTROVANNE_ARRIERE,ActuatorOrder.ACTIVE_ELECTROVANNE_ARRIERE);
        mCorrespondenceMap.put(ActuatorOrder.DESACTIVE_ELECTROVANNE_ARRIERE,ActuatorOrder.ACTIVE_ELECTROVANNE_ARRIERE);
        mCorrespondenceMap.put(ActuatorOrder.ACTIVE_ELECTROVANNE_AVANT,ActuatorOrder.ACTIVE_ELECTROVANNE_AVANT);
        mCorrespondenceMap.put(ActuatorOrder.DESACTIVE_ELECTROVANNE_AVANT,ActuatorOrder.ACTIVE_ELECTROVANNE_AVANT);
        mCorrespondenceMap.put(ActuatorOrder.BAISSE_LE_BRAS_ARRIERE,ActuatorOrder.BAISSE_LE_BRAS_ARRIERE);
        mCorrespondenceMap.put(ActuatorOrder.RELEVE_LE_BRAS_ARRIERE,ActuatorOrder.RELEVE_LE_BRAS_ARRIERE);
        mCorrespondenceMap.put(ActuatorOrder.ACTIVE_BRAS_AVANT_POUR_ABEILLE,ActuatorOrder.ACTIVE_BRAS_AVANT_POUR_ABEILLE);
        mCorrespondenceMap.put(ActuatorOrder.OUVRE_LA_PORTE_ARRIERE,ActuatorOrder.OUVRE_LA_PORTE_ARRIERE);
        mCorrespondenceMap.put(ActuatorOrder.FERME_LA_PORTE_ARRIERE,ActuatorOrder.FERME_LA_PORTE_ARRIERE);

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
