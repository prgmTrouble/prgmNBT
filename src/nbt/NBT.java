package nbt;

import java.io.DataOutput;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import nbt.exception.NBTParsingException;
import nbt.value.NBTString;
import nbt.value.NBTValue;
import nbt.value.collection.NBTTag;
import settings.Settings;
import util.string.Indenter;
import util.string.Sequence;
import util.string.Sequence.SequenceIterator;
import util.string.Stringifiable;

public abstract class NBT implements Stringifiable {
    /**
     * <code>true</code> if this NBT should be represented minimally, where
     * possible.
     */
    public boolean minimal;
    
    /**
     * Constructs an NBT with {@linkplain #minimal} set to
     * {@linkplain Settings#defaultMinimal()}.
     */
    public NBT() {minimal = Settings.defaultMinimal();}
    
    /**
     * Constructs an NBT.
     * 
     * @param minimal {@linkplain #minimal}
     */
    public NBT(final boolean minimal) {this.minimal = minimal;}
    /**Writes this NBT.*/
    public abstract void write(final DataOutput out) throws IOException;
    
    /**@return <code>true</code> iff this NBT is equal to its default value.*/
    public abstract boolean isDefault();
    
    /**Sets this NBT and all of its children (if any) to the specified minimalism.*/
    public void setDeepMinimal(final boolean minimal) {this.minimal = minimal;}
    
    @Override public String toString() {return toSequence().toString();}
    
    /**
     * Breaks up the string representation into mutiple lines where necessary using
     * the appropriate indentation.
     * 
     * @param i {@linkplain Indenter}
     * 
     * @return <code>i</code>.
     */
    public Indenter prettyPrint(final Indenter i) {return appendTo(i);}
    
    public static NBT parse(Sequence s) throws NBTParsingException {
        if(s == null || (s = s.stripLeading()).isEmpty())
            throw new NBTParsingException("Cannot parse an empty value.");
        SequenceIterator i = s.iterator();
        NBT value;
        NBTString k = null;
        
        // Attempt to find a valid key.
        try {k = NBTTag.parseKey(i);}
        // If no valid key is found, then prepare to search for string value
        // instead.
        catch(final NBTParsingException e) {i.jumpTo(0);}
        
        // Attempt to parse a value.
        final NBTValue v = NBTValue.parse(i,null,false);
        
        // Return a tag if successful.
        value = k == null? v : new NBTTag(k,v);
        if(i.hasNext()) i.nextNonWS();
        if(i.hasNext())
            throw new NBTParsingException(
                "Trailing data found",
                i.index(),s
            );
        return value;
    }
    
    public static NBT parse(final SequenceIterator i,
                            final Character terminator,
                            final boolean commas)
                            throws NBTParsingException {
        NBT value = null; final int start = i.index();
        NBTString k = null;
        
        // Attempt to find a valid key.
        try {k = NBTTag.parseKey(i);}
        // If no valid key is found, then prepare to search for string value
        // instead.
        catch(final NBTParsingException e) {i.jumpTo(start);}
        
        // Attempt to parse a value.
        final NBTValue v = NBTValue.parse(i,terminator,commas);
        
        // Return a tag if successful.
        value = k == null? v : new NBTTag(k,v);
        final Character c = i.skipWS();
        if(!(commas && c == ',') && c != terminator)
            throw new NBTParsingException("generic value",i,terminator,commas,c);
        return value;
    }
    
    public static NBT parseSNBT(final Path path) throws NBTParsingException,IOException {
        return NBT.parse(new Sequence(new String(Files.readAllBytes(path),StandardCharsets.UTF_8)));
    }
}