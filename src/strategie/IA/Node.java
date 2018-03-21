package strategie.IA;

import exceptions.BadVersionException;
import exceptions.BlockedActuatorException;
import exceptions.ExecuteException;
import exceptions.Locomotion.PointInObstacleException;
import exceptions.Locomotion.UnableToMoveException;
import pathfinder.Noeud;
import scripts.AbstractScript;
import strategie.GameState;

import java.util.ArrayList;

public class Node {

    private Node previous;
    private ArrayList<Node> nextNodes;
    private Boolean condition;  //si true on execute ce noeud
    private Boolean executed;   //si true execute n'a soulevé aucune exeption
    private String action;      //nom de l'action réalisée
    private double time;           //ne pas executer le script si le match a duré plus longtemps que cette valeur
    private int score;

    private GameState gamestate;
    private AbstractScript script;
    private Exception exception;
    private int versionToexecute;



    public Node(String action, Node previous, long time, int score, AbstractScript script,int versionToexecute ,Exception exception, GameState gamestate) {
        this.previous = previous;
        this.nextNodes = null;
        this.condition = false;
        this.executed = false;
        this.action = action;
        this.time = time;
        this.gamestate = gamestate;
        this.script = script;
        this.exception = exception;
        this.score = score;
        this.versionToexecute=versionToexecute;
    }


    /**met à jour la condition pour savoir si ce noeud doit être executé
    si aucune exception n'est levée, le noeud doit necessairement etre executée     */

    public void updateCondition(Exception e) {
        if (this.exception == null && this.previous.getExecuted()) {
            condition = true;
        } else {
            condition = false;
//            return gamestate.getTimeEllapsed() < time && gamestate.robot.getScriptDone().get(script) && !exception.equals(null) || exception == e && this.previous.executed;
        }

    }

    /** met à jour la condition de tout les noeuds enfants */

    public void updateConditions(Exception e) {
        for (Node node : nextNodes) {
            node.updateCondition(e);
        }
    }

    public Node getNextNode(Exception e){
        for(Node node : nextNodes){
            node.updateCondition(e);
            if(node.getCondition() && node.getExecuted()){
                return node.getNextNode(e);
            }
            if (node.getCondition()){
                return node;
            }
        }
        return null;
    }

    /** Cette méthode retourne le noeud executé parmi les noeuds fils du node pris en
      paramètre c'est pour l'IA     */

    public Node returnExecutedNode(Node node) {
        for (Node noeud : node.getNextNodes()) {
            if (noeud.getExecuted()) {
                return noeud;
            }
        }
        return null;
    }

    /** lance l'action du noeud */

    public void execute(GameState gs, Exception e) throws UnableToMoveException, ExecuteException, BlockedActuatorException, PointInObstacleException, BadVersionException {
        Node node = getNextNode(e);
        node.script.goToThenExec(this.versionToexecute, gs);
        node.executed = true;
    }

    /** Affiche les noeuds suivants */

    public void display() {
        System.out.println(this);
        if (nextNodes != null) {
            for (Node node : nextNodes) {
                node.display();
            }
        }
    }

    @Override
    public String toString() {
        return "action=" + action;
    }


    public ArrayList<Node> getNextNodes() {
        return nextNodes;
    }

    public Node getPrevious() {
        return previous;
    }

    public Boolean getCondition() {
        return condition;
    }

    public Boolean getExecuted() {
        return executed;
    }

    public int getscore() {
        return this.score;
    }

    public void setNextNodes(ArrayList<Node> nextNodes) {
        this.nextNodes = nextNodes;
    }

    public void setExecuted(Boolean executed) {
        this.executed = executed;
    }
}
