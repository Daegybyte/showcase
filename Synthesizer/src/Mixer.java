//import java.util.ArrayList;

import java.util.ArrayList;

public class Mixer implements AudioComponent {

    private ArrayList<AudioComponent> inputs_;
    private AudioClip mixed_;

    Mixer() {
        mixed_ = new AudioClip();
        inputs_ = new ArrayList<>();
    }

    public int clamp(int value) {
        if (value < Short.MIN_VALUE) {
            value = Short.MIN_VALUE;
        } else if (value > Short.MAX_VALUE) {
            value = Short.MAX_VALUE;
        }
        return value;
    }

    @Override
    public AudioClip getClip() {

        //change the ArrayList of AudioComponents into an ArrayList of AudioClips
        ArrayList<AudioClip> inputClips = new ArrayList<>();
        for (AudioComponent input : inputs_) {
            inputClips.add(input.getClip());
        }

        AudioClip result = new AudioClip();

        for(int i = 0; i < AudioClip.totalSamples; i++) {
            int sum = 0;

            // sum each AudioComponent in inputs_ across all samples
            for (AudioClip inputClip : inputClips) {
                //sum = result.getSample(i) + inputClip.getSample(i);
                //result.setSample(i, (short) mixedSample); // result is only the running sums at this point
                sum += inputClip.getSample(i);
            }

            // divide by the number of inputs_ so that the summed wave scales back down to be within our valid range
            int average = sum / inputClips.size();

            //clamp it!
            //I'm not sure this is necessary with how I have it setup right now
            if (average > Short.MAX_VALUE) {
                average = Short.MAX_VALUE;
            }
            else if (average < Short.MIN_VALUE) {
                average = Short.MIN_VALUE;
            }

            //store it in the result AudioClip
            result.setSample(i, (short) (sum / inputClips.size()));
        }

        return result;

    }

    @Override
    public boolean hasInput() {
        return false;
    }

    @Override
    public void connectInput(AudioComponent input) {
        inputs_.add(input);
    }
}
