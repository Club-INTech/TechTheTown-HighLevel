package scripts;

import hook.HookFactory;
import pfg.config.Config;
import strategie.GameState;
import sun.font.Script;
import utils.Log;

public class MatchScript extends AbstractScript {

    public MatchScript(Config config, Log log, HookFactory hookFactory){
        super(config,log,hookFactory);
    }

    public void execute(GameState gameState) throws UnableToMoveException, BadVersionException, ExecuteException, BlockedActuatorException, PointInObstacleException{
        ActivationPanneauDomotique actPD=new ActivationPanneauDomotique(config,log,hookFactory);
        actPD.goToThenExec(0,gameState);


    }
}
