package json.exception;

import static util.ExceptionUtils.fmtParsing;
import static util.ExceptionUtils.fmtParsingTerminator;

import util.string.Sequence;
import util.string.Sequence.SequenceIterator;

public class JSONParsingException extends JSONException {
    private static final long serialVersionUID = 1L;
    
    public JSONParsingException() {super();}
    public JSONParsingException(final String message) {super(message);}
    public JSONParsingException(final String message,final Throwable t) {super(message,t);}
    
    /**
     * A constructor meant for exceptions during parsing.
     * 
     * @param message A description of the error.
     * @param index   The location of the error.
     * @param data    The sequence which caused the error.
     */
    public JSONParsingException(final String message,final int index,final Sequence data) {
        super(fmtParsing(message,index,data));
    }
    /**@see #JSONParsingException(String,int,Sequence)*/
    public JSONParsingException(final String message,final int index,final Sequence data,final Throwable t) {
        super(fmtParsing(message,index,data),t);
    }
    /**
     * A constructor meant for exceptions during parsing.
     * 
     * @param message A description of the error.
     * @param data    The {@linkplain SequenceIterator} which caused the error.
     */
    public JSONParsingException(final String message,final SequenceIterator data) {
        this(message,data.index(),data.getParent());
    }
    /**@see #JSONParsingException(String,SequenceIterator)*/
    public JSONParsingException(final String message,final SequenceIterator data,final Throwable t) {
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
    public JSONParsingException(final String type,
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
    public JSONParsingException(final String type,
                                final SequenceIterator data,
                                final Character terminator,
                                final boolean commas,
                                final Character c) {
        super(fmtParsingTerminator(type,data.index(),data.getParent(),terminator,commas,c));
    }
}