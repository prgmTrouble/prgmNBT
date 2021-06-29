package nbt.value.collection;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;

import java.io.BufferedInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import nbt.NBT;
import nbt.exception.NBTException;
import nbt.exception.NBTParsingException;
import nbt.value.NBTString;
import nbt.value.NBTValue;
import nbt.value.ValueType;
import nbt.value.number.NBTNumber;
import util.container.Container;
import util.string.Joiner;
import util.string.Sequence;
import util.string.Sequence.SequenceIterator;
import util.string.outline.WrappingSegment;

/**
 * An {@linkplain NBTCollection} which functions as a map.
 * 
 * @author prgmTrouble
 * @author AzureTriple
 */
public class NBTObject extends NBTCollection<NBTString,NBTTag> {
    public static final ValueType TYPE = ValueType.OBJECT;
    @Override public ValueType type() {return TYPE;}
    /**The byte that marks the end of NBT objects in binary files.*/
    public static final byte END_BYTE = 0;
    
    public static final char OPEN = '{',CLOSE = '}';
    @Override protected Joiner getJoiner() {return new Joiner(OPEN,CLOSE);}
    
    protected final TreeMap<NBTString,NBTValue> values = new TreeMap<>();
    
    /**
     * Constructs an empty object with default minimalism.
     * 
     * @see NBTValue#NBTValue()
     */
    public NBTObject() {super();}
    /**
     * Constructs an empty object.
     * 
     * @see NBTValue#NBTValue(boolean)
     */
    public NBTObject(final boolean minimal) {super(minimal);}
    /**
     * Constructs an object with default minimalism.
     * 
     * @see NBTValue#NBTValue()
     */
    public NBTObject(final Container<NBTTag> tags) {
        super();
        for(final NBTTag tag : tags) set(tag);
    }
    /**
     * Reads an object.
     * 
     * @throws IOException The object could not be read.
     * @throws NBTException The object is invalid.
     */
    public NBTObject(final DataInput in) throws IOException,NBTException {
        super();
        for(byte id = in.readByte();id != END_BYTE;id = in.readByte())
            set(new NBTTag(in,id));
    }
    private static DataInput stream(final File in,final boolean compressed) throws IOException {
        return new DataInputStream(
            new BufferedInputStream(
                compressed? new GZIPInputStream(new FileInputStream(in))
                          : new FileInputStream(in)
            )
        );
    }
    /**
     * Reads an object from a binary file.
     * 
     * @param compressed <code>true</code> iff the file is in a GZIP compressed format.
     * 
     * @throws IOException  The file could not be read.
     * @throws NBTException The object is invalid.
     */
    public NBTObject(final File in,final boolean compressed) throws IOException,NBTException {
        super();
        final DataInput i = stream(in,compressed);
        {
            final byte b = i.readByte();
            if(b != TYPE.id) {
                final ValueType t = ValueType.getType(b);
                throw new NBTException(
                    "Non-object type %s found at beginning of file."
                    .formatted(t == null? "UNKNOWN <%d>".formatted(b) : t.name)
                );
            }
        }
        i.readUTF();
        for(byte id = i.readByte();id != END_BYTE;id = i.readByte())
            set(new NBTTag(i,id));
    }
    /**
     * Writes this object value.
     * 
     * @throws IOException The object could not be written.
     */
    @Override
    public void write(final DataOutput out) throws IOException {
        super.write(out);
        out.writeByte(END_BYTE);
    }
    
    /**@throws IllegalArgumentException The key is empty.*/
    @Override
    public NBTObject set(final NBTString key,final NBTValue value) throws NullPointerException,
                                                                          IllegalArgumentException {
        if(key == null)
            throw new NullPointerException("Key is null.");
        if(value == null)
            throw new NullPointerException("Value is null.");
        if(key.unwrapped().stripLeading().isEmpty())
            throw new IllegalArgumentException("Key is empty.");
        
        values.put(key,value);
        return this;
    }
    
    /**
     * Maps the value to the key.
     * 
     * @return <code>this</code>
     * 
     * @throws NBTParsingException The input is not a valid key.
     */
    public NBTObject set(final Sequence key,final NBTValue value) throws NBTParsingException {
        return set(new NBTString(key),value);
    }
    /**
     * Maps the value to the key.
     * 
     * @return <code>this</code>
     * 
     * @throws NBTParsingException The input is not a valid key.
     */
    public NBTObject set(final String key,final NBTValue value) throws NBTParsingException {
        return set(new NBTString(key),value);
    }
    /**
     * Adds an entry.
     * 
     * @return <code>this</code>
     * 
     * @throws NullPointerException The entry is <code>null</code>.
     */
    public NBTObject set(final NBTTag entry) throws NullPointerException {values.put(entry.key,entry.value()); return this;}
    
