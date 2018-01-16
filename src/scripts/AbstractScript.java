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

package scripts;

import container.Service;
import exceptions.BadVersionException;
import exceptions.BlockedActuatorException;
import exceptions.ExecuteException;
import exceptions.Locomotion.PointInObstacleException;
import exceptions.Locomotion.UnableToMoveException;
import hook.HookFactory;
import pfg.config.Config;
import smartMath.Circle;
import smartMath.Vec2;
import strategie.GameState;
import utils.Log;

/**
 * Classe abstraite dont héritent les différents scripts.
 * Les scripts héritants de cette classe peuvent être indifférement exécutés par un Robot ou un Robotchrono.
 * @author pf
 */
public abstract class AbstractScript implements Service 
{
	/**  système de log sur lequel écrire. */
	protected static Log log;
	
	/**  le fichier de config a partir duquel le script pourra se configurer. */
	protected static Config config;

	/**  Liste des versions du script. */
	protected Integer[] versions;
	/**  tableau de 2 dimensions des versions du script. */
	protected Integer[][] versions2;

	/** HookFactory pour gérer les hooks */
	protected static HookFactory hookFactory;
	
	/**
	 * Constructeur à appeller lorsqu'un script héritant de la classe AbstractScript est instancié.
	 * Le constructeur se charge de renseigner la hookFactory, le système de config et de log.
	 * @param config le fichier de config a partir duquel le script pourra se configurer
	 * @param log le système de log qu'utilisera le script
	 */
	protected AbstractScript(Config config, Log log, HookFactory hookFactory)
	{
		AbstractScript.config = config;
		AbstractScript.log = log;
		AbstractScript.hookFactory = hookFactory;
	}
		
	/**
	 * Va au point d'entrée du script (en utilisant le Pathfinding), puis l'exécute
	 * @param versionToExecute la version du script
	 * @param actualState l'état courrant du match.
	 * @throws UnableToMoveException losrque le robot veut se déplacer et que quelque chose sur le chemin cloche et que le robot ne peut s'en défaire simplement: bloquage mécanique immobilisant le robot ou obstacle percu par les capteurs
	 * @throws ExecuteException
	 */
	public void goToThenExec(int versionToExecute,GameState actualState) throws UnableToMoveException, BadVersionException, ExecuteException, BlockedActuatorException, PointInObstacleException {
		// va jusqu'au point d'entrée de la version demandée
		log.debug("Lancement de " + this.toString() + " version " + versionToExecute);
		try 
		{
			if(actualState.robot.getPosition().minusNewVector(entryPosition(versionToExecute,actualState.robot.getRobotRadius(), actualState.robot.getPositionFast()).getCenter()).squaredLength() > 40) {

				log.debug("Appel au PathFinding, car Position du robot :" + actualState.robot.getPosition() + " et entrée du script :" + entryPosition(versionToExecute, actualState.robot.getRobotRadius(), actualState.robot.getPosition()).getCenter());

				actualState.robot.moveToCircle(entryPosition(versionToExecute, actualState.robot.getRobotRadius(), actualState.robot.getPositionFast()), actualState.table);
			}
		}
		catch (UnableToMoveException e)
		{
			log.debug("Catch de "+e+" Impossible de goToThenExec : abandon d'exec, throw de "+e);
			throw e;
		}

		// exécute la version demandée
		execute(versionToExecute, actualState);
	}

	   
	/**
	 * Exécute le script
	 * @param versionToExecute la version du script à exécuter
	 * @param actualState l'état courant du match.
	 * @throws UnableToMoveException exception levée lorsque le robot ne peut se déplacer (décor ou obstacles détectés par capteurs)
	 * @throws ExecuteException
	 */
	public abstract void execute(int versionToExecute, GameState actualState) throws UnableToMoveException, ExecuteException, BlockedActuatorException;

	public void execute(int versionToExecute, GameState actualState, boolean shift, Long alpha) throws UnableToMoveException, ExecuteException, BlockedActuatorException{};

	/**
	 * Renvoie le score que peut fournir une version d'un script.
	 * Si l'exécution du script ne rapporte aucun point étant donné le gamestate fourni, renvoie 0.
	 *
	 * @param version version dont on veut le score potentiel
	 * @param state l'état du jeu ou l'on veut évaluer le nombre de point que rapporterait l'execution de la version fournie de ce script.
	 * @return le score demandé
	 */
	public abstract int remainingScoreOfVersion(int version, final GameState state);

	/**
	 * Retourne la position d'entrée associée à la version.
	 *
	 * @param version version dont on veut le point d'entrée
	 * @param ray : rayon du robot
	 * @param robotPosition la position actuelle du robot
	 * @return la position du point d'entrée
	 */
	public abstract Circle entryPosition(int version, int ray, Vec2 robotPosition) throws BadVersionException;
	
	/**
	 * Méthode appelée à la fin du script si une exception a lieu.
	 * Le repli des actionneurs est impératif à demander au sein de cette méthode : si un bras reste déployé en cours de match, il risque de se faire arracher !  
	 * Ainsi, les exceptions lancées par cette méthode sont les plus critiques que l'on puisse imaginer : elles préviennent qu'on peut casser la méca si on ne réagit pas bien !
	 * @param state : état du jeu au sein duquel il faut finaliser le script
	 * @param e : l'exception qui a déclenché le finalize 
	 * @throws UnableToMoveException exception levée lorsque le robot ne peut se déplacer (décor ou obstacles détectés par capteurs)
	 */
	public abstract void finalize(GameState state, Exception e) throws UnableToMoveException;

	/**
	 * Fonction qui attend que l'ennemi détecté par les capteurs se dégage du chemin suivi par le robot
	 * @param posRobot la position du robot
	 * @return true si chemin dégagé, false sinon
     */
	public boolean waitForEnnemy(GameState actualState, Vec2 posRobot, boolean forward)
	{
		// Attente d'une seconde
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long time = System.currentTimeMillis();
        
        // Détermination du chemin suivi par le robot
		int signe = forward ? 1 : -1;
		Vec2 aim = posRobot.plusNewVector(new Vec2((int)(signe*300*Math.cos(actualState.robot.getOrientation())),(int)(signe*300*Math.sin(actualState.robot.getOrientation()))));
		
		// boucle d'attente de 4 secondes maximum
		while(actualState.table.getObstacleManager().isDiscObstructed(aim, 100))
		{
			if(System.currentTimeMillis() - time > 4000)
				return false;
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see container.Service#updateConfig()
	 */


	public void updateConfig() {
	}
	
	/** Getter utilisé par l'IA
	 * @param stateToConsider état de jeu actuel
	 * @return les versions possibles du script*/
	abstract public Integer[] getVersion(GameState stateToConsider);
	abstract public Integer[][] getVersion2(GameState stateToConsider);

}
