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
import scripts.ScriptManager;
import smartMath.Vec2;
import strategie.GameState;
import table.Table;

import javax.swing.JFrame;
import java.awt.Color;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Interface graphique pour faciliter le debugage HL
 * @author Etienne, rem
 */
public class Window extends JFrame
{
	/** numéro de serialisation	 */
	private static final long serialVersionUID = -3140220993568124763L;

	/** Panels : sert à définir ce que l'on dessine sur la fenêtre */
	private TablePanel tablePanel;

	/** Mouse Listener & Keyboard Listener */
	private Mouse mouse;
	private Keyboard keyboard;

	/** Couleur de fond */
	private Color backgroundColor = new Color(25, 20, 30);

	/** Construit toute l'interface : cette dernière contient la table (avec robot et chemin), ainsi que l'état du robot
	 * (action executée, état de certaines variables, ...)
	 * @param table
	 * @param state
	 * @param scriptManager
	 */
	public Window(Table table, GameState state, ScriptManager scriptManager, boolean isMontlheryActive)
	{
		this.setTitle("Interface - Full");
	    this.setSize(1300, 630);
	    this.setLocationRelativeTo(null);
	    this.setResizable(false);
	    this.setBackground(backgroundColor);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    tablePanel = new TablePanel(table, state.robot);
	    this.setContentPane(tablePanel);
	    
	    mouse = new Mouse(tablePanel);
	    addMouseListener(mouse);

		if (isMontlheryActive) {
			keyboard = new Keyboard(state, scriptManager);
			addKeyListener(keyboard);
		}
	    this.setVisible(true);
	}

	/** Construit l'interface de debug du pathfinding : ce dernier contient la table, sur laquelle est présente le graph,
	 * un mouse Listener permettant de tester le pathfinding de manière interactive, ainsi que quelques infos de debug
	 * @param table
	 */
	public Window(Table table)
	{
		this.setTitle("Interface - Pathfinding");
		this.setSize(1300, 950);
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		this.setBackground(backgroundColor);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		tablePanel = new TablePanel(table);
		this.setContentPane(tablePanel);

		mouse = new Mouse(tablePanel);
		addMouseListener(mouse);

		this.setVisible(true);
	}

	/** Attend que l'on clic droit et gauche et renvoie les positions des clics (gauche puis droit) */
	public ArrayList<Vec2> waitLRClic() throws InterruptedException{
		mouse.resetClics();
		ArrayList<Vec2> clics = new ArrayList<>();
		while(mouse.getLeftClicPosition() == null || mouse.getRightClicPosition() == null){
			Thread.sleep(100);
			// TODO Mettre une vérification de la position du clic : accès à Table par le tablePanel
		}
		clics.add(mouse.getLeftClicPosition().clone());
		clics.add(mouse.getRightClicPosition().clone());
		tablePanel.setClics(clics);
		mouse.resetClics();
		return clics;
	}

	/** Attend que l'on clic gauche et renvoie la position des clics (gauche puis droit) */
	public Vec2 waitLClic() throws InterruptedException{
		mouse.resetClics();
		ArrayList<Vec2> clics = new ArrayList<>();
		while(mouse.getLeftClicPosition() == null ){
			Thread.sleep(100);
			// TODO Mettre une vérification de la position du clic : accès à Table par le tablePanel
		}
		clics.add(mouse.getLeftClicPosition().clone());
		tablePanel.setClics(new ArrayList<Vec2>(clics));
		mouse.resetClics();
		return clics.get(0);
	}

	/** Permet d'afficher les aretes/le chemin */
	public void setArete(ArrayList<Arete> aretes){
		tablePanel.setAretes(aretes);
	}
	public void setPath(ArrayList<Vec2> path){
		tablePanel.setPath(path);
	}
	public void setPoint(Vec2 point){
		tablePanel.setPoint(point);
		repaint();
	}
	public void setNode(ArrayList<Noeud> nodes){
		tablePanel.setNodes(nodes);;
	}

	/** Getters */
	public TablePanel getPanel()
	{
		return tablePanel;
	}
	public Mouse getMouse()
	{
		return mouse;
	}
	public Keyboard getKeyboard()
	{
		return keyboard;
	}
}
