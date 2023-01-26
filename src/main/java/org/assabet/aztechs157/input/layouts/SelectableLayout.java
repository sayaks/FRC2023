package org.assabet.aztechs157.input.layouts;

import java.util.function.Supplier;

import org.assabet.aztechs157.input.values.Axis;
import org.assabet.aztechs157.input.values.Button;

/**
 * Object that manages layouts. A layout can be selected from Shuffleboard that
 * can then be used by the robot. It maps the inputs of a
 * {@link SelectableLayout}
 * to the desired functions of the robot.
 */
public class SelectableLayout implements Layout {
    private final Supplier<Layout> layoutSupplier;

    public SelectableLayout(final Supplier<Layout> layoutSupplier) {
        this.layoutSupplier = layoutSupplier;
    }

    public Layout getSelected() {
        return layoutSupplier.get();
    }

    /**
     * Get a button from the currently selected layout.
     *
     * @param key Which button to retrieve
     * @return A {@link Button} and {@link Button.Key} representing the input
     */
    public Button button(final Button.Key key) {
        return new Button(null, () -> getSelected().button(key).get());
    }

    /**
     * Get a axis from the currently selected layout.
     *
     * @param key Which axis to retrieve
     * @return A {@link Axis} representing the input
     */
    public Axis axis(final Axis.Key key) {
        return new Axis(null, () -> getSelected().axis(key).get());
    }

    private String label = "Unlabeled Selectable Layout";

    public SelectableLayout label(final String label) {
        this.label = label;
        return this;
    }

    @Override
    public String toString() {
        return label + "\n" + getSelected().toString();
    }
}
