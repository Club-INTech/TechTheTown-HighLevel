package strategie.IA;

import enums.ScriptNames;
import exceptions.BadVersionException;
import scripts.ScriptManager;
import strategie.GameState;

import java.util.ArrayList;

public class Panneau extends Node {

    public Panneau(int versionToExecute, ArrayList<Node> nextNodes, ScriptManager scriptManager, GameState gameState) throws BadVersionException {
        super(versionToExecute, nextNodes, scriptManager, gameState);
        this.setScript(scriptManager.getScript(ScriptNames.ACTIVATION_PANNEAU_DOMOTIQUE));
        this.setScore(42);
        this.setPosition(updatePosition());
    }

    @Override
    public void exception(Exception e) {

    }

    @Override
    public String toString() {
        return "Panneau";
    }
}
