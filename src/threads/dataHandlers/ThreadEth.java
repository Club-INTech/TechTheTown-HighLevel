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
import threads.ThreadTimer;
import utils.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Classe implémentant une communication via Ethernet pour communiquer avec le Bas Niveau,
 *
 * @author rem
 */
public class ThreadEth extends AbstractThread implements Service {

    /** Nom */

    public String name;

    /**
     * Flux d'entrée du port
     */
    private BufferedReader input;

    /**
     * Flux de sortie du port
     */
    private BufferedWriter output;

    /**
     * Socket
     */
    private Socket socket;

    /**
     * IP Teensy & local
     */
    private String teensyAdress = "192.168.0.1";
    private String localAdress = "127.0.0.1";

    /**
     * Timeout pour l'envoie de message
     */
    private static final int TIMEOUT = 1000;

    /**
     * True si besoin de fichiers de debug
     */
    private boolean debug = true;
    private volatile boolean comFlag;
    private long timeRef;

    /**
     * Emplacement des fichiers
     */
    private File standardFileTmp;
    private File standardFile;
    private File ordersFileTmp;
    private File ordersFile;
    private File debugFileTmp;
    private File debugFile;
    private File acknowledgeFileTmp;
    private File acknowledgeFile;
    private File positionFileTmp;
    private File positionFile;
    private File eventFileTmp;
    private File eventFile;
    private File fullDebugFileTmp;
    private File fullDebugFile;
    private File sensorUSFileTmp;
    private File sensorUSFile;
    private File logFileTmp;
    private File logFile;

    /**
     * Buffer pour fichiers de debug
     */
    private BufferedWriter outStandard;
    private BufferedWriter outOrders;
    private BufferedWriter outDebug;
    private BufferedWriter outAcknowledge;
    private BufferedWriter outPosition;
    private BufferedWriter outEvent;
    private BufferedWriter fullDebug;
    private BufferedWriter outSensor;

    /**
     * True pour couper la connexion (pas trouvé d'autres idées, mais la lib java.net a probablement un truc propre à proposer)
     */
    public static boolean shutdown = false;

    /**
     * True si utilisation du simulateur LL
     */
    private static boolean simulation;

    /**
     * Singeproof de la connexion : l'interface ne doit etre initialisée qu'une fois
     */
    private boolean interfaceCreated = false;

    /**
     * Buffers représentant les différents canaux
     */
    private volatile ConcurrentLinkedQueue<String> standardBuffer = new ConcurrentLinkedQueue<>();
    private volatile ConcurrentLinkedQueue<String> eventBuffer = new ConcurrentLinkedQueue<>();
    private volatile ConcurrentLinkedQueue<String> ultrasoundBuffer = new ConcurrentLinkedQueue<>();
    private volatile ConcurrentLinkedQueue<String> debugBuffer = new ConcurrentLinkedQueue<>();

    /**
     * Le "canal" position & orientation
     */
    private volatile XYO positionAndOrientation = new XYO(Table.entryPosition, Table.entryOrientation);
    private String splitString = " ";

    /**
     * Nombre de fois qu'on a renvoyé le message
     */

    private int nbRepeatMessage;

    private boolean symmetry=config.getBoolean(ConfigInfoRobot.COULEUR);

