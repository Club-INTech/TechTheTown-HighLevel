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
        Node node1=new Node("patern", root, 666, 5, null, 0, null, gameState);
        lnode1.add(node1);
        root.setNextNodes(lnode1);
        ArrayList<Node> lnode2=new ArrayList<>();
        Node node2=new Node("interrupteur",node1,666,25,new ActivationBrasLateral(container.getConfig(),container.getLog(),hookFactory),0,null,gameState);
        //TODO: générer les noeuds avec les exceptions
        lnode2.add(node2);
        ArrayList<Node> lnode3=new ArrayList<>();
        Node node30=new Node("cubes2",node2,666,10,new TakeCubes(container.getConfig(),container.getLog(),hookFactory),2,null,gameState);
        lnode3.add(node30);
        Node node31=new Node("cubes2",node2,666,10,new TakeCubes(container.getConfig(),container.getLog(),hookFactory),12,null,gameState);
        lnode3.add(node31);
        Node node32=new Node("cubes2",node2,666,10,new TakeCubes(container.getConfig(),container.getLog(),hookFactory),22,null,gameState);
        lnode3.add(node32);
        Node node33=new Node("cubes2",node2,666,10,new TakeCubes(container.getConfig(),container.getLog(),hookFactory),32,null,gameState);
        lnode3.add(node33);
        Node node34=new Node("cubes2",node2,666,10,new TakeCubes(container.getConfig(),container.getLog(),hookFactory),42,null,gameState);
        lnode3.add(node34);
        Node node35=new Node("cubes2",node2,666,10,new TakeCubes(container.getConfig(),container.getLog(),hookFactory),52,null,gameState);
        lnode3.add(node35);
        Node node36=new Node("cubes2",node2,666,10,new TakeCubes(container.getConfig(),container.getLog(),hookFactory),62,null,gameState);
        lnode3.add(node36);
        Node node37=new Node("cubes2",node2,666,10,new TakeCubes(container.getConfig(),container.getLog(),hookFactory),72,null,gameState);
        lnode3.add(node37);
        Node node38=new Node("cubes2",node2,666,10,new TakeCubes(container.getConfig(),container.getLog(),hookFactory),82,null,gameState);
        lnode3.add(node38);
        Node node39=new Node("cubes2",node2,666,10,new TakeCubes(container.getConfig(),container.getLog(),hookFactory),92,null,gameState);
        lnode3.add(node39);
        node2.setNextNodes(lnode3);



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
