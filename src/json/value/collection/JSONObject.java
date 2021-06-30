package json.value.collection;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import json.exception.JSONException;
import json.exception.JSONParsingException;
import json.value.JSONNull;
import json.value.JSONString;
import json.value.JSONValue;
import json.value.ValueType;
import util.string.Joiner;
import util.string.Sequence;
import util.string.Sequence.SequenceFileIterator;
import util.string.Sequence.SequenceIterator;
import util.string.outline.WrappingSegment;

/**
 * A {@linkplain JSONCollection} which functions as a map.
 * 
 * @author prgmTrouble
 * @author AzureTriple
 */
public class JSONObject extends JSONCollection<JSONString,JSONTag> {
    private static final ValueType TYPE = ValueType.OBJECT;
    @Override public ValueType type() {return TYPE;}
    
    public static final char OPEN = '{',CLOSE = '}';
    @Override protected Joiner getJoiner() {return new Joiner(OPEN,CLOSE);}
    
    private final TreeMap<JSONString,JSONValue> values = new TreeMap<>();
    
    /**Creates an empty object.*/
    public JSONObject() {super();}
    
    private static class JSONObjectIterator implements Iterator<JSONTag> {
        private final Iterator<Entry<JSONString,JSONValue>> i;
        private JSONObjectIterator(final Iterator<Entry<JSONString,JSONValue>> i) {this.i = i;}
        @Override public boolean hasNext() {return i.hasNext();}
        @Override
        public JSONTag next() {
            final Entry<JSONString,JSONValue> e = i.next();
            return new JSONTag(e.getKey(),e.getValue()); // NullPointerException should never be thrown.
        }
    }
    @Override public Iterator<JSONTag> iterator() {return new JSONObjectIterator(values.entrySet().iterator());}
    @Override protected Iterable<JSONValue> values() {return values.values();}
    
    /**@throws JSONException The input does not have a valid key.*/
    @Override
    public JSONObject set(final JSONString key,final JSONValue value) throws JSONException {
        if(key == null) throw new JSONException("Cannot set a null key.");
        values.put(key,value == null? JSONNull.INSTANCE : value);
        return this;
    }
    /**
     * Maps the value to the key.
     * 
     * @throws JSONException The input does not have a valid key.
     */
    public JSONObject set(final String key,final JSONValue value) throws JSONException {
        values.put(new JSONString(key),value == null? JSONNull.INSTANCE : value);
        return this;
    }
    
    /**@throws NullPointerException The input key is <code>null</code>.*/
    @Override public JSONValue get(final JSONString key) throws NullPointerException {return values.get(key);}
    /**
     * Gets an value mapped to the key.
     * 
     * @throws JSONException The input is not a valid string.
     */
    public JSONValue get(final String key) throws JSONException {return get(new JSONString(key));}
    
    /**@throws NullPointerException The key is <code>null</code>.*/
    @Override public JSONValue remove(final JSONString key) throws NullPointerException {return values.remove(key);}
    /**
     * Removes and returns the value mapped to the key.
     * 
     * @throws JSONException The input is not a valid string.
     */
    public JSONValue remove(final String key) throws JSONException {return remove(new JSONString(key));}
    
    @Override
    protected WrappingSegment getWrapper() {
        final Sequence[] wrapper = Sequence.shared(new char[] {OPEN,CLOSE},1);
        return new WrappingSegment(wrapper[0],wrapper[1]); //TODO customization?
    }
    
    /**
     * @param i A {@linkplain SequenceIterator} which points to the position at the
     *          opening brace.
     * 
     * @return An {@linkplain JSONObject}.
     * 
     * @throws JSONParsingException The iterator cannot find a valid object.
     */
    public static JSONObject parse(final SequenceIterator i) throws JSONParsingException {
        final JSONObject out = new JSONObject();
        if(testEmpty(i,CLOSE)) return out;
        while(i.hasNext()) {
            // Get key.
            final JSONString key;
            try {key = JSONTag.parseKey(i);}
            catch(final JSONParsingException e) {
                throw new JSONParsingException("Error while parsing key in object",i,e);
            }
            // Get value.
            final JSONValue value;
            try {value = JSONTag.parseValue(i);}
            catch(final JSONParsingException e) {
                throw new JSONParsingException(
                    "Error while parsing value for key %s in object"
                    .formatted(key),i,e
                );
            }
            // Bypass constructor checks.
            out.values.put(key,value);
            // Check for close.
            if(i.peek() == CLOSE) {
                // Advance past close.
                i.next();
                return out;
            }
            // Advance past the comma.
            i.next();
        }
        // If hasNext() fails, then no closing character was found.
        throw new JSONParsingException("Missing closing character '%c'".formatted(CLOSE),i);
    }
    /**
     * @param i          A {@linkplain SequenceIterator} which points to the
     *                   position just before the opening brace.
     * @param terminator A character which marks the end of a structure.
     *                   <code>null</code> indicates the end of the sequence.
     * @param commas     <code>true</code> iff commas are allowed to terminate a
     *                   value.
     * 
     * @return The appropriate {@linkplain JSONObject}.
     * 
     * @throws JSONParsingException The iterator cannot find a valid object.
     */
    public static JSONObject parse(final SequenceIterator i,
                                  final Character terminator,
                                  final boolean commas)
                                  throws JSONParsingException {
        final JSONObject out = parse(i);
        final Character c = i.skipWS();
        if(!(commas && c == ',') && c != terminator)
            throw new JSONParsingException("object",i,terminator,commas,c);
        return out;
    }
    
    /**
     * Reads an object from a file.
     * 
     * @throws JSONException The file could not be read or the object is invalid.
     */
    public static JSONObject read(final File f) throws JSONException {
        try(final SequenceFileIterator i = new SequenceFileIterator(f)) {return parse(i);}
        catch(IOException|UncheckedIOException e) {throw new JSONException("Failed to read JSON from file.",e);}
    }
    /**
     * Writes an object to a file.
     * 
     * @throws IOException       The file could not be written.
     * @throws SecurityException The security manager denied access.
     */
    public void write(final File f) throws IOException,SecurityException {
        try(final FileOutputStream os = new FileOutputStream(f)) {
            final ByteBuffer bb;
            {
                System.gc();
                final CharBuffer cb = CharBuffer.wrap(toSegment().concat());
                (bb = ByteBuffer.allocateDirect(cb.capacity())).asCharBuffer().put(cb);
            }
            System.gc();
            final FileChannel fc = os.getChannel();
            fc.lock();
            fc.write(bb);
        }
        System.gc();
    }
}