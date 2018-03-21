package strategie.IA;

import container.Container;
import container.Service;
import exceptions.BadVersionException;
import exceptions.BlockedActuatorException;
import exceptions.ContainerException;
import exceptions.ExecuteException;
import exceptions.Locomotion.PointInObstacleException;
import exceptions.Locomotion.UnableToMoveException;
import hook.HookFactory;
import org.classpath.icedtea.Config;
import robot.EthWrapper;
import scripts.*;
import strategie.GameState;
import sun.font.Script;
import utils.Log;

import java.util.ArrayList;

public class IA implements Service {

    private Node root;
    private GameState gameState;
    private int scorefinal;
    private Container container;
    private ScriptManager scriptManager;
    private HookFactory hookFactory;

    public IA(Container container, GameState gameState, ScriptManager scriptManager, HookFactory hookFactory) throws InterruptedException, ContainerException {
        this.root = new Node("root", null, 666, 0, null, 0, null, gameState);
        this.gameState = gameState;
        this.scorefinal = 0;
        this.container = container;
        this.scriptManager = scriptManager;
        this.hookFactory = hookFactory;
    }

    @Override
    public void updateConfig() {

    }


    //génère l'arbre
    public void create() throws InterruptedException, ContainerException {
        ArrayList<Node> lnode1 = new ArrayList<Node>();
        //noeud du pattern
        Node node1 = new Node("pattern", root, 666, 0, null, 0, null, gameState);
        lnode1.add(node1);
        root.setNextNodes(lnode1);
        ArrayList<Node> lnode2 = new ArrayList<>();
        //noeud interrupteur
        Node node2 = new Node("interrupteur", node1, 666, 25, container.getService(ActivationPanneauDomotique.class), 0, null, gameState);
        lnode2.add(node2);
        node1.setNextNodes(lnode2);
        ArrayList<Node> lnode3 = new ArrayList<>();
        //noeud des cubes de la position 2
        Node node3 = new Node("Cubes 2", node2, 666, 0, container.getService(TakeCubes.class), 2, null, gameState);
        lnode3.add(node3);
        node2.setNextNodes(lnode3);
        ArrayList<Node> lnodes4 = new ArrayList<>();
        //noeud de l'abeille
        Node node4 = new Node("abeille", node3, 666, 50, container.getService(ActiveAbeille.class), 0, null,gameState);
        lnodes4.add(node4);
        node3.setNextNodes(lnodes4);
        ArrayList<Node> lnode5 = new ArrayList<>();
        //noeud des cubes de la position 0
        Node node5 = new Node("Cubes 0", node4, 666, 0, container.getService(TakeCubes.class), 0, null, gameState);
        lnode5.add(node5);
        node4.setNextNodes(lnode5);
        ArrayList<Node> lnodes6 = new ArrayList<>();
        //noeud pour déposer tous les cubes
        Node node6 = new Node("deposer les cubes 2 et 0", node5, 666, 80, container.getService(DeposeCubes.class), 0,  null,gameState);
        lnodes6.add(node6);
        node5.setNextNodes(lnodes6);
        ArrayList<Node> lnode7 = new ArrayList<>();
        //noeud pour prendre les cubes de la position 1
        Node node7 = new Node("Cubes 1", node6, 666, 0, container.getService(TakeCubes.class), 0, null, gameState);
        lnode7.add(node7);
        node6.setNextNodes(lnode7);
        ArrayList<Node> lnodes8 = new ArrayList<>();
        //noeud pour prendre les cubes qui restent de la position 1
        Node node8 = new Node("Cubes qui restent position 1", node7, 666, 0,container.getService(TakeCubes.class), 1, null, gameState);
        lnodes8.add(node8);
        node7.setNextNodes(lnodes8);
        ArrayList<Node> lnode9 = new ArrayList<>();
        //noeud pour prendre les cubes qui restent de la position 0
        Node node9 = new Node("Cubes qui restent position 0", node8, 666, 0, container.getService(TakeCubes.class), 0, null, gameState);
        lnode9.add(node9);
        node8.setNextNodes(lnode9);
        ArrayList<Node> lnode10 = new ArrayList<>();
        //déposer tous les cubes qui restent
        Node node10 = new Node("déposer tous les cubes", node9, 666, 46, container.getService(DeposeCubes.class), 1,  null,gameState);
        lnode10.add(node10);
        node9.setNextNodes(lnode10);
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

    /**
     *Il s'agit d'une méthode récursive pour calculer le score, on lui file comme paramètre en Node
     * root
     */

    public int getscorefinal(Exception e,Node node) {
        //on get la dernière version de l'arbre
       root.updateConditions(e);
       //on get les fils du noeud passé en paramètre
       ArrayList<Node> lnodes=node.getNextNodes();
       for(Node noeud : lnodes){
           //si l'un des noeuds fils est executé, on rajoute son score au score final
           if(noeud.getExecuted()){
               scorefinal+=noeud.getscore();
               //on refait la même chose avec le noeud executé
               return getscorefinal(e,noeud);
           }
       }
       return scorefinal;

    }

}
