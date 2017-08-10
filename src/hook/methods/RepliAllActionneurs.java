package hook.methods;

import enums.ActuatorOrder;
import hook.Executable;
import strategie.GameState;

/**
 * Created by rem on 5/11/17.
 */
public class RepliAllActionneurs implements Executable {

    @Override
    public boolean execute(GameState state){
        try{

            state.robot.useActuator(ActuatorOrder.LEVE_ASC, true);

            state.robot.useActuator(ActuatorOrder.MID_ATTRAPE_G, false);
            state.robot.useActuator(ActuatorOrder.MID_ATTRAPE_D, true);
            state.robot.useActuator(ActuatorOrder.REPOS_CALLE_D, false);
            state.robot.useActuator(ActuatorOrder.REPOS_CALLE_G, false);
            state.robot.useActuator(ActuatorOrder.BAISSE_ASC, false);

            state.robot.useActuator(ActuatorOrder.LIVRE_CALLE_D, false);
            state.robot.useActuator(ActuatorOrder.LIVRE_CALLE_G, false);
            state.robot.useActuator(ActuatorOrder.PREND_MODULE_G, false);
            state.robot.useActuator(ActuatorOrder.PREND_MODULE_D, false);

        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
}
