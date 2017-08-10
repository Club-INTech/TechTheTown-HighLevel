package hook.methods;

import enums.ActuatorOrder;
import hook.Executable;
import strategie.GameState;

/**
 * Methode pour prendre un module sans la table, notamment ceux pouvant etre très génant pour le déplacement du robot
 * @author Rem
 */
public class CatchModuleD implements Executable {

    @Override
    public boolean execute(GameState stateToConsider)
    {
        try {
            stateToConsider.robot.useActuator(ActuatorOrder.PREND_MODULE_D, false);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
}
