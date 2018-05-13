package pathfinder;

import smartMath.Vect;

import java.util.ArrayList;

public class Noeud {
    private Vect position;
    private double heuristique;
    private double cout;
    private ArrayList<Noeud> voisins;
    private Noeud pred;

    /** Constructeur*/
    public Noeud(Vect position, int heuristique, int cout, ArrayList<Noeud> voisins) {
        this.position = position;
        this.heuristique = heuristique;
        this.cout = cout;
        this.voisins = voisins;
    }


    /** Test utiliser pour trouver si un noeud est plus interessant qu'un autre */

    @Override
    public boolean equals(Object object){
        if(object instanceof Noeud){
            if(this.position.equals(((Noeud) object).position) ){
                return true;
            }
        }
        return false;
    }

    /**
     * Cette méthode supprime un voisin de la liste des voisins, elle est appelée
     * par le pathfinding
     * @param noeud
     */
    public void removeNeighbour(Noeud noeud){ voisins.remove(noeud);}




    /**
     * Cette méthode est appelée par createAretes de Graphe afin de set les voisins
     * de chaque noeud : un voisin est un noeud qu'on pourrait atteindre, c'est à dire
     * une arete pourrait etre tracée facilement sans qu'elle passe par un obstacle
     * circulaire ou rectangulaire
     * @param voisin
     */
    public void addVoisin(Noeud voisin){
        if (!this.voisins.contains(voisin)) {
            this.voisins.add(voisin);
        }
    }

    /**
     * Cette méthode ajoute à un noeud un voisin, elle est appelée par le addNodeInGraphe
     * qui est appelée par le pathfinding
     * @param voisins
     */
    public void addVoisins(ArrayList<Noeud> voisins){
        for (Noeud voisin : voisins) {
            if (!this.voisins.contains(voisin)) {
                this.voisins.add(voisin);
            }
        }
    }

    @Override
    public String toString(){
        String toReturn = "Node ("+this.position.getX()+","+this.position.getY()+")";
        return toReturn;
    }

    public void setCout(double cout) {      this.cout = cout;    }

    public void setPred(Noeud pred) {        this.pred = pred;   }


    public Vect getPosition() {  return position;   }

    public double getHeuristique(){  return heuristique;}

    public ArrayList<Noeud> getVoisins() { return voisins;  }

    public double getCout() { return cout; }

    public Noeud getPred() {   return pred;    }

    public void setPosition(Vect position) { this.position = position;  }

    public void setHeuristique(double heuristique) { this.heuristique = heuristique;  }



}
