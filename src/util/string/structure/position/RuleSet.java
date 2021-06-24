package util.string.structure.position;

import util.string.structure.Rule;

public class RuleSet {
    private static final int N_ADJACENCIES = Adjacency.values().length,
                             N_FOLD_STATUSES = FoldingStatus.values().length,
                             N_TOKENS = Token.values().length;
    private static Rule[][][] init() {
        return new Rule[N_ADJACENCIES]
                       [N_FOLD_STATUSES]
                       [N_TOKENS];
    }
    private final Rule[][][] rules = init();
    
    public RuleSet setRule(final Adjacency adjacency,
                           final FoldingStatus foldingStatus,
                           final Token token,
                           final Rule rule) {
        rules[adjacency.ordinal()]
             [foldingStatus.ordinal()]
             [token.ordinal()]
             = rule;
        return this;
    }
    public Rule getRule(final Adjacency adjacency,
                        final FoldingStatus foldingStatus,
                        final Token token) {
        return rules[adjacency.ordinal()]
                    [foldingStatus.ordinal()]
                    [token.ordinal()];
    }
}