package strategie.IA;

import enums.ScriptNames;
import exceptions.BadVersionException;
import exceptions.Locomotion.UnableToMoveException;
import hook.HookFactory;
import pathfinder.Pathfinding;
import scripts.ScriptManager;
import strategie.GameState;

import java.util.ArrayList;

public class Panneau extends Node {

    public Panneau(int versionToExecute, ArrayList<Node> nextNodes, ScriptManager scriptManager, GameState gameState, Pathfinding pathfinding, HookFactory hookFactory) throws BadVersionException {
        super(versionToExecute, nextNodes, scriptManager, gameState,pathfinding,hookFactory);
        this.setScript(scriptManager.getScript(ScriptNames.ACTIVATION_PANNEAU_DOMOTIQUE));
        this.setScore(25);
        this.setPosition(updatePosition());
    }


    public void exception(Exception e) {

    }

    @Override
    public String toString() {
        return "Panneau";
    }

    @Override
    public void unableToMoveExceptionHandled(UnableToMoveException e) {

    }
}
