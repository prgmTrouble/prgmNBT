package json.value;

import json.JSON;
import json.exception.JSONConversionException;
import json.exception.JSONException;
import json.exception.JSONParsingException;
import json.value.collection.JSONArray;
import json.value.collection.JSONObject;
import util.string.Sequence.SequenceIterator;
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
        if(target == type()) return this;
        if(target == ValueType.STRING) {
            try {return new JSONString(toSequence());}
            catch(final JSONException e) {} // Should never throw
        }
        throw new JSONConversionException(type(),target);
    }
    
    @Override public Segment toSegment() {return new ValueSegment(toSequence());}
    
    /**
     * Uses the iterator to parse the next value. The iterator's position following
     * this call will be after the last character of the parsed value.
     * 
     * @param i           A {@linkplain SequenceIterator} which points to the
     *                    position just before the value's sequence.
     * @param terminators A character which marks the end of a structure.
     *                    <code>null</code> indicates the end of the sequence.
     * @param commas      <code>true</code> iff commas are allowed to terminate a
     *                    value.
     * 
     * @return The appropriate {@linkplain JSONValue}.
     * 
     * @throws JSONParsingException The iterator cannot find a valid value.
     */
    public static JSONValue parse(final SequenceIterator i,
                                  final Character terminator,
                                  final boolean commas)
                                  throws JSONParsingException {
        if(i.skipWS() == null)
            throw new JSONParsingException("Cannot parse an empty value.");
        final JSONValue v = switch(i.peek()) {
            case 'n' -> JSONNull.parse(i,terminator,commas);
            case 't','f' -> JSONBool.parse(i,terminator,commas);
            case '"' -> JSONString.parse(i,terminator,commas);
            case '{' -> JSONObject.parse(i,terminator,commas);
            case '[' -> JSONArray.parse(i,terminator,commas);
            case '+','-','.','0','1','2','3',
                 '4','5','6','7','8','9' -> JSONNumber.parse(i,terminator,commas);
            default -> throw new JSONParsingException(
                "Invalid character '%c' ('\\u%04X')"
                .formatted(i.peek()),i
            );
        };
        final Character c = i.skipWS();
        if(!(commas && c == ',') && c != terminator)
            throw new JSONParsingException("value",i,terminator,commas,c);
        return v;
    }
    /**
     * Uses the iterator to parse the next non-string value. The iterator's position
     * following this call will be after the last character of the parsed value.
     * 
     * @param i           A {@linkplain SequenceIterator} which points to the
     *                    position just before the value's sequence.
     * @param terminators A character which marks the end of a structure.
     *                    <code>null</code> indicates the end of the sequence.
     * @param commas      <code>true</code> iff commas are allowed to terminate a
     *                    value.
     * 
     * @return The appropriate {@linkplain JSONValue}.
     * 
     * @throws JSONParsingException The iterator cannot find a valid value.
     */
    public static JSONValue parseNotString(final SequenceIterator i,
                                           final Character terminator,
                                           final boolean commas)
                                           throws JSONParsingException {
        if(i.skipWS() == null)
            throw new JSONParsingException("Cannot parse an empty value.");
        final JSONValue v = switch(i.peek()) {
            case 'n' -> JSONNull.parse(i,terminator,commas);
            case 't','f' -> JSONBool.parse(i,terminator,commas);
            case '{' -> JSONObject.parse(i,terminator,commas);
            case '[' -> JSONArray.parse(i,terminator,commas);
            case '+','-','.','0','1','2','3',
                 '4','5','6','7','8','9' -> JSONNumber.parse(i,terminator,commas);
            case '"' -> throw new JSONParsingException("Invalid string",i);
            default -> throw new JSONParsingException(
                "Invalid character '%c' ('\\u%04X')"
                .formatted(i.peek()),i
            );
        };
        final Character c = i.skipWS();
        if(!(commas && c == ',') && c != terminator)
            throw new JSONParsingException("value",i,terminator,commas,c);
        return v;
    }
}