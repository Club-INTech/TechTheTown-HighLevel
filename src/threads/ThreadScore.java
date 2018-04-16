package threads;

import pfg.config.Config;
import strategie.GameState;
import utils.Log;

public class ThreadScore extends AbstractThread {

    private Config config;
    private Log log;
    private GameState state;

    public ThreadScore(Config config, Log log, GameState state){
        super(config, log);
        Thread.currentThread().setPriority(4);
        this.config=config;
        this.log=log;
        this.state=state;
    }

    @Override
    public void run(){
        



        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
