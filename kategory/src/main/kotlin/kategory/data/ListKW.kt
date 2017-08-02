package kategory

typealias ListKindW<A> = HK<ListKW.F, A>

fun <A> ListKindW<A>.ev(): ListKW<A> =
        this as ListKW<A>

class ListKW<A> private constructor(private val list: List<A>) : ListKindW<A>, Collection<A> by list {

    class F private constructor()

    fun <B> map(f: (A) -> B): ListKW<B> = ListKW(list.map(f))

    fun <B> flatMap(f: (A) -> ListKW<B>): ListKW<B> = ListKW(list.flatMap { f(it).list })

    operator fun plus(list: List<@UnsafeVariance A>): ListKW<A> = ListKW(this.list + list)

    operator fun plus(listKW: ListKW<@UnsafeVariance A>): ListKW<A> = ListKW(this.list + listKW.list)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as ListKW<*>

        if (list != other.list) return false

        return true
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }

    companion object : ListKWInstances, GlobalInstance<Monad<ListKW.F>>() {
        @JvmStatic fun <A> listOfK(vararg a: A): ListKW<A> = ListKW(if (a.isEmpty()) emptyList() else a.asList())

        fun functor(): Functor<ListKW.F> = this

        fun applicative(): Applicative<ListKW.F> = this

        fun monad(): Monad<ListKW.F> = this

        fun <A> semigroup(): Semigroup<ListKW<A>> = object : ListKWSemigroup<A> {}

        fun semigroupK(): SemigroupK<ListKW.F> = object : ListKWSemigroupK {}

        fun <A> monoid(): ListKWMonoid<A> = object : ListKWMonoid<A> {}

    }

}
