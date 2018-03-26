package strategie.IA;

import container.Service;
import exceptions.BadVersionException;
import exceptions.BlockedActuatorException;
import exceptions.ExecuteException;
import exceptions.Locomotion.PointInObstacleException;
import exceptions.Locomotion.UnableToMoveException;
import scripts.*;
import strategie.GameState;

import java.util.ArrayList;

public class IA implements Service {

    Node root;
    GameState gameState;
    ScriptManager scriptManager;

    public IA(GameState gameState, ScriptManager scriptManager) {
        this.gameState = gameState;
        this.scriptManager = scriptManager;
    }

    public void create() {
        root = new Pattern(0, null, scriptManager, gameState);
        Node abeille = new Abeille(0, null, scriptManager, gameState);
        Node panneau = new Panneau(0, null, scriptManager, gameState);
        Node takeCubes = new TakeCubes(0, null, scriptManager, gameState);
        Node deposeCubes = new DeposeCubes(0, null, scriptManager, gameState);

        ArrayList<Node> a1 = new ArrayList<>();
        a1.add(panneau);
        a1.add(abeille);
        root.setNextNodes(a1);

        ArrayList<Node> a2 = new ArrayList<>();
    }

    public void execute(Exception e) throws BlockedActuatorException, UnableToMoveException, PointInObstacleException, ExecuteException, BadVersionException {
        root.selectNode().execute(e);
    }


    @Override
    public void updateConfig() {

    }

}
