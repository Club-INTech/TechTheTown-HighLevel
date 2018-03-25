package strategie.IA;

import enums.ScriptNames;
import scripts.ScriptManager;
import strategie.GameState;

import java.util.ArrayList;

public class Panneau extends Node {

    public Panneau(int versionToExecute, ArrayList<Node> nextNodes, ScriptManager scriptManager, GameState gameState) {
        super(versionToExecute, nextNodes, scriptManager, gameState);
        this.script = scriptManager.getScript(ScriptNames.ACTIVATION_PANNEAU_DOMOTIQUE);
        this.score = 42;
    }

    @Override
    public void exception(Exception e) {

    }
}
