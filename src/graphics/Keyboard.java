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
import simulator.ThreadSimulator;
import strategie.GameState;
import tests.container.A;
import threads.AbstractThread;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.Array;

/**
 * Gestionnaire des actions clavier pour l'interface graphique, ajoutez vos actions aux blocks correspondants
 * @author etienne, discord, florian
 */
public class Keyboard extends AbstractThread implements KeyListener {
	private GameState mRobot;
	private ScriptManager scriptManager;
	private TurningStrategy turningStr = TurningStrategy.FASTEST;
	private boolean modeActual = false;
	
	public Keyboard(GameState robot, ScriptManager scriptManager) {
		mRobot = robot;
		this.scriptManager = scriptManager;
	}

	public void run(){
		while(true) {
			doThat();
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private boolean isUpPressed;
	private boolean isDownPressed;
	private boolean isLeftPressed;
	private boolean isRightPressed;
	private String lastKeyPressed="none";
	private boolean isMoving=false;

	private boolean isApressed;
	private boolean wasAreleased=true;

	private boolean isKpressed;
	private boolean wasKreleased=true;
	private boolean isPpressed;
	private boolean wasPreleased=true;

	private boolean isWpressed;
	private boolean wasWreleased=true;
	private boolean isXpressed;
	private boolean wasXreleased=true;
	private boolean isCpressed;
	private boolean wasCreleased=true;
	private boolean isVpressed;
	private boolean wasVreleased=true;

	private boolean isPompeActivated=false;
	private boolean isPorteAvantOuverte=false;
	private boolean isPorteArriereOuverte=false;
	private boolean takingCube=false;

	private void doThat() {
		if (lastKeyPressed.equals("up")){
			goForward();
		}
		else if (lastKeyPressed.equals("down")){
			goBackward();
		}
		else if (lastKeyPressed.equals("left")){
			goLeft();
		}
		else if (lastKeyPressed.equals("right")){
			goRight();
		}

		else if(isUpPressed) {
			goForward();
		}
		else if(isDownPressed) {
			goBackward();
		}
		else if(isLeftPressed) {
			goLeft();
		}
		else if(isRightPressed) {
			goRight();
		}
		else{
			if (isMoving) {
				isMoving = false;
				sstop();
			}
		}

		if (isWpressed){
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
			if (!isMoving) {
				if (wasXreleased) {
					if (!takingCube) {
						this.takingCube = true;
						mRobot.robot.useActuator(ActuatorOrder.DESACTIVE_ELECTROVANNE_ARRIERE, false);
						mRobot.robot.useActuator(ActuatorOrder.ACTIVE_ELECTROVANNE_AVANT, true);
						mRobot.robot.useActuator(ActuatorOrder.ACTIVE_LA_POMPE, false);
						mRobot.robot.useActuator(ActuatorOrder.BAISSE_LE_BRAS_AVANT, true);
						mRobot.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS_AVANT, true);
						mRobot.robot.useActuator(ActuatorOrder.ACTIVE_ELECTROVANNE_ARRIERE, false);
						mRobot.robot.useActuator(ActuatorOrder.DESACTIVE_LA_POMPE, true);
						mRobot.robot.useActuator(ActuatorOrder.DESACTIVE_ELECTROVANNE_ARRIERE, true);
						mRobot.robot.useActuator(ActuatorOrder.DESACTIVE_ELECTROVANNE_AVANT, false);
						System.out.println("test");
						this.takingCube = false;
					}
				}
			}
		}
		else if (isVpressed) {
			if (!isMoving) {
				if (wasVreleased) {
					if (!takingCube) {
						this.takingCube = true;
						mRobot.robot.useActuator(ActuatorOrder.DESACTIVE_ELECTROVANNE_AVANT, false);
						mRobot.robot.useActuator(ActuatorOrder.ACTIVE_ELECTROVANNE_ARRIERE, true);
						mRobot.robot.useActuator(ActuatorOrder.ACTIVE_LA_POMPE, false);
						mRobot.robot.useActuator(ActuatorOrder.BAISSE_LE_BRAS_ARRIERE, true);
						mRobot.robot.useActuator(ActuatorOrder.RELEVE_LE_BRAS_ARRIERE, true);
						mRobot.robot.useActuator(ActuatorOrder.ACTIVE_ELECTROVANNE_AVANT, false);
						mRobot.robot.useActuator(ActuatorOrder.DESACTIVE_LA_POMPE, true);
						mRobot.robot.useActuator(ActuatorOrder.DESACTIVE_ELECTROVANNE_ARRIERE, true);
						mRobot.robot.useActuator(ActuatorOrder.DESACTIVE_ELECTROVANNE_AVANT, false);
						this.takingCube = false;
					}
				}
			}
		}
	}



	private void goForward(){
		sstop();
		isMoving=true;
		mRobot.robot.useActuator(ActuatorOrder.MOVE_FORWARD, false);
	}private void goBackward(){
		sstop();
		isMoving=true;
		mRobot.robot.useActuator(ActuatorOrder.MOVE_BACKWARD, false);
	}private void goLeft(){
		sstop();
		isMoving=true;
		mRobot.robot.useActuator(ActuatorOrder.TURN_LEFT, false);
	}private void goRight(){
		sstop();
		isMoving=true;
		mRobot.robot.useActuator(ActuatorOrder.TURN_RIGHT, false);
	}

	private void sstop(){
		try {
			mRobot.robot.useActuator(ActuatorOrder.SSTOP,false);
		}
		catch(Exception exception) {
			System.out.println("ça marche pas bien trololo");
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_UP:
				isUpPressed = true;
				lastKeyPressed="up";
				break;
			case KeyEvent.VK_DOWN:
				isDownPressed = true;
				lastKeyPressed="down";
				break;
			case KeyEvent.VK_LEFT:
				isLeftPressed = true;
				lastKeyPressed="left";
				break;
			case KeyEvent.VK_RIGHT:
				isRightPressed = true;
				lastKeyPressed="right";
				break;

			case KeyEvent.VK_A:
				isApressed = true;
				break;
			case KeyEvent.VK_K:
				isKpressed = true;
				break;
			case KeyEvent.VK_P:
				isPpressed = true;
				break;
			case KeyEvent.VK_W:
				isWpressed = true;
				break;
			case KeyEvent.VK_X:
				isXpressed = true;
				break;
			case KeyEvent.VK_C:
				isCpressed = true;
				break;
			case KeyEvent.VK_V:
				isVpressed = true;
				break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		switch (e.getKeyCode())
		{
			case KeyEvent.VK_UP:
				this.isUpPressed=false;
				if (lastKeyPressed.equals("up")){
					lastKeyPressed="none";
				}
				break;
			case KeyEvent.VK_DOWN:
				this.isDownPressed=false;
				if (lastKeyPressed.equals("down")){
					lastKeyPressed="none";
				}
				break;
			case KeyEvent.VK_LEFT:
				this.isLeftPressed=false;
				if (lastKeyPressed.equals("left")){
					lastKeyPressed="none";
				}
				break;
			case KeyEvent.VK_RIGHT:
				this.isRightPressed=false;
				if (lastKeyPressed.equals("right")){
					lastKeyPressed="none";
				}
				break;

			case KeyEvent.VK_A:
				wasAreleased=true;isApressed=false;break;
			case KeyEvent.VK_K:
				wasKreleased=true;isKpressed=false;break;
			case KeyEvent.VK_P:
				wasPreleased=true;isPpressed=false;break;
			case KeyEvent.VK_W:
				wasWreleased=true;isWpressed=false;break;
			case KeyEvent.VK_X:
				wasXreleased=true;isXpressed=false;break;
			case KeyEvent.VK_C:
				wasCreleased=true;isCpressed=false;break;
			case KeyEvent.VK_V:
				wasVreleased=true;isVpressed=false;break;
		}
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
