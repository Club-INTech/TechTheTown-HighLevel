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

package pathfinder;

import smartMath.Circle;
import smartMath.Geometry;
import smartMath.Segment;
import smartMath.Vec2;
import table.Table;
import table.obstacles.ObstacleCircular;
import table.obstacles.ObstacleManager;
import table.obstacles.ObstacleRectangular;
import utils.Config;
import utils.Log;

import java.util.ArrayList;


public class Graphe {
    private  Table table;
    private Config config;
    private  Log log;

    private ArrayList<Noeud> lNoeuds;
    private int n=6;
    private int ecart=20;

    public int getN() {
        return n;
    }

    public Table getTable() {
        return table;
    }

    public int getEcart() {
        return ecart;
    }


    public ArrayList<Noeud> getlNoeuds() {
        return lNoeuds;
    }

    /**
     * Constructeur standard du graphe
     * @param log log
     * @param config config
     * @param table table
     */
    public Graphe(Log log, Config config, Table table)
{
    this.log=log;
    this.config=config;
    this.table=table;

    this.lNoeuds=new ArrayList<Noeud>();
    initGraphe();
}

    /**
     * Construit le graphe initial de la table, en reliant tous les noeuds et supprimant ceux bloqués par des obstacles
     */
    public void initGraphe() //le graphe initial V1
    {
        //on fabrique les noeuds. On les relie TOUS. On supprime ceux bloqués. C'est sale, mais ça fait un graphe bien fourni

        ObstacleManager oManager = this.table.getObstacleManager();

        //Pour sortir
        for (ObstacleCircular x:oManager.getmCircularObstacle())
        {
            x.fabriqueNoeud(this,this.n,this.ecart);
        }
        for (ObstacleRectangular x:oManager.getRectangles())
        {
            x.fabriqueNoeud(this, this.ecart);
        }
        int j=0;

        for (Noeud noeud1: this.getlNoeuds())
        {
            for (int i=0;i<this.getlNoeuds().size();i++)
            {
                if(noeud1 != this.getlNoeuds().get(i))
                {
                    if (noeud1==null ||this.getlNoeuds().get(i)==null)
                    {
                        log.debug("null except");
                    }
                    else {
                        boolean creer=true;
                        j=0;
                        int nombobst= oManager.getmCircularObstacle().size();
                        int nombobstRec=oManager.getRectangles().size();

                        ArrayList <Segment> lineObstacles = oManager.getLines();
                        //si on est en dehors du graphe
                        if(Math.abs(noeud1.position.getX())>1500-oManager.mRobotRadius || noeud1.position.getY()<oManager.mRobotRadius || noeud1.position.getY()>2000-oManager.mRobotRadius)
                        {
                            creer=false;
                        }

                        //On vérifie l'intersection avec les lignes
                        for(int k=0 ; k<lineObstacles.size() ; k++)
                        {
                            if(Geometry.intersects(new Segment(noeud1.position, this.getlNoeuds().get(i).position), lineObstacles.get(k)))
                            {
                                creer = false;
                                break;
                            }
                        }

                        while (creer && j<nombobst)  {
                            if(oManager.getmCircularObstacle().get(j).isInObstacle(noeud1.position)||oManager.getmCircularObstacle().get(j).isInObstacle(this.getlNoeuds().get(i).position))
                            {
                              creer=false;
                            }
                            else
                            {
                                creer = creer && !(Geometry.intersects(new Segment(noeud1.position, this.getlNoeuds().get(i).position), new Circle(oManager.getmCircularObstacle().get(j).getPosition(), oManager.getmCircularObstacle().get(j).getRadius())));
                            }
                            j++;
                        }
                        j=0;
                        while (creer && j<nombobstRec) {
                            if (oManager.getRectangles().get(j).isInObstacle(noeud1.position) || oManager.getRectangles().get(j).isInObstacle(this.getlNoeuds().get(i).position))
                            {
                                creer=false;
                            }
                            else {

                                creer = creer && !Geometry.intersects(new Segment(noeud1.position, this.getlNoeuds().get(i).position), new Segment(oManager.getRectangles().get(j).getlNoeud().get(0).position, oManager.getRectangles().get(j).getlNoeud().get(1).position));
                                creer = creer && !Geometry.intersects(new Segment(noeud1.position, this.getlNoeuds().get(i).position), new Segment(oManager.getRectangles().get(j).getlNoeud().get(1).position, oManager.getRectangles().get(j).getlNoeud().get(3).position));
                                creer = creer && !Geometry.intersects(new Segment(noeud1.position, this.getlNoeuds().get(i).position), new Segment(oManager.getRectangles().get(j).getlNoeud().get(0).position, oManager.getRectangles().get(j).getlNoeud().get(2).position));
                                creer = creer && !Geometry.intersects(new Segment(noeud1.position, this.getlNoeuds().get(i).position), new Segment(oManager.getRectangles().get(j).getlNoeud().get(2).position, oManager.getRectangles().get(j).getlNoeud().get(3).position));
                                creer = creer && !Geometry.intersects(new Segment(noeud1.position, this.getlNoeuds().get(i).position), new Segment(oManager.getRectangles().get(j).getlNoeud().get(1).position, oManager.getRectangles().get(j).getlNoeud().get(2).position));
                                creer = creer && !Geometry.intersects(new Segment(noeud1.position, this.getlNoeuds().get(i).position), new Segment(oManager.getRectangles().get(j).getlNoeud().get(0).position, oManager.getRectangles().get(j).getlNoeud().get(3).position));

                                j++;
                            }
                        }
                        if(creer)
                        {noeud1.attacheLien(this.getlNoeuds().get(i));}
                    }
                }
            }
        }

        for (int i = 0; i < this.lNoeuds.size(); i++) {
            if(this.lNoeuds.get(i).lArretes.size()==0)
            {
                this.lNoeuds.remove(this.lNoeuds.get(i));
                i--;
            }
        }
    }
    /**
     * Renvoie la position du noeud du graphe le plus proche du vecteur en argument
     * @param vec
     */
    public Vec2 noeudProche(Vec2 vec){

        double min=1000;
        Vec2 minVec = new Vec2();

        for (Noeud node : lNoeuds){
            if(node.position.distance(vec) < min){
                min = node.position.distance(vec);
                minVec = node.position;
            }
        }
        return minVec;
    }
}
