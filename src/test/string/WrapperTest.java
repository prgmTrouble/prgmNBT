package test.string;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static util.string.Sequence.EMPTY;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import util.string.Sequence;
import util.string.Wrapper;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class WrapperTest {
    private static final Sequence data = new Sequence("asdf".toCharArray());
    private static void match(final Wrapper w,
                              final Sequence prefix,
                              final Sequence suffix) {
        assertEquals(new Sequence(Sequence.merge(prefix,suffix)),w.concat());
        assertEquals(new Sequence(Sequence.merge(prefix,data,suffix)),w.push(data).concat());
    }
    private static final char O = '[',C = ']',S = '"';
    private static final Sequence OS = new Sequence(O),CS = new Sequence(C),SS = new Sequence(S);
    private static final char[] OA = {'P',O},CA = {C,'S'};
    private static final Sequence OAS = new Sequence(OA),CAS = new Sequence(CA);
    
    @Test @Order(1)
    void testWrapperChar() {match(new Wrapper(S),SS,SS);}
    
    @Test @Order(2)
    void testWrapperCharBoolean() {
        match(new Wrapper(O,true),OS,EMPTY);
        match(new Wrapper(C,false),EMPTY,CS);
    }
    
    @Test @Order(3)
    void testWrapper2Char() {match(new Wrapper(O,C),OS,CS);}
    
    @Test @Order(4)
    void testWrapper2CharArray() {match(new Wrapper(OA,CA),OAS,CAS);}
    
    @Test @Order(5)
    void testWrap() {
        assertEquals(new Sequence(Sequence.merge(SS,SS)),Wrapper.wrap(EMPTY,S));
        assertEquals(new Sequence(Sequence.merge(SS,data,SS)),Wrapper.wrap(data,S));
    }
    
}
