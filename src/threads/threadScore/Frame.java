package threads.threadScore;

import strategie.GameState;
import utils.Log;

import javax.swing.JFrame;
import java.awt.Color;

public class Frame extends JFrame {

    private boolean symetry;
    private Panel panel;
    private Log log;
    private GameState state;

    public Frame(Log log, GameState state, boolean symetry){
        this.log=log;
        this.state=state;
        this.symetry=symetry;
        this.setTitle("Score");
        this.setSize(400, 200);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        if (this.symetry) {
            this.setBackground(Color.ORANGE);
        }
        else{
            this.setBackground(Color.GREEN);
        }
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.panel=new Panel(this.log, this.state, this.symetry);
        this.setContentPane(panel);
        this.setVisible(true);
    }

}
