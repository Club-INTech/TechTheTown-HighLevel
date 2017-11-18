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

import scripts.ScriptManager;
import strategie.GameState;
import table.Table;

import javax.swing.*;

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

	/** Construit toute l'interface : cette dernière contient la table (avec robot et chemin), ainsi que l'état du robot
	 * (action executée, état de certaines variables, ...)
	 * @param table
	 * @param state
	 * @param scriptManager
	 */
	public Window(Table table, GameState state, ScriptManager scriptManager)
	{
		this.setVisible(true);
		this.setTitle("Interface - Full");
	    this.setSize(1300, 635);
	    this.setLocationRelativeTo(null);
	    this.setResizable(false);
	    
	    tablePanel = new TablePanel(table, state.robot);
	    this.setContentPane(tablePanel);
	    
	    mouse = new Mouse(tablePanel);
	    addMouseListener(mouse);
	    
	    keyboard = new Keyboard(state, scriptManager);
	    addKeyListener(keyboard);
	}

	/** Construit l'interface de debug du pathfinding : ce dernier contient la table, sur laquelle est présente le graph,
	 * un mouse Listener permettant de tester le pathfinding de manière interactive, ainsi que quelques infos de debug
	 * @param table
	 */
	public Window(Table table)
	{
		this.setVisible(true);
		this.setTitle("Interface - Pathfinding");
		this.setSize(1300, 635);
		this.setLocationRelativeTo(null);
		this.setResizable(false);

		tablePanel = new TablePanel(table);
		this.setContentPane(tablePanel);

		mouse = new Mouse(tablePanel);
		addMouseListener(mouse);
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
