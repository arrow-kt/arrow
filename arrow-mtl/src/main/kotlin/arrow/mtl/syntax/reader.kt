package arrow.mtl.syntax

import arrow.MonadReader
import arrow.data.ReaderApi
import arrow.data.ReaderKindPartial

inline fun <reified D> ReaderApi.monadReader(): MonadReader<ReaderKindPartial<D>, D> = arrow.monadReader()