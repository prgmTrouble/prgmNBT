package json.value;

import json.exception.JSONConversionException;

/**
 * An enumeration of value types. In addition to identification, these also help
 * with type conversions.
 * 
 * @author prgmTrouble
 * @author AzureTriple
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
        return (value == null? JSONNull.INSTANCE : value)
            .convertTo(target == null? NULL : target);
    }
}