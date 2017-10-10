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
import enums.CommunicationHeaders;
import enums.TurningStrategy;
import pfg.config.Config;
import strategie.GameStateSimulator;
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
                    output.write(header.firstHeader);
                    output.write(header.secondHeader);
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
            if (head.equals("cx")) {

            } else if (head.equals("cy")) {

            } else if (head.equals("co")) {

            } else if (head.equals("nh")) {

            } else if (head.equals("eh")) {

            } else if (head.equals("dh")) {

            }

            /** LOCOMOTION */
            else if (head.equals("d")) {
                float distance = Float.parseFloat(messages[1]);
                state.moveLengthwise(distance);
            } else if (head.equals("t")) {
                state.turn(Float.parseFloat(messages[1]), TurningStrategy.FASTEST);
            } else if (head.equals("tor")) {
                state.turn(Float.parseFloat(messages[1]), TurningStrategy.RIGHT_ONLY);
            } else if (head.equals("tol")) {
                state.turn(Float.parseFloat(messages[1]), TurningStrategy.LEFT_ONLY);
            } else if (head.equals("stop")) {

            } else if (head.equals("f")) {

            } else if (head.equals("ctv")) {

            } else if (head.equals("crv")) {

            } else if (head.equals("?xyo")) {
                communicate(null, String.format("%s",state.getPosition().getX()),
                        String.format("%s",state.getPosition().getY()),
                        String.format("%s", state.getOrientation()));
            }

            /** ACTIONNEURS */
            //TODO A remplir ? (faire le liens avec ActuatorOrder ??)

            else if (head == "cv0" || head == "cv1" || head == "ct0" || head == "ct1" || head == "cr0" || head == "cr1" || head == "efm" || head == "dfm" || head == "sus") {
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
