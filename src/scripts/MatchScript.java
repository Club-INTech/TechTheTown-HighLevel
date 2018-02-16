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

    public void execute(GameState gameState){
        ActivationBrasLateral actblat=new ActivationBrasLateral(config,log,hookFactory);
        actblat.goToThenExec();

    }
}
