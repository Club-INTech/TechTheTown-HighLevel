package threads.threadScore;

import strategie.GameState;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Font;

public class Panel extends JPanel {

    private GameState state;
    private boolean symetry;

    public Panel(GameState state, boolean symetry){
        this.state=state;
        this.symetry=symetry;
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
        g.drawString(Integer.toString(state.getObtainedPoints()),posTextX,120);
    }
}
