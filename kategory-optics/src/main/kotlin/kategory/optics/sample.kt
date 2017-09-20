package kategory.optics

import kategory.Try
import kategory.Tuple2
import kategory.left
import kategory.right
import kategory.toT

fun <T, R> successTry(): PPrism<Try<T>, Try<R>, T, R> = PPrism(
        {
            when (it) {
                is Try.Success -> it.value.right()
                is Try.Failure -> Try.Failure<R>(it.exception).left()
            }
        },
        { Try.Success(it) }
)

fun <A, B, C> firstTuple(): PLens<Tuple2<A, B>, Tuple2<C, B>, A, C> = PLens(
        { it.a },
        { c -> { ab -> c toT ab.b } }
)

fun main(args: Array<String>) {

    val modify = successTry<Int, Double>().modify { it.toDouble() } (Try { "ffff".toInt() })
    println(modify) //Failure(exception=java.lang.NumberFormatException: For input string: "ffff")

    val modify1 = successTry<Int, Double>().modify { it.toDouble() } (Try { "1".toInt() })
    println(modify1) //Success(value=1.0)

    val changedTuple = firstTuple<Int, String, String>().modify({ "Hello" }, 5 toT "World")
    println(changedTuple) // Tuple2(a=Hello, b=World)

}
