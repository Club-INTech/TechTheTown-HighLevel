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

public abstract class Node {

    protected Node previous;
    protected ArrayList<Node> nextNodes;
    protected Boolean condition;  //si true on execute ce noeud
    protected Boolean executed;   //true execute nous lancé aucune exeption
    protected String action;
    protected long time;           //ne pas executer le script si le match est plus avancé que cette valeur

    protected Pathfinding pathfinding;
    protected GameState gamestate;
    protected AbstractScript script;


    public Node(Node previous, ArrayList<Node> nextNodes, Boolean condition, String action, long time, Pathfinding pathfinding, GameState gamestate, AbstractScript script) {
        this.previous = previous;
        this.nextNodes = nextNodes;
        this.condition = condition;
        this.action = action;
        this.time = time;
        this.pathfinding = pathfinding;
        this.gamestate = gamestate;
        this.script = script;
    }

    public abstract void execute(int versiontoexecute, GameState gs) throws UnableToMoveException, ExecuteException, BlockedActuatorException, PointInObstacleException, BadVersionException ;

    public abstract Boolean getCondition() ;

    @Override
    public String toString() {
        return "Node{" +
                "action='" + action + '\'' +
                '}';
    }
}
