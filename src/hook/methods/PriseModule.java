package hook.methods;

import enums.ActuatorOrder;
import hook.Executable;
import strategie.GameState;

/**
 * Created by melanie on 04/05/17.
 */
public class PriseModule implements Executable{

    @Override
    public boolean execute(GameState stateToConsider)
    {
        try {
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
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
}

