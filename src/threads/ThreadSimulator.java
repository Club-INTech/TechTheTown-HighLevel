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

package threads;

import container.Service;
import enums.ActuatorOrder;
import enums.CommunicationHeaders;
import enums.TurningStrategy;
import pfg.config.Config;
import strategie.GameStateSimulator;
import tests.container.A;
import utils.Log;

import java.io.*;
import java.net.*;

/**
 * Thread qui simule le LL, ou plutot la partie com du LL,
 * Utile pour tester tout le HL sans teensy
 *
 * @author rem
 */

public class ThreadSimulator extends AbstractThread implements Service {

    /** Nom du Thread */
    public String name;

    /** GameState propre au LL ?? */
    private GameStateSimulator state;

    /** Sockets */
    private ServerSocket server;
    private Socket client;

    /** IO */
    private BufferedReader input;
    private BufferedWriter output;

    /** Buffers pour fichiers de debug */
    private BufferedWriter out;

    /** Shutdown... */
    public static boolean shutdown = false;

    /**
     * Constructeur du Simulateur ! Tout s'instancie automatiquement à partir du moment où l'on instancie un ThreadSimulator
     * @param config
     * @param log
     */
    public ThreadSimulator(Config config, Log log, GameStateSimulator state){
        super(config, log);
        this.name = "Simulator";
        this.state = state;
    }

    /**
     * Créer l'Interface Ethernet (socket & IO)
     */
    private void createInterface(){
        try {
            server = new ServerSocket(23500);
            client = server.accept();
            input = new BufferedReader(new InputStreamReader(client.getInputStream()));
            output = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));

        }catch(IOException e){
            log.debug("IO Exception : manque de droits pour IO");
            e.printStackTrace();
        }
    }

    /**
     * Fonction pour communiquer avec le HL
     * @param header
     * @param messages
     */
    private void communicate(CommunicationHeaders header, String... messages){
        try {
            for (String mess : messages) {
                if(header != null){
                    output.write(header.getFirstHeader());
                    output.write(header.getSecondHeader());
                }
                output.write(mess);
                output.newLine();
                output.flush();
            }
        }catch (IOException e){
            log.debug("Manque de droits pour l'output ??");
            e.printStackTrace();
        }
    }

    /**
     * Fonction qui centralise les requete et répond en fonction (copie du main du LL)
     */
    private void respond(String request){
        String[] messages;
        String head;

        communicate(CommunicationHeaders.DEBUG, "Message recu : " + request);
        messages = request.split(" ");
        head = messages[0];

        try {
            /** INITIALISATION */
            if (head.equals(ActuatorOrder.SET_X.getSerialOrder())) {

            }
            else if (head.equals(ActuatorOrder.SET_Y.getSerialOrder())) {

            }
            else if (head.equals(ActuatorOrder.SET_ORIENTATION.getSerialOrder())) {

            }
            else if (head.equals(ActuatorOrder.SET_POSITION.getSerialOrder())){

            }
            else if (head.equals(ActuatorOrder.INITIALISE_HOOK.getSerialOrder())) {

            }
            else if (head.equals(ActuatorOrder.ENABLE_HOOK.getSerialOrder())) {

            }
            else if (head.equals(ActuatorOrder.DISABLE_HOOK.getSerialOrder())) {

            }

            /** LOCOMOTION */
            else if (head.equals(ActuatorOrder.MOVE_LENTGHWISE.getSerialOrder())) {
                state.moveLengthwise(Float.parseFloat(messages[1]));
            }
            else if (head.equals(ActuatorOrder.TURN.getSerialOrder())) {
                state.turn(Float.parseFloat(messages[1]), TurningStrategy.FASTEST);
            }
            else if (head.equals(ActuatorOrder.TURN_RIGHT_ONLY.getSerialOrder())) {
                state.turn(Float.parseFloat(messages[1]), TurningStrategy.RIGHT_ONLY);
            }
            else if (head.equals(ActuatorOrder.TURN_LEFT_ONLY.getSerialOrder())) {
                state.turn(Float.parseFloat(messages[1]), TurningStrategy.LEFT_ONLY);
            }
            else if (head.equals(ActuatorOrder.STOP.getSerialOrder())) {

            }
            else if (head.equals(ActuatorOrder.IS_ROBOT_MOVING.getSerialOrder())) {

            }
            else if (head.equals(ActuatorOrder.SET_TRANSLATION_SPEED.getSerialOrder())) {

            }
            else if (head.equals(ActuatorOrder.SET_ROTATIONNAL_SPEED.getSerialOrder())) {

            }
            else if (head.equals(ActuatorOrder.SEND_POSITION.getSerialOrder())) {
                communicate(null, String.format("%s",state.getPosition().getX()),
                        String.format("%s",state.getPosition().getY()),
                        String.format("%s", state.getOrientation()));
            }

            /** ACTIONNEURS */
            //TODO A remplir ? (faire le liens avec ActuatorOrder ??)

            else {
                //TODO condition à compléter
                communicate(CommunicationHeaders.DEBUG, "Mode Simu : balec");
            }
        }catch (InterruptedException e){
            log.debug("Gestion MultiThread fail...");
            e.printStackTrace();
        }
    }

    @Override
    public void run(){
        String buffer;
        createInterface();
        log.debug("ThreadSimulator started");

        while(!shutdown){
            try{
                buffer = input.readLine();
                respond(buffer);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void updateConfig(){
    }
}
