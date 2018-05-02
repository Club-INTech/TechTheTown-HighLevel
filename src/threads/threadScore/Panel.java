package threads.threadScore;

import strategie.GameState;
import utils.Log;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Font;

public class Panel extends JPanel {

    private GameState state;
    private boolean symetry;
    private int points;
    private Log log;

    public Panel(Log log, GameState state, boolean symetry){
        this.state=state;
        this.symetry=symetry;
        this.points=0;
        this.setFont(new Font("Comic Sans MS",Font.BOLD, 100));
    }

    @Override
    public void paintComponent(Graphics g) {
        int posTextX=0;
        int score=state.getObtainedPoints();
        if ((score/10)>=1){
            posTextX=40;
        }
        else if (score/100>=1){
            posTextX=10;
        }
        else{
            posTextX=70;
        }
        if (this.state.getObtainedPoints()!=this.points) {
            log.critical("Points : "+this.points);
            this.points = this.state.getObtainedPoints();
            g.drawString(Integer.toString(this.points), posTextX, 120);
        }
    }
}
