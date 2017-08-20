package kategory.kindedj;

import io.kindedj.HK;

public interface KindJShow<F> {
    <A> String show(HK<F, A> hk);
}
