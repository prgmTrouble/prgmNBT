package test.string;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import util.string.Indenter;
import util.string.Sequence;

class IndenterTest {
    
    @Test
    void testNormal() {
        final Indenter i = new Indenter();
        i.push(new Sequence('{'));
        i.increaseIndent();
        i.push(new Sequence("\"a\":b,"));
        i.push(new Sequence("\"c\":d"));
        i.decreaseIndent();
        i.push(new Sequence('}'));
        assertEquals(new Sequence("{\n    \"a\":b,\n    \"c\":d\n}"),i.concat());
    }
    @Test
    void testPushIndenter() {
        final Indenter i = new Indenter();
        i.push(new Sequence("\"a\":b,"));
        i.push(new Sequence("\"c\":d"));
        final Indenter j = new Indenter();
        j.push(new Sequence('{'));
        j.increaseIndent();
        j.push(new Sequence("\"key\":{"));
        j.increaseIndent();
        j.push(i);
        j.decreaseIndent();
        j.push(new Sequence('}'));
        j.decreaseIndent();
        j.push(new Sequence('}'));
        
        assertEquals(new Sequence("{\n    \"key\":{\n        \"a\":b,\n        \"c\":d\n    }\n}"),j.concat());
    }
}
