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

import pathfinder.Arete;
import pathfinder.Noeud;
import robot.Robot;
import smartMath.Segment;
import smartMath.Vec2;
import table.Table;
import table.obstacles.ObstacleCircular;
import table.obstacles.ObstacleProximity;
import table.obstacles.ObstacleRectangular;
import tests.container.A;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

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
	private ArrayList<Arete> aretes;
	private ArrayList<Vec2> clics;
	private ArrayList<Noeud> nodes;
	public static boolean showGraph = false;

	/** Table & robot */
	private Table table;
	private Robot robot;

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
	private Color pathColor = new Color(200, 0, 80);
	private Color graphColor = new Color(50, 80, 120, 100);

	/** Construit un panel pour du l'interface full
	 * @param table
	 * @param robot
	 */
	public TablePanel(Table table, Robot robot)
	{
		path = new ArrayList<>();
		aretes = new ArrayList<>();
		clics = new ArrayList<>();
		nodes=new ArrayList<>();
		this.table = table;
		this.robot = robot;

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
		aretes = new ArrayList<>();
		clics = new ArrayList<>();
		nodes=new ArrayList<>();
        this.table = table;
		isRobotPresent = false;
		showGraph = true;

		try{
			tableBackground = ImageIO.read(new File("images/RobotCities_2018.png"));
		}catch (IOException e){
			e.printStackTrace();
		}
	}

	@Override
	public void paintComponent(Graphics graphics)
	{
		// La table
		int wideDisplay = (int)(table.getObstacleManager().getRobotRadius()*0.3);
		Vec2 upLeftCorner;
		Vec2 pathNode1;
		Vec2 pathNode2;
		Vec2 pathNode3;


		// Background
		graphics.drawImage(tableBackground,0, 0, 900, 600, this);

		// Bords de la table
		graphics.setColor(Color.BLACK);
		graphics.drawRect(0, 0, 899, 599);
	    
	    // Obstacle de la table
	    graphics.setColor(obstacleColor);
		graphics.fillRect(0,0, 900, wideDisplay);
		graphics.fillRect(0, 600 - wideDisplay, 900, wideDisplay);
		graphics.fillRect(0, wideDisplay, wideDisplay, 600 - 2*wideDisplay);
		graphics.fillRect(900 - wideDisplay, wideDisplay, wideDisplay, 600 - 2*wideDisplay);
	    
	    // Obstacles rectangulaires
	    for(ObstacleRectangular rectangular : table.getObstacleManager().getRectangles())
	    {
	    	upLeftCorner = changeRefToDisplay(rectangular.getPosition().plusNewVector(new Vec2(-rectangular.getSizeX()/2, rectangular.getSizeY()/2)));
	    	graphics.fillRect(upLeftCorner.getX(), upLeftCorner.getY(), (int)(rectangular.getSizeX()*0.3), (int)(rectangular.getSizeY()*0.3));
	    }	    
	    
	    // Obstacles ciculaires
	    for(ObstacleCircular circular : table.getObstacleManager().getmCircularObstacle())
	    {
	    	upLeftCorner = changeRefToDisplay(circular.getPosition().plusNewVector(new Vec2(-circular.getRadius(), circular.getRadius())));
			graphics.fillOval(upLeftCorner.getX(), upLeftCorner.getY(), (int)(circular.getRadius()*0.6), (int)(circular.getRadius()*0.6));
	    }
	    
	    // Robot adverse
	    graphics.setColor(adverseColor);
		for(ObstacleProximity adverse : table.getObstacleManager().getMobileObstacles())
		{
			upLeftCorner = changeRefToDisplay(adverse.getPosition().plusNewVector(new Vec2(-adverse.getRadius(), adverse.getRadius())));
			graphics.fillOval(upLeftCorner.getX(), upLeftCorner.getY(), (int)(adverse.getRadius()*0.6), (int)(adverse.getRadius()*0.6));
		}
	    
	    // Robot adverse non confirmé
		graphics.setColor(unconfirmedColor);
		for(ObstacleProximity unconfirmed : table.getObstacleManager().getUntestedArrayList())
		{
			upLeftCorner = changeRefToDisplay(unconfirmed.getPosition().plusNewVector(new Vec2(-unconfirmed.getRadius(), unconfirmed.getRadius())));
			graphics.fillOval(upLeftCorner.getX(), upLeftCorner.getY(), (int)(unconfirmed.getRadius()*0.6), (int)(unconfirmed.getRadius()*0.6));
		}
	    
		// Notre robot
	    if(isRobotPresent)
	    {
		    graphics.setColor(robotColor);
			Vec2 robotPosition = robot.getPositionFast();
			Vec2 robotPositionDisplay = changeRefToDisplay(robotPosition);
			double robotOrientation = robot.getOrientationFast();
			Vec2 orentationIndicator = changeRefToDisplay(new Vec2(new Double(robot.getRobotRadius()), robotOrientation));

			upLeftCorner = changeRefToDisplay(robotPosition).plusNewVector(new Vec2(-wideDisplay, -wideDisplay));
			graphics.fillOval(upLeftCorner.getX(), upLeftCorner.getY(), wideDisplay*2, wideDisplay*2);

			graphics.setColor(teamColor);
			graphics.drawLine(robotPositionDisplay.getX(), robotPositionDisplay.getY(), orentationIndicator.getX(), orentationIndicator.getY());
	    }

		// Le chemin suivi
		graphics.setColor(pathColor);
		for(int i=0; i<path.size()-1; i++){
			pathNode1 = changeRefToDisplay(path.get(i));
			pathNode2 = changeRefToDisplay(path.get(i+1));
			graphics.drawLine(pathNode1.getX(), pathNode1.getY(), pathNode2.getX(), pathNode2.getY());
		}

		// Le graphe
		if(showGraph){
			graphics.setColor(graphColor);
			for(Noeud noeud : nodes){
				pathNode3=changeRefToDisplay(noeud.getPosition());
				graphics.fillOval(pathNode3.getX()-4,pathNode3.getY()-4,8,8);

			}
			for (Arete ridge : aretes){
				pathNode1 = changeRefToDisplay(ridge.noeud1.getPosition());
				pathNode2 = changeRefToDisplay(ridge.noeud2.getPosition());
				graphics.drawLine(pathNode1.getX(), pathNode1.getY(), pathNode2.getX(), pathNode2.getY());
				graphics.fillOval(pathNode1.getX() - 4, pathNode1.getY() - 4, 8, 8);
				graphics.fillOval(pathNode2.getX() - 4, pathNode2.getY() - 4, 8, 8);
			}

		}

		// Print les clics et leur position
		for (Vec2 clic : clics){
			Vec2 clicDisplay = changeRefToDisplay(clic);
			graphics.fillOval(clicDisplay.getX() - 4, clicDisplay.getY() - 4, 8, 8);
			graphics.drawString(clic.toStringInterface(), clicDisplay.getX() - 30, clicDisplay.getY() + 20);
		}

		// Infos diverses
		graphics.setColor(Color.DARK_GRAY);
		graphics.fillRoundRect(920, 20, 360, 580, 20, 20);
		graphics.fillRoundRect(20, 620, 1260, 275, 20, 20);
	}

	/** Conversion en coordonnées d'affichage
	 * @param vec
	 */
	private Vec2 changeRefToDisplay(Vec2 vec){
		return new Vec2(new Integer((int)((vec.getX() + 1500)*0.3)),new Integer((int)((2000 - vec.getY())*0.3)));
	}

	/** Setters */
	public void setPath(ArrayList<Vec2> path) {
		this.path = path;
		removeAll();
		revalidate();
	}
	public void setAretes(ArrayList<Arete> aretes) {
		this.aretes = aretes;
		removeAll();
		revalidate();
	}
	public void setClics(ArrayList<Vec2> clics) {
		this.clics = clics;
		removeAll();
		revalidate();
	}
	public void setNodes(ArrayList<Noeud> nodes){
		this.nodes=nodes;
		removeAll();
		revalidate();
	}

	/** Getters */
	public Table getTable()
	{
		return table;
	}
}
