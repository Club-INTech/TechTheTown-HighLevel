package strategie.IA;

import container.Service;
import exceptions.BadVersionException;
import exceptions.BlockedActuatorException;
import exceptions.ExecuteException;
import exceptions.Locomotion.PointInObstacleException;
import exceptions.Locomotion.UnableToMoveException;
import strategie.GameState;

public class IA implements Service {

    private Node root;
    private int scorefinal;

    public IA(Node root) {
        this.root = root;
        this.scorefinal=0;
    }

    @Override
    public void updateConfig() {

    }


    //génère l'arbre
    public void create() {

    }

    //parcourt l'arbre si il y a une exception
    public void execute(Exception e, GameState gs) throws UnableToMoveException, ExecuteException, BlockedActuatorException, PointInObstacleException, BadVersionException {
        root.updateConditions(e);
        for (Node node : root.getNextNodes()) {
            if(node.getCondition()==true){
                node.execute(gs);
            }
        }
    }

    public int getscorefinal(Node node,Exception e){
        if(node==root){
            return root.getscore();
        }
        else{
            if(node.getExecuted()){
                this.scorefinal=scorefinal+node.getscore();
                return scorefinal+node.getscore();
            }
            else{
                return getscorefinal(node.getPrevious().getNextNodes().get(0),e);
            }
        }

    }
}



