package strategie.IA;

import enums.ScriptNames;
import exceptions.BadVersionException;
import exceptions.Locomotion.UnableToMoveException;
import hook.HookFactory;
import pathfinder.Pathfinding;
import pfg.config.Config;
import scripts.ScriptManager;
import smartMath.Vec2;
import strategie.GameState;
import table.Table;
import utils.Log;


public class DeposeCubes extends Node {

    public DeposeCubes(String name, int versionToExecute, ScriptManager scriptManager, GameState gameState, Pathfinding pathfinding, HookFactory hookFactory, Config config, Log log) throws BadVersionException {
        super(name, versionToExecute, scriptManager, gameState ,pathfinding,hookFactory,config, log);
        this.setScript(scriptManager.getScript(ScriptNames.DEPOSE_CUBES));
        this.setScore(0);
        this.setPosition(updatePosition());
    }

    @Override
    public Vec2 updatePosition() throws BadVersionException {
        return getScript().entryPosition(0, Table.entryPosition).getCenter();
    }

    @Override
    public boolean isDone() {
        return isDone;
    }

    @Override
    public void unableToMoveExceptionHandled(UnableToMoveException e) {

    }
}
