package arrow.fx.coroutines.stream

import arrow.core.Right
import arrow.core.extensions.list.foldable.foldMap
import arrow.fx.coroutines.ArrowFxSpec
import arrow.fx.coroutines.Atomic
import arrow.fx.coroutines.Resource
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int

class ScopeTest : ArrowFxSpec(spec = {

  "Scope.newRoot creates a parentless Scope" {
    Scope.newRoot().parent shouldBe null
  }

  "Scope.newRoot has no ancestors" {
    Scope.newRoot().parent shouldBe null
  }

  "Scope.newRoot can acquire resources" {
    checkAll(Arb.int()) { i ->
      val scope = Scope.newRoot()

      scope.acquireResource(
        { i },
        { _, _ -> Unit }
      ) shouldBe Right(i)

      scope.resources().size shouldBe 1
    }
  }

  "scope closure" {
    var buffer = listOf<Int>()
    (0..10).fold(Stream.raiseError<Int>(RuntimeException())) { acc, i ->
      Stream.just(i).append {
        Stream.bracket({ i }, { i -> buffer = buffer + i })
          .flatMap { acc }
      }
    }
      .attempt()
      .drain()

    buffer shouldBe (0..10).toList()
  }

  "handleErrorWith closes scopes" {
    val events = Atomic<List<BracketEvent>>(emptyList())

    events.recordBracketEvents(1)
      .flatMap { Stream.raiseError<Unit>(RuntimeException()) }
      .handleErrorWith { Stream.empty() }
      .append { events.recordBracketEvents(2) }
      .drain()

    events.get() shouldBe listOf(Acquired(1), Released(1), Acquired(2), Released(2))
  }

  "last scope extended, not all scopes - 1" {
    val st = Atomic(emptyList<String>())

    Stream.just("start")
      .onFinalize { st.record("first finalize") }
      .onFinalize { st.record("second finalize") }
      .asResource()
      .lastOrError()
      .use { st.record(it) }

    st.get() shouldBe listOf("first finalize", "start", "second finalize")
  }

  "last scope extended, not all scopes - 2" {
    val st = Atomic(emptyList<String>())

    Stream.bracket({ "a" }, { st.record("first finalize") }).append {
      Stream.bracket({ "b" }, { st.record("second finalize") }).append {
        Stream.bracket({ "c" }, { st.record("third finalize") })
      }
    }
      .asResource()
      .lastOrError()
      .use { st.record(it) }

    st.get() shouldBe listOf("first finalize", "second finalize", "c", "third finalize")
  }

  "scope - 1" {
    val c = Atomic(0)

    val s1 = Stream.just("a")
    val s2 = Stream.bracket(
      { assert(c.updateAndGet(Int::inc) == 1) },
      { assert(c.updateAndGet(Int::dec) == 0) }
    ).flatMap { Stream.just("b") }

    s1.scope().append { s2 }
      .take(2)
      .scope()
      .repeat()
      .take(4)
      .drain()

    c.get() shouldBe 0
  }

  "scope - 2" {
    val counter = Counter()

    Stream(1).flatMap {
      Stream.bracketWeak({ counter.increment() }, { counter.decrement() })
        .effectMap { counter.count() }
        .append { Stream.effect { counter.count() } }
    }
      .scope()
      .append { Stream.effect { counter.count() } }
      .toList() shouldBe listOf(1, 1, 0)
  }

  "resource" {
    val st = Atomic(emptyList<String>())

    fun mkRes(s: String): Resource<Unit> =
      Resource({ st.record("acquire $s") }, { _ -> st.record("release $s") })

    // We aim to trigger all the possible cases, and make sure all of them
    // introduce scopes.

    // Allocate
    val res1 = mkRes("1")
    // Bind
    val res2 = mkRes("21").flatMap { mkRes("22") }
    // Suspend
    val res3 = Resource.defer {
      st.record("suspend")
      mkRes("3")
    }

    listOf(res1, res2, res3)
      .foldMap(Stream.monoid(), Stream.Companion::resource)
      .effectTap { st.record("use") }
      .append { Stream.effect_ { st.record("done") } }
      .drain()

    st.get() shouldBe listOf(
      "acquire 1",
      "use",
      "release 1",
      "acquire 21",
      "acquire 22",
      "use",
      "release 22",
      "release 21",
      "suspend",
      "acquire 3",
      "use",
      "release 3",
      "done"
    )
  }

  "resourceWeak" {
    val st = Atomic(emptyList<String>())
    fun mkRes(s: String): Resource<Unit> =
      Resource({ st.record("acquire $s") }, { _ -> st.record("release $s") })

    // We aim to trigger all the possible cases, and make sure none of them
    // introduce scopes.

    // Allocate
    val res1 = mkRes("1")
    // Bind
    val res2 = mkRes("21").flatMap { mkRes("22") }
    // Suspend
    val res3 = Resource.defer {
      st.record("suspend")
      mkRes("3")
    }

    listOf(res1, res2, res3)
      .foldMap(Stream.monoid(), Stream.Companion::resourceWeak)
      .effectTap { st.record("use") }
      .append { Stream.effect_ { st.record("done") } }
      .drain()

    st.get() shouldBe listOf(
      "acquire 1",
      "use",
      "acquire 21",
      "acquire 22",
      "use",
      "suspend",
      "acquire 3",
      "use",
      "done",
      "release 3",
      "release 22",
      "release 21",
      "release 1"
    )
  }
})

sealed class BracketEvent
data class Acquired(val order: Int) : BracketEvent() {
  companion object {
    val one: BracketEvent = Acquired(1)
    val two: BracketEvent = Acquired(2)
  }
}

data class Released(val order: Int) : BracketEvent() {
  companion object {
    val one: BracketEvent = Released(1)
    val two: BracketEvent = Released(2)
  }
}

fun Atomic<List<BracketEvent>>.recordBracketEvents(n: Int): Stream<Unit> =
  Stream.bracket({ update { it + Acquired(n) } }, { update { it + Released(n) } })

suspend fun Atomic<List<String>>.record(s: String): Unit =
  update { it + s }
