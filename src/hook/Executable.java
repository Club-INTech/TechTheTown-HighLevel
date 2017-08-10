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

package hook;

import strategie.GameState;

/**
 * Interface utilisée pour passer des méthodes à Callback.
 * Il faut créer une classe (dans le package hook.methods) implémentant cette interface par méthode d'intérêt.
 * Il y a alors, dans l'implémentation, des attributs pour les arguments qui sont initialisés par le constructeur,
 * de manière à ce que execute reste sans argument.
 * @author pf
 */

public interface Executable
{
	/**
	 * La méthode qui sera exécutée par le hook, elle est à override dans chaque classe implémentant Executable.
	 * C'est dans cette méthode que seront définies les actions effectuées par le robot lors d'un déplacement. 
	 * @param stateToConsider 
     * @return un booléen qui renseignera sur le fait que la méthode fait ou non bouger le robot
	 */
    boolean execute(GameState stateToConsider);
}
