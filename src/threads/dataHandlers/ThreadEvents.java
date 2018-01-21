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
    private ConcurrentLinkedQueue<String> events;

    /** Buffer d'envoie des events */
    private ConcurrentLinkedQueue<String> unableToMoveEvent = new ConcurrentLinkedQueue<>();

    /** Le robot bouge */
    private volatile Boolean isMoving;

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
        this.isMoving = new Boolean(false);
    }

    @Override
    public void run()
    {
        String event;
        Thread.currentThread().setPriority(6);
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
                        log.debug("Arret du robot");
                        synchronized (this.isMoving) {
                            this.isMoving = new Boolean(false);
                        }
                    }
                } else {
                    Thread.sleep(100);
                }
            }catch (InterruptedException e){
                e.getStackTrace();
            }
        }
    }

    public ConcurrentLinkedQueue<String> getUnableToMoveEvent() {
        return unableToMoveEvent;
    }
    public Boolean getIsMovingObject() {
        return this.isMoving; }
    public void setIsMoving(boolean value){ this.isMoving=value; }
}