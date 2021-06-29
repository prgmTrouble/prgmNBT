package json.value;

import json.exception.JSONConversionException;

/**
 * 
 * 
 * 
 * @author prgmTrouble
 */
public enum ValueType {
    NULL,
    BOOL,
    NUMBER,
    STRING,
    ARRAY,
    OBJECT;
    
    public final String name = name().toLowerCase();
    
    /**
     * @param value  Value to convert.
     * @param target Type of conversion result.
     * 
     * @return The converted value.
     */
    public static JSONValue convert(final JSONValue value,
                                    final ValueType target)
                                    throws JSONConversionException {
        //TODO
        return null;
    }
}