package strategie.IA;

import exceptions.BadVersionException;
import exceptions.BlockedActuatorException;
import exceptions.ExecuteException;
import exceptions.Locomotion.PointInObstacleException;
import exceptions.Locomotion.UnableToMoveException;
import scripts.AbstractScript;
import scripts.ScriptManager;
import strategie.GameState;

import java.util.ArrayList;

public abstract class Node {

    ScriptManager scriptManager;
    AbstractScript script;
    int versionToExecute;
    int score;
    int timeLimit;
    int timeToGo;
    int timeToExecute;
    boolean isDone;
    ArrayList<Node> nextNodes;
    GameState gameState;

    public Node(int versionToExecute, ArrayList<Node> nextNodes, ScriptManager scriptManager ,GameState gameState) {
        this.versionToExecute = versionToExecute;
        this.timeLimit = 0;
        this.timeToGo = 0;
        this.timeToExecute = 0;
        this.isDone = false;
        this.nextNodes = nextNodes;
        this.scriptManager = scriptManager;
        this.gameState = gameState;
    }

    public void execute(Exception e) throws BlockedActuatorException, UnableToMoveException, PointInObstacleException, ExecuteException, BadVersionException {
        if (e != null) {
            exception(e);
        } else {
            script.goToThenExec(versionToExecute, gameState);
            setDone(true);
        }
    }

    public Node selectNode() {
        int bestScore = 0;
        int i = 0;
        Node currentNode;
        if (!isDone() || getNextNodes()==null){
            return this;
        }
        if (gameState.isTourAvantRemplie()&& gameState.isTourArriereRemplie()){
            //dépose cube
        }
        for (int j = 0; j < nextNodes.size(); j++) {
            currentNode = nextNodes.get(j);
            if (currentNode.getScore() > bestScore && !currentNode.isDone()) {
                bestScore = currentNode.getScore();
                i = j;
            }
        }
        if (i!=0){
            return nextNodes.get(i);
        }
        else{
            return nextNodes.get(0).selectNode();
        }

    }
    /** Gère les exceptions soulevées */

    public abstract void exception(Exception e);

    @Override
    public abstract String toString() ;

    public boolean isDone() {
        return isDone;
    }

    public ArrayList<Node> getNextNodes() {
        return nextNodes;
    }

    public int getScore() {
        return score;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public void setNextNodes(ArrayList<Node> nextNodes) {
        this.nextNodes = nextNodes;
    }
}
