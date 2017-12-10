package kategory.kindedj.fromkategory;

import io.kindedj.Hk;
import kategory.kindedj.ConvertHK;
import kategory.kindedj.ConvertKt;
import kategory.kindedj.KatDataclassHK;
import kategory.kindedj.KatDataclassKt;
import kategory.kindedj.KindedJShow;

public class KatDataclassKindedJShow implements KindedJShow<Hk<ConvertHK, KatDataclassHK>> {
    private KatDataclassKindedJShow() {
    }

    @Override
    public <A> String show(Hk<Hk<ConvertHK, KatDataclassHK>, A> hk) {
        final kategory.HK<KatDataclassHK, A> cast = ConvertKt.convert(hk);
        return KatDataclassKt.show(cast);
    }

    public static KatDataclassKindedJShow INSTANCE = new KatDataclassKindedJShow();
}
