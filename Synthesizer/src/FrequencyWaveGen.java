public class FrequencyWaveGen implements AudioComponent {

    private AudioComponent input_;

    @Override
    public AudioClip getClip() {

        AudioClip input = input_.getClip();
        AudioClip output = new AudioClip();
        float phase = 0;
        for(int i = 0; i < AudioClip.totalSamples; i++){
                phase += 2 * Math.PI * input.getSample(i) /AudioClip.sampleRate;
                output.setSample(i, (short) (Short.MAX_VALUE*Math.sin(phase)));
        }
        return output;
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
