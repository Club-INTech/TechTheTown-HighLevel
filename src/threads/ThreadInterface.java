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

package threads;


import graphics.Window;
import pfg.config.Config;
import robot.Robot;
import scripts.ScriptManager;
import smartMath.Vec2;
import strategie.GameState;
import table.Table;
import utils.Log;


/**
 * Thread affichant l'interface graphique pour observer ce que le robot pense être vrai, ainsi que le chemin
 * qu'il va suivre.
 * @author discord
 */
public class ThreadInterface extends AbstractThread
{
    private Window win;
    private Log log;
    private Robot robot;

    public ThreadInterface(Config config, Log log, Table table, GameState state, ScriptManager scriptManager)
    {
        super(config, log);
        Thread.currentThread().setPriority(3); // C'est le thread le moins prioritaire du lot
        this.log = log;
        this.robot = robot;
        this.win = new Window(table, state, scriptManager,false);
        win.setPoint(new Vec2(597,1269));

    }

    @Override
    public void run() {
        while(true)
        {
            win.getPanel().repaint();
            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Getter de window
     * @return
     */
    public Window getWindow() {
        return win;
    }
}
