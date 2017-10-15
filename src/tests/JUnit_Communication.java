package tests;

import enums.ActuatorOrder;
import enums.Speed;
import org.junit.Test;
import robot.EthWrapper;
import robot.Locomotion;
import simulator.ThreadSimulator;
import simulator.ThreadSimulatorMotion;
import threads.ThreadInterface;
import threads.dataHandlers.ThreadEth;
import utils.Sleep;

import java.util.HashMap;

/**
 * Test de Comm
 */
public class JUnit_Communication extends JUnit_Test {

    /** Thread ou Wrapper ?*/
    private ThreadEth eth;
    private EthWrapper ethWrapper;

    /** Thread de simulation du LL */
    private ThreadSimulator simulator;
    private ThreadSimulatorMotion motion;

    /** Locomotion */
    private Locomotion locomotion;

    @Test
    public void testSimulator(){

        long start;
        int signe = 1;

        try {
            simulator = container.getService(ThreadSimulator.class);
            motion = container.getService(ThreadSimulatorMotion.class);
            ethWrapper = container.getService(EthWrapper.class);
            locomotion = container.getService(Locomotion.class);

            container.startInstanciedThreads();

            ethWrapper.setPositionAndOrientation(200, 500, 2.215);
            Thread.sleep(100);
            ethWrapper.getCurrentPositionAndOrientation();
            ethWrapper.setX(0);
            Thread.sleep(100);
            ethWrapper.getCurrentPositionAndOrientation();
            ethWrapper.setY(500);
            Thread.sleep(100);
            ethWrapper.getCurrentPositionAndOrientation();
            ethWrapper.setOrientation(1.57);
            Thread.sleep(100);
            ethWrapper.getCurrentPositionAndOrientation();

            for (Speed speed : Speed.values()){
                ethWrapper.setTranslationnalSpeed(speed.translationSpeed);
                ethWrapper.setRotationnalSpeed(speed.rotationSpeed);
                signe = - signe;

                start = System.currentTimeMillis();
                locomotion.moveLengthwise(500, false, true);
                log.debug("Vitesse Translation : " + speed.translationSpeed);
                log.debug("Temps : " + (System.currentTimeMillis() - start));

                locomotion.turn(signe*Math.PI/2, false, true);
                log.debug("Vitesse Rotation : " + speed.rotationSpeed);
                log.debug("Temps : " + (System.currentTimeMillis() - start));

                Thread.sleep(1000);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void testCodeuse() throws Exception{
        double a = Math.PI + 0.2;
        double b = -Math.PI + 0.2;
        double c = 2*Math.PI + 0.2;

        log.debug("Angle a : "+ a%Math.PI + "Angle b : " + b%Math.PI + "Angle c : " + c%Math.PI);
        Sleep.sleep(20000);
    }

    @Test
    public void testSpam() throws Exception{
        eth = container.getService(ThreadEth.class);
        container.startInstanciedThreads();

        for (int i = 0; i<1001; i++){
            eth.communicate(1, String.format("%s", i));
            Sleep.sleep(200);
        }
    }
}