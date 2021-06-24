package nbt.version;

/**
 * An enumeration of various game versions. These are used in other functions to
 * ensure that the results model the original game's behavior as closely as
 * possible.
 * 
 * @author prgmTrouble 
 * @author AzureTriple
 */
public enum Version {
    /**
     * This version introduced SNBT ("Stringified Named Binary Tags") via the
     * <code>give</code> and <code>summon</code> commands. Everything about NBT is
     * practically unchanged up until the {@linkplain #v17w16a} release.
     */
    v13w36a("13w36a"),
    
    /**
     * This version added primitive arrays, allowed keys in NBT objects to be
     * wrapped in quotes, disallowed the previously optional indices in arrays (e.g.
     * <code>[0:16,1:3,2:67]</code> was dropped in favor of <code>[16,3,67]</code>),
     * and disallowed empty keys and values. As a consequence, semi-colons
     * (<code>';'</code>) are no longer allowed to be in unwrapped strings.
     */
    v17w16a("17w16a"),
    
    /**
     * This is just a symbolic name for version 1.13 because I felt like using
     * this name instead. It is essentially just a period where the game's
     * developers refactored a lot of the code, which changed command and SNBT
     * parsing significantly.
     */
    The_Flattening("The Flattening"),
    
    /**
     * This version slightly tweaked the parsing so that strings can be wrapped in
     * single quotes. This eliminates the annoying escape characters before double
     * quotes in nested strings. However, escaping a single or double quote when the
     * wrapping character is not the same is not allowed (i.e. <code>'\''</code> and
     * <code>"\""</code> are valid while <code>'\"'</code> and <code>"\'"</code> are
     * invalid).
     */
    v19w08a("19w08a"),
    
    /**
     * This is not an actual version. Instead, it's a flag that I use to label
     * features which are not in the current game.
     */
    unknown("unknown");
    
    /**A user-friendly name for the version.*/
    public final String name;
    private Version(final String name) {this.name = name;}
    
    /**The version of the game referenced.*/
    public static Version CURRENT = v17w16a;
    
    public static boolean isAfter(final Version version) {return version.ordinal() < CURRENT.ordinal();}
    public static boolean atLeast(final Version version) {return version.ordinal() <= CURRENT.ordinal();}
    public static boolean atMost(final Version version) {return version.ordinal() >= CURRENT.ordinal();}
    public static boolean isBefore(final Version version) {return version.ordinal() > CURRENT.ordinal();}
    
    /**Returns a string in the format: <code>[version: &lt{@link #name}&gt]</code>*/
    @Override public String toString() {return new StringBuilder("[version: ").append(name).append(']').toString();}
}