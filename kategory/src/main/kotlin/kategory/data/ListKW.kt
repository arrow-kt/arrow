package kategory

typealias ListKindW<A> = HK<ListKW.F, A>

fun <A> ListKindW<A>.ev(): ListKW<A> =
        this as ListKW<A>

data class ListKW<A> constructor(val list: List<A>) : ListKindW<A>, Collection<A> by list {

    class F private constructor()

    fun <B> map(f: (A) -> B): ListKW<B> =
            ListKW(list.map(f))

    fun <B> flatMap(f: (A) -> ListKW<B>): ListKW<B> =
            ListKW(list.flatMap { f(it).list })

    operator fun plus(list: List<A>): ListKW<A> =
            ListKW(this.list + list)

    operator fun plus(listKW: ListKW<A>): ListKW<A> =
            ListKW(this.list + listKW.list)

    operator fun get(position: Int): Option<A> =
            if (list.isEmpty() || position < 0 || position > list.size) Option.None else Option.Some(list[position])

    fun <B> fold(b: B, f: (B, A) -> B): B =
            list.fold(b, f)

    companion object : ListKWInstances, GlobalInstance<Monad<ListKW.F>>() {

        @JvmStatic fun <A> listOfK(vararg a: A): ListKW<A> = ListKW(if (a.isEmpty()) emptyList() else a.asList())
        @JvmStatic fun <A> listOfK(list: List<A>): ListKW<A> = ListKW(list)

        fun functor(): Functor<ListKW.F> = this

        fun applicative(): Applicative<ListKW.F> = this

        fun monad(): Monad<ListKW.F> = this

        fun <A> semigroup(): Semigroup<ListKW<A>> = object : ListKWSemigroup<A> {}

        fun semigroupK(): SemigroupK<ListKW.F> = object : ListKWSemigroupK {}

        fun <A> monoid(): ListKWMonoid<A> = object : ListKWMonoid<A> {}

    }

}

fun <A> List<A>.toListKW(): ListKW<A> =
        ListKW.listOfK(this)
