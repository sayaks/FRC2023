package org.assabet.aztechs157;

public final class Expect {
    private Expect() {
        throw new IllegalStateException("Expect is a utility class");
    }

    public static class ExpectError extends RuntimeException {
        public ExpectError(final String message) {
            super(message);
        }
    }

    public static ExpectNumber number(final double number) {
        return new ExpectNumber(number);
    }

    public static record ExpectNumber(double value) {
        public ExpectNumber equalTo(final double other) {
            if (value == other) {
                return this;
            } else {
                throw new ExpectError(value + " was not equal to " + other);
            }
        }

        public ExpectNumber notEqualTo(final double other) {
            if (value != other) {
                return this;
            } else {
                throw new ExpectError(value + " was equal to " + other);
            }
        }

        public ExpectNumber greaterThan(final double other) {
            if (value > other) {
                return this;
            } else {
                throw new ExpectError(value + " was not greater than " + other);
            }
        }

        public ExpectNumber lessThan(final double other) {
            if (value < other) {
                return this;
            } else {
                throw new ExpectError(value + " was not less than " + other);
            }
        }

        public ExpectNumber greaterOrEqual(final double other) {
            if (value >= other) {
                return this;
            } else {
                throw new ExpectError(value + " was not greater or equal to " + other);
            }
        }

        public ExpectNumber lessOrEqual(final double other) {
            if (value <= other) {
                return this;
            } else {
                throw new ExpectError(value + " was not less or equal to " + other);
            }
        }
    }

    public static ExpectBoolean bool(final boolean value) {
        return new ExpectBoolean(value);
    }

    public static record ExpectBoolean(boolean value) {
        public ExpectBoolean equal(final boolean other) {
            if (value == other) {
                return this;
            } else {
                throw new ExpectError(value + " was not equal to " + other);
            }
        }
    }
}
