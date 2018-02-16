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

import enums.ActuatorOrder;
import enums.ScriptNames;
import enums.Speed;
import enums.UnableToMoveReason;
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
    private ThreadSimulator simulator;
    private ThreadSimulatorMotion simulatorMotion;
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
     //       simulatorMotion = container.getService(ThreadSimulatorMotion.class);
     //       simulator = container.getService(ThreadSimulator.class);

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

            robotReal.setPosition(new Vec2(900,850));
            robotReal.setOrientation(Math.PI);
            robotReal.setLocomotionSpeed(Speed.SLOW_ALL);
            robotReal.moveLengthwise(200);
            /*robotReal.setOrientation(Math.PI/2);
            robotReal.setPosition(new Vec2(0, 500));

      //   robotReal.moveLengthwise(-200);
        //    robotReal.turnRelatively(-Math.PI/2);

         //   robotReal.goTo(new Vec2(0,500))
      //      robotReal.goTo(new Vec2( 0,400));
        //    robotReal.goTo(new Vec2(1000,600));
         //   robotReal.goTo(new Vec2(1000,800));

       //     robotReal.moveLengthwise(-100);
       //     robotReal.moveLengthwise(100);
            ArrayList<Vec2> path = new ArrayList<>();
            path.add(new Vec2(0,500));
            for (int i=0; i<=40; i++){
                path.add(new Vec2((int) (500*Math.cos(2*Math.PI*i/40)), (int) (500*Math.sin(2*i*Math.PI/40))+500));
            }

    //            Thread.sleep(3000);
              robotReal.followPath(path);
              */

            /*
            robotReal.goTo(new Vec2(650, 500));
            Thread.sleep(5000);
            robotReal.moveLengthwise(-50);
            robotReal.turn(1.2);
            robotReal.turn(0.8);

            */

            Pathfinding pathfinding = new Pathfinding(log, config, table);


            Thread.sleep(500);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void basicTest() {
        for (int i=0; i<20; i++){
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
        }
    }
}
