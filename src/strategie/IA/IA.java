package strategie.IA;

import container.Container;
import container.Service;
import enums.ScriptNames;
import exceptions.BadVersionException;
import exceptions.BlockedActuatorException;
import exceptions.ContainerException;
import exceptions.ExecuteException;
import exceptions.Locomotion.PointInObstacleException;
import exceptions.Locomotion.UnableToMoveException;
import hook.HookFactory;
import org.classpath.icedtea.Config;
import robot.EthWrapper;
import scripts.*;
import strategie.GameState;
import sun.font.Script;
import utils.Log;

import java.security.spec.ECField;
import java.util.ArrayList;

public class IA implements Service {

    Node root;
    GameState gameState;
    ScriptManager scriptManager;

    public IA(GameState gameState, ScriptManager scriptManager) {
        this.gameState = gameState;
        this.scriptManager = scriptManager;
    }

    public void create()  {
        root = new Pattern(0,null,scriptManager,gameState);
    }

    public void execute(Exception e) throws BlockedActuatorException, UnableToMoveException, PointInObstacleException, ExecuteException, BadVersionException {
        if (root.isDone()){
            root.execute(e);
        }
        else {
        Node node = root.selectNode();
        node.execute(e);
        }
    }


    @Override
    public void updateConfig() {

    }

}
