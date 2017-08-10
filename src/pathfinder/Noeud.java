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

import java.util.ArrayList;


public class Noeud
{

    public Vec2 position;
    public double distArrivee; // distance vers l'arrivee
    public ArrayList <Arrete> lArretes;
    public double sommeDepart;
    public Noeud noeudPrecedent;

    /**
     * Constructeur Noeud vide
     */
    public Noeud()
    {
        this.lArretes=new ArrayList<Arrete>();
        this.position=new Vec2();
        this.distArrivee =100000000;
        this.sommeDepart =100000000;
        this.noeudPrecedent=null;
    }
    /**
     * Constructeur
     * @param g graphe
     * @param position
     */

    public Noeud(Graphe g, Vec2 position)
    {
        this.position=position;
        this.distArrivee =100000000;
        this.sommeDepart =100000000;
        this.lArretes=new ArrayList<Arrete>();
        this.noeudPrecedent=null;
    }


    /**
     * Constructeur de copy
     * @param n1 noeud à copier
     */
    public Noeud (Noeud n1)
    {
        this.position=n1.position;
        this.distArrivee =n1.distArrivee;
        this.lArretes=n1.lArretes;
        this.sommeDepart =n1.sommeDepart;
        this.noeudPrecedent=n1.noeudPrecedent;
    }

    /**
     * Créée l'arête entre this et le noeud fourni dans un seul sens

     * @param autre Noeud destination
     */
    public Arrete attacheLien(Noeud autre)
    {
        Arrete b=new Arrete(this,autre);
        b.calcCout();
        this.lArretes.add(b);
        return b;
    }

    /**
     * Actualise la distance euclidienne jusqu'à l'arrivée
     * @param arrivee noeud d'arrivée
     */
    public void distHeuristique(Noeud arrivee)
    {
        this.distArrivee =  this.position.minusNewVector(arrivee.position).length();
    }

    /**
     * Enlève 1 au TTL de chaque arrête et supprime celles qui sont à 0.
     */
    public void actuTTL()
    {
        for (int i=0;i<lArretes.size();i++)
        {
            lArretes.get(i).timeToLive--;
            if(lArretes.get(i).timeToLive==0)
            {
                this.lArretes.remove(i);
            }

        }
    }
}
