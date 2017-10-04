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
import pfg.config.Config;
import smartMath.Vec2;
import table.Table;
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

    /** Table DIFFERENTE de celui du HL, afin de pouvoir simuler au mieux certains évènements */
    private Table LLTable;

    /** Position & orientation du Robot sur la table */
    private Vec2 LLPosition;
    private double LLorientation;

    /** Headers */
    private final char[] eventHeader = {0x13, 0x37};
    private final char[] ultrasoundHeader = {0x01, 0x10};
    private final char[] debugHeader = {0x02, 0x20};

    /** Sockets */
    private ServerSocket server;
    private Socket socket;

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
    public ThreadSimulator(Config config, Log log){
        super(config, log);
        this.name = "Simulator";
        createInterface();
    }

    /**
     * Créer l'Interface Ethernet (socket & IO)
     */
    private void createInterface(){
        try {
            server = new ServerSocket(2009);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        }catch(IOException e){
            log.debug("IO Exception : manque de droits pour IO");
            e.printStackTrace();
        }
    }

    /**
     * Réponse à ?xyo
     * @throws IOException
     */
    public void sendPosition() throws IOException {
        output.write(String.format("%s", LLPosition.getX()));
        output.flush();
        output.write(String.format("%s", LLPosition.getY()));
        output.flush();
        output.write(String.format("%s", LLorientation).substring(0,8));
        output.flush();
    }

    @Override
    public void run(){
        String buffer;
        createInterface();
        log.debug("ThreadSimulator started");

        try {
            socket = server.accept();
        }catch (IOException e){
            e.printStackTrace();
        }

        while(!shutdown){
            try{
                log.debug("FLAG");
                buffer = input.readLine();
                log.debug("FLAG");
                if (buffer == "?xyo"){
                    sendPosition();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void updateConfig(){
    }
}
