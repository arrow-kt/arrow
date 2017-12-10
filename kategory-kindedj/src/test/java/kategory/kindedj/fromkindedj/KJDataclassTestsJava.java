package kategory.kindedj.fromkindedj;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import kategory.kindedj.Convert;
import kategory.kindedj.KJDataclassKategoryShow;
import kategory.kindedj.fromkindedj.KJDataclassHK.KJDataclass1;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class KJDataclassTestsJava {

    private final KJDataclass1<Integer> kinded = new KJDataclass1<>(0);

    @Test
    public void hk1CanBeConvertedToKategory() {
        final Convert.FromKindedJToKategory<KJDataclassHK, Integer> hkKindedJ = Convert.fromKindedJ(kinded);
        assertEquals(KJDataclassKindedJShow.INSTANCE.show(kinded), KJDataclassKategoryShow.INSTANCE.show(hkKindedJ));
    }
}
