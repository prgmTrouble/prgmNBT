package nbt.value.collection;

import java.util.ArrayList;
import java.util.Iterator;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import nbt.NBT;
import nbt.exception.NBTConversionException;
import nbt.exception.NBTException;
import nbt.exception.NBTParsingException;
import nbt.value.NBTValue;
import nbt.value.ValueType;
import settings.Version;
import util.string.Joiner;
import util.string.Sequence;
import util.string.Sequence.SequenceIterator;
import util.string.outline.JoiningSegment;
import util.string.outline.Segment;
import util.string.outline.ValueSegment;
import util.string.outline.WrappingSegment;

/**
 * An {@linkplain NBTCollection} which holds a list of values with the same
 * type.
 * 
 * @author prgmTrouble 
 * @author AzureTriple
 */
public class NBTArray extends NBTCollection<Integer,NBTValue> {
    /**Causes the array to attempt conversion on foreign types.*/
    public static final boolean ADOPTION = Version.atLeast(Version.v13w36a);
    /**Causes the array to attempt conversion on all elements to a foreign type.*/
    public static final boolean RETROACTIVE_ADOPTION = Version.atLeast(Version.unknown);
    /**
     * Causes the array to allow a key followed by a colon to appear before
     * elements, where the key can practically be anything that does not violate any
     * rules with string or collection parsing. Additionally, empty elements are
     * also allowed and elements which don't match the sub-type are simply ignored.
     */
    public static final boolean THE_WILD_WEST = Version.isBefore(Version.v17w16a);
    
    public static final ValueType TYPE = ValueType.ARRAY;
    @Override public ValueType type() {return TYPE;}
    
    public static final char OPEN = '[',CLOSE = ']';
    public static final char INDEX_SEPARATOR = ':';
    
    @Override protected Joiner getJoiner() {return new Joiner(OPEN,CLOSE);}
    
    protected ArrayList<NBTValue> values = new ArrayList<>();
    protected ValueType subtype = null;
    
    /**
     * Creates an empty array with default minimalism.
     * 
     * @see {@linkplain NBTValue#NBTValue()}
     */
    public NBTArray() {super();}
    /**
     * Creates an empty array.
     * 
     * @param minimal {@linkplain NBTValue#minimal}
     * 
     * @see {@linkplain NBTValue#NBTValue(boolean)}
     */
    public NBTArray(final boolean minimal) {super(minimal);}
    /**Reads an array.*/
    public NBTArray(final DataInput in) throws IOException,NBTException {
        super();
        final int l;
        final ValueType t;
        {
            final byte id = in.readByte();
            l = in.readInt();
            if(id == 0) {
                if(l != 0) throw new NBTException("Missing type for array.");
                return;
            }
            if((t = ValueType.getType(id)) == null)
                throw new NBTException(
                    "Unknown type %d.".formatted(id)
                );
            values.ensureCapacity(l);
        }
        for(int i = 0;i < l;++i) values.add(t.read(in));
    }
    /**Writes this array value.*/
    @Override
    public void write(final DataOutput out) throws IOException {
        if(subtype == null) {
            out.write(0);
            out.writeInt(0);
            return;
        }
        out.write(subtype.id);
        out.writeInt(values.size());
        super.write(out);
    }
    
    /**
     * Attempts to convert the element to this array's type or convert the array to
     * the element's type. If this array does not have a type, it will take the
     * element's type. The argument must not be null.
     * 
     * @return The converted element, or <code>null</code> if the conversion failed.
     */
    protected NBTValue adopt(final NBTValue element) throws NBTConversionException {
        if(subtype == null) {subtype = element.type(); return element;}
        if(subtype == element.type()) return element;
        if(THE_WILD_WEST) return null;
        if(ADOPTION) {
            try {return element.convertTo(subtype);}
            catch(final NBTConversionException e) {
                if(!RETROACTIVE_ADOPTION)
                    throw new NBTConversionException(
                        "Could not convert element to subtype.",e
                    );
            }
            // Retroactive adoption is implicitly active.
            final ArrayList<NBTValue> copy = new ArrayList<>(values.size() + 1);
            final ValueType t = element.type();
            try {for(final NBTValue v : values) copy.add(v.convertTo(t));}
            catch(final NBTConversionException e) {
                throw new NBTConversionException(
                    "Could not convert array type to element type.",e
                );
            }
            values = copy;
            subtype = t;
            return element;
            // Technically, it would be possible to try and find a set intersection
            // between convertible types, but I'll leave that alone until it's needed.
        }
        throw new NBTConversionException(
            "Could not assign a value of type \"%s\" to an array of type \"%s\"."
            .formatted(element.type(),subtype)
        );
    }
    
