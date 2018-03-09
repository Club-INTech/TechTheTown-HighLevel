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
        mCorrespondenceMap.put(ActuatorOrder.MOVE_LENTGHWISE,ActuatorOrder.MOVE_LENTGHWISE);
        mCorrespondenceMap.put(ActuatorOrder.TURN,ActuatorOrder.TURN);
        mCorrespondenceMap.put(ActuatorOrder.TURN_RIGHT_ONLY,ActuatorOrder.TURN_RIGHT_ONLY);
        mCorrespondenceMap.put(ActuatorOrder.TURN_LEFT_ONLY,ActuatorOrder.TURN_LEFT_ONLY);
        mCorrespondenceMap.put(ActuatorOrder.STOP,ActuatorOrder.STOP);
        mCorrespondenceMap.put(ActuatorOrder.IS_ROBOT_MOVING,ActuatorOrder.IS_ROBOT_MOVING);
        mCorrespondenceMap.put(ActuatorOrder.SEND_POSITION,ActuatorOrder.SEND_POSITION);

        mCorrespondenceMap.put(ActuatorOrder.ENABLE_FORCE_MOVEMENT,ActuatorOrder.ENABLE_FORCE_MOVEMENT);
        mCorrespondenceMap.put(ActuatorOrder.DISABLE_FORCE_MOVEMENT,ActuatorOrder.DISABLE_FORCE_MOVEMENT);
        mCorrespondenceMap.put(ActuatorOrder.SET_TRANSLATION_SPEED,ActuatorOrder.SET_TRANSLATION_SPEED);
        mCorrespondenceMap.put(ActuatorOrder.SET_ROTATIONNAL_SPEED,ActuatorOrder.SET_ROTATIONNAL_SPEED);
        mCorrespondenceMap.put(ActuatorOrder.SET_SPEED,ActuatorOrder.SET_SPEED);

        mCorrespondenceMap.put(ActuatorOrder.SET_X,ActuatorOrder.SET_X);
        mCorrespondenceMap.put(ActuatorOrder.SET_Y,ActuatorOrder.SET_Y);
        mCorrespondenceMap.put(ActuatorOrder.SET_ORIENTATION,ActuatorOrder.SET_ORIENTATION);
        mCorrespondenceMap.put(ActuatorOrder.TURN_LEFT_ONLY,ActuatorOrder.TURN_LEFT_ONLY);
        mCorrespondenceMap.put(ActuatorOrder.SET_POSITION,ActuatorOrder.SET_POSITION);

        mCorrespondenceMap.put(ActuatorOrder.INITIALISE_HOOK,ActuatorOrder.INITIALISE_HOOK);
        mCorrespondenceMap.put(ActuatorOrder.ENABLE_HOOK,ActuatorOrder.ENABLE_HOOK);
        mCorrespondenceMap.put(ActuatorOrder.DISABLE_HOOK,ActuatorOrder.DISABLE_HOOK);

        mCorrespondenceMap.put(ActuatorOrder.ACTIVE_LA_POMPE,ActuatorOrder.ACTIVE_LA_POMPE);
        mCorrespondenceMap.put(ActuatorOrder.DESACTIVE_LA_POMPE,ActuatorOrder.DESACTIVE_LA_POMPE);
        mCorrespondenceMap.put(ActuatorOrder.BAISSE_LE_BRAS_AVANT,ActuatorOrder.BAISSE_LE_BRAS_AVANT);
        mCorrespondenceMap.put(ActuatorOrder.RELEVE_LE_BRAS_AVANT,ActuatorOrder.RELEVE_LE_BRAS_AVANT);
        mCorrespondenceMap.put(ActuatorOrder.OUVRE_LA_PORTE_AVANT,ActuatorOrder.OUVRE_LA_PORTE_AVANT);
        mCorrespondenceMap.put(ActuatorOrder.FERME_LA_PORTE_AVANT,ActuatorOrder.FERME_LA_PORTE_AVANT);
        mCorrespondenceMap.put(ActuatorOrder.OUVRE_LA_PORTE_AVANT_UNPEU,ActuatorOrder.OUVRE_LA_PORTE_AVANT_UNPEU);
        mCorrespondenceMap.put(ActuatorOrder.OUVRE_LA_PORTE_ARRIERE_UNPEU,ActuatorOrder.OUVRE_LA_PORTE_ARRIERE_UNPEU);
        mCorrespondenceMap.put(ActuatorOrder.ACTIVE_ELECTROVANNE_ARRIERE,ActuatorOrder.ACTIVE_ELECTROVANNE_ARRIERE);
        mCorrespondenceMap.put(ActuatorOrder.DESACTIVE_ELECTROVANNE_ARRIERE,ActuatorOrder.DESACTIVE_ELECTROVANNE_ARRIERE);
        mCorrespondenceMap.put(ActuatorOrder.ACTIVE_ELECTROVANNE_AVANT,ActuatorOrder.ACTIVE_ELECTROVANNE_AVANT);
        mCorrespondenceMap.put(ActuatorOrder.DESACTIVE_ELECTROVANNE_AVANT,ActuatorOrder.DESACTIVE_ELECTROVANNE_AVANT);
        mCorrespondenceMap.put(ActuatorOrder.BAISSE_LE_BRAS_ARRIERE,ActuatorOrder.BAISSE_LE_BRAS_ARRIERE);
        mCorrespondenceMap.put(ActuatorOrder.RELEVE_LE_BRAS_ARRIERE,ActuatorOrder.RELEVE_LE_BRAS_ARRIERE);
        mCorrespondenceMap.put(ActuatorOrder.ACTIVE_BRAS_AVANT_POUR_ABEILLE,ActuatorOrder.ACTIVE_BRAS_AVANT_POUR_ABEILLE);
        mCorrespondenceMap.put(ActuatorOrder.OUVRE_LA_PORTE_ARRIERE,ActuatorOrder.OUVRE_LA_PORTE_ARRIERE);
        mCorrespondenceMap.put(ActuatorOrder.FERME_LA_PORTE_ARRIERE,ActuatorOrder.FERME_LA_PORTE_ARRIERE);

        mCorrespondenceMap.put(ActuatorOrder.NO_ASSERV_TRANSLATION,ActuatorOrder.NO_ASSERV_TRANSLATION);
        mCorrespondenceMap.put(ActuatorOrder.NO_ASSERV_ROTATION,ActuatorOrder.NO_ASSERV_ROTATION);
        mCorrespondenceMap.put(ActuatorOrder.ASSERV_TRANSLATION,ActuatorOrder.ASSERV_TRANSLATION);
        mCorrespondenceMap.put(ActuatorOrder.ASSERV_ROTATION,ActuatorOrder.ASSERV_ROTATION);
        mCorrespondenceMap.put(ActuatorOrder.NO_ASSERV_SPEED,ActuatorOrder.NO_ASSERV_SPEED);
        mCorrespondenceMap.put(ActuatorOrder.ASSERV_SPEED,ActuatorOrder.ASSERV_SPEED);
        mCorrespondenceMap.put(ActuatorOrder.DEBUG,ActuatorOrder.DEBUG);
        mCorrespondenceMap.put(ActuatorOrder.MONTLHERY,ActuatorOrder.MONTLHERY);
        mCorrespondenceMap.put(ActuatorOrder.MOVE_FORWARD,ActuatorOrder.MOVE_FORWARD);
        mCorrespondenceMap.put(ActuatorOrder.MOVE_BACKWARD,ActuatorOrder.MOVE_BACKWARD);
        mCorrespondenceMap.put(ActuatorOrder.TURN_RIGHT,ActuatorOrder.TURN_RIGHT);
        mCorrespondenceMap.put(ActuatorOrder.TURN_LEFT,ActuatorOrder.TURN_LEFT);
        mCorrespondenceMap.put(ActuatorOrder.SSTOP,ActuatorOrder.SSTOP);


        mCorrespondenceMap.put(ActuatorOrder.DIST_US_BACK_LEFT,ActuatorOrder.DIST_US_BACK_LEFT);
        mCorrespondenceMap.put(ActuatorOrder.DIST_US_BACK_RIGHT,ActuatorOrder.DIST_US_BACK_RIGHT);
        mCorrespondenceMap.put(ActuatorOrder.DIST_US_FRONT_RIGHT,ActuatorOrder.DIST_US_FRONT_RIGHT);
        mCorrespondenceMap.put(ActuatorOrder.DIST_US_FRONT_LEFT,ActuatorOrder.DIST_US_FRONT_LEFT);
        mCorrespondenceMap.put(ActuatorOrder.JUMPER_STATE,ActuatorOrder.JUMPER_STATE);

        mCorrespondenceMap.put(ActuatorOrder.ETAT_CONTACTEUR1,ActuatorOrder.ETAT_CONTACTEUR1);
        mCorrespondenceMap.put(ActuatorOrder.ETAT_CONTACTEUR2,ActuatorOrder.ETAT_CONTACTEUR2);
        mCorrespondenceMap.put(ActuatorOrder.ETAT_CONTACTEUR3,ActuatorOrder.ETAT_CONTACTEUR3);

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
