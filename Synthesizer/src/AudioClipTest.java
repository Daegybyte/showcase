import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

class AudioClipTest {
    @Test
    public void testMax(){
        AudioClip max = new AudioClip();
        max.setSample(0, Short.MAX_VALUE);
        int sample = max.getSample(0);
        Assertions.assertEquals(Short.MAX_VALUE, sample);
    }

    @Test
    public void testMin(){
        AudioClip min = new AudioClip();
        min.setSample(0, Short.MIN_VALUE);
        int y = min.getSample(0);
        Assertions.assertEquals(Short.MIN_VALUE, y);
    }

    @Test
    void testFullRange() {
        AudioClip testingClip = new AudioClip();
        for (short i = Short.MIN_VALUE; i < Short.MAX_VALUE; i++) {
            testingClip.setSample(15, i);
            Assertions.assertEquals(i, testingClip.getSample(15));
        }
    }
}
