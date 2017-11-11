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

import container.Service;
import smartMath.Vec2;
import table.obstacles.ObstacleCircular;
import table.obstacles.ObstacleManager;



import java.util.ArrayList;

/**
 * Pathfinding du robot ! Contient l'algorithme
 * @author Yousra, Sam
 */
public class Pathfinding implements Service {
    private ObstacleManager obstacleManager;
    private ArrayList<ObstacleCircular> listCircu;
    private ArrayList<Vec2> tabposition;
    private ArrayList<Noeud> nodes;
    private double a1;
    private double b1;
    private double a2;
    private double b2;
    private int centreCercleX;
    private int centreCercleY;


    private Pathfinding() {
    }

    @Override
    public void updateConfig() {


    }

    public void createnoeudTable() {
        listCircu = obstacleManager.getmCircularObstacle();
        tabposition.get(0).setX(listCircu.get(0).getPosition().getX());
        tabposition.get(0).setY(listCircu.get(0).getPosition().getY() + listCircu.get(0).getRadius() + 264);
        nodes.get(0).position = tabposition.get(0);
        tabposition.get(1).setX(listCircu.get(1).getPosition().getX() + listCircu.get(1).getRadius() + 264);
        tabposition.get(1).setY(listCircu.get(1).getPosition().getY());
        nodes.get(1).position = tabposition.get(1);
        tabposition.get(2).setX(listCircu.get(1).getPosition().getX() + listCircu.get(1).getRadius() - 264);
        tabposition.get(2).setY(listCircu.get(1).getPosition().getY());
        nodes.get(2).position = tabposition.get(2);
        tabposition.get(3).setX(listCircu.get(1).getPosition().getX());
        tabposition.get(3).setY(listCircu.get(1).getPosition().getY() + listCircu.get(1).getRadius() - 264);
        nodes.get(3).position = tabposition.get(3);
        tabposition.get(4).setX(listCircu.get(2).getPosition().getX() + listCircu.get(2).getRadius() - 264);
        tabposition.get(4).setY(listCircu.get(2).getPosition().getY());
        nodes.get(4).position = tabposition.get(4);
        tabposition.get(5).setX(listCircu.get(2).getPosition().getX());
        tabposition.get(5).setY(listCircu.get(2).getPosition().getY() + listCircu.get(2).getRadius() - 264);
        nodes.get(5).position = tabposition.get(5);
        tabposition.get(6).setX(listCircu.get(3).getPosition().getX());
        tabposition.get(6).setY(alineate(nodes.get(4).position.getX(), nodes.get(4).position.getY(), listCircu.get(3).getPosition().getX(), listCircu.get(3).getPosition().getY() + 264).getY());
        nodes.get(6).position = tabposition.get(6);
        tabposition.get(7).setX(listCircu.get(4).getPosition().getX());
        tabposition.get(7).setY(listCircu.get(4).getPosition().getY() + listCircu.get(4).getRadius() + 264);
        nodes.get(7).position = tabposition.get(7);
        tabposition.get(8).setX(listCircu.get(4).getPosition().getX() + listCircu.get(4).getRadius() + 264);
        tabposition.get(8).setY(listCircu.get(4).getPosition().getY());
        nodes.get(8).position = tabposition.get(8);
        tabposition.get(9).setX(listCircu.get(5).getPosition().getX() + listCircu.get(5).getRadius() + 264);
        tabposition.get(9).setY(listCircu.get(5).getPosition().getY());
        nodes.get(9).position = tabposition.get(9);
        tabposition.get(10).setX(listCircu.get(5).getPosition().getX() + listCircu.get(5).getRadius() - 264);
        tabposition.get(10).setY(listCircu.get(5).getPosition().getY());
        nodes.get(10).position = tabposition.get(9);
        tabposition.get(11).setX(listCircu.get(5).getPosition().getX());
        tabposition.get(11).setY(alineate(nodes.get(1).position.getX(), nodes.get(1).position.getY(), listCircu.get(5).getPosition().getX(), listCircu.get(3).getPosition().getY() + 264).getY());
        nodes.get(11).position = tabposition.get(11);
        tabposition.get(12).setX(alineate(nodes.get(1).position.getX(), nodes.get(1).position.getY(), listCircu.get(6).getPosition().getX(), listCircu.get(6).getPosition().getY() + 264).getX());
        tabposition.get(12).setY(listCircu.get(6).getPosition().getY());
        nodes.get(12).position = tabposition.get(12);
        tabposition.get(13).setX(alineate(nodes.get(4).position.getX(), nodes.get(4).position.getY(), listCircu.get(5).getPosition().getX() - 264, listCircu.get(5).getPosition().getY()).getX());
        tabposition.get(13).setY(listCircu.get(5).getPosition().getY());
        nodes.get(13).position = tabposition.get(12);
    }

    private Vec2 alineate(int xdepart, int ydepart, int xPointoalinate, int yPointoalinate) {
        Vec2 position = new Vec2();
        double r = Math.sqrt((Math.pow(xPointoalinate - xdepart, 2)) - Math.pow(yPointoalinate - ydepart, 2));
        double r1 = 0;
        double teta = Math.atan(ydepart / xdepart) - Math.atan(yPointoalinate / xPointoalinate);
        while ((Math.abs(r - r1) > Math.pow(10, -3)) && (Math.pow(r, 2) != ((Math.pow(xPointoalinate - xdepart, 2) + Math.pow(yPointoalinate - ydepart, 2)))))
            ;
        r1 = r1 + 1;

        xPointoalinate = (int) (r * Math.cos(teta));
        yPointoalinate = (int) (r * Math.sin(teta));
        position.setX(xPointoalinate);
        position.setY(yPointoalinate);
        return position;


    }


    public void int_aretes() {
        ArrayList<Arete> aretes;
        aretes = new ArrayList<Arete>();
        float distance;
        double delta;
        float xa;
        float xb;
        float ya;
        float yb;
        float xc;
        float yc;
        float dx;
        float dy;

        for (Noeud noeud1 : nodes) {

            for (Noeud noeud2 : nodes) {
                distance = (float) Math.sqrt(noeud1.position.getX() * noeud2.position.getX() + noeud1.position.getY() * noeud2.position.getY());
                for (ObstacleCircular obstaclecircular : listCircu) {
                    xa = noeud1.position.getX();
                    xb = noeud2.position.getX();
                    ya = noeud1.position.getY();
                    yb = noeud2.position.getY();
                    xc = obstaclecircular.getCircle().getCenter().getX();
                    yc = obstaclecircular.getCircle().getCenter().getY();
                    dx = xb - xa;
                    dy = yb - ya;
                    delta = Math.pow(2 * (dx * (xa - xc) + dy * (ya - yc)), 2) - 4 * (Math.pow((xb - xa), 2) + Math.pow((yb - ya), 2)) * (Math.pow((xa - xc), 2) + Math.pow((ya - yc), 2) - obstaclecircular.getRadius() * obstaclecircular.getRadius());
                    if (delta < 0) {
                        aretes.add(new Arete(noeud1, noeud2));
                    }

                }

            }
        }
    }
}