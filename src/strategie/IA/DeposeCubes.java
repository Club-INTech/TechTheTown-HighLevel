package strategie.IA;

import enums.ScriptNames;
import exceptions.BadVersionException;
import scripts.ScriptManager;
import smartMath.Vec2;
import strategie.GameState;

import java.util.ArrayList;

public class DeposeCubes extends Node {

    public DeposeCubes(int versionToExecute, ArrayList<Node> nextNodes, ScriptManager scriptManager, GameState gameState) throws BadVersionException {
        super(versionToExecute, nextNodes, scriptManager, gameState);
        this.setScript(scriptManager.getScript(ScriptNames.DEPOSE_CUBES));
        this.setScore(0);
        this.setPosition(updatePosition());
    }

    @Override
    public void exception(Exception e) {

    }

    @Override
    public String toString() {
        return "DeposeCube";
    }
}
