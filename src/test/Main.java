package test;

import static settings.Settings.version;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import nbt.NBT;
import nbt.exception.NBTException;
import nbt.exception.NBTParsingException;
import nbt.value.collection.NBTArray;
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
        //v17w16a_bin();
        NBTObject o = v19w08a_bin();
        o = (NBTObject)o.get("Data");
        o = (NBTObject)o.get("Player");
        NBTArray arr = (NBTArray)o.get("Inventory");
        o = (NBTObject)arr.get(4);
        o = (NBTObject)o.get("tag");
        o = (NBTObject)o.get("Fireworks");
        System.out.println(o);
        /*Version.CURRENT = Version.v19w08a;
        try {
            System.out.println(new NBTObject(new File("r.0.0.mca"),false));
            System.out.println(
                ((NBTObject)new NBTObject(new File("r.0.0.mca"),false).get("Level"))
                .get("TileEntities")
            );
        } catch(IOException | NBTException e) {
            e.printStackTrace();
        }*/
        //for(final String x : new String[] {"0.0","0.-1","-1.0","-1.-1"})
        //    readBin(Version.v19w08a,"r."+x+".mca",false);
        /*
        try(final DataInputStream i = new DataInputStream(new BufferedInputStream(new FileInputStream(new File("r."+x+".mca"))))) {
            for(int j = i.read();j != -1;j = i.read())
                System.out.printf("%3d ",j);
            System.err.println("END");
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        }*/
    }
    
}
