package arrow.syntax.partialfunction

import arrow.PartialFunction
import arrow.Tuple2

fun <A, B> case(ff: Tuple2<(A) -> Boolean, (A) -> B>): PartialFunction<A, B> =
        object : PartialFunction<A, B>() {
            override fun isDefinedAt(a: A): Boolean = ff.a(a)
            override fun invoke(a: A): B = ff.b(a)
        }

infix fun <A, B> ((A) -> Boolean).then(f: (A) -> B): Tuple2<(A) -> Boolean, (A) -> B> = Tuple2(this, f)
