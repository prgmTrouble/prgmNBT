package util.string.outline.position;

import util.string.Sequence;
import util.string.outline.Outline;

public class FoldingTokenSet {
    private final Sequence[][] tokens;
    {
        tokens = new Sequence[Token.values().length]
                             [FoldingStatus.values().length];
    }
    
    public FoldingTokenSet addToken(final Token token,
                                    final FoldingStatus fs,
                                    final Sequence value) {
        tokens[token.ordinal()][fs.ordinal()] = value;
        return this;
    }
    public Sequence getToken(final Token token,
                             final FoldingStatus fs,
                             final Outline ol) {
        final Sequence[] out = {ol.apply(Adjacency.before,fs,token),
                                tokens[token.ordinal()][fs.ordinal()],
                                ol.apply(Adjacency.after,fs,token)};
        return new Sequence(Sequence.merge(out));
    }
}