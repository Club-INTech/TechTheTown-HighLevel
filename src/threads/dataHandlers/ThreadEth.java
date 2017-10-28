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

import container.Service;
import enums.CommunicationHeaders;
import enums.ConfigInfoRobot;
import pfg.config.Config;
import smartMath.XYO;
import table.Table;
import threads.AbstractThread;
import utils.Log;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Classe implémentant une communication via Ethernet pour communiquer avec le Bas Niveau,
 * @author rem
 */
public class ThreadEth extends AbstractThread implements Service {

    /** Nom */
    public String name;

    /** Flux d'entrée du port */
    private BufferedReader input;

    /** Flux de sortie du port */
    private BufferedWriter output;

    /** Socket */
    private Socket socket;
    private InetSocketAddress server;

    /** IP Teensy & local */
    private String teensyAdress = "192.168.0.1";
    private String localAdress = "127.0.0.1";

    /** Timeout pour l'envoie de message */
    private static final int TIMEOUT = 1000;

    /** True si besoin de fichiers de debug */
    private boolean debug = true;

    /** Buffer pour fichiers de debug */
    private BufferedWriter outStandard;
    private BufferedWriter outDebug;

    /** True pour couper la connexion (pas trouvé d'autres idées, mais la lib java.net a probablement un truc propre à proposer) */
    public static boolean shutdown = false;

    /** True si utilisation du simulateur LL */
    private static boolean simulation;

    /** Singeproof de la connexion : l'interface ne doit etre initialisée qu'une fois */
    private boolean interfaceCreated = false;

    /** Buffers représentant les différents canaux */
    private ConcurrentLinkedQueue<String> standardBuffer = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<String> eventBuffer = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<String> ultrasoundBuffer = new ConcurrentLinkedQueue<>();

    /** Le "canal" position & orientation */
    private XYO positionAndOrientation = new XYO(Table.entryPosition, Table.entryOrientation);
    private String splitString = " ";

    /**
     * Créer l'interface Ethernet en pouvant choisir ou non de simuler le LL
     * @param log
     */
    private ThreadEth(Log log, Config config){
        super(config, log);
        this.name = "Teensy";
        if(debug){
            try
            {
                File file = new File("orders.txt");
                File fileDebug = new File("debugLL.txt");
                if (!file.exists())
                {
                    file.createNewFile();
                }
                if(!file.exists())
                {
                    file.createNewFile();
                }
                outStandard = new BufferedWriter(new FileWriter(file));
                outDebug = new BufferedWriter(new FileWriter(fileDebug));

            } catch (IOException e) {
                log.critical("Manque de droits pour l'output");
                e.printStackTrace();
            }
        }
        else{
            this.outStandard = null;
            this.outDebug = null;
        }
        updateConfig();
    }

    /**
     * Initialise la connexion
     * @throws IOException
     */
    private void createInterface() {
        try {
            if (simulation) {
                socket = new Socket(localAdress, 23500);
            } else {
                socket = new Socket(teensyAdress, 23500);
            }
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            interfaceCreated = true;

        }catch (IOException e){
            log.critical("Manque de droit pour l'output");
            e.printStackTrace();
        }
    }

    /**
     * Fonction verifiant si on recoit bien de l'ascii etendu : sinon, bah le bas niveau deconne.
     * @param inputLines
     * @return
     * @throws Exception
     */
    @SuppressWarnings("javadoc")
    public boolean isAsciiExtended(String inputLines) throws Exception
    {
        for (int i = 0; i < inputLines.length(); i++)
        {
            if (inputLines.charAt(i) > 259)
            {
                log.critical(inputLines+" n'est pas ASCII");
                return false;
            }
        }
        return true;
    }

