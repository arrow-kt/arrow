/*
 * Copyright Sergey Igushkin https://github.com/h0tk3y
 *
 * This file adapted and modified to fit this project originally comes from
 * https://github.com/h0tk3y/kotlin-monads/blob/master/src/main/kotlin/com/github/h0tk3y/kotlinMonads/ContinuationUtils.kt
 */

package katz

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