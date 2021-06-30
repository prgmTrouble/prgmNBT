package json.value;

import json.exception.JSONConversionException;
import json.exception.JSONParsingException;
import util.string.Sequence;
import util.string.Sequence.SequenceIterator;
import util.string.outline.Segment;
import util.string.outline.ValueSegment;

/**
 * A {@linkplain JSONValue} representing <code>null</code>.
 * 
 * @author prgmTrouble
 * @author AzureTriple
 */
public final class JSONNull extends JSONValue {
    public static final ValueType TYPE = ValueType.NULL;
    @Override public ValueType type() {return TYPE;}
    @Override
    public JSONValue convertTo(ValueType target) throws JSONConversionException {
        return super.convertTo(target == null? ValueType.NULL : target);
    }
    
    /**A shared instance of the <code>null</code> type.*/
    public static final JSONNull INSTANCE = new JSONNull();
    public static final Sequence NULL_SEQUENCE = new Sequence(String.valueOf((Object)null));
    private static final Segment SEGMENT = new ValueSegment(NULL_SEQUENCE);
    
    private JSONNull() {super();}
    
    @Override public Sequence toSequence() {return NULL_SEQUENCE;}
    @Override public Segment toSegment() {return SEGMENT;}
    
    /**
     * @param i A {@linkplain SequenceIterator} which points to the position at the
     *          first character.
     * 
     * @return {@linkplain #INSTANCE}
     * 
     * @throws JSONParsingException The iterator cannot find a valid
     *                              <code>null</code> sequence.
     */
    public static JSONNull parse(final SequenceIterator i,
                                 final Character terminator,
                                 final boolean commas)
                                 throws JSONParsingException {
        final SequenceIterator match = NULL_SEQUENCE.iterator();
        for(Character c = i.peek();match.hasNext();c = i.next())
            if(match.next() != c)
                throw new JSONParsingException(
                    "Invalid null character '%c' ('\\u%04X')"
                    .formatted(i.peek(),(int)i.peek()),i
                );
        final Character c = i.nextNonWS();
        if(!(commas && c == ',') && c != terminator)
            throw new JSONParsingException("null",i,terminator,commas,c);
        return INSTANCE;
    }
}