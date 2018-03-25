package strategie.IA;

import enums.ScriptNames;
import pathfinder.Noeud;
import patternRecognition.PatternRecognition;
import scripts.AbstractScript;
import scripts.ScriptManager;
import strategie.GameState;

import java.util.ArrayList;

public class Pattern extends Node{

    public Pattern(int versionToExecute, ArrayList<Node> nextNodes, ScriptManager scriptManager, GameState gameState) {
        super(versionToExecute, nextNodes, scriptManager, gameState);
        this.score = 42;
//        this.script = scriptManager.getScript(ScriptNames.);
    }

    @Override
    public void exception(Exception e) {

    }
}
