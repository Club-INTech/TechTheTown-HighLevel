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

//TODO : refactor

package graphics;

import pathfinder.Graphe;
import pathfinder.Node;
import robot.Robot;
import smartMath.Vec2;
import table.Table;
import table.obstacles.ObstacleCircular;
import table.obstacles.ObstacleProximity;
import table.obstacles.ObstacleRectangular;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import java.awt.Image;
import java.awt.Color;
import java.awt.Graphics;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * panneau sur lequel est dessine la table
 * @author Etienne, rem
 *
 */
public class TablePanel extends JPanel
{
	/** numéro pour la serialisation	 */
	private static final long serialVersionUID = -3033815690221481964L;

	/** Champs pour l'interface Pathfinding : n'ayant pas de robot instancié, on récupère en brut les données */
	private ArrayList<Vec2> path;
	private ArrayList<Vec2> clics;
	private Vec2 point;
	private CopyOnWriteArrayList<Node> nodes;
	public static boolean showGraph = true;

	/** Table & robot */
	private Table table;
	private Robot robot;
	private Graphe graphe;

	/** Pour de l'affichage dynamique lorsque l'on test avec le robot */
	private boolean isRobotPresent = true;

	/** Image de background */
	private Image tableBackground;

	/** Couleurs */
	private Color obstacleColor = new Color(180, 50, 50, 100);
	private Color adverseColor = new Color(180, 120, 50, 100);
	private Color unconfirmedColor = new Color(220, 220, 50, 100);
	private Color robotColor = new Color(50, 180, 50, 100);
	private Color teamColor = new Color(50, 80, 50, 220);
	private Color pathColor = new Color(200, 0, 80, 220);
	private Color graphColor = new Color(50, 80, 120, 40);

	/** Construit un panel pour du l'interface full
	 * @param table
	 * @param robot
	 */
	public TablePanel(Table table, Robot robot)
	{
		path = new ArrayList<>();
		clics = new ArrayList<>();
		this.table = table;
		this.robot = robot;
		this.graphe = table.getGraph();
		this.nodes = table.getGraph().getNodes();
		this.point = new Vec2();

		try{
			tableBackground = ImageIO.read(new File("images/RobotCities_2018.png"));
		}catch (IOException e){
			e.printStackTrace();
		}
	}

	/** Construit un panel pour l'interface pathfinding
	 * @param table
	 */
	public TablePanel(Table table)
	{
		path = new ArrayList<>();
		clics = new ArrayList<>();
        this.table = table;
        this.graphe = table.getGraph();
        nodes = graphe.getNodes();
		isRobotPresent = false;
		showGraph = true;
		this.point=new Vec2();

		try{
			tableBackground = ImageIO.read(new File("images/RobotCities_2018.png"));
		}catch (IOException e){
			e.printStackTrace();
		}
	}

