package scripts;

import enums.ActuatorOrder;
import exceptions.BadVersionException;
import exceptions.BlockedActuatorException;
import exceptions.ExecuteException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;
import hook.Hook;
import hook.types.HookFactory;
import smartMath.Circle;
import smartMath.Vec2;
import strategie.GameState;
import utils.Config;
import utils.Log;

import java.util.ArrayList;

/**
 * Script pour prendre les modules et les stocker
 * Version 0 : Version immobile avec le bras droit
 * Version 1 : Pour la fusé de la zone de départ par la droite (censé être éxecuté dès le début du match)
 * Version 2 : Pour la fusé de la zone de départ par la gauche
 * Version 3 : Pour la fusé pret du cratere
 * Version 4 : Pour le module près de la zone de départ
 * Version 5 : Pour le module près de la base lunaire
 * Version 6 : V1 sans les actionneurs
 * @author Rem
 */

public class CatchModule extends AbstractScript {


    protected CatchModule(HookFactory hookFactory, Config config, Log log) {
        super(hookFactory, config, log);

        versions = new Integer[]{0,1,2,3,6};
    }

    // TODO : Calibrer les waitForCompletions, notamment ceux qui ne représentent pas le temps réel de l'action (LIVRE_CALLE_G et peut-être BAISSE_ASC)

