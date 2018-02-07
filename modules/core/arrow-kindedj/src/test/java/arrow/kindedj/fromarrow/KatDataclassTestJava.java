package arrow.kindedj.fromarrow;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import arrow.kindedj.Convert;
import arrow.kindedj.KatDataclass1;
import arrow.kindedj.KatDataclassHK;
import arrow.kindedj.KatDataclassArrowShow;

@RunWith(JUnit4.class)
public class KatDataclassTestJava {

    private final KatDataclass1<Integer> kinded = new KatDataclass1<>(0);

    @Test
    public void hk1CanBeConvertedToArrow() {
        final Convert.FromArrowToKindedJ<KatDataclassHK, Integer> toKindedJ = Convert.toKindedJ(kinded);
        Assert.assertEquals(KatDataclassArrowShow.INSTANCE.show(this.kinded), KatDataclassKindedJShow.INSTANCE.show(toKindedJ));
    }
}
