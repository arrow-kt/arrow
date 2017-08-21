package kategory

@higherkind data class ListKW<A> constructor(val list: List<A>) : ListKWKind<A>, List<A> by list {

    companion object : ListKWInstances, GlobalInstance<Monad<ListKWHK>>() {

        fun functor(): Functor<ListKWHK> = this

        fun applicative(): Applicative<ListKWHK> = this

        fun monad(): Monad<ListKWHK> = this

        fun <A> semigroup(): Semigroup<ListKW<A>> = object : ListKWMonoid<A> {}

        fun semigroupK(): SemigroupK<ListKWHK> = object : ListKWMonoidK {}

        fun <A> monoid(): ListKWMonoid<A> = object : ListKWMonoid<A> {}

        fun monoidK(): MonoidK<ListKWHK> = object : ListKWMonoidK {}

        fun traverse(): Traverse<ListKWHK> = this

        fun foldable(): Foldable<ListKWHK> = this

    }

}

fun <A> List<A>.k(): ListKW<A> = ListKW(this)
