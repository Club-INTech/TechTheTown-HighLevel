package strategie.IA;

import enums.ScriptNames;
import scripts.ScriptManager;
import strategie.GameState;

import java.util.ArrayList;

public class Abeille extends Node {

    public Abeille(int versionToExecute, ArrayList<Node> nextNodes, ScriptManager scriptManager, GameState gameState) {
        super(versionToExecute, nextNodes, scriptManager, gameState);
        this.script = scriptManager.getScript(ScriptNames.ACTIVE_ABEILLE);
        this.score = 50;
    }

    @Override
    public void exception(Exception e) {

    }
}
