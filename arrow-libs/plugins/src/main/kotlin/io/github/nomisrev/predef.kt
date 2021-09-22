package io.github.nomisrev

import org.gradle.api.provider.Property

infix fun <T> Property<T>.by(value: T) {
  set(value)
}