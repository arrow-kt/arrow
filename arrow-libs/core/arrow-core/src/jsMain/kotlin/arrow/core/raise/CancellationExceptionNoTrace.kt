package arrow.core.raise

@OptIn(DelicateRaiseApi::class)
@Suppress(
  "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING",
  "SEALED_INHERITOR_IN_DIFFERENT_MODULE"
)
private class NoTraceImpl(raised: Any?, raise: Raise<Any?>) : RaiseCancellationException(raised, raise)

private object SpecialRaised
private object SpecialRaise : Raise<Any?> {
  override fun raise(r: Any?): Nothing = error("Should never be called")
}

private val noTrace = NoTraceImpl(raised = SpecialRaised, raise = SpecialRaise)

private fun entriesOf(jsObject: dynamic): List<Pair<String, dynamic>> =
  (js("Object").entries(jsObject) as Array<Array<dynamic>>)
    .map { entry -> entry[0] as String to entry[1] }

private val noTraceKeys: Pair<String, String> = run {
  val entries = entriesOf(js("Object").getOwnPropertyDescriptors(noTrace))
  entries.first { (key, value) -> value.value == SpecialRaised }.first to entries.first { (key, value) -> value.value == SpecialRaise }.first
} // Seems to always just be raised_1 and raise_1, but we compute them on startup to be safe.

@DelicateRaiseApi
internal actual fun NoTrace(raised: Any?, raise: Raise<Any?>): RaiseCancellationException {
  val descs = js("Object").getOwnPropertyDescriptors(noTrace)
  descs[noTraceKeys.first].value = raised
  descs[noTraceKeys.second].value = raise
  return js("Object").defineProperties(js("Object").create(js("Object").getPrototypeOf(noTrace)), descs)
}
