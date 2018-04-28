package strategie.IA;

import enums.ConfigInfoRobot;
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
    protected Pathfinding pathfinding;;
    protected Log log;
    protected Config config;
    protected HookFactory hookFactory;
    protected int basicDetectionDistance;


    /** Noeud d'action, principale composant du graphe de décision. Il permet de lancer les scripts et de gérer les
     * exeptions. Il possède plusieurs paramètre utilisé pour calculer le coup d'une arrete en points/s.
     *
     * @param versionToExecute
     * @param nextNodes
     * @param scriptManager
     * @param gameState
     */

    public Node(String name, int versionToExecute, ArrayList<Node> nextNodes, ScriptManager scriptManager ,GameState gameState, Pathfinding pathfinding, HookFactory hookFactory,Config config, Log log) {
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
        this.config=config;
        this.log = log;
        updateConfig();
    }

    /** Permet d'executer le script d'un noeud et de gérer les exeptions si il y en a. */

    public void execute(Exception e, GameState gameState)
            throws PointInObstacleException, BadVersionException, ExecuteException, BlockedActuatorException, UnableToMoveException, ImmobileEnnemyForOneSecondAtLeast {
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
        log.debug("on est dans Exception du Node Abstrait");
        if (e instanceof ImmobileEnnemyForOneSecondAtLeast) {
            log.debug("j'ai bien catch immmobileEnnemy et je tente d'esquiver : je suis bien intelligent");
            Vec2 aim = ((ImmobileEnnemyForOneSecondAtLeast) e).getAim();
            boolean ennemyDodged = false;
            int attemps=0;
            while (!ennemyDodged && attemps<10) {
                attemps++;
                try {
                    log.debug("Début esquive");

                    //On s'éloigne de l'aim
                    Vec2 directionToGo = (aim.minusNewVector(gameState.robot.getPosition()));
                    double prodScal = directionToGo.dot(new Vec2(100.0, gameState.robot.getOrientation()));
                    Vec2 pointToGo;
                    //On regarde si le point où l'on veut reculer est dans un obstacle, si c'est le cas, on throw PointInObstacleException
                    if (prodScal>0) {
                        Vec2 wantToBackUpTo=gameState.robot.getPosition().plusNewVector(new Vec2(-50.0,gameState.robot.getOrientation()));
                        if (!gameState.table.getObstacleManager().isPositionInObstacle(wantToBackUpTo)) {
                            gameState.robot.moveLengthwise(-50);
                        }
                        else{
                            log.debug("Point in obstacle");
                            //on renvoie une exception avec notre objectif initial en paramètre
                            throw new PointInObstacleException(aim);
                        }
                    }
                    else{
                        Vec2 wantToBackUpTo=gameState.robot.getPosition().plusNewVector(new Vec2(50.0,gameState.robot.getOrientation()));
                        if (!gameState.table.getObstacleManager().isPositionInObstacle(wantToBackUpTo)) {
                            gameState.robot.moveLengthwise(50);
                        }
                        else{
                            log.debug("Point in obstacle");
                            //on renvoie une exception avec notre objectif initial en paramètre
                            throw new PointInObstacleException(aim);
                        }
                    }

                    //On cherche un nouveau chemin pour y aller
                    ArrayList<Vec2> pathToFollow = gameState.robot.getPathfinding().findmyway(gameState.robot.getPosition(), aim);
                    gameState.robot.followPath(pathToFollow);
                    ennemyDodged = true;
                } catch (ImmobileEnnemyForOneSecondAtLeast immobileEnnemyForOneSecondAtLeast) {
                    immobileEnnemyForOneSecondAtLeast.printStackTrace();
                    log.debug("L'ennemi est toujours là");
                } catch (PointInObstacleException e1) {
                    log.debug("PointInObstacleException");
                    e1.printStackTrace();
                } catch (UnableToMoveException e1) {
                    log.debug("UnableToMoveException");
                    e1.printStackTrace();
                } catch (NoPathFound noPathFound) {
                    log.debug("NoPathFound");
                    noPathFound.printStackTrace();
                } finally {
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            }
            try {
                execute(null, gameState);
            } catch (PointInObstacleException e1) {
                e1.printStackTrace();
            } catch (BadVersionException e1) {
                e1.printStackTrace();
            } catch (ExecuteException e1) {
                e1.printStackTrace();
            } catch (BlockedActuatorException e1) {
                e1.printStackTrace();
            } catch (UnableToMoveException e1) {
                e1.printStackTrace();
            } catch (ImmobileEnnemyForOneSecondAtLeast immobileEnnemyForOneSecondAtLeast) {
                immobileEnnemyForOneSecondAtLeast.printStackTrace();
            }
        }
        // on est dans un obstacle, on va au noeud le plus proche du graphe
        else if(e instanceof PointInObstacleException){

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
    public void updateConfig(){
        this.basicDetectionDistance=config.getInt(ConfigInfoRobot.BASIC_DETECTION_DISTANCE);
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
