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

import enums.ActuatorOrder;
import enums.EventType;
import pfg.config.Config;
import threads.AbstractThread;
import utils.Log;

import java.util.ArrayList;
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

    /** ThreadEth */
    private ThreadEth eth;

    /** Buffer de lecture des events, rempli par ThreadEth */
    private volatile ConcurrentLinkedQueue<String> events;

    /** Buffer d'envoie des events */
    private volatile ConcurrentLinkedQueue<String> unableToMoveEvent = new ConcurrentLinkedQueue<>();

    /** Le jumper a-t-il été enlevé ?*/
    private boolean jumperRemoved = false;

    /** Le bras avant a-t-il pris un cube ?*/
    private boolean cubeTakenBrasAV=false;

    /** Le bras arrière a-t-il pris un cube ?*/
    private boolean cubeTakenBrasAR=false;

    /** Un obstacle a-t-il été détecté ?*/
    private boolean obstacleBasicDetected=false;

    /** Liste des IDs des events reçus pendant le match */
    private ArrayList<String> eventIDReceived = new ArrayList<>();

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
        this.eth=eth;
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
                    if (event.length()>3) {
                        String eventID = event.substring(0,4);
                        if (!eventIDReceived.contains(eventID)) {
                            boolean validEvent=false;
                            String[] message = event.substring(4).split(" ");
                            if (message[0].equals(EventType.BLOCKED.getEventName())) {
                                validEvent=true;
                                log.critical("Event du LL : UnableToMove");
                                unableToMoveEvent.add(message[1]);
                            } else if (message[0].equals(EventType.STOPPEDMOVING.getEventName())) {
                                validEvent=true;
                                log.debug("StoppedMoving : Le robot a fini de bouger");
                                this.isMoving = false;
                            } else if (message[0].equals(EventType.CUBE_PRIS_BRAS_AVANT.getEventName())) {
                                validEvent=true;
                                this.cubeTakenBrasAV = true;
                                log.debug("Prise de cube bras avant : REUSSITE");
                            } else if (message[0].equals(EventType.CUBE_PAS_PRIS_BRAS_AVANT.getEventName())) {
                                validEvent=true;
                                log.debug("Prise de cube bras avant : ECHEC");
                            } else if (message[0].equals(EventType.CUBE_PRIS_BRAS_ARRIERE.getEventName())) {
                                validEvent=true;
                                this.cubeTakenBrasAR = true;
                                log.debug("Prise de cube bras arrière : REUSSITE");
                            } else if (message[0].equals(EventType.CUBE_PAS_PRIS_BRAS_ARRIERE.getEventName())) {
                                validEvent=true;
                                log.debug("Prise de cube bras arrière : ECHEC");
                            } else if (message[0].equals(EventType.BASIC_DETECTION_TRIGGERED.getEventName())) {
                                validEvent=true;
                                this.obstacleBasicDetected = true;
                                log.debug("La basic detection a été triggered");
                            } else if (message[0].equals(EventType.BASIC_DETECTION_FINISHED.getEventName())) {
                                validEvent=true;
                                this.obstacleBasicDetected = false;
                                log.debug("La basicDetection ne détecte plus rien");
                            } else if (message[0].equals(EventType.JUMPER_REMOVED.getEventName())) {
                                validEvent=true;
                                this.jumperRemoved = true;
                                log.debug("Jumper enlevé");
                            } else {
                                log.critical("////////// MAUVAIS EVENT RECU ///////////");
                                log.critical(event);
                                log.critical("//////// FIN MAUVAIS EVENT RECU /////////");
                            }
                            if (validEvent){
                                eventIDReceived.add(eventID);
                                this.eth.communicate(0, ActuatorOrder.ACKNOWLEDGE.getEthernetOrder(), eventID);
                            }
                        }
                        else{
                            log.debug("Event déjà reçu : "+event);
                        }
                    }
                    else{
                        log.critical("////////// MAUVAIS EVENT RECU ///////////");
                        log.critical(event);
                        log.critical("//////// FIN MAUVAIS EVENT RECU /////////");
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

    public boolean isObstacleBasicDetected() {
        return obstacleBasicDetected;
    }

    public boolean wasJumperRemoved() {
        return jumperRemoved;
    }

    public void setJumperRemoved(boolean value){
        this.jumperRemoved=value;
    }
}