package strategie.IA;

import container.Service;
import exceptions.ExecuteException;

public class IA implements Service {

    private Node root;

    public IA(Node root) {
        this.root = root;
    }

    @Override
    public void updateConfig() {

    }


    //génère l'arbre
    public void create() {

    }

    //parcourt l'arbre si il y a une exeption
    public void execute(Exception e) {
        root.updateConditions(e);
        for (Node node : root.getNextNodes()) {

        }
    }
}



