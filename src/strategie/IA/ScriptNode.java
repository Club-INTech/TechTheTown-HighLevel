package strategie.IA;

import exceptions.BadVersionException;
import exceptions.BlockedActuatorException;
import exceptions.ExecuteException;
import exceptions.Locomotion.PointInObstacleException;
import exceptions.Locomotion.UnableToMoveException;
import pathfinder.Pathfinding;
import scripts.AbstractScript;
import strategie.GameState;

import java.util.ArrayList;

public class ScriptNode extends Node{


    public ScriptNode(Node previous, ArrayList<Node> nextNodes, Boolean condition, String action, long time, Pathfinding pathfinding, GameState gamestate, AbstractScript script) {
        super(previous, nextNodes, condition, action, time, pathfinding, gamestate, script);
    }

    public void execute(int versiontoexecute, GameState gs) throws UnableToMoveException, ExecuteException, BlockedActuatorException, PointInObstacleException, BadVersionException {
        this.script.goToThenExec(versiontoexecute, gs);
        this.script.execute(versiontoexecute, gs);

    }

    public Boolean getCondition() {
        return gamestate.getTimeEllapsed()<time && condition;
    }
}
