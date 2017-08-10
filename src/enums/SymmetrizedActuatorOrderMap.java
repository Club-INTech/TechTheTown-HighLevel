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

/*			 ____________________
 * 		   *|                    |*
 *		   *|    Pelle T-3000    |*
 *		   *|____________________|*
 */

    //Bras Pelleteuse

        mCorrespondenceMap.put(ActuatorOrder.DEPLOYER_PELLETEUSE_BAS, ActuatorOrder.DEPLOYER_PELLETEUSE_BAS);
        mCorrespondenceMap.put(ActuatorOrder.LIVRAISON_PELLETEUSE, ActuatorOrder.LIVRAISON_PELLETEUSE);
        mCorrespondenceMap.put(ActuatorOrder.REPLIER_PELLETEUSE, ActuatorOrder.REPLIER_PELLETEUSE);
        mCorrespondenceMap.put(ActuatorOrder.MED_PELLETEUSE, ActuatorOrder.MED_PELLETEUSE);
        mCorrespondenceMap.put(ActuatorOrder.LIVRAISON_PELLETEUSE, ActuatorOrder.LIVRAISON_PELLETEUSE);
        mCorrespondenceMap.put(ActuatorOrder.DEPLOYER_PELLETEUSE, ActuatorOrder.DEPLOYER_PELLETEUSE);

    //Pelle

        mCorrespondenceMap.put(ActuatorOrder.PRET_PELLE, ActuatorOrder.PRET_PELLE);
        mCorrespondenceMap.put(ActuatorOrder.P1, ActuatorOrder.P1);
        mCorrespondenceMap.put(ActuatorOrder.P2, ActuatorOrder.P2);
        mCorrespondenceMap.put(ActuatorOrder.PREND_PELLE, ActuatorOrder.PREND_PELLE);
        mCorrespondenceMap.put(ActuatorOrder.LIVRE_PELLE, ActuatorOrder.LIVRE_PELLE);
        mCorrespondenceMap.put(ActuatorOrder.RANGE_PELLE, ActuatorOrder.RANGE_PELLE);
        mCorrespondenceMap.put(ActuatorOrder.PELLE_REASSERV, ActuatorOrder.PELLE_REASSERV);

/*			 ___________________
 * 		   *|                   |*
 *		   *|      AM-SSV2      |*
 *		   *|___________________|*
 */
    //Attrape-Module
        mCorrespondenceMap.put(ActuatorOrder.REPLI_CALLE_D, ActuatorOrder.REPLI_CALLE_G);
        mCorrespondenceMap.put(ActuatorOrder.REPLI_CALLE_G, ActuatorOrder.REPLI_CALLE_D);
        mCorrespondenceMap.put(ActuatorOrder.LIVRE_CALLE_G, ActuatorOrder.LIVRE_CALLE_D);
        mCorrespondenceMap.put(ActuatorOrder.LIVRE_CALLE_D, ActuatorOrder.LIVRE_CALLE_G);

        mCorrespondenceMap.put(ActuatorOrder.REPOS_CALLE_D, ActuatorOrder.REPOS_CALLE_G);
        mCorrespondenceMap.put(ActuatorOrder.REPOS_CALLE_G, ActuatorOrder.REPOS_CALLE_D);


        mCorrespondenceMap.put(ActuatorOrder.LEVE_ASC, ActuatorOrder.LEVE_ASC);
        mCorrespondenceMap.put(ActuatorOrder.BAISSE_ASC, ActuatorOrder.BAISSE_ASC);

        mCorrespondenceMap.put(ActuatorOrder.REPOS_LARGUEUR, ActuatorOrder.REPOS_LARGUEUR);
        mCorrespondenceMap.put(ActuatorOrder.POUSSE_LARGUEUR, ActuatorOrder.POUSSE_LARGUEUR);
	mCorrespondenceMap.put(ActuatorOrder.POUSSE_LARGUEUR_LENT, ActuatorOrder.POUSSE_LARGUEUR_LENT);

        mCorrespondenceMap.put(ActuatorOrder.REPOS_ATTRAPE_D, ActuatorOrder.REPOS_ATTRAPE_G);
        mCorrespondenceMap.put(ActuatorOrder.MID_ATTRAPE_D, ActuatorOrder.MID_ATTRAPE_G);
        mCorrespondenceMap.put(ActuatorOrder.PREND_MODULE_D, ActuatorOrder.PREND_MODULE_G);

        mCorrespondenceMap.put(ActuatorOrder.REPOS_ATTRAPE_G, ActuatorOrder.REPOS_ATTRAPE_D);
        mCorrespondenceMap.put(ActuatorOrder.MID_ATTRAPE_G, ActuatorOrder.MID_ATTRAPE_D);
        mCorrespondenceMap.put(ActuatorOrder.PREND_MODULE_G, ActuatorOrder.PREND_MODULE_D);

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
