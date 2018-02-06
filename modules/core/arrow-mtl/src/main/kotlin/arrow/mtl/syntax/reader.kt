package arrow.mtl.syntax

import arrow.mtl.MonadReader
import arrow.data.ReaderApi
import arrow.data.ReaderKindPartial

inline fun <reified D> ReaderApi.monadReader(): MonadReader<ReaderKindPartial<D>, D> = arrow.mtl.monadReader()