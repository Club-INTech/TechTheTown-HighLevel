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

package pathfinder;

import container.Service;
import smartMath.Vec2;


/**
 * Pathfinding du robot ! Contient l'algorithme
 */
public class Pathfinding implements Service {
    private Pathfinding() {}

    @Override
    public void updateConfig() {
    }


    private class Noeud{
        protected Vec2 position;
        int heuristique;


        private Noeud( Vec2 position, int heuristique ){
            this.position=position;
            this.heuristique=heuristique;


        }

    }

    Noeud noeud_depart = new Noeud( new Vec2( 1200, 400),0);
    

}