package strategie.IA;

import enums.BrasUtilise;
import enums.ScriptNames;
import exceptions.BadVersionException;
import exceptions.BlockedActuatorException;
import exceptions.ExecuteException;
import exceptions.Locomotion.ImmobileEnnemyForOneSecondAtLeast;
import exceptions.Locomotion.PointInObstacleException;
import exceptions.Locomotion.UnableToMoveException;
import hook.HookFactory;
import pathfinder.Pathfinding;
import pfg.config.Config;
import scripts.ScriptManager;
import smartMath.Vec2;
import strategie.GameState;
import utils.Log;

import java.util.ArrayList;

public class TakeCubes extends Node {

    public TakeCubes(String name, int versionToExecute, ArrayList<Node> nextNodes, ScriptManager scriptManager, GameState gameState, Pathfinding pathfinding, HookFactory hookFactory, Config config, Log log) throws BadVersionException {
        super(name, versionToExecute, nextNodes, scriptManager, gameState,pathfinding,hookFactory,config, log);
        this.setScript(scriptManager.getScript(ScriptNames.TAKE_CUBES));
        this.setScore(40);
        this.setPosition(updatePosition());
    }

    @Override
    public void execute(Exception e, GameState gameState) throws BlockedActuatorException, UnableToMoveException, PointInObstacleException, ExecuteException, BadVersionException, ImmobileEnnemyForOneSecondAtLeast {
        if (e != null) {
            exception(e);
        } else {
            if (gameState.isTourAvantRemplie() && !gameState.isTourArriereRemplie()){
                gameState.setTakeCubesBras(BrasUtilise.ARRIERE);
            }
            else if(gameState.isTourArriereRemplie() && !gameState.isTourAvantRemplie()){
                gameState.setTakeCubesBras(BrasUtilise.AVANT);
            } else {
                float scalar = gameState.robot.getPosition().minusNewVector(getScript().entryPosition(getVersionToExecute(),gameState.robot.getPosition()).getCenter()).dot(new Vec2(0,42));
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


    @Override
    public void unableToMoveExceptionHandled(UnableToMoveException e) {

    }

    @Override
    public boolean isDone() {
        if(getVersionToExecute()==0)
            return getGameState().isTas_base_pris();
        if(getVersionToExecute()==1)
            return getGameState().isTas_chateau_eau_pris();
        if (getVersionToExecute()==2)
            return getGameState().isTas_station_epuration_pris();
        else { return false;}
    }
}
