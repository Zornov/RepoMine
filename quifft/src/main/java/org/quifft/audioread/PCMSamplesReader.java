package org.quifft.audioread;

import org.quifft.params.FFTParameters;

import javax.sound.sampled.AudioFormat;
import java.io.File;
import java.util.NoSuchElementException;

/**
 * Audio reader for raw PCM sample arrays.
 * Allows QuiFFT to operate on in-memory audio data without creating a temp file.
 */
public class PCMSamplesReader extends AudioReader {

    private final int[] samples;
    private final int channels;
    private final long durationMs;
    private final AudioFormat audioFormat;
    private final File dummyFile = new File("pcm_samples");

    private int currentIndex = 0;
    private int windowSizeSamples;
    private int stepSize;
    private int numFrames;
    private int framesGenerated;

    public PCMSamplesReader(short[] pcmSamples, float sampleRate) {
        this(pcmSamples, sampleRate, 1);
    }

    public PCMSamplesReader(short[] pcmSamples, float sampleRate, int channels) {
        this.channels = channels;
        this.samples = new int[pcmSamples.length];
        for (int i = 0; i < pcmSamples.length; i++) {
            this.samples[i] = pcmSamples[i];
        }
        this.audioFormat = new AudioFormat(sampleRate, 16, channels, true, false);
        this.durationMs = Math.round(pcmSamples.length / (double) channels / sampleRate * 1000.0);
    }

    @Override
    public File getFile() {
        return dummyFile;
    }

    @Override
    public long getFileDurationMs() {
        return durationMs;
    }

    @Override
    public AudioFormat getAudioFormat() {
        return audioFormat;
    }

    @Override
    public int[] getWaveform() {
        return samples.clone();
    }

    @Override
    public void setFFTParameters(FFTParameters parameters) {
        super.setFFTParameters(parameters);
        boolean isStereo = channels == 2;
        windowSizeSamples = parameters.windowSize * (isStereo ? 2 : 1);
        int samplesToKeep = (int) Math.round(windowSizeSamples * parameters.windowOverlap);
        stepSize = windowSizeSamples - samplesToKeep;
        int lengthOfWave = samples.length / (isStereo ? 2 : 1);
        double frameOverlapMultiplier = 1 / (1 - parameters.windowOverlap);
        numFrames = (int) Math.ceil(((double) lengthOfWave / parameters.windowSize) * frameOverlapMultiplier);
    }

    @Override
    public boolean hasNext() {
        return framesGenerated < numFrames;
    }

    @Override
    public int[] next() {
        if (!hasNext()) throw new NoSuchElementException();
        int[] window = new int[windowSizeSamples];
        int end = Math.min(currentIndex + windowSizeSamples, samples.length);
        System.arraycopy(samples, currentIndex, window, 0, end - currentIndex);
        currentIndex += stepSize;
        framesGenerated++;
        return window;
    }
}