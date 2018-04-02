package strategie.IA;

import enums.ConfigInfoRobot;
import exceptions.BadVersionException;
import exceptions.BlockedActuatorException;
import exceptions.ExecuteException;
import exceptions.Locomotion.PointInObstacleException;
import exceptions.Locomotion.UnableToMoveException;
import scripts.AbstractScript;
import scripts.ScriptManager;
import smartMath.Vec2;
import strategie.GameState;
import table.Table;

import java.util.ArrayList;

public abstract class Node {

    private ScriptManager scriptManager;
    private AbstractScript script;
    private int versionToExecute;
    private int id;
    private int score;
    private int timeLimit;
    private int timeToGo;
    private int timeToExecute;
    private boolean isDone;
    private ArrayList<Node> nextNodes;
    private GameState gameState;
    private Vec2 position;

    /** Noeud d'action, principale composant du graphe de décision. Il permet de lancer les scripts et de gérer les
     * exeptions. Il possède plusieurs paramètre utilisé pour calculer le coup d'une arrete en points/s.
     *
     * @param versionToExecute
     * @param nextNodes
     * @param scriptManager
     * @param gameState
     */

    public Node(int versionToExecute, ArrayList<Node> nextNodes, ScriptManager scriptManager ,GameState gameState) {
        this.versionToExecute = versionToExecute;
        this.id = 0;
        this.timeLimit = 0;
        this.timeToGo = 0;
        this.timeToExecute = 0;
        this.isDone = false;
        this.nextNodes = nextNodes;
        this.scriptManager = scriptManager;
        this.gameState = gameState;
        this.position = Table.entryPosition;
    }

    /** Permet d'executer le script d'un noeud et de gérer les exeptions si il y en a. */

    public void execute(Exception e) throws BlockedActuatorException, UnableToMoveException, PointInObstacleException, ExecuteException, BadVersionException {
        if (e != null) {
            exception(e);
        } else {
            script.goToThenExec(versionToExecute, gameState);
            setDone(true);
        }
    }

    //arbre de décision
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

    public Vec2 updatePosition() throws BadVersionException {
        return getScript().entryPosition(getVersionToExecute(),Table.entryPosition).getCenter();
    }


    public boolean isDone() {
        return isDone;
    }

    public ArrayList<Node> getNextNodes() { return nextNodes; }

    public ScriptManager getScriptManager() {  return scriptManager;    }

    public int getScore(){   return score;    }

    public int getId() {   return id;    }

    public AbstractScript getScript() {  return script;    }

    public int getVersionToExecute() {  return versionToExecute;    }

    public Vec2 getPosition() {return position;}

    public void setDone(boolean done) {
        this.isDone = done;
    }

    public void setId (int id) { this.id = id;}

    public void setNextNodes(ArrayList<Node> nextNodes) {
        this.nextNodes = nextNodes;
    }

    public void setScript(AbstractScript script) {    this.script = script;    }

    public void setScore(int score) {   this.score = score;    }

    public void setPosition(Vec2 position) { this.position = position; }
}