    /**
     * Créer l'interface Ethernet en pouvant choisir ou non de simuler le LL
     *
     * @param log
     */
    private ThreadEth(Log log, Config config) {
        super(config, log);
        updateConfig();
        this.positionAndOrientation = new XYO(Table.entryPosition,Table.entryOrientation);
        this.name = "Teensy";
        this.nbRepeatMessage=0;
        if (debug) {
            try {
                this.standardFileTmp = new File("/tmp/standard.txt");
                this.standardFile = new File("./standard.txt");
                this.ordersFileTmp = new File("/tmp/orders.txt");
                this.ordersFile = new File("./orders.txt");
                this.debugFileTmp = new File("/tmp/debugLL.txt");
                this.debugFile = new File("./debugLL.txt");
                this.acknowledgeFileTmp = new File("/tmp/acknowledge.txt");
                this.acknowledgeFile = new File("./acknowldge.txt");
                this.positionFileTmp = new File("/tmp/debugPosition.txt");
                this.positionFile = new File("./debugPosition.txt");
                this.eventFileTmp = new File("/tmp/debugEvent.txt");
                this.eventFile = new File("./debugEvent.txt");
                this.fullDebugFileTmp = new File("/tmp/fullDebug.txt");
                this.fullDebugFile = new File("./fullDebug.txt");
                this.sensorUSFileTmp = new File("/tmp/us.txt");
                this.sensorUSFile = new File("./us.txt");
                this.logFileTmp = new File(log.getSavePath());
                this.logFile = new File(log.getFinalSavePath());

                if (!this.standardFileTmp.exists()){
                    this.standardFileTmp.createNewFile();
                }
                if (!this.ordersFileTmp.exists()) {
                    this.ordersFileTmp.createNewFile();
                }
                if (!this.debugFileTmp.exists()) {
                    this.debugFileTmp.createNewFile();
                }
                if (!this.acknowledgeFileTmp.exists()){
                    this.acknowledgeFileTmp.createNewFile();
                }
                if (!this.positionFileTmp.exists()) {
                    this.positionFileTmp.createNewFile();
                }
                if (!this.eventFileTmp.exists()) {
                    this.eventFileTmp.createNewFile();
                }
                if (!this.fullDebugFileTmp.exists()) {
                    this.fullDebugFileTmp.createNewFile();
                }
                if (!this.sensorUSFileTmp.exists()) {
                    this.sensorUSFileTmp.createNewFile();
                }
                outStandard = new BufferedWriter(new FileWriter(this.standardFileTmp));
                outStandard.newLine();
                outOrders = new BufferedWriter(new FileWriter(this.ordersFileTmp));
                outOrders.newLine();
                outDebug = new BufferedWriter(new FileWriter(this.debugFileTmp));
                outDebug.newLine();
                outAcknowledge = new BufferedWriter(new FileWriter(this.acknowledgeFileTmp));
                outAcknowledge.newLine();
                outPosition = new BufferedWriter(new FileWriter(this.positionFileTmp));
                outPosition.newLine();
                outEvent = new BufferedWriter(new FileWriter(this.eventFileTmp));
                outEvent.newLine();
                fullDebug = new BufferedWriter(new FileWriter(this.fullDebugFileTmp));
                fullDebug.newLine();
                outSensor = new BufferedWriter(new FileWriter(this.sensorUSFileTmp));
                outSensor.newLine();
            } catch (IOException e) {
                log.critical("Manque de droits pour l'output");
                e.printStackTrace();
            }
        } else {
            this.outOrders = null;
            this.outDebug = null;
        }
    }

    /**
     * Initialise la connexion
     *
     * @throws IOException
     */
    private void createSocket() {
        try {
            if (simulation) {
                socket = new Socket(localAdress, 23500);
            } else {
                socket = new Socket(teensyAdress, 23500);
            }
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            interfaceCreated = true;
            log.debug("Socket créée");
        } catch (IOException e) {
            log.critical("On n'a pas réussi à créer la socket");
            e.printStackTrace();
        }
    }

    /**
     * Fonction verifiant si on recoit bien de l'ascii etendu : sinon, bah le bas niveau deconne.
     *
     * @param inputLines
     * @return
     * @throws Exception
     */
    @SuppressWarnings("javadoc")
    public boolean isAsciiExtended(String inputLines) throws Exception {
        for (int i = 0; i < inputLines.length(); i++) {
            if (inputLines.charAt(i) > 128) {
                log.critical(inputLines + " n'est pas ASCII");
                return false;
            }
        }
        return true;
    }

