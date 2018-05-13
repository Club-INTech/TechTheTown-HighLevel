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

package graphics;

import smartMath.Vect;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Gestion de la souris
 * @author Etienne, rem
 */
public class Mouse implements MouseListener
{
	/** Position des clics */
	private Vect rightClicPosition;
	private Vect middleClicPosition;
	private Vect leftClicPosition;

	/** Panel */
	private TablePanel panel;

	/** Constructeur... */
	public Mouse(TablePanel pan)
	{
		panel = pan;
		rightClicPosition = null;
		middleClicPosition = null;
		leftClicPosition = null;
	}
	
    @Override
    public void mousePressed(MouseEvent e)
    {
        if (e.getButton()==MouseEvent.BUTTON1)
        {
        	leftClicPosition = new Vect();
        	leftClicPosition.setX(e.getX());
        	leftClicPosition.setY(e.getY());
        	changeRefToTable(leftClicPosition);
        }
        if (e.getButton()==MouseEvent.BUTTON2)
        {
			middleClicPosition = new Vect();
			middleClicPosition.setX(e.getX());
			middleClicPosition.setY(e.getY());
			changeRefToTable(middleClicPosition);
        }
        if (e.getButton()==MouseEvent.BUTTON3)
        {
        	rightClicPosition = new Vect();
			rightClicPosition.setX(e.getX());
			rightClicPosition.setY(e.getY());
			changeRefToTable(rightClicPosition);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    /** Change de référentiel (display -> table)
	 * @param position */
    private void changeRefToTable(Vect position){
		position.setX((int)(position.getX()*10/3.0) - 1500);
		position.setY((int)(-position.getY()*40/13.0) + 2000);
	}

	/** Set les clics à 0 */
	public void resetClics(){
		rightClicPosition = null;
		leftClicPosition = null;
		middleClicPosition = null;
	}

	/** Getters */
	public Vect getRightClicPosition() {
		return rightClicPosition;
	}
	public Vect getMiddleClicPosition() {
		return middleClicPosition;
	}
	public Vect getLeftClicPosition() {
		return leftClicPosition;
	}
}