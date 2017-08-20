package kategory.kindedj.fromkindedj;

import io.kindedj.HK;
import kategory.kindedj.KindJShow;

public class KJDataclassKindedJShow implements KindJShow<KJDataclassHK> {
    public static KJDataclassKindedJShow INSTANCE = new KJDataclassKindedJShow();

    private KJDataclassKindedJShow() {
    }

    @Override
    public <A> String show(HK<KJDataclassHK, A> hk) {
        return KJDataclassHK.value(hk).toString();
    }
}