    @Override
    public void execute(int versionToExecute, GameState actualState, ArrayList<Hook> hooksToConsider) throws UnableToMoveException, ExecuteException, SerialConnexionException, BlockedActuatorException {

        try {

            if (versionToExecute == 0){

                // Déploie l'attrape-module et la calle
                actualState.robot.useActuator(ActuatorOrder.LIVRE_CALLE_G, false);
                actualState.robot.useActuator(ActuatorOrder.REPOS_ATTRAPE_D, true);
                actualState.robot.useActuator(ActuatorOrder.REPLI_CALLE_D, true);
                actualState.robot.useActuator(ActuatorOrder.BAISSE_ASC, true);
                actualState.robot.useActuator(ActuatorOrder.ETAT_CONTACTEUR2, false);
            }

            if (versionToExecute == 1) {

                // Se place dans la bonne direction : (370,300), Or 0.58 (3PI/16 ~ 0.59)
                actualState.robot.turn(0);

                // Recule pour arriver devant la fusé
                actualState.robot.moveLengthwise(-155);

                // Déploie l'attrape-module et la calle
                actualState.robot.useActuator(ActuatorOrder.LIVRE_CALLE_G, false);
                actualState.robot.useActuator(ActuatorOrder.REPOS_ATTRAPE_D, true);
                actualState.robot.useActuator(ActuatorOrder.REPLI_CALLE_D, true);

            } else if (versionToExecute == 2) {

                // Se place dans la bonne direction
                actualState.robot.turn(0);

                // Avance pour arriver devant la fusé
                actualState.robot.moveLengthwise(-150);

                // Déploie l'attrape-module et la calle-bisou
                actualState.robot.useActuator(ActuatorOrder.REPLI_CALLE_G, true);
                actualState.robot.useActuator(ActuatorOrder.REPOS_ATTRAPE_G, true);

            }
            else if (versionToExecute == 3) {

                // Fait une manoeuvre pour arriver à la bonne position sans risque de toucher un obstacle
                actualState.robot.turn(-Math.PI/2 - Math.acos(0.8));
                actualState.robot.moveLengthwise(-250);
                actualState.robot.turn(-Math.PI/2);

                // Déploie l'attrape-module et la calle-bisou
                actualState.robot.useActuator(ActuatorOrder.REPLI_CALLE_D, true);
                actualState.robot.useActuator(ActuatorOrder.REPOS_ATTRAPE_D, true);
            }
            else if (versionToExecute == 4){

                // Manoeuvre
                actualState.robot.turn(0);
                actualState.robot.moveLengthwise(500);

                // Déploiement
                actualState.robot.useActuator(ActuatorOrder.REPLI_CALLE_G, true);
                actualState.robot.useActuator(ActuatorOrder.REPOS_ATTRAPE_G, true);



            }
            else if (versionToExecute ==6) {
                // Se place dans la bonne direction : (370,300), Or 0.58 (3PI/16 ~ 0.59)
                actualState.robot.turn(0);

                // Recule pour arriver devant la fusé
                actualState.robot.moveLengthwise(-100);

            }

            if (versionToExecute == 0 || versionToExecute == 1 || versionToExecute == 3) {

                for (int i = 0; i < 4; i++) {

                    // Attrape le module
                    actualState.robot.useActuator(ActuatorOrder.PREND_MODULE_D, true);

                    // Recule l'attrape module pour laisser passer le bras de la calle
                    actualState.robot.useActuator(ActuatorOrder.REPOS_ATTRAPE_D, true);

                    // Calle le module dans l'ascenceur
                    actualState.robot.useActuator(ActuatorOrder.LIVRE_CALLE_D, false);

                    // Repli l'attrape-module
                    actualState.robot.useActuator(ActuatorOrder.REPOS_ATTRAPE_D,false);

                    // Repli les calles
                    actualState.robot.useActuator(ActuatorOrder.MID_ATTRAPE_G, true);
                    actualState.robot.useActuator(ActuatorOrder.REPOS_CALLE_D, false);
                    actualState.robot.useActuator(ActuatorOrder.REPOS_CALLE_G, true);

                    if (i != 3) {

                        // Monte la plaque
                        actualState.robot.useActuator(ActuatorOrder.LEVE_ASC, true);
                        actualState.robot.useActuator(ActuatorOrder.ETAT_CONTACTEUR1,false);

                        // Baisse la plaque
                        actualState.robot.useActuator(ActuatorOrder.BAISSE_ASC, true);
                        actualState.robot.useActuator(ActuatorOrder.ETAT_CONTACTEUR2, false);

                    }

                    // Remet en place les calles
                    actualState.robot.useActuator(ActuatorOrder.LIVRE_CALLE_G, false);
                    actualState.robot.useActuator(ActuatorOrder.REPLI_CALLE_D, true);
                }
            }
            else if (versionToExecute ==6) {}
            else {
                for (int i = 0; i < 4; i++) {

                    // Attrape le module
                    actualState.robot.useActuator(ActuatorOrder.PREND_MODULE_G, true);

                    // Recule l'attrape module pour laisser passer le bras de la calle
                    actualState.robot.useActuator(ActuatorOrder.MID_ATTRAPE_G, false);

                    // Calle le module dans l'ascenceur
                    actualState.robot.useActuator(ActuatorOrder.LIVRE_CALLE_G, false);

                    // Repli l'attrape-module
                    actualState.robot.useActuator(ActuatorOrder.REPOS_ATTRAPE_G,true);

                    // Repli les calles
                    actualState.robot.useActuator(ActuatorOrder.REPLI_CALLE_G, false);
                    actualState.robot.useActuator(ActuatorOrder.MID_ATTRAPE_D, true);
                    actualState.robot.useActuator(ActuatorOrder.REPLI_CALLE_D, true);

                    if (i != 3) {

                        // Monte la plaque
                        actualState.robot.useActuator(ActuatorOrder.LEVE_ASC, true);
                        actualState.robot.useActuator(ActuatorOrder.ETAT_CONTACTEUR1, false);

                        // Baisse la plaque
                        actualState.robot.useActuator(ActuatorOrder.BAISSE_ASC, true);
                        actualState.robot.useActuator(ActuatorOrder.ETAT_CONTACTEUR2, false);

                    }

                    // Remet en place la calle gauche
                    actualState.robot.useActuator(ActuatorOrder.LIVRE_CALLE_D, false);
                    }
                }
            }

            catch (Exception e)
                {
                    finalize(actualState,e);
                }
        }

        @Override
        public int remainingScoreOfVersion(int version, GameState state) {
            return 0;
        }

        @Override
        public Circle entryPosition(int version, int ray, Vec2 robotPosition) throws BadVersionException {

            if (version == 0)
            {
                return new Circle(robotPosition);
            }
            else if (version == 1 || version == 6)
            {
                return new Circle(new Vec2(480,213));
            }
            else if (version == 2)
            {
                return new Circle(new Vec2(200,255));
            }
            else if (version == 3)
            {
                return new Circle(new Vec2(1124,1150));
            }
            else if (version == 4)
            {
                return new Circle(new Vec2(400,700));  //TODO bien mesurer le x
            }
            else if (version == 5){
                return new Circle(new Vec2(0,0)); //TODO trouver la position, et faire un script hybride en mélangeant CatchBalls/Module
            }

            else{
                log.debug("erreur : mauvaise version de script");
                throw new BadVersionException();
            }
        }

        @Override
        public void finalize(GameState state, Exception e) throws UnableToMoveException
        {
            log.debug("Exception " + e + "dans CatchModule : Lancement du Finalize !");
            state.robot.setBasicDetection(false);
        }

        @Override
        public Integer[] getVersion(GameState stateToConsider) {
            return versions;
        }
    }