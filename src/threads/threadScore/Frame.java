package threads.threadScore;

import graphics.TablePanel;
import strategie.GameState;

import javax.swing.JFrame;
import java.awt.Color;

public class Frame extends JFrame {

    private boolean symetry;
    private Panel panel;
    private GameState state;

    public Frame(GameState state, boolean symetry){
        this.state=state;
        this.symetry=symetry;
        this.setTitle("Score");
        this.setSize(200, 200);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        if (this.symetry) {
            this.setBackground(Color.ORANGE);
        }
        else{
            this.setBackground(Color.GREEN);
        }
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.panel=new Panel(this.state, this.symetry);
        this.setContentPane(panel);
        this.setVisible(true);
    }

}
