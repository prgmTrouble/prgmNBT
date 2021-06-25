package test;

import static settings.Settings.version;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import nbt.NBT;
import nbt.exception.NBTException;
import nbt.exception.NBTParsingException;
import nbt.value.collection.NBTObject;
import settings.Version;
import util.container.Container;
import util.container.Queue;
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
    
    static void fill(final Container<Integer> container,int start,final int end) {
        do container.push(start);
        while(++start != end);
    }
    static final int KERNEL = 1 << 16;
    static Container<Integer> container() {return new Queue<>();}
    static Container<Integer> make(final int size) {
        final Queue<Container<Integer>> qs;
        {
            final ExecutorService taskPool;
            {
                final int fullGroups = size / KERNEL;
                taskPool = Executors.newFixedThreadPool(
                    Math.min(fullGroups,Runtime.getRuntime().availableProcessors())
                );
                qs = new Queue<>();
                int start = 0;
                for(int i = 0;i < fullGroups;++i) {
                    final Container<Integer> c = container();
                    qs.push(c);
                    final int _start = start,end = _start + KERNEL;
                    taskPool.execute(() -> fill(c,_start,end));
                    start = end;
                }
                {
                    final int fs = size % KERNEL;
                    if(fs != 0) {
                        final Container<Integer> c = container();
                        qs.push(c);
                        final int _start = start;
                        taskPool.execute(() -> fill(c,_start,size));
                    }
                }
            }
            taskPool.shutdown();
            try {taskPool.awaitTermination(Long.MAX_VALUE,TimeUnit.DAYS);}
            catch(final InterruptedException e) {e.printStackTrace();}
        }
        final Container<Integer> out = qs.pop();
        while(!qs.empty()) out.merge(qs.pop());
        return out;
    }
    static volatile Integer[] garbage;
    static final int BATCH = 10;
    static long test(final Container<Integer> container,final int chunkSize) {
        System.out.printf("testing %10d:",chunkSize);
        System.gc();
        final long t0 = System.nanoTime();
        for(int i = 0;i < BATCH;++i)
            garbage = container.parallelToArray(chunkSize);
        final long t1 = System.nanoTime();
        final long time = t1 - t0;
        System.out.printf(" %10d\n",time);
        return time;
    }
    static final int SIZE = Integer.MAX_VALUE / 60;
    static void test(final int size,final int offset) {
        final Container<Integer> container = make(SIZE);
        test(container,offset - size);
        test(container,offset);
        test(container,offset + size);
    }
    public static void main(String[] args) throws NBTParsingException {
        final int factor = 8;
        System.out.println("Size: "+SIZE); //TODO test to ensure that all elements are in array
        test(Integer.MAX_VALUE / (1 << (1 + factor)),Integer.MAX_VALUE / (1 << factor));
    }
    
}

































