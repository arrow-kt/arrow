package arrow.optics.plugin

import kotlin.test.Test

/** Target selection (algo §2.3) and the handling of ineligible classes (review §3.1). */
class TargetTests {

  @Test
  fun `explicit LENS target still generates the base lens`() {
    """
      |$`package`
      |$imports
      |@optics([OpticsTarget.LENS])
      |data class OnlyLens(val x: Int) { companion object }
      |
      |val l: Lens<OnlyLens, Int> = OnlyLens.x
      |val r = l.get(OnlyLens(5)) == 5
      """.evals("r" to true)
  }

  @Test
  fun `PRISM target on a data class generates nothing (empty intersection)`() {
    """
      |$`package`
      |$imports
      |@optics([OpticsTarget.PRISM])
      |data class OnlyPrism(val x: Int) { companion object }
      |
      |val l = OnlyPrism.x
      """.compilationFails()
  }

  @Test
  fun `ISO target on a value class generates the iso`() {
    """
      |$`package`
      |$imports
      |@optics([OpticsTarget.ISO]) @JvmInline
      |value class OnlyIso(val v: Int) { companion object }
      |
      |val i: Iso<OnlyIso, Int> = OnlyIso.v
      |val r = i.get(OnlyIso(2)) == 2
      """.evals("r" to true)
  }

  @Test
  fun `ineligible class is rejected`() {
    // A plain class is not data/value/sealed: it is reported, and no optics are generated.
    """
      |$`package`
      |$imports
      |@optics
      |class Plain(val x: Int) { companion object }
      """.failsWith { "Only data classes, value classes with a single field, and sealed classes and interfaces can be annotated with @optics" in it }
  }
}
