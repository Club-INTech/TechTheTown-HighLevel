package hook.methods;

import enums.ActuatorOrder;
import hook.Executable;
import strategie.GameState;

/**
 * Created by rem on 5/12/17.
 */
public class ReadyPelle implements Executable {

    @Override
    public boolean execute(GameState state){
        try{

            state.robot.useActuator(ActuatorOrder.MED_PELLETEUSE, false);
            state.robot.useActuator(ActuatorOrder.PRET_PELLE, false);

        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
}
