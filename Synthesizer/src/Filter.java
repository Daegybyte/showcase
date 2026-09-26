public class Filter implements AudioComponent{

    public static double volume_;
    AudioComponent input_;

    public Filter(float scale){

        volume_ = scale;
    }

    public AudioClip Filter (float scale, AudioClip original){
        AudioClip adjustedSample = new AudioClip();
        for(int i = 0; i < AudioClip.totalSamples;  i++) {
            adjustedSample.setSample(i, (byte) (scale * original.getSample(i)));
        }
        return adjustedSample;
    }


    @Override
    public AudioClip getClip() {
        AudioClip original = input_.getClip();
        AudioClip result = new AudioClip();

        for(int i = 0; i < AudioClip.totalSamples;  i++) {
            result.setSample(i, (short) (volume_ * original.getSample(i)));
            if(result.getSample(i) >= Short.MAX_VALUE){
                result.setSample(i,  Short.MAX_VALUE);
            }
            else if( result.getSample(i) <= Short.MIN_VALUE){
                result.setSample(i, Short.MIN_VALUE);
            }
        }
        return result;
    }

    @Override
    public boolean hasInput() {
        return true;
    }

    @Override
    public void connectInput(AudioComponent input) {
        input_ = input;
    }
}
