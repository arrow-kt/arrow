package arrow.generics.examples

public sealed interface Tree<A>
public data class Leaf<A>(val x: A) : Tree<A>
public data class Node<A>(val left: Tree<A>, val right: Tree<A>) : Tree<A>
