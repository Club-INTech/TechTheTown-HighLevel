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

    /** Log à utilisé */
    Log log;

    /** Nom */
    public String name;

    /** Flux d'entrée du port */
    private BufferedReader input;

    /** Flux de sortie du port */
    private BufferedWriter output;

    /** Socket */
    private Socket socket;
    private InetSocketAddress server;

    /** Timeout pour l'envoie de message */
    private static final int TIMEOUT = 1000;

    /** True si besoin de fichiers de debug */
    private boolean debug = true;

    /** Buffer pour fichiers de debug */
    private BufferedWriter outStandart;
    private BufferedWriter outDebug;

    /** True pour couper la connexion (pas trouvé d'autres idées, mais la lib java.net a probablement un truc propre à proposer) */
    public static boolean shutdown = false;

    /** Buffers représentant les différents canaux */
    private ConcurrentLinkedQueue<String> standardBuffer = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<String> eventBuffer = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<String> ultrasoundBuffer = new ConcurrentLinkedQueue<>();

    /** Headers */
    public final char[] eventHeader = {0x13, 0x37};
    public final char[] ultrasoundHeader = {0x01, 0x10};
    public final char[] debugHeader = {0x02, 0x20};

    /**
     * Créer l'interface Ethernet !
     * @param log
     */
    private ThreadEth(Log log){
        super();
        this.log = log;
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
                outStandart = new BufferedWriter(new FileWriter(file));
                outDebug = new BufferedWriter(new FileWriter(fileDebug));
                createInterface();

            } catch (IOException e) {
                log.critical("Manque de droits pour l'output des ordres");
                e.printStackTrace();
            }
        }
        else{
            this.outStandart = null;
            this.outDebug = null;
        }
    }

    /**
     * Initialise la connexion
     * @throws IOException
     */
    private void createInterface() throws IOException{
        server = new InetSocketAddress("192.168.0.1", 23500);
        socket = new Socket(server.getAddress(), server.getPort());
        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
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
    public void close(){
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
     * Envoie de message au LL & réception
     * @return LL response
     */
    public synchronized String[] communicate(String message, int nb_line_response){
        String[] mess = {message};
        return communicate(mess, nb_line_response);
    }

    /**
     * Fonction pour envoyer un message au LL
     * @return LL response
     */
    public synchronized String[] communicate(String[] message, int nb_line_response)
    {
        int length;
        standardBuffer.clear();
        String inputLines[] = new String[nb_line_response];

        /* Envoie de l'ordre */
        try
        {
            for (String m : message)
            {
                length = m.length();
                m += "\r\n";
                // On envoie au LL le nombre de caractères qu'il est censé recevoir
                output.write(length + " " + m, 0, m.length());
                output.flush();

                if(debug)
                {
                    outStandart.write(m);
                    outStandart.newLine();
                    outStandart.flush();
                }
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
                    outStandart.write("\t" + inputLines[i]);
                    outStandart.newLine();
                    outStandart.flush();
                }

                if(inputLines[i]==null || inputLines[i].replaceAll(" ", "").equals("")|| inputLines[i].replaceAll(" ", "").equals("-"))
                {
                    log.critical("Reception de "+inputLines[i]+" , en réponse à " + message[0].replaceAll("\r", "").replaceAll("\n", "") + " : Attente du LL");
                    if(debug)
                    {
                        outStandart.write("Reception de "+inputLines[i]+" , en réponse à " + message[0].replaceAll("\r", "").replaceAll("\n", "") + " : Attente du LL");
                        outStandart.newLine();
                        outStandart.flush();
                    }

                    while (inputLines[i]==null || inputLines[i].replaceAll(" ", "").equals("")|| inputLines[i].replaceAll(" ", "").equals("-")){
                        Thread.sleep(500);
                        inputLines[i] = waitAndGetResponse();
                    }
                }

                if(!isAsciiExtended(inputLines[i]))
                {
                    log.critical("Reception de "+inputLines[i]+" (non Ascii) , en réponse à "+ message[0].replaceAll("\r", "").replaceAll("\n", "") + " envoi du message a nouveau");
                    communicate(message, nb_line_response); // On retente
                }
            }
        }
        catch (SocketException e1){
            log.critical("LL ne répond pas, on ferme la socket et on en recrée une...");
            try {
                if (socket != null) {
                    socket.close();
                    Thread.sleep(1000);
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
        log.debug("ThreadEth started");
        while(!shutdown)
        {
            try
            {
                buffer = input.readLine();
                if(buffer.length()>=2 && !(buffer.replaceAll(" ", "").equals("")/*|| buffer.replaceAll(" ", "").equals("-")*/))
                {
                    if (buffer.toCharArray()[0] == eventHeader[0] && buffer.toCharArray()[1] == eventHeader[1]) {
                        eventBuffer.add(buffer);
                        continue;
                    } else if (buffer.toCharArray()[0] == ultrasoundHeader[0] && buffer.toCharArray()[1] == ultrasoundHeader[1]) {
                        ultrasoundBuffer.add(buffer);
                        continue;
                    } else if (buffer.toCharArray()[0] == debugHeader[0] && buffer.toCharArray()[1] == debugHeader[1]) {
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
                else if (!(buffer.replaceAll(" ", "").equals("")/*|| buffer.replaceAll(" ", "").equals("-")*/))
                {
                    standardBuffer.add(buffer);
                    continue;
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
                }catch (Exception e1){
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
            catch (Exception except){
                log.debug("LL ne répond pas, on shutdown");
                shutdown = true;
                except.printStackTrace();
            }
        }
    }

    /** Getters & Setters */
    ConcurrentLinkedQueue<String> getEventBuffer() {return eventBuffer;}
    ConcurrentLinkedQueue<String> getUltrasoundBuffer() {return ultrasoundBuffer;}
    ConcurrentLinkedQueue<String> getStandardBuffer() {return standardBuffer;}
}
