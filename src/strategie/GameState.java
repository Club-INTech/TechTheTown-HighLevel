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
import patternRecognition.PatternRecognition;
import pfg.config.Config;
import robot.Robot;
import table.Table;
import threads.ThreadTimer;
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
    
	/** points marqués depuis le debut du match */
    public int obtainedPoints;

    /** Indice du pattern trouvé*/
    public int indicePattern;

    /** Tour avant déjà remplie */
    public boolean tourAvantRemplie;

    /** Tour arriere déjà remplie */
    public boolean tourArriereRemplie;

    /** Cube bonus avant présent dans la tour*/
    public boolean cubeAvantPresent;

    /** Cube bonus arrière présent dans la tour*/
    public boolean cubeArrierePresent;

    private Config config;
    private Log log;
    public boolean recognitionlock;




    /**
     * Instancie un nouvel état de jeu. (il ne représente pas forcément la réalité, il peut être fictif)
     * @param config inutilisé
     * @param log inutilisé
     * @param table l'état de la table a considérer
     * @param robot Le robot a considérer, soit un Robot, soit un Robothrono
     */
    private GameState(Config config, Log log, Table table, Robot robot,PatternRecognition patternRecognition)
    {
        this.table = table;
        this.robot = robot;
        this.config=config;
        this.log=log;

        
        //On n'a marqué aucun point en début de match
        this.obtainedPoints = 0;

        //Aucun des tours n'est remplie en début de match
        this.tourAvantRemplie=false;
        this.tourArriereRemplie=false;

        //On dit que les cubes bonus sont présents au début du match
        this.cubeAvantPresent=true;
        this.cubeArrierePresent=true;

        this.recognitionlock=patternRecognition.isRecognitionlock();



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
