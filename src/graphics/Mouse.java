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

import smartMath.Vec2;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * gestion de la souris
 * @author Etienne
 *
 */
public class Mouse implements MouseListener
{
	private Vec2 mRightClickPosition;
	private Vec2 mMiddleClickPosition;
	private Vec2 mLeftClickPosition;
	private boolean mHasClicked;
	private TablePanel mPanel;
	private boolean mHasClickedRight;
	
	public Mouse(TablePanel pan)
	{
		mPanel = pan;
		mHasClicked = false;
		mHasClickedRight = false;
		mRightClickPosition = new Vec2(0, 0);
		mMiddleClickPosition = new Vec2(0, 0);
		mLeftClickPosition = new Vec2(0, 0);
	}
	
    @Override
    public void mousePressed(MouseEvent e)
    {
    	mHasClicked = true;
        if (e.getButton()==MouseEvent.BUTTON1)
        {
        	mLeftClickPosition.setX((e.getX()/* - 8*/) * 3000 / mPanel.getWidth() - 1500); // mettre 0 au lieu de 8 sous linux
        	mLeftClickPosition.setY((-e.getY() + 31) * 2000 / mPanel.getHeight() + 2000); // mettre 0 au lieu de 31 sous windows
        }
        if (e.getButton()==MouseEvent.BUTTON2)
        {
        	mMiddleClickPosition.setX((e.getX()/* - 8*/) * 3000 / mPanel.getWidth() - 1500); // mettre 0 au lieu de 8 sous linux
        	mMiddleClickPosition.setY((-e.getY() + 31) * 2000 / mPanel.getHeight() + 2000); // mettre 0 au lieu de 31 sous windows
        }
        if (e.getButton()==MouseEvent.BUTTON3)
        {
        	mRightClickPosition.setX((e.getX()/* - 8*/) * 3000 / mPanel.getWidth() - 1500); // mettre 0 au lieu de 8 sous linux
        	mRightClickPosition.setY((-e.getY() + 31) * 2000 / mPanel.getHeight() + 2000); // mettre 0 au lieu de 31 sous windows
			mHasClickedRight = true;

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
    
	public Vec2 getRightClickPosition()
	{
		return mRightClickPosition;
	}
	
	public Vec2 getMiddleClickPosition()
	{
		return mMiddleClickPosition;
	}
	
	public Vec2 getLeftClickPosition()
	{
		return mLeftClickPosition;
	}
	
	public boolean hasClicked()
	{
		if(mHasClicked)
		{
			mHasClicked = false;
            mHasClickedRight = false;
			return true;
		}
		return false;
	}

	public boolean hasClickedRight()
	{
		if(mHasClickedRight)
		{
			mHasClickedRight = false;
			return true;
		}
		return false;
	}

    public void resetHasClicked()
    {
        mHasClicked = false;
    }

}