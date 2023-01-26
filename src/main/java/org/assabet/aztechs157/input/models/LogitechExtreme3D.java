package org.assabet.aztechs157.input.models;

import org.assabet.aztechs157.input.Model;
import org.assabet.aztechs157.input.Pov;
import org.assabet.aztechs157.input.values.Axis;
import org.assabet.aztechs157.input.values.Button;

public class LogitechExtreme3D extends Model {
    public LogitechExtreme3D(final int joystickId) {
        super(joystickId);
    }

    public final Button trigger = button("Trigger", 1);
    public final Button thumb = button("Thumb", 2);
    public final Button button3 = button("#3", 3);
    public final Button button4 = button("#4", 4);
    public final Button button5 = button("#5", 5);
    public final Button button6 = button("#6", 6);
    public final Button button7 = button("#7", 7);
    public final Button button8 = button("#8", 8);
    public final Button button9 = button("#9", 9);
    public final Button button10 = button("#10", 10);
    public final Button button11 = button("#11", 11);
    public final Button button12 = button("#12", 12);

    public final Axis stickX = axis("Stick X", 0);
    public final Axis stickY = axis("Stick Y", 1);
    public final Axis stickRotate = axis("Stick Rotate", 2);
    public final Axis slider = axis("Slider", 3);

    public final Pov pov = pov(0);
}
