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
    private int score;

    private GameState gamestate;
    private AbstractScript script;
    private Exception exception;
    private int pattern;


    public Node(String action, Node previous, long time, int score, AbstractScript script, int pattern, Exception exception, GameState gamestate) {
        this.previous = previous;
        this.nextNodes = null;
        this.condition = false;
        this.executed = false;
        this.action = action;
        this.time = time;
        this.gamestate = gamestate;
        this.script = script;
        this.exception = exception;
        this.pattern = pattern;
        this.score = score;
    }

    //lance l'action du noeud
    public void execute(GameState gs) throws UnableToMoveException, ExecuteException, BlockedActuatorException, PointInObstacleException, BadVersionException {
        this.executed = true;
        for (Node node : this.nextNodes) {
            node.executed = false;
            node.condition = false;
        }
        this.script.goToThenExec(this.versionToexecute, gs);
        this.executed = true;
    }

    //met à jour la condition pour savoir si ce noeud doit être executé
    //si aucune exception n'est levée, le noeud doit necessairement etre executée
    public boolean updateCondition(Exception e) {
        if (e == null && this.previous.executed == true) {
            return true;
        } else {
            return gamestate.getTimeEllapsed() < time && gamestate.robot.getScriptDone().get(script) && !exception.equals(null) || exception == e && this.previous.executed;
        }

    }

    //met à jour la condition de tout les noeuds enfants
    public void updateConditions(Exception e) {
        for (Node node : nextNodes) {
            node.updateCondition(e);
        }
    }

    /* Cette méthode retourne le noeud executé parmi les noeuds fils du node pris en
      paramètre c'est pour l'IA
     */
    public Node returnExecutedNode(Node node) {
        for (Node noeud : node.getNextNodes()) {
            if (noeud.getExecuted()) {
                return noeud;
            }
        }
        return null;
    }

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

    public void setNextNodes(ArrayList<Node> nextNodes) {
        this.nextNodes = nextNodes;
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

    /**
     * Cette fonction permet de set les versions de scripts selon la version du pattern retournée
     * par le code de reconnaissance de couleur,cela permet d'instancier moins de noeuds
     * @param pattern
     * @return
     */
    public int setVersionduscript(int pattern){

    }

}
