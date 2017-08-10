

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

import enums.ActuatorOrder;
import enums.Speed;
import exceptions.BadVersionException;
import exceptions.BlockedActuatorException;
import exceptions.ExecuteException;
import exceptions.Locomotion.UnableToMoveException;
import hook.Hook;
import hook.types.HookFactory;
import smartMath.Circle;
import smartMath.Vec2;
import strategie.GameState;
import utils.Config;
import utils.Log;

import java.util.ArrayList;

    // TODO: calibrer les WaitforCompletion + faire toute les versions pour tout les cratères

/**
 * Script pour ramasser les balles dans un cratère avec la pelleteuse
 *
 * Version 0: robot déjà placé, il ramasse juste les balles (pour tests sans base roulante)
 * Version 1: ramassage dans un cratère proche de la zone de départ(l'autre petit cratère est plus teshenique)
 * Version 2: ramassage dans le cratère derrière la base lunaire
 * version 3: dans le cratère près de la base masi par l'autre côté, plus module sur le chemin
 *
 * @author Gaelle, tic-tac, Rem
 */

public class CatchBalls extends AbstractScript {

    public CatchBalls(HookFactory hookFactory, Config config, Log log)
    {
        super(hookFactory, config, log);

        versions = new Integer[]{0,1,2,3};
    }


    /**
     * On lance le script choisi.
     * @param versionToExecute Version a lancer
     * @param stateToConsider Notre bon vieux robot
     * @param hooksToConsider Les hooks nécessaires pour l'execution du script
     *
     */

    // TODO : prendre en compte le cratère considéré (6 différents sur la table/ 3 en considérant la symétrie)

    @Override
    public void execute(int versionToExecute, GameState stateToConsider, ArrayList<Hook> hooksToConsider) throws ExecuteException, UnableToMoveException, BlockedActuatorException
    {
        try
        {
            if(versionToExecute == 0){
                stateToConsider.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);


                // Prepare la pelleteuse avant déploiement(bras relevés mais légèrement abaissés pour ne pas bloquer la rotation de la pelle, puis pelle mise à 300°)
                stateToConsider.robot.useActuator(ActuatorOrder.MED_PELLETEUSE, true);
                stateToConsider.robot.useActuator(ActuatorOrder.PRET_PELLE, true);

                // Déploie la pelleteuse (descendre les bras, avec pelle toujours à 300 °)
                stateToConsider.robot.useActuator(ActuatorOrder.LIVRAISON_PELLETEUSE, true);

                // Fait tourner la pelleteuse (jusqu'à ~150 ou 200°)
                stateToConsider.robot.useActuator(ActuatorOrder.PREND_PELLE, false);

                // Déploie la pelleteuse (descendre les bras, avec pelle toujours à 300 °)
                stateToConsider.robot.useActuator(ActuatorOrder.DEPLOYER_PELLETEUSE_BAS, true);

                // "Lèves les bras Maurice, c'est plus rigolo quand tu lèves les bras !", RIP King Julian
                stateToConsider.robot.useActuator(ActuatorOrder.TIENT_BOULES,false);
                stateToConsider.robot.useActuator(ActuatorOrder.MED_PELLETEUSE, false);
            }
            if(versionToExecute == 1){
                stateToConsider.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);

                // Calcul de l'angle pour se diriger vers le centre du robot
                Vec2 posCratere= new Vec2(850, 540);
                Vec2 posRobot=stateToConsider.robot.getPosition();
                Vec2 vec = posCratere.minusNewVector(posRobot);

                // Manoeuvre pour se diriger vers le cratère
                //stateToConsider.robot.useActuator(ActuatorOrder.PREND_MODULE_D, true);

                //stateToConsider.robot.switchSensor();
                stateToConsider.robot.turn(vec.getA());
                stateToConsider.robot.moveLengthwise(200); // TODO config

                // Prepare la pelleteuse avant déploiement(bras relevés mais légèrement abaissés pour ne pas bloquer la rotation de la pelle, puis pelle mise à 300°)
                stateToConsider.robot.useActuator(ActuatorOrder.MED_PELLETEUSE, true);
                stateToConsider.robot.useActuator(ActuatorOrder.PRET_PELLE, true);

                // Déploie la pelleteuse (descendre les bras, avec pelle toujours à 300 °)
                stateToConsider.robot.useActuator(ActuatorOrder.LIVRAISON_PELLETEUSE, true);

                // Fait tourner la pelleteuse (jusqu'à ~150 ou 200°)
                stateToConsider.robot.useActuator(ActuatorOrder.PREND_PELLE, false);

                // Déploie la pelleteuse (descendre les bras, avec pelle toujours à 300 °)
                stateToConsider.robot.useActuator(ActuatorOrder.DEPLOYER_PELLETEUSE_BAS, true);

                // "Lèves les bras Maurice, c'est plus rigolo quand tu lèves les bras !", RIP King Julian
                stateToConsider.robot.useActuator(ActuatorOrder.TIENT_BOULES,false);
                stateToConsider.robot.useActuator(ActuatorOrder.MED_PELLETEUSE, false);

                //stateToConsider.robot.switchSensor();
            }

