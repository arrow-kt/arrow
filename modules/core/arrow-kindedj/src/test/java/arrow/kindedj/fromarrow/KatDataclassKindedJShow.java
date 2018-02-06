package arrow.kindedj.fromarrow;

import arrow.kindedj.ConvertKt;
import arrow.kindedj.ForConvert;
import arrow.kindedj.ForKatDataclass;
import arrow.kindedj.KatDataclassKt;
import arrow.kindedj.KindedJShow;
import io.kindedj.Hk;

public class KatDataclassKindedJShow implements KindedJShow<Hk<ForConvert, ForKatDataclass>> {
    private KatDataclassKindedJShow() {
    }

    @Override
    public <A> String show(Hk<Hk<ForConvert, ForKatDataclass>, A> hk) {
        final arrow.HK<ForKatDataclass, A> cast = ConvertKt.toArrow(hk);
        return KatDataclassKt.show(cast);
    }

    public static KatDataclassKindedJShow INSTANCE = new KatDataclassKindedJShow();
}
