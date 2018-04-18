package strategie.IA;

import enums.ConfigInfoRobot;
import exceptions.BadVersionException;
import exceptions.BlockedActuatorException;
import exceptions.ExecuteException;
import exceptions.Locomotion.ImmobileEnnemyForOneSecondAtLeast;
import exceptions.Locomotion.PointInObstacleException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.Locomotion.UnexpectedObstacleOnPathException;
import exceptions.NoPathFound;
import hook.HookFactory;
import pathfinder.Pathfinding;
import pfg.config.Config;
import scripts.AbstractScript;
import scripts.ScriptManager;
import smartMath.Vec2;
import strategie.GameState;
import table.Table;

import java.util.ArrayList;

public abstract class Node {

    protected String name;
    protected ScriptManager scriptManager;
    protected AbstractScript script;
    protected int versionToExecute;
    protected int id;  //utiliser pour la réalisation des classes d'équivalence.
    protected int score;
    protected int timeLimit;
    protected int timeToGo;
    protected int timeToExecute;
    protected boolean isDone;
    protected ArrayList<Node> nextNodes;
    protected GameState gameState;
    protected Vec2 position;
    protected Pathfinding pathfinding;
    protected Config config;
    protected HookFactory hookFactory;


    /** Noeud d'action, principale composant du graphe de décision. Il permet de lancer les scripts et de gérer les
     * exeptions. Il possède plusieurs paramètre utilisé pour calculer le coup d'une arrete en points/s.
     *
     * @param versionToExecute
     * @param nextNodes
     * @param scriptManager
     * @param gameState
     */

    public Node(String name, int versionToExecute, ArrayList<Node> nextNodes, ScriptManager scriptManager ,GameState gameState, Pathfinding pathfinding, HookFactory hookFactory) {
        this.name = name;
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
        this.pathfinding=pathfinding;
        this.hookFactory=hookFactory;
    }

    /** Permet d'executer le script d'un noeud et de gérer les exeptions si il y en a. */

    public void execute(Exception e, GameState gameState)
            throws PointInObstacleException, BadVersionException, ExecuteException, BlockedActuatorException, UnableToMoveException, ImmobileEnnemyForOneSecondAtLeast, UnexpectedObstacleOnPathException {
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

    public void exception(Exception e) {
        if (e instanceof ImmobileEnnemyForOneSecondAtLeast) {
            boolean ennemyDodged = false;
            while (!ennemyDodged) {
                try {
                    gameState.robot.moveLengthwise(-20);
                    ArrayList<Vec2> pathToFollow = gameState.robot.getPathfinding().findmyway(gameState.robot.getPosition(), ((ImmobileEnnemyForOneSecondAtLeast) e).getAim());
                    gameState.robot.followPath(pathToFollow);
                    ennemyDodged = true;
                } catch (ImmobileEnnemyForOneSecondAtLeast immobileEnnemyForOneSecondAtLeast) {
                    immobileEnnemyForOneSecondAtLeast.printStackTrace();
                    System.out.println("L'ennemi est toujours là");
                } catch (PointInObstacleException e1) {
                    System.out.println("PointInObstacleException");
                    e1.printStackTrace();
                } catch (UnableToMoveException e1) {
                    System.out.println("UnableToMoveException");
                    e1.printStackTrace();
                } catch (NoPathFound noPathFound) {
                    System.out.println("NoPathFound");
                    noPathFound.printStackTrace();
                } catch (UnexpectedObstacleOnPathException e1) {
                    e1.printStackTrace();
                } finally {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
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

    public ArrayList<Node> getNextNodes() { return nextNodes; }

    public ScriptManager getScriptManager() {  return scriptManager;    }

    public int getScore(){   return score;    }

    public int getId() {   return id;    }

    public AbstractScript getScript() {  return script;    }

    public int getVersionToExecute() {  return versionToExecute;    }

    public Vec2 getPosition() {return position;}

    public GameState getGameState() {return gameState;}

    public String getName() { return name; }

    public boolean getIsDone(){ return isDone; }

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

    public void setVersionToExecute(int versionToExecute) { this.versionToExecute = versionToExecute; }



}
