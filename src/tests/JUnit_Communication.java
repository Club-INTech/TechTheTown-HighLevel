package tests;

import org.junit.Before;
import org.junit.Test;
import robot.EthWrapper;
import threads.ThreadSimulator;
import threads.dataHandlers.ThreadEth;
import utils.Sleep;

import java.util.ArrayList;

/**
 * Test de Comm
 */
public class JUnit_Communication extends JUnit_Test {

    /** Thread ou Wrapper ?*/
    private ThreadEth eth;
    private EthWrapper ethWrapper;

    @Test
    public void testSimulator() throws Exception{

        String[][] ordersList = {{"?"}, {"cx", "120", "cy", "500", "co", "3.14", "?xyo"}, {"d", "100"}, {"t", "0.5"}, {"tor", "0.5"}, {"tol", "0.5"}, {"stop"}};
        eth = container.getService(ThreadEth.class);
        container.startInstanciedThreads();

        while (true) {
            for (String mess : ordersList[1]) {
                int nb_line_resp;
                if (mess == "?xyo") {
                    nb_line_resp = 3;
                } else {
                    nb_line_resp = 0;
                }
                String[] message = {mess};
                eth.communicate(message, nb_line_resp);
                Sleep.sleep(1000);
            }
        }
    }

    @Test
    public void testCodeuse() throws Exception{
        ethWrapper = container.getService(EthWrapper.class);
        container.startInstanciedThreads();
        ethWrapper.turn(2);
        Sleep.sleep(20000);
    }

    @Test
    public void testSpam() throws Exception{
        eth = container.getService(ThreadEth.class);
        container.startInstanciedThreads();

        for (int i = 0; i<1000; i++){
            eth.communicate(String.format("%s", i), 1);
            Sleep.sleep(200);
        }
    }
}
