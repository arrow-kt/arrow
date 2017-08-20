package kategory.kindedj.fromkategory;

import io.kindedj.HK;
import kategory.ConvertHK;
import kategory.ConvertKt;
import kategory.fromkotlin.KatDataclassHK;
import kategory.fromkotlin.KatDataclassKt;
import kategory.kindedj.KindJShow;

public class KatDataclassKindedJShow implements KindJShow<HK<ConvertHK, KatDataclassHK>> {
    private KatDataclassKindedJShow() {
    }

    @Override
    public <A> String show(HK<HK<ConvertHK, KatDataclassHK>, A> hk) {
        final kategory.HK<KatDataclassHK, A> cast = ConvertKt.convert(hk);
        return KatDataclassKt.value(cast).toString();
    }

    public static KatDataclassKindedJShow INSTANCE = new KatDataclassKindedJShow();
}
