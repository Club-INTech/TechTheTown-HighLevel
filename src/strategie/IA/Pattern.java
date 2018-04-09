package strategie.IA;

import exceptions.BadVersionException;
import exceptions.BlockedActuatorException;
import exceptions.ExecuteException;
import exceptions.Locomotion.PointInObstacleException;
import exceptions.Locomotion.UnableToMoveException;
import scripts.ScriptManager;
import strategie.GameState;

import java.util.ArrayList;

public class Pattern extends Node{

    public Pattern(int versionToExecute, ArrayList<Node> nextNodes, ScriptManager scriptManager, GameState gameState) throws BadVersionException {
        super(versionToExecute, nextNodes, scriptManager, gameState);
        this.setScore(666);
//        this.script = scriptManager.getScript(ScriptNames.);
    }

    @Override
    public void execute(Exception e) {
        System.out.println("Patern Recognition");
        setDone(true);
    }

    @Override
    public void exception(Exception e) {

    }

    @Override
    public String toString() {
        return "Pattern";
    }
}
