package kategory

@higherkind data class ListKW<A> constructor(val list: List<A>) : ListKWKind<A> {

    fun <B> map(f: (A) -> B): ListKW<B> = ListKW(list.map(f))

    fun <B> flatMap(f: (A) -> ListKW<B>): ListKW<B> = ListKW(list.flatMap { f(it).list })

    operator fun plus(list: List<A>): ListKW<A> = ListKW(this.list + list)

    operator fun plus(listKW: ListKW<A>): ListKW<A> = ListKW(this.list + listKW.list)

    operator fun get(position: Int): Option<A> = if (list.isEmpty() || position < 0 || position > list.size) Option.None else Option.Some(list[position])

    fun <B> fold(b: B, f: (B, A) -> B): B = list.fold(b, f)

    companion object : ListKWInstances, GlobalInstance<Monad<ListKWHK>>() {

        @JvmStatic fun <A> listOfK(vararg a: A): ListKW<A> = ListKW(a.asList())
        @JvmStatic fun <A> listOfK(list: List<A>): ListKW<A> = ListKW(list)

        fun functor(): Functor<ListKWHK> = this

        fun applicative(): Applicative<ListKWHK> = this

        fun monad(): Monad<ListKWHK> = this

        fun <A> semigroup(): Semigroup<ListKW<A>> = object : ListKWMonoid<A> {}

        fun semigroupK(): SemigroupK<ListKWHK> = object : ListKWMonoidK {}

        fun <A> monoid(): ListKWMonoid<A> = object : ListKWMonoid<A> {}

        fun monoidK(): MonoidK<ListKWHK> = object : ListKWMonoidK {}

        fun traverse(): Traverse<ListKWHK> = this

    }

}

fun <A> List<A>.k(): ListKW<A> = ListKW.listOfK(this)

fun <A> ListKW<A>.drop(n: Int): ListKW<A> = this.list.drop(n).k()

fun <A> ListKW<A>.first(): A = this.list.first()
