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

import smartMath.Vec2;
import table.obstacles.ObstacleCircular;

/**
 * Created by shininisan on 19/10/16.
 */
public class Arrete {
    public Noeud depart;
    public Noeud arrivee;
    public double cout;
    public boolean isUpdated= true;
    public int timeToLive=-1;

    public Arrete(Noeud n1, Noeud n2)
    {
        depart=n1;
        arrivee=n2;
        this.cout=0;
        this.calcCout();

    }
    public Arrete(Noeud n1, Noeud n2,int TTL)
    {
        depart=n1;
        arrivee=n2;
        this.cout=0;
        this.calcCout();
        this.timeToLive=TTL;
    }

    /**
     * détruit le lien entre arrivee et départ si l'obstacle le bloque
     * @param obstacle obstacle potentiellement bloquant
     */
    public boolean isBloquant(ObstacleCircular obstacle) // calcul de l'intersection de la ligne et des objets
    {



            Vec2 da= new Vec2(depart.position.getX()-arrivee.position.getX(),depart.position.getY()-arrivee.position.getY());
            Vec2 dc= new Vec2(depart.position.getX()-obstacle.getPosition().getX(),depart.position.getY()-obstacle.getPosition().getY());
            double distcentre= (double)(da.dot(dc))/dc.length();
            //... découverte de la fonction isinobstacle
            if (Math.abs(distcentre)< obstacle.getRadius())
            {
                // on le détache de ses noeuds
                this.depart.lArretes.remove(this);
                this.arrivee.lArretes.remove(this);
                return true;
            }
        return false;

    }

   /**
     * actualise le cout de l'arrête
     */
    public void calcCout()
    {
        this.cout=new Vec2 (this.depart.position.getX()-this.arrivee.position.getX(),this.depart.position.getY()-this.arrivee.position.getY()).length();
    }
}
