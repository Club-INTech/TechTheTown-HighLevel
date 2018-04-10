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
import scripts.ScriptManager;
import smartMath.Vec2;
import strategie.GameState;

import java.util.ArrayList;

public class Abeille extends Node {

    public Abeille(String name, int versionToExecute, ArrayList<Node> nextNodes, ScriptManager scriptManager, GameState gameState, Pathfinding pathfinding, HookFactory hookFactory) throws BadVersionException {
        super(name, versionToExecute, nextNodes, scriptManager, gameState, pathfinding, hookFactory);
        this.setScript(scriptManager.getScript(ScriptNames.ACTIVE_ABEILLE));
        this.setScore(50);
        this.setPosition(updatePosition());
    }

    @Override
    public void exception(Exception e) {

    }

    /**
     * On override le execute du node, l'idée c'est qu'on puisse déjà enable et
     * disable les hooks qu'on veut en fonction des bras
     */
    @Override
    public void execute(Exception e, GameState gameState) throws BlockedActuatorException, UnableToMoveException, PointInObstacleException, ExecuteException, BadVersionException, ImmobileEnnemyForOneSecondAtLeast {
        if (e != null) {
            exception(e);
        }
        /**
         *On n'a pas d'exception, du coup tout est normal, on active les hooks
         */
        else {
            Vec2 entry = this.script.entryPosition(1, gameState.robot.getPosition()).getCenter();
            Vec2 directionToGo = (entry.minusNewVector(gameState.robot.getPosition()));
            double prodScal = directionToGo.dot(new Vec2(100.0, gameState.robot.getOrientation()));
            if (prodScal > 0) {
                //ON UTILISE LE BRAS AVANT
                //On disable le hook pour le bras arrière
                hookFactory.disableHook(HookNames.ACTIVE_BRAS_ARRIERE_ABEILLE);
                hookFactory.disableHook(HookNames.ACTIVE_BRAS_ARRIERE_ABEILLE_SYMETRIQUE);
                //On enable le kook pour le bras avant
                hookFactory.enableHook(HookNames.ACTIVE_BRAS_AVANT_ABEILLE);
                hookFactory.enableHook(HookNames.ACTIVE_BRAS_AVANT_ABEILLE_SYMETRIQUE);
                this.script.goToThenExec(1, gameState);
                hookFactory.disableHook(HookNames.ACTIVE_BRAS_AVANT_ABEILLE);
                hookFactory.disableHook(HookNames.ACTIVE_BRAS_AVANT_ABEILLE_SYMETRIQUE);
            }
            if (prodScal < 0) {
                //ON UTILISE LE BRAS AVANT
                //On disable le hook pour le bras arrière
                hookFactory.disableHook(HookNames.ACTIVE_BRAS_AVANT_ABEILLE);
                hookFactory.disableHook(HookNames.ACTIVE_BRAS_AVANT_ABEILLE);
                //On enable le kook pour le bras avant
                hookFactory.enableHook(HookNames.ACTIVE_BRAS_ARRIERE_ABEILLE);
                hookFactory.enableHook(HookNames.ACTIVE_BRAS_ARRIERE_ABEILLE);
                this.script.goToThenExec(1, gameState);
                hookFactory.disableHook(HookNames.ACTIVE_BRAS_ARRIERE_ABEILLE);
                hookFactory.disableHook(HookNames.ACTIVE_BRAS_ARRIERE_ABEILLE);
            }
        }
    }

    public void unableToMoveExceptionHandled (UnableToMoveException e){

    }
}
