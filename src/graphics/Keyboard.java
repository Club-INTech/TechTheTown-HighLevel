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

import enums.ActuatorOrder;
import enums.TurningStrategy;
import scripts.ScriptManager;
import strategie.GameState;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Gestionnaire des actions clavier pour l'interface graphique, ajoutez vos actions aux blocks correspondants
 * @author etienne, discord, florian
 */
public class Keyboard implements KeyListener {
	private GameState mRobot;
	private ScriptManager scriptManager;
	private TurningStrategy turningStr = TurningStrategy.FASTEST;
	private boolean modeActual = false;
	
	public Keyboard(GameState robot, ScriptManager scriptManager)
	{
		mRobot= robot;
		this.scriptManager = scriptManager;
	}

	int isUpPressed = 0;
	int isDownPressed = 0;
	int isLeftPressed = 0;
	int isRightPressed = 0;

	boolean isUpPressedb;
	boolean isDownPressedb;
	boolean isLeftPressedb;
	boolean isRightPressedb;
	boolean isApressed;

	boolean isKpressed;
	boolean isPpressed;

	boolean isWpressed;
	boolean isXpressed;
	boolean isCpressed;
	boolean isVpressed;
	int lastEvent;

	void doThat() {
		if(/*isUpPressed < 15 &&*/ isUpPressedb)
		{
			try
			{
				mRobot.robot.useActuator(ActuatorOrder.MOVE_FORWARD, false);
			}
			catch(Exception exception)
			{
				System.out.println("ça marche pas bien trololo");
			}
		}
		else if(/*isDownPressed < 15 &&*/ isDownPressedb)
		{
			try
			{
				mRobot.robot.useActuator(ActuatorOrder.MOVE_BACKWARD, false);
			}
			catch(Exception exception)
			{
				System.out.println("ça marche pas bien trololo");
			}
		}
		else if(/*isLeftPressed < 15 &&*/ isLeftPressedb)
		{
			try{
				mRobot.robot.useActuator(ActuatorOrder.TURN_LEFT, false);
			}catch(Exception e)
			{
				e.printStackTrace();
				System.out.println("ça marche pas bien trololo");
			}
		}
		else if( /*isRightPressed < 15 && */isRightPressedb)
		{
			try
			{
				mRobot.robot.useActuator(ActuatorOrder.TURN_RIGHT, false);
			}
			catch(Exception exception)
			{
				System.out.println("ça marche pas bien trololo");
			}
		}else {
			release();
		}

	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		if (e.getKeyCode() != lastEvent) {
			switch (e.getKeyCode()) {
				case KeyEvent.VK_UP:
					isUpPressed++;
					isUpPressedb = true;
					lastEvent = e.getKeyCode();
					break;
				case KeyEvent.VK_DOWN:
					isDownPressed++;
					isDownPressedb = true;
					lastEvent = e.getKeyCode();
					break;
				case KeyEvent.VK_LEFT:
					isLeftPressed++;
					isLeftPressedb = true;
					lastEvent = e.getKeyCode();
					break;

				case KeyEvent.VK_A:
					isApressed=true;
					lastEvent = e.getKeyCode();
					break;
				case KeyEvent.VK_K:
					isKpressed=true;
					lastEvent = e.getKeyCode();
					break;
				case KeyEvent.VK_P:
					isPpressed = true;
					lastEvent = e.getKeyCode();
					break;
				case KeyEvent.VK_W:
					isWpressed = true;
					lastEvent = e.getKeyCode();
					break;
				case KeyEvent.VK_X:
					isXpressed = true;
					lastEvent = e.getKeyCode();
					break;
				case KeyEvent.VK_C:
					isCpressed = true;
					lastEvent = e.getKeyCode();
					break;
				case KeyEvent.VK_V:
					isVpressed = true;
					lastEvent = e.getKeyCode();
					break;
			}
			doThat();
		}
	}

	void release(){
		try
		{
			mRobot.robot.useActuator(ActuatorOrder.SSTOP,false);
		}catch(Exception exception)
		{
			System.out.println("ça marche pas bien trololo");
		}
	}


	@Override
	public void keyReleased(KeyEvent e)
	{
		switch (e.getKeyCode())
		{
			case KeyEvent.VK_UP:
				lastEvent = 0;isUpPressedb = false;break;
			case KeyEvent.VK_DOWN:
				lastEvent = 0;isDownPressedb = false;break;
			case KeyEvent.VK_LEFT:
				lastEvent = 0;isLeftPressedb = false;break;
			case KeyEvent.VK_RIGHT:
				lastEvent = 0;isRightPressedb = false;break;

			case KeyEvent.VK_A:
				lastEvent = 0;isApressed = false;break;
			case KeyEvent.VK_K:
				lastEvent = 0;isKpressed = false;break;
			case KeyEvent.VK_P:
				lastEvent = 0;isPpressed = false;break;
			case KeyEvent.VK_W:
				lastEvent = 0;isWpressed = false;break;
			case KeyEvent.VK_X:
				lastEvent = 0;isXpressed = false;break;
			case KeyEvent.VK_C:
				lastEvent = 0;isCpressed = false;break;
			case KeyEvent.VK_V:
				lastEvent = 0;isVpressed = false;break;
		}
		release();
	}
	public boolean isModeActual()
    {
        return modeActual;
    }

    public void resetModeActual()
    {
        modeActual = false;
    }


    public TurningStrategy getTurningStrategy()
    {
        return turningStr;
    }
    
	@Override
	public void keyTyped(KeyEvent e)
	{
	}

}
