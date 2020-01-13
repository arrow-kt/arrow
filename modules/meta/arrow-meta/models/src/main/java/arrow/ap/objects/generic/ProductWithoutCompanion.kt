package arrow.ap.objects.generic

import arrow.core.Option
import arrow.product

@product
data class ProductWithoutCompanion(val field: String, val option: Option<String>)
