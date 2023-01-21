package org.assabet.aztechs157.input.layouts;

import java.util.HashMap;
import java.util.Map;

import org.assabet.aztechs157.input.inputs.Axis;
import org.assabet.aztechs157.input.inputs.Button;
import org.assabet.aztechs157.input.inputs.Pov;
import java.util.function.BiFunction;

/**
 * A simple structure that stores the mapping between keys and inputs. These can
 * be used with {@link SelectableLayout} to allow hot-swapping of layouts.
 */
public class MapLayout implements Layout {
    private final Map<Button.Key, Button> buttons = new HashMap<>();
    private final Map<Axis.Key, Axis> axes = new HashMap<>();
    private final Map<Pov.Key, Pov> povs = new HashMap<>();

    /**
     * For this Layout, assign a {@link Button.Key} to a {@link Button}.
     * Calling
     * this method multiple times with the same key will override the previous
     * assignment.
     *
     * @param key    The key to assign with
     * @param button The button being assigned
     */
    public void assign(final Button.Key key, final Button button) {
        buttons.put(key, buttonAssigner.apply(buttons.get(key), button));
    }

    /**
     * For this Layout, assign a {@link Axis.KeyBase} to a {@link Axis}. Calling
     * this
     * method multiple times with the same key will override the previous
     * assignment.
     *
     * @param key  The key to assign with
     * @param axis The axis being assigned
     */
    public void assign(final Axis.Key key, final Axis axis) {
        axes.put(key, axisAssigner.apply(axes.get(key), axis));
    }

    /**
     * For this Layout, assign a {@link Pov.KeyBase} to a {@link Pov}. Calling this
     * method multiple times with the same key will override the previous
     * assignment.
     *
     * @param key The key to assign with
     * @param pov The pov being assigned
     */
    public void assign(final Pov.Key key, final Pov pov) {
        povs.put(key, povAssigner.apply(povs.get(key), pov));
    }

    private BiFunction<Button, Button, Button> buttonAssigner = (prev, next) -> next;
    private BiFunction<Axis, Axis, Axis> axisAssigner = (prev, next) -> next;
    private BiFunction<Pov, Pov, Pov> povAssigner = (prev, next) -> next;

    public MapLayout assignButtonsWith(final BiFunction<Button, Button, Button> buttonAssigner) {
        this.buttonAssigner = buttonAssigner;
        return this;
    }

    public MapLayout assignAxesWith(final BiFunction<Axis, Axis, Axis> axisAssigner) {
        this.axisAssigner = axisAssigner;
        return this;
    }

    public MapLayout assignPovsWith(final BiFunction<Pov, Pov, Pov> povAssigner) {
        this.povAssigner = povAssigner;
        return this;
    }

    /**
     * Retrieve the {@link Button} associated with a {@link Button.Key}
     *
     * @param key The key a button was assigned to
     * @return The associated button
     */
    public Button button(final Button.Key key) {
        return buttons.get(key);
    }

    /**
     * Retrieve the {@link Axis} associated with a {@link Axis.KeyBase}
     *
     * @param key The key an axis was assigned to
     * @return The associated axis
     */
    public Axis axis(final Axis.Key key) {
        return axes.get(key);
    }

    /**
     * Retrieve the {@link Pov} associated with a {@link Pov.KeyBase}
     *
     * @param key The key an pov was assigned to
     * @return The associated pov
     */
    public Pov pov(final Pov.Key key) {
        return povs.get(key);
    }

    private String label = "Unlabeled MapLayout";

    public MapLayout label(final String label) {
        this.label = label;
        return this;
    }

    @Override
    public String toString() {
        final var layoutLabelFormat = "%s\n";
        final var headerFormat = "\n%s:\n";
        final var entryFormat = "%s -> %s\n";

        final var builder = new StringBuilder();

        builder.append(String.format(layoutLabelFormat, this.label));

        if (!buttons.isEmpty()) {
            builder.append(String.format(headerFormat, "Buttons"));
            for (final var entry : buttons.entrySet()) {
                builder.append(String.format(
                        entryFormat,
                        entry.getKey(),
                        entry.getValue()));
            }
        }

        if (!axes.isEmpty()) {
            builder.append(String.format(headerFormat, "Axes"));
            for (final var entry : axes.entrySet()) {
                builder.append(String.format(
                        entryFormat,
                        entry.getKey(),
                        entry.getValue()));
            }
        }

        if (!povs.isEmpty()) {
            builder.append(String.format(headerFormat, "Povs"));
            for (final var entry : povs.entrySet()) {
                builder.append(String.format(
                        entryFormat,
                        entry.getKey(),
                        entry.getValue()));
            }
        }

        return builder.toString();
    }
}
