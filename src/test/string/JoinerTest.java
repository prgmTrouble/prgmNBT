package test.string;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static util.string.Sequence.EMPTY;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import util.string.Joiner;
import util.string.Sequence;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class JoinerTest {
    private static void match(final Joiner j,final int n,
                              final Sequence prefix,
                              final Sequence suffix,
                              final Sequence sep) {
        final char[] data = new char[n+Math.max(0,n-1)*sep.length()+prefix.length()+suffix.length()];
        int cursor = prefix.copyInto(data,0);
        int ts = 0;
        if(n > 0) {
            {
                final Sequence s = new Sequence('0');
                ts += s.length();
                j.push(s);
                cursor = s.copyInto(data,cursor);
            }
            for(int i = 1;i < n;++i) {
                final Sequence s = new Sequence((char)('0'+i));
                ts += s.length();
                j.push(s);
                cursor = s.copyInto(data,sep.copyInto(data,cursor));
            }
        }
        suffix.copyInto(data,cursor);
        assertEquals(
            new Sequence(data),
            j.concat(),
            "ts:"+ts
        );
    }
    private static void match4(final Joiner j,
                               final Sequence prefix,
                               final Sequence suffix,
                               final Sequence sep) {
        for(int i = 0;i < 4;++i) match(j,i,prefix,suffix,sep);
    }
    private static final char O = '[',C = ']',S = ',';
    private static final Sequence OS = new Sequence(O),CS = new Sequence(C),SS = new Sequence(S);
    private static final char[] OA = {'P',O},CA = {C,'S'},SA = {'_',S,'_'};
    private static final Sequence OAS = new Sequence(OA),CAS = new Sequence(CA),SAS = new Sequence(SA);
    
    @Test @Order(1)
    void testJoiner() {match4(new Joiner(),EMPTY,EMPTY,SS);}
    
    @Test @Order(2)
    void testJoiner3Char() {match4(new Joiner(O,C,S),OS,CS,SS);}
    
    @Test @Order(3)
    void testJoiner3CharArray() {match4(new Joiner(OA,CA,SA),OAS,CAS,SAS);}
    
    @Test @Order(4)
    void testJoiner2CharArray() {match4(new Joiner(OA,CA),OAS,CAS,SS);}
    
    @Test @Order(5)
    void testJoiner2Char() {match4(new Joiner(O,C),OS,CS,SS);}
    
    @Test @Order(6)
    void testJoinerSequenceArray() {
        match4(new Joiner(OAS,EMPTY),OAS,EMPTY,SS);
        match4(new Joiner(OAS,CAS),OAS,CAS,SS);
        match4(new Joiner(OAS,CAS,SAS),OAS,CAS,SAS);
    }
    
}