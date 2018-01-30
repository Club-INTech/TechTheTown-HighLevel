package strategie.IA;

import container.Service;
import exceptions.BadVersionException;
import exceptions.BlockedActuatorException;
import exceptions.ExecuteException;
import exceptions.Locomotion.PointInObstacleException;
import exceptions.Locomotion.UnableToMoveException;
import strategie.GameState;

import java.util.ArrayList;

public class IA implements Service {

    private Node root;
    private GameState gameState;

    public IA(GameState gameState) {
        this.root = new Node("root", null, 666, -1, null, 0, null, gameState);
        this.gameState = gameState;
    }

    @Override
    public void updateConfig() {

    }


    //génère l'arbre
    public void create() {
        ArrayList<Node> node1 = new ArrayList<Node>();
        node1.add(new Node("patern", root, 666, 10, null, 0, null, gameState));
        root.setNextNodes(node1);
    }

    //parcourt l'arbre si il y a une exception
    public void execute(Exception e, GameState gs) throws UnableToMoveException, ExecuteException, BlockedActuatorException, PointInObstacleException, BadVersionException {
        root.updateConditions(e);
        for (Node node : root.getNextNodes()) {
            if (node.getCondition() == true) {
                node.execute(gs);
            }
        }
    }

    public Node getRoot() {        return root;    }

    public GameState getGameState() {        return gameState;    }
}



