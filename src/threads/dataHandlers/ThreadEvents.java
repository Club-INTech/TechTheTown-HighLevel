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

package threads.dataHandlers;

import enums.EventType;
import pfg.config.Config;
import threads.AbstractThread;
import utils.Log;

import java.util.concurrent.ConcurrentLinkedQueue;


/**
 *  Gestionnaire des events LL
 *  @author discord, rem
 */
public class ThreadEvents extends AbstractThread
{
    /** Config & Log */
    private Config config;
    private Log log;

    /** Buffer de lecture des events, rempli par ThreadEth */
    private volatile ConcurrentLinkedQueue<String> events;

    /** Buffer d'envoie des events */
    private volatile ConcurrentLinkedQueue<String> unableToMoveEvent = new ConcurrentLinkedQueue<>();

    private boolean cubeTakenBrasAV=false;

    private boolean cubeTakenBrasAR=false;

    private boolean sth_detected_basic =false;

    /** Le robot bouge */
    public volatile boolean isMoving;

    /**
     * Constructeur
     * @param config
     * @param log
     * @param eth
     */
    public ThreadEvents(Config config, Log log, ThreadEth eth)
    {
        this.config = config;
        this.log = log;
        events = eth.getEventBuffer();
        this.isMoving = false;
    }

    @Override
    public void run()
    {
        String event;
        Thread.currentThread().setPriority(8);
        while(!ThreadEth.shutdown)
        {
            try {
                if (events.peek() != null) {
                    event = events.poll();
                    String[] message = event.split(" ");

                    if (message[0].equals(EventType.BLOCKED.getEventId())) {
                        log.critical("Event du LL : UnableToMove");
                        unableToMoveEvent.add(message[1]);
                    }
                    else if (message[0].equals(EventType.STOPPEDMOVING.getEventId())){
                        log.debug("Le robot a fini de bouger");
                        this.isMoving = false;
                        log.debug("isMoving variable has been defined to False");
                    }
                    else if(message[0].equals(EventType.CUBE_PRIS_BRAS_AVANT.getEventId())){
                        cubeTakenBrasAV=true;
                        log.debug("Le robot a pris un cube en utilisant le bras AV");
                    }
                    else if(message[0].equals(EventType.CUBE_PRIS_BRAS_ARRIERE.getEventId())){
                        cubeTakenBrasAR=true;
                        log.debug("Le robot a pris un cube en utilisant le bras AR");
                    }
                    else if(message[0].equals(EventType.BASIC_DETECTION_TRIGGERED.getEventId())){
                        sth_detected_basic =true;
                        log.debug("La basic detection a été triggered");
                    }
                } else {
                    Thread.sleep(5);
                }
            }catch (InterruptedException e){
                e.getStackTrace();
            }
        }
    }

    public ConcurrentLinkedQueue<String> getUnableToMoveEvent() {
        return unableToMoveEvent;
    }

    public boolean getCubeTakenBrasAV() {
        return cubeTakenBrasAV;
    }

    public void setCubeTakenBrasAV(boolean value){
        this.cubeTakenBrasAV=value;
    }

    public boolean getCubeTakenBrasAR() {
        return cubeTakenBrasAR;
    }

    public void setCubeTakenBrasAR(boolean value){
        this.cubeTakenBrasAR=value;
    }

    public void setIsMoving(boolean value){ this.isMoving=value; }

    public boolean getIsMoving(){ return this.isMoving; }

    public boolean isSth_detected_basic() {
        return sth_detected_basic;
    }
}