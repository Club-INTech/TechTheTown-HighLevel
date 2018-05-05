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
import enums.ScriptNames;
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

    /** Je jumper a-t-il été enlevé ?*/
    private boolean jumperRemoved;

    /** Points marqués depuis le debut du match */
    private int obtainedPoints;

    /** On active ou désactive la basicDetection*/
    private boolean basicDetectionIgnored;

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

    /** Réussites tour avant */
    private int[] reussitesTourAvant;

    /** Réussites tour arrière */
    private int[] reussitesTourArriere;

    /** Variables permettant de savoir quels tas de cubes on été pris, permet la gestion des obstacles */
    private boolean tas_base_pris;
    private boolean tas_chateau_eau_pris;
    private boolean tas_station_epuration_pris;
    private boolean tas_base_ennemi_pris;
    private boolean tas_chateau_ennemi_eau_pris;
    private boolean tas_station_epuration_ennemi_pris;

    /** Panneau domotique activé */
    private boolean panneauActive;

    /** Abeille lancée */
    private boolean abeilleLancee;

    /** DeposeCubes done */
    private boolean deposeCubes0Done;
    private boolean deposeCubes1Done;

    /** Dernier script lancé */
    private ScriptNames lastScript;
    private int lastScriptVersion;

    /**Permet de savoir si on a acrivé ou désactivé les capteurs comme ça si on
     * a à faire des movelenghtwise par exemple, on les fait mais sans se soucier
     * de la détection de l'ennemi
     */
    private volatile boolean capteursActivated;

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
        this.obtainedPoints = 10;

        //Aucun des tours n'est remplie en début de match
        this.tourAvantRemplie=false;
        this.tourArriereRemplie=false;

        //On dit que les cubes bonus sont présents au début du match
        this.cubeAvantPresent=true;
        this.cubeArrierePresent=true;

        this.reussitesTourAvant=new int[]{-1,-1,-1,-1};
        this.reussitesTourArriere=new int[]{-1,-1,-1,-1};

        this.tas_base_pris=false;
        this.tas_chateau_eau_pris=false;
        this.tas_station_epuration_pris=false;
        this.tas_base_ennemi_pris=false;
        this.tas_chateau_ennemi_eau_pris=false;
        this.tas_station_epuration_ennemi_pris=false;

        this.panneauActive = false;
        this.abeilleLancee = false;

        this.deposeCubes0Done = false;
        this.deposeCubes1Done = false;

        //La reconnaissance de couleurs est faite ou non
        this.recognitionDone=false;
        //On set une valeur de base, qui sera changée par PatternRecognition par la suite
        this.indicePattern=-2;
        //au début, le threadSensor est lancé, donc les capteurs sont bien activés au début
        this.capteursActivated =true;
        this.lastScript=null;
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
     * Temps écoulé depuis le début du match en ms
	 * @return the time Ellapsed
	 */
	public long getTimeEllapsed()
	{
		timeEllapsed = ThreadTimer.getMatchCurrentTime();
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

    public void addObtainedPoints(int pointsToAdd){
	    this.obtainedPoints+=pointsToAdd;
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



    public boolean isCubeBonusAvantPresent() {
        return cubeAvantPresent;
    }

    public void setCubeBonusAvantPresent(boolean cubeAvantPresent) {
        this.cubeAvantPresent = cubeAvantPresent;
    }



    public boolean isCubeBonusArrierePresent() {
        return cubeArrierePresent;
    }

    public void setCubeBonusArrierePresent(boolean cubeArrierePresent) {
        this.cubeArrierePresent = cubeArrierePresent;
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

    public boolean isDeposeCubes0Done () { return deposeCubes0Done; }

    public boolean isDeposeCubes1Done () { return deposeCubes1Done; }

    public void setPanneauActive(boolean panneauActive) {
        this.panneauActive = panneauActive;
    }

    public void setAbeilleLancee(boolean abeilleLancee) {
        this.abeilleLancee = abeilleLancee;
    }

    public void setDeposeCubes0Done(boolean deposeCubes0Done){
        this.deposeCubes0Done=deposeCubes0Done;
    }
    public void setDeposeCubes1Done(boolean deposeCubes1Done){
        this.deposeCubes1Done=deposeCubes1Done;
    }


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


    public void setJumperRemoved(boolean value){
        this.jumperRemoved=value;
    }
    public boolean wasJumperRemoved(){
        return this.jumperRemoved;
    }


    public int[] getReussitesTourAvant(){
        return this.reussitesTourAvant;
    }
    public void setReussitesTourAvant(int value, int positionIdealeDansLaTour){
        log.debug("Reussites tour avant: "+value+" à la position "+positionIdealeDansLaTour);
        if (positionIdealeDansLaTour>=0 && positionIdealeDansLaTour<=3){
            this.reussitesTourAvant[positionIdealeDansLaTour] = value;
        }
    }

    public int[] getReussitesTourArriere(){
        return this.reussitesTourArriere;
    }
    public void setReussitesTourArrière(int value, int positionIdealeDansLaTour){
        log.debug("Reussites tour arrière: "+value+" à la position "+positionIdealeDansLaTour);
        if (positionIdealeDansLaTour>=0 && positionIdealeDansLaTour<=3){
            this.reussitesTourArriere[positionIdealeDansLaTour] = value;
        }
    }

    public boolean isCapteursActivated() {
        return this.capteursActivated;
    }

    public void setCapteursActivated(boolean capteursActivés) {
        this.capteursActivated = capteursActivés;
    }

    public void setLastScript(ScriptNames lastScript){
        this.lastScript=lastScript;
    }

    public ScriptNames getLastScript(){
        return this.lastScript;
    }

    public void setLastScriptVersion(int version){
        this.lastScriptVersion=version;
    }

    public int getLastScriptVersion(){
        return this.lastScriptVersion;
    }
}


