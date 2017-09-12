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
import exceptions.serial.SerialConnexionException;
import exceptions.serial.SerialLookoutException;
import gnu.io.*;
import threads.AbstractThread;
import utils.Log;
import utils.Sleep;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Classe implémentant le concept d'une connexion série.
 * Utilisable pour parler à la carte bas-niveau
 * @author dede, kayou, pf, discord
 *
 * Fonctionne désormais en thread séparant les informations en différents canaux, le canal principal
 * restant dans cette classe
 * @author discord
 *
 */
public class ThreadSerial extends AbstractThread implements SerialPortEventListener, Service
{
    /**
     * Port de la connexion
     */
    SerialPort serialPort;

    /**
     * Sortie de log a utiliser
     */
    Log log;

    /**
     * Nom de la connexion série
     */
    String name;

    String port_name;

    /**
     * Flux d'entrée du port
     */
    private InputStream input;

    /**
     * Flux de sortie du port
     */
    private OutputStream output;

    /** Liste pour stocker les series qui sont connectees au système, afin de trouver la bonne */
    private ArrayList<String> connectedSerial = new ArrayList<String>();

    /** Baudrate de la liaison série */
    public static final int baudrate = 115200;

    /**
     * TIME_OUT d'attente de réception d'un message
     */
    private static final int TIME_OUT = 1000;

    private BufferedWriter out;
    private BufferedWriter outLL;
    private boolean debug = true;
    private BufferedWriter outFull;
    private boolean fulldebugofthedead = true;

    /**
     * Permet de couper la communication, oui c'est dégueulasse
     */
    public static boolean shutdown = false;

    /**
     * DEFINE pour le print du log bas-niveau
     */
    private final boolean printLLDebug = true;

    /**
     * Booléen d'initialisation série
     */
    private static boolean init = false;

    /**
     * Booléen de statut de la série
     */
    private boolean serialReady = false;

    private boolean receivingInProgress = false;


    //=================BUFFERS LinkedList<String>=======================

    private ConcurrentLinkedQueue<String> standardBuffer = new ConcurrentLinkedQueue<>();

    private ConcurrentLinkedQueue<String> eventBuffer = new ConcurrentLinkedQueue<>();

    private ConcurrentLinkedQueue<String> ultrasoundBuffer = new ConcurrentLinkedQueue<>();

//   .
//   .
//   .

    //===========================HEADERS=================================

    public final char[] eventHeader = {0x13, 0x37};

    public final char[] ultrasoundHeader = {0x01, 0x10};

    public final char[] debugHeader = {0x02, 0x20};

    //==================IGNORED ORDERS FOR LOGGING=======================

    private final ArrayList<String> ignoredOrders = new ArrayList<String>(){{
        //add("f");
        //add("?xy0");
    }};

    //===================================================================


    /**
     * Construit une connexion série
     * @param log Sortie de log a utiliser
     */
    private ThreadSerial(Log log) throws SerialLookoutException
    {
        super();
        this.log = log;
        this.name = "STM32";
        if(this.debug)
        {
            try
            {
                File file = new File("orders.txt");
                File fileDebug = new File("debugLL.txt");
                File fileFull = new File("debugfull.txt");
                if (!file.exists())
                {
                    //file.delete();
                    file.createNewFile();
                }
                if(!file.exists())
                {
                    file.createNewFile();
                }
                out = new BufferedWriter(new FileWriter(file));
                outLL = new BufferedWriter(new FileWriter(fileDebug));
                outFull = new BufferedWriter(new FileWriter(fileFull));

            } catch (IOException e) {
                log.critical("Manque de droits pour l'output des ordres");
                //out = null;
                e.printStackTrace();
            }
        }
        else
            this.out = null;

        if(!init)
        {
            init = true;
            checkSerial();
            createSerial();
        }
    }

