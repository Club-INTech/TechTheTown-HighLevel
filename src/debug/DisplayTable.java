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

package debug;

import smartMath.Vec2;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Interface graphique écrite à l'arrache
 * @author pf
 *
 */

public class DisplayTable extends JPanel {

	private static final long serialVersionUID = 1L;

	private class Point
	{
		public int x, y;
		Color couleur;
		
		public Point(int x, int y, Color couleur)
		{
			this.x = x;
			this.y = y;
			this.couleur = couleur;
		}

		public Point(Vec2 p, Color couleur)
		{
			this(p.getX(), p.getY(), couleur);
		}
	}
	
	private static final int nbListe = 3;
	@SuppressWarnings("unchecked")
	private ArrayList<Point>[] points = (ArrayList<Point>[]) new ArrayList[nbListe];
	private Point uniquePoint = null;
	private ArrayList<Hyperbola> hyperboles = new ArrayList<Hyperbola>();
	private Color[] couleursDefaut = {Couleur.BLEU.couleur, Couleur.ROUGE.couleur, Couleur.VERT.couleur};
	private boolean afficheFond;
	private int sizeX = 900, sizeY = 600; // taille par défaut
	private Image image;
	
	/**
	 * Crée un affichage. Le paramètre contrôle l'utilisation du fichier "fond.png" comme image de fond.
	 * @param afficheFond
	 */
	public DisplayTable(boolean afficheFond)
	{
		for(int i = 0; i < nbListe; i++)
			points[i] = new ArrayList<Point>();

		this.afficheFond = afficheFond;
		if(afficheFond)
		{
			try {
				image = ImageIO.read(new File("fond.png"));
				sizeX = image.getWidth(this);
				sizeY = image.getHeight(this);
			} catch (IOException e) {
				this.afficheFond = false;
				System.out.println("Fichier fond.png introuvable !");
			}
		}
		showOnFrame();
	}	
	
	// Conversions qui pourront être utiles un jour
/*	private int distanceXtoWindow(int dist)
	{
		return dist*sizeX/3000;
	}

	private int distanceYtoWindow(int dist)
	{
		return dist*sizeY/2000;
	}

	private int WindowToX(int x)
	{
		return x*3000/sizeX-1500;
	}

	private int WindowToY(int y)
	{
		return 2000-y*2000/sizeY;
	}
*/

	private int XtoWindow(double x)
	{
		return (int)((x+1500)*sizeX/3000);
	}

	private int YtoWindow(double y)
	{
		return (int)((2000-y)*sizeY/2000);
	}
	
	/**
	 * Appelé automatiquement. Affiche tous les points
	 */
	public synchronized void paint(Graphics g)
	{
		g.setColor(Color.WHITE);
		if(afficheFond)
			g.drawImage(image, 0, 0, this);
		else
			g.fillRect(0, 0, sizeX, sizeY);

		for(Hyperbola h : hyperboles)
		{
			drawHyperbola(g, h);
		}

		for(int i = 0; i < nbListe; i++)
		{
			Point last = null;
			for(Point p : points[i])
			{
				drawPoint(g, p, 8);
				if(last != null)
					drawLine(g, last, p);
				last = p;
			}
		}
		
		if(uniquePoint != null)
			drawPoint(g, uniquePoint, 8);
		
/*		last = null;
		for(Point p : points2)
		{
			drawPoint(g, p, 8);
			if(last != null)
				drawLine(g, last, p);
			last = p;
		}*/
	}

	/**
	 * Affichage d'une hyperbole
	 * @param g
	 * @param h
	 */
	public void drawHyperbola(Graphics g, Hyperbola h)
	{
		double a = h.delta / 2;
		double c = h.p1.distance(h.p2) / 2;
		double b = Math.sqrt(c*c - a*a);
		Vec2 centre =  new Vec2((h.p1.getX() + h.p2.getX()) / 2, (h.p1.getY() + h.p2.getY()) / 2);
		double angle = Math.atan2(h.p2.getY() - h.p1.getY(), h.p2.getX() - h.p1.getX());
		double cos = Math.cos(angle);
		double sin = Math.sin(angle);
		Point last = null, point;
		for(double i = -10000; i < 10000; i++)
		{
			double x = -a*Math.cosh(i/1000);
			double y = b*Math.sinh(i/1000);
			Vec2 p = new Vec2((int)(cos*x-sin*y), (int)(sin*x+cos*y));
			p.setX(centre.getX());
			p.setY(centre.getY());
			point = new Point(p, Couleur.NOIR.couleur);
			if(last != null)
				drawLine(g, last, point);
			last = point;
		}
	}
	
