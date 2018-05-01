package strategie.IA;

import enums.ScriptNames;
import enums.UnableToMoveReason;
import exceptions.BadVersionException;
import exceptions.BlockedActuatorException;
import exceptions.ExecuteException;
import exceptions.Locomotion.ImmobileEnnemyForOneSecondAtLeast;
import exceptions.Locomotion.PointInObstacleException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.NoPathFound;
import hook.HookFactory;
import pathfinder.Pathfinding;
import pfg.config.Config;
import scripts.AbstractScript;
import scripts.ScriptManager;
import smartMath.Vec2;
import strategie.GameState;
import table.Table;
import utils.Log;

import java.util.ArrayList;

public abstract class Node {

    protected ScriptNames scriptName;
    protected ScriptManager scriptManager;
    protected AbstractScript script;
    protected int versionToExecute;
    protected int id;  //utiliser pour la réalisation des classes d'équivalence.
    protected int score;
    protected int timeLimit;
    protected int timeToGo;
    protected int timeToExecute;
    protected int tentatives;       //nombre de tentatives d'un noeuds, si on tente plus d'un certain nombre de fois on suppose que le noeud est inaccessible.
    protected boolean isDone;
    protected GameState gameState;
    protected Vec2 position;
    protected Pathfinding pathfinding;
    protected Log log;
    protected Config config;
    protected HookFactory hookFactory;


    /** Noeud d'action, principale composant du graphe de décision. Il permet de lancer les scripts et de gérer les
     * exeptions. Il possède plusieurs paramètre utilisé pour calculer le coup d'une arrete en points/s.
     *
     * @param versionToExecute
     * @param scriptManager
     * @param gameState
     */

    public Node(ScriptNames scriptName, int versionToExecute, ScriptManager scriptManager , GameState gameState, Pathfinding pathfinding, HookFactory hookFactory, Config config, Log log) {
        this.scriptName = scriptName;
        this.versionToExecute = versionToExecute;
        this.id = 0;
        this.timeLimit = 0;
        this.timeToGo = 0;
        this.timeToExecute = 0;
        this.tentatives = 0;
        this.isDone = false;
        this.scriptManager = scriptManager;
        this.gameState = gameState;
        this.position = Table.entryPosition;
        this.pathfinding=pathfinding;
        this.hookFactory=hookFactory;
        this.config=config;
        this.log = log;
    }

    /** Permet d'executer le script d'un noeud et de gérer les exeptions si il y en a. */

    public void execute(GameState gameState) throws PointInObstacleException, BadVersionException, ExecuteException, BlockedActuatorException, UnableToMoveException, ImmobileEnnemyForOneSecondAtLeast, NoPathFound {
        script.goToThenExec(versionToExecute, gameState);
        setDone(true);
    }

    public abstract void unableToMoveExceptionHandled(UnableToMoveException e);

    @Override
    public String toString() {
        return "Nom : "+ getName()+", version : "+getVersionToExecute();
    }

    public Vec2 updatePosition() throws BadVersionException {
        return getScript().entryPosition(getVersionToExecute(),Table.entryPosition).getCenter();
    }

    public abstract boolean isDone();

    public ScriptManager getScriptManager() { return scriptManager; }

    public int getScore(){ return score; }

    public int getId() { return id; }

    public AbstractScript getScript() { return script; }

    public int getVersionToExecute() { return versionToExecute; }

    public Vec2 getPosition() { return position; }

    public GameState getGameState() { return gameState; }

    public String getName() { return scriptName.getName(); }

    public boolean getIsDone(){ return isDone; }

    public void setDone(boolean done) {
        this.isDone = done;
    }

    public void setId (int id) { this.id = id; }

    public void setScript(AbstractScript script) { this.script = script; }

    public void setScore(int score) { this.score = score; }

    public void setPosition(Vec2 position) { this.position = position; }

    public void setVersionToExecute(int versionToExecute) { this.versionToExecute = versionToExecute; }
}
