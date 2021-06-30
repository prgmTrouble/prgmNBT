package json.value.collection;

import json.JSON;
import json.exception.JSONParsingException;
import json.value.JSONNull;
import json.value.JSONString;
import json.value.JSONValue;
import util.string.Joiner;
import util.string.Sequence;
import util.string.Sequence.SequenceIterator;
import util.string.outline.JoiningSegment;
import util.string.outline.Segment;

/**
 * An {@linkplain JSON} representing a key-value pair.
 * 
 * @author prgmTrouble
 * @author AzureTriple
 */
public class JSONTag extends JSON {
    public static final char SEPARATOR = ':';
    
    /**This tag's key. This should not be modifiable.*/
    private final JSONString key;
    /**The {@linkplain JSONValue} held by this object.*/
    private JSONValue value = JSONNull.INSTANCE;
    
    /**
     * Creates a tag with a <code>null</code> value.
     * 
     * @throws NullPointerException The key is <code>null</code>.
     */
    public JSONTag(final JSONString key) throws NullPointerException {
        if((this.key = key) == null)
            throw new NullPointerException("Cannot set a null key.");
    }
    /**
     * Creates a tag.
     * 
     * @throws NullPointerException The key is <code>null</code>.
     */
    public JSONTag(final JSONString key,final JSONValue value) throws NullPointerException {this(key); value(value);}
    
    /**@return This tag's value.*/
    public JSONValue value() {return value;}
    /**
     * @param value This tag's value.
     * 
     * @return <code>this</code>
     */
    public JSONTag value(final JSONValue value) {
        this.value = value == null? JSONNull.INSTANCE : value;
        return this;
    }
    
    @Override public Sequence toSequence() {return value.appendTo(key.appendTo(new Joiner(':'))).concat();}
    @Override
    public Segment toSegment() {//TODO customization?
        return new JoiningSegment(new Sequence(SEPARATOR))
                            .push(key.toSegment())
                            .push(value.toSegment());
    }
    
    /**
     * Strictly parses a tag.
     * 
     * @throws JSONParsingException Either the key or value could not be parsed.
     */
    public static JSONTag parse(final SequenceIterator i) throws JSONParsingException {
        return new JSONTag(parseKey(i),parseValue(i));
    }
    /**
     * Attempts to parse a tag, returning the key if the value could not be parsed.
     * 
     * @throws JSONParsingException The key could not be parsed.
     */
    public static JSON parseUnknown(final SequenceIterator i) throws JSONParsingException {
        final JSONString key = new JSONString(i);
        if(i.skipWS() == null) return key;
        ensureValue(i);
        final JSONValue value = parseValue(i);
        return new JSONTag(key,value);
    }
    private static void ensureValue(final SequenceIterator i) throws JSONParsingException {
        // The key must be followed by a separator character to be a tag.
        if(i.skipWS() != SEPARATOR)
            throw new JSONParsingException(
                "Missing separator character ('%c')"
                .formatted(SEPARATOR),i
            );
        // Separator must be followed by something else.
        if(i.nextNonWS() == null) throw new JSONParsingException("Missing value",i);
    }
    
    /**
     * Parses a key.
     * 
     * @throws JSONParsingException The key could not be parsed.
     */
    public static JSONString parseKey(final SequenceIterator i) throws JSONParsingException {
        final JSONString key;
        try {key = new JSONString(i);} // This method already skips whitespace.
        catch(final JSONParsingException e) {
            throw new JSONParsingException(
                "Error while parsing a key for a tag.",
                e
            );
        }
        ensureValue(i);
        return key;
    }
    /**
     * Parses a value.
     * 
     * @throws JSONParsingException The value could not be parsed.
     */
    protected static JSONValue parseValue(final SequenceIterator i) throws JSONParsingException {
        try {return JSONValue.parse(i,JSONObject.CLOSE,true);}
        catch(final JSONParsingException e) {
            // Since a separator was found, any exceptions indicate that there
            // cannot be a valid value.
            throw new JSONParsingException(
                "Error while parsing tag value",
                i,e
            );
        }
    }
}