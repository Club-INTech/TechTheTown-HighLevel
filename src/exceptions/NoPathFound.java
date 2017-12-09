package exceptions;

/**
 * Exception levée qu'un noeud est dans un obstacle.
 */

public class NoPathFound extends Exception {

    private boolean nodeInObstacle;
    private boolean noPathFound;

    public NoPathFound(boolean nodeInObstacle, boolean noPathFound) {
        this.nodeInObstacle = nodeInObstacle;
        this.noPathFound = noPathFound;
    }

    public boolean isNodeInObstacle() {
        return nodeInObstacle;
    }

    public boolean isNoPathFound() {
        return noPathFound;
    }

    public void setNodeInObstacle(boolean nodeInObstacle) {
        this.nodeInObstacle = nodeInObstacle;
    }

    public void setNoPathFound(boolean noPathFound) {
        this.noPathFound = noPathFound;
    }
}
