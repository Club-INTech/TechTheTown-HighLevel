package threads.threadScore;

import enums.ConfigInfoRobot;
import pfg.config.Config;
import strategie.GameState;
import threads.AbstractThread;
import utils.Log;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Font;

public class ThreadScore extends AbstractThread {

    private Config config;
    private Log log;
    private GameState state;
    private boolean shutdown;
    private boolean symetry;
    private int score;
    private boolean usingJumper;
    private int width;
    private int height;
    private Frame frame;

    public ThreadScore(Log log, Config config, GameState state){
        super(config, log);
        Thread.currentThread().setPriority(4);
        this.config=config;
        this.log=log;
        this.state=state;
        updateConfig();
        this.frame = new Frame(this.state,this.symetry);
    }

    @Override
    public void run(){

        while (!shutdown){
            this.frame.repaint();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void updateConfig(){
        this.symetry=(this.config.getString(ConfigInfoRobot.COULEUR)).equals("orange");
        this.usingJumper=this.config.getBoolean(ConfigInfoRobot.ATTENTE_JUMPER);
    }

    public void shutdown(){
        this.shutdown=true;
    }
}
