package json.value;

import json.JSON;
import json.exception.JSONConversionException;

public abstract class JSONValue extends JSON {
    public JSONValue() {super();}
    
    /**@return The {@linkplain ValueType} associated with this JSON value.*/
    public abstract ValueType type();
    
    /**@see ValueType#convert(JSONValue,ValueType)*/
    public JSONValue convertTo(final ValueType target)
                               throws JSONConversionException {
        return ValueType.convert(this,target);
    }
    
    //TODO parsing
}