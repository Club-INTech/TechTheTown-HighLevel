package strategie.IA;

import enums.ScriptNames;
import exceptions.BadVersionException;
import exceptions.Locomotion.UnableToMoveException;
import hook.HookFactory;
import pathfinder.Pathfinding;
import pfg.config.Config;
import scripts.ScriptManager;
import strategie.GameState;
import utils.Log;


public class TakeCubes extends Node {

    public TakeCubes(ScriptNames name, int versionToExecute, ScriptManager scriptManager, GameState gameState, Pathfinding pathfinding, HookFactory hookFactory, Config config, Log log) throws BadVersionException {
        super(name, versionToExecute, scriptManager, gameState,pathfinding,hookFactory,config, log);
        this.setScript(scriptManager.getScript(ScriptNames.TAKE_CUBES));
        this.setScore(40);
        this.setPosition(updatePosition());
    }

    @Override
    public void unableToMoveExceptionHandled(UnableToMoveException e) {

    }

    @Override
    public boolean isDone(){
        if(getVersionToExecute()==0)
            return getGameState().isTas_base_pris() || getIsDone();
        else if(getVersionToExecute()==1)
            return getGameState().isTas_chateau_eau_pris() || getIsDone();
        else if(getVersionToExecute()==2)
            return getGameState().isTas_station_epuration_pris() || getIsDone();
        else { return false;}
    }
}
