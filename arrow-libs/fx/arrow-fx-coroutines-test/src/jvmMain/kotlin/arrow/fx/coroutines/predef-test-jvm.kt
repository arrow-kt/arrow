package arrow.fx.coroutines

import kotlinx.atomicfu.atomic
import java.util.concurrent.ThreadFactory

val singleThreadName = "single"
val single = Resource.singleThreadContext(singleThreadName)

val threadName: suspend () -> String =
  { Thread.currentThread().name }

class NamedThreadFactory(private val mkName: (Int) -> String) : ThreadFactory {
  private val count = atomic(0)
  override fun newThread(r: Runnable): Thread =
    Thread(r, mkName(count.value))
      .apply { isDaemon = true }
}

// /**
//  * Catches `System.err` output, for testing purposes.
//  */
// fun catchSystemErr(thunk: () -> Unit): String {
//   val outStream = ByteArrayOutputStream()
//   catchSystemErrInto(outStream, thunk)
//   return String(outStream.toByteArray(), StandardCharsets.UTF_8)
// }
//
// /**
//  * Catches `System.err` output into `outStream`, for testing purposes.
//  */
// @Synchronized
// fun <A> catchSystemErrInto(outStream: OutputStream, thunk: () -> A): A {
//   val oldErr = System.err
//   val fakeErr = PrintStream(outStream)
//   System.setErr(fakeErr)
//   return try {
//     thunk()
//   } finally {
//     System.setErr(oldErr)
//     fakeErr.close()
//   }
// }
