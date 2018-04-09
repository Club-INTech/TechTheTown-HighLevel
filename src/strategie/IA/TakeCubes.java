package strategie.IA;

import enums.ScriptNames;
import exceptions.BadVersionException;
import exceptions.BlockedActuatorException;
import exceptions.ExecuteException;
import exceptions.Locomotion.PointInObstacleException;
import exceptions.Locomotion.UnableToMoveException;
import scripts.ScriptManager;
import strategie.GameState;

import java.util.ArrayList;

public class TakeCubes extends Node {

    public TakeCubes(int versionToExecute, ArrayList<Node> nextNodes, ScriptManager scriptManager, GameState gameState) throws BadVersionException {
        super(versionToExecute, nextNodes, scriptManager, gameState);
        this.setScript(scriptManager.getScript(ScriptNames.TAKE_CUBES));
        this.setScore(40);
        this.setPosition(updatePosition());
    }

    @Override
    public void exception(Exception e) {

    }

    @Override
    public String toString() {
        return "TakeCubes";
    }
}
