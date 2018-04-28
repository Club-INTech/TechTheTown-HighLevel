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

import enums.Speed;
import exceptions.BadVersionException;
import exceptions.ExecuteException;
import exceptions.Locomotion.UnableToMoveException;
import hook.HookFactory;
import pfg.config.Config;
import smartMath.Circle;
import smartMath.Vec2;
import strategie.GameState;
import utils.Log;


/**
 * Script pour la fermeture des portes des cabines
 * Version 0 : Déplacement de la serviette aux portes puis fermeture en même temps ; aucune action prevue hors du deplacement ; aucun pathdingding/evitement ; si pb -> arret complet
 * Version 1 : Identique à la version 0, sauf qu'on ferme les portes en marche avant
 * Version 3 : Intégration des trajectoires courbes
 * Version 4 : version 0 avec appel PDD
 * @author Discord, CF
 */
public class CloseDoors extends AbstractScript
{
	public CloseDoors(Config config, Log log, HookFactory hookFactory) {
		super(config, log, hookFactory);
		versions = new int[]{0,1,3,4};
	}
	
	/**
	 * On lance le script choisi.
	 * @param versionToExecute Version a lancer
	 * @param stateToConsider Notre bon vieux robot
	 */
	@Override
	public void execute(int versionToExecute, GameState stateToConsider) throws ExecuteException, UnableToMoveException
	{
		//Les paramètres de cette version ont été déterminés expérimentalement, fonctionnels sur robot 2015
		try
		{
			if(versionToExecute == 0 || versionToExecute == 3)
			{
				//On ralentit pour éviter de démonter les éléments de jeu "Discord-style"
				Speed speedBeforeScriptWasCalled = stateToConsider.robot.getLocomotionSpeed();

                stateToConsider.robot.setBasicDetection(true);
				stateToConsider.robot.setForceMovement(false);
                stateToConsider.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);

                //On s'oriente vers les portes
				stateToConsider.robot.turn(-(Math.PI / 2), false);

                stateToConsider.robot.setBasicDetection(true);

//                Hook hook = hookFactory.newYGreaterHook(1700);
//                hook.addCallback(new Callback(new SpeedDown(), true, stateToConsider));
//                hooksToConsider.add(hook);

                stateToConsider.robot.setForceMovement(true);

                //On ferme les portes
				stateToConsider.robot.moveLengthwise(-1000, true);

                stateToConsider.robot.setBasicDetection(true);

               // stateToConsider.robot.setForceMovement(false);


                //PORTES FERMEES !
				stateToConsider.setObtainedPoints(stateToConsider.getObtainedPoints()+20);;
//				stateToConsider.table.extDoorClosed = true;
//				stateToConsider.table.intDoorClosed = true;

				//if(Geometry.isBetween(stateToConsider.robot.getPosition().y, 1790, 1890))
					stateToConsider.robot.setPosition(new Vec2(stateToConsider.robot.getPosition().getX(),1840));
					stateToConsider.robot.setOrientation(-Math.PI/2);

				//else
				//	log.debug("Position trop éloignée pour se recaler en y (cylindre ?)");

                stateToConsider.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);

                stateToConsider.robot.setBasicDetection(true);
				//On avance
				stateToConsider.robot.moveLengthwiseWithoutDetection(300, false);

                stateToConsider.robot.setForceMovement(false);

                stateToConsider.robot.turn(Math.PI);
                stateToConsider.robot.setForceMovement(true);


//                hook = hookFactory.newXGreaterHook(1200);
//                hook.addCallback(new Callback(new SpeedDown(), true, stateToConsider));
//                hooksToConsider.add(hook);

				stateToConsider.robot.moveLengthwise(-500, true);

				stateToConsider.robot.setForceMovement(false);

				//  if(Geometry.isBetween(stateToConsider.robot.getPosition().x, 1300, 1400))
			//	{
				    stateToConsider.robot.setPosition(new Vec2(1350, stateToConsider.robot.getPosition().getY()));
					stateToConsider.robot.setOrientation(Math.PI);
			//	}
            //    else
            //        log.debug("Position trop éloignée pour se recaler en x (cylindre ?)");

				stateToConsider.robot.moveLengthwise(300,false);


				stateToConsider.robot.setBasicDetection(false);



                stateToConsider.robot.setLocomotionSpeed(speedBeforeScriptWasCalled);

			}

