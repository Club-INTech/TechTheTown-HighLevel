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

import pfg.config.Config;
import smartMath.Vec2;
import table.Table;
import utils.Log;

import java.io.*;
import java.net.*;
import java.util.Scanner;

/**
 * Thread qui simule le LL, ou plutot la partie com du LL,
 * Utile pour tester tout le HL sans teensy
 *
 * @author rem
 */

public class ThreadSimulator extends AbstractThread {

    /** Table DIFFERENTE de celui du HL, afin de pouvoir simuler au mieux certains évènements */
    private Table LLTable;

    /** Position du Robot sur la table */
    private Vec2 LLPosition;

    /** Headers */
    public final char[] eventHeader = {0x13, 0x37};
    public final char[] ultrasoundHeader = {0x01, 0x10};
    public final char[] debugHeader = {0x02, 0x20};

    /** Sockets */
    public Socket socket;

    /** IO */
    public BufferedReader input;
    public BufferedWriter output;

    public Scanner scanner;
    public InetSocketAddress server;

    public ThreadSimulator(Config config, Log log){
        super(config, log);
    }

    @Override
    public void run(){
        initialization();
        log.debug("To send :");
        String message = "?xyo";
        String response;

        while (message != "stop") {
            try {
                log.debug("Message send : " + message);
                output.write(message, 0, message.length());
                output.newLine();
                output.flush();

                response = input.readLine();
                log.debug("Réponse du LL (?): "+ response);

                Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void initialization(){
        try {
            log.debug("Searching Teensy...");
            scanner = new Scanner(System.in);
            server = new InetSocketAddress("192.168.0.1", 23500);
            socket = new Socket(server.getAddress(), server.getPort());
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            log.debug("Connected !");

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void updateConfig(){

    }
}