            else if(versionToExecute == 2){

                stateToConsider.robot.turn(-Math.PI/2);
                stateToConsider.robot.moveLengthwise(-540);

                //Attraper le module avec le côté droit

                // Manoeuvre pour arriver au niveau du module et être prêt à choper le module
                stateToConsider.robot.useActuator(ActuatorOrder.MID_ATTRAPE_D, true);
                stateToConsider.robot.useActuator(ActuatorOrder.REPLI_CALLE_D, false);
                stateToConsider.robot.useActuator(ActuatorOrder.REPOS_ATTRAPE_D, false);
                stateToConsider.robot.moveLengthwise(-130); //TODO config

                // Attraper le module
                stateToConsider.robot.useActuator(ActuatorOrder.PREND_MODULE_D, true);
                stateToConsider.robot.useActuator(ActuatorOrder.REPOS_ATTRAPE_D, true);
                stateToConsider.robot.useActuator(ActuatorOrder.LIVRE_CALLE_D, true);
                stateToConsider.robot.useActuator(ActuatorOrder.REPOS_CALLE_D, true);
                stateToConsider.robot.useActuator(ActuatorOrder.LIVRE_CALLE_D, true);

                // Et remonte-le à l'aide de l'ascenceur
                stateToConsider.robot.useActuator(ActuatorOrder.MID_ATTRAPE_G, true);
                stateToConsider.robot.useActuator(ActuatorOrder.REPOS_LARGUEUR,false);
                stateToConsider.robot.useActuator(ActuatorOrder.REPOS_CALLE_G, false);
                stateToConsider.robot.useActuator(ActuatorOrder.REPOS_CALLE_D, true);
                stateToConsider.robot.useActuator(ActuatorOrder.LEVE_ASC, true);
                stateToConsider.robot.useActuator(ActuatorOrder.BAISSE_ASC, true);

                // Replie le tout
                stateToConsider.robot.useActuator(ActuatorOrder.LIVRE_CALLE_D, false);
                stateToConsider.robot.useActuator(ActuatorOrder.LIVRE_CALLE_G, true);
                stateToConsider.robot.useActuator(ActuatorOrder.PREND_MODULE_D, false);
                stateToConsider.robot.useActuator(ActuatorOrder.PREND_MODULE_G, false);


                //stateToConsider.robot.turn(Math.PI-0.55);
               // stateToConsider.robot.moveLengthwise(150);
                stateToConsider.robot.turn(Math.PI-0.65); //TODO config
                stateToConsider.robot.moveLengthwise(250);


            // Prepare la pelleteuse avant déploiement(bras relevés mais légèrement abaissés pour ne pas bloquer la rotation de la pelle, puis pelle mise à 300°)
            stateToConsider.robot.useActuator(ActuatorOrder.MED_PELLETEUSE, true);
            stateToConsider.robot.useActuator(ActuatorOrder.PRET_PELLE, true);

            // Déploie la pelleteuse (descendre les bras, avec pelle toujours à 300 °)
            stateToConsider.robot.useActuator(ActuatorOrder.LIVRAISON_PELLETEUSE, true);

            // Fait tourner la pelleteuse (jusqu'à ~150 ou 200°)
            stateToConsider.robot.useActuator(ActuatorOrder.PREND_PELLE, true);

            // "Lèves les bras Maurice, c'est plus rigolo quand tu lèves les bras !", RIP King Julian
            stateToConsider.robot.useActuator(ActuatorOrder.TIENT_BOULES,true);
            stateToConsider.robot.useActuator(ActuatorOrder.MED_PELLETEUSE, true);

            stateToConsider.robot.turn(Math.PI-0.42);

            }

