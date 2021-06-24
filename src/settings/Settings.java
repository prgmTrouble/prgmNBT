package settings;

import gui.theme.Theme;
import java.io.File;
import util.string.Sequence;
import util.string.structure.StructureBuilder.FoldingPolicy;

public final class Settings {
    private Settings() {}
    
    static Theme theme;
    static Version version;
    static boolean defaultMinimal;
    static FoldingPolicy defaultFoldingPolicy;
    static int maxFoldedLength;
    static Sequence defaultIndent;
    
    public static void export(final File f) {
        
    }
    
    public static Theme theme() {return theme;}
    public static Version version() {return version;}
    public static boolean defaultMinimal() {return defaultMinimal;}
    public static FoldingPolicy defaultFoldingPolicy() {return defaultFoldingPolicy;}
    public static int maxFoldedLength() {return maxFoldedLength;}
    public static Sequence defaultIndent() {return defaultIndent;}
    
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
    public static void defaultFoldingPolicy(final FoldingPolicy fp) {defaultFoldingPolicy = fp;}
    public static void maxFoldedLength(final int fl) {maxFoldedLength = fl;}
    public static void defaultIndent(final Sequence di) {defaultIndent = nn(di,"default indent");}
}






























