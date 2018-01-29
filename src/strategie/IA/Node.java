package strategie.IA;

import com.sun.xml.internal.bind.v2.runtime.reflect.Lister;
import exceptions.BadVersionException;
import exceptions.BlockedActuatorException;
import exceptions.ExecuteException;
import exceptions.Locomotion.PointInObstacleException;
import exceptions.Locomotion.UnableToMoveException;
import pathfinder.Pathfinding;
import robot.Locomotion;
import scripts.AbstractScript;
import strategie.GameState;
import sun.font.Script;

import java.util.ArrayList;

public class Node {

    private Node previous;
    private ArrayList<Node> nextNodes;
    private Boolean condition;  //si true on execute ce noeud
    private Boolean executed;   //si true execute n'a soulèvé aucune exeption
    private String action;      //nom de l'action réalisée
    private long time;           //ne pas executer le script si le match a duré plus longtemps que cette valeur

    private GameState gamestate;
    private AbstractScript script;
    private Exception exception;
    private int versionToexecute;


    public Node(Node previous, ArrayList<Node> nextNodes, Boolean condition, Boolean executed, String action, long time, GameState gamestate, AbstractScript script, Exception exception,int versionToexecute) {
        this.previous = previous;
        this.nextNodes = nextNodes;
        this.condition = condition;
        this.executed = executed;
        this.action = action;
        this.time = time;
        this.gamestate = gamestate;
        this.script = script;
        this.exception = exception;
        this.versionToexecute=versionToexecute;
    }

    //lance l'action du noeud
    public void execute( GameState gs) throws UnableToMoveException, ExecuteException, BlockedActuatorException, PointInObstacleException, BadVersionException {
        this.executed=true;
        for(Node node : this.nextNodes){
            node.executed=false;
            node.condition=false;
        }
        this.script.goToThenExec(this.versionToexecute, gs);
    }

    //met à jour la condition pour savoir si ce noeud doit être executé
    //si aucune exception n'est levée, le noeud doit necessairement etre executée
    public boolean updateCondition(Exception e) {
        if(e==null && this.previous.executed==true){
            return true;
        }
        else{
            return gamestate.getTimeEllapsed() < time && gamestate.robot.getScriptDone().get(script) && !exception.equals(null) || exception == e;
        }

    }

    //met à jour la condition de tout les noeuds enfants
    public void updateConditions(Exception e) {
        for (Node node : nextNodes) {
            node.updateCondition(e);
        }
    }

    @Override
    public String toString() {
        return "Node{" +
                "action='" + action + '\'' +
                '}';
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
}
