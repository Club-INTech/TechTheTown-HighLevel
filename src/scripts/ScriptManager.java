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

package scripts;

import container.Service;
import enums.ScriptNames;
import hook.HookFactory;
import pfg.config.Config;
import utils.Log;

/**
  * Classe enregistrée comme service qui instancie puis fournira les scripts.
  * @author pf
  */
 
public class ScriptManager implements Service
{
	/** système de log sur lequel écrire. */
	@SuppressWarnings("unused")
	private Log log;

	/** endroit ou lire la configuration du robot */
	@SuppressWarnings("unused")
	private Config config;

	private HookFactory hookFactory;

	/** Map contenant l'ensemble des scripts instanciés. Permet de retrouver un script via son nom */
	private AbstractScript[] instanciedScripts = new AbstractScript[ScriptNames.values().length];
	
	/**
	 * Instancie le scriptManager
	 * @param config the config endroit ou lire la configuration du robot
	 * @param log système de log sur lequel écrire
	 */
	private ScriptManager(Config config, Log log, HookFactory factory)
	{
		this.log = log;
		this.config = config;
		
		// exemple: instanciedScripts[ScriptNames.CLOSE_DOORS.ordinal()] = new CloseDoors(config, log, factory);
		instanciedScripts[ScriptNames.CLOSE_DOORS.ordinal()] = new CloseDoors(config, log, factory);
	}
	
	/**
	 * Renvois le script spécifié via son nom
	 *
	 * @param nom le nom du script voulu
	 * @return le script voulu
	 */
	public AbstractScript getScript(ScriptNames nom)
	{
		AbstractScript script = instanciedScripts[nom.ordinal()];
		return script;
	}
	
	/* (non-Javadoc)
	 * @see container.Service#updateConfig()
	 */
	@Override
	public void updateConfig() {}
}
