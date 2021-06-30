package settings;

import gui.theme.Theme;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import json.exception.JSONException;
import json.value.JSONBool;
import json.value.JSONNumber;
import json.value.JSONString;
import json.value.JSONValue;
import json.value.collection.JSONObject;
import util.string.Sequence;
import util.string.outline.Segment;

/**
 * The settings for all aspects of this program.
 * 
 * @author prgmTrouble
 * @author AzureTriple
 */
public final class Settings {
    private Settings() {}
    
    static final Theme d_theme = Theme.DARK;
    static final Version d_version = Version.v17w16a;
    static final boolean d_defaultMinimal = true;
    static final Sequence d_defaultIndent = new Sequence(' ',4);
    static final int d_defaultFoldedCharLimit = Segment.MAX_LIMIT;
    static final int d_defaultFoldedChildLimit = Segment.MAX_LIMIT;
    static final int d_exceptionCharacterLimit = 50;
    static final boolean d_escapeUnicodeByDefault = false;
    
    
    private static final File SETTINGS_LOCATION = new File("settings.json");
    private static JSONObject SETTINGS;
    static {
        // Attempt to load from settings file.
        try {SETTINGS = JSONObject.read(SETTINGS_LOCATION);}
        catch(final JSONException e) {
            // Cannot find/read settings, so generate a default one.
            SETTINGS = new JSONObject();
            generateDefault();
        }
    }
    
    private static void generateDefault() {
        theme(d_theme);
        version(d_version);
        defaultMinimal(d_defaultMinimal);
        defaultIndent(d_defaultIndent);
        defaultFoldedCharLimit(d_defaultFoldedCharLimit);
        defaultFoldedChildLimit(d_defaultFoldedChildLimit);
        exceptionCharacterLimit(d_exceptionCharacterLimit);
        escapeUnicodeByDefault(d_escapeUnicodeByDefault);
        
        try {export(SETTINGS_LOCATION);}
        catch(final IOException e) {
            throw new UncheckedIOException(
                "Critical error while saving settings.",
                e
            );
        }
    }
    
    /**
     * Saves all settings to an external file.
     * 
     * @throws IOException       The settings could not be saved.
     * @throws SecurityException The security manager denied access.
     */
    public static void export(final File f) throws IOException,SecurityException {
        final File tmp = File.createTempFile("SETTINGS_TMP","json");
        tmp.deleteOnExit();
        SETTINGS.write(tmp);
        if(!tmp.renameTo(f))
            Files.move(tmp.toPath(),f.toPath(),StandardCopyOption.REPLACE_EXISTING);
        tmp.delete();
    }
    
    private static Sequence getSequence(final String key) throws NullPointerException,
                                                                 JSONException {
        return ((JSONString)SETTINGS.get(key)).value().unwrap();
    }
    private static boolean getBool(final String key) throws NullPointerException,
                                                            JSONException {
        return ((JSONBool)SETTINGS.get(key)).value();
    }
    private static Number getNumber(final String key) throws NullPointerException,
                                                             JSONException {
        return ((JSONNumber)SETTINGS.get(key)).value();
    }
    
    public static Theme theme() {
        try {return Theme.valueOf(getSequence("theme").toString());}
        catch(NullPointerException|JSONException|IllegalArgumentException e) {
            theme(d_theme);
            return d_theme;
        }
    }
    public static Version version() {
        try {return Version.valueOf(getSequence("version").toString());}
        catch(NullPointerException|JSONException|IllegalArgumentException e) {
            version(d_version);
            return d_version;
        }
    }
    public static boolean defaultMinimal() {
        try {return getBool("defaultMinimal");}
        catch(NullPointerException|JSONException e) {
            defaultMinimal(d_defaultMinimal);
            return d_defaultMinimal;
        }
    }
    public static Sequence defaultIndent() {
        try {return getSequence("defaultIndent");}
        catch(NullPointerException|JSONException e) {
            defaultIndent(d_defaultIndent);
            return d_defaultIndent;
        }
    }
    public static int defaultFoldedCharLimit() {
        try {return getNumber("defaultFoldedCharLimit").intValue();}
        catch(NullPointerException|JSONException e) {
            defaultFoldedCharLimit(d_defaultFoldedCharLimit);
            return d_defaultFoldedCharLimit;
        }
    }
    public static int defaultFoldedChildLimit() {
        try {return getNumber("defaultFoldedChildLimit").intValue();}
        catch(NullPointerException|JSONException e) {
            defaultFoldedChildLimit(d_defaultFoldedChildLimit);
            return d_defaultFoldedChildLimit;
        }
    }
    public static int exceptionCharacterLimit() {
        try {return getNumber("exceptionCharacterLimit").intValue();}
        catch(NullPointerException|JSONException e) {
            exceptionCharacterLimit(d_exceptionCharacterLimit);
            return d_exceptionCharacterLimit;
        }
    }
    public static boolean escapeUnicodeByDefault() {
        try {return getBool("escapeUnicodeByDefault");}
        catch(NullPointerException|JSONException e) {
            escapeUnicodeByDefault(d_escapeUnicodeByDefault);
            return d_escapeUnicodeByDefault;
        }
    }
    
    private static <V> V nn(final V v,final String name) {
        if(v == null)
            throw new NullPointerException(
                "Attempted to assign null value to \"%s\"."
                .formatted(name)
            );
        return v;
    }
    private static void set(final String key,final JSONValue value) {
        try {SETTINGS.set(key,value);}
        catch(final JSONException e) {e.printStackTrace();}
    }
    private static void set(final String key,final String value) {
        try {set(key,new JSONString(value));}
        catch(final JSONException e) {e.printStackTrace();}
    }
    private static void set(final String key,final Sequence value) {
        try {set(key,new JSONString(value));}
        catch(final JSONException e) {e.printStackTrace();}
    }
    private static void set(final String key,final boolean value) {
        set(key,new JSONBool(value));
    }
    private static void set(final String key,final Number value) {
        set(key,new JSONNumber(value));
    }
    
    public static void theme(final Theme t) throws NullPointerException {
        set("theme",nn(t,"theme").name());
    }
    public static void version(final Version v) throws NullPointerException {
        set("version",nn(v,"version").name);
    }
    public static void defaultMinimal(final boolean dm) {
        set("defaultMinimal",dm);
    }
    public static void defaultIndent(final Sequence di) throws NullPointerException {
        set("defaultIndent",di);
    }
    public static void defaultFoldedCharLimit(final int dfcl) {
        set("defaultFoldedCharLimit",dfcl);
    }
    public static void defaultFoldedChildLimit(final int dfcl) {
        set("defaultFoldedChildLimit",dfcl);
    }
    public static void exceptionCharacterLimit(final int ecl) {
        set("exceptionCharacterLimit",ecl);
    }
    public static void escapeUnicodeByDefault(final boolean eubd) {
        set("escapeUnicodeByDefault",eubd);
    }
}






