			else if (versionToExecute == 1)
			{


				//On ralentit pour éviter de démonter les éléments de jeu "Discord-style"
				//Speed speedBeforeScriptWasCalled = stateToConsider.robot.getLocomotionSpeed();
				//stateToConsider.robot.setLocomotionSpeed(Speed.SLOW);

				//On s'oriente vers les portes
				stateToConsider.robot.turn((Math.PI / 2),false);

				//On ferme les portes
				stateToConsider.robot.moveLengthwise(600, true);

				//PORTES FERMEES !
				stateToConsider.setObtainedPoints(stateToConsider.getObtainedPoints()+20);
//				stateToConsider.table.extDoorClosed = true;
//				stateToConsider.table.intDoorClosed = true;

				//On recule
				stateToConsider.robot.moveLengthwise(-200, false);


				//stateToConsider.robot.setLocomotionSpeed(speedBeforeScriptWasCalled);

			}
            else if(versionToExecute == 4)
            {
                //On ralentit pour éviter de démonter les éléments de jeu "Discord-style"
                @SuppressWarnings("unused")
				Speed speedBeforeScriptWasCalled = stateToConsider.robot.getLocomotionSpeed();

                stateToConsider.robot.setBasicDetection(true);
                stateToConsider.robot.setForceMovement(false);
                stateToConsider.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);

                //On s'oriente vers les portes
                stateToConsider.robot.turn(-(Math.PI / 2), false);

                stateToConsider.robot.setBasicDetection(true);

//                Hook hook = hookFactory.newYGreaterHook(1700);
//                hook.addCallback(new Callback(new SpeedDown(), true, stateToConsider));
//                hooksToConsider.add(hook);

                stateToConsider.robot.setForceMovement(true);

                //On ferme les portes
                stateToConsider.robot.moveLengthwise(-900, true);

                stateToConsider.robot.setBasicDetection(true);

                // stateToConsider.robot.setForceMovement(false);


                //PORTES FERMEES !
				stateToConsider.setObtainedPoints(stateToConsider.getObtainedPoints()+20);
//                stateToConsider.table.extDoorClosed = true;
//                stateToConsider.table.intDoorClosed = true;

                //if(Geometry.isBetween(stateToConsider.robot.getPosition().y, 1790, 1890))
                stateToConsider.robot.setPosition(new Vec2(stateToConsider.robot.getPosition().getX(),1840));
                stateToConsider.robot.setOrientation(-Math.PI/2);

                //else
                //	log.debug("Position trop éloignée pour se recaler en y (cylindre ?)");

                stateToConsider.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);

                stateToConsider.robot.setBasicDetection(true);
                //On avance
                stateToConsider.robot.moveLengthwiseWithoutDetection(300, false);

                stateToConsider.robot.setForceMovement(false);


                stateToConsider.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);

                stateToConsider.robot.setBasicDetection(true);
                //On avance
                stateToConsider.robot.moveLengthwiseWithoutDetection(300, false);

                stateToConsider.robot.setForceMovement(false);

                stateToConsider.robot.turn(Math.PI);
                stateToConsider.robot.setForceMovement(true);


//                hook = hookFactory.newXGreaterHook(1200);
//                hook.addCallback(new Callback(new SpeedDown(), true, stateToConsider));
//                hooksToConsider.add(hook);

                stateToConsider.robot.moveLengthwise(-500, true);

                stateToConsider.robot.setForceMovement(false);

                //  if(Geometry.isBetween(stateToConsider.robot.getPosition().x, 1300, 1400))
                //	{
                stateToConsider.robot.setPosition(new Vec2(1350, stateToConsider.robot.getPosition().getY()));
                stateToConsider.robot.setOrientation(Math.PI);
                //	}
                //    else
                //        log.debug("Position trop éloignée pour se recaler en x (cylindre ?)");

                stateToConsider.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);

                stateToConsider.robot.moveLengthwise(300, false);

                stateToConsider.robot.setBasicDetection(false);
            }
		}
		catch(Exception e)
		{
			finalize(stateToConsider,e);
		}
	}

	@Override
	public int remainingScoreOfVersion(int version, GameState state)
	{
		
		// Score maximal possible de 20 points
		int score = 20;

		// Score de 10 points fournis par porte fermée
		if (version == 0 || version == 1)
		{
			//if (state.table.extDoorClosed)
			{
				score-=10;
			}
			//if (state.table.intDoorClosed)
			{
				score-=10;
			}
		}
		return score;
	}

	@Override
	public Circle entryPosition(int version, Vec2 robotPosition) throws BadVersionException
	{
		if (version == 0 || version == 1 || version == 4)
		{
			// modification possible selon l'envergure du robot new Vec2(1135,1600)
			return new Circle(robotPosition);
		}
		else if(version == 3)
		{
			return new Circle(new Vec2(1050,1750));
		}
		else
		{
			log.debug("erreur : mauvaise version de script");
			throw new BadVersionException();
		}
	}

	@Override
	public void finalize(GameState state, Exception e)
	{
		log.debug("Exception " + e + "dans Close Doors : Lancement du Finalize !");
        state.robot.setBasicDetection(false);
    }

	@Override
	public int[] getVersion(GameState stateToConsider)
	{
		return versions;
	}


}