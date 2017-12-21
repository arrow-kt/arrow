package arrow.kindedj;

import io.kindedj.Hk;

public interface KindedJShow<F> {
    <A> String show(Hk<F, A> hk);
}
