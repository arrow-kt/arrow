package kategory

fun <A> IdKind<A>.value(): A = this.ev().value

@higherkind
@deriving(Bimonad::class, Traverse::class)
data class Id<out A>(val value: A) : IdKind<A> {

    inline fun <B> map(f: (A) -> B): Id<B> = Id(f(value))

    inline fun <B> flatMap(f: (A) -> IdKind<B>): Id<B> = f(value).ev()

    fun <B> foldL(b: B, f: (B, A) -> B): B = f(b, this.ev().value)

    fun <B> foldR(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> = f(this.ev().value, lb)

    fun <G, B> traverse(f: (A) -> HK<G, B>, GA: Applicative<G>): HK<G, Id<B>> = GA.map(f(this.ev().value), { Id(it) })

    fun <B> coflatMap(f: (IdKind<A>) -> B): Id<B> = this.ev().map({ f(this) })

    fun extract(): A = this.ev().value

    companion object {

        tailrec fun <A, B> tailRecM(a: A, f: (A) -> IdKind<Either<A, B>>): Id<B> {
            val x: Either<A, B> = f(a).ev().value
            return when (x) {
                is Either.Left<A, B> -> tailRecM(x.a, f)
                is Either.Right<A, B> -> Id(x.b)
            }
        }

        fun <A> pure(a: A): Id<A> = Id(a)

        fun functor(): IdHKBimonadInstance = Id.bimonad()

        fun applicative(): IdHKBimonadInstance = Id.bimonad()

        fun monad(): IdHKBimonadInstance = Id.bimonad()

        fun comonad(): IdHKBimonadInstance = Id.bimonad()

        fun foldable(): IdHKTraverseInstance = Id.traverse()

    }

}

