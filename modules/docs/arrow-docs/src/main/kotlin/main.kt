import arrow.effects.*
import arrow.effects.instances.io.applicative.applicative

fun main(args: Array<String>) {
  //sampleStart
  val conn: IOConnection = KindConnection.uncancelable(IO.applicative())
  //sampleEnd
}