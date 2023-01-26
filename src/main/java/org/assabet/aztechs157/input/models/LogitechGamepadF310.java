package org.assabet.aztechs157.input.models;

import org.assabet.aztechs157.input.Model;
import org.assabet.aztechs157.input.inputs.Axis;
import org.assabet.aztechs157.input.inputs.Button;
import org.assabet.aztechs157.input.inputs.Pov;

public class LogitechGamepadF310 extends Model {

    public LogitechGamepadF310(final int joystickId) {
        super(joystickId);
    }

    public final Button a = button("A", 1);
    public final Button b = button("B", 2);
    public final Button x = button("X", 3);
    public final Button y = button("Y", 4);
    public final Button leftBumper = button("Left Bumper", 5);
    public final Button rightBumper = button("Right Bumper", 6);
    public final Button back = button("Back", 7);
    public final Button start = button("Start", 8);
    public final Button leftStickPress = button("Left Stick Press", 9);
    public final Button rightStickPress = button("Right Stick Press", 10);

    public final Axis leftStickX = axis("Left Stick X", 0);
    public final Axis leftStickY = axis("Left Stick Y", 1);
    public final Axis rightTriggerHeld = axis("Right Trigger Held", 2);
    public final Axis leftTriggerHeld = axis("Left Trigger Held", 3);
    public final Axis rightStickX = axis("Right Stick X", 4);
    public final Axis rightStickY = axis("Right Stick Y", 5);

    public final Pov pov = pov(0);
}
