package arrow

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@MustBeDocumented
annotation class extension

val given: Nothing
  get() = TODO("Should have been replaced by Arrow Meta Compiler Plugin provided by [apply plugin: 'arrow.meta.plugin.gradle']")

fun <A> given(): A =
  TODO("Should have been replaced by Arrow Meta Compiler Plugin provided by [apply plugin: 'arrow.meta.plugin.gradle']")
