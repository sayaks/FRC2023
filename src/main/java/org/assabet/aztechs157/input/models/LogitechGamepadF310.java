package org.assabet.aztechs157.input.models;

import org.assabet.aztechs157.input.Model;
import org.assabet.aztechs157.input.inputs.Axis;
import org.assabet.aztechs157.input.inputs.Button;
import org.assabet.aztechs157.input.inputs.Pov;

public class LogitechGamepadF310 extends Model {

    public LogitechGamepadF310(final int joystickId) {
        super(joystickId);
    }

    public final Button a = button(1).label("A");
    public final Button b = button(2).label("B");
    public final Button x = button(3).label("X");
    public final Button y = button(4).label("Y");
    public final Button leftBumper = button(5).label("Left Bumper");
    public final Button rightBumper = button(6).label("Right Bumper");
    public final Button back = button(7).label("Back");
    public final Button start = button(8).label("Start");
    public final Button leftStickPress = button(9).label("Left Stick Press");
    public final Button rightStickPress = button(10).label("Right Stick Press");

    public final Axis leftStickX = axis("Left Stick X", 0);
    public final Axis leftStickY = axis("Left Stick Y", 1);
    public final Axis rightTriggerHeld = axis("Right Trigger Held", 2);
    public final Axis leftTriggerHeld = axis("Left Trigger Held", 3);
    public final Axis rightStickX = axis("Right Stick X", 4);
    public final Axis rightStickY = axis("Right Stick Y", 5);

    public final Pov pov = pov(0).label("");
}
