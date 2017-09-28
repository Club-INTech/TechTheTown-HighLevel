package enums;

/*
 * Copyright (C) 2013-2017 Pierre-François Gimenez
 * Distributed under the MIT License.
 */

import pfg.config.ConfigInfo;

/**
 * Configuration du robot : la valeur est celle par défaut; si la variable figure dans le .ini,
 * elle est override.
 * @author Pierre-François Gimenez
 */

public enum ConfigInfoRobot implements ConfigInfo
{
    /** Constantes (rarement modifiées) */
    TABLE_X(3000),
    TABLE_Y(2000),
    TEMPS_MATCH(100),
    ETH_DELAY(50),

    /** Paramètres log */
    PRINT_LOG(true),
    SAVE_LOG(true),

    /** Dimensions du robot */
    ROBOT_LENGTH(300),
    ROBOT_WIDTH(300),
    ROBOT_RADIUS(424),
    ENNEMY_RADIUS(400),

    /** Paramètres obstacles */
    PEREMP_OBST(5000),

    /** Paramètres match !*/
    COULEUR("vert"),
    C_DES_FOUS_EN_FACE(false),

    /** Paramètres capteurs */
    /** Paramètres de dégagement du robot */
    ;

    private Object defaultValue;

    /**
     * Constructor with some default value
     * @param defaultValue
     */
    private ConfigInfoRobot(Object defaultValue)
    {
        this.defaultValue = defaultValue;
    }

    /**
     * Just a getter
     */
    @Override
    public Object getDefaultValue()
    {
        return defaultValue;
    }

    /**
     * The toString() method is already adapted
     */
}