    @Override
    public NBTArray set(final Integer key,NBTValue element) throws NBTConversionException {
        if(key == null || values.size() < key || key < 0)
            throw new IllegalArgumentException(String.format(
                "Cannot set value at position %s (size = %d).",
                key,values.size()
            ));
        if((element = adopt(element)) != null)
            values.add(key,element);
        return this;
    }
    /**Appends the element to the last index.*/
    public NBTArray add(NBTValue element) throws NBTConversionException {
        if((element = adopt(element)) != null)
            values.add(element);
        return this;
    }
    @Override
    public NBTValue get(final Integer key) {
        if(key == null || values.size() <= key || key < 0)
            throw new IllegalArgumentException(String.format(
                "Cannot get value at position %s (size = %d).",
                key,values.size()
            ));
        return values.get(key);
    }
    @Override
    public NBTValue remove(final Integer key) {
        if(key == null || values.size() <= key || key < 0)
            throw new IllegalArgumentException(String.format(
                "Cannot remove value at position %s (size = %d).",
                key,values.size()
            ));
        if(values.size() == 1) subtype = null;
        return values.remove(key.intValue());
    }
    
    @Override
    public boolean isDefault() {
        for(final NBTValue v : this) if(!v.isDefault()) return false;
        return true;
    }
    
    @Override
    protected Sequence complete() {
        if(!THE_WILD_WEST) return super.complete();
        final Joiner j = getJoiner(),k = new Joiner(INDEX_SEPARATOR);
        int i = -1;
        for(final NBTValue v : values)
            j.push(
                k.push(new Sequence(Integer.toString(++i)))
                 .push(v.toSequence())
            );
        return j.concat();
    }
    @Override protected Sequence minimal() {return super.complete();}
    
    @Override
    protected WrappingSegment getWrapper() {
        final Sequence[] wrapper = Sequence.shared(new char[] {OPEN,CLOSE},1);
        return new WrappingSegment(wrapper[0],wrapper[1]); //TODO customization?
    }
    @Override
    protected Segment getChildren() {
        if(!THE_WILD_WEST || minimal) return super.getChildren();
        final JoiningSegment s = new JoiningSegment(new Sequence(',')); //TODO customization?
        int i = -1;
        final Sequence sep = new Sequence(INDEX_SEPARATOR);
        for(final NBTValue v : this)
            s.push(
                new JoiningSegment(sep) //TODO customization?
                             .push(new ValueSegment(new Sequence(++i)))
                             .push(v.toSegment())
            );
        return null;
    }
    
    @Override public Iterator<NBTValue> iterator() {return values.iterator();}
    @Override protected NBTArray values() {return this;}
    
    /**
     * @param i A {@linkplain SequenceIterator} which points to the position at the
     *          opening bracket.
     * 
     * @return An {@linkplain NBTArray}.
     * 
     * @throws NBTParsingException If the iterator cannot find a valid array.
     */
    public static NBTArray parse(final SequenceIterator i,
                                 final Character terminator,
                                 final boolean commas)
                                 throws NBTParsingException {
        final NBTArray arr;
        {
            final int start = i.index();
            arr = NBTPrimitiveArray.parseHeader(i);
            if(arr.type() == TYPE) i.jumpTo(start);
        }
        try {
            if(THE_WILD_WEST) {
                while(i.hasNext()) {
                    final Character c = i.nextNonWS();
                    if(c == null)
                        throw new NBTParsingException(
                            "Missing closing character '%c'"
                            .formatted(CLOSE),i
                        );
                    if(c == ',') continue; // Empty values permitted.
                    if(c == CLOSE) break;
                    final NBT nbt = NBT.parse(i,CLOSE,true);
                    arr.add(nbt instanceof NBTTag? ((NBTTag)nbt).value : (NBTValue)nbt);
                    if(i.peek() == CLOSE) break;
                }
            } else {
                i.nextNonWS();
                while(i.hasNext()) {
                    arr.add(NBTValue.parse(i,CLOSE,true));
                    if(i.peek() == CLOSE) break;
                    i.nextNonWS();
                }
            }
        } catch(final NBTConversionException e) {
            throw new NBTParsingException(
                "Error while converting value",
                i,e
            );
        }
        if(i.peek() != CLOSE)
            throw new NBTParsingException(
                "Missing closing character '%c'"
                .formatted(CLOSE),i
            );
        final Character c = i.nextNonWS();
        if(!(commas && c == ',') && c != terminator)
            throw new NBTParsingException("Trailing data",i);
        return arr;
    }
}