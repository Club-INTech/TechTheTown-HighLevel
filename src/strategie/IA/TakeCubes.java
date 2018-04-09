package strategie.IA;

import enums.BrasUtilise;
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

public class TakeCubes extends Node {

    public TakeCubes(int versionToExecute, ArrayList<Node> nextNodes, ScriptManager scriptManager, GameState gameState, Pathfinding pathfinding) throws BadVersionException {
        super(versionToExecute, nextNodes, scriptManager, gameState,pathfinding);
        this.setScript(scriptManager.getScript(ScriptNames.TAKE_CUBES));
        this.setScore(40);
        this.setPosition(updatePosition());
    }

    @Override
    public void execute(Exception e, GameState gameState) throws BlockedActuatorException, UnableToMoveException, PointInObstacleException, ExecuteException, BadVersionException, ImmobileEnnemyForOneSecondAtLeast {
        if (e != null) {
            exception(e);
        } else {
            if (gameState.isTourArriereRemplie() && !gameState.isTourArriereRemplie()){
                gameState.setTakeCubesBras(BrasUtilise.ARRIERE);
            }
            else if(!gameState.isTourArriereRemplie() && gameState.isTourArriereRemplie()){
                gameState.setTakeCubesBras(BrasUtilise.AVANT);
            } else {
                int scalar = gameState.robot.getPosition().minusNewVector(getScript().entryPosition(getVersionToExecute(),gameState.robot.getPosition()).getCenter()).dot(new Vec2(0,42));
                if(scalar > 0){
                    gameState.setTakeCubesBras(BrasUtilise.AVANT);
                } else {
                    gameState.setTakeCubesBras(BrasUtilise.ARRIERE);
                }
            }
        }
        getScript().goToThenExec(getVersionToExecute(), gameState);
        setDone(true);
    }


    public void exception(Exception e) {

    }

    @Override
    public String toString() {
        return "TakeCubes";
    }

    @Override
    public void unableToMoveExceptionHandled(UnableToMoveException e) {

    }
}
