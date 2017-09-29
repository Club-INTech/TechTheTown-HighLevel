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

    /** Configure les hooks en paramètres (envoie toute les infos au LL) */
    public void configureHook(HookNames... hooks) throws Exception{

        String serialOrder = "";

        for(HookNames hook:hooks){
            if (hook.getOrder() instanceof Speed){
                serialOrder = "ctrv " + ((Speed) hook.getOrder()).translationSpeed + " " + ((Speed) hook.getOrder()).rotationSpeed;
            }
            else if (hook.getOrder() instanceof ActuatorOrder){
                serialOrder = ((ActuatorOrder) hook.getOrder()).getSerialOrder();
            }else{
                log.critical("Mauvaise enum; doit implémenter MotionOrder");
            }
            eth.configureHook(hook.getId(), hook.getPosition(), serialOrder);
            configuredHook.add(hook);
        }
    }

    @Override
    public void updateConfig(){}
}