    /**
     * Attend la réponse du LL; si le timeout est dépassé, lancement d'une exception
     */
    private synchronized String waitAndGetResponse(){
        String response = null;
        long startTime = System.currentTimeMillis();

        while((System.currentTimeMillis() - startTime) < TIMEOUT) {
            try {
                if((response = standardBuffer.peek()) != null) break;
                Thread.sleep(2);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if(response == null) {
            return "";
        } else {
            standardBuffer.poll();
        }
        return response;
    }

    /**
     * Ferme la socket !
     */
    public synchronized void close(){
        try {
            shutdown = true;
            socket.close();
        }catch (IOException e){
            log.debug("Socket refuses to get closed !");
            e.printStackTrace();
        }
    }

    /*******************************************
     * FONCTION COMMUNICATION & RUN (LISTENER) *
     *******************************************/


    /**
     * Fonction pour envoyer un message au LL
     * @return LL response
     */
    public synchronized String[] communicate(int nb_line_response, String... message)
    {
        String mess = "";
        int tries = 0;
        standardBuffer.clear();
        String inputLines[] = new String[nb_line_response];

        for (String m : message){
            mess += m + " ";
        }

        /* Envoie de l'ordre */
        try
        {
            mess += "\r\n";
            // On envoie au LL le nombre de caractères qu'il est censé recevoir
            output.write(mess, 0, mess.length());
            output.flush();

            if(debug)
            {
                outStandard.write(mess);
                outStandard.newLine();
                outStandard.flush();
            }

        }
        catch (SocketException e)
        {
            log.critical("LL ne répond pas, on ferme la socket et on en recrée une...");
            try {
                if (socket != null) {
                    socket.close();
                    Thread.sleep(1000);
                }
            }
            catch (Exception e1){
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        catch (IOException except){
            log.debug("LL ne répond pas, on shutdown");
            shutdown = true;
            except.printStackTrace();
        }

        /* Réponse du LL (listener dans le run) */
        try{
            for (int i = 0 ; i < nb_line_response; i++)
            {
                inputLines[i] = waitAndGetResponse();

                if(debug) {
                    outStandard.write("\t" + inputLines[i]);
                    outStandard.newLine();
                    outStandard.flush();
                }

                if(inputLines[i]==null || inputLines[i].replaceAll(" ", "").equals(""))
                {
                    log.critical("Reception de "+inputLines[i]+" , en réponse à " + message[0].replaceAll("\r", "").replaceAll("\n", "") + " : Attente du LL");
                    if(debug)
                    {
                        outStandard.write("Reception de "+inputLines[i]+" , en réponse à " + message[0].replaceAll("\r", "").replaceAll("\n", "") + " : Attente du LL");
                        outStandard.newLine();
                        outStandard.flush();
                    }

                    while ((inputLines[i]==null || inputLines[i].replaceAll(" ", "").equals("")) && tries < 5){
                        Thread.sleep(200);
                        tries += 1;
                        inputLines[i] = waitAndGetResponse();
                    }

                    if(tries == 5){
                        throw new SocketException("Pas de réponse...");
                    }
                }

                if(!isAsciiExtended(inputLines[i]))
                {
                    log.critical("Reception de "+inputLines[i]+" (non Ascii) , en réponse à "+ message[0].replaceAll("\r", "").replaceAll("\n", "") + " envoi du message a nouveau");
                    communicate(nb_line_response, message); // On retente
                }
            }
            if (nb_line_response != 0) {
                outStandard.newLine();
                outStandard.flush();
            }
        }
        catch (SocketException e1){
            log.critical("LL ne répond pas, on ferme la socket et on en recrée une...");
            try {
                if (socket != null) {
                    socket.close();
                    Thread.sleep(500);
                    createInterface();
                    communicate(nb_line_response, message);
                }
            }
            catch (Exception e2){
                e1.printStackTrace();
            }
            e1.printStackTrace();
        }
        catch (Exception except2){
            log.debug("LL ne répond pas, on shutdown");
            shutdown = true;
            except2.printStackTrace();
        }
        return inputLines;
    }

    @Override
    public void run(){
        String buffer;
        Thread.currentThread().setPriority(8);
        createInterface();
        log.debug("ThreadEth started");

        while(!shutdown)
        {
            try
            {
                buffer = input.readLine();
                if(buffer.length()>=2 && !(buffer.replaceAll(" ", "").equals("")))
                {
                    char[] headers = {buffer.toCharArray()[0], buffer.toCharArray()[1]};
                    if (CommunicationHeaders.EVENT.getFirstHeader() == headers[0] && CommunicationHeaders.EVENT.getSecondHeader() == headers[1]) {
                        eventBuffer.add(buffer);
                        continue;
                    } else if (CommunicationHeaders.ULTRASON.getFirstHeader() == headers[0] && CommunicationHeaders.ULTRASON.getSecondHeader() == headers[1]) {
                        ultrasoundBuffer.add(buffer);
                        continue;
                    } else if (CommunicationHeaders.POSITION.getFirstHeader() == headers[0] && CommunicationHeaders.POSITION.getSecondHeader() == headers[1]) {
                        synchronized (this.positionAndOrientation){
                            positionAndOrientation = new XYO(buffer, splitString);
                        }
                        continue;
                    } else if (CommunicationHeaders.DEBUG.getFirstHeader() == headers[0] && CommunicationHeaders.DEBUG.getSecondHeader() == headers[1]) {
                        outDebug.write(buffer.substring(2));
                        outDebug.newLine();
                        outDebug.flush();
                        continue;
                    }
                    else {
                        standardBuffer.add(buffer);
                        continue;
                    }
                }
                else if (!(buffer.replaceAll(" ", "").equals("")))
                {
                    standardBuffer.add(buffer);
                    continue;
                }
            }
            catch (SocketException se)
            {
                log.critical("LL ne répond pas, on ferme la socket et on en recrée une...");
                try {
                    if (socket != null) {
                        socket.close();
                        Thread.sleep(1000);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                se.printStackTrace();
            }
            catch (IOException ioe) {
                log.debug("LL ne répond pas, on shutdown");
                shutdown = true;
                ioe.printStackTrace();
            }
        }
    }

    /** Getters & Setters */
    public ConcurrentLinkedQueue<String> getEventBuffer() {return eventBuffer;}
    public ConcurrentLinkedQueue<String> getUltrasoundBuffer() {return ultrasoundBuffer;}
    public ConcurrentLinkedQueue<String> getStandardBuffer() {return standardBuffer;}
    public XYO getPositionAndOrientation() {
        synchronized (positionAndOrientation) {
            return positionAndOrientation;
        }
    }
    public boolean isInterfaceCreated(){return interfaceCreated;}

    @Override
    public void updateConfig(){
        simulation = config.getBoolean(ConfigInfoRobot.SIMULATION);
    }
}