package settings;

import gui.theme.Theme;
import java.io.File;
import util.string.Sequence;
import util.string.outline.Segment;

public final class Settings {
    private Settings() {}
    
    static Theme theme;
    static Version version;
    static boolean defaultMinimal;
    static Sequence defaultIndent;
    static int defaultFoldedCharLimit;
    static int defaultFoldedChildLimit;
    static int exceptionCharacterLimit;
    static boolean escapeUnicodeByDefault;
    
    private static final File SETTINGS_LOCATION = new File(""); //TODO
    static {
        // Attempt to load from settings file.
        //TODO
        // Cannot find settings, so generate a default one.
        generateDefault();
    }
    
    private static void generateDefault() {
        theme(Theme.DARK);
        version(Version.v17w16a);
        defaultMinimal(true);
        defaultIndent(new Sequence(' ',4));
        defaultFoldedCharLimit(Segment.MAX_LIMIT);
        defaultFoldedChildLimit(Segment.MAX_LIMIT);
        export(SETTINGS_LOCATION);
        exceptionCharacterLimit(50);
        escapeUnicodeByDefault(true);
    }
    
    public static void export(final File f) {
        //TODO export as json file
    }
    
    public static Theme theme() {return theme;}
    public static Version version() {return version;}
    public static boolean defaultMinimal() {return defaultMinimal;}
    public static Sequence defaultIndent() {return defaultIndent;}
    public static int defaultFoldedCharLimit() {return defaultFoldedCharLimit;}
    public static int defaultFoldedChildLimit() {return defaultFoldedChildLimit;}
    public static int exceptionCharacterLimit() {return exceptionCharacterLimit;}
    public static boolean escapeUnicodeByDefault() {return escapeUnicodeByDefault;}
    
    private static <V> V nn(final V v,final String name) {
        if(v == null)
            throw new NullPointerException(
                "Attempted to assign null value to \"%s\"."
                .formatted(name)
            );
        return v;
    }
    public static void theme(final Theme t) {theme = nn(t,"theme");}
    public static void version(final Version v) {version = nn(v,"version");}
    public static void defaultMinimal(final boolean dm) {defaultMinimal = dm;}
    public static void defaultIndent(final Sequence di) {defaultIndent = nn(di,"default indent");}
    public static void defaultFoldedCharLimit(final int dfcl) {defaultFoldedCharLimit = dfcl;}
    public static void defaultFoldedChildLimit(final int dfcl) {defaultFoldedChildLimit = dfcl;}
    public static void exceptionCharacterLimit(final int ecl) {exceptionCharacterLimit = ecl;}
    public static void escapeUnicodeByDefault(final boolean eubd) {escapeUnicodeByDefault = eubd;}
}






























