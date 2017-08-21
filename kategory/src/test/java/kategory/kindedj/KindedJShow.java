package kategory.kindedj;

import io.kindedj.HK;

public interface KindedJShow<F> {
    <A> String show(HK<F, A> hk);
}
