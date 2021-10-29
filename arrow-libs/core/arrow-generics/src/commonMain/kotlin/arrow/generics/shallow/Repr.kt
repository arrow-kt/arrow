package arrow.generics.shallow

// this is the type which encodes representations
// at the type level
public sealed interface Repr
// we always have a sum...
public sealed interface SumRepr : Repr
public interface Choice<out A : ProductRepr, out Rest : SumRepr> : SumRepr
// ... with products inside
public sealed interface ProductRepr : Repr
public interface Field<out A, out Rest : ProductRepr> : ProductRepr
// we finish with both
public interface End : SumRepr, ProductRepr
