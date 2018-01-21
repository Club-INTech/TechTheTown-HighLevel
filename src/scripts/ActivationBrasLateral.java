package scripts;

import enums.ActuatorOrder;
import exceptions.BadVersionException;
import exceptions.BlockedActuatorException;
import exceptions.ExecuteException;
import exceptions.Locomotion.UnableToMoveException;
import hook.HookFactory;
import pfg.config.Config;
import robot.Locomotion;
import smartMath.Circle;
import smartMath.Vec2;
import strategie.GameState;
import utils.Log;

public class ActivationBrasLateral extends AbstractScript {

    /**Permet d'activer le bras latéral. */

    public ActivationBrasLateral(Config config, Log log, HookFactory hookFactory) {
        super(config, log, hookFactory);

        /** La version 0 active le panneau domotique.
         * La version 1 active l'abeille.*/
        versions = new Integer[]{0,1};
    }

    @Override
    public Circle entryPosition(int version, int ray, Vec2 robotPosition) throws BadVersionException {

        if (version == 0){
            int x = 370;
            int y = 0;  //a mesurer
            return new Circle(new Vec2(x,y));
        }
        else if(version == 1){
            int x = 0;  //a mesurer
            int y = 0;  //a mesurer
            return new Circle(new Vec2(x,y));
        }
        else{
            log.critical("Version invalide");
            throw new BadVersionException();
        }

    }

    @Override
    public void execute(int versionToExecute, GameState actualState) throws exceptions.Locomotion.UnableToMoveException {

        /** version de l'activation du panneau domotique */

        if (versionToExecute == 0){
            actualState.robot.moveLengthwise(0); //a mesurer
            actualState.robot.turn(-Math.PI/2);
            actualState.robot.useActuator(ActuatorOrder.ACTIVE_LE_BRAS_LATERAL,true);
        }

        /** version de l'activation du panneau domotique */

      else if(versionToExecute == 1){
            //voir la méca
        }
    }

    @Override
    public void finalize(GameState state, Exception e) {

    }

    @Override
    public Integer[] getVersion(GameState stateToConsider) {
        return new Integer[0];
    }

    @Override
    public Integer[][] getVersion2(GameState stateToConsider) {
        return new Integer[0][];
    }

    @Override
    public int remainingScoreOfVersion(int version, GameState state) {
        return 0;
    }
}
