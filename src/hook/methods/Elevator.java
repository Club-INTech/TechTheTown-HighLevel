package hook.methods;

import enums.ActuatorOrder;
import hook.Executable;
import strategie.GameState;

/**
 * Created by rem on 5/25/17.
 */
public class Elevator implements Executable {

    @Override
    public boolean execute(GameState state){
        try{

            state.robot.useActuator(ActuatorOrder.LEVE_ASC, false);

        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
}
