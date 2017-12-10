package kategory.kindedj.fromkindedj;

import io.kindedj.Hk;
import kategory.kindedj.KindedJShow;

public class KJDataclassKindedJShow implements KindedJShow<KJDataclassHK> {
    public static KJDataclassKindedJShow INSTANCE = new KJDataclassKindedJShow();

    private KJDataclassKindedJShow() {
    }

    @Override
    public <A> String show(Hk<KJDataclassHK, A> hk) {
        return KJDataclassHK.show(hk);
    }
}
