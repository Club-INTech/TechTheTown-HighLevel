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
    private long time;           //ne pas executer le script si le match à duré plus longtemps que cette valeur

    private Pathfinding pathfinding;
    private GameState gamestate;
    private AbstractScript script;
    private Exception exception;


    public Node(Node previous, ArrayList<Node> nextNodes, Boolean condition, Boolean executed, String action, long time, Pathfinding pathfinding, GameState gamestate, AbstractScript script, Exception exception) {
        this.previous = previous;
        this.nextNodes = nextNodes;
        this.condition = condition;
        this.executed = executed;
        this.action = action;
        this.time = time;
        this.pathfinding = pathfinding;
        this.gamestate = gamestate;
        this.script = script;
        this.exception = exception;
    }
    //lance l'action du noeud
    public void execute(int versiontoexecute, GameState gs) throws UnableToMoveException, ExecuteException, BlockedActuatorException, PointInObstacleException, BadVersionException {
        this.script.goToThenExec(versiontoexecute, gs);
    }

    //met à jour la la condition pour savoir si ce noeud doit être executé
    public Boolean updateCondition(Exception e) {
        return gamestate.getTimeEllapsed()<time && gamestate.robot.getScriptDone().get(script) && ! exception.equals(null)|| exception==e ;
    }

    @Override
    public String toString() {
        return "Node{" +
                "action='" + action + '\'' +
                '}';
    }
}
