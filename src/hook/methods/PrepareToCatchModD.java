package hook.methods;

import enums.ActuatorOrder;
import hook.Executable;
import strategie.GameState;

/**
 * Created by rem on 5/11/17.
 */
public class PrepareToCatchModD implements Executable {

    @Override
    public boolean execute(GameState state){
        try{

            state.robot.useActuator(ActuatorOrder.MID_ATTRAPE_D, false);
            state.robot.useActuator(ActuatorOrder.REPLI_CALLE_D, false);

        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
}
