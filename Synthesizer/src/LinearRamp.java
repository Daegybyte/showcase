public class LinearRamp implements AudioComponent {

    private float start_;
    private  float stop_;
    LinearRamp(float start, float stop){
        start_ = start;
        stop_ = stop;
    }


    @Override
    public AudioClip getClip() {
        AudioClip ac = new AudioClip();
        for(int i = 0; i < AudioClip.totalSamples;  i++){
            short sample = (short) ((start_*(AudioClip.totalSamples - i) + stop_*i)/ AudioClip.totalSamples);
            ac.setSample(i, sample);
        }
        return ac;
    }

    @Override
    public boolean hasInput() {
        return false;
    }

    @Override
    public void connectInput(AudioComponent input) {

    }
}
