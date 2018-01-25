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

package enums;

public enum ContactSensors {
	// Syntaxe: NOM_METHODE("protocole_serie", duree_action, valeur_par_default, nombre_de_ligne_reponse)
	// exemple : DOOR_OPENED_LEFT("ildo",10,true),
	// TODO : rajouter les contacteurs
	/**TO DO: Définir les durées d'actions*/
	PORTE_OUVERTE("po",10,false),
	BRASINTECH_BAISSE("binb",10,false),
	BRASTSP_BAISSE("btspb",10,false),
	BRASABEILLE_BAISSE("bab",10,false),
	BRASINTERR_BAISSE("bib",10,false),
	CUBE_PRIS("cp",10,false),
	TOUR_CONSTRUITE("tc",10,false),

	;


	/**la duree moyenne que fait perdre une autre valeur que la valeur par default*/
	private int averageDuration;
	
	/**le string a envoyer a la serie*/
	private String serialSensor;
	
	/**la valeur par defaut envoyee par le capteur (celle qui fait terminer le script le plus vite)*/
	private boolean defaultValue;

	public int getAverageDuration() 
	{
		return averageDuration;
	}
	
	ContactSensors(String serialString, int duration, boolean defaultValue)
	{
		this.defaultValue = defaultValue;
		this.serialSensor = serialString;
		this.averageDuration = duration;
	}
	
	/**
	 * Retrouve la chaine de caractère a envoyer par la série a la carte capteur pour obtenir la valeure du capteur
	 * @return la chaine de caractère a envoyer par la série a la carte
	 */
	public String getSerialCommunication()
	{
		return serialSensor;
	}
	
	/**
	 *  
	 * @return la valeur par default du capteur
	 */
	public boolean getDefaultValue()
	{
		return defaultValue;
	}
}
