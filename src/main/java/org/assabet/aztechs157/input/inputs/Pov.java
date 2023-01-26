package org.assabet.aztechs157.input.inputs;

import java.util.function.IntSupplier;
import java.util.function.IntUnaryOperator;

import edu.wpi.first.wpilibj.DriverStation;

/**
 * Class for getting input from a pov.
 */
public class Pov {
    public static record Key(String label) {
    }

    private final IntSupplier degrees;

    public Pov(final IntSupplier degrees) {
        this.degrees = degrees;
    }

    public static Pov fromDriverStation(final int deviceId, final int povId) {
        return new Pov(() -> DriverStation.getStickPOV(deviceId, povId));
    }

    public int get() {
        return degrees.getAsInt();
    }

    public Pov map(final IntUnaryOperator function) {
        return new Pov(() -> function.applyAsInt(get()));
    }

    public static final int CENTER = -1;
    public static final int UP = 45 * 0;
    public static final int UP_RIGHT = 45 * 1;
    public static final int RIGHT = 45 * 2;
    public static final int DOWN_RIGHT = 45 * 3;
    public static final int DOWN = 45 * 4;
    public static final int DOWN_LEFT = 45 * 5;
    public static final int LEFT = 45 * 6;
    public static final int UP_LEFT = 45 * 7;

    public Button matchesValue(final int degrees, final String name) {
        return new Button("Pov " + name, () -> get() == degrees);
    }

    public final Button center = matchesValue(CENTER, "Center");
    public final Button up = matchesValue(UP, "Up");
    public final Button upRight = matchesValue(UP_RIGHT, "Up Right");
    public final Button right = matchesValue(RIGHT, "Right");
    public final Button downRight = matchesValue(DOWN_RIGHT, "Down Right");
    public final Button down = matchesValue(DOWN, "Down");
    public final Button downLeft = matchesValue(DOWN_LEFT, "Down Left");
    public final Button left = matchesValue(LEFT, "Left");
    public final Button upLeft = matchesValue(UP_LEFT, "Up Left");

    // TODO: Change these to remove trig and be "raw" axis values
    public final Axis x = new Axis("Pov X", () -> {
        final var value = get();
        if (value == CENTER) {
            return 0;
        }
        return Math.round(Math.sin(Math.toRadians(value)));
    });

    public final Axis y = new Axis("Pov Y", () -> {
        final var value = get();
        if (value == CENTER) {
            return 0;
        }
        return Math.round(Math.cos(Math.toRadians(value)));
    });
}
