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

import java.util.Comparator;

/**
 * Created by shininisan on 03/11/16.
 */

public class ComparaNoeud implements Comparator<Noeud> {
 private double translationSpeed;
    public ComparaNoeud(double translationSpeed)
    {
        this.translationSpeed=translationSpeed;
    }
    /**
     * Opérateur utilisé dans la Priority queue de l'A*. Inclue l'heuristique. noeud1>t1 => résultat positif noeud1<t1 => résultat négatif
     * @param noeud le premier noeud
     * @param t1 le second noeud
     * @return la soustraction des deux comprenant la distance  l'arrivée
     */

    @Override
    public int compare(Noeud noeud, Noeud t1) {
        return (int)(noeud.sommeDepart +noeud.distArrivee/translationSpeed - t1.sommeDepart -t1.distArrivee/translationSpeed);
    }

    @Override
    public boolean equals(Object o) {
        return this==o;
    }
}
