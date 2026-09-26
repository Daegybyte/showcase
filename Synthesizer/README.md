# Synthesizer

A modular audio synthesizer built in Java, using JavaFX for the interface and the `javax.sound` API for real-time audio playback.

## Overview

This project models audio signal processing as a graph of connected components. Each component implements a shared `AudioComponent` interface, so oscillators, filters, and mixers can be linked together into custom signal chains, similar to how a modular hardware synthesizer works.

## Features

- Sine wave oscillator with adjustable frequency
- Frequency modulation using phase accumulation
- Amplitude filtering with configurable scaling
- Multi input signal mixing
- Linear ramp generator for envelopes and sweeps
- Interactive JavaFX interface with draggable components and live signal routing
- Real time audio playback through `javax.sound.sampled`

## Architecture

All audio producing components implement the `AudioComponent` interface, which defines:

- `getClip()`, returns the generated or processed audio data
- `hasInput()`, indicates whether the component accepts an input signal
- `connectInput()`, wires one component's output into another's input

This design allows components to be composed freely. For example, a `SineWave` can feed into a `Filter`, and multiple filtered signals can be combined through a `Mixer`.

Audio data itself is represented in `AudioClip`, which stores samples as raw bytes and handles the conversion between 16 bit PCM samples and byte pairs.

## Components

| Class | Description |
|---|---|
| `AudioClip` | Stores and manipulates raw 16 bit PCM audio sample data |
| `AudioComponent` | Interface implemented by all signal producing and processing classes |
| `SineWave` | Generates a sine wave at a given frequency |
| `Filter` | Scales the amplitude of an input signal |
| `FrequencyWaveGen` | Applies frequency modulation to an input signal |
| `Mixer` | Combines multiple input signals into one averaged output |
| `LinearRamp` | Produces a linearly increasing or decreasing signal, useful for envelopes |
| `SynthApp` | Main JavaFX application window and UI logic |
| `AudioComponentWidget` | Base draggable UI widget representing a component on the canvas |
| `SineWidget` | UI widget for the sine wave component, includes a frequency slider |

## Getting Started

### Prerequisites

- Java 11 or later
- JavaFX SDK (if not bundled with your JDK)

### Running the Project

This project uses the Gradle wrapper, so a separate Gradle installation is not required.

```bash
./gradlew run
```

On Windows:

```bash
gradlew.bat run
```

### Running Tests

```bash
./gradlew test
```

Unit tests cover audio sample encoding and decoding, including boundary values across the full 16 bit range.

## Usage

1. Launch the application.
2. Use the sidebar to add components such as a sine wave oscillator to the canvas.
3. Drag from a component's output to the speaker icon to route audio to the output.
4. Adjust parameters, such as frequency, using the sliders on each component.
5. Press the piano style buttons at the top to play preset notes, or use the Play button to hear the current routing.

## Known Limitations

- Audio playback is monophonic and does not currently support saving or exporting patches.
- Some UI wiring, such as the frequency modulation button, is present but not fully connected.
- The mixer includes a clamp method that is defined but not currently applied to its output.

## Notes

This project was built as a learning exercise in digital signal processing fundamentals, low level audio data handling, and JavaFX UI development, rather than as a production audio tool.
