package org.assabet.aztechs157.numbers;

public record Range(double start, double end) {

    public boolean contains(final double value) {
        return start <= value && value <= end;
    }

    public double range() {
        return end - start;
    }

    public double clamp(final double value) {
        if (value < start) {
            return start;
        } else if (value > end) {
            return end;
        } else {
            return value;
        }
    }

    public RangeConverter convertingTo(final Range output) {
        return new RangeConverter(this, output);
    }
}
