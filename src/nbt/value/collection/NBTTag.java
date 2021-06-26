package nbt.value.collection;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import nbt.NBT;
import nbt.exception.NBTException;
import nbt.exception.NBTParsingException;
import nbt.value.NBTString;
import nbt.value.NBTValue;
import nbt.value.ValueType;
import settings.Version;
import util.string.Joiner;
import util.string.Sequence;
import util.string.Sequence.SequenceIterator;
import util.string.outline.JoiningSegment;
import util.string.outline.Segment;

public class NBTTag extends NBT {
    public static final char SEPARATOR = ':';
    
    /**
     * Causes the parser to think that quotes in the key are a part of the literal
     * string.
     */
    public static final boolean THE_WILD_WEST = Version.isBefore(Version.v17w16a);
    
    /**The {@linkplain NBTValue} held by this object.*/
    public NBTValue value;
    /**This tag's key. This should not be modifiable.*/
    protected final NBTString key;
    
    /**Constructs a tag.*/
    public NBTTag(final NBTString key,final NBTValue value) {
        super((THE_WILD_WEST || key.minimal) && value.minimal);
        this.key = key; this.value = value;
        if(THE_WILD_WEST) this.key.minimal = true;
    }
    /**Reads a tag.*/
    public NBTTag(final DataInput in,final byte id) throws IOException,NBTException {
        final ValueType t = ValueType.getType(id);
        if(t == null)
            throw new NBTException(
                "Unknown type %d.".formatted(id)
            );
        key = new NBTString(in);
        value = t.read(in);
    }
    /**Writes this tag.*/
    @Override
    public void write(final DataOutput out) throws IOException {
        out.write(value.type().id);
        key.write(out);
        value.write(out);
    }
    
    @Override public boolean isDefault() {return value.isDefault();}
    
    @Override
    public void setDeepMinimal(final boolean minimal) {
        super.setDeepMinimal(minimal);
        key.setDeepMinimal(THE_WILD_WEST || minimal);
        value.setDeepMinimal(minimal);
    }
    
    @Override public Sequence toSequence() {return value.appendTo(key.appendTo(new Joiner(':'))).concat();}
    @Override
    public Segment toSegment() {//TODO customization?
        return new JoiningSegment(new Sequence(SEPARATOR))
                            .push(key.toSegment())
                            .push(value.toSegment());
    }
    
    /**Strictly parses a key.*/
    public static NBT parse(final SequenceIterator i) throws NBTParsingException {
        return new NBTTag(parseKey(i),parseValue(i));
    }
    
    public static NBTString parseKey(final SequenceIterator i) throws NBTParsingException {
        if(THE_WILD_WEST) {
            // I hate this as much as you do.
            class Key extends NBTString {
                Key(final SequenceIterator i) throws NBTParsingException {
                    super();
                    if(i.skipWS() != null) {
                        final int start = i.index();
                        int end = start;
                        boolean inQuote;
                        {
                            final char c = i.peek();
                            if(c == SEPARATOR) {
                                value = i.getParent().subSequence(start,end);
                                i.nextNonWS();
                                return;
                            }
                            ++end;
                            inQuote = isStringWrapper(c);
                            if(!inQuote && (Sequence.mapToClose(c) != null || Sequence.isClose(c)))
                                throw new NBTParsingException("Invalid character in key",i);
                        }
                        while(i.hasNext()) {
                            final Character c = i.nextNonWS();
                            if(c == null) break;
                            if(!inQuote) {
                                if(c == SEPARATOR) {
                                    value = i.getParent().subSequence(start,end);
                                    i.nextNonWS();
                                    return;
                                }
                                if(Sequence.mapToClose(c) != null || Sequence.isClose(c))
                                    throw new NBTParsingException("Invalid character '%c' in key".formatted(c),i);
                            }
                            end = i.index() + 1;
                            if(c == '\\') {
                                if(!i.hasNext()) break;
                                final char nc = i.next();
                                if(!inQuote && isStringWrapper(nc))
                                    throw new NBTParsingException("Invalid escaped quote in key",i);
                                ++end;
                            } else if(isStringWrapper(c)) inQuote ^= true;
                        }
                    }
                    throw new NBTParsingException("Invalid key",i);
                }
                @Override public Sequence toSequence() {return value;}
                @Override public NBTString setValue(final Sequence value) throws NBTParsingException {return this;}
                @Override public NBTString setDefault(final Sequence value) throws NBTParsingException {return this;}
            }
            return new Key(i);
        }
        final NBTString key;
        try {key = new NBTString(i);} // This method already skips whitespace.
        catch(final NBTParsingException e) {
            throw new NBTParsingException(
                "Error while parsing a key for a tag.",
                e
            );
        }
        // Blank keys are not allowed.
        if(key.unwrapped().stripLeading().isEmpty()) throw new NBTParsingException("Blank key",i);
        // The key must be followed by a separator character to be a tag.
        if(i.skipWS() != SEPARATOR)
            throw new NBTParsingException(
                "Missing separator character ('%c')"
                .formatted(SEPARATOR),i
            );
        // Separator must be followed by something else.
        if(i.nextNonWS() == null) throw new NBTParsingException("Missing value",i);
        return key;
    }
    protected static NBTValue parseValue(final SequenceIterator i) throws NBTParsingException {
        try {return NBTValue.parse(i,NBTObject.CLOSE,true);}
        catch(final NBTParsingException e) {
            // Since a separator was found, any exceptions indicate that there
            // cannot be a valid value.
            throw new NBTParsingException(
                "Error while parsing tag value",
                i,e
            );
        }
    }
}
























