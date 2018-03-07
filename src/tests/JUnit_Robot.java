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

package tests;

import enums.*;
import exceptions.Locomotion.UnableToMoveException;
import org.junit.Before;
import org.junit.Test;
import pathfinder.Pathfinding;
import robot.Robot;
import scripts.ScriptManager;
import simulator.ThreadSimulator;
import simulator.ThreadSimulatorMotion;
import smartMath.Vec2;
import strategie.GameState;
import table.Table;


/**
 * The Class JUnit_Robot.
 */
public class JUnit_Robot extends JUnit_Test {
    /**
     * The robotvrai.
     */
    private Robot robotReal;
    private ScriptManager scriptManager;
    private GameState state;
    private Table table;

    /* (non-Javadoc)
     * @see tests.JUnit_Test#setUp()
     */
    @Before
    public void setUp() {
        try {
            super.setUp();
            robotReal = container.getService(Robot.class);
            scriptManager = container.getService(ScriptManager.class);
            state = container.getService(GameState.class);
            table = container.getService(Table.class);

            container.startInstanciedThreads();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testScript() {
        try {
            robotReal.getPosition();
            robotReal.getOrientation();

            robotReal.setPosition(new Vec2(890,840));
            robotReal.setOrientation(Math.PI);
            robotReal.setLocomotionSpeed(Speed.FAST_ALL);
            robotReal.moveLengthwise(200);
            state.setIndicePattern(1);

            scriptManager.getScript(ScriptNames.TAKE_CUBES).goToThenExec(TasCubes.TAS_STATION_EPURATION.getID(),state);
            scriptManager.getScript(ScriptNames.TAKE_CUBES).goToThenExec(TasCubes.TAS_CHATEAU_EAU.getID(),state);

            scriptManager.getScript(ScriptNames.DEPOSE_CUBES).goToThenExec(0,state);

            Thread.sleep(500);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void basicTest() {
        //robotReal.useActuator(ActuatorOrder.BAISSE_LE_BRAS_ARRIERE,false);
        //robotReal.useActuator(ActuatorOrder.BAISSE_LE_BRAS_AVANT,true);
        robotReal.useActuator(ActuatorOrder.RELEVE_LE_BRAS_ARRIERE,false);
        robotReal.useActuator(ActuatorOrder.RELEVE_LE_BRAS_AVANT,true);
        /*for (int i=0; i<20; i++){
            try {
                robotReal.moveLengthwise(250);
                robotReal.useActuator(ActuatorOrder.BAISSE_LE_BRAS_AVANT, true);
                robotReal.useActuator(ActuatorOrder.RELEVE_LE_BRAS_AVANT, true);

                robotReal.moveLengthwise(-250);
                robotReal.useActuator(ActuatorOrder.BAISSE_LE_BRAS_ARRIERE, true);
                robotReal.useActuator(ActuatorOrder.RELEVE_LE_BRAS_ARRIERE, true);
            }catch (UnableToMoveException e){
                e.printStackTrace();
            }
        }*/
    }
}
