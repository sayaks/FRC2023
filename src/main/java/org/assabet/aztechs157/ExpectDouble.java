package org.assabet.aztechs157;

public record ExpectDouble(double value) {

    public static ExpectDouble expect(final double value) {
        return new ExpectDouble(value);
    }

    public ExpectDouble greaterThan(final double other) {
        if (value > other) {
            return this;
        } else {
            throw new ExpectError(value + " was not greater than " + other);
        }
    }

    public ExpectDouble lessThan(final double other) {
        if (value < other) {
            return this;
        } else {
            throw new ExpectError(value + " was not less than " + other);
        }
    }

    public ExpectDouble greaterOrEqual(final double other) {
        if (value >= other) {
            return this;
        } else {
            throw new ExpectError(value + " was not greater or equal to " + other);
        }
    }

    public ExpectDouble lessOrEqual(final double other) {
        if (value <= other) {
            return this;
        } else {
            throw new ExpectError(value + " was not less or equal to " + other);
        }
    }

    public static class ExpectError extends RuntimeException {
        public ExpectError(final String message) {
            super(message);
        }
    }
}
