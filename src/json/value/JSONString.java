package json.value;

import json.exception.JSONException;
import json.exception.JSONParsingException;
import settings.Settings;
import util.container.NodeIterator;
import util.container.Stack;
import util.string.Sequence;
import util.string.Sequence.SequenceIterator;

/**
 * A {@linkplain JSONValue} holding a string.
 * 
 * @author prgmTrouble
 * @author AzureTriple
 */
public class JSONString extends JSONValue {
    public static final ValueType TYPE = ValueType.STRING;
    @Override public ValueType type() {return TYPE;}
    
    private Sequence value = Sequence.EMPTY;
    private boolean escapeUnicode = Settings.escapeUnicodeByDefault();
    
    /**Creates an empty string value.*/
    public JSONString() {super();}
    /**
     * Creates a string value.
     * 
     * @throws JSONException The value is <code>null</code> or otherwise invalid.
     */
    public JSONString(final Sequence value) throws JSONException {super(); value(value);}
    /**
     * Creates a string value.
     * 
     * @throws JSONException The value is <code>null</code> or otherwise invalid.
     */
    public JSONString(final char...value) throws JSONException {super(); value(value);}
    /**
     * Creates a string value.
     * 
     * @throws JSONException The value is <code>null</code> or otherwise invalid.
     */
    public JSONString(final String value) throws JSONException {super(); value(value);}
    
    /**
     * @return <code>true</code> iff values larger than <code>'\u007F'</code> will
     *         be converted into escaped unicode sequences.
     */
    public boolean escapeUnicode() {return escapeUnicode;}
    /**
     * @param escapeUnicode <code>true</code> iff values larger than <code>'\u007F'</code> will
     *         be converted into escaped unicode sequences.
     * 
     * @return <code>this</code>
     */
    public JSONString escapeUnicode(final boolean escapeUnicode) {
        this.escapeUnicode = escapeUnicode;
        return this;
    }
    
    /**@return This string's value.*/
    public Sequence value() {return value;}
    private static <V> V checkNN(final V v) throws JSONException {
        if(v == null) throw new JSONException("Cannot assign a null value.");
        return v;
    }
    private static final char toHexChar(int c) {return (char)((c &= 0xF) > 9? c - 10 + 'A' : ('0' + c));}
    private static Sequence eatSequence(final SequenceIterator i,
                                        final Character terminator,
                                        final boolean escapeUnicode,
                                        final boolean checkQuotes)
                                        throws JSONParsingException { //TODO test
        final Character wrapper = i.skipWS();
        if(wrapper == null) throw new JSONParsingException("Empty sequence",i);
        i.mark();
        if(checkQuotes && wrapper != '"')
            throw new JSONParsingException(
                "Invalid wrapper character '%c' ('\\u%04X')"
                .formatted(wrapper,(int)wrapper),i
            );
        if(!i.hasNext()) // Ensure that there are more characters.
            throw new JSONParsingException("Missing ending quote",i);
        final Stack<Integer> chars = new Stack<>(),indices = new Stack<>();
        {
            boolean escaped = false;
            int unicode = 0;
            char c = i.next();
            while(i.hasNext()) {
                // Eat unicode characters.
                if(unicode != 0) { // Skip 4 hex characters.
                    if(!Sequence.allowUnicodeHex(c))
                        throw new JSONParsingException(
                            "Invalid hexadecimal character '%c' ('\\u%04X')"
                            .formatted(c,(int)c),i
                        );
                    --unicode;
                    continue;
                }
                // Check for unicode.
                if(escaped && c == 'u') unicode = 4;
                
                if(escaped) { // Skip escaped character.
                    // Check allowed single-character escape sequences.
                    if(
                        unicode != 4 && !(
                            c == wrapper ||
                            Sequence.isJavaEscape(c)
                        )
                    ) throw new JSONParsingException(
                        "Invalid escape character '%c' ('\\u%04d')"
                        .formatted(c,(int)c),i
                    );
                    escaped = false;
                } else if(escapeUnicode && c > '\u007F') { // Check for non-ASCII.
                    chars.push((int)c);
                    indices.push(i.index());
                } else if(!(escaped = c == '\\') && checkQuotes && c == wrapper)
                    break; // Break on checked un-escaped quotes.
                // Ignore everything else.
                c = i.next(); // Should never return null because of while condition.
            }
        }
        boolean flag = false;
        if(wrapper == '"' && i.peek() == wrapper) i.next();
        else if(checkQuotes) throw new JSONParsingException("Missing ending quote",i);
        else flag = true;
        Sequence v = i.subSequence();
        if(escapeUnicode && chars.size() != 0) {
            // Buffer size is the number of characters in the original sequence, plus 5
            // times the number of added unicode sequences. This accounts for one
            // character removed, and 6 added in the "\\u####" expression.
            final char[] buf = new char[v.length() + chars.size() * 5];
            int cursor = buf.length,prev = v.length();
            for(
                final NodeIterator<Integer> j = indices.iterator(),
                c = chars.iterator();
                j.hasNext();
            ) {
                final int p = j.next();
                // Copy the next contiguous section of ASCII characters.
                v.subSequence(p + 1,prev).copyInto(buf,cursor -= prev - p - 1);
                {
                    // Convert the character into hexadecimal.
                    int t = c.next();
                    buf[--cursor] = toHexChar(t);
                    buf[--cursor] = toHexChar(t >>>= 4);
                    buf[--cursor] = toHexChar(t >>>= 4);
                    buf[--cursor] = toHexChar(t >>>  4);
                }
                // Add the prefix.
                buf[--cursor] = 'u';
                buf[--cursor] = '\\';
                // Update the last index.
                prev = p;
            }
            // Copy the remaining characters from the beginning.
            v.subSequence(0,prev).copyInto(buf,0);
            v = new Sequence(buf);
        }
        return flag? v.quoteAndEscape('"') : v;
    }
    /**Ensures that the input sequence is valid and unwraps if necessary.*/
    private static Sequence validate(Sequence value,final boolean escapeUnicode) throws JSONParsingException {
        final SequenceIterator i = value.iterator();
        value = eatSequence(i,null,escapeUnicode,false);
        if(i.hasNext()) throw new JSONParsingException("Trailing data found",i);
        return value.isWrappedIn('"')? value.unwrapAndUnescape()
                                     : value;
    }
    private JSONString internalSetV(final Sequence value) throws JSONException {
        this.value = validate(value,escapeUnicode);
        return this;
    }
    /**
     * @param value This string's value.
     * 
     * @return <code>this</code>
     * 
     * @throws JSONException The value is <code>null</code> or otherwise invalid.
     */
    public JSONString value(final Sequence value) throws JSONException {
        return internalSetV(checkNN(value));
    }
    /**
     * @param value This string's value.
     * 
     * @return <code>this</code>
     * 
     * @throws JSONException The value is <code>null</code> or otherwise invalid.
     */
    public JSONString value(final char...value) throws JSONException {
        return internalSetV(new Sequence(checkNN(value)));
    }
    /**
     * @param value This string's value.
     * 
     * @return <code>this</code>
     * 
     * @throws JSONException The value is <code>null</code> or otherwise invalid.
     */
    public JSONString value(final String value) throws JSONException {
        return internalSetV(new Sequence(checkNN(value)));
    }
    
    @Override public Sequence toSequence() {return value;}
    
    //TODO parsing (use eatSequence with checkQuotes true)
}