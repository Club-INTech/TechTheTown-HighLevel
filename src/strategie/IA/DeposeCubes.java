package strategie.IA;

import enums.ScriptNames;
import exceptions.BadVersionException;
import exceptions.Locomotion.UnableToMoveException;
import hook.HookFactory;
import org.classpath.icedtea.Config;
import pathfinder.Pathfinding;
import scripts.ScriptManager;
import smartMath.Vec2;
import strategie.GameState;
import table.Table;
import utils.Log;

import java.util.ArrayList;

public class DeposeCubes extends Node {

    public DeposeCubes(String name, int versionToExecute, ArrayList<Node> nextNodes, ScriptManager scriptManager, GameState gameState, Pathfinding pathfinding, HookFactory hookFactory, pfg.config.Config config, Log log) throws BadVersionException {
        super(name, versionToExecute, nextNodes, scriptManager, gameState ,pathfinding,hookFactory,config, log);
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

    public void exception(Exception e) {

    }


    @Override
    public void unableToMoveExceptionHandled(UnableToMoveException e) {

    }
}
