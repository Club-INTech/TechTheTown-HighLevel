package tests;

import org.junit.Before;
import org.junit.Test;
import robot.Robot;
import scripts.ScriptManager;
import simulator.ThreadSimulator;
import simulator.ThreadSimulatorMotion;
import strategie.GameState;
import table.Table;
import strategie.IA.*;
import threads.ThreadInterface;

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

public class JUnit_IA extends JUnit_Test{


        private Robot robotReal;
        private ScriptManager scriptManager;
        private GameState state;
        private ThreadSimulator simulator;
        private ThreadSimulatorMotion simulatorMotion;
        private Table table;

        private IA ia;

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
//                container.getService(ThreadInterface.class);

                ia=container.getService(IA.class);

                container.startInstanciedThreads();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Test
        public void testScript() {
            try {
                robotReal = container.getService(Robot.class);
                robotReal.setPosition(Table.entryPosition);
                robotReal.setOrientation(Table.entryOrientation);

                ia.getGraph().display();
                ia.kruskal();
//                while (true){
//                    ia.execute(null);
//                    Thread.sleep(500);
//                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }