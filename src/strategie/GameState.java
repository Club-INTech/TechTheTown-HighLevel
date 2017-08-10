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

package strategie;

import container.Service;
import robot.Robot;
import table.Table;
import threads.ThreadTimer;
import utils.Config;
import utils.Log;

/**
 * Classe indiquant l'ensemble des informations sur le match suceptibles d'évoluer durant le match.
 * Le game state contient toutes les informations à connaître pour la stratégie. Il contient:
 * - Robot (real ou chrono), qui apporte des informations sur le robot (position, orientation, ...)
 * - Table, qui apporte des informations sur les obstacles et les éléments de jeux
 *
 */

public class GameState implements Service
{    
    /*
     * Les attributs public sont en "final". Cela signifie que les objets
     * peuvent être modifiés mais pas ces références.
     */
	
    /** La table */
    public final Table table;
    
    /** Le robot que l'on fait agir sur cette table*/
    public final Robot robot;

    /** Temps écoulé depuis le début du match en ms */
    private long timeEllapsed;
    
	/** points marqués depus le debut du match */
    public int obtainedPoints;

    /**
     * Instancie un nouvel état de jeu. (il ne représente pas forcément la réalité, il peut être fictif)
     *
     * @param config inutilisé
     * @param log inutilisé
     * @param table l'état de la table a considérer
     * @param robot Le robot a considérer, soit un Robot, soit un Robothrono
     */
    private GameState(Config config, Log log, Table table, Robot robot)
    {
        this.table = table;
        this.robot = robot;
        
        // on n'a marqué aucun point en début de match
        obtainedPoints = 0;
    }

    /* (non-Javadoc)
     * @see container.Service#updateConfig()
     */
    
    @Override
    public void updateConfig()
    {
        table.updateConfig();
        robot.updateConfig();
    }
    

    
    /**
     * temps écoulé depuis le début du match en ms
     * 
	 * @return the time Ellapsed
	 */
	public long getTimeEllapsed()
	{
		timeEllapsed = ThreadTimer.ellapsedTimeSinceMatchStarted();
		return timeEllapsed;
	}

    /**
     * Change le rayon du robot et fait toutes les modifs necesssaires
     * A utiliser dans les scripts et la stratégie
     * @param newRad le nouveau rayon
     */
    public void changeRobotRadius(int newRad)
    {
        this.robot.setRobotRadius(newRad);
        this.table.getObstacleManager().updateObstacles(newRad);
    }
	
}
