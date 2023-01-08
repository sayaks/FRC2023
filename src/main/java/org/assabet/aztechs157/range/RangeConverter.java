package org.assabet.aztechs157.range;

public class RangeConverter {
    private final Range inputRange;
    private final Range outputRange;
    private final double scaleFactor;

    public RangeConverter(final Range inputRange, final Range outputRange) {
        this.inputRange = inputRange;
        this.outputRange = outputRange;
        this.scaleFactor = outputRange.range() / inputRange.range();
    }

    public double convert(final double inputValue) {
        // Shift to zero based input range
        final var basedInput = inputValue - inputRange.start();

        // Scale the zero based input
        final var scaled = basedInput * scaleFactor;

        // Shift from zero based to output range
        final var outputValue = scaled + outputRange.start();

        return outputValue;
    }

    public Range inputRange() {
        return inputRange;
    }

    public Range outputRange() {
        return outputRange;
    }

    public double scaleFactor() {
        return scaleFactor;
    }
}