            if (versionToExecute == 1){
                //stateToConsider.robot.setLocomotionSpeed(Speed.SLOW_ALL);
                stateToConsider.robot.moveLengthwise(-210); //TODO config
                stateToConsider.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
            }
            else if(versionToExecute == 2){
                stateToConsider.robot.moveLengthwise(-60); //TODO configs
                stateToConsider.robot.turn(Math.PI/4);
                stateToConsider.robot.moveLengthwise(-50);
                // Drop un module
                stateToConsider.robot.useActuator(ActuatorOrder.POUSSE_LARGUEUR_LENT, true);
                stateToConsider.robot.useActuator(ActuatorOrder.REPOS_LARGUEUR, false);
                // Opération créneau
                //stateToConsider.robot.moveLengthwise(60);
                //stateToConsider.robot.turn(Math.PI/4 - Math.asin(110.0/150));
                //stateToConsider.robot.moveLengthwise(110);


                //stateToConsider.robot.turn(Math.PI/4);
                //stateToConsider.robot.moveLengthwise(-140);
                //stateToConsider.robot.useActuator(ActuatorOrder.POUSSE_LARGUEUR, true);
                //stateToConsider.robot.useActuator(ActuatorOrder.REPOS_LARGUEUR, false);
                stateToConsider.robot.moveLengthwise(60);
                stateToConsider.robot.turn(-Math.PI/3);
                stateToConsider.robot.moveLengthwise(400);
                //stateToConsider.robot.turn(-Math.PI/2);
                //stateToConsider.robot.moveLengthwise(200);
            }

