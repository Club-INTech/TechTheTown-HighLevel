package strategie.IA;

import enums.ScriptNames;
import exceptions.BadVersionException;
import scripts.ScriptManager;
import smartMath.Vec2;
import strategie.GameState;

import java.util.ArrayList;

public class Abeille extends Node {

    public Abeille(int versionToExecute, ArrayList<Node> nextNodes, ScriptManager scriptManager, GameState gameState) throws BadVersionException {
        super(versionToExecute, nextNodes, scriptManager, gameState);
        this.setScript(scriptManager.getScript(ScriptNames.ACTIVE_ABEILLE));
        this.setScore(50);
        this.setPosition(new Vec2(1300,1765));
    }

    @Override
    public void exception(Exception e) {

    }

    @Override
    public String toString() {
        return "Abeille";
    }


}
