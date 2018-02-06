package arrow.kindedj.fromarrow;

import io.kindedj.Hk;
import arrow.kindedj.ConvertHK;
import arrow.kindedj.ConvertKt;
import arrow.kindedj.KatDataclassHK;
import arrow.kindedj.KatDataclassKt;
import arrow.kindedj.KindedJShow;

public class KatDataclassKindedJShow implements KindedJShow<Hk<ForConvert, KatDataclassHK>> {
    private KatDataclassKindedJShow() {
    }

    @Override
    public <A> String show(Hk<Hk<ForConvert, KatDataclassHK>, A> hk) {
        final arrow.HK<ForKatDataclass, A> cast = ConvertKt.toArrow(hk);
        return KatDataclassKt.show(cast);
    }

    public static KatDataclassKindedJShow INSTANCE = new KatDataclassKindedJShow();
}
