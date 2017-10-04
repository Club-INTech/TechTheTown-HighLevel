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

package hook;

import container.Service;
import enums.ActuatorOrder;
import enums.Speed;
import pfg.config.Config;
import robot.EthWrapper;
import utils.Log;
import java.util.ArrayList;

/**
 * Classe permettant de gérer les hooks via une enum : pour créer un hook, il suffit de l'ajouter dans l'enum HookNames
 * Les hooks sont configurés (=envoyés au LL) via la méthode configureHook, a appelé en début de match ou de script
 */
public class HookFactory implements Service {

    /** Config */
    private Config config;

    /** Log */
    private Log log;

    /** Wrapper */
    private EthWrapper eth;

    /** Liste des Hooks */
    private ArrayList<HookNames> configuredHook = new ArrayList<HookNames>();

    /** Constructeur */
    public HookFactory (EthWrapper eth, Config config, Log log){
        this.eth=eth;
        this.config=config;
        this.log=log;
    }

    /**
     * Configure les hooks en paramètres (envoie toute les infos au LL)
     */
    public void configureHook(HookNames... hooks) {

        String serialOrder;
        for(HookNames hook:hooks){

            if (hook.getOrder() instanceof Speed){
                serialOrder = "ctrv " + ((Speed) hook.getOrder()).translationSpeed + " " + (float) ((Speed) hook.getOrder()).rotationSpeed;
            }
            else if (hook.getOrder() instanceof ActuatorOrder){
                serialOrder = ((ActuatorOrder) hook.getOrder()).getSerialOrder();
            }else{
                log.critical("Mauvaise enum, la méthode doit implémenter MotionOrder");
                break;
            }

            if (configuredHook.contains(hook)){
                log.warning("Hook déjà configuré : on ne fait rien");
                break;
            }
            eth.configureHook(hook.getId(), hook.getPosition(), hook.getTolerency(), serialOrder);
            log.debug("Hook " + hook.getDeclaringClass() + " : Configuré");
            configuredHook.add(hook);
        }
    }

    /**
     * Active les hooks en paramètres
     * Balance un WARNING si le hook n'a pas été configuré (et ne fait rien du coup...)
     */
    public void enableHook(HookNames... hooks){
        for(HookNames hook:hooks){
            if (!configuredHook.contains(hook)){
                log.warning("Hook " + hook.getDeclaringClass().getName() + " : Non configuré ! Ne peut etre activé");
                break;
            }
            eth.enableHook(hook);
            log.debug("Hook " + hook.getDeclaringClass().getName() + " : Activé");
        }
    }

    /**
     * Desactive les hooks en paramètres
     * Balance un WARNING si le hook n'a pas été configuré
     */
    public void disableHook(HookNames... hooks){
        for(HookNames hook:hooks){
            if(!configuredHook.contains(hook)){
                log.warning("Hook " + hook.getDeclaringClass().getName() + " : Non configuré ! Ne peut etre désactivé");
                break;
            }
            eth.disableHook(hook);
            log.debug("Hook " + hook.getDeclaringClass().getName() + " : Désactivé");
        }
    }

    /**
     * Active tous les hooks configurés
     */
    public void enableConfiguredHook(){
        for(HookNames hook:configuredHook){
            eth.enableHook(hook);
            log.debug("Hook " + hook.getDeclaringClass().getName() + " : Activé");
        }
    }

    /**
     * Désactive tous les hooks configurés
     */
    public void disableConfiguredHook(){
        for(HookNames hook:configuredHook){
            eth.disableHook(hook);
            log.debug("Hook " + hook.getDeclaringClass().getName() + " : Désactivé");
        }
    }

    @Override
    public void updateConfig(){}
}