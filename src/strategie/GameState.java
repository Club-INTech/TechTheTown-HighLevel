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
import enums.BrasUtilise;
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
    
    /** Le robot que l'on fait agir sur cette table */
    public final Robot robot;

    /** Temps écoulé depuis le début du match en ms */
    private long timeEllapsed;

    /** points marqués depuis le debut du match */
    private int obtainedPoints;

    /** Indice du pattern trouvé */
    private int indicePattern;

    /** Calculs de reconnaissance de patterns finis */
    private boolean recognitionDone;

    /** Tour avant déjà remplie */
    private boolean tourAvantRemplie;

    /** Tour arriere déjà remplie */
    private boolean tourArriereRemplie;

    /** Cube bonus avant présent dans la tour */
    private boolean cubeAvantPresent;

    /** Cube bonus arrière présent dans la tour */
    private boolean cubeArrierePresent;

    private boolean tas_base_pris;
    private boolean tas_chateau_eau_pris;
    private boolean tas_station_epuration_pris;
    private boolean tas_base_ennemi_pris;
    private boolean tas_chateau_ennemi_eau_pris;
    private boolean tas_station_epuration_ennemi_pris;
    private BrasUtilise takeCubesBras;

    /** Panneau domotique activé */
    private boolean panneauActive;

    /** Abeille lancée */
    private boolean abeilleLancee;



    private Config config;
    private Log log;




    /**
     * Instancie un nouvel état de jeu. (il ne représente pas forcément la réalité, il peut être fictif)
     * @param config inutilisé
     * @param log inutilisé
     * @param table l'état de la table a considérer
     * @param robot Le robot a considérer, soit un Robot, soit un Robothrono
     */
    private GameState(Config config, Log log, Table table, Robot robot)
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

        this.takeCubesBras= BrasUtilise.AVANT;

        this.tas_base_pris=false;
        this.tas_chateau_eau_pris=false;
        this.tas_station_epuration_pris=false;
        this.tas_base_ennemi_pris=false;
        this.tas_chateau_ennemi_eau_pris=false;
        this.tas_station_epuration_ennemi_pris=false;

        this.panneauActive = false;
        this.abeilleLancee = false;

        //La reconnaissance de couleurs est faite ou non
        this.recognitionDone=false;
        //On set une valeur de base, qui sera changée par PatternRecognition par la suite
        this.indicePattern=-2;

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

    public void setTimeEllapsed(long timeEllapsed) {
        this.timeEllapsed = timeEllapsed;
    }



    public int getObtainedPoints() {
        return this.obtainedPoints;
    }

    public void setObtainedPoints(int obtainedPoints) {
        this.obtainedPoints = obtainedPoints;
    }



    public int getIndicePattern() {
        return this.indicePattern;
    }

    public void setIndicePattern(int indicePattern) {
        this.indicePattern = indicePattern;
    }



    public boolean isRecognitionDone() {
        return this.recognitionDone;
    }

    public void setRecognitionDone(boolean recognitionDone) {
        this.recognitionDone = recognitionDone;
    }



    public boolean isTourAvantRemplie() {
        return this.tourAvantRemplie;
    }

    public void setTourAvantRemplie(boolean tourAvantRemplie) {
        this.tourAvantRemplie = tourAvantRemplie;
    }



    public boolean isTourArriereRemplie() {
        return tourArriereRemplie;
    }

    public void setTourArriereRemplie(boolean tourArriereRemplie) {
        this.tourArriereRemplie = tourArriereRemplie;
    }



    public boolean isCubeAvantPresent() {
        return cubeAvantPresent;
    }

    public void setCubeAvantPresent(boolean cubeAvantPresent) {
        this.cubeAvantPresent = cubeAvantPresent;
    }



    public boolean isCubeArrierePresent() {
        return cubeArrierePresent;
    }

    public void setCubeArrierePresent(boolean cubeArrierePresent) {
        this.cubeArrierePresent = cubeArrierePresent;
    }

    public BrasUtilise getTakeCubesBras() {
        return takeCubesBras;
    }

    public void setTakeCubesBras(BrasUtilise bras){
	    this.takeCubesBras=bras;
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

    public boolean isTas_base_pris() {
        return tas_base_pris;
    }

    public boolean isTas_chateau_eau_pris() {
        return tas_chateau_eau_pris;
    }

    public boolean isTas_station_epuration_pris() {
        return tas_station_epuration_pris;
    }

    public boolean isTas_base_ennemi_pris() {
        return tas_base_ennemi_pris;
    }

    public boolean isTas_chateau_ennemi_eau_pris() {
        return tas_chateau_ennemi_eau_pris;
    }

    public boolean isTas_station_epuration_ennemi_pris() {
        return tas_station_epuration_ennemi_pris;
    }

    public boolean isPanneauActive () { return panneauActive; }

    public boolean isAbeilleLancee () { return abeilleLancee; }

    public void setTas_base_pris(boolean tas_base_pris) {
        this.tas_base_pris = tas_base_pris;
    }

    public void setTas_chateau_eau_pris(boolean tas_chateau_eau_pris) {
        this.tas_chateau_eau_pris = tas_chateau_eau_pris;
    }

    public void setTas_station_epuration_pris(boolean tas_station_epuration_pris) {
        this.tas_station_epuration_pris = tas_station_epuration_pris;
    }

    public void setTas_base_ennemi_pris(boolean tas_base_ennemi_pris) {
        this.tas_base_ennemi_pris = tas_base_ennemi_pris;
    }

    public void setTas_chateau_ennemi_eau_pris(boolean tas_chateau_ennemi_eau_pris) {
        this.tas_chateau_ennemi_eau_pris = tas_chateau_ennemi_eau_pris;
    }

    public void setTas_station_epuration_ennemi_pris(boolean tas_station_epuration_ennemi_pris) {
        this.tas_station_epuration_ennemi_pris = tas_station_epuration_ennemi_pris;
    }

    public void setPanneauActive(boolean panneauActive) {
        this.panneauActive = panneauActive;
    }

    public void setAbeilleLancee(boolean abeilleLancee) {
        this.abeilleLancee = abeilleLancee;
    }
}