            if (versionToExecute == 3) {


                stateToConsider.robot.turn(-Math.PI/2);
                stateToConsider.robot.moveLengthwise(1000);



                stateToConsider.robot.turn(Math.PI);

                stateToConsider.robot.useActuator(ActuatorOrder.MID_ATTRAPE_G, true);
                stateToConsider.robot.useActuator(ActuatorOrder.REPLI_CALLE_G, false);
                stateToConsider.robot.useActuator(ActuatorOrder.REPOS_ATTRAPE_G, false);

                stateToConsider.robot.moveLengthwise(-100);
                // Attraper le module
                stateToConsider.robot.useActuator(ActuatorOrder.PREND_MODULE_G, true);
                stateToConsider.robot.useActuator(ActuatorOrder.REPOS_ATTRAPE_G, true);
                stateToConsider.robot.useActuator(ActuatorOrder.LIVRE_CALLE_G, true);
                stateToConsider.robot.useActuator(ActuatorOrder.REPOS_CALLE_G, true);
                stateToConsider.robot.useActuator(ActuatorOrder.LIVRE_CALLE_G, true);

                // Et remonte-le à l'aide de l'ascenceur
                stateToConsider.robot.useActuator(ActuatorOrder.MID_ATTRAPE_D, true);
                stateToConsider.robot.useActuator(ActuatorOrder.REPOS_LARGUEUR,false);
                stateToConsider.robot.useActuator(ActuatorOrder.REPOS_CALLE_G, false);
                stateToConsider.robot.useActuator(ActuatorOrder.REPOS_CALLE_D, true);
                stateToConsider.robot.useActuator(ActuatorOrder.LEVE_ASC, true);
                stateToConsider.robot.useActuator(ActuatorOrder.BAISSE_ASC, true);

                // Replie le tout
                stateToConsider.robot.useActuator(ActuatorOrder.LIVRE_CALLE_D, false);
                stateToConsider.robot.useActuator(ActuatorOrder.LIVRE_CALLE_G, true);
                stateToConsider.robot.useActuator(ActuatorOrder.PREND_MODULE_D, false);
                stateToConsider.robot.useActuator(ActuatorOrder.PREND_MODULE_G, false);


                //Drop le module
                stateToConsider.robot.useActuator(ActuatorOrder.POUSSE_LARGUEUR, true);
                stateToConsider.robot.useActuator(ActuatorOrder.REPOS_LARGUEUR, false);

                stateToConsider.robot.moveLengthwise(100);

                stateToConsider.robot.turn(-Math.PI/2);

                stateToConsider.robot.moveLengthwise(150);
                //abaisser les bras au plus bas
                stateToConsider.robot.useActuator(ActuatorOrder.LIVRAISON_PELLETEUSE, true);

                //rotation de la pelle jusqu'à la position de livraison
                stateToConsider.robot.useActuator(ActuatorOrder.LIVRE_PELLE, true);

                //éventuellement, attendre le temps que les boules tombent (en millisecondes)
                //actualstate.robot.sleep(1000);

                //lever les bras jusqu'à la position intermédiaire
                stateToConsider.robot.useActuator(ActuatorOrder.MED_PELLETEUSE, true);

                //Reculer un peu
                stateToConsider.robot.moveLengthwise(-150);

                //tourner la pelle jusqu'à la position initiale
                stateToConsider.robot.useActuator(ActuatorOrder.PRET_PELLE, true);

                //monter les bras le plus haut \o/
                stateToConsider.robot.useActuator(ActuatorOrder.REPLIER_PELLETEUSE, true);

                stateToConsider.robot.turn(Math.PI/2);


                // Calcule de l'angle pour se diriger vers le centre du robot
                Vec2 posCratere= new Vec2(850, 540);
                Vec2 posRobot=stateToConsider.robot.getPosition();
                Vec2 vec = posCratere.minusNewVector(posRobot);

                // Manoeuvre pour se diriger vers le cratère
                //stateToConsider.robot.useActuator(ActuatorOrder.PREND_MODULE_D, true);
                stateToConsider.robot.turn(vec.getA());
                stateToConsider.robot.moveLengthwise(160);

                // Prepare la pelleteuse avant déploiement(bras relevés mais légèrement abaissés pour ne pas bloquer la rotation de la pelle, puis pelle mise à 300°)
                stateToConsider.robot.useActuator(ActuatorOrder.MED_PELLETEUSE, true);
                stateToConsider.robot.useActuator(ActuatorOrder.PRET_PELLE, true);

                // Déploie la pelleteuse (descendre les bras, avec pelle toujours à 300 °)
                stateToConsider.robot.useActuator(ActuatorOrder.LIVRAISON_PELLETEUSE, true);

                // Fait tourner la pelleteuse (jusqu'à ~150 ou 200°)
                stateToConsider.robot.useActuator(ActuatorOrder.PREND_PELLE, true);

                // "Lèves les bras Maurice, c'est plus rigolo quand tu lèves les bras !", RIP King Julian
                stateToConsider.robot.useActuator(ActuatorOrder.TIENT_BOULES,false);
                stateToConsider.robot.useActuator(ActuatorOrder.MED_PELLETEUSE, false);

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
        int score = 0;
        return score;
    }

    @Override
    public Circle entryPosition(int version, int ray, Vec2 robotPosition) throws BadVersionException
    {
        if (version == 0 )
        {
            return new Circle(robotPosition);
        }
        else if (version == 1)
        {
            return new Circle(new Vec2(850,620), ray + 155, -Math.PI/6, 5*Math.PI/6, true);
        }
        else if (version == 2)
        {
            return new Circle(new Vec2(890, 1150), 0);
        }
        else if (version ==3)
        {
            return new Circle(new Vec2(1200,1000), 0);
        }
        else
        {
            log.debug("erreur : mauvaise version de script");
            throw new BadVersionException();
        }
    }

    @Override
    public void finalize(GameState state, Exception e) throws UnableToMoveException
    {
        log.debug("Exception " + e + "dans CatchBalls : Lancement du Finalize !");
        state.robot.setBasicDetection(false);
    }

    @Override
    public Integer[] getVersion(GameState stateToConsider)
    {
        return versions;
    }

}