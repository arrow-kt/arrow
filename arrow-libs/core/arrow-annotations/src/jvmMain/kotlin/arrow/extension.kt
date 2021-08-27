package arrow

@Deprecated("This lives now in Arrow Meta", level = DeprecationLevel.HIDDEN)
public val given: Nothing
  get() = TODO("Should have been replaced by Arrow Meta Compiler Plugin provided by [plugins { id 'io.arrow-kt.arrow' version 'x.x.x' }")

@Deprecated("This lives now in Arrow Meta", level = DeprecationLevel.HIDDEN)
public fun <A> given(): A =
  TODO("Should have been replaced by Arrow Meta Compiler Plugin provided by [plugins { id 'io.arrow-kt.arrow' version 'x.x.x' }]")
