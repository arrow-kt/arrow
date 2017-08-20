package kategory.kindedj.fromkategory;

import io.kindedj.HK;
import kategory.ConvertHK;
import kategory.ConvertKt;
import kategory.fromkotlin.KatDataclassHK;
import kategory.fromkotlin.KatDataclassKt;
import kategory.kindedj.KindJShow;

public class KatDataclassKindJShow implements KindJShow<HK<ConvertHK, KatDataclassHK>> {
    private KatDataclassKindJShow() {
    }

    @Override
    public <A> String show(HK<HK<ConvertHK, KatDataclassHK>, A> hk) {
        final kategory.HK<KatDataclassHK, A> cast = ConvertKt.convert(hk);
        return KatDataclassKt.value(cast).toString();
    }

    public static KatDataclassKindJShow INSTANCE = new KatDataclassKindJShow();
}
