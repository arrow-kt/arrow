package kategory.kindedj.fromkindedj;

import io.kindedj.HK;
import kategory.kindedj.KindJShow;

public class KJDataclassKindJShow implements KindJShow<KJDataclassHK> {
    public static KJDataclassKindJShow INSTANCE = new KJDataclassKindJShow();

    private KJDataclassKindJShow() {
    }

    @Override
    public <A> String show(HK<KJDataclassHK, A> hk) {
        return KJDataclassHK.value(hk).toString();
    }
}
