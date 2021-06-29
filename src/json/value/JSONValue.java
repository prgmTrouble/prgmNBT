package json.value;

import json.JSON;
import json.exception.JSONConversionException;
import util.string.outline.Segment;
import util.string.outline.ValueSegment;

/**
 * A typed {@linkplain JSON} value.
 * 
 * @author prgmTrouble
 * @author AzureTriple
 */
public abstract class JSONValue extends JSON {
    /**@return The {@linkplain ValueType} associated with this JSON value.*/
    public abstract ValueType type();
    
    public JSONValue() {super();}
    
    /**@see ValueType#convert(JSONValue,ValueType)*/
    public JSONValue convertTo(final ValueType target)
                               throws JSONConversionException {
        return ValueType.convert(this,target);
    }
    
    @Override public Segment toSegment() {return new ValueSegment(toSequence());}
    
    //TODO parsing
}