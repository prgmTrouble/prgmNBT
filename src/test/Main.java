package test;

import static settings.Settings.version;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import nbt.NBT;
import nbt.exception.NBTException;
import nbt.exception.NBTParsingException;
import nbt.value.collection.NBTObject;
import settings.Version;
import util.string.Sequence;

public class Main {
    static void test(final Version v,final String test) {
        version(v);
        try {
            NBT nbt = NBT.parse(
                new Sequence(test)
            );
            System.out.println(nbt);
        } catch(NBTParsingException e) {e.printStackTrace();}
    }
    static void file(final Version v,final String test) {
        version(v);
        try {
            NBT nbt = NBT.parseSNBT(
                Path.of(test)
            );
            System.out.println(nbt);
        } catch(NBTParsingException e) {e.printStackTrace();}
        catch(IOException e) {e.printStackTrace();}
    }
    static void v13w36a_test() {test(Version.v13w36a,"{display:{Lore:[:3d,c:\"\\\"\",d:e]}}");}
    static void v17w16a_test() {test(Version.v17w16a,"{display:{Lore:[a,b,c]}}");}
    static void v19w08a_file() {file(Version.v19w08a,"test2.txt");}
    static NBTObject readBin(final Version v,final String test,final boolean compressed) {
        version(v);
        try {
            NBTObject o = new NBTObject(new File(test),compressed);
            System.out.println(o);
            return o;
        } catch(NBTException|IOException e) {e.printStackTrace();}
        return null;
    }
    static NBTObject v17w16a_bin() {return readBin(Version.v17w16a,"1.12.nbt",true);}
    static NBTObject v19w08a_bin() {return readBin(Version.v19w08a,"1.16.dat_old",true);}
    
    public static void main(String[] args) throws NBTParsingException {
        v19w08a_file();
    }
    
}

































