package nbt.exception;

import util.string.Sequence;
import util.string.Sequence.SequenceIterator;

/**
 * An exception class which is thrown during operations on NBT objects.
 * 
 * @author prgmTrouble 
 * @author AzureTriple
 */
public class NBTParsingException extends NBTException {
    private static final long serialVersionUID = 1L;
    private static final int MAX_SHOWN_CHARS = 50;
    private static String fmtParsing(final String message,final int index,final Sequence data) {
        return "%s at position %d: %s <-".formatted(
            message,index,
            (index > MAX_SHOWN_CHARS? "..." : "") +
            data.subSequence(
                Math.min(Math.max(index - MAX_SHOWN_CHARS,0),data.length()),
                Math.min(Math.max(index + 1,0),data.length())
            )
        );
    }
    private static String fmtParsingTerminator(final String type,
                                               final int index,
                                               final Sequence data,
                                               final Character terminator,
                                               final boolean commas,
                                               final Character c) {
        return fmtParsing(
            "Missing %s%s in %s (was '%s')".formatted(
                commas? "comma or "
                      : "",
                terminator == null? "end of sequence"
                                  : "closing character '%c'".formatted(terminator),
                type,c == null? "null":c
            ),
            index,data
        ); 
    }
    
    public NBTParsingException() {super(version(null));}
    public NBTParsingException(final String message) {super(version(message));}
    public NBTParsingException(final String message,final Throwable t) {super(version(message),t);}
    
    /**
     * A constructor meant for exceptions during parsing.
     * 
     * @param message A description of the error.
     * @param index   The location of the error.
     * @param data    The sequence which caused the error.
     */
    public NBTParsingException(final String message,final int index,final Sequence data) {
        super(fmtParsing(message,index,data));
    }
    /**@see #NBTParsingException(String,int,Sequence)*/
    public NBTParsingException(final String message,final int index,final Sequence data,final Throwable t) {
        super(fmtParsing(message,index,data),t);
    }
    /**
     * A constructor meant for exceptions during parsing.
     * 
     * @param message A description of the error.
     * @param data    The {@linkplain SequenceIterator} which caused the error.
     */
    public NBTParsingException(final String message,final SequenceIterator data) {
        this(message,data.index(),data.getParent());
    }
    /**@see #NBTParsingException(String,SequenceIterator)*/
    public NBTParsingException(final String message,final SequenceIterator data,final Throwable t) {
        this(message,data.index(),data.getParent(),t);
    }
    
    /**
     * A constructor meant for exceptions during parsing related to an invalid end
     * of value.
     * 
     * @param index      The location of the error.
     * @param data       The sequence which caused the error.
     * @param terminator The character expected to end the value.
     * @param commas     <code>true</code> iff commas are allowed to end the value.
     */
    public NBTParsingException(final String type,
                               final int index,
                               final Sequence data,
                               final Character terminator,
                               final boolean commas,
                               final Character c) {
        super(fmtParsingTerminator(type,index,data,terminator,commas,c));
    }
    /**
     * A constructor meant for exceptions during parsing related to an invalid end
     * of value.
     * 
     * @param data       The {@linkplain SequenceIterator} which caused the error.
     * @param terminator The character expected to end the value.
     * @param commas     <code>true</code> iff commas are allowed to end the value.
     */
    public NBTParsingException(final String type,
                               final SequenceIterator data,
                               final Character terminator,
                               final boolean commas,
                               final Character c) {
        super(fmtParsingTerminator(type,data.index(),data.getParent(),terminator,commas,c));
    }
}
























