package arrow.streams.internal

/**
 * https://github.com/monix/monix/issues/349
 * Non fatal from Monix. https://github.com/monix/monix/blob/v2.3.3/monix-execution/shared/src/main/scala/monix/execution/misc/NonFatal.scala
 */
fun <A> catchNonFatal(trie: () -> A, catch: (Throwable) -> A): A = try {
  trie()
} catch (VME: VirtualMachineError) {
  catch(VME)
}