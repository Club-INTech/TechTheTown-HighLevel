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

import java.util.ArrayList;

public class Panneau extends Node {

    public Panneau(String name, int versionToExecute, ArrayList<Node> nextNodes, ScriptManager scriptManager, GameState gameState, Pathfinding pathfinding, HookFactory hookFactory, Config config, Log log) throws BadVersionException {
        super(name, versionToExecute, nextNodes, scriptManager, gameState,pathfinding,hookFactory,config, log);
        this.setScript(scriptManager.getScript(ScriptNames.ACTIVATION_PANNEAU_DOMOTIQUE));
        this.setScore(25);
        this.setPosition(updatePosition());
    }


    public void exception(Exception e) {

    }

    @Override
    public void unableToMoveExceptionHandled(UnableToMoveException e) {

    }

    @Override
    public boolean isDone() {
        return getGameState().isPanneauActive();
    }
}
