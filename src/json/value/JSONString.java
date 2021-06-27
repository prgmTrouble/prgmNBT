package json.value;

import json.exception.JSONException;
import json.exception.JSONParsingException;
import settings.Settings;
import util.container.NodeIterator;
import util.container.Stack;
import util.string.Sequence;
import util.string.Sequence.SequenceIterator;
import util.string.outline.Segment;

public class JSONString extends JSONValue {
    public static final ValueType TYPE = ValueType.STRING;
    
    private Sequence value = Sequence.EMPTY;
    private boolean escapeUnicode = Settings.escapeUnicodeByDefault();
    
    public JSONString() {super();}
    public JSONString(final Sequence value) throws JSONException {super(); value(value);}
    public JSONString(final char...value) throws JSONException {super(); value(value);}
    public JSONString(final String value) throws JSONException {super(); value(value);}
    
    public boolean escapeUnicode() {return escapeUnicode;}
    public JSONString escapeUnicode(final boolean escapeUnicode) {
        this.escapeUnicode = escapeUnicode;
        return this;
    }
    
    public Sequence value() {return value;}
    private static <V> V checkNN(final V v) {
        if(v == null) throw new NullPointerException("Cannot assign a null value.");
        return v;
    }
    private static final char toHexChar(int c) {return (char)((c &= 0xF) > 9? c - 10 + 'A' : ('0' + c));}
    private static Sequence eatSequence(final SequenceIterator i,
                                        final Character terminator,
                                        final boolean escapeUnicode,
                                        final boolean checkQuotes)
                                        throws JSONParsingException {
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
        // Ensure there's a terminating quote.
        if(checkQuotes && i.peek() != wrapper)
            throw new JSONParsingException("Missing ending quote",i);
        // Advance past the quote.
        i.next();
        final Sequence value = i.subSequence();
        // Return immediately if none of the characters were translated into unicode
        // escape sequences.
        if(!escapeUnicode || chars.size() == 0) return value;
        
        // Buffer size is the number of characters in the original sequence, plus 5
        // times the number of added unicode sequences. This accounts for one
        // character removed, and 6 added in the "\\u####" expression.
        final char[] buf = new char[value.length() + chars.size() * 5]; //TODO quotes
        int cursor = buf.length,prev = value.length();
        for(
            final NodeIterator<Integer> j = indices.iterator(),
            c = chars.iterator();
            j.hasNext();
        ) {
            final int p = j.next();
            // Copy the next contiguous section of ASCII characters.
            value.subSequence(p + 1,prev).copyInto(buf,cursor -= prev - p - 1);
            {
                // Convert the character into hexadecimal.
                int v = c.next();
                buf[--cursor] = toHexChar(v);
                buf[--cursor] = toHexChar(v >>>= 4);
                buf[--cursor] = toHexChar(v >>>= 4);
                buf[--cursor] = toHexChar(v >>>  4);
            }
            // Add the prefix.
            buf[--cursor] = 'u';
            buf[--cursor] = '\\';
            // Update the last index.
            prev = p;
        }
        // Copy the remaining characters from the beginning.
        value.subSequence(0,prev).copyInto(buf,0);
        return new Sequence(buf);
    }
    /**Ensures that the input sequence is valid and unwraps if necessary.*/
    private static Sequence validate(Sequence value,final boolean escapeUnicode) throws JSONParsingException {
        final SequenceIterator i = value.iterator();
        value = eatSequence(i,null,escapeUnicode,false);
        if(i.hasNext()) throw new JSONParsingException("Trailing data found",i);
        return value.isWrappedIn('"')? value.unwrapAndUnescape()
                                     : value;
    }
    private JSONString internalSetV(final Sequence value) throws JSONParsingException {
        this.value = validate(value,escapeUnicode);
        return this;
    }
    public JSONString value(final Sequence value) throws JSONParsingException {
        return internalSetV(checkNN(value));
    }
    public JSONString value(final char...value) throws JSONParsingException {
        return internalSetV(new Sequence(checkNN(value)));
    }
    public JSONString value(final String value) throws JSONParsingException {
        return internalSetV(new Sequence(checkNN(value)));
    }
    
    @Override
    public Sequence toSequence() {
        return null;
    }
    
    @Override
    public Segment toSegment() {
        return null;
    }
    
    @Override public ValueType type() {return TYPE;}
    
    //TODO parsing (use eatSequence with checkQuotes true)
}


































