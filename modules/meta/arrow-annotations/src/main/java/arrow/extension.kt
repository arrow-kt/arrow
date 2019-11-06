package arrow

@Retention(AnnotationRetention.RUNTIME)
@Target(
  AnnotationTarget.CLASS,
  AnnotationTarget.LOCAL_VARIABLE,
  AnnotationTarget.FUNCTION
)
@MustBeDocumented
annotation class extension

val given: Nothing
  get() = TODO("Should have been replaced by Arrow Meta Compiler Plugin provided by [plugins { id 'io.arrow-kt.arrow' version 'x.x.x' }")

fun <A> given(): A =
  TODO("Should have been replaced by Arrow Meta Compiler Plugin provided by [plugins { id 'io.arrow-kt.arrow' version 'x.x.x' }]")
