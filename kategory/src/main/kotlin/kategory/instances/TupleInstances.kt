package kategory

fun <A, B> monoidTuple(MA: Monoid<A>, MB: Monoid<B>): Monoid<Tuple2<A, B>> =
        object: Monoid<Tuple2<A, B>> {
            override fun combine(x: Tuple2<A, B>, y: Tuple2<A, B>): Tuple2<A, B> {
                val (xa, xb) = x
                val (ya, yb) = y
                return Tuple2(MA.combine(xa, ya), MB.combine(xb, yb))
            }
            override fun empty(): Tuple2<A, B> = Tuple2(MA.empty(), MB.empty())
        }

