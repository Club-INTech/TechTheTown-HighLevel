package strategie;

import scripts.AbstractScript;

public class Pair {
    /** Script */
    private AbstractScript script;

    /** Version à executer */
    private Integer version;

    /** Constructor */
    public Pair(AbstractScript script, Integer version) {
        this.script = script;
        this.version = version;
    }

    /** Getters & Setters */
    public AbstractScript getScript() {
        return script;
    }
    public Integer getVersion() {
        return version;
    }
}
