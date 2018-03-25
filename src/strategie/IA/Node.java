package strategie.IA;

import enums.ScriptNames;
import exceptions.BadVersionException;
import exceptions.BlockedActuatorException;
import exceptions.ExecuteException;
import exceptions.Locomotion.PointInObstacleException;
import exceptions.Locomotion.UnableToMoveException;
import pathfinder.Noeud;
import scripts.AbstractScript;
import scripts.ScriptManager;
import strategie.GameState;

import java.util.ArrayList;

public class Node {

    AbstractScript script;
    int versionToExecute;
    int score;
    int timeLimit;
    int timeToGo;
    int timeToExexecute;
    boolean isDone;
    ArrayList<Node> nextNodes;
    GameState gameState;

    public Node(AbstractScript script, int versionToExecute, int score, ArrayList<Node> nextNodes, GameState gameState) {
        this.script = script;
        this.versionToExecute = versionToExecute;
        this.score = score;
        this.timeLimit = 0;
        this.timeToGo = 0;
        this.timeToExexecute = 0;
        this.isDone = false;
        this.nextNodes = nextNodes;
        this.gameState = gameState;
    }

    public void execute(Exception e) throws BlockedActuatorException, UnableToMoveException, PointInObstacleException, ExecuteException, BadVersionException {
        if (e != null) {
            //gestion des exeptions
        } else {
            script.goToThenExec(versionToExecute, gameState);
            setDone(true);
        }
    }

    public Node selectNode() {
        int bestScore = 0;
        int i = 0;
        Node currentNode;
        if (gameState.isTourAvantRemplie()&& gameState.isTourArriereRemplie()){
            //dépose cube
        }
        for (int j = 0; j < nextNodes.size(); j++) {
            currentNode = nextNodes.get(j);
            if (currentNode.getScore() > bestScore && isDone()) {
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
