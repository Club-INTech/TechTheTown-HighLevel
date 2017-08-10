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

//TODO calibrer les WaitForCompletion

/** Script permettant de livrer les boules déjà prises, dans la zone de départ ou sur le robot secondaire
 *
 * Version 0: suppose que le robot est déjà placé, sans la base roulante
 * Version 1: Va vers la zone de départ, face au robot secondaire, livre les boules.
 * Version 2: Version qui chope le module près de la zone de depose des balls avant de les déposées
 *
 * @author tic-tac
 */
public class DropBalls extends AbstractScript
{
    /**
     * Constructeur à appeller lorsqu'un script héritant de la classe AbstractScript est instancié.
     * Le constructeur se charge de renseigner la hookFactory, le système de config et de log.
     *
     * @param hookFactory La factory a utiliser pour générer les hooks dont pourra avoir besoin le script
     * @param config      le fichier de config a partir duquel le script pourra se configurer
     * @param log         le système de log qu'utilisera le script
     */
    protected DropBalls(HookFactory hookFactory, Config config, Log log)
    {
        super(hookFactory, config, log);

        versions = new Integer[]{0};
    }

    @Override
    public void execute(int versionToExecute, GameState actualState, ArrayList<Hook> hooksToConsider) throws ExecuteException, UnableToMoveException, BlockedActuatorException
    {
        try{
            if (versionToExecute==0)
            {
                //abaisser les bras au plus bas
                actualState.robot.useActuator(ActuatorOrder.LIVRAISON_PELLETEUSE, true);

                //rotation de la pelle jusqu'à la position de livraison
                actualState.robot.useActuator(ActuatorOrder.LIVRE_PELLE, true);

                //lever les bras jusqu'à la position intermédiaire
                actualState.robot.useActuator(ActuatorOrder.MED_PELLETEUSE, true);

                //tourner la pelle jusqu'à la position initiale
                actualState.robot.useActuator(ActuatorOrder.PRET_PELLE, true);

                //monter les bras le plus haut \o/
                actualState.robot.useActuator(ActuatorOrder.REPLIER_PELLETEUSE, true);
            }
            else if (versionToExecute==1)
            {
                // Manoeuvre
                actualState.robot.turn(-Math.PI/2);

                //Se caler contre la zone de livraison
                actualState.robot.setLocomotionSpeed(Speed.SLOW_ALL);
                actualState.robot.moveLengthwise(250,hooksToConsider, true);

                //abaisser les bras au plus bas
                actualState.robot.useActuator(ActuatorOrder.LIVRAISON_PELLETEUSE, true);

                //rotation de la pelle jusqu'à la position de livraison
                actualState.robot.useActuator(ActuatorOrder.LIVRE_PELLE, true);

                //éventuellement, attendre le temps que les boules tombent (en millisecondes)
                //actualstate.robot.sleep(1000);

                //lever les bras jusqu'à la position intermédiaire
                actualState.robot.useActuator(ActuatorOrder.MED_PELLETEUSE, true);

                //Reculer un peu
                actualState.robot.moveLengthwise(-150);

                //tourner la pelle jusqu'à la position initiale
                actualState.robot.useActuator(ActuatorOrder.PRET_PELLE, true);

                //monter les bras le plus haut \o/
                actualState.robot.useActuator(ActuatorOrder.REPLIER_PELLETEUSE, true);

                // Recule pour se décaler de l'obstacle
                actualState.robot.moveLengthwise(-450);

            }
            else if (versionToExecute == 2){

                // Manoeuvre pour arriver au niveau du module et être prêt à choper le module
                actualState.robot.turn(Math.PI/2);
                actualState.robot.useActuator(ActuatorOrder.MID_ATTRAPE_D, true);
                actualState.robot.useActuator(ActuatorOrder.REPLI_CALLE_D, false);
                actualState.robot.useActuator(ActuatorOrder.REPOS_ATTRAPE_D, false);

                actualState.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
                actualState.robot.moveLengthwise(-280);

                // Chope le module billy !
                actualState.robot.useActuator(ActuatorOrder.PREND_MODULE_D, true);
                actualState.robot.useActuator(ActuatorOrder.REPOS_ATTRAPE_D, true);
                actualState.robot.useActuator(ActuatorOrder.LIVRE_CALLE_D, true);

                // Et remontes-le à l'aide de l'ascenceur
                actualState.robot.useActuator(ActuatorOrder.MID_ATTRAPE_G, true);
                actualState.robot.useActuator(ActuatorOrder.REPOS_LARGUEUR,false);
                actualState.robot.useActuator(ActuatorOrder.REPOS_CALLE_D, false);
                actualState.robot.useActuator(ActuatorOrder.REPOS_CALLE_G, true);
                actualState.robot.useActuator(ActuatorOrder.LEVE_ASC, true);
                actualState.robot.useActuator(ActuatorOrder.BAISSE_ASC, true);

                // Repli tout le bouzin
                actualState.robot.useActuator(ActuatorOrder.LIVRE_CALLE_G, false);
                actualState.robot.useActuator(ActuatorOrder.LIVRE_CALLE_D, true);
                actualState.robot.useActuator(ActuatorOrder.PREND_MODULE_G, false);
                actualState.robot.useActuator(ActuatorOrder.PREND_MODULE_D, false);

                actualState.robot.moveLengthwise(-80);
                actualState.robot.useActuator(ActuatorOrder.POUSSE_LARGUEUR, true);

                actualState.robot.moveLengthwise(80);

                // Et maintenant dépose les boules
                actualState.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
                actualState.robot.turn(-Math.PI/4);

                actualState.robot.moveLengthwise(110);
                actualState.robot.turn(-Math.PI/2);


                //abaisser les bras au plus bas
                actualState.robot.useActuator(ActuatorOrder.LIVRAISON_PELLETEUSE, true);

                //rotation de la pelle jusqu'à la position de livraison
                actualState.robot.useActuator(ActuatorOrder.LIVRE_PELLE, true);

                //lever les bras jusqu'à la position intermédiaire
                actualState.robot.useActuator(ActuatorOrder.MED_PELLETEUSE, true);

                //tourner la pelle jusqu'à la position initiale
                actualState.robot.useActuator(ActuatorOrder.PRET_PELLE, true);

                //monter les bras le plus haut \o/
                actualState.robot.useActuator(ActuatorOrder.MED_PELLETEUSE, true);

                // Manoeuvre pour se dégager (On test le pathfinding en même temps puisqu'on le lâche dans un obstacle)
                //actualState.robot.setLocomotionSpeed(Speed.SLOW_ALL);
                actualState.robot.moveLengthwise(-260);
                actualState.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
                actualState.robot.turn(3*Math.PI/4 );
                actualState.robot.moveLengthwise(400);
                actualState.robot.turn(-Math.PI/2 );

            }
        }
        catch(Exception e)
        {
            log.critical("Robot ou actionneur bloqué dans DropBalls");
            finalize(actualState, e);
        }
    }

    @Override
    public int remainingScoreOfVersion(int version, GameState state)
    {

        //TODO: si on considère que l'action est complètement successfull, on a 5 boules au moins(pour une version
        //TODO:utilisée dans un petit cratère, donc 5*3=15 points obtenus par cette action

        int score = 0;
        return score;
    }

    @Override
    public Circle entryPosition(int version, int ray, Vec2 robotPosition) throws BadVersionException
    {
        if (version == 0 || version == 1) //Le robot va aller livrer depuis la position de départ du robot, où qu'il soit
        {
            // modification possible selon l'envergure du robot new Vec2(1135,1600)
            return new Circle(new Vec2(1090,805));
        }
        else if (version == 2)
        {
            return new Circle(new Vec2(1200,715),0);
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
        log.debug("Exception " + e +"dans DropBalls : Lancement du finalize !");
        state.robot.setBasicDetection(false);
    }

    @Override
    public Integer[] getVersion(GameState stateToConsider) {
        return versions;
    }
}
