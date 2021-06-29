package util.string;

import java.util.HashMap;

import util.container.Queue;

/**
 * A {@linkplain Builder} which manages the indentation of lines.
 * 
 * @author prgmTrouble
 * @author AzureTriple
 */
public class Indenter implements Builder {
    /**A sequence of exactly 4 space (<code>' '</code>, or <code>'\u0020'</code>) characters.*/
    private static final Sequence DEFAULT_INDENT = new Sequence(' ',4);
    
    private final Sequence indent;
    private final HashMap<Integer,Sequence> indentCache = new HashMap<>();
    
    private class Line {
        private final Sequence content;
        private int indentCount;
        private int size;
        
        private Line(final Sequence content,final int indentCount) {
            size = (this.content = content) == null
                    ? 0
                    : content.length() + indentCount * indent.length();
            this.indentCount = indentCount - 1;
        }
        private int pop(final char[] buf,int cursor) {
            if(size == 0) return cursor;
            if(indentCount != -1) {
                final Sequence ws;
                {
                    final Sequence mapped = indentCache.get(indentCount);
                    if(mapped == null) indentCache.put(indentCount,ws = indent.repeat(indentCount + 1));
                    else ws = mapped;
                }
                cursor = ws.copyInto(buf,cursor);
            }
            return content.copyInto(buf,cursor);
        }
    }
    private final Queue<Line> lines = new Queue<>();
    private int indents = 0,size = 0;
    
    /**
     * Creates an indenter with the specified indentation sequence. A
     * <code>null</code> value yields an empty sequence.
     */
    public Indenter(final Sequence indent) {
        this.indent = indent == null? Sequence.EMPTY
                                    : indent;
    }
    /**Creates an indenter using {@linkplain #DEFAULT_INDENT}.*/
    public Indenter() {this(DEFAULT_INDENT);}
    
    public Indenter increaseIndent() {++indents; return this;}
    public Indenter decreaseIndent() {--indents; return this;}
    
    @Override
    public Indenter push(final Sequence s) {
        final Line l = new Line(s,indents);
        lines.push(l);
        size += l.size;
        return this;
    }
    @Override
    public Indenter push(final Builder b) {
        if(b instanceof Indenter) {
            final Queue<Line> in = ((Indenter)b).lines;
            final int ds = indents * indent.length();
            while(!in.empty()) {
                final Line l = in.pop();
                l.indentCount += indents;
                l.size += ds;
                lines.push(l);
                size += l.size;
            }
        } else if(b != null) push(b.concat());
        return this;
    }
    
    @Override
    public Sequence concat() {
        if(lines.empty()) return Sequence.EMPTY;
        final char[] buf = new char[lines.size() - 1 + size];
        for(
            int cursor = lines.pop().pop(buf,0); // Copy first line.
            cursor < buf.length;
            cursor = lines.pop().pop(buf,cursor) // Copy next line.
        ) buf[cursor++] = '\n'; // Add a line break before subsequent lines.
        return new Sequence(buf);
    }
}