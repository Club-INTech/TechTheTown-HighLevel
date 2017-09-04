package tests;

import org.junit.Before;
import org.junit.Test;
import threads.ThreadSimulator;
import utils.Sleep;

/**
 * Test de Comm
 */
public class JUnit_Communication extends JUnit_Test {

    @Test
    public void testSimulator() throws Exception{

        container.getService(ThreadSimulator.class);
        container.startInstanciedThreads();
        while(true) {
            Sleep.sleep(1000);
        }
    }
}
