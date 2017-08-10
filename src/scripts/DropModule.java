package scripts;

import enums.Speed;
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
 * Script pour deposer les modules
 * Version 0 : ???
 * Version 1 : Pour la zone de côté droit
 * Version 2 : Pour la zone de côté gauche
 * Version 3 base
 *
 * @author Rem
 */

public class DropModule extends AbstractScript{

    protected DropModule(HookFactory hookFactory, Config config, Log log){
        super(hookFactory, config, log);

        versions = new Integer[]{0,1,2,3};
    }

    @Override
    public void execute(int versionToExecute, GameState actualState, ArrayList<Hook> hooksToConsider) throws UnableToMoveException, ExecuteException, SerialConnexionException, BlockedActuatorException {

        try
        {

            if(versionToExecute==1) {

                // Manoeuvre pour se caller contre le depose-module
                actualState.robot.turn(Math.PI);
                actualState.robot.moveLengthwise(-55);
                for (int i = 0; i < 2; i++) {

                    // Drop un module
                    actualState.robot.useActuator(ActuatorOrder.POUSSE_LARGUEUR, true);
                    actualState.robot.useActuator(ActuatorOrder.REPOS_LARGUEUR, false);

                    // Manoeuvre degueu pour se décaler
                    actualState.robot.moveLengthwise(60, hooksToConsider, false);

                    // Bon discord tu vas geuler mais j'avais la flemme

                    actualState.robot.turn(Math.PI - Math.asin(130.0 / 150));
                    actualState.robot.moveLengthwise(150);


                    actualState.robot.turn(Math.PI);


                    // Callage contre le depose-module
                    actualState.robot.moveLengthwise(-157, hooksToConsider, true, false, Speed.SLOW_ALL);
                }
                actualState.robot.useActuator(ActuatorOrder.POUSSE_LARGUEUR, true);
                actualState.robot.useActuator(ActuatorOrder.REPOS_LARGUEUR, false);

                // Manoeuvre degueu pour se décaler
                actualState.robot.moveLengthwise(60, hooksToConsider, false);


                // Monte le dernier module et le drop
                //actualState.robot.useActuator(ActuatorOrder.MID_ATTRAPE_D,false);
                //actualState.robot.useActuator(ActuatorOrder.MID_ATTRAPE_G,false);
                //actualState.robot.useActuator(ActuatorOrder.REPOS_CALLE_D, false);
                //actualState.robot.useActuator(ActuatorOrder.REPOS_CALLE_G, true);
                //actualState.robot.useActuator(ActuatorOrder.LEVE_ASC, true);
                //actualState.robot.useActuator(ActuatorOrder.BAISSE_ASC, true);
                //actualState.robot.useActuator(ActuatorOrder.POUSSE_LARGUEUR, true);
                //actualState.robot.useActuator(ActuatorOrder.REPOS_LARGUEUR, false);

                // Se décale de depose-module
                //actualState.robot.useActuator(ActuatorOrder.LIVRE_CALLE_D,false);
                //actualState.robot.useActuator(ActuatorOrder.LIVRE_CALLE_G,true);
                //actualState.robot.useActuator(ActuatorOrder.PREND_MODULE_D,false);
                //actualState.robot.useActuator(ActuatorOrder.PREND_MODULE_G,false);

                actualState.robot.moveLengthwise(200, hooksToConsider, false);

            }
            else if(versionToExecute==3)
            {
                //manoeuvre grossière pour se caler contre
                actualState.robot.turn(3*Math.PI/4);
                actualState.robot.moveLengthwise(330);
                actualState.robot.turn(Math.PI/4);
                actualState.robot.moveLengthwise(-40);
                // Drop un module
                actualState.robot.useActuator(ActuatorOrder.POUSSE_LARGUEUR, true);
                actualState.robot.useActuator(ActuatorOrder.REPOS_LARGUEUR, false);
                // Opération créneau
                actualState.robot.moveLengthwise(60);
                actualState.robot.turn(Math.PI/4 - Math.asin(110.0/150));
                actualState.robot.moveLengthwise(110);


                actualState.robot.turn(Math.PI/4);
                actualState.robot.moveLengthwise(-140);
                actualState.robot.useActuator(ActuatorOrder.POUSSE_LARGUEUR, true);
                actualState.robot.useActuator(ActuatorOrder.REPOS_LARGUEUR, false);

            }
            if(versionToExecute==4) {

                for (int i = 0; i < 2; i++) {


                    // Manoeuvre degueu pour se décaler
                    actualState.robot.moveLengthwise(60, hooksToConsider, false);

                    // Bon discord tu vas geuler mais j'avais la flemme

                    actualState.robot.turn(Math.PI - Math.asin(110.0 / 150));
                    actualState.robot.moveLengthwise(150);


                    actualState.robot.turn(Math.PI);


                    // Callage contre le depose-module
                    actualState.robot.moveLengthwise(-200, hooksToConsider, true, false, Speed.SLOW_ALL);

                    // Drop un module
                    actualState.robot.useActuator(ActuatorOrder.POUSSE_LARGUEUR, true);
                    actualState.robot.useActuator(ActuatorOrder.REPOS_LARGUEUR, false);

                }
                    // Se décale de depose-module
                    actualState.robot.useActuator(ActuatorOrder.LIVRE_CALLE_D, false);
                    actualState.robot.useActuator(ActuatorOrder.LIVRE_CALLE_G, true);
                    actualState.robot.useActuator(ActuatorOrder.PREND_MODULE_D, false);
                    actualState.robot.useActuator(ActuatorOrder.PREND_MODULE_G, false);

                    actualState.robot.moveLengthwise(100, hooksToConsider, false);

                }





        }
        catch(Exception e) {
            finalize(actualState, e);
        }
    }
    @Override
    public int remainingScoreOfVersion(int version, GameState state) {
        return 0;
    }

    @Override
    public Circle entryPosition(int version, int ray, Vec2 robotPosition) throws BadVersionException {

        if(version==1){

            //return new Circle(new Vec2(1170,700));
            return new Circle(robotPosition);
        }
        else if(version==2){

            return new Circle(new Vec2(-1170,795));
        }
        else if (version==3)
        {
            return new Circle(new Vec2(750,1600),0);
        }
        else if(version==4)
        {
            return new Circle(new Vec2(920,850),0);

        }
        else {
            log.debug("mauvaise version de script");
            throw new BadVersionException();
        }
    }

    @Override
    public void finalize(GameState state, Exception e) throws UnableToMoveException
    {
        log.debug("Exception " + e + "dans DropModule : Lancement du Finalize !");
        state.robot.setBasicDetection(false);
    }

    @Override
    public Integer[] getVersion(GameState stateToConsider) {
        return versions;
    }
}