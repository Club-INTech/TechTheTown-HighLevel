package threads;

import enums.ConfigInfoRobot;
import pfg.config.Config;
import strategie.GameState;
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

    //Java Swing objects
    private JFrame frame;
    private JPanel panel;

    public ThreadScore(Log log, Config config, GameState state){
        super(config, log);
        Thread.currentThread().setPriority(4);
        this.config=config;
        this.log=log;
        this.state=state;
        updateConfig();
        this.shutdown=false;
        this.width=200;
        this.height=200;

        //On crée la fenêtre de score
        this.frame = new JFrame();
        this.frame.setTitle("Score");
        this.frame.setSize(this.width, this.height);
        this.frame.setLocationRelativeTo(null);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.panel=new JPanel();
        this.frame.setVisible(true);
        this.panel.setVisible(true);
        this.frame.setContentPane(this.panel);
        if (this.symetry){
            this.panel.setBackground(Color.ORANGE);
            this.frame.setBackground(Color.ORANGE);
        }
        else{
            this.panel.setBackground(Color.GREEN);
            this.frame.setBackground(Color.GREEN);
        }
        Font f =new Font("Comic Sans MS", Font.BOLD, 100);
        this.panel.setFont(f);
        this.frame.setFont(f);
        this.panel.getGraphics().setColor(Color.BLACK);
        this.frame.getGraphics().setColor(Color.BLACK);
    }

    @Override
    public void run(){

        int posTextX=0;
        while (!shutdown){
            this.score=state.getObtainedPoints();
            if ((this.score/10)>=1){
                posTextX=40;
            }
            else if (this.score/100>=1){
                posTextX=10;
            }
            else{
                posTextX=70;
            }
            this.panel.getGraphics().clearRect(0,0,this.width,this.height);
            this.panel.getGraphics().drawString(Integer.toString(this.score),this.frame.getWidth()/3,this.frame.getHeight()/2);
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
