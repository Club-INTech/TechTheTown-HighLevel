package strategie.IA;

import enums.ScriptNames;
import exceptions.BadVersionException;
import exceptions.BlockedActuatorException;
import exceptions.ExecuteException;
import exceptions.Locomotion.ImmobileEnnemyForOneSecondAtLeast;
import exceptions.Locomotion.PointInObstacleException;
import exceptions.Locomotion.UnableToMoveException;
import hook.HookFactory;
import hook.HookNames;
import pathfinder.Pathfinding;
import pfg.config.Config;
import scripts.ScriptManager;
import smartMath.Vec2;
import strategie.GameState;
import utils.Log;

import java.util.ArrayList;

public class Abeille extends Node {

    public Abeille(String name, int versionToExecute, ScriptManager scriptManager, GameState gameState, Pathfinding pathfinding, HookFactory hookFactory, Config config, Log log) throws BadVersionException {
        super(name, versionToExecute, scriptManager, gameState, pathfinding, hookFactory,config, log);
        this.setScript(scriptManager.getScript(ScriptNames.ACTIVE_ABEILLE));
        this.setScore(50);
        this.setPosition(updatePosition());
    }

    /**
     * On override le execute du node, l'idée c'est qu'on puisse déjà enable et
     * disable les hooks qu'on veut en fonction des bras
     */

    public void unableToMoveExceptionHandled (UnableToMoveException e){

    }

    @Override
    public boolean isDone() {
        return getGameState().isAbeilleLancee() || getIsDone();
    }
}
