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

package utils;

import container.Service;
import exceptions.ConfigPropertyNotFoundException;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Service de configuration du robot.
 * Cette classe lit le fichier  /pc/config/config.ini pour en extraire les informations de configuration et les redistribuer a qui les demandera
 *
 * @author pf,
 */
public class Config implements Service
{
	/** Nom du fichier local. */
	private String localFileName = "local.ini";
	
	/** Nom du fichier de configuration a charger. */
	public static String configFileName = "config_bleu.txt";
	
	/** chemin relatif au chemin d'exécution d'ou charger le fichier de config */
	private String path;
	
	/** Le fichier de configuration, une fois parsé par le classe builltin de java. */
	private Properties configProperties = new Properties();

	/** Le fichier local, une fois parsé par le classe builltin de java. */
	private Properties localProperties = new Properties();
	

	
	/**
	 * fait un nouveau gestionnaire de configuration
	 *
	 * @param path endroit ou trouver le fichier de configuration
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private Config(String path) throws IOException
	{
		this.path = path;
		System.out.println("Loading config from current directory : " +  System.getProperty("user.dir") + path);
		try
		{
			this.configProperties.load(new FileInputStream(this.path+this.configFileName));
		}
		catch  (IOException e)
		{
			e.printStackTrace();
			throw new IOException("Erreur ouverture de config.ini Le chemin d'exécution du programme est-il bien dans /pc ?");
		}
		
	
		try
		{
			this.configProperties.load(new FileInputStream(this.path+this.localFileName));
		}
		catch  (IOException e)
		{
			try
			{
				FileOutputStream fileOut = new FileOutputStream(this.path+this.localFileName);
				this.localProperties.store(fileOut, "Ce fichier est un fichier généré par le programme.\nVous pouvez redéfinir les variables de config.ini dans ce fichier dans un mode de votre choix.\nPS : SopalINT RULEZ !!!\n");
			}
			catch (IOException e2)
			{
				e2.printStackTrace();
				throw new IOException("Erreur création de local.ini");
			}	
			throw new IOException("Erreur ouverture de local.ini");
		}	
		printConfigFile();
	}

		/**
	 * Méthode que tout le monde utilise de récupération des paramètres de configuration.
	 *
	 * @param nom nom de la propriété a récupérer
	 * @return Le paramètre coresspondant a ce nom
	 * @throws ConfigPropertyNotFoundException 
	 */
	public String getProperty(String nom) throws ConfigPropertyNotFoundException
	{		
		String out = null;
		out = configProperties.getProperty(nom);
		if(out == null)
		{
			System.out.println("Erreur config, la propriété nommée '"+nom+"' est introuvable.");
			throw new ConfigPropertyNotFoundException(nom);
		}
		return out;
	}

	/**
	 * Change la valeur d'un parmaètre de configuration.
	 * Le changement sera perdu si le programme redémarre.
	 * Méthode utilisée seulement par les tests.
	 *
	 * @param nom nom de la propriété a modifier
	 * @param value la nouvelle valeur a lui donner
	 */
	public void set(String nom, String value)
	{
		System.out.println("Changement de config: " +nom+" = "+value+" (ancienne valeur: "+configProperties.getProperty(nom)+")");
		configProperties.setProperty(nom, value);
	}

	/**
	 * Affiche tout le fichier de config.
	 * Si la valeur de config affiche_debug est a false, le fichier de config ne sera pas affiché, même si cette méthode est appellée
	 */
	private void printConfigFile()
	{
		if(Boolean.parseBoolean(configProperties.getProperty("affiche_debug")))
		{
			System.out.println("Configuration initiale");
			
			// imprime chaque propriété
			for(Object o: configProperties.keySet())
				System.out.println(o+": "+configProperties.get(o));
		}
	}
	
	/* (non-Javadoc)
	 * @see container.Service#updateConfig()
	 */
	public void updateConfig()
	{
	}
	
}
