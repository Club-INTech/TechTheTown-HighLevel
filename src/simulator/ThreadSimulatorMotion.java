/*
 * Copyright (c) 2016, INTech.
 *
 * This file is part of INTech's HighLevel.
 *
 *  INTech's HighLevel is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  INTech's HighLevel is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with it.  If not, see <http://www.gnu.org/licenses/>.
 */

package simulator;

import container.Service;
import enums.ActuatorOrder;
import enums.TurningStrategy;
import pfg.config.Config;
import threads.AbstractThread;
import utils.Log;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Thread qui simule les déplacements du robot : le thread attend le temps qu'il faut en mettant à jour les informations
 * dont pourrait avoir besoin le HL
 * @author rem
 */

public class ThreadSimulatorMotion extends AbstractThread implements Service {

    /** Nom du Thread */
    public String name;

    /** GameState propre au LL ?? */
    private GameStateSimulator state;

    /** Ordre de mouvement du HL */
    private ConcurrentLinkedQueue<String> orders;

    /** Shutdown... */
    public static boolean shutdown = false;

    /**
     * Constructeur du Simulateur ! Tout s'instancie automatiquement à partir du moment où l'on instancie un ThreadSimulatorMotion
     * @param config
     * @param log
     */
    public ThreadSimulatorMotion(Config config, Log log, GameStateSimulator state, ThreadSimulator sim){
        super(config, log);
        this.name = "simulator";
        this.state = state;
        this.orders = sim.getMotionOrderBuffer();
    }

    /**
     * Fonction qui centralise les requete et répond en fonction (copie du main du LL)
     */
    private void move(String order){
        String messages[];
        String head;

        messages = order.split(" ");
        head = messages[0];

        try {
            if (head.equals(ActuatorOrder.MOVE_LENTGHWISE.getSerialOrder())) {
                state.moveLengthwise(Float.parseFloat(messages[1]));
            } else if (head.equals(ActuatorOrder.TURN.getSerialOrder())) {
                state.turn(Float.parseFloat(messages[1]), TurningStrategy.FASTEST);
            } else if (head.equals(ActuatorOrder.TURN_LEFT_ONLY.getSerialOrder())) {
                state.turn(Float.parseFloat(messages[1]), TurningStrategy.LEFT_ONLY);
            } else if (head.equals(ActuatorOrder.TURN_RIGHT_ONLY.getSerialOrder())) {
                state.turn(Float.parseFloat(messages[1]), TurningStrategy.RIGHT_ONLY);
            } else {
                log.warning("Ordre Inconnue : " + head);
            }
        }catch (InterruptedException e){
            log.critical("Mauvaise gestion du Multi-Threading...");
            e.printStackTrace();
        }
    }

    @Override
    public void run(){
        String order;
        log.debug("ThreadSimulatorMotion started");

        while(!shutdown){
            try{
                if(orders.peek() != null){
                    order = orders.poll();
                    move(order);
                }else{
                    Thread.sleep(50);
                }
            }catch (InterruptedException e){
                log.critical("Mauvaise gestion du multi-Threading");
                e.printStackTrace();
            }
        }
    }

    @Override
    public void updateConfig(){
    }
}
