package katz

typealias ReaderKind<D, A> = Kleisli<Id.F, D, A>

class Reader<D, A>(val k: ReaderKind<D, A>) {

    companion object Factory {

        operator fun <D, A> invoke(fa : (D) -> A): Reader<D, A> = Reader(Kleisli(fa.andThen { Id(it) }))

        fun <D, A> pure(x: A): Reader<D, A> = Reader { _ -> x }

        fun <D> ask(): Reader<D, D> = Reader { it }

    }

    fun <DD> local(f: (DD) -> D): Reader<DD, A> = Reader(k.local(f))

}

infix fun <A, B, C> ((B) -> C).compose(f: (A) -> B): (A) -> C = { a: A -> this(f(a)) }

infix fun <A, B, C> ((A) -> B).andThen(g: (B) -> C): (A) -> C = { a: A -> g(this(a)) }

fun <D, A, B> Reader<D, A>.map(fa: (A) -> B): Reader<D, B> = Reader(k.map(IdMonad, fa.andThen { Id(it).value }))

fun <D, A, B> Reader<D, A>.flatMap(fa: (A) -> Reader<D, B>): Reader<D, B> = Reader(k.flatMap(IdMonad, fa.andThen { it.k }))

fun <D, A, B> Reader<D, A>.zip(o: Reader<D, B>) = Reader(k.zip(IdMonad, o.k))

fun <D, A> Reader<D, A>.run(d : D): A = k.run(d).value()

fun <D, A> ((D) -> A).reader(): Reader<D, A> = Reader(this)
