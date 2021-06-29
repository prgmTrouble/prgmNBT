package nbt.exception;

import static util.ExceptionUtils.fmtParsing;
import static util.ExceptionUtils.fmtParsingTerminator;

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