
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
import hook.types.HookFactory;
import utils.Config;
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

	/** Map contenant l'ensemble des scripts instanciés. Permet de retrouver un script via son nom */
	private AbstractScript[] instanciedScripts = new AbstractScript[ScriptNames.values().length];
	
	/**
	 * Instancie le scriptManager
	 * @param factory le générateur de hook a utiliser dans les scripts
	 * @param config the config endroit ou lire la configuration du robot
	 * @param log système de log sur lequel écrire
	 */
	private ScriptManager(HookFactory factory, Config config, Log log)
	{
		this.log = log;
		this.config = config;
		
		// exemple: instanciedScripts[ScriptNames.CLOSE_DOORS.ordinal()] = new CloseDoors(factory, config, log);
		instanciedScripts[ScriptNames.CATCH_BALLS.ordinal()] = new CatchBalls(factory, config, log);
		instanciedScripts[ScriptNames.DROP_BALLS.ordinal()] = new DropBalls(factory, config, log);
		instanciedScripts[ScriptNames.CATCH_MODULE.ordinal()] = new CatchModule(factory, config, log);
		instanciedScripts[ScriptNames.DROP_MODULE.ordinal()] = new DropModule(factory, config, log);
		instanciedScripts[ScriptNames.INITIALISE_ROBOT.ordinal()] = new InitialisationRobot(factory, config, log);
		instanciedScripts[ScriptNames.FULLSCRIPTED.ordinal()] = new FullScripted(factory, config, log);
		instanciedScripts[ScriptNames.SCRIPTED_GO_TO.ordinal()] = new ScriptedGoTo(factory, config, log);
		instanciedScripts[ScriptNames.SCRIPTED_GO_TO_CRATEREFOND.ordinal()] = new ScriptedGoTo_CratereFond(factory, config, log);
		instanciedScripts[ScriptNames.SCRIPTED_GO_TO_CRATERE_PRES_BASE.ordinal()] = new ScriptedGoTo_CraterePresBase(factory, config, log);
		instanciedScripts[ScriptNames.SCRIPTED_GO_TO_CRATERE_LIVRAISON_BOULES1.ordinal()] = new ScriptedGoTo_LivraisonBoules1(factory, config, log);
		instanciedScripts[ScriptNames.SCRIPTED_GO_TO_CRATERE_LIVRAISON_BOULES2.ordinal()] = new ScriptedGoTo_LivraisonBoules2(factory, config, log);
		instanciedScripts[ScriptNames.SCRIPTED_GO_TO_LIVRAISON_MODULEFOND.ordinal()] = new ScriptedGoTo_LivraisonModuleFond(factory, config, log);
		instanciedScripts[ScriptNames.SCRIPTED_GO_TO_MODULEFOND.ordinal()] = new ScriptedGoTo_ModuleFond(factory, config, log);
		instanciedScripts[ScriptNames.SCRIPTED_GO_TO_MODULE_PRES_BASE.ordinal()] = new ScriptedGoTo_ModulePresBase(factory, config, log);
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

	//LE HAUT NIVEAU C D PD
	public void updateConfig()
	{
	}

}
