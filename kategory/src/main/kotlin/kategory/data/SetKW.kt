package kategory

@higherkind
@deriving(Foldable::class, MonoidK::class)
data class SetKW<out A>(val set: Set<A>) : SetKWKind<A>, Set<A> by set {

    fun <B> foldL(b: B, f: (B, A) -> B): B = this.fold(b, f)

    fun <B> foldR(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> {
        fun loop(fa_p: SetKW<A>): Eval<B> = when {
            fa_p.set.isEmpty() -> lb
            else -> f(fa_p.set.first(), Eval.defer { loop(fa_p.set.drop(1).toSet().k()) })
        }
        return Eval.defer { loop(this) }
    }

    companion object {

        fun <A> pure(a: A): SetKW<A> = setOf(a).k()

        fun <A> empty(): SetKW<A> = emptySet<A>().k()

        fun <A> semigroup(): SetKWMonoid<A> = object : SetKWMonoid<A> {}

        fun semigroupK(): SetKWHKMonoidKInstance = SetKW.monoidK()
    }
}

fun <A> SetKW<A>.combineK(y: SetKWKind<A>): SetKW<A> = (this.set + y.ev().set).k()

fun <A> Set<A>.k(): SetKW<A> = SetKW(this)