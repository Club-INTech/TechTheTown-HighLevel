package strategie.IA;

import enums.ScriptNames;
import exceptions.BadVersionException;
import exceptions.BlockedActuatorException;
import exceptions.ExecuteException;
import exceptions.Locomotion.ImmobileEnnemyForOneSecondAtLeast;
import exceptions.Locomotion.PointInObstacleException;
import exceptions.Locomotion.UnableToMoveException;
import pathfinder.Pathfinding;
import scripts.ScriptManager;
import smartMath.Vec2;
import strategie.GameState;

import java.util.ArrayList;

public class Abeille extends Node {

    public Abeille(int versionToExecute, ArrayList<Node> nextNodes, ScriptManager scriptManager, GameState gameState, Pathfinding pathfinding) throws BadVersionException {
        super(versionToExecute, nextNodes, scriptManager, gameState,pathfinding);
        this.setScript(scriptManager.getScript(ScriptNames.ACTIVE_ABEILLE));
        this.setScore(50);
        this.setPosition(updatePosition());
    }

    @Override
    public void exception(Exception e) {

    }

    /**On override le execute du node, l'idée c'est qu'on puisse déjà enable et
     * disable les hooks qu'on veut en fonction des bras
     * */

    public void execute(Exception e) throws BlockedActuatorException, UnableToMoveException, PointInObstacleException, ExecuteException, BadVersionException, ImmobileEnnemyForOneSecondAtLeast {
        if (e != null) {
            exception(e);
        }

        else {

        }
    }

    @Override
    public String toString() {
        return "Abeille";
    }

    @Override
    public void unableToMoveExceptionHandled(UnableToMoveException e) {

    }
}
