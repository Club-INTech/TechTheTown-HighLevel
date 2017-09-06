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
    private static boolean shutdown = false;

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
    public void createInterface() throws IOException{
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
            log.debug("Null dans le buffer");
            return "";
        } else {
            standardBuffer.poll();
        }
        return response;
    }

    /**
     * Ping le LL !
     * @return 0 si le LL n'est pas en PLS
     */
    public synchronized String ping()
    {
        String response;
        try {
            output.flush();
            output.write("?\r", 0, 2);
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Fonction pour envoyer un message au LL
     * @return LL response
     */
    public synchronized String[] communicate(String[] message, int nb_line_response)
    {
        standardBuffer.clear();
        String inputLines[] = new String[nb_line_response];

        /* Envoie de l'ordre */
        try
        {
            for (String m : message)
            {
                m += "\r";
                output.flush();
                output.write(m, 0, m.length());

                if(debug)
                {
                    outStandart.write(m);
                    outStandart.newLine();
                    outStandart.flush();
                }

                int nb_tests = 0;
                boolean acquitte = false;

                while (!acquitte)
                {
                    String responseFromCard = waitAndGetResponse();

                    for(int i=0 ; i < responseFromCard.length() ; i++)
                        acquitte = acquitte || (responseFromCard.charAt(i) == '_');

                    if (!acquitte)
                    {
                        log.critical("Non acquittement "+m.replaceAll("\r", "").replaceAll("\n", "")+" : "+responseFromCard);
                        output.write(m, 0, m.length());
                    } else if (debug) {
                        outStandart.write("\t"+responseFromCard);
                        outStandart.newLine();
                        outStandart.flush();
                    }
                    if (++nb_tests > 10)
                    {
                        log.critical("La com " + this.name + " ne répond pas après " + nb_tests + " tentatives (envoyé : '" + m + "', reponse : '" + responseFromCard + "')");
                        break;
                    }
                }
            }
        }
        catch (Exception e)
        {
            log.critical("Ne peut pas parler a la carte " + this.name + " lancement de "+e);
        }

        /* Réponse du LL (listener dans le run) */
        try{
            for (int i = 0 ; i < nb_line_response; i++)
            {
                inputLines[i] = waitAndGetResponse();
                if(inputLines[i]==null || inputLines[i].replaceAll(" ", "").equals("")|| inputLines[i].replaceAll(" ", "").equals("-"))
                {
                    log.critical("Reception de "+inputLines[i]+" , en réponse à " + message[0].replaceAll("\r", "").replaceAll("\n", "") + " envoi du message a nouveau");
                    if(debug)
                    {
                        outStandart.newLine();
                        outStandart.newLine();
                        outStandart.write("Reception de "+inputLines[i]+" , en réponse à " + message[0].replaceAll("\r", "").replaceAll("\n", "") + " envoi du message a nouveau");
                        outStandart.newLine();
                        outStandart.newLine();
                        outStandart.flush();
                    }
                    communicate(message, nb_line_response);
                }

                if(!isAsciiExtended(inputLines[i]))
                {
                    log.critical("Reception de "+inputLines[i]+" (non Ascii) , en réponse à "+ message[0].replaceAll("\r", "").replaceAll("\n", "") + " envoi du message a nouveau");
                    communicate(message, nb_line_response); // On retente
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
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
            catch (IOException e)
            {
                e.printStackTrace();
                log.critical("ThreadEth is shutdown, no communication until restart.");
                return;
            }
        }
    }
}
