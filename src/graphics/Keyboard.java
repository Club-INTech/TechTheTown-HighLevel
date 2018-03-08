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
import tests.container.A;

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
	boolean wasAreleased=true;

	boolean isKpressed;
	boolean wasKreleased=true;
	boolean isPpressed;
	boolean wasPreleased=true;

	boolean isWpressed;
	boolean wasWreleased=true;
	boolean isXpressed;
	boolean wasXreleased=true;
	boolean isCpressed;
	boolean wasCreleased=true;
	boolean isVpressed;
	boolean wasVreleased=true;

	int lastEvent;

	boolean isPompeActivated=false;
	boolean isPorteAvantOuverte=false;
	boolean isPorteArriereOuverte=false;
	boolean takingCube=false;

	void doThat() {
		if(/*isUpPressed < 15 &&*/ isUpPressedb)
		{
			mRobot.robot.useActuator(ActuatorOrder.MOVE_FORWARD, false);
		}
		else if(/*isDownPressed < 15 &&*/ isDownPressedb)
		{
			mRobot.robot.useActuator(ActuatorOrder.MOVE_BACKWARD, false);
		}
		else if(/*isLeftPressed < 15 &&*/ isLeftPressedb)
		{
			mRobot.robot.useActuator(ActuatorOrder.TURN_LEFT, false);
		}
		else if( /*isRightPressed < 15 && */isRightPressedb)
		{
			mRobot.robot.useActuator(ActuatorOrder.TURN_RIGHT, false);
		}

		else if (isWpressed){
			if (wasWreleased) {
				if (!isPorteAvantOuverte) {
					mRobot.robot.useActuator(ActuatorOrder.OUVRE_LA_PORTE_AVANT, false);
					wasWreleased = false;
					isPorteAvantOuverte = true;
				}
				else{
					mRobot.robot.useActuator(ActuatorOrder.FERME_LA_PORTE_AVANT, false);
					wasWreleased = false;
					isPorteAvantOuverte = false;
				}
			}
		}
		else if (isCpressed){
			if (wasCreleased) {
				if (!isPorteArriereOuverte) {
					mRobot.robot.useActuator(ActuatorOrder.OUVRE_LA_PORTE_ARRIERE, false);
					wasCreleased = false;
					isPorteArriereOuverte = true;
				}
				else{
					mRobot.robot.useActuator(ActuatorOrder.FERME_LA_PORTE_ARRIERE, false);
					wasCreleased = false;
					isPorteArriereOuverte = false;

				}
			}
		}
		else if (isXpressed){
			if (wasXreleased) {
				if (!takingCube) {
					takingCube=true;
					mRobot.robot.useActuator(ActuatorOrder.ACTIVE_ELECTROVANNE_AVANT,false);
					mRobot.robot.useActuator(ActuatorOrder.ACTIVE_LA_POMPE,false);
					mRobot.robot.useActuator(ActuatorOrder.BAISSE_LE_BRAS_AVANT,true);
					mRobot.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS_AVANT,true);
					mRobot.robot.useActuator(ActuatorOrder.ACTIVE_ELECTROVANNE_ARRIERE,true);
					mRobot.robot.useActuator(ActuatorOrder.DESACTIVE_LA_POMPE,true);
					mRobot.robot.useActuator(ActuatorOrder.DESACTIVE_ELECTROVANNE_ARRIERE,true);
					mRobot.robot.useActuator(ActuatorOrder.DESACTIVE_ELECTROVANNE_AVANT,true);
					takingCube=false;
				}
			}
		}
		else if (isVpressed){
			if (wasVreleased){
				if (!takingCube){
					takingCube=true;
					mRobot.robot.useActuator(ActuatorOrder.ACTIVE_ELECTROVANNE_ARRIERE,false);
					mRobot.robot.useActuator(ActuatorOrder.ACTIVE_LA_POMPE,false);
					mRobot.robot.useActuator(ActuatorOrder.BAISSE_LE_BRAS_ARRIERE,true);
					mRobot.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS_ARRIERE,true);
					mRobot.robot.useActuator(ActuatorOrder.ACTIVE_ELECTROVANNE_AVANT,true);
					mRobot.robot.useActuator(ActuatorOrder.DESACTIVE_LA_POMPE,true);
					mRobot.robot.useActuator(ActuatorOrder.DESACTIVE_ELECTROVANNE_AVANT,true);
					mRobot.robot.useActuator(ActuatorOrder.DESACTIVE_ELECTROVANNE_ARRIERE,true);
					takingCube=false;
				}
			}
		}
		else {
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
				case KeyEvent.VK_RIGHT:
					isRightPressed++;
					isRightPressedb = true;
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
				lastEvent = 0;wasAreleased=true;isApressed=false;break;
			case KeyEvent.VK_K:
				lastEvent = 0;wasKreleased=true;isKpressed=false;break;
			case KeyEvent.VK_P:
				lastEvent = 0;wasPreleased=true;isPpressed=false;break;
			case KeyEvent.VK_W:
				lastEvent = 0;wasWreleased=true;isWpressed=false;break;
			case KeyEvent.VK_X:
				lastEvent = 0;wasXreleased=true;isXpressed=false;break;
			case KeyEvent.VK_C:
				lastEvent = 0;wasCreleased=true;isCpressed=false;break;
			case KeyEvent.VK_V:
				lastEvent = 0;wasVreleased=true;isVpressed=false;break;
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
