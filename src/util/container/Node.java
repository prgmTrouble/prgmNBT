package util.container;

class Node<V> {
    final V v;
    Node<V> n;
    
    Node(final V v,final Node<V> n) {this.v = v; this.n = n;}
    Node(final V v) {this.v = v; n = null;}
    
    @Override
    protected Node<V> clone() {return new Node<>(v,n == null? null : n.clone());}
    
    Node<V> shallowClone() {return new Node<>(v,n);}
}