	/**
	 * Affiche un point à l'écran
	 * @param g
	 * @param p
	 * @param taille
	 */
	public void drawPoint(Graphics g, Point p, int taille)
	{
		g.setColor(p.couleur);
		g.fillOval(XtoWindow(p.x)-taille/2,
				YtoWindow(p.y)-taille/2,
				taille,
				taille);
	}
	
	public void drawLine(Graphics g, Point p1, Point p2)
	{
		g.setColor(Color.BLACK);
		g.drawLine(XtoWindow(p1.x), YtoWindow(p1.y), XtoWindow(p2.x), YtoWindow(p2.y));
	}
	
	public void showOnFrame()
	{
		setBackground(Color.WHITE);
		setPreferredSize(new Dimension(sizeX,sizeY));
		JFrame frame = new JFrame("Balise T3");
		frame.getContentPane().add(this);
		frame.pack();
		frame.setVisible(true);
	}

	public synchronized void addPoint(Vec2 p, int indiceListe)
	{
		addPoint(p, indiceListe, couleursDefaut[indiceListe]);
	}

	public synchronized void addPointFromTimestamps(double t0, double t1, double t2, int indiceListe)
	{
		//t0 = CANAL_1 ; t1 = CANAL_2 ; t2 = INT
		addPoint(Triangulation.computePoints(t0, t1, t2)[0], indiceListe);
	}

	public synchronized void addAllPointsFromTimestamps(double t0, double t1, double t2, int indiceListe1, int indiceListe2)
	{
		//t0 = CANAL_1 ; t1 = CANAL_2 ; t2 = INT
        Vec2[] pos = Triangulation.computePoints(t0, t1, t2);
		addPoint(pos[0], indiceListe1);
        addPoint(pos[1], indiceListe2);
	}
	
	private synchronized void addPoint(Vec2 p, int indiceListe, Color couleur)
	{
		if(p == null)
			return;

		if(p.getX() >= -1500 && p.getX() <= 1500 && p.getY() >= 0 && p.getY() <= 2000 && indiceListe >= 0 && indiceListe < nbListe)
		{
			points[indiceListe].add(new Point(p, couleur));
			repaint();
		}
	}

	public synchronized void showHyperbolaFromTimestamps(double t0, double t1, double t2)
	{
		clearHyperboles();
		hyperboles.add(new Hyperbola(0, Math.abs(t2-t1)));
        hyperboles.add(new Hyperbola(1, Math.abs(t0-t2)));
        hyperboles.add(new Hyperbola(2, Math.abs(t0-t1)));
        repaint();
	}
	
	public synchronized void clearPoints()
	{
		for(int i = 0; i < nbListe; i++)
			points[i].clear();
		repaint();
	}
	
	public synchronized void addHyperbola(Hyperbola h)
	{
		hyperboles.add(h);
		repaint();
	}

	public synchronized void clearHyperboles()
	{
		hyperboles.clear();
		repaint();
	}

	/**
	 * Sauvegarde (en png) l'image
	 * @param filename
	 */
	public void saveImage(String filename)
	{
		BufferedImage bi = new BufferedImage(sizeX, sizeY, BufferedImage.TYPE_INT_RGB);
		paint(bi.getGraphics());
	    try {
			ImageIO.write(bi, "PNG", new File(filename));
		} catch (IOException e) {
			System.err.println(e);
		}
	}
	
	public void setUniquePoint(Vec2 v)
	{
		uniquePoint = new Point(v, Couleur.JAUNE.couleur);
	}
	
	public void setUniquePointFromTimestamps(double t0, double t1, double t2)
	{
		setUniquePoint(Triangulation.computePoints(t0, t1, t2)[0]);
	}
	
}