    @Override public NBTValue get(final NBTString key) throws NullPointerException {return values.get(key);}
    /**
     * @return The value associated with the key.
     * 
     * @throws NBTParsingException The input is not a valid {@linkplain NBTString}.
     */
    public NBTValue get(final Sequence key) throws NBTParsingException {return get(new NBTString(key));}
    /**
     * @return The value associated with the key.
     * 
     * @throws NBTParsingException The input is not a valid {@linkplain NBTString}.
     */
    public NBTValue get(final String key) throws NBTParsingException {return get(new NBTString(key));}
    
    @Override public NBTValue remove(final NBTString key) throws NullPointerException {return values.remove(key);}
    /**
     * @return The value associated with the key.
     * 
     * @throws NBTParsingException The input is not a valid {@linkplain NBTString}.
     */
    public NBTValue remove(final Sequence key) throws NBTParsingException {return remove(new NBTString(key));}
    /**
     * @return The value associated with the key.
     * 
     * @throws NBTParsingException The input is not a valid {@linkplain NBTString}.
     */
    public NBTValue remove(final String key) throws NBTParsingException {return remove(new NBTString(key));}
    
    private static class NBTObjectIterator implements Iterator<NBTTag> {
        private final Iterator<Entry<NBTString,NBTValue>> i;
        private NBTObjectIterator(final Iterator<Entry<NBTString,NBTValue>> i) {this.i = i;}
        
        @Override public boolean hasNext() {return i.hasNext();}
        @Override
        public NBTTag next() {
            final Entry<NBTString,NBTValue> e = i.next();
            try {return new NBTTag(e.getKey(),e.getValue());}
            catch(final NBTParsingException x) {}
            // No errors should get thrown by the tag constructor.
            return null;
        }
    }
    @Override public Iterator<NBTTag> iterator() {return new NBTObjectIterator(values.entrySet().iterator());}
    @Override protected Iterable<NBTValue> values() {return values.values();}
    
    @Override
    protected Sequence minimal() {
        final Joiner j = getJoiner();
        for(final NBT nbt : this) if(!nbt.isDefault()) nbt.appendTo(j);
        return j.concat();
    }
    
    @Override
    protected WrappingSegment getWrapper() {
        final Sequence[] wrapper = Sequence.shared(new char[] {OPEN,CLOSE},1);
        return new WrappingSegment(wrapper[0],wrapper[1]); //TODO customization?
    }
    
    /**
     * @param i A {@linkplain SequenceIterator} which points to the position at the
     *          opening brace.
     * 
     * @return An {@linkplain NBTObject}.
     * 
     * @throws NBTParsingException The iterator cannot find a valid object.
     */
    public static NBTObject parse(final SequenceIterator i) throws NBTParsingException {
        final NBTObject out = new NBTObject();
        if(testEmpty(i,CLOSE)) return out;
        while(i.hasNext()) {
            // Get key.
            final NBTString key;
            try {key = NBTTag.parseKey(i);}
            catch(final NBTParsingException e) {
                throw new NBTParsingException("Error while parsing key in object",i,e);
            }
            // Get value.
            final NBTValue value;
            try {value = NBTTag.parseValue(i);}
            catch(final NBTParsingException e) {
                throw new NBTParsingException(
                    "Error while parsing value for key \"%s\" in object"
                    .formatted(key.unwrapped()),i,e
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
        throw new NBTParsingException("Missing closing character '%c'".formatted(CLOSE),i);
    }
    /**
     * @param i          A {@linkplain SequenceIterator} which points to the
     *                   position just before the opening brace.
     * @param terminator A character which marks the end of a structure.
     *                   <code>null</code> indicates the end of the sequence.
     * @param commas     <code>true</code> iff commas are allowed to terminate a
     *                   value.
     * 
     * @return The appropriate {@linkplain NBTNumber}.
     * 
     * @throws NBTParsingException The iterator cannot find a valid number.
     */
    public static NBTObject parse(final SequenceIterator i,
                                  final Character terminator,
                                  final boolean commas)
                                  throws NBTParsingException {
        final NBTObject out = parse(i);
        final Character c = i.skipWS();
        if(!(commas && c == ',') && c != terminator)
            throw new NBTParsingException("object",i,terminator,commas,c);
        return out;
    }
    
    /**
     * Reads an object from a file.
     * 
     * @throws NBTException The file could not be read or the object is invalid.
     */
    public static NBTObject read(final File f) throws NBTException {
        try(
            final DataInputStream in = new DataInputStream(
                new BufferedInputStream(
                    new GZIPInputStream(
                        new FileInputStream(f)
                    )
                )
            )
        ) {return new NBTObject(in);}
        catch(final IOException e) {throw new NBTException("Error reading object from file.",e);}
    }
}