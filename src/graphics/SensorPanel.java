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

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Color;
import java.util.ArrayList;

/**
 * panneau sur lequel est dessine la table
 * @author Etienne
 *
 */
public class SensorPanel extends JPanel
{	
	/** numéro pour la serialisation	 */
	private static final long serialVersionUID = -3033815690221481964L;
	
	long startTime = System.currentTimeMillis();
	int captureTime = 10000;
	private ArrayList<Integer> values1 = new ArrayList<Integer>();
	private ArrayList<Integer> values2 = new ArrayList<Integer>();
	private ArrayList<Integer> values3 = new ArrayList<Integer>();
	private ArrayList<Integer> values4 = new ArrayList<Integer>();
	private ArrayList<Integer> times = new ArrayList<Integer>();
	
	public SensorPanel()
	{
	}

	@Override
	public void paintComponent(Graphics g)
	{
		g.setColor(Color.black);
	    g.fillRect(0, 0, this.getWidth(), this.getHeight());
	    
	    ///////////////////////////////////////////////
	    // Graphe 1
	    ///////////////////////////////////////////////
	    
	    int x = 10, y = 10, width = (int)(this.getWidth()/2.1), height = (int)(this.getHeight()/2.1);
	    
	    g.setColor(Color.white);
	    g.drawLine(x,  y + height, x + width, y + height);
	    g.drawLine(x,  y + height, x, y);
	    
	    g.setColor(Color.blue);
	    if(values1.size() > 1)
	    {
	    	//recherche d'extremum pour values
	    	int min = values1.get(0), max = values1.get(0);
	    	for(int i = 0; i < values1.size(); i++)
	    	{
	    		if(values1.get(i) < min)
	    			min = values1.get(i);
	    		if(values1.get(i) > max)
	    			max = values1.get(i);
	    	}
	    	
	    	//echelle fixe
	    	min = 0;
	    	max = 500;
	    	
	    	g.drawString(""+min, x, y + height);
	    	g.drawString(""+max, x, y + 10);
	    	
	    	double a = (height)/(double)(min-max);
	    	double b = y + height - a * min;
	    	double aT = (width)/(double)(captureTime);
	    	double bT = x - aT * (System.currentTimeMillis() - startTime - captureTime);
	    	for(int i = 0; i < values1.size() - 1; i++)
	    		g.drawLine((int)(times.get(i)*aT + bT), (int)(values1.get(i)*a + b), (int)(times.get(i+1)*aT + bT), (int)(values1.get(i+1)*a + b));
	    }
	    
	    ///////////////////////////////////////////////
	    // Graphe 2
	    ///////////////////////////////////////////////
	    
	    x = 10 + width; y = 10;
	    
	    g.setColor(Color.white);
	    g.drawLine(x,  y + height, x + width, y + height);
	    g.drawLine(x,  y + height, x, y);
	    
	    g.setColor(Color.green);
	    if(values2.size() > 1)
	    {
	    	//recherche d'extremum pour values
	    	int min = values2.get(0), max = values2.get(0);
	    	for(int i = 0; i < values1.size(); i++)
	    	{
	    		if(values2.get(i) < min)
	    			min = values2.get(i);
	    		if(values2.get(i) > max)
	    			max = values2.get(i);
	    	}
	    	
	    	//echelle fixe
	    	min = 0;
	    	max = 500;
	    	
	    	g.drawString(""+min, x, y + height);
	    	g.drawString(""+max, x, y + 10);
	    	
	    	double a = (height)/(double)(min-max);
	    	double b = y + height - a * min;
	    	double aT = (width)/(double)(captureTime);
	    	double bT = x - aT * (System.currentTimeMillis() - startTime - captureTime);
	    	for(int i = 0; i < values2.size() - 1; i++)
	    		g.drawLine((int)(times.get(i)*aT + bT), (int)(values2.get(i)*a + b), (int)(times.get(i+1)*aT + bT), (int)(values2.get(i+1)*a + b));
	    }
	    
	    ///////////////////////////////////////////////
	    // Graphe 3
	    ///////////////////////////////////////////////
	    
	    x = 10; y = 10 + height;
	    
	    g.setColor(Color.white);
	    g.drawLine(x,  y + height, x + width, y + height);
	    g.drawLine(x,  y + height, x, y);
	    
	    g.setColor(new Color(255, 100, 100));
	    if(values3.size() > 1)
	    {
	    	//recherche d'extremum pour values
	    	int min = values3.get(0), max = values3.get(0);
	    	for(int i = 0; i < values3.size(); i++)
	    	{
	    		if(values3.get(i) < min)
	    			min = values3.get(i);
	    		if(values3.get(i) > max)
	    			max = values3.get(i);
	    	}
	    	
	    	//echelle fixe
	    	min = 0;
	    	max = 500;
	    	
	    	g.drawString(""+min, x, y + height);
	    	g.drawString(""+max, x, y + 10);
	    	
	    	double a = (height)/(double)(min-max);
	    	double b = y + height - a * min;
	    	double aT = (width)/(double)(captureTime);
	    	double bT = x - aT * (System.currentTimeMillis() - startTime - captureTime);
	    	for(int i = 0; i < values3.size() - 1; i++)
	    		g.drawLine((int)(times.get(i)*aT + bT), (int)(values3.get(i)*a + b), (int)(times.get(i+1)*aT + bT), (int)(values3.get(i+1)*a + b));
	    }
	    
	    ///////////////////////////////////////////////
	    // Graphe 4
	    ///////////////////////////////////////////////
	    
	    x = 10 + width; y = 10 + height;
	    
	    g.setColor(Color.white);
	    g.drawLine(x,  y + height, x + width, y + height);
	    g.drawLine(x,  y + height, x, y);
	    
	    g.setColor(Color.cyan);
	    if(values4.size() > 1)
	    {
	    	//recherche d'extremum pour values
	    	int min = values4.get(0), max = values4.get(0);
	    	for(int i = 0; i < values4.size(); i++)
	    	{
	    		if(values4.get(i) < min)
	    			min = values4.get(i);
	    		if(values4.get(i) > max)
	    			max = values4.get(i);
	    	}
	    	
	    	//echelle fixe
	    	min = 0;
	    	max = 500;
	    	
	    	g.drawString(""+min, x, y + height);
	    	g.drawString(""+max, x, y + 10);
	    	
	    	double a = (height)/(double)(min-max);
	    	double b = y + height - a * min;
	    	double aT = (width)/(double)(captureTime);
	    	double bT = x - aT * (System.currentTimeMillis() - startTime - captureTime);
	    	for(int i = 0; i < values4.size() - 1; i++)
	    		g.drawLine((int)(times.get(i)*aT + bT), (int)(values4.get(i)*a + b), (int)(times.get(i+1)*aT + bT), (int)(values4.get(i+1)*a + b));
	    }
	    
	    
	}
	
	public void drawInteger(Integer value1, Integer value2, Integer value3, Integer value4)
	{
		values1.add(value1);
		times.add(new Integer((int)(System.currentTimeMillis() - startTime)));
		values2.add(value2);
		values3.add(value3);
		values4.add(value4);
		for(int i = 0; i < times.size() && times.get(i) < System.currentTimeMillis() - startTime - captureTime; i++)
		{
			values1.remove(i);
			values2.remove(i);
			values3.remove(i);
			values4.remove(i);
			times.remove(i);
		}
		this.repaint();
	}
}