    /**
     * Appelé par le constructeur, il donne a la série tout ce qu'il faut pour fonctionner
     * @param port_name : Le port où est connectée la carte (/dev/ttyUSB ou /dev/ttyACM)
     * @param baudrate : Le baudrate que la carte utilise
     */
    public void initialize(String port_name, int baudrate)
    {
        CommPortIdentifier portId = null;
        try
        {
            portId = CommPortIdentifier.getPortIdentifier(port_name);
        }
        catch (NoSuchPortException e2)
        {
            log.critical("Catch de "+e2+" dans initialize");
        }

        try
        {
            serialPort = (SerialPort) portId.open(this.getClass().getName(), TIME_OUT);
        }
        catch (PortInUseException e1)
        {
            log.critical("Catch de "+e1+" dans initialize");
        }
        try
        {
            serialPort.setSerialPortParams(baudrate,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
            serialPort.notifyOnDataAvailable(false);
            serialPort.enableReceiveTimeout(TIME_OUT);

            input = serialPort.getInputStream();
            output = serialPort.getOutputStream();

        }
        catch (Exception e)
        {
            log.critical("Catch de "+e+" dans initialize");
        }

        this.port_name = port_name;
    }

    /**
     * Regarde toutes les series qui sont branchees (sur /dev/ttyUSB* et /dev/ttyACM*)
     */
    public  void checkSerial()
    {
        Enumeration<?> ports = CommPortIdentifier.getPortIdentifiers();
        while (ports.hasMoreElements())
        {
            CommPortIdentifier port = (CommPortIdentifier) ports.nextElement();
            this.connectedSerial.add(port.getName());
        }
    }

    /**
     * Création de la serie (il faut au prealable faire un checkSerial()).
     *
     * Instancie un thread pour chaque série, vérifie que tout fonctionne (ou non) et valide
     * le tout une fois la bonne trouvée
     *
     * @throws SerialLookoutException
     */
    public void createSerial() throws SerialLookoutException
    {
        int id;

        for (String connectedSerial : this.connectedSerial)
        {
            if(connectedSerial.contains("ACM"))
                continue;

            ThreadSerial ser = new ThreadSerial(log);
            ser.initialize(connectedSerial, baudrate);

            if (ser.ping() != null)
                id = Integer.parseInt(ser.ping());
            else {
                ser.close();
                continue;
            }

            if (id != 0) {
                ser.close();
                continue;
            }

            ser.close();
            System.out.println("Carte sur: " + connectedSerial);

            this.initialize(connectedSerial, baudrate);

            if(this.isAlive())  //S'il est vivant, on le tue
            {
                ThreadSerial.shutdown = true;

                try
                {
                    this.join();
                }
                catch (InterruptedException e) {}

                ThreadSerial.shutdown = false;
            }

            this.start();
            return;
        }

        log.critical("La carte STM32 n'est pas détectée");
        throw new SerialLookoutException();
    }

    /**
     * Méthode pour communiquer a la liaison série. Il ne faut absolument pas se tromper sur le nombre de lignes attendu en retour.
     * (une ligne est délimitée par un "\r\n" sur une communication série par le canal principal.
     * Elle peut etre envoyée par le bas niveau dans un:
     * printf("\r\n") ou un printfln("...") où ici le ln veut dire retour a la ligne donc se charge de mettre
     * "\r\n" é la fin du message pour l'utilisateur).
     * @param message Message a envoyer
     * @param nb_lignes_reponse Nombre de lignes que le bas niveau va répondre (sans compter les acquittements)
     * @return Un tableau contenant le message
     * @throws SerialConnexionException
     */
    public String[] communiquer(String message, int nb_lignes_reponse) throws SerialConnexionException
    {
        String[] messages = {message};
        return communiquer(messages, nb_lignes_reponse);
    }

    /**
     * Méthode pour communiquer a la liaison série. Il ne faut absolument pas se tromper sur le nombre de lignes attendu en retour.
     * (une ligne est délimitée par un "\r\n" sur une communication série par le canal principal.
     * Elle peut etre envoyée par le bas niveau dans un:
     * printf("\r\n") ou un printfln("...") où ici le ln veut dire retour a la ligne donc se charge de mettre
     * "\r\n" é la fin du message pour l'utilisateur).
     * @param messages Messages a envoyer
     * @param nb_lignes_reponse Nombre de lignes que le bas niveau va répondre (sans compter les acquittements)
     * @return Un tableau contenant le message
     * @throws SerialConnexionException
     */
    public synchronized String[] communiquer(String[] messages, int nb_lignes_reponse) throws SerialConnexionException
    {
        if(shutdown)
            throw new SerialConnexionException();

        if(!serialReady) log.warning("Order " + messages[0] + " in waiting due to not-ready serial.");

        while(!serialReady)
        {
            Sleep.sleep(5);
        }

        synchronized(serialPort)
        {
            standardBuffer.clear();
            String inputLines[] = new String[nb_lignes_reponse];
            boolean ignoredOrderForLogging = !this.debug || this.ignoredOrders.contains(messages[0]);
            try
            {
                for (String m : messages)
                {
                    //log.debug("Envoi serie : '" + m  + "'");
                    m += "\r";

                    output.flush();
                    output.write(m.getBytes());

                    if(!ignoredOrderForLogging)
                    {
                        out.write(m);
                        out.newLine();
                        out.flush();
                    }
                    int nb_tests = 0;
                    boolean acquitte = false;

                    while (!acquitte)
                    {
                        String responseFromCard = waitAndGetResponse();

                        //TODO commenter.
                        //log.debug("Reception acquitement : '" + responseFromCard  + "'");

                        for(int i=0 ; i < responseFromCard.length() ; i++)
                            acquitte = acquitte || (responseFromCard.charAt(i) == '_');

                        // acquittement = responseFromCard.charAt(responseFromCard.length()-1);
                        if (!acquitte)
                        {
                            log.critical("NON ACQUITEMENT SUR "+m.replaceAll("\r", "").replaceAll("\n", "")+" : "+responseFromCard);
                            output.write(m.getBytes());
                        } else if (debug) {
                            out.write("\t"+responseFromCard);
                            out.newLine();
                            out.flush();
                        }
                        if (++nb_tests > 10)
                        {
                            log.critical("La série " + this.name + " ne répond pas après " + nb_tests + " tentatives (envoyé : '" + m + "', reponse : '" + responseFromCard + "')");
                            break;
                        }
                    }
                }
            }
            catch (Exception e)
            {
                log.critical("Ne peut pas parler a la carte " + this.name + " lancement de "+e);
                communiquer(messages, nb_lignes_reponse);
            }

            try
            {
                for (int i = 0 ; i < nb_lignes_reponse; i++)
                {
                    inputLines[i] = waitAndGetResponse();

                    //TODO commenter.
                    //log.debug("Ligne "+i+": '"+inputLines[i]+"'");
                    if(inputLines[i]==null || inputLines[i].replaceAll(" ", "").equals("")|| inputLines[i].replaceAll(" ", "").equals("-"))
                    {
                        log.critical("Reception de "+inputLines[i]+" , en réponse à " + messages[0].replaceAll("\r", "").replaceAll("\n", "") + " envoi du message a nouveau");
                        if(fulldebugofthedead)
                        {
                            outFull.newLine();
                            outFull.newLine();
                            outFull.write("Reception de "+inputLines[i]+" , en réponse à " + messages[0].replaceAll("\r", "").replaceAll("\n", "") + " envoi du message a nouveau");
                            outFull.newLine();
                            outFull.newLine();
                            outFull.flush();
                        }
                        if(debug)
                        {
                            out.newLine();
                            out.newLine();
                            out.write("Reception de "+inputLines[i]+" , en réponse à " + messages[0].replaceAll("\r", "").replaceAll("\n", "") + " envoi du message a nouveau");
                            out.newLine();
                            out.newLine();
                            out.flush();
                        }
                        communiquer(messages, nb_lignes_reponse);
                    }

                    if(!isAsciiExtended(inputLines[i]))
                    {
                        log.critical("Reception de "+inputLines[i]+" (non Ascii) , en réponse à "+ messages[0].replaceAll("\r", "").replaceAll("\n", "") + " envoi du message a nouveau");
                        communiquer(messages, nb_lignes_reponse); // On retente
                    }

                    if(!ignoredOrderForLogging)
                    {
                        out.write("\t"+inputLines[i]);
                        out.newLine();
                        out.flush();
                    }
                }
            }
            catch (Exception e)
            {
                log.critical("Ne peut pas parler a la carte " + this.name + " lancement de "+e);
                communiquer(messages, nb_lignes_reponse);
            }
            return inputLines;
        }
    }

    /**
     * Idem que communiquer mais place un header devant chaque ligne du message
     * Je recommande d'utiliser les defines dans cette classe pour les headers
     * @param messages le message à envoyer SANS HEADER
     * @param nb_lignes_reponse nb de lignes attendues en réponse du LL
     * @param header le header à ajouter
     * @return la réponse du LL
     * @throws SerialConnexionException
     */
    public String[] communiquerAvecHeader(String[] messages, int nb_lignes_reponse, char[] header) throws SerialConnexionException
    {
        for(int i=0 ; i < messages.length ; i++)
        {
            messages[i] = String.valueOf(header) + messages[i];
        }

        return communiquer(messages, nb_lignes_reponse);
    }

    /**
     * Idem que communiquer mais place un header devant le message
     * Je recommande d'utiliser les defines dans cette classe pour les headers
     * @param message le message à envoyer SANS HEADER
     * @param nb_lignes_reponse nb de lignes attendues en réponse du LL
     * @param header le header à ajouter
     * @return la réponse du LL
     * @throws SerialConnexionException
     */
    public String[] communiquerAvecHeader(String message, int nb_lignes_reponse, char[] header) throws SerialConnexionException
    {
        message = String.valueOf(header) + message;

        return communiquer(message, nb_lignes_reponse);
    }

    /**
     * Doit etre appelé quand on arrete de se servir de la série
     */
    public void close()
    {
        if (serialPort != null)
        {
            log.debug("Fermeture de "+name);
            serialPort.close();
        }
    }

    /**
     * Handle an event on the serial port.
     * NE PAS SUPPRIMER!!!!!! Cette méthode est essentielle au fonctionnement de la communication.
     */
    public synchronized void serialEvent(SerialPortEvent oEvent)
    {
     /*   try {
            if(input.available() > 0)
                notify();
//			else
//				log.debug("Fausse alerte");
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    /**
     * Envoie un String sans chercher d'acquittement ou quoi que ce soit
     * @param message le message
     */
    public synchronized void sendRaw(byte[] message) throws IOException {
        output.write(message);
    }

    /**
     * Ping de la carte.
     * Peut envoyer un message d'erreur lors de l'exécution de createSerial() dans le constructeur.
     *
     * (Avec la carte de test dans createSerial(), on ne sait pas encore si celle-ci va répondre ou non, c'est a dire,
     * si il s'agit bien d'une liaison série, ou alors d'un autre périphérique. Si il s'agit d'un autre périphérique,
     * alors cette méthode va catch une exception)
     * Utilisé que par createSerial
     * @return l'id de la carte
     */
    public synchronized String ping()
    {
        synchronized(output) {
            try
            {
                serialPort.notifyOnDataAvailable(false);

                //Evacuation de l'eventuel buffer indÃƒÂ©sirable
                output.flush();

                byte[] ping = new byte[2];
                ping[0] = (byte)'?';
                ping[1] = (byte)'\r';
                output.write(ping);

                Sleep.sleep(1000);

                while(input.available() != 0)
                {
                    if(input.read() == 48)
                    {
                        return "0";
                    }
                }

            }
            catch (Exception e)
            {
                log.critical("Catch de "+e+" dans ping");
            }
            return null;
        }
    }


    public void updateConfig()
    {
    }

    /**
     * Fonction exécutée par le thread, capture tout ce qui arrive sur la série et trie selon différents canaux
     */
    @Override
    public void run()
    {
        String buffer;
        Thread.currentThread().setPriority(8);
        this.serialReady = true;
        log.debug("ThreadSerial started");
        while(!shutdown)
        {
            try
            {
                if(available())
                {
                 //   receivingInProgress = false;

                    buffer = readLine();
                    // log.debug("readLine : " + buffer);

                //    receivingInProgress = true;

                    if(fulldebugofthedead)
                    {
                        outFull.write(buffer);
                        outFull.newLine();
                        outFull.flush();
                    }

                    if(buffer.length()>=2 && !(buffer.replaceAll(" ", "").equals("")/*|| buffer.replaceAll(" ", "").equals("-")*/))
                    {
                        if (buffer.toCharArray()[0] == eventHeader[0] && buffer.toCharArray()[1] == eventHeader[1]) {
                            eventBuffer.add(buffer);
                            continue;
                        } else if (buffer.toCharArray()[0] == ultrasoundHeader[0] && buffer.toCharArray()[1] == ultrasoundHeader[1]) {
                            ultrasoundBuffer.add(buffer);
                            continue;
                        } else if (buffer.toCharArray()[0] == debugHeader[0] && buffer.toCharArray()[1] == debugHeader[1]) {
                            if (!printLLDebug) continue;
                            outLL.write(buffer.substring(2));
                            outLL.newLine();
                            outLL.flush();

                            // log.debug("Debug LL : "+buffer.substring(2));
                            continue;
                        }
                        else
                        {
                            standardBuffer.add(buffer);
                            continue;
                        }
                    }
                    else if (!(buffer.replaceAll(" ", "").equals("")/*|| buffer.replaceAll(" ", "").equals("-")*/))
                    {
                        standardBuffer.add(buffer);
                        continue;
                    }

                    if(fulldebugofthedead)
                    {
                        outFull.newLine();
                        outFull.newLine();
                        outFull.write("DROPPED : "+buffer.replaceAll("\r", "").replaceAll("\n", ""));
                        outFull.newLine();
                        outFull.newLine();
                        outFull.flush();
                    }
                }

            }
            catch (IOException e)
            {
                e.printStackTrace();
                log.critical("ThreadSerial is shutdown, no serial until restart.");
                this.serialReady = false;
                restartSerial();
                return;
            }
        }

        this.serialReady = false;
        log.critical("ThreadSerial is shutdown, no serial until restart.");
    }

    /**
     * Relance complètement la série, lancé en cas de mauvaise réception
     */
    private void restartSerial()
    {
        log.warning("Restarting serial...");
        try
        {
            this.connectedSerial.clear();
            this.input.close();
            this.output.close();
            this.port_name = "";
            this.serialPort.close();

            checkSerial();
            createSerial();
        }
        catch (SerialLookoutException e)
        {
            e.printStackTrace();

            log.critical("No card found, restarting...");

            restartSerial();
        }
        catch (IOException e)
        {
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
     * Il y a-t-il un octet de disponible ?
     * @throws IOException
     */
    public boolean available() throws IOException
    {
        // tant qu'on est occupé, on dit qu'on ne reçoit rien
       /* if(busy)
            return false;*/
        return input.available() != 0;
    }

    /**
     * Lit un byte. On sait qu'il doit y en a avoir un.
     * @throws IOException
     */
    public int read() throws IOException
    {
        if (input.available() == 0)
            Sleep.sleep(5); // On attend un tout petit peu, au cas où

        if (input.available() == 0)
            throw new IOException(); // visiblement on ne recevra rien de plus

        byte out = (byte) input.read();
        return out & 0xFF;

    }

    /**
     * Lecture complète d'une ligne se terminant par "\r\n"
     * @return la ligne sans le "\r\n"
     */
    private String readLine()
    {
        String res = "";
        try {
            int lastReceived;

            long time = System.currentTimeMillis();
            while (!available())
            {
                if(System.currentTimeMillis() - time > TIME_OUT)
                {
                    log.critical("Il ne daigne même pas répondre !");
                    return (res+(char)260);
                }
                Thread.sleep(0, 500);
            }

            while (available()) {

                if ((lastReceived = read()) == 13)
                    break;

                res += (char) lastReceived;

                time = System.currentTimeMillis();
                while (!available())
                {
                    if(System.currentTimeMillis() - time > TIME_OUT)
                    {
                        log.critical("blocaqe attente nouveau char (pas de /r ?) dernier : "+ lastReceived);
                        return (res+(char)260);
                    }
                    Thread.sleep(0, 500);
                }
            }

            time = System.currentTimeMillis();
            while (!available())
            {
                if(System.currentTimeMillis() - time > TIME_OUT)
                {
                    log.critical("bloquage attente newChar (normalement newLine)");
                    return (res+(char)260);
                }
                Thread.sleep(0, 500);
            }

            while(available()) {

                if (read() == 10)
                    break;
                time = System.currentTimeMillis();
                while (!available())
                {
                    if(System.currentTimeMillis() - time > TIME_OUT)
                    {
                        log.critical("Bloquage attente newLine");
                        return (res+(char)260);
                    }
                    Thread.sleep(0, 500);
                }
            }

        } catch (IOException e) {
            log.debug("On a perdu la série !!");
            while (ping() == null) {
                Sleep.sleep(100);
            }
            res+=(char)260;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return res;

    }

    /**
     * On attend la disponibilité d'une réponse traité par la partie thread (pour l'ancien système)
     * @return la réponse
     */
    private synchronized String waitAndGetResponse()
    {
        String res;

        long startTime = System.currentTimeMillis();

        res = null;
        while((System.currentTimeMillis() - startTime) < 2*TIME_OUT)
        {
            try
            {
                if((res = standardBuffer.peek()) != null) break;
                Thread.sleep(2);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

        if(res == null) {
            log.debug("Null dans le buffer");
            return "";
        } else {
            standardBuffer.poll();
        }

        return res;
    }

    ConcurrentLinkedQueue<String> getEventBuffer() {return eventBuffer;}
    ConcurrentLinkedQueue<String> getUltrasoundBuffer() {return ultrasoundBuffer;}
}