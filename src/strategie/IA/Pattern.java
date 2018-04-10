package strategie.IA;

import exceptions.BadVersionException;
import exceptions.BlockedActuatorException;
import exceptions.ExecuteException;
import exceptions.Locomotion.PointInObstacleException;
import exceptions.Locomotion.UnableToMoveException;
import hook.HookFactory;
import pathfinder.Pathfinding;
import scripts.ScriptManager;
import strategie.GameState;

import java.util.ArrayList;

public class Pattern extends Node{

    public Pattern(String name, int versionToExecute, ArrayList<Node> nextNodes, ScriptManager scriptManager, GameState gameState, Pathfinding pathfinding, HookFactory hookFactory) throws BadVersionException {
        super(name, versionToExecute, nextNodes, scriptManager, gameState,pathfinding,hookFactory);
        this.setScore(666);
//        this.script = scriptManager.getScript(ScriptNames.);
    }

    @Override
    public void execute(Exception e, GameState gameState) {
        System.out.println("Patern Recognition");
        setDone(true);
    }

    @Override
    public void exception(Exception e) {

    }

    @Override
    public void unableToMoveExceptionHandled(UnableToMoveException e) {

    }
}
