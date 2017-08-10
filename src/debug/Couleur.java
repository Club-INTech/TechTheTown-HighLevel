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

package debug;

import java.awt.*;

/**
 * Définition de quelques couleurs
 * @author pf
 *
 */
public enum Couleur
{

	BLANC(255, 255, 255),
	NOIR(0, 0, 0),
	BLEU(0, 0, 200),
	JAUNE(200, 200, 0),
	ROUGE(200, 0, 0),
	VERT(0, 200, 0),
	VIOLET(200, 0, 200);
	
	private static final int alpha = 150;
	public final Color couleur;
	
	Couleur(int r, int g, int b, int a)
	{
		this.couleur = new Color(r,g,b,a);
	}

	Couleur(int r, int g, int b)
	{
		this.couleur = new Color(r,g,b,alpha);
	}
}
