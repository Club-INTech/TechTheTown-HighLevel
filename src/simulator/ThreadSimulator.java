package simulator;

import container.Service;
import enums.ActuatorOrder;
import enums.CommunicationHeaders;
import pfg.config.Config;
import smartMath.Vec2;
import threads.AbstractThread;
import utils.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Thread qui écoute le HL et renvoie les informations demandées/relaie les ordres de déplacement au ThreadSimulatorMotion
 * @author rem
 */
public class ThreadSimulator extends AbstractThread implements Service {

    /** Nom du Thread */
    public String name;

    /** GameState propre au LL */
    private GameStateSimulator state;

    /** Sockets */
    private ServerSocket server;
    private Socket client;

    /** IO */
    private BufferedReader input;
    private BufferedWriter output;

    /** Pile de comm avec le ThreadSimulatorMotion */
    private ConcurrentLinkedQueue<String> motionOrderBuffer = new ConcurrentLinkedQueue<>();

    /** Buffers pour fichiers de debug */
    private BufferedWriter out;

    /** Identifiant du dernier message recu */
    private char idLastMessage;
    private int eventId;

    /** Shutdown... */
    public static boolean shutdown = false;
    public static boolean ready = false;

    /**
     * Constructeur du Simulateur ! Tout s'instancie automatiquement à partir du moment où l'on instancie un ThreadSimulatorMotion
     * @param state
     * @param config
     * @param log
     */
    public ThreadSimulator(Config config, Log log, GameStateSimulator state){
        super(config, log);
        this.name = "simulator";
        this.state = state;
        this.idLastMessage = 'A';
        this.eventId = 1000;
    }

    /**
     * Créer l'Interface Ethernet (socket & IO)
     */
    private void createInterface(){
        try {
            server = new ServerSocket(23500);
            server.setReuseAddress(true);
            ready = true;
            client = server.accept();
            input = new BufferedReader(new InputStreamReader(client.getInputStream()));
            output = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));

        }catch(IOException e){
            log.debug("IO Exception : manque de droits pour IO");
            e.printStackTrace();
        }
    }

    public void shutdown() {
        try {
            server.close();
            shutdown = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Fonction pour communiquer avec le HL
     * @param header
     * @param messages
     */
    public void communicate(CommunicationHeaders header, String... messages){
        try {
            for (String mess : messages) {
                if(header != null){
                    output.write(header.getFirstHeader());
                    output.write(header.getSecondHeader());
                    if(header == CommunicationHeaders.EVENT) {
                        output.write(String.format("%d",eventId));
                        eventId++;
                    }
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

        communicate(CommunicationHeaders.ACKNOWLEDGEMENT, Character.toString(idLastMessage));
        request = request.substring(1);
        communicate(CommunicationHeaders.DEBUG, "Message recu : " + request);
        messages = request.split(" ");
        head = messages[0];

        /** INFORMATIONS */
        if (head.equals(ActuatorOrder.IS_ROBOT_MOVING.getEthernetOrder())) {
            communicate(CommunicationHeaders.STANDARD, String.format("%s", state.isRobotMoving()), String.format("%s", state.isMoveNormal()));
        }
        else if (head.equals(ActuatorOrder.SEND_POSITION.getEthernetOrder())) {
            communicate(CommunicationHeaders.STANDARD, String.format("%s",state.getPosition().getX()),
                    String.format("%s",state.getPosition().getY()),
                    String.format("%s", state.getOrientation()));
        }

        /** SETTINGS */
        else if (head.equals(ActuatorOrder.SET_X.getEthernetOrder())) {
            Vec2 newPos = new Vec2((int) Float.parseFloat(messages[1]), state.getPosition().getY());
            state.setPosition(newPos);
        }
        else if (head.equals(ActuatorOrder.SET_Y.getEthernetOrder())) {
            Vec2 newPos = new Vec2(state.getPosition().getX(), (int) Float.parseFloat(messages[1]));
            state.setPosition(newPos);
        }
        else if (head.equals(ActuatorOrder.SET_ORIENTATION.getEthernetOrder())) {
            state.setOrientation(Float.parseFloat(messages[1]));
        }
        else if (head.equals(ActuatorOrder.SET_POSITION.getEthernetOrder())){
            state.setPosition(new Vec2((int) Float.parseFloat(messages[1]), (int) Float.parseFloat(messages[2])));
            state.setOrientation(Float.parseFloat(messages[3]));
        }
        else if (head.equals(ActuatorOrder.SET_TRANSLATION_SPEED.getEthernetOrder())) {
            state.setTranslationSpeed((int) Float.parseFloat(messages[1]));
        }
        else if (head.equals(ActuatorOrder.SET_ROTATIONNAL_SPEED.getEthernetOrder())) {
            state.setRotationnalSpeed(Float.parseFloat(messages[1]));
        }
        //TODO Système des hooks
        else if (head.equals(ActuatorOrder.INITIALISE_HOOK.getEthernetOrder())) {

        }
        else if (head.equals(ActuatorOrder.ENABLE_HOOK.getEthernetOrder())) {

        }
        else if (head.equals(ActuatorOrder.DISABLE_HOOK.getEthernetOrder())) {

        }

        /** MOTION ORDERS */
        else if (head.equals(ActuatorOrder.MOVE_LENTGHWISE.getEthernetOrder()) ||
                head.equals(ActuatorOrder.TURN.getEthernetOrder()) ||
                head.equals(ActuatorOrder.TURN_RIGHT_ONLY.getEthernetOrder()) ||
                head.equals(ActuatorOrder.TURN_LEFT_ONLY.getEthernetOrder())) {

            state.setMustStop(false);
            motionOrderBuffer.add(request);
        }
        else if (head.equals(ActuatorOrder.STOP.getEthernetOrder())) {
            state.setMustStop(true);
        }
        else {
            //TODO condition à compléter
            communicate(CommunicationHeaders.DEBUG, "Mode Simu : balec' frère...");
        }

        this.idLastMessage+=1;
        if (idLastMessage>'Z'){
            idLastMessage='A';
        }
    }

    /** Getters & Setters */
    public ConcurrentLinkedQueue<String> getMotionOrderBuffer() {
        return motionOrderBuffer;
    }

    @Override
    public void run(){
        String buffer;
        createInterface();
        log.debug("ThreadSimulator started");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> shutdown()));

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
