package hook.methods;

import enums.ActuatorOrder;
import hook.Executable;
import strategie.GameState;

/**
 * Created by melanie on 04/05/17.
 */
public class ReposLargueModule implements Executable {

    @Override
    public boolean execute(GameState stateToConsider)
    {
        try {
            stateToConsider.robot.useActuator(ActuatorOrder.REPOS_LARGUEUR, false);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
}
