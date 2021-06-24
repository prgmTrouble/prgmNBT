package util.string;

public class StructureFormatter implements Builder {
    
    private final Wrapper wrapper;
    private final Joiner joiner;
    public StructureFormatter(final Wrapper wrapper,
                              final Sequence separator) {
        this.wrapper = wrapper;
        joiner = new Joiner(null,null,separator);
    }
    
    @Override
    public Builder push(Sequence s) {
        
        return null;
    }
    
    @Override
    public Sequence concat() {
        return null;
    }
    
}
