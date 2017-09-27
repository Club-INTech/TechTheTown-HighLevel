package enums;

import pfg.config.ConfigInfo;

/**
 * Enum de config pout le JUnit_config
 * @author rem
 */
public enum ConfigInfoJUnit implements ConfigInfo
{
    RANDOM_INT(50),
    RANDOM_DOUBLE(5.22),
    RANDOM_STRING("suus"),
    RANDOM_BOOL(true),
    ;

    private Object defaultValue;

    /**
     * Constructor with some default value
     * @param defaultValue
     */
    private ConfigInfoJUnit(Object defaultValue)
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
