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

//TODO : refactor

package graphics;

import robot.Robot;
import scripts.ScriptManager;
import strategie.GameState;
import table.Table;

import javax.swing.*;

/**
 * interface graphique de debugage
 * @author Etienne
 *
 */
public class Window extends JFrame
{
	/** numéro de serialisation	 */
	private static final long serialVersionUID = -3140220993568124763L;

	private TablePanel mPanel;
	private SensorPanel mSensorPanel;
	private Mouse mMouse;
	private Keyboard mKeyboard;
	
	public Window(Table table, GameState mRobot, ScriptManager scriptManager)
	{
		this.setVisible(true);
		this.setTitle("Table");
	    this.setSize(600, 400);
	    this.setLocationRelativeTo(null);
	    
	    mPanel = new TablePanel(table, mRobot.robot);
	    this.setContentPane(mPanel);
	    
	    mMouse = new Mouse(mPanel);
	    addMouseListener(mMouse);
	    
	    mKeyboard = new Keyboard(mRobot, scriptManager);
	    addKeyListener(mKeyboard);
	}

	public Window(Table table)
	{
		this.setVisible(true);
		this.setTitle("table");
		this.setSize(600, 400);
		this.setLocationRelativeTo(null);

		mPanel = new TablePanel(table);
		this.setContentPane(mPanel);

		mMouse = new Mouse(mPanel);
		addMouseListener(mMouse);

	}
	
	public Window()
	{
		this.setVisible(true);
		this.setTitle("sensorValues");
	    this.setSize(1200, 800);
	    this.setLocationRelativeTo(null);
	    
	    mSensorPanel = new SensorPanel();
	    this.setContentPane(mSensorPanel);
	}
	
	/**
	 * 
	 * @return le panneau
	 */
	public TablePanel getPanel()
	{
		return mPanel;
	}
	
	public void drawInt(int value1, int value2, int value3, int value4)
	{
		mSensorPanel.drawInteger(new Integer(value1), new Integer(value2), new Integer(value3), new Integer(value4));
	}
	
	public Mouse getMouse()
	{
		return mMouse;
	}

	public Keyboard getKeyboard()
	{
		return mKeyboard;
	}
}
