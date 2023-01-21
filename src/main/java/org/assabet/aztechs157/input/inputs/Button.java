package org.assabet.aztechs157.input.inputs;

import java.util.function.BooleanSupplier;
import java.util.function.UnaryOperator;

import org.assabet.aztechs157.input.BaseKey;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.button.Trigger;

/**
 * Class for getting input from a button. This class has methods and static
 * methods to modify and compose {@link Button}s into a new
 * {@link Button}.
 */
public class Button extends Trigger {
    public static class Key extends BaseKey<Key> {
    }

    public Button(final BooleanSupplier isPressed) {
        super(isPressed);
    }

    public static Button fromDriverStation(final int deviceId, final int buttonId) {
        return new Button(() -> DriverStation.getStickButton(deviceId, buttonId))
                .label("Device " + deviceId + " Button " + buttonId);
    }

    private String label = "Unlabeled Button";

    public Button label(final String label) {
        this.label = label;
        return this;
    }

    @Override
    public String toString() {
        return label;
    }

    public boolean get() {
        return getAsBoolean();
    }

    public Button map(final UnaryOperator<Boolean> function) {
        return new Button(() -> function.apply(get())).label(label);
    }

    /**
     * Inverts the input; similar to a boolean `!`
     *
     * @return A new inverted input
     */
    public Button inverted() {
        return map(value -> !value);
    }

    /**
     * Checks that all inputs are true; similar to a boolean `&&`
     *
     * @param first The first input
     * @param rest  The rest of the inputs
     * @return A new input that is only true when all of the passed inputs are true
     */
    public static Button all(final Button first, final Button... rest) {
        final var button = new Button(() -> {
            // Check each input individually
            // As soon as one input is false, return false
            // The first argument is explicit to prevent being given empty arrays

            if (first != null && first.get() == false) {
                return false;
            }

            for (final var input : rest) {
                if (input != null && input.get() == false) {
                    return false;
                }
            }

            // All inputs are true at this point, so return true
            return true;
        });

        final var label = new StringBuilder(first + "");
        for (final var input : rest) {
            label.append(" + " + input);
        }

        return button.label(label.toString());
    }

    /**
     * Checks that any input is true; similar to a boolean `||`
     *
     * @param first The first input
     * @param rest  The rest of the inputs
     * @return A new input that is true when any of the passed inputs are true
     */
    public static Button any(final Button first, final Button... rest) {
        final var button = new Button(() -> {
            // Check each input individually
            // As soon as one input is true, return true
            // The first argument is explicit to prevent being given empty arrays

            if (first != null && first.get()) {
                return true;
            }

            for (final var input : rest) {
                if (input != null && input.get()) {
                    return true;
                }
            }

            // All inputs are false at this point, so return false
            return false;
        });

        final var label = new StringBuilder(first + "");
        for (final var input : rest) {
            label.append(" or " + input);
        }
        return button.label(label.toString());
    }
}
