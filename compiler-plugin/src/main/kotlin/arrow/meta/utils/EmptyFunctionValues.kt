package arrow.meta.utils

val NoOp1: (Any?) -> Unit = { _ -> Unit }
val NoOp2: (Any?, Any?) -> Unit = { _, _ -> Unit }
val NoOp3: (Any?, Any?, Any?) -> Unit = { _, _, _ -> Unit }
val NoOp4: (Any?, Any?, Any?, Any?) -> Unit = { _, _, _, _ -> Unit }
val NoOp5: (Any?, Any?, Any?, Any?, Any?) -> Unit = { _, _, _, _, _ -> Unit }
val NoOp6: (Any?, Any?, Any?, Any?, Any?, Any?) -> Unit = { _, _, _, _, _, _ -> Unit }

fun <A> NullableOp1(): (Any?, Any?) -> A? = { _, _ -> null }