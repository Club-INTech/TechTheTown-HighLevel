package strategie;

import java.util.ArrayList;
import java.util.HashMap;
import pfg.config.Config;
import utils.Log;

/**
 * Created by shininisan on 09/12/16.
 */
public class Runlist {
    private HashMap<String, ArrayList<ArrayList<Runnable>>> runList;// liste de liste de runnable
    private int indice;
    private Config config;
    private Log log;
    public Runlist(Log log,Config config)
    {
        this.log=log;
        this.config=config;
    }
    public HashMap<String, ArrayList<ArrayList<Runnable>>> getRunList() {
        return runList;
    }
//On utilisera ici des lambdas fonctions tels que Runnable monTraitement = () -> System.out.println("bonjour"); pour chaque intruction
    public void executeScript(String scriptToExecute, int versionToExecute, GameState stateToConsider) {
        int n = this.runList.get(scriptToExecute).get(versionToExecute).size();
       new Boucle(0,n,runList.get(scriptToExecute).get(versionToExecute),1).run();

    }


}
