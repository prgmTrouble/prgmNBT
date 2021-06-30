package json.value;

import json.exception.JSONConversionException;
import json.exception.JSONParsingException;
import util.string.Sequence;
import util.string.Sequence.SequenceIterator;
import util.string.outline.Segment;
import util.string.outline.ValueSegment;

/**
 * A {@linkplain JSONValue} representing a boolean.
 * 
 * @author prgmTrouble
 * @author AzureTriple
 */
public class JSONBool extends JSONValue {
    public static final ValueType TYPE = ValueType.BOOL;
    @Override public ValueType type() {return TYPE;}
    @Override
    public JSONValue convertTo(final ValueType target) throws JSONConversionException {
        return target == ValueType.NUMBER? new JSONNumber(value()? 1 : 0)
                                         : super.convertTo(target);
    }
    
    public static final Sequence TRUE,FALSE;
    static {
        final Sequence[] share = Sequence.shared(
            Boolean.toString(true).toCharArray(),
            Boolean.toString(false).toCharArray()
        );
        TRUE = share[0];
        FALSE = share[1];
    }
    private static final Segment TRUE_SEG = new ValueSegment(TRUE),
                                 FALSE_SEG = new ValueSegment(FALSE);
    
    private boolean value = false;
    
    /**Creates a boolean value of <code>false</code>.*/
    public JSONBool() {super();}
    /**Creates a boolean value.*/
    public JSONBool(final boolean value) {super(); value(value);}
    
    /**@return This boolean's value.*/
    public boolean value() {return value;}
    /**
     * @param value This boolean's value.
     * 
     * @return <code>this</code>
     */
    public JSONBool value(final boolean value) {this.value = value; return this;}
    
    @Override public Sequence toSequence() {return value()? TRUE : FALSE;}
    @Override public Segment toSegment() {return value()? TRUE_SEG : FALSE_SEG;}
    
    /**
     * @param i          A {@linkplain SequenceIterator} which points to the
     *                   position just before the boolean sequence.
     * @param terminator A character which marks the end of a structure.
     *                   <code>null</code> indicates the end of the sequence.
     * @param commas     <code>true</code> iff commas are allowed to terminate a
     *                   value.
     * 
     * @return The appropriate {@linkplain JSONBool}.
     * 
     * @throws JSONParsingException The iterator cannot find a valid boolean.
     * 
     * @implNote This function does not consider numeric values. These should be
     *           handled by {@linkplain JSONNumber}.
     */
    public static JSONBool parse(final SequenceIterator i,
                                 final Character terminator,
                                 final boolean commas)
                                 throws JSONParsingException {
        final boolean parity = switch(i.peek()) {
            case 't' -> true;
            case 'f' -> false;
            default -> throw new JSONParsingException(
                "Invalid boolean character '%c' ('\\u%04X')"
                .formatted(i.peek(),(int)i.peek()),i
            );
        };
        final SequenceIterator match = (parity? TRUE : FALSE).iterator();
        match.next();
        while(match.hasNext())
            if(match.next() != i.next())
                throw new JSONParsingException(
                    "Invalid boolean character '%c' ('\\u%04X')"
                    .formatted(i.peek(),(int)i.peek()),i
                );
        final Character c = i.nextNonWS();
        if(!(commas && c == ',') && c != terminator)
            throw new JSONParsingException("boolean",i,terminator,commas,c);
        return new JSONBool(parity);
    }
}