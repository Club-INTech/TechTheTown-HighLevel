package tests;

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

    /** Thread de simulation du LL */
    private ThreadSimulator simulator;

    @Test
    public void testSimulator(){

        try {
            simulator = container.getService(ThreadSimulator.class);
            eth = container.getService(ThreadEth.class);
            container.startInstanciedThreads();
            String mess = "?xyo";

            log.debug("Envoie de : " + mess + " au Simu...");
            eth.communicate(mess, 3);
            Sleep.sleep(1000);

        }catch (Exception e){
            e.printStackTrace();
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

        for (int i = 0; i<1001; i++){
            eth.communicate(String.format("%s", i), 1);
            Sleep.sleep(200);
        }
    }
}
