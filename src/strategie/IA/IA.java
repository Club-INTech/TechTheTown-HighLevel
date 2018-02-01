package strategie.IA;

import container.Container;
import container.Service;
import exceptions.BadVersionException;
import exceptions.BlockedActuatorException;
import exceptions.ExecuteException;
import exceptions.Locomotion.PointInObstacleException;
import exceptions.Locomotion.UnableToMoveException;
import hook.HookFactory;
import org.classpath.icedtea.Config;
import scripts.ActivationBrasLateral;
import scripts.TakeCubes;
import strategie.GameState;
import utils.Log;

import java.util.ArrayList;

public class IA implements Service {

    private Node root;
    private GameState gameState;
    private int scorefinal;
    //implémenter container et hookfactory
    private Container container;
    private HookFactory hookFactory;




    public IA(GameState gameState) {
        this.root = new Node("root", null, 666, -1, null, 0, null, gameState);
        this.gameState = gameState;
        this.scorefinal = 0;
    }

    @Override
    public void updateConfig() {

    }


    //génère l'arbre
    public void create() {
        ArrayList<Node> lnode1 = new ArrayList<Node>();
        //noeud du pattern
        Node node1=new Node("patern", root, 666, 5, null,0,
        lnode1.add(node1);
        root.setNextNodes(lnode1);
        ArrayList<Node> lnode2=new ArrayList<>();
        //noeud interrupteur
        Node node2=new Node("interrupteur",node1,666,25,new ActivationBrasLateral(container.getConfig(),container.getLog(),hookFactory),0,null,gameState);

    }

    //parcourt l'arbre si il y a une exception
    public void execute(Exception e, GameState gs) throws UnableToMoveException, ExecuteException, BlockedActuatorException, PointInObstacleException, BadVersionException {
        root.updateConditions(e);
        for (Node node : root.getNextNodes()) {
            if (node.getCondition() == true) {
                node.execute(gs);
            }
        }
    }


    public Node getRoot() {
        return root;
    }

    public GameState getGameState() {
        return gameState;
    }

    public int getscorefinal(Node node, Exception e) {
        if (node == root) {
            return root.getscore();
        } else {
            return getscorefinal(node.returnExecutedNode(node), e);
        }
    }
}
