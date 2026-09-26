public class SineWave implements AudioComponent {

    public static double frequency_;

    //Constructor
    public SineWave(int frequency) {
        this.frequency_ = frequency;
    }

    @Override
    public AudioClip getClip() {
        AudioClip clip = new AudioClip();
        short max = Short.MAX_VALUE;
        for(int i = 0; i < AudioClip.duration *AudioClip.sampleRate;  i++){
            clip.setSample(i, (int) (max * Math.sin(2*Math.PI *frequency_ *i / AudioClip.sampleRate)));
        }
        return clip;
    }

    @Override
    public boolean hasInput() {
        return false;
    }

    @Override
    public void connectInput(AudioComponent input) {
        System.out.println("sine wave does not take any inputs.");
        assert (false);
    }
}
