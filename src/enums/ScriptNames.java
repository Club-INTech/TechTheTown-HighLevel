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
 * Enumération des noms des différents scripts.
 * @author pf
 *
 */

public enum ScriptNames
{
    //Script exemple
    CLOSE_DOORS("CloseDoors"),

	// TODO : Rajouter les noms des scripts
    ACTIVATION_PANNEAU_DOMOTIQUE("Panneau"),
    ACTIVE_ABEILLE("Abeille"),
    DEPOSE_CUBES("DeposeCubes"),
    MATCH_SCRIPT("MatchScript"),
    TAKE_CUBES("TakeCubes"),
    SCRIPT_HOMOLOGATION("Homologation"),
    RECALAGE("Recalage")
    ;

    private String name;
    ScriptNames(String name){
        this.name=name;
    }

    public String getName(){
        return this.name;
    }
}
