import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import java.util.Arrays;

public class AudioClip {
    protected static final float duration = 2.0F; //in seconds
    protected static final int sampleRate = 44100; //Samples per second

    protected static float totalSamples = duration * sampleRate;

    private byte[] bytes_ = new byte[(int) (2* ( totalSamples ))]; //Times 2 to go from shorts to bytes
    protected static int bit = 16;

    //methods
    public int getSample(int index){
        short byte1 = (bytes_[index * 2 + 1]);
        byte1  = (short) (byte1 << 8);
        int byte2 = byte1 | (bytes_[index * 2] & 0xFF);
        return byte2;
    }

    public void setSample(int index, int value){
        (bytes_[index * 2  +1]) = (byte) (value >>> 8);
        (bytes_[index * 2]) = (byte)  value;

    }

    public byte[] getData(){
        byte[] copy = Arrays.copyOf(bytes_, bytes_.length);
        return copy;
    }
}
