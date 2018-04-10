package strategie.IA;

import enums.ScriptNames;
import exceptions.BadVersionException;
import exceptions.Locomotion.UnableToMoveException;
import hook.HookFactory;
import pathfinder.Pathfinding;
import scripts.ScriptManager;
import smartMath.Vec2;
import strategie.GameState;

import java.util.ArrayList;

public class DeposeCubes extends Node {

    public DeposeCubes(String name, int versionToExecute, ArrayList<Node> nextNodes, ScriptManager scriptManager, GameState gameState, Pathfinding pathfinding, HookFactory hookFactory) throws BadVersionException {
        super(name, versionToExecute, nextNodes, scriptManager, gameState,pathfinding,hookFactory);
        this.setScript(scriptManager.getScript(ScriptNames.DEPOSE_CUBES));
        this.setScore(0);
        this.setPosition(updatePosition());
    }


    public void exception(Exception e) {

    }


    @Override
    public void unableToMoveExceptionHandled(UnableToMoveException e) {

    }
}
