package katz

class Test<A> private constructor(
        val head: A,
        val tail: Array<A>,
        val array: Array<A>) {

    constructor(head: A, tail: Array<A>) : this(head, tail, tail.copyOfRange(0, 0).plus(head).plus(tail))

}