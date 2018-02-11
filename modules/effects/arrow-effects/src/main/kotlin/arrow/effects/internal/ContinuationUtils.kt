package arrow.effects.internal

import kotlin.coroutines.experimental.Continuation

private val coroutineImplClass by lazy { Class.forName("kotlin.coroutines.experimental.jvm.internal.CoroutineImpl") }

private val labelField by lazy { coroutineImplClass.getDeclaredField("label").apply { isAccessible = true } }

private val completionField by lazy { coroutineImplClass.getDeclaredField("completion").apply { isAccessible = true } }

private var <T> Continuation<T>.label
    get() = labelField.get(this)
    set(value) = labelField.set(this@label, value)

private var <T> Continuation<T>.completion: Continuation<*>?
    get() = completionField.get(this) as Continuation<*>
    set(value) = completionField.set(this@completion, value)

internal var <T> Continuation<T>.stackLabels: List<Any>
    get() = if (coroutineImplClass.isInstance(this)) listOf(label) + completion?.stackLabels.orEmpty() else emptyList()
    set(value) {
        if (coroutineImplClass.isInstance(this)) {
            label = value.first()
            completion?.stackLabels = value.subList(1, value.size)
        }
    }