    /**
     * Attend la réponse du LL; si le timeout est dépassé, lancement d'une exception
     */
    private synchronized String waitAndGetResponse() {
        String response = null;
        long startTime = System.currentTimeMillis();

        while ((System.currentTimeMillis() - startTime) < TIMEOUT) {
            try {
                if ((response = standardBuffer.peek()) != null) {
                    break;
                }
                Thread.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (response == null) {
            return "";
        } else {
            standardBuffer.poll();
        }
        return response;
    }

    /**
     * Attend que le LL réponde sur le canal acknowledgement lors d'un envoi d'ordre
     */
    private synchronized void waitForAcknowledgement() throws SocketException {
        int nbTimesHasBeenWaiting=0;
        int maxTimesWaiting=500;
        boolean socketExceptionThrown=false;
        while (comFlag && !socketExceptionThrown){
            try {
                Thread.sleep(1);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            nbTimesHasBeenWaiting+=1;
            if (nbTimesHasBeenWaiting==maxTimesWaiting){
                log.critical("On a attendu trop longtemps pour un event de fin de mouvement (>"+maxTimesWaiting+"ms)...");
                socketExceptionThrown=true;
                throw new SocketException();
            }
        }
    }

    /**
     * On shutdown le ThreadEth
     */
    public void shutdown(){
        shutdown=true;
        try {
            fullDebug.flush();
            fullDebug.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            outStandard.flush();
            outStandard.close();
        } catch (IOException e){
            e.printStackTrace();
        }
        try {
            outEvent.flush();
            outEvent.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            outSensor.flush();
            outSensor.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            outPosition.flush();
            outPosition.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            outDebug.flush();
            outDebug.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            outAcknowledge.flush();
            outAcknowledge.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            outOrders.flush();
            outOrders.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.debug("Fichiers de debug fermés");
        try { Files.copy(fullDebugFileTmp.toPath(), fullDebugFile.toPath(), StandardCopyOption.REPLACE_EXISTING); }
        catch (IOException e) { e.printStackTrace(); }

        try { Files.copy(standardFileTmp.toPath(), standardFile.toPath(), StandardCopyOption.REPLACE_EXISTING); }
        catch (IOException e){ e.printStackTrace(); }

        try { Files.copy(eventFileTmp.toPath(), eventFile.toPath(), StandardCopyOption.REPLACE_EXISTING); }
        catch (IOException e) { e.printStackTrace(); }

        try{ Files.copy(sensorUSFileTmp.toPath(), sensorUSFile.toPath(), StandardCopyOption.REPLACE_EXISTING); }
        catch (IOException e) { e.printStackTrace(); }

        try { Files.copy(positionFileTmp.toPath(), positionFile.toPath(), StandardCopyOption.REPLACE_EXISTING); }
        catch (IOException e) { e.printStackTrace(); }

        try { Files.copy(debugFileTmp.toPath(), debugFile.toPath(), StandardCopyOption.REPLACE_EXISTING); }
        catch (IOException e) { e.printStackTrace(); }

        try { Files.copy(acknowledgeFileTmp.toPath(), acknowledgeFile.toPath(), StandardCopyOption.REPLACE_EXISTING); }
        catch (IOException e) { e.printStackTrace(); }

        try { Files.copy(ordersFileTmp.toPath(), ordersFile.toPath(), StandardCopyOption.REPLACE_EXISTING); }
        catch (IOException e) { e.printStackTrace(); }

        log.debug("Fichiers de debug bien copiés dans le répertoire courant");
        Log.stop();

        try { Files.copy(logFileTmp.toPath(),logFile.toPath(), StandardCopyOption.REPLACE_EXISTING); }
        catch (IOException e) { e.printStackTrace(); }
        closeSocket();
    }

    /**
     * Ferme la socket !
     */
    private void closeSocket() {
        shutdown = true;
        try {
            socket.close();
            System.out.println("La socket a été fermée correctement");
        } catch (IOException e) {
            System.out.println("IOException à la fermeture de la socket");
            e.printStackTrace();
        }
    }


    /**
     * Recrée le socket
     */
    private void recreateSocket() {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                log.critical("IOException : problème pour fermer le socket");
                e.printStackTrace();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.critical("InterruptedException : le thread de recréation de socket a été interrompu");
                e.printStackTrace();
            }
            createSocket();
        }
    }

    /*******************************************
     * FONCTION COMMUNICATION & RUN (LISTENER) *
     *******************************************/

    /**
     * Fonction pour envoyer un message au LL
     *
     * @return LL response
     */
    public synchronized String[] communicate(int nb_line_response, String... message) {
        String mess = "";
        standardBuffer.clear();
        String inputLines[] = new String[nb_line_response];

        for (String m : message) {
            mess += m + " ";
        }

        /* Envoi de l'ordre */
        try {
            timeRef = System.currentTimeMillis();
            comFlag = true;
            mess += "\r\n";
            // On envoie au LL le nombre de caractères qu'il est censé recevoir
            output.write(mess, 0, mess.length());
            output.flush();
        } catch (SocketException e) {
            log.critical("LL ne répond pas, on ferme la socket et on en recrée une...");
            e.printStackTrace();
            recreateSocket();
        } catch (IOException except) {
            log.critical("LL ne répond pas, on shutdown");
            shutdown = true;
            except.printStackTrace();
        }

        if (debug) {
            try {
                outOrders.write(mess);
            } catch (IOException e) {
                log.debug("On n'arrive pas à écrire dans le fichier de debug orders");
                e.printStackTrace();
            }try {
                fullDebug.write(String.format("[%d ms] ", ThreadTimer.getMatchCurrentTime())+mess);
            } catch (IOException e) {
                log.debug("On n'arrive pas à écrire dans le fichier fullDebug");
                e.printStackTrace();
            }
        }

        try {
        /* Réponse du LL (listener dans le run) */
            for (int i = 0; i < nb_line_response; i++) {
                int tries=0;
                inputLines[i] = waitAndGetResponse();

                while (inputLines[i] == null || inputLines[i].replaceAll(" ", "").equals("") && tries < 5) {
                    log.critical("Reception de " + inputLines[i] + " , en réponse à " + message[0].replaceAll("\r", "").replaceAll("\n", "") + " : Attente du LL");
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    tries += 1;
                    inputLines[i] = waitAndGetResponse();
                    if (tries==5) {
                        log.critical("On n'a pas reçu les informations attendues par le LL, on renvoie l'ordre");
                        throw new SocketException();
                    }
                }

                if (debug) {
                    try {
                        outOrders.write("Reception de " + inputLines[i] + " , en réponse à " + message[0].replaceAll("\r", "").replaceAll("\n", "") + " : Attente du LL");
                        outOrders.newLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (nb_line_response != 0) {
                try {
                    outOrders.newLine();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
            waitForAcknowledgement();

        } catch (SocketException e){
            log.critical("LL ne répond pas, on ferme la socket et on en recrée une, et on renvoie le message");
            recreateSocket();
            this.nbRepeatMessage += 1;
            if (this.nbRepeatMessage < 5) {
                inputLines = communicate(nb_line_response, message);
            } else {
                log.critical("On a renvoyé le message plus de 5 fois, y a un gros problème poto, mais dans le doute on continue le match");
            }
        }
        this.nbRepeatMessage = 0;
        return inputLines;
    }


    @Override
    public void run() {
        String buffer;
        Thread.currentThread().setPriority(10);
        createSocket();
        log.debug("ThreadEth started");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> shutdown()));

        while (!shutdown) {
            try {
                buffer = input.readLine();
            } catch (IOException e) {
                log.critical("IOException à la lecture du buffer input");
                buffer="";
                e.printStackTrace();
            }
            try {
                fullDebug.write(String.format("[%d ms] ", ThreadTimer.getMatchCurrentTime())+buffer);
                fullDebug.newLine();
                fullDebug.flush();
            } catch (IOException e) {
                log.critical("IOException pour fullDebug.txt");
                e.printStackTrace();
            }
            if (buffer.length() >= 2 && !(buffer.replaceAll(" ", "").equals(""))) {
                char[] headers = {buffer.toCharArray()[0], buffer.toCharArray()[1]};
                String infosFromBuffer=buffer.substring(2);
                if (CommunicationHeaders.EVENT.getFirstHeader() == headers[0] && CommunicationHeaders.EVENT.getSecondHeader() == headers[1]) {
                    eventBuffer.add(infosFromBuffer);
                    try {
                        outEvent.write(String.format("[%d ms] ", ThreadTimer.getMatchCurrentTime())+infosFromBuffer);
                        outEvent.newLine();
                        outEvent.flush();
                    } catch (IOException e) {
                        log.critical("IOException pour debugEvent.txt");
                        e.printStackTrace();
                    }
                }
                else if (CommunicationHeaders.ULTRASON.getFirstHeader() == headers[0] && CommunicationHeaders.ULTRASON.getSecondHeader() == headers[1]) {
                    ultrasoundBuffer.add(infosFromBuffer);
                    try {
                        outSensor.write(String.format("[%d ms] ", ThreadTimer.getMatchCurrentTime())+infosFromBuffer);
                        outSensor.newLine();
                        outSensor.flush();
                    } catch (IOException e) {
                        log.critical("IOException pour us.txt");
                        e.printStackTrace();
                    }
                }
                else if (CommunicationHeaders.POSITION.getFirstHeader() == headers[0] && CommunicationHeaders.POSITION.getSecondHeader() == headers[1]) {
                    synchronized (this.positionAndOrientation) {
                        positionAndOrientation.update(infosFromBuffer,splitString);
                        if(symmetry){
                            positionAndOrientation.getPosition().setX(-positionAndOrientation.getPosition().getX());
                            positionAndOrientation.setOrientation(Math.PI-positionAndOrientation.getOrientation());
                        }
                        try {
                            outPosition.write(String.format("[%d ms] ", ThreadTimer.getMatchCurrentTime())+infosFromBuffer);
                            outPosition.newLine();
                            outPosition.flush();
                        }catch (IOException e) {
                            log.critical("IOException pour debugPosition.txt");
                            e.printStackTrace();
                        }
                    }
                }
                else if (CommunicationHeaders.ACKNOWLEDGEMENT.getFirstHeader() == headers[0] && CommunicationHeaders.ACKNOWLEDGEMENT.getSecondHeader() == headers[1]){
                    comFlag=false;
                    try {
                        outAcknowledge.write(String.format("[%d ms] ", ThreadTimer.getMatchCurrentTime())+infosFromBuffer+String.format(" [TimeToTravel : %d ms]", System.currentTimeMillis() - timeRef));
                        outAcknowledge.newLine();
                        outAcknowledge.flush();
                    }
                    catch (IOException e) {
                        log.critical("IOException pour acknowledge.txt");
                        e.printStackTrace();
                    }
                }
                else if (CommunicationHeaders.DEBUG.getFirstHeader() == headers[0] && CommunicationHeaders.DEBUG.getSecondHeader() == headers[1]) {
                    try {
                        outDebug.write(String.format("[%d ms] ", ThreadTimer.getMatchCurrentTime())+infosFromBuffer);
                        outDebug.newLine();
                        outDebug.flush();
                    }
                    catch (IOException e) {
                        log.critical("IOException pour debugLL.txt");
                        e.printStackTrace();
                    }
                }
                else if (CommunicationHeaders.STANDARD.getFirstHeader() == headers[0] && CommunicationHeaders.STANDARD.getFirstHeader() == headers[1]){
                    standardBuffer.add(infosFromBuffer);
                    try {
                        outStandard.write(String.format("[%d ms] ", ThreadTimer.getMatchCurrentTime()) + buffer);
                        outStandard.newLine();
                        outStandard.flush();
                    }
                    catch (IOException e){
                        log.critical("IOException pour standard.txt");
                    }
                }
                else{
                    log.critical("///////// MESSAGE AVEC MAUVAIS HEADER ///////////");
                    log.critical(infosFromBuffer);
                    log.critical("/////// FIN MESSAGE AVEC MAUVAIS HEADER /////////");
                }
            } else if (!(buffer.replaceAll(" ", "").equals(""))) {
                log.critical("/////////// MESSAGE SANS HEADER ///////////");
                log.critical(buffer);
                log.critical("///////// FIN MESSAGE SANS HEADER /////////");
            }
        }
    }

    /**
     * Getters & Setters
     */
    public ConcurrentLinkedQueue<String> getEventBuffer() {
        return eventBuffer;
    }
    public ConcurrentLinkedQueue<String> getUltrasoundBuffer() {
        return ultrasoundBuffer;
    }
    public ConcurrentLinkedQueue<String> getStandardBuffer() {
        return standardBuffer;
    }

    /**
     * On stocke la position et l'orientation ici : les classes qui en ont besoin l'a mettre à jour via le Wrapper
     */
    public XYO getPositionAndOrientation() {
        synchronized (positionAndOrientation) {
            return positionAndOrientation;
        }
    }
    public void setPositionAndOrientation(XYO positionAndOrientation) {
        this.positionAndOrientation = positionAndOrientation;
    }

    /**
     * Utilisé par le container pour temporiser tant que l'interface n'a pas été créée
     */
    public boolean isInterfaceCreated() {
        return interfaceCreated;
    }

    @Override
    public void updateConfig() {
        simulation = config.getBoolean(ConfigInfoRobot.SIMULATION);
    }
}