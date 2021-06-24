package test.string;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import util.string.Sequence;
import util.string.Sequence.SequenceIterator;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SequenceTest {
    private static final char[] data = "abc123".toCharArray();
    static void assertEmpty(final Sequence s) {
        assertTrue(s.isEmpty());
        assertEquals(0,s.length());
        assertArrayEquals(new char[0],s.toChars());
    }
    static void assertData(final Sequence s,final char[] data) {
        assertFalse(s.isEmpty());
        assertEquals(data.length,s.length());
        assertArrayEquals(data,s.toChars());
    }
    
    @Test @Order(1)
    void testSequence() {assertEmpty(new Sequence());}
    
    @Test @Order(2)
    void testSequenceCharArray() {
        assertData(new Sequence(data),data);
        assertEmpty(new Sequence(new char[0]));
        assertEmpty(new Sequence((char[])null));
    }
    
    @Test @Order(3)
    void testSequenceIntCharArray() {
        for(int i = 0;i < data.length;++i) {
            final char[] nd = new char[data.length - i];
            System.arraycopy(data,i,nd,0,nd.length);
            assertData(new Sequence(i,data),nd);
        }
        assertEmpty(new Sequence(data.length,data));
        assertEmpty(new Sequence(data.length + 1,data));
        assertData(new Sequence(-1,data),data);
        assertEmpty(new Sequence(0,new char[0]));
        assertEmpty(new Sequence(0,null));
    }
    
    @Test @Order(4)
    void testSequenceIntIntCharArray() {
        for(int j = 0;j < data.length;++j) {
            for(int i = 0;i < j;++i) {
                final char[] nd = new char[j - i];
                System.arraycopy(data,i,nd,0,nd.length);
                assertData(new Sequence(i,j,data),nd);
            }
            assertEmpty(new Sequence(j,j,data));
        }
        assertEmpty(new Sequence(4,1,data));
        assertData(new Sequence(-1,data.length,data),data);
        assertData(new Sequence(0,data.length + 1,data),data);
        assertData(new Sequence(-1,data.length + 1,data),data);
        assertEmpty(new Sequence(0,0,new char[0]));
        assertEmpty(new Sequence(0,0,null));
    }
    
    @Test @Order(5)
    void testCharAt() {
        assertThrows(ArrayIndexOutOfBoundsException.class,() -> new Sequence().charAt(0));
        {
            final Sequence s = new Sequence(data);
            assertThrows(ArrayIndexOutOfBoundsException.class,() -> s.charAt(data.length));
            assertThrows(ArrayIndexOutOfBoundsException.class,() -> s.charAt(-1-data.length));
            assertEquals(data[0],s.charAt(0));
            for(int i = 1;i < data.length;++i) {
                assertEquals(data[data.length - i],s.charAt(-i));
                assertEquals(data[i],s.charAt(i));
            }
            assertEquals(data[0],s.charAt(-data.length));
        }
        
        {
            final Sequence s = new Sequence(1,data);
            assertEquals(data[1],s.charAt(0));
            for(int i = 1;i < data.length - 1;++i) {
                assertEquals(data[data.length - i],s.charAt(-i));
                assertEquals(data[i + 1],s.charAt(i));
            }
            assertEquals(data[1],s.charAt(-s.length()));
        }
        
        {
            final Sequence s = new Sequence(1,5,data);
            assertThrows(ArrayIndexOutOfBoundsException.class,() -> s.charAt(data.length - 1));
            assertThrows(ArrayIndexOutOfBoundsException.class,() -> s.charAt(-data.length));
            assertEquals(data[1],s.charAt(0));
            for(int i = 1;i < (5 - 1);++i) {
                assertEquals(data[5 - i],s.charAt(-i));
                assertEquals(data[i + 1],s.charAt(i));
            }
            assertEquals(data[1],s.charAt(-s.length()));
        }
    }
    
    private static void itrTestBase(final SequenceIterator i,final int j,final int k) {
        assertTrue(i.hasNext());
        assertEquals(data[k],i.next());
        assertEquals(data[k],i.peek());
        assertEquals(j,i.index());
    }
    private static void testFwdItr(final Sequence s,final int a,final int b) {
        final char[] data = s.toChars();
        int i = 0;
        for(final char c : s) assertEquals(data[i++],c);
        final SequenceIterator j = s.iterator();
        assertEquals(-1,j.index());
        for(i = 0;i < (b-a);++i) itrTestBase(j,i,i + a);
        assertFalse(j.hasNext());
    }
    private static void testRevItr(final Sequence s,final int a,final int b) {
        final SequenceIterator i = s.reverseIterator();
        assertEquals(b-a,i.index());
        for(int j = b-a;j-- > 0;) itrTestBase(i,j,j + a);
        assertFalse(i.hasNext());
    }
    @Test @Order(6)
    void testIterator() {
        testFwdItr(new Sequence(),0,0);
        testFwdItr(new Sequence(data),0,data.length);
        testFwdItr(new Sequence(1,data),1,data.length);
        testFwdItr(new Sequence(1,5,data),1,5);
    }
    
    @Test @Order(7)
    void testReverseIterator() {
        testRevItr(new Sequence(),0,0);
        testRevItr(new Sequence(data),0,data.length);
        testRevItr(new Sequence(1,data),1,data.length);
        testRevItr(new Sequence(1,5,data),1,5);
    }
    
    @Test @Order(8)
    void testEqualsObject() {
        {
            final Sequence s = new Sequence(data);
            Object o = s;
            assertTrue(s.equals(o));
            assertTrue(o.equals(s));
            
            o = new Sequence(data);
            assertTrue(s.equals(o));
            assertTrue(o.equals(s));
        }
        {
            final Sequence s = new Sequence(1,5,data);
            final Object o = new Sequence(1,5,data);
            assertTrue(s.equals(o));
            assertTrue(o.equals(s));
        }
    }
    
    @Test @Order(9)
    void testSubSequenceIntInt() {
        assertEquals(new Sequence(1,5,data),new Sequence(data).subSequence(1,5));
        assertEquals(new Sequence(2,3,data),new Sequence(1,5,data).subSequence(1,2));
    }
    
    @Test @Order(10)
    void testSubSequenceInt() {
        assertEquals(new Sequence(1,data),new Sequence(data).subSequence(1));
        assertEquals(new Sequence(2,data),new Sequence(data).subSequence(1).subSequence(1));
    }
    
    @Test @Order(11)
    void testUnwrap() {
        assertEquals(new Sequence(1,data.length-1,data),new Sequence(data).unwrap());
    }
    
    @Test @Order(12)
    void testCopyInto() {
        final char[] nd = new char[data.length];
        System.arraycopy(data,1,nd,2,4);
        final char[] buf = new char[data.length];
        final int ct = new Sequence(1,5,data).copyInto(buf,2);
        assertEquals(6,ct);
        assertArrayEquals(nd,buf);
    }
    
    @Test @Order(13)
    void testGetSharedSequence() {
        assertEquals(new Sequence(data),new Sequence(1,5,data).getSharedSequence());
    }
    
    private static final char[] wsData = " \tabc 123\t".toCharArray();
    
    @Test @Order(14)
    void testStripLeading() {
        assertEquals(new Sequence(2,wsData),new Sequence(wsData).stripLeading());
        assertEquals(new Sequence(data),new Sequence(data).stripLeading());
    }
    
    @Test @Order(15)
    void testStripTailing() {
        assertEquals(new Sequence(0,wsData.length - 1,wsData),new Sequence(wsData).stripTailing());
        assertEquals(new Sequence(data),new Sequence(data).stripTailing());
    }
    
    @Test @Order(16)
    void testStrip() {
        assertEquals(new Sequence(2,wsData.length - 1,wsData),new Sequence(wsData).strip());
        assertEquals(new Sequence(data),new Sequence(data).strip());
    }
    
    private static final char[] splitData = "{,[{}]}\",\",asdf,fdsa".toCharArray();
    //                                       0123456 78 90123456789
    @Test @Order(17)
    void testSharedCharArrayArray() {
        final char[] a = new char[splitData.length / 2];
        final char[] b = new char[splitData.length / 2 + splitData.length % 2];
        System.arraycopy(splitData,0,a,0,a.length);
        System.arraycopy(splitData,a.length,b,0,b.length);
        final Sequence[] shared = Sequence.shared(a,b);
        assertArrayEquals(a,shared[0].toChars());
        assertArrayEquals(b,shared[1].toChars());
        assertArrayEquals(splitData,shared[0].getSharedSequence().toChars());
    }
    
    private static void testSharedSplit(final int...split) {
        final Sequence[] shared = Sequence.shared(splitData,split);
        assertEquals(shared.length,split.length + 1);
        int prev = 0,idx = 0;
        for(final int i : split) {
            final int l = i - prev;
            final char[] arr = new char[l];
            System.arraycopy(splitData,prev,arr,0,l);
            assertArrayEquals(arr,shared[idx++].toChars());
            prev = i;
        }
        final int l = splitData.length - prev;
        final char[] arr = new char[l];
        System.arraycopy(splitData,prev,arr,0,l);
        assertArrayEquals(arr,shared[idx].toChars());
    }
    @Test @Order(18)
    void testSharedCharArrayIntArray() {
        testSharedSplit(11);
        testSharedSplit(11,16);
        assertThrows(IllegalArgumentException.class,() -> Sequence.shared(splitData,13,11));
    }
    
    @Test @Order(19)
    void testSplitInt() {
        {
            final Sequence[] split = new Sequence(splitData).split(11);
            assertEquals(2,split.length);
            assertEquals(new Sequence(0,11,splitData),split[0]);
            assertEquals(new Sequence(11,splitData),split[1]);
        }
        {
            final Sequence[] split = new Sequence(1,18,splitData).split(10);
            assertEquals(2,split.length);
            assertEquals(new Sequence(1,11,splitData),split[0]);
            assertEquals(new Sequence(11,18,splitData),split[1]);
        }
    }
    
    private static void testCharSplit(final Sequence[] split,final int...indices) {
        assertNotNull(split);
        int prev = 0,idx = 0;
        for(final int i : indices) {
            final int l = i - prev;
            final char[] arr = new char[l];
            System.arraycopy(splitData,prev,arr,0,l);
            assertArrayEquals(arr,split[idx++].toChars());
            prev = i + 1;
        }
        final int l = splitData.length - prev;
        final char[] arr = new char[l];
        System.arraycopy(splitData,prev,arr,0,l);
        assertArrayEquals(arr,split[idx].toChars());
    }
    
    @Test @Order(20)
    void testBasicSplit() {
        final Sequence s = new Sequence(splitData);
        testCharSplit(s.basicSplit(','),1,8,10,15);
        testCharSplit(s.basicSplit('a'),11,19);
    }
    
    @Test @Order(21)
    void testRepeat() {
        assertEquals(
            new Sequence("abc").repeat(15),
            new Sequence("abc".repeat(15))
        );
        assertEquals(
            new Sequence("abc").repeat(8),
            new Sequence("abc".repeat(8))
        );
        assertEquals(
            new Sequence("a").repeat(2),
            new Sequence("a".repeat(2))
        );
    }
}





































