package json;

import java.io.File;
import java.io.IOException;
import json.exception.JSONParsingException;
import json.value.JSONValue;
import json.value.collection.JSONTag;
import util.string.Sequence;
import util.string.Sequence.SequenceFileIterator;
import util.string.Sequence.SequenceIterator;
import util.string.Stringifiable;

/**
 * A {@linkplain Stringifiable} data format using the ECMA-404 standard.
 * 
 * @author prgmTrouble
 * @author AzureTriple
 */
public abstract class JSON implements Stringifiable {
    /**Creates a new JSON.*/
    public JSON() {}
    
    @Override public String toString() {return toSequence().toString();}
    
    private static JSON parse(final SequenceIterator i) throws JSONParsingException {
        if(i.skipWS() == null)
            throw new JSONParsingException("Cannot parse empty sequence.");
        return i.peek() == '"'? JSONTag.parseUnknown(i)
                              : JSONValue.parseNotString(i,null,false);
    }
    /**
     * Parses an arbitrary JSON sequence.
     * 
     * @throws JSONParsingException The sequence is not a valid JSON structure.
     * @throws NullPointerException The input is <code>null</code>.
     */
    public static JSON parse(final Sequence s) throws JSONParsingException,
                                                      NullPointerException {
        if(s == null)
            throw new NullPointerException("Cannot parse null sequence.");
        return parse(s.iterator());
    }
    /**
     * Parses an arbitrary JSON sequence from a text file.
     * 
     * @throws JSONParsingException The sequence is not a valid JSON structure.
     * @throws IOException          The file is too large
     *                              <code>(f.length() >= 2^32)</code> or could not
     *                              be read.
     * @throws NullPointerException The input is <code>null</code>.
     */
    public static JSON parse(final File f) throws JSONParsingException,IOException,
                                                  NullPointerException {
        if(f == null)
            throw new NullPointerException("File is null.");
        return parse(new SequenceFileIterator(f));
    }
}