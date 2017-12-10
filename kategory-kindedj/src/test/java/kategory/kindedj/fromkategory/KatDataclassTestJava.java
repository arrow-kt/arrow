package kategory.kindedj.fromkategory;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import kategory.kindedj.Convert;
import kategory.kindedj.KatDataclass1;
import kategory.kindedj.KatDataclassHK;
import kategory.kindedj.KatDataclassKategoryShow;

@RunWith(JUnit4.class)
public class KatDataclassTestJava {

    private final KatDataclass1<Integer> kinded = new KatDataclass1<>(0);

    @Test
    public void hk1CanBeConvertedToKategory() {
        final Convert.FromKategoryToKindedJ<KatDataclassHK, Integer> toKindedJ = Convert.toKindedJ(kinded);
        Assert.assertEquals(KatDataclassKategoryShow.INSTANCE.show(this.kinded), KatDataclassKindedJShow.INSTANCE.show(toKindedJ));
    }
}
