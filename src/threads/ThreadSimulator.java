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
import pfg.config.Config;
import strategie.GameState;
import strategie.LLGameState;
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
    private LLGameState state;

    /** Headers */
    public final char[] eventHeader = {0x13, 0x37};
    public final char[] ultrasoundHeader = {0x01, 0x10};
    public final char[] debugHeader = {0x02, 0x20};

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
     *
     * @param config
     * @param log
     */
    public ThreadSimulator(Config config, Log log, LLGameState state){
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
                if (header == CommunicationHeaders.EVENT){
                    output.write(eventHeader[0]);
                    output.write(eventHeader[1]);
                }else if(header == CommunicationHeaders.DEBUG){
                    output.write(debugHeader[0]);
                    output.write(debugHeader[1]);
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
        communicate(CommunicationHeaders.DEBUG, "Message recu : " + request);
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