	@Override
	public void paintComponent(Graphics graphics)
	{
		// Variables
		int robotRadius = (int) (robot.getRobotRadius()*0.3);
		double robotOrientation = robot.getOrientation();
		Vec2 robotPosition = robot.getPosition();
		Vec2 vec1;
		Vec2 vec2;
		Vec2 vec3;
		Vec2 vec4;

		// Background
		graphics.drawImage(tableBackground,0, 0, 900, 600, this);

		// Bords de la table
		graphics.setColor(Color.BLACK);
		graphics.drawRect(0, 0, 899, 599);

	    // Obstacle de la table
	    graphics.setColor(obstacleColor);
		graphics.fillRect(0,0, 900, robotRadius);
		graphics.fillRect(0, 600 - robotRadius, 900, robotRadius);
		graphics.fillRect(0, robotRadius, robotRadius, 600 - 2*robotRadius);
		graphics.fillRect(900 - robotRadius, robotRadius, robotRadius, 600 - 2*robotRadius);


	    // Obstacles rectangulaires
	    for(ObstacleRectangular rectangular : table.getObstacleManager().getRectangles())
	    {
	        vec1 = new Vec2(-rectangular.getSizeX()/2, rectangular.getSizeY()/2);
	    	vec2 = changeRefToDisplay(rectangular.getPosition().plusNewVector(vec1));
	    	graphics.fillRect(vec2.getX(), vec2.getY(), (int)(rectangular.getSizeX()*0.3), (int)(rectangular.getSizeY()*0.3));
	    }

	    // Obstacles ciculaires
	    for(ObstacleCircular circular : table.getObstacleManager().getmCircularObstacle())
	    {
	        vec1 = new Vec2(-circular.getRadius(), circular.getRadius());
	    	vec2 = changeRefToDisplay(circular.getPosition().plusNewVector(vec1));
			graphics.fillOval(vec2.getX(), vec2.getY(), (int)(circular.getRadius()*0.6), (int)(circular.getRadius()*0.6));
	    }

	    // Robot adverse
	    graphics.setColor(adverseColor);
		for(ObstacleProximity adverse : table.getObstacleManager().getMobileObstacles())
		{
		    vec1 = new Vec2(-adverse.getRadius() - robotRadius*3.3, adverse.getRadius() - robotRadius*3.3);
			vec2 = changeRefToDisplay(adverse.getPosition().plusNewVector(vec1));
			graphics.fillOval(vec2.getX(), vec2.getY(), (int) ((adverse.getRadius() - robotRadius)*0.6), (int) ((adverse.getRadius() - robotRadius)*0.6));
		}

	    // Robot adverse non confirmé
		graphics.setColor(unconfirmedColor);
		for(ObstacleProximity unconfirmed : table.getObstacleManager().getUntestedArrayList())
		{
		    vec1 = new Vec2(-unconfirmed.getRadius() - robotRadius*3.3, unconfirmed.getRadius() - robotRadius*3.3);
			vec2 = changeRefToDisplay(unconfirmed.getPosition().plusNewVector(vec1));
			graphics.fillOval(vec2.getX(), vec2.getY(), (int)((unconfirmed.getRadius() - robotRadius)*0.6), (int)((unconfirmed.getRadius() - robotRadius)*0.6));
		}

		// Notre robot
	    if(isRobotPresent)
	    {
		    graphics.setColor(robotColor);
		    vec1 = new Vec2(new Double(robotRadius), robotOrientation);
			vec2 = changeRefToDisplay(robotPosition).plusNewVector(vec1);

			vec3 = new Vec2(-robotRadius, -robotRadius);
			vec4 = changeRefToDisplay(robotPosition).plusNewVector(vec3);
			graphics.fillOval(vec4.getX(), vec4.getY(), robotRadius*2, robotRadius*2);

			graphics.setColor(teamColor);
			graphics.drawLine(vec2.getX(), vec2.getY(), vec4.getX(), vec4.getY());
	    }

		// Le chemin suivi
		graphics.setColor(pathColor);
		for(int i=0; i<path.size()-1; i++){
			vec1 = changeRefToDisplay(path.get(i));
			vec2 = changeRefToDisplay(path.get(i+1));
			graphics.drawLine(vec1.getX(), vec1.getY(), vec2.getX(), vec2.getY());
		}

		// Le graphe
		if(showGraph){
		    synchronized (graphe.lock) {
                graphics.setColor(graphColor);
                for (Node node : nodes) {
                    vec1 = changeRefToDisplay(node.getPosition());
                    graphics.fillOval(vec1.getX() - 2, vec1.getY() - 2, 4, 4);
                    for (Node node1 : node.getNeighbours().keySet()) {
                        if (node.getNeighbours().get(node1).isReachable()) {
                            vec2 = changeRefToDisplay(node1.getPosition());
                            graphics.drawLine(vec1.getX(), vec1.getY(), vec2.getX(), vec2.getY());
                        }
                    }
                }
            }
		}

		// Print les clics et leur position
		for (Vec2 clic : clics){
			Vec2 clicDisplay = changeRefToDisplay(clic);
			graphics.fillOval(clicDisplay.getX() - 4, clicDisplay.getY() - 4, 8, 8);
			graphics.drawString(clic.toStringInterface(), clicDisplay.getX() - 30, clicDisplay.getY() + 20);
		}

		// Infos diverses
		graphics.setColor(Color.BLACK);
		graphics.fillRect(900, 0, 400, 600);
		graphics.fillRect(0, 600, 1300, 400);
		graphics.setColor(Color.DARK_GRAY);
		graphics.fillRoundRect(920, 20, 360, 580, 20, 20);
		//graphics.fillRoundRect(20, 620, 1260, 275, 20, 20);
		//afficher le point qu'on veut
		graphics.setColor(Color.GREEN);
		Vec2 position=changeRefToDisplay(point);
		graphics.fillOval(position.getX()-4,position.getY()-4,8,8);
	}

	/** Conversion en coordonnées d'affichage
	 * @param vec
	 */
	private Vec2 changeRefToDisplay(Vec2 vec){
		return new Vec2(new Integer((int)((vec.getX() + 1500)*0.3)),new Integer((int)((2000 - vec.getY())*0.3)-5));
	}

	/** Setters */
	public void setPath(ArrayList<Vec2> path) {
		this.path = path;
		removeAll();
		revalidate();
	}
	public void setClics(ArrayList<Vec2> clics) {
		this.clics = clics;
		removeAll();
		revalidate();
	}
	public void setPoint(Vec2 point ){
		this.point=point;
		removeAll();
		revalidate();
	}

	/** Getters */
	public Table getTable()
	{
		return table;
	}
}
