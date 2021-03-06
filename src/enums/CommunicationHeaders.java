package enums;

/**
 * Enum qui rassemble les headers (plus propre pour le Simulateur & le ThreadEth)
 */
public enum CommunicationHeaders {

    EVENT((char) 0x14, (char) 0x17),
    ULTRASON((char) 0x01, (char) 0x06),
    DEBUG((char) 0x02, (char) 0x20),
    POSITION((char) 0x07, (char) 0x05),
    STANDARD((char) 0x40, (char) 0x40),
    ACKNOWLEDGEMENT((char) 0x06,(char) 0x1A),
    ;

    /** Le seul contenu de cette enum... */
    private char firstHeader;
    private char secondHeader;

    /**
     * Constructeur
     * @param firstHeader
     * @param secHeader
     */
    CommunicationHeaders(char firstHeader, char secHeader){
        this.firstHeader = firstHeader;
        this.secondHeader = secHeader;
    }

    /** Getters */
    public char getFirstHeader() {
        return firstHeader;
    }
    public char getSecondHeader() {
        return secondHeader;
    }

}


