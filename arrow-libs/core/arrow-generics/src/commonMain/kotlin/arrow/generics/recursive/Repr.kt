package arrow.generics.recursive

// this is the type which encodes representations
// at the type level
public sealed interface Repr
// we always have a sum...
public typealias TopRepr = SumRepr
public sealed interface SumRepr : Repr
public interface TyS<out A : ProductRepr, out Rest : SumRepr> : SumRepr
public interface EndS : SumRepr
// ... with products inside ...
public sealed interface ProductRepr : Repr
public interface TyP<out A : ValueRepr, out Rest : ProductRepr> : ProductRepr
public interface EndP : ProductRepr
// ... with values inside
public sealed interface ValueRepr : Repr
public interface RecR : ValueRepr
public interface ParR : ValueRepr
public interface FieldR<out A> : ValueRepr

// some simple aliases
public typealias Sum1<Repr1> = TyS<Repr1, EndS>
public typealias Sum2<Repr1, Repr2> = TyS<Repr1, TyS<Repr2, EndS>>
public typealias Product1<Repr1> = TyP<Repr1, EndP>
public typealias Product2<Repr1, Repr2> = TyP<Repr1, TyP<Repr2, EndP>>
