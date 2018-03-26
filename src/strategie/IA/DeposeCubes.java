package strategie.IA;

import enums.ScriptNames;
import scripts.ScriptManager;
import strategie.GameState;

import java.util.ArrayList;

public class DeposeCubes extends Node {

    public DeposeCubes(int versionToExecute, ArrayList<Node> nextNodes, ScriptManager scriptManager, GameState gameState) {
        super(versionToExecute, nextNodes, scriptManager, gameState);
        this.script = scriptManager.getScript(ScriptNames.DEPOSE_CUBES);
        this.score = 42;
    }

    @Override
    public void exception(Exception e) {

    }

    @Override
    public String toString() {
        return "DeposeCube";
    }
}